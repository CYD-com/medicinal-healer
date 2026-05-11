function formatTime(date) {
  if (typeof date === 'string') {
    date = new Date(date);
  }
  const year = date.getFullYear();
  const month = padZero(date.getMonth() + 1);
  const day = padZero(date.getDate());
  const hour = padZero(date.getHours());
  const minute = padZero(date.getMinutes());
  return year + '-' + month + '-' + day + ' ' + hour + ':' + minute;
}

function formatDate(date) {
  if (typeof date === 'string') {
    date = new Date(date);
  }
  const year = date.getFullYear();
  const month = padZero(date.getMonth() + 1);
  const day = padZero(date.getDate());
  return year + '-' + month + '-' + day;
}

function padZero(num) {
  return num < 10 ? '0' + num : '' + num;
}

function getStatusText(status) {
  const map = {
    'pending': '待处理',
    'confirmed': '已确认',
    'in_progress': '进行中',
    'completed': '已完成',
    'cancelled': '已取消',
    'rejected': '已拒绝'
  };
  return map[status] || status;
}

function getStatusClass(status) {
  const map = {
    'pending': 'tag-warning',
    'confirmed': 'tag-primary',
    'in_progress': 'tag-primary',
    'completed': 'tag-success',
    'cancelled': 'tag-info',
    'rejected': 'tag-danger'
  };
  return map[status] || 'tag-info';
}

function showToast(title, icon) {
  wx.showToast({
    title,
    icon: icon || 'none',
    duration: 2000
  });
}

function checkLogin() {
  const app = getApp();
  if (!app.checkLogin()) {
    wx.redirectTo({ url: '/pages/login/login' });
    return false;
  }
  return true;
}

function getWeekDay(dateStr) {
  const days = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
  return days[new Date(dateStr).getDay()];
}

module.exports = {
  formatTime,
  formatDate,
  getStatusText,
  getStatusClass,
  showToast,
  checkLogin,
  getWeekDay
};