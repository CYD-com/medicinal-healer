const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    userInfo: {},
    roleText: '',
    baseUrl: '',
    showPasswordModal: false,
    passwordForm: {
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    }
  },

  onLoad() {
    this.setData({ baseUrl: app.globalData.baseUrl });
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 3 });
    }
    this.loadUserInfo();
  },

  async loadUserInfo() {
    try {
      const res = await api.getUserInfo();
      const userInfo = res.data;
      const roleMap = {
        'user': '普通用户',
        'doctor': '医生',
        'admin': '管理员'
      };
      this.setData({
        userInfo,
        roleText: roleMap[userInfo.role] || '普通用户'
      });
      app.setUserInfo(userInfo);
    } catch (err) {
      console.error('获取用户信息失败', err);
    }
  },

  changeAvatar() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: async (res) => {
        const filePath = res.tempFilePaths[0];
        try {
          const uploadRes = await api.uploadAvatar(filePath);
          await api.updateAvatar(uploadRes.data);
          wx.showToast({ title: '头像更新成功', icon: 'success' });
          this.loadUserInfo();
        } catch (err) {
          console.error('上传头像失败', err);
        }
      }
    });
  },

  goEditProfile() {
    wx.navigateTo({ url: '/pages/profile/edit' });
  },

  goHealthRecord() {
    wx.navigateTo({ url: '/pages/health-record/health-record' });
  },

  goPrescription() {
    wx.navigateTo({ url: '/pages/prescription/prescription' });
  },

  changePassword() {
    this.setData({
      showPasswordModal: true,
      passwordForm: { oldPassword: '', newPassword: '', confirmPassword: '' }
    });
  },

  closePasswordModal() {
    this.setData({ showPasswordModal: false });
  },

  onPasswordInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ [`passwordForm.${field}`]: e.detail.value });
  },

  async submitPassword() {
    const { passwordForm } = this.data;
    if (!passwordForm.oldPassword) {
      wx.showToast({ title: '请输入原密码', icon: 'none' });
      return;
    }
    if (!passwordForm.newPassword) {
      wx.showToast({ title: '请输入新密码', icon: 'none' });
      return;
    }
    if (passwordForm.newPassword.length < 6) {
      wx.showToast({ title: '密码长度不能少于6位', icon: 'none' });
      return;
    }
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      wx.showToast({ title: '两次密码不一致', icon: 'none' });
      return;
    }

    try {
      await api.changePassword({
        oldPassword: passwordForm.oldPassword,
        newPassword: passwordForm.newPassword
      });
      wx.showToast({ title: '密码修改成功', icon: 'success' });
      this.setData({ showPasswordModal: false });
    } catch (err) {
      console.error('修改密码失败', err);
    }
  },

  goAbout() {
    wx.showModal({
      title: '关于我们',
      content: '智慧医疗小程序 v1.0.0\n让健康管理更简单',
      showCancel: false
    });
  },

  onLogout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await api.logout();
          } catch (err) {}
          app.clearLogin();
          wx.redirectTo({ url: '/pages/login/login' });
        }
      }
    });
  }
});