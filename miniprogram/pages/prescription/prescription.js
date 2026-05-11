const api = require('../../utils/api');
const util = require('../../utils/util');

Page({
  data: {
    prescriptions: [],
    selectedPrescription: null,
    showDetail: false,
    page: 1,
    hasMore: true,
    loading: false
  },

  onLoad() {
    this.loadPrescriptions();
  },

  async loadPrescriptions() {
    this.setData({ loading: true, page: 1 });
    try {
      const res = await api.getPrescriptions({ page: 1, size: 10 });
      const records = (res.data.records || []).map(item => ({
        ...item,
        statusText: this.getStatusText(item.status),
        statusClass: this.getStatusClass(item.status),
        createTime: util.formatTime(item.createTime)
      }));
      this.setData({
        prescriptions: records,
        hasMore: records.length >= 10,
        loading: false
      });
    } catch (err) {
      this.setData({ loading: false });
    }
  },

  async loadMore() {
    const { page, prescriptions } = this.data;
    const nextPage = page + 1;
    this.setData({ loading: true });
    try {
      const res = await api.getPrescriptions({ page: nextPage, size: 10 });
      const records = (res.data.records || []).map(item => ({
        ...item,
        statusText: this.getStatusText(item.status),
        statusClass: this.getStatusClass(item.status),
        createTime: util.formatTime(item.createTime)
      }));
      this.setData({
        prescriptions: [...prescriptions, ...records],
        page: nextPage,
        hasMore: records.length >= 10,
        loading: false
      });
    } catch (err) {
      this.setData({ loading: false });
    }
  },

  getStatusText(status) {
    const map = {
      'active': '有效',
      'used': '已使用',
      'expired': '已过期',
      'cancelled': '已作废'
    };
    return map[status] || status;
  },

  getStatusClass(status) {
    const map = {
      'active': 'tag-success',
      'used': 'tag-info',
      'expired': 'tag-warning',
      'cancelled': 'tag-danger'
    };
    return map[status] || 'tag-info';
  },

  async goDetail(e) {
    const id = e.currentTarget.dataset.id;
    try {
      const res = await api.getPrescriptionById(id);
      const data = res.data;
      data.createTime = util.formatTime(data.createTime);
      this.setData({
        selectedPrescription: data,
        showDetail: true
      });
    } catch (err) {
      console.error('获取处方详情失败', err);
    }
  },

  closeDetail() {
    this.setData({ showDetail: false });
  }
});