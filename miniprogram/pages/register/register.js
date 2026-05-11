const api = require('../../utils/api');

Page({
  data: {
    form: {
      username: '',
      password: '',
      confirmPassword: '',
      realName: '',
      age: '',
      phone: '',
      idCard: '',
      email: '',
      address: ''
    },
    genders: ['男', '女'],
    genderIndex: 0,
    loading: false
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({ [`form.${field}`]: e.detail.value });
  },

  onGenderChange(e) {
    this.setData({ genderIndex: e.detail.value });
  },

  validate() {
    const { form } = this.data;
    if (!form.username.trim()) return '请输入用户名';
    if (!form.password.trim()) return '请输入密码';
    if (form.password.length < 6) return '密码长度不能少于6位';
    if (form.password !== form.confirmPassword) return '两次密码不一致';
    if (!form.realName.trim()) return '请输入真实姓名';
    if (form.phone && !/^1[3-9]\d{9}$/.test(form.phone)) return '手机号格式不正确';
    if (form.idCard && !/^\d{17}[\dXx]$/.test(form.idCard)) return '身份证号格式不正确';
    if (form.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) return '邮箱格式不正确';
    return '';
  },

  async onRegister() {
    const msg = this.validate();
    if (msg) {
      wx.showToast({ title: msg, icon: 'none' });
      return;
    }

    const { form, genders, genderIndex } = this.data;
    this.setData({ loading: true });

    try {
      await api.register({
        username: form.username,
        password: form.password,
        realName: form.realName,
        gender: genders[genderIndex],
        age: form.age ? parseInt(form.age) : 0,
        phone: form.phone,
        idCard: form.idCard,
        email: form.email,
        address: form.address
      });
      wx.showToast({ title: '注册成功', icon: 'success' });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    } catch (err) {
      console.error('注册失败', err);
    } finally {
      this.setData({ loading: false });
    }
  },

  goLogin() {
    wx.navigateBack();
  }
});