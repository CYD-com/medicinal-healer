Component({
  data: {
    selected: 0,
    color: '#999999',
    selectedColor: '#4A90D9',
    list: [
      {
        pagePath: '/pages/index/index',
        text: '首页',
        icon: 'home'
      },
      {
        pagePath: '/pages/appointment/appointment',
        text: '预约',
        icon: 'appointment'
      },
      {
        pagePath: '/pages/consultation/consultation',
        text: '问诊',
        icon: 'consult'
      },
      {
        pagePath: '/pages/profile/profile',
        text: '我的',
        icon: 'profile'
      }
    ]
  },

  methods: {
    switchTab(e) {
      const data = e.currentTarget.dataset;
      const url = data.path;
      wx.switchTab({ url });
    }
  }
});