import request from '@/utils/request'
import type { Department, Doctor, Appointment } from '@/types'

export interface AppointmentCreateForm {
  doctorId: number
  departmentId: number
  appointmentDate: string
  startTime: string
  endTime: string
  patientName: string
  patientPhone: string
  symptoms?: string
}

export interface AppointmentQueryParams {
  doctorId?: string
  departmentId?: string
  status?: string
  page?: number
  size?: number
}

export const getDepartments = () => {
  return request.get<Department[]>('/api/appointment/departments')
}

export const getDepartmentById = (id: string) => {
  return request.get<Department>(`/api/appointment/departments/${id}`)
}

export const getDoctors = (departmentName?: string) => {
  const params: Record<string, string> = {}
  if (departmentName) {
    params.departmentName = departmentName
  }
  return request.get<Doctor[]>('/api/appointment/doctors', { params })
}

export const getDoctorById = (id: string) => {
  return request.get<Doctor>(`/api/appointment/doctors/${id}`)
}

export const createAppointment = (data: AppointmentCreateForm) => {
  return request.post<Appointment>('/api/appointment/create', data)
}

export const getAppointments = (params: AppointmentQueryParams = {}) => {
  const queryParams: Record<string, string | number> = {
    page: params.page || 1,
    size: params.size || 10
  }
  if (params.doctorId) queryParams.doctorId = params.doctorId
  if (params.departmentId) queryParams.departmentId = params.departmentId
  if (params.status) queryParams.status = params.status
  return request.get('/api/appointment/list', { params: queryParams })
}

export const getAppointmentById = (id: string) => {
  return request.get<Appointment>(`/api/appointment/${id}`)
}

export const updateAppointment = (id: string, data: Partial<AppointmentCreateForm>) => {
  return request.put(`/api/appointment/update/${id}`, data)
}

export const cancelAppointment = (id: string) => {
  return request.put(`/api/appointment/cancel/${id}`)
}

export const initAppointmentData = () => {
  return request.get('/api/appointment/init')
}