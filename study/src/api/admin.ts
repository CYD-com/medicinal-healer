import request from '@/utils/request'

export interface DepartmentForm {
  id?: number
  name: string
  description: string
}

export interface DoctorForm {
  id?: number
  name: string
  title: string
  avatar?: string
  rating?: number
  consultationCount?: number
  specialty?: string
  departmentId: number
  userId?: number
}

export interface StatisticsData {
  totalUsers: number
  totalDoctors: number
  totalDepartments: number
  totalAppointments: number
  pendingConsultations: number
  inProgressConsultations: number
  completedConsultations: number
  totalConsultations: number
}

export const getStatistics = () => {
  return request.get('/api/statistics/overview')
}

export const getDepartmentList = (params?: { page?: number; size?: number; name?: string }) => {
  return request.get('/api/department/list', { params })
}

export const createDepartment = (data: DepartmentForm) => {
  return request.post('/api/department/create', data)
}

export const updateDepartment = (data: DepartmentForm) => {
  return request.put('/api/department/update', data)
}

export const deleteDepartment = (id: number) => {
  return request.delete(`/api/department/delete/${id}`)
}

export const getDoctorList = (params?: { page?: number; size?: number; name?: string }) => {
  return request.get('/api/doctor/list', { params })
}

export const createDoctor = (data: DoctorForm) => {
  return request.post('/api/doctor/create', data)
}

export const updateDoctor = (data: DoctorForm) => {
  return request.put('/api/doctor/update', data)
}

export const deleteDoctor = (id: number) => {
  return request.delete(`/api/doctor/delete/${id}`)
}

export const getUserList = () => {
  return request.get('/api/user/list')
}
