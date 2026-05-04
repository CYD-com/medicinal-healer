import request from '@/utils/request'

export interface BasicInfo {
  realName: string
  gender: string
  age: number
  height: number
  weight: number
  bmi: number
  bloodType: string
  maritalStatus: string
  phone: string
}

export interface Statistics {
  totalVisits: number
  totalPrescriptions: number
  healthScore: number
  lastVisitDate: string
}

export interface HealthRecordOverviewVO {
  basicInfo: BasicInfo
  statistics: Statistics
}

export interface PastDisease {
  diseaseName: string
  diagnosisDate: string
  diagnosisHospital: string
  currentStatus: string
  treatment: string
}

export interface FamilyDisease {
  diseaseName: string
  relation: string
  remark: string
}

export interface Allergy {
  allergen: string
  reaction: string
  severity: string
}

export interface Surgery {
  surgeryName: string
  surgeryDate: string
  hospital: string
  recovery: string
}

export interface MedicalHistoryVO {
  pastDiseases: PastDisease[]
  familyDiseases: FamilyDisease[]
  allergies: Allergy[]
  surgicalHistory: Surgery[]
}

export interface Doctor {
  doctorId: number
  name: string
  title: string
  departmentName: string
  specialty: string
}

export interface VisitRecordVO {
  visitId: number
  visitDate: string
  visitType: string
  department: string
  doctor: Doctor
  chiefComplaint: string
  diagnosis: string
  treatment: string
  prescriptionNos: string[]
}

export interface VisitListVO {
  list: VisitRecordVO[]
  total: number
}

export interface IndicatorRecord {
  recordDate: string
  recordTime: string
  value: number
  systolic?: number
  diastolic?: number
  remark: string
}

export interface HealthIndicatorVO {
  records: IndicatorRecord[]
  total: number
}

export interface AuthorizedDoctor {
  doctorId: number
  name: string
  title: string
  departmentName: string
  authorizedDate: string
  expiryDate: string
  isActive: boolean
}

export interface AuthorizationVO {
  doctors: AuthorizedDoctor[]
  total: number
}

export interface MedicalHistoryUpdateDTO {
  pastDiseases?: PastDisease[]
  familyDiseases?: FamilyDisease[]
  allergies?: Allergy[]
  surgicalHistory?: Surgery[]
}

export interface IndicatorCreateDTO {
  type: string
  value?: number
  systolic?: number
  diastolic?: number
  recordDate: string
  recordTime: string
  remark?: string
}

export interface AuthorizationCreateDTO {
  doctorId: number
  expiryDate: string
}

export const healthRecordAPI = {
  getOverview: (): Promise<HealthRecordOverviewVO> => {
    return request.get('/api/health-records/overview')
  },

  getMedicalHistory: (): Promise<MedicalHistoryVO> => {
    return request.get('/api/health-records/medical-history')
  },

  updateMedicalHistory: (data: MedicalHistoryUpdateDTO): Promise<void> => {
    return request.put('/api/health-records/medical-history', data)
  },

  getVisits: (params?: {
    type?: string
    startDate?: string
    endDate?: string
    doctorId?: string
  }): Promise<VisitListVO> => {
    return request.get('/api/health-records/visits', { params })
  },

  getVisitDetail: (visitId: number): Promise<VisitRecordVO> => {
    return request.get(`/api/health-records/visits/${visitId}`)
  },

  getIndicators: (params: {
    type: string
    startDate?: string
    endDate?: string
  }): Promise<HealthIndicatorVO> => {
    return request.get('/api/health-records/indicators', { params })
  },

  addIndicator: (data: IndicatorCreateDTO): Promise<void> => {
    return request.post('/api/health-records/indicators', data)
  },

  getAuthorizations: (): Promise<AuthorizationVO> => {
    return request.get('/api/health-records/authorizations')
  },

  addAuthorization: (data: AuthorizationCreateDTO): Promise<void> => {
    return request.post('/api/health-records/authorizations', data)
  }
}
