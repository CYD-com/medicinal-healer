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
  departmentName?: string
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
  consultationFee?: number
  patientName?: string
  diagnosis?: string
  doctorReply?: string
  prescriptionSuggestion?: string
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

export const getConsultations = (params?: ConsultationQueryParams & { page?: number; size?: number }) => {
  const queryParams: Record<string, any> = {}
  if (params?.status) {
    queryParams.status = params.status
  }
  if (params?.page) {
    queryParams.page = params.page
  }
  if (params?.size) {
    queryParams.size = params.size
  }
  return request.get<ApiResponse<any>>('/api/consultation/list', { params: queryParams })
}

export const getConsultationById = (id: number) => {
  return request.get<ApiResponse<ConsultationVO>>(`/api/consultation/${id}`)
}

export const cancelConsultation = (id: number) => {
  return request.put<ApiResponse<ConsultationVO>>(`/api/consultation/${id}/cancel`)
}

export const updateConsultation = (id: number, data: Partial<ConsultationVO>) => {
  return request.put<ApiResponse<ConsultationVO>>(`/api/consultation/${id}`, data)
}