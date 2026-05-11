const api = require('../../utils/api');
const util = require('../../utils/util');

Page({
  data: {
    currentTab: 'overview',
    overview: null,
    medicalHistory: null,
    visits: [],
    indicators: [],
    indicatorTypes: [
      { label: '血压', value: 'blood_pressure' },
      { label: '血糖', value: 'blood_sugar' },
      { label: '心率', value: 'heart_rate' },
      { label: '体温', value: 'temperature' },
      { label: '体重', value: 'weight' }
    ],
    indicatorIndex: 0,
    showAddModal: false,
    newIndicator: {
      value: '',
      recordDate: ''
    },
    loading: false
  },

  onLoad() {
    this.loadOverview();
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({ currentTab: tab });
    switch (tab) {
      case 'overview':
        this.loadOverview();
        break;
      case 'history':
        this.loadMedicalHistory();
        break;
      case 'visits':
        this.loadVisits();
        break;
      case 'indicators':
        this.loadIndicators();
        break;
    }
  },

  async loadOverview() {
    try {
      const res = await api.getHealthOverview();
      this.setData({ overview: res.data });
    } catch (err) {
      console.error('获取概览失败', err);
    }
  },

  async loadMedicalHistory() {
    try {
      const res = await api.getMedicalHistory();
      this.setData({ medicalHistory: res.data });
    } catch (err) {
      console.error('获取病史失败', err);
    }
  },

  async loadVisits() {
    this.setData({ loading: true });
    try {
      const res = await api.getVisits({});
      this.setData({ visits: res.data || [], loading: false });
    } catch (err) {
      this.setData({ loading: false });
    }
  },

  async loadIndicators() {
    const { indicatorTypes, indicatorIndex } = this.data;
    this.setData({ loading: true });
    try {
      const res = await api.getIndicators({
        type: indicatorTypes[indicatorIndex].value
      });
      this.setData({ indicators: res.data || [], loading: false });
    } catch (err) {
      this.setData({ loading: false });
    }
  },

  onIndicatorTypeChange(e) {
    this.setData({ indicatorIndex: e.detail.value });
    this.loadIndicators();
  },

  goVisitDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: '/pages/health-record/health-record?visitId=' + id
    });
  },

  showAddIndicator() {
    const now = new Date();
    this.setData({
      showAddModal: true,
      newIndicator: {
        value: '',
        recordDate: util.formatDate(now)
      }
    });
  },

  hideAddModal() {
    this.setData({ showAddModal: false });
  },

  onIndicatorInput(e) {
    this.setData({ 'newIndicator.value': e.detail.value });
  },

  onIndicatorDateChange(e) {
    this.setData({ 'newIndicator.recordDate': e.detail.value });
  },

  async addIndicator() {
    const { newIndicator, indicatorTypes, indicatorIndex } = this.data;
    if (!newIndicator.value) {
      wx.showToast({ title: '请输入数值', icon: 'none' });
      return;
    }

    try {
      await api.addIndicator({
        type: indicatorTypes[indicatorIndex].value,
        value: parseFloat(newIndicator.value),
        recordDate: newIndicator.recordDate
      });
      wx.showToast({ title: '添加成功', icon: 'success' });
      this.setData({ showAddModal: false });
      this.loadIndicators();
    } catch (err) {
      console.error('添加指标失败', err);
    }
  }
});