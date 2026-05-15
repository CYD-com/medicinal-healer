const api = require('../../utils/api');
const util = require('../../utils/util');

Page({
  data: {
    consultation: {},
    messages: [],
    inputMessage: '',
    scrollToId: '',
    loading: false
  },

  onLoad(options) {
    this.consultationId = options.id;
    this.loadDetail();
    this.loadMessages();
  },

  async loadDetail() {
    try {
      const res = await api.getConsultationById(this.consultationId);
      const data = res.data;
      data.statusText = util.getStatusText(data.status);
      data.statusClass = util.getStatusClass(data.status);
      data.doctorName = data.doctor ? data.doctor.name : '';
      this.setData({ consultation: data });
    } catch (err) {
      console.error('获取问诊详情失败', err);
    }
  },

  async loadMessages() {
    this.setData({ loading: true });
    try {
      const res = await api.getConsultationMessages(this.consultationId);
      const messages = (res.data || []).map(item => ({
        ...item,
        createTimeFormatted: util.formatTime(item.createdAt)
      }));
      this.setData({
        messages,
        loading: false,
        scrollToId: messages.length > 0 ? 'msg-' + messages[messages.length - 1].id : ''
      });
    } catch (err) {
      this.setData({ loading: false });
    }
  },

  onMessageInput(e) {
    this.setData({ inputMessage: e.detail.value });
  },

  async sendMessage() {
    const { inputMessage } = this.data;
    if (!inputMessage.trim()) return;

    try {
      await api.patientReply(this.consultationId, { message: inputMessage });
      this.setData({ inputMessage: '' });
      this.loadMessages();
    } catch (err) {
      console.error('发送消息失败', err);
    }
  }
});