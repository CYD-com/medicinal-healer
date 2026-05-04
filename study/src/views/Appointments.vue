<template>
  <div class="appointments-container">
    <h2>预约挂号</h2>
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="科室列表" name="departments">
        <div class="departments-card">
          <el-input v-model="deptSearch" placeholder="搜索科室" prefix-icon="el-icon-search" style="margin-bottom: 20px"></el-input>
          <div class="departments-grid">
            <div class="dept-card" v-for="dept in filteredDepartments" :key="dept.id" @click="selectDepartment(dept)">
              <el-icon class="dept-icon"><House /></el-icon>
              <h4>{{ dept.name }}</h4>
              <p>{{ dept.description }}</p>
              <span class="doctors-count">{{ dept.doctorsCount }}位医生</span>
            </div>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="医生列表" name="doctors">
        <div class="doctors-card">
          <el-input v-model="doctorSearch" placeholder="搜索医生" prefix-icon="el-icon-search" style="margin-bottom: 20px"></el-input>
          <el-select v-model="selectedDepartment" placeholder="选择科室" style="width: 200px; margin-bottom: 20px">
            <el-option :key="0" label="全部科室" :value="null"></el-option>
            <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id"></el-option>
          </el-select>
          <el-table :data="filteredDoctors" style="width: 100%">
            <el-table-column label="医生信息" width="300">
              <template #default="scope">
                <div class="doctor-info">
                  <el-avatar :size="48" :src="scope.row.avatar"></el-avatar>
                  <div class="doctor-details">
                    <div class="doctor-name">{{ scope.row.name }} <span class="doctor-title">{{ scope.row.title }}</span></div>
                    <div class="doctor-department">{{ scope.row.department.name }}</div>
                    <div class="doctor-specialty">{{ scope.row.specialty.join('、') }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="rating" label="评分" width="80"></el-table-column>
            <el-table-column prop="consultationCount" label="问诊量" width="100"></el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button type="primary" size="small" @click="viewDoctorSchedule(scope.row)">预约</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
      <el-tab-pane label="我的预约" name="my-appointments">
        <div class="my-appointments-card">
          <el-input v-model="appointmentSearch" placeholder="搜索预约" prefix-icon="el-icon-search" style="margin-bottom: 20px"></el-input>
          <el-select v-model="appointmentStatus" placeholder="选择状态" style="width: 200px; margin-bottom: 20px">
            <el-option label="全部" value=""></el-option>
            <el-option label="待确认" value="pending"></el-option>
            <el-option label="已确认" value="confirmed"></el-option>
            <el-option label="已完成" value="completed"></el-option>
            <el-option label="已取消" value="cancelled"></el-option>
          </el-select>
          <el-table :data="myAppointments" style="width: 100%">
            <el-table-column prop="appointmentDate" label="预约日期" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.appointmentDate) }}
              </template>
            </el-table-column>
            <el-table-column label="预约时间" width="120">
              <template #default="scope">
                {{ scope.row.timeSlot?.startTime }} - {{ scope.row.timeSlot?.endTime }}
              </template>
            </el-table-column>
            <el-table-column label="医生" width="120">
              <template #default="scope">
                {{ scope.row.doctor?.name }}
              </template>
            </el-table-column>
            <el-table-column prop="department" label="科室" width="100"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="患者" width="100">
              <template #default="scope">
                {{ scope.row.patientName }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <el-button v-if="scope.row.status === 'confirmed'" type="primary" size="small" @click="viewQueue(scope.row)">查看排队</el-button>
                <el-button v-if="scope.row.status === 'pending' || scope.row.status === 'confirmed'" type="danger" size="small" @click="cancelAppointment(scope.row)">取消预约</el-button>
                <el-button v-else type="info" size="small" @click="viewAppointmentDetail(scope.row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog title="预约挂号" v-model="showAppointmentModal" width="500px" height="1000px">
      <el-form :model="appointmentForm" label-width="100px">
        <el-form-item label="就诊医生" prop="doctor">
          <el-input :value="selectedDoctor?.name + ' - ' + selectedDoctor?.title" disabled class="doctor-info-input"></el-input>
        </el-form-item>
        <el-form-item label="科室" prop="department">
          <el-input :value="selectedDoctor?.department.name" disabled></el-input>
        </el-form-item>
        <el-form-item label="预约日期" prop="appointmentDate">
          <el-date-picker v-model="appointmentForm.appointmentDate" type="date" placeholder="选择日期" :disabled="!selectedDoctor" :min-date="new Date()"></el-date-picker>
        </el-form-item>
        <el-form-item label="时间段" prop="timeSlot">
          <el-select v-model="appointmentForm.timeSlot" placeholder="选择时间段" :disabled="!selectedDoctor">
            <el-option label="08:00-08:30" value="08:00-08:30"></el-option>
            <el-option label="08:30-09:00" value="08:30-09:00"></el-option>
            <el-option label="09:00-09:30" value="09:00-09:30"></el-option>
            <el-option label="09:30-10:00" value="09:30-10:00"></el-option>
            <el-option label="10:00-10:30" value="10:00-10:30"></el-option>
            <el-option label="10:30-11:00" value="10:30-11:00"></el-option>
            <el-option label="11:00-11:30" value="11:00-11:30"></el-option>
            <el-option label="14:00-14:30" value="14:00-14:30"></el-option>
            <el-option label="14:30-15:00" value="14:30-15:00"></el-option>
            <el-option label="15:00-15:30" value="15:00-15:30"></el-option>
            <el-option label="15:30-16:00" value="15:30-16:00"></el-option>
            <el-option label="16:00-16:30" value="16:00-16:30"></el-option>
            <el-option label="16:30-17:00" value="16:30-17:00"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="患者姓名" prop="patientName">
          <el-input v-model="appointmentForm.patientName" placeholder="请输入患者姓名"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="patientPhone">
          <el-input v-model="appointmentForm.patientPhone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item label="症状描述" prop="symptoms">
          <el-input type="textarea" v-model="appointmentForm.symptoms" placeholder="请简述症状，如：头痛、发烧、咳嗽等" :rows="4"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAppointmentModal = false">取消</el-button>
        <el-button type="primary" @click="submitAppointment" :loading="submitting">确认预约</el-button>
      </template>
    </el-dialog>

    <el-dialog title="预约详情" :visible.sync="showDetailModal" width="500px">
      <div class="appointment-detail">
        <div class="detail-row">
          <span class="detail-label">预约编号：</span>
          <span>{{ selectedAppointment?.appointmentId }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">预约日期：</span>
          <span>{{ formatDate(selectedAppointment?.appointmentDate) }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">预约时间：</span>
          <span>{{ selectedAppointment?.timeSlot?.startTime }} - {{ selectedAppointment?.timeSlot?.endTime }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">科室：</span>
          <span>{{ selectedAppointment?.department }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">医生：</span>
          <span>{{ selectedAppointment?.doctor?.name }} {{ selectedAppointment?.doctor?.title }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">患者姓名：</span>
          <span>{{ selectedAppointment?.patientName }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">联系电话：</span>
          <span>{{ selectedAppointment?.patientPhone }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">症状描述：</span>
          <span>{{ selectedAppointment?.symptoms || '无' }}</span>
        </div>
        <div class="detail-row">
          <span class="detail-label">状态：</span>
          <el-tag :type="getStatusType(selectedAppointment?.status || '')">{{ getStatusText(selectedAppointment?.status || '') }}</el-tag>
        </div>
      </div>
      <template #footer>
        <el-button @click="showDetailModal = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog title="排队信息" :visible.sync="showQueueModal" width="400px">
      <div class="queue-info">
        <div class="queue-title">
          <span>{{ selectedAppointment?.doctor?.name }}医生 - {{ formatDate(selectedAppointment?.appointmentDate) }}</span>
        </div>
        <div class="queue-number">
          <span class="queue-label">您的排队号码：</span>
          <span class="queue-value">第 {{ queueNumber }} 位</span>
        </div>
        <div class="queue-stats">
          <div class="stat-item">
            <span>已就诊：{{ servedCount }}人</span>
          </div>
          <div class="stat-item">
            <span>等待中：{{ waitingCount }}人</span>
          </div>
        </div>
        <div class="queue-tips">
          <el-alert title="温馨提示" type="info" :closable="false">
            请提前30分钟到达医院，凭身份证或预约信息取号就诊。
          </el-alert>
        </div>
      </div>
      <template #footer>
        <el-button @click="showQueueModal = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { House } from '@element-plus/icons-vue'
import type { Department, Doctor, Appointment } from '@/types'
import {
  getDepartments,
  getDoctors,
  getAppointments,
  createAppointment,
  cancelAppointment as cancelAppointmentApi,
  initAppointmentData
} from '@/api/appointment'

const activeTab = ref<string>('departments')
const deptSearch = ref<string>('')
const doctorSearch = ref<string>('')
const selectedDepartment = ref<string | null>(null)
const appointmentSearch = ref<string>('')
const appointmentStatus = ref<string>('')

const departments = ref<Department[]>([])
const doctors = ref<Doctor[]>([])
const myAppointments = ref<Appointment[]>([])
const loading = ref<boolean>(false)
const submitting = ref<boolean>(false)

const showAppointmentModal = ref(false)
const showDetailModal = ref(false)
const showQueueModal = ref(false)
const selectedDoctor = ref<Doctor | null>(null)
const selectedDoctorId = ref<string>('')
const selectedAppointment = ref<Appointment | null>(null)

const queueNumber = ref(3)
const servedCount = ref(5)
const waitingCount = ref(8)

const appointmentForm = ref({
  appointmentDate: '',
  timeSlot: '',
  patientName: '',
  patientPhone: '',
  symptoms: ''
})

const filteredDepartments = computed(() => {
  if (!deptSearch.value) return departments.value
  return departments.value.filter(dept => 
    dept.name.includes(deptSearch.value) || dept.description.includes(deptSearch.value)
  )
})

const filteredDoctors = computed(() => {
  let result = doctors.value || []
  if (selectedDepartment.value) {
    result = result.filter(d => d.department.id === selectedDepartment.value)
  }
  if (doctorSearch.value) {
    result = result.filter(d => d.name.includes(doctorSearch.value))
  }
  return result
})

const loadDepartments = async (): Promise<void> => {
  loading.value = true
  try {
    const res = await getDepartments()
    departments.value = res.data
    if (departments.value.length === 0) {
      await initAppointmentData()
      const initRes = await getDepartments()
      departments.value = initRes.data
    }
  } catch {
    // 错误提示已由全局拦截器处理
  } finally {
    loading.value = false
  }
}

const loadDoctors = async (): Promise<void> => {
  loading.value = true
  try {
    const res: any = await getDoctors(selectedDepartment.value ?? undefined)
    doctors.value = res.data?.data || res.data || []
  } catch {
    doctors.value = []
  } finally {
    loading.value = false
  }
}

const loadMyAppointments = async (): Promise<void> => {
  loading.value = true
  try {
    const res = await getAppointments({
      status: appointmentStatus.value || undefined,
      page: 1,
      size: 100
    })
    myAppointments.value = res.data?.records || []
  } catch {
    // 错误提示已由全局拦截器处理
  } finally {
    loading.value = false
  }
}

const handleTabChange = (tabName: string) => {
  if (tabName === 'my-appointments') {
    loadMyAppointments()
  } else if (tabName === 'doctors') {
    loadDoctors()
  }
}

const selectDepartment = (dept: Department): void => {
  selectedDepartment.value = dept.id
  activeTab.value = 'doctors'
  loadDoctors()
}

const viewDoctorSchedule = (doctor: Doctor): void => {
  selectedDoctor.value = doctor
  selectedDoctorId.value = String(doctor.doctorId)
  appointmentForm.value = {
    appointmentDate: '',
    timeSlot: '',
    patientName: '',
    patientPhone: '',
    symptoms: ''
  }
  showAppointmentModal.value = true
}

const submitAppointment = async (): Promise<void> => {
  if (!selectedDoctor.value) {
    ElMessage.error('请选择医生')
    return
  }
  if (!appointmentForm.value.appointmentDate) {
    ElMessage.error('请选择预约日期')
    return
  }
  if (!appointmentForm.value.timeSlot) {
    ElMessage.error('请选择时间段')
    return
  }
  if (!appointmentForm.value.patientName) {
    ElMessage.error('请输入患者姓名')
    return
  }
  if (!appointmentForm.value.patientPhone) {
    ElMessage.error('请输入联系电话')
    return
  }
  if (!/^1[3-9]\d{9}$/.test(appointmentForm.value.patientPhone)) {
    ElMessage.error('请输入正确的手机号')
    return
  }

  const [startTime, endTime] = appointmentForm.value.timeSlot.split('-')
  submitting.value = true

  try {
    await createAppointment({
      doctorId: Number(selectedDoctor.value.doctorId),
      departmentId: Number(selectedDoctor.value.department.id),
      appointmentDate: appointmentForm.value.appointmentDate,
      startTime,
      endTime,
      patientName: appointmentForm.value.patientName,
      patientPhone: appointmentForm.value.patientPhone,
      symptoms: appointmentForm.value.symptoms
    })
    ElMessage.success('预约成功')
    showAppointmentModal.value = false
    selectedDoctor.value = null
    selectedDoctorId.value = ''
    loadMyAppointments()
  } catch {
    // 错误提示已由全局拦截器处理
  } finally {
    submitting.value = false
  }
}

const viewQueue = async (appointment: Appointment): Promise<void> => {
  selectedAppointment.value = appointment
  queueNumber.value = Math.floor(Math.random() * 10) + 1
  servedCount.value = Math.floor(Math.random() * 20)
  waitingCount.value = Math.floor(Math.random() * 15) + 3
  showQueueModal.value = true
}

const cancelAppointment = async (appointment: Appointment): Promise<void> => {
  ElMessageBox.confirm('确定要取消预约吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await cancelAppointmentApi(String(appointment.appointmentId))
      ElMessage.success('预约已取消')
      loadMyAppointments()
    } catch {
      // 错误提示已由全局拦截器处理
    }
  }).catch(() => {})
}

const viewAppointmentDetail = async (appointment: Appointment): Promise<void> => {
  selectedAppointment.value = appointment
  showDetailModal.value = true
}

const getStatusType = (status: string): string => {
  const statusMap: Record<string, string> = {
    pending: 'warning',
    confirmed: 'primary',
    completed: 'success',
    cancelled: 'danger'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status: string): string => {
  const statusMap: Record<string, string> = {
    pending: '待确认',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}

const formatDate = (date: string | undefined): string => {
  if (!date) return ''
  return date
}

watch(selectedDepartment, () => {
  loadDoctors()
})

watch(appointmentStatus, () => {
  if (activeTab.value === 'my-appointments') {
    loadMyAppointments()
  }
})

onMounted(() => {
  loadDepartments()
})
</script>

<style scoped>
.appointments-container {
  padding: 20px;
}

.appointments-container h2 {
  margin-bottom: 20px;
  color: #333;
}

.departments-card,
.doctors-card,
.my-appointments-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 24px;
}

.departments-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.dept-card {
  background: #f5f7fa;
  padding: 24px;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.dept-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  background: white;
}

.dept-icon {
  font-size: 48px;
  color: #409EFF;
  margin-bottom: 16px;
}

.dept-card h4 {
  font-size: 18px;
  margin-bottom: 8px;
  color: #333;
}

.dept-card p {
  font-size: 14px;
  color: #666;
  margin-bottom: 16px;
  line-height: 1.5;
}

.doctors-count {
  font-size: 12px;
  color: #999;
  background: #e6f7ff;
  padding: 4px 12px;
  border-radius: 12px;
}

.doctor-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.doctor-details {
  flex: 1;
}

.doctor-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.doctor-title {
  font-size: 12px;
  color: #409EFF;
  margin-left: 8px;
}

.doctor-department {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.doctor-specialty {
  font-size: 12px;
  color: #999;
}

.appointment-detail {
  padding: 10px;
}

.detail-row {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-label {
  font-weight: bold;
  color: #666;
  width: 100px;
}

.queue-info {
  padding: 20px;
}

.queue-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
}

.queue-number {
  text-align: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.queue-label {
  font-size: 14px;
  color: #666;
}

.queue-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-left: 10px;
}

.queue-stats {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 20px;
}

.stat-item {
  font-size: 14px;
  color: #666;
}

.queue-tips {
  margin-top: 10px;
}

@media (max-width: 768px) {
  .departments-grid {
    grid-template-columns: 1fr;
  }
  
  .departments-card,
  .doctors-card,
  .my-appointments-card {
    padding: 16px;
  }
  
  .doctor-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .queue-stats {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
}
</style>