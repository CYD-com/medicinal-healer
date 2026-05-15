const api = require('../../utils/api');
const util = require('../../utils/util');
const app = getApp();

Page({
  data: {
    currentTab: 'list',
    appointments: [],
    departments: [],
    doctors: [],
    selectedDeptId: null,
    selectedDeptName: '',
    statusList: [
      { label: '全部状态', value: '' },
      { label: '待处理', value: 'pending' },
      { label: '已确认', value: 'confirmed' },
      { label: '已完成', value: 'completed' },
      { label: '已取消', value: 'cancelled' }
    ],
    statusIndex: 0,
    page: 1,
    hasMore: true,
    loading: false,
    baseUrl: ''
  },

  onLoad() {
    this.setData({ baseUrl: app.globalData.baseUrl });
  },

  onShow() {
    if (typeof this.getTabBar === 'function' && this.getTabBar()) {
      this.getTabBar().setData({ selected: 1 });
    }
    if (!util.checkLogin()) return;
    this.loadAppointments();
    this.loadDepartments();
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({ currentTab: tab });
  },

  async loadAppointments() {
    this.setData({ loading: true, page: 1 });
    try {
      const { statusList, statusIndex } = this.data;
      const res = await api.getAppointments({
        status: statusList[statusIndex].value,
        page: 1,
        size: 10
      });
      const records = (res.data.records || []).map(item => ({
        ...item,
        id: item.appointmentId,
        doctorName: item.doctor ? item.doctor.name : '',
        departmentName: item.department || '',
        timeSlotDisplay: item.timeSlot ? item.timeSlot.startTime + '-' + item.timeSlot.endTime : '',
        statusText: util.getStatusText(item.status),
        statusClass: util.getStatusClass(item.status)
      }));
      this.setData({
        appointments: records,
        hasMore: records.length >= 10,
        loading: false
      });
    } catch (err) {
      this.setData({ loading: false });
    }
  },

  async loadMore() {
    const { page, appointments, statusList, statusIndex } = this.data;
    const nextPage = page + 1;
    this.setData({ loading: true });
    try {
      const res = await api.getAppointments({
        status: statusList[statusIndex].value,
        page: nextPage,
        size: 10
      });
      const records = (res.data.records || []).map(item => ({
        ...item,
        id: item.appointmentId,
        doctorName: item.doctor ? item.doctor.name : '',
        departmentName: item.department || '',
        timeSlotDisplay: item.timeSlot ? item.timeSlot.startTime + '-' + item.timeSlot.endTime : '',
        statusText: util.getStatusText(item.status),
        statusClass: util.getStatusClass(item.status)
      }));
      this.setData({
        appointments: [...appointments, ...records],
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
    this.loadAppointments();
  },

  async loadDepartments() {
    try {
      const res = await api.getDepartments();
      this.setData({ departments: res.data || [] });
    } catch (err) {
      console.error('获取科室失败', err);
    }
  },

  async selectDepartment(e) {
    const { id, name } = e.currentTarget.dataset;
    this.setData({ selectedDeptId: id, selectedDeptName: name, doctors: [] });
    try {
      const res = await api.getDoctors({ departmentId: id });
      this.setData({ doctors: res.data || [] });
    } catch (err) {
      console.error('获取医生失败', err);
    }
  },

  selectDoctor(e) {
    const doctorId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: '/pages/appointment/create?doctorId=' + doctorId + '&departmentName=' + this.data.selectedDeptName
    });
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: '/pages/appointment/create?id=' + id
    });
  },

  async onCancel(e) {
    const id = e.currentTarget.dataset.id;
    wx.showModal({
      title: '确认取消',
      content: '确定要取消这个预约吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await api.cancelAppointment(id);
            wx.showToast({ title: '取消成功', icon: 'success' });
            this.loadAppointments();
          } catch (err) {
            console.error('取消失败', err);
          }
        }
      }
    });
  }
});