import request from '@/utils/request'

export interface AppointmentVO {
  appointmentId: number
  appointmentDate: string
  timeSlot: { startTime: string; endTime: string }
  doctor: any
  department: string
  status: string
  patientName: string
  patientPhone: string
  symptoms: string
}

export const getDoctorTodayAppointments = () => {
  return request.get('/api/appointment/doctor/today')
}

export const getDoctorSchedule = (date?: string) => {
  return request.get('/api/appointment/doctor/schedule', { params: { date } })
}

export const getDoctorConsultations = (status?: string) => {
  return request.get('/api/consultation/doctor/list', { params: { status } })
}

export const replyConsultation = (id: number, data: { doctorReply: string; diagnosis?: string }) => {
  return request.put(`/api/consultation/${id}/reply`, data)
}

export const completeConsultation = (id: number, data: { diagnosis: string }) => {
  return request.put(`/api/consultation/${id}/complete`, data)
}
