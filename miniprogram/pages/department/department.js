const api = require('../../utils/api');

Page({
  data: {
    departments: [],
    loading: false
  },

  onLoad() {
    this.loadDepartments();
  },

  async loadDepartments() {
    this.setData({ loading: true });
    try {
      const res = await api.getDepartments();
      this.setData({ departments: res.data || [], loading: false });
    } catch (err) {
      this.setData({ loading: false });
    }
  },

  goDepartment(e) {
    const { id, name } = e.currentTarget.dataset;
    wx.navigateTo({
      url: '/pages/doctor/doctor?departmentId=' + id + '&departmentName=' + name
    });
  }
});