const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    doctors: [],
    departmentId: null,
    departmentName: '',
    keyword: '',
    page: 1,
    hasMore: true,
    loading: false,
    baseUrl: ''
  },

  onLoad(options) {
    this.setData({ baseUrl: app.globalData.baseUrl });
    if (options.departmentId) {
      this.setData({
        departmentId: options.departmentId,
        departmentName: options.departmentName || ''
      });
      wx.setNavigationBarTitle({ title: options.departmentName || '医生列表' });
    }
    this.loadDoctors();
  },

  onSearchInput(e) {
    this.setData({ keyword: e.detail.value });
  },

  onSearch() {
    this.setData({ page: 1, doctors: [] });
    this.loadDoctors();
  },

  async loadDoctors() {
    this.setData({ loading: true });
    try {
      const params = { page: 1, size: 20 };
      if (this.data.departmentId) {
        params.departmentId = this.data.departmentId;
      }
      if (this.data.keyword) {
        params.name = this.data.keyword;
      }
      const res = await api.getDoctors(params);
      const records = res.data || [];
      this.setData({
        doctors: records,
        hasMore: records.length >= 20,
        loading: false
      });
    } catch (err) {
      this.setData({ loading: false });
    }
  },

  async loadMore() {
    const { page, doctors, departmentId, keyword } = this.data;
    const nextPage = page + 1;
    this.setData({ loading: true });
    try {
      const params = { page: nextPage, size: 20 };
      if (departmentId) params.departmentId = departmentId;
      if (keyword) params.name = keyword;
      const res = await api.getDoctors(params);
      const records = res.data || [];
      this.setData({
        doctors: [...doctors, ...records],
        page: nextPage,
        hasMore: records.length >= 20,
        loading: false
      });
    } catch (err) {
      this.setData({ loading: false });
    }
  },

  selectDoctor(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: '/pages/appointment/create?doctorId=' + id + '&departmentName=' + this.data.departmentName
    });
  }
});