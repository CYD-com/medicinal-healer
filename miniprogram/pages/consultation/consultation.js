const api = require('../../utils/api');
const util = require('../../utils/util');
const app = getApp();

Page({
  data: {
    currentTab: 'list',
    consultations: [],
    selectedDoctor: null,
    form: {
      symptoms: '',
      duration: '',
      medicalHistory: ''
    },
    statusList: [
      { label: '全部状态', value: '' },
      { label: '待处理', value: 'pending' },
      { label: '进行中', value: 'in_progress' },
      { label: '已完成', value: 'completed' },
      { label: '已取消', value: 'cancelled' }
    ],
    statusIndex: 0,
    page: 1,
    hasMore: true,
    loading: false,
    submitting: false,
    baseUrl: ''
  },

  onLoad() {
    this.setData({ baseUrl: app.globalData.baseUrl });
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 2 });
    }
    if (!util.checkLogin()) return;
    this.loadConsultations();
    const doctor = wx.getStorageSync('selectedDoctor');
    if (doctor) {
      this.setData({ selectedDoctor: doctor });
      wx.removeStorageSync('selectedDoctor');
    }
  },

  switchTab(e) {
    this.setData({ currentTab: e.currentTarget.dataset.tab });
  },

  async loadConsultations() {
    this.setData({ loading: true, page: 1 });
    try {
      const { statusList, statusIndex } = this.data;
      const res = await api.getConsultations({
        status: statusList[statusIndex].value,
        page: 1,
        size: 10
      });
      const records = (res.data.records || []).map(item => ({
        ...item,
        statusText: util.getStatusText(item.status),
        statusClass: util.getStatusClass(item.status),
        createTime: util.formatTime(item.createTime)
      }));
      this.setData({
        consultations: records,
        hasMore: records.length >= 10,
        loading: false
      });
    } catch (err) {
      this.setData({ loading: false });
    }
  },

  async loadMore() {
    const { page, consultations, statusList, statusIndex } = this.data;
    const nextPage = page + 1;
    this.setData({ loading: true });
    try {
      const res = await api.getConsultations({
        status: statusList[statusIndex].value,
        page: nextPage,
        size: 10
      });
      const records = (res.data.records || []).map(item => ({
        ...item,
        statusText: util.getStatusText(item.status),
        statusClass: util.getStatusClass(item.status),
        createTime: util.formatTime(item.createTime)
      }));
      this.setData({
        consultations: [...consultations, ...records],
        page: nextPage,
        hasMore: records.length >= 10,
        loading: false
      });
    } catch (err) {
      this.setData({ loading: false });
    }
  },

  onStatusChange(e) {
    this.setData({ statusIndex: e.detail.value });
    this.loadConsultations();
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ [`form.${field}`]: e.detail.value });
  },

  goSelectDoctor() {
    wx.navigateTo({ url: '/pages/doctor/doctor?selectMode=true' });
  },

  removeDoctor() {
    this.setData({ selectedDoctor: null });
  },

  goDetail(e) {
    wx.navigateTo({
      url: '/pages/consultation/detail?id=' + e.currentTarget.dataset.id
    });
  },

  async onSubmit() {
    const { selectedDoctor, form } = this.data;
    if (!selectedDoctor) {
      wx.showToast({ title: '请选择医生', icon: 'none' });
      return;
    }
    if (!form.symptoms.trim()) {
      wx.showToast({ title: '请描述症状', icon: 'none' });
      return;
    }

    this.setData({ submitting: true });
    try {
      await api.createConsultation({
        doctorId: selectedDoctor.doctorId,
        symptoms: form.symptoms,
        duration: form.duration,
        medicalHistory: form.medicalHistory
      });
      wx.showToast({ title: '问诊提交成功', icon: 'success' });
      this.setData({
        currentTab: 'list',
        selectedDoctor: null,
        form: { symptoms: '', duration: '', medicalHistory: '' }
      });
      this.loadConsultations();
    } catch (err) {
      console.error('提交问诊失败', err);
    } finally {
      this.setData({ submitting: false });
    }
  }
});