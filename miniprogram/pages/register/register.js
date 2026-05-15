const api = require('../../utils/api');

Page({
  data: {
    form: {
      username: '',
      password: '',
      confirmPassword: '',
      code: ''
    },
    captchaImage: '',
    captchaUuid: '',
    loading: false
  },

  onLoad() {
    this.refreshCaptcha();
  },

  async refreshCaptcha() {
    try {
      const res = await api.getCaptcha();
      this.setData({
        captchaImage: 'data:image/png;base64,' + res.data.image,
        captchaUuid: res.data.uuid,
        'form.code': ''
      });
    } catch (err) {
      console.error('获取验证码失败', err);
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ [`form.${field}`]: e.detail.value });
  },

  validate() {
    const { form } = this.data;
    if (!form.username.trim()) return '请输入用户名';
    if (form.username.length < 3) return '用户名长度不能少于3位';
    if (!form.password.trim()) return '请输入密码';
    if (form.password.length < 6) return '密码长度不能少于6位';
    if (form.password !== form.confirmPassword) return '两次密码不一致';
    if (!form.code.trim()) return '请输入验证码';
    return '';
  },

  async onRegister() {
    const msg = this.validate();
    if (msg) {
      wx.showToast({ title: msg, icon: 'none' });
      return;
    }

    const { form, captchaUuid } = this.data;
    this.setData({ loading: true });

    try {
      await api.register({
        username: form.username,
        password: form.password,
        code: form.code,
        uuid: captchaUuid
      });
      wx.showToast({ title: '注册成功', icon: 'success' });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    } catch (err) {
      this.refreshCaptcha();
    } finally {
      this.setData({ loading: false });
    }
  },

  goLogin() {
    wx.navigateBack();
  }
});
