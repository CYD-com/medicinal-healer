const app = getApp();

function request(options) {
  return new Promise((resolve, reject) => {
    const { url, method = 'GET', data, header = {} } = options;
    const token = app.globalData.token;

    wx.request({
      url: app.globalData.baseUrl + url,
      method,
      data,
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? 'Bearer ' + token : '',
        ...header
      },
      success(res) {
        if (res.statusCode === 200) {
          if (res.data.code === 200 || res.data.code === 0) {
            resolve(res.data);
          } else if (res.data.code === 401) {
            app.clearLogin();
            wx.showToast({ title: '请重新登录', icon: 'none' });
            setTimeout(() => {
              wx.redirectTo({ url: '/pages/login/login' });
            }, 1500);
            reject(res.data);
          } else {
            wx.showToast({ title: res.data.msg || '请求失败', icon: 'none' });
            reject(res.data);
          }
        } else if (res.statusCode === 401) {
          app.clearLogin();
          wx.showToast({ title: '请重新登录', icon: 'none' });
          setTimeout(() => {
            wx.redirectTo({ url: '/pages/login/login' });
          }, 1500);
          reject(res.data);
        } else {
          wx.showToast({ title: '服务器错误', icon: 'none' });
          reject(res.data);
        }
      },
      fail(err) {
        wx.showToast({ title: '网络错误', icon: 'none' });
        reject(err);
      }
    });
  });
}

function get(url, data) {
  return request({ url, method: 'GET', data });
}

function post(url, data) {
  return request({ url, method: 'POST', data });
}

function put(url, data) {
  return request({ url, method: 'PUT', data });
}

function del(url, data) {
  return request({ url, method: 'DELETE', data });
}

module.exports = {
  request,
  get,
  post,
  put,
  del
};