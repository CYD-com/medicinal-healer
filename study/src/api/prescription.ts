import request from '@/utils/request'

export interface PrescriptionItemForm {
  drugId: string
  drugName: string
  specification?: string
  dosage?: string
  quantity: number
  unit?: string
  unitPrice: number
  amount?: number
}

export interface PrescriptionCreateForm {
  doctorId?: number
  diagnosis: string
  doctorAdvice?: string
  items: PrescriptionItemForm[]
}

export interface PrescriptionDoctorInfo {
  id: number
  name: string
  title: string
  avatar: string
  department: string
  signature?: string
}

export interface PrescriptionItemVO {
  id: number
  drugId: string
  drugName: string
  specification: string
  dosage: string
  quantity: number
  unit: string
  unitPrice: number
  amount: number
}

export interface PrescriptionVO {
  id: number
  prescriptionNo: string
  doctor: PrescriptionDoctorInfo
  diagnosis: string
  doctorAdvice: string
  totalAmount: number
  drugCount: number
  status: string
  statusText: string
  validUntil: string
  createdAt: string
  drugs: PrescriptionItemVO[]
}

export interface ApiResponse<T> {
  code: number
  msg: string
  data: T
}

export const createPrescription = (data: PrescriptionCreateForm) => {
  return request.post<ApiResponse<PrescriptionVO>>('/api/prescription/create', data)
}

export const getPrescriptions = (params?: { status?: string; prescriptionNo?: string; page?: number; size?: number }) => {
  const queryParams: Record<string, string | number> = {}
  if (params?.status) {
    queryParams.status = params.status
  }
  if (params?.prescriptionNo) {
    queryParams.prescriptionNo = params.prescriptionNo
  }
  if (params?.page) {
    queryParams.page = params.page
  }
  if (params?.size) {
    queryParams.size = params.size
  }
  return request.get<ApiResponse<PrescriptionVO[]>>('/api/prescription/list', { params: queryParams })
}

export const getPrescriptionById = (id: number) => {
  return request.get<ApiResponse<PrescriptionVO>>(`/api/prescription/${id}`)
}

export const deletePrescription = (id: number) => {
  return request.delete<ApiResponse<void>>(`/api/prescription/${id}`)
}

export const updatePrescriptionStatus = (id: number, status: string) => {
  return request.put<ApiResponse<PrescriptionVO>>(`/api/prescription/${id}/status`, null, {
    params: { status }
  })
}
