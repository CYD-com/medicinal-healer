const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    form: {
      realName: '',
      age: '',
      phone: '',
      email: '',
      idCard: '',
      address: ''
    },
    genders: ['男', '女'],
    genderIndex: 0,
    saving: false
  },

  onLoad() {
    this.loadUserInfo();
  },

  async loadUserInfo() {
    try {
      const res = await api.getUserInfo();
      const userInfo = res.data;
      this.setData({
        form: {
          realName: userInfo.realName || '',
          age: userInfo.age || '',
          phone: userInfo.phone || '',
          email: userInfo.email || '',
          idCard: userInfo.idCard || '',
          address: userInfo.address || ''
        },
        genderIndex: userInfo.gender === '女' ? 1 : 0
      });
    } catch (err) {
      console.error('获取用户信息失败', err);
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ [`form.${field}`]: e.detail.value });
  },

  onGenderChange(e) {
    this.setData({ genderIndex: e.detail.value });
  },

  async onSave() {
    const { form, genders, genderIndex } = this.data;
    if (!form.realName.trim()) {
      wx.showToast({ title: '请输入真实姓名', icon: 'none' });
      return;
    }

    this.setData({ saving: true });
    try {
      await api.updateUserInfo({
        realName: form.realName,
        gender: genders[genderIndex],
        age: form.age ? parseInt(form.age) : 0,
        phone: form.phone,
        email: form.email,
        idCard: form.idCard,
        address: form.address
      });
      wx.showToast({ title: '保存成功', icon: 'success' });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    } catch (err) {
      console.error('保存失败', err);
    } finally {
      this.setData({ saving: false });
    }
  }
});