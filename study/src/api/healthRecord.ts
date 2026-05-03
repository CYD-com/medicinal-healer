import request from '../utils/request'

export const healthRecordAPI = {
  getOverview: () => {
    return request.get('/health-records/overview')
  },

  getMedicalHistory: () => {
    return request.get('/health-records/medical-history')
  },

  updateMedicalHistory: (data: Record<string, unknown>) => {
    return request.put('/health-records/medical-history', data)
  },

  getVisits: (params?: Record<string, unknown>) => {
    return request.get('/health-records/visits', { params })
  },

  getVisitDetail: (visitId: string) => {
    return request.get(`/health-records/visits/${visitId}`)
  },

  getIndicators: (params: Record<string, unknown>) => {
    return request.get('/health-records/indicators', { params })
  },

  addIndicator: (data: Record<string, unknown>) => {
    return request.post('/health-records/indicators', data)
  },

  getAuthorizations: () => {
    return request.get('/health-records/authorizations')
  },

  addAuthorization: (data: Record<string, unknown>) => {
    return request.post('/health-records/authorizations', data)
  }
}
