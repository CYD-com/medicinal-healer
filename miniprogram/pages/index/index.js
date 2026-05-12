const app = getApp();
const util = require('../../utils/util');

Page({
  data: {
    userInfo: null,
    baseUrl: ''
  },

  onLoad() {
    this.setData({ baseUrl: app.globalData.baseUrl });
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 0 });
    }
    this.checkLoginStatus();
  },

  checkLoginStatus() {
    if (!app.checkLogin()) {
      wx.redirectTo({ url: '/pages/login/login' });
      return;
    }
    this.loadUserInfo();
  },

  async loadUserInfo() {
    try {
      const res = await require('../../utils/api').getUserInfo();
      this.setData({ userInfo: res.data });
      app.setUserInfo(res.data);
    } catch (err) {
      console.error('获取用户信息失败', err);
    }
  },

  goProfile() {
    wx.switchTab({ url: '/pages/profile/profile' });
  },

  goAppointment() {
    wx.switchTab({ url: '/pages/appointment/appointment' });
  },

  goConsultation() {
    wx.switchTab({ url: '/pages/consultation/consultation' });
  },

  goHealthRecord() {
    wx.navigateTo({ url: '/pages/health-record/health-record' });
  },

  goPrescription() {
    wx.navigateTo({ url: '/pages/prescription/prescription' });
  },

  goDepartments() {
    wx.navigateTo({ url: '/pages/department/department' });
  },

  goDoctors() {
    wx.navigateTo({ url: '/pages/doctor/doctor' });
  },

  goNotifications() {
    wx.showToast({ title: '功能开发中', icon: 'none' });
  }
});