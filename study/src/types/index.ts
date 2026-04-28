export interface Department {
  id: string
  departmentId: string
  name: string
  description: string
  doctorsCount: number
}

export interface Doctor {
  doctorId: string
  name: string
  title: string
  avatar: string
  rating: number
  consultationCount: number
  specialty: string[]
  department: Department
}

export interface TimeSlot {
  startTime: string
  endTime: string
}

export interface Appointment {
  appointmentId: string
  appointmentDate: string
  timeSlot: TimeSlot
  doctor: Doctor
  department: string
  status: 'pending' | 'confirmed' | 'completed' | 'cancelled'
  patientName?: string
  patientPhone?: string
  symptoms?: string
}

export interface User {
  id: string
  username: string
  role: string
  avatar?: string
}

export interface RecordForm {
  id?: number
  category: string
  type: string
  status: string
  amount: number
  time: string
}

export interface RecordItem extends RecordForm {
  id: number
}

export interface SearchForm {
  category: string
  type: string
  status: string
}

export interface UserForm {
  username: string
  password: string
  role: string
}

export interface UserItem extends UserForm {
  id: string
  createTime: string
}

export interface LoginForm {
  username: string
  password: string
}

export interface RegisterForm extends LoginForm {
  confirmPassword: string
  code: string
}

export interface UserInfo {
  email: string
  gender: string
  age: number
  registerDate: string
  avatar: string
}

export interface EditDialogForm {
  id: number
  category: string
  style: string
  status: string
  time: string
  money: number
}

export interface DrugItem {
  id: string
  drugName: string
  genericName: string
  englishName: string
  specification: string
  manufacturer: string
  category: string
  type: string
  form: string
  price: number
  stock: number
  unit: string
  shelfLife: string
  storage: string
  approvalNo: string
  indications: string
  contraindications: string
  adverseReactions: string
  precautions: string
  drugInteractions: string
  dosage: string
  image: string
}

export interface DrugCategory {
  id: string
  name: string
}

export interface InventoryItem {
  drugName: string
  specification: string
  stock: number
  safetyStock: number
  status: 'normal' | 'low' | 'out'
  expiryDate: string
  daysToExpiry: number
}

export interface InventorySummary {
  totalDrugs: number
  lowStock: number
  outOfStock: number
  expiringSoon: number
}
