import request from '@/utils/request'

export interface ConsultationCreateForm {
  doctorId: number
  consultationType: 'text' | 'video'
  symptom: string
  description: string
  images?: string
  medicalHistory?: string
  currentMedication?: string
}

export interface ConsultationQueryParams {
  status?: string
}

export interface DoctorVO {
  id: number
  name: string
  title: string
  avatar: string
}

export interface ConsultationVO {
  id: number
  consultationNo: string
  doctor: DoctorVO
  consultationType: string
  symptom: string
  description: string
  images?: string
  medicalHistory?: string
  currentMedication?: string
  status: string
  statusText: string
  createdAt: string
}

export interface ApiResponse<T> {
  code: number
  msg: string
  data: T
}

export const createConsultation = (data: ConsultationCreateForm) => {
  return request.post<ApiResponse<ConsultationVO>>('/api/consultation/create', data)
}

export const getConsultations = (params?: ConsultationQueryParams) => {
  const queryParams: Record<string, string> = {}
  if (params?.status) {
    queryParams.status = params.status
  }
  return request.get<ApiResponse<ConsultationVO[]>>('/api/consultation/list', { params: queryParams })
}

export const getConsultationById = (id: number) => {
  return request.get<ApiResponse<ConsultationVO>>(`/api/consultation/${id}`)
}

export const cancelConsultation = (id: number) => {
  return request.put<ApiResponse<ConsultationVO>>(`/api/consultation/${id}/cancel`)
}