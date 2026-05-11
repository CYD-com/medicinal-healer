const api = require('./request');

module.exports = {
  login: (data) => api.post('/api/user/login', data),
  register: (data) => api.post('/api/user/register', data),
  logout: () => api.post('/api/user/logout'),
  getUserInfo: () => api.get('/api/user/info'),
  updateUserInfo: (data) => api.put('/api/user/info', data),
  updateProfile: (data) => api.put('/api/user/profile', data),
  changePassword: (data) => api.put('/api/user/password', data),
  uploadAvatar: (filePath) => {
    return new Promise((resolve, reject) => {
      const app = getApp();
      wx.uploadFile({
        url: app.globalData.baseUrl + '/api/user/avatar',
        filePath,
        name: 'file',
        header: {
          'Authorization': 'Bearer ' + app.globalData.token
        },
        success(res) {
          const data = JSON.parse(res.data);
          if (data.code === 200 || data.code === 0) {
            resolve(data);
          } else {
            reject(data);
          }
        },
        fail: reject
      });
    });
  },
  updateAvatar: (avatar) => api.put('/api/user/avatar', { avatar }),

  getDepartments: () => api.get('/api/appointment/departments'),
  getDepartmentById: (id) => api.get('/api/appointment/departments/' + id),
  getDoctors: (params) => api.get('/api/appointment/doctors', params),
  getDoctorById: (id) => api.get('/api/appointment/doctors/' + id),
  createAppointment: (data) => api.post('/api/appointment/create', data),
  getAppointments: (params) => api.get('/api/appointment/list', params),
  getAppointmentById: (id) => api.get('/api/appointment/' + id),
  cancelAppointment: (id) => api.put('/api/appointment/cancel/' + id),

  createConsultation: (data) => api.post('/api/consultation/create', data),
  getConsultations: (params) => api.get('/api/consultation/list', params),
  getConsultationById: (id) => api.get('/api/consultation/' + id),
  cancelConsultation: (id) => api.put('/api/consultation/' + id + '/cancel'),
  getConsultationMessages: (id) => api.get('/api/consultation/' + id + '/messages'),
  patientReply: (id, data) => api.put('/api/consultation/' + id + '/patient-reply', data),

  getHealthOverview: () => api.get('/api/health-records/overview'),
  getMedicalHistory: () => api.get('/api/health-records/medical-history'),
  updateMedicalHistory: (data) => api.put('/api/health-records/medical-history', data),
  getVisits: (params) => api.get('/api/health-records/visits', params),
  getVisitDetail: (id) => api.get('/api/health-records/visits/' + id),
  getIndicators: (params) => api.get('/api/health-records/indicators', params),
  addIndicator: (data) => api.post('/api/health-records/indicators', data),
  getAuthorizations: () => api.get('/api/health-records/authorizations'),
  addAuthorization: (data) => api.post('/api/health-records/authorizations', data),

  getPrescriptions: (params) => api.get('/api/prescription/list', params),
  getPrescriptionById: (id) => api.get('/api/prescription/' + id)
};