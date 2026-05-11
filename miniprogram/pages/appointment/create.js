const api = require('../../utils/api');
const util = require('../../utils/util');
const app = getApp();

Page({
  data: {
    isDetail: false,
    appointment: null,
    doctor: null,
    departmentName: '',
    doctorId: null,
    form: {
      patientName: '',
      patientIdCard: '',
      appointmentDate: '',
      symptoms: ''
    },
    timeSlots: [
      '08:00-09:00', '09:00-10:00', '10:00-11:00', '11:00-12:00',
      '14:00-15:00', '15:00-16:00', '16:00-17:00'
    ],
    timeIndex: -1,
    today: '',
    submitting: false,
    baseUrl: ''
  },

  onLoad(options) {
    this.setData({
      baseUrl: app.globalData.baseUrl,
      today: util.formatDate(new Date())
    });

    if (options.id) {
      this.setData({ isDetail: true });
      this.loadAppointmentDetail(options.id);
    } else if (options.doctorId) {
      this.setData({
        doctorId: options.doctorId,
        departmentName: options.departmentName || ''
      });
      this.loadDoctorDetail(options.doctorId);
    }
  },

  async loadAppointmentDetail(id) {
    try {
      const res = await api.getAppointmentById(id);
      const data = res.data;
      data.statusText = util.getStatusText(data.status);
      data.statusClass = util.getStatusClass(data.status);
      this.setData({ appointment: data });
    } catch (err) {
      console.error('获取预约详情失败', err);
    }
  },

  async loadDoctorDetail(id) {
    try {
      const res = await api.getDoctorById(id);
      this.setData({ doctor: res.data });
    } catch (err) {
      console.error('获取医生信息失败', err);
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ [`form.${field}`]: e.detail.value });
  },

  onDateChange(e) {
    this.setData({ 'form.appointmentDate': e.detail.value });
  },

  onTimeChange(e) {
    this.setData({ timeIndex: e.detail.value });
  },

  goSelectDoctor() {
    wx.navigateTo({ url: '/pages/doctor/doctor' });
  },

  validate() {
    const { form, timeIndex, doctorId } = this.data;
    if (!doctorId) return '请选择医生';
    if (!form.patientName.trim()) return '请输入就诊人姓名';
    if (!form.appointmentDate) return '请选择预约日期';
    if (timeIndex < 0) return '请选择时间段';
    return '';
  },

  async onSubmit() {
    const msg = this.validate();
    if (msg) {
      wx.showToast({ title: msg, icon: 'none' });
      return;
    }

    const { form, timeSlots, timeIndex, doctorId } = this.data;
    this.setData({ submitting: true });

    try {
      await api.createAppointment({
        doctorId: doctorId,
        appointmentDate: form.appointmentDate,
        timeSlot: timeSlots[timeIndex],
        patientName: form.patientName,
        patientIdCard: form.patientIdCard,
        symptoms: form.symptoms
      });
      wx.showToast({ title: '预约成功', icon: 'success' });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    } catch (err) {
      console.error('预约失败', err);
    } finally {
      this.setData({ submitting: false });
    }
  },

  async onCancel() {
    const { appointment } = this.data;
    wx.showModal({
      title: '确认取消',
      content: '确定要取消这个预约吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await api.cancelAppointment(appointment.id);
            wx.showToast({ title: '取消成功', icon: 'success' });
            setTimeout(() => {
              wx.navigateBack();
            }, 1500);
          } catch (err) {
            console.error('取消失败', err);
          }
        }
      }
    });
  }
});