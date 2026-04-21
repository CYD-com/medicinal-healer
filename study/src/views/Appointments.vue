<template>
  <div class="appointments-container">
    <h2>预约挂号</h2>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="科室列表" name="departments">
        <div class="departments-card">
          <el-input v-model="deptSearch" placeholder="搜索科室" prefix-icon="el-icon-search" style="margin-bottom: 20px"></el-input>
          <div class="departments-grid">
            <div class="dept-card" v-for="dept in departments" :key="dept.id" @click="selectDepartment(dept)">
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
            <el-option label="全部科室" value=""></el-option>
            <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id"></el-option>
          </el-select>
          <el-table :data="doctors" style="width: 100%">
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
            <el-table-column prop="appointmentDate" label="预约日期" width="180"></el-table-column>
            <el-table-column prop="timeSlot.startTime" label="预约时间" width="120"></el-table-column>
            <el-table-column prop="doctor.name" label="医生" width="120"></el-table-column>
            <el-table-column prop="department" label="科室" width="100"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <el-button v-if="scope.row.status === 'confirmed'" type="primary" size="small" @click="viewQueue(scope.row)">查看排队</el-button>
                <el-button v-if="scope.row.status === 'confirmed'" type="danger" size="small" @click="cancelAppointment(scope.row)">取消预约</el-button>
                <el-button v-else type="info" size="small" @click="viewAppointmentDetail(scope.row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { House } from '@element-plus/icons-vue'
import type { Department, Doctor, Appointment } from '@/types'

const activeTab = ref<string>('departments')
const deptSearch = ref<string>('')
const doctorSearch = ref<string>('')
const selectedDepartment = ref<string>('')
const appointmentSearch = ref<string>('')
const appointmentStatus = ref<string>('')

const departments = ref<Department[]>([])
const doctors = ref<Doctor[]>([])
const myAppointments = ref<Appointment[]>([])
const loading = ref<boolean>(false)

const loadDepartments = async (): Promise<void> => {
  loading.value = true
  try {
    departments.value = [
      { id: '1', departmentId: '1', name: '内科', description: '内科诊疗', doctorsCount: 5 },
      { id: '2', departmentId: '2', name: '外科', description: '外科诊疗', doctorsCount: 4 },
      { id: '3', departmentId: '3', name: '儿科', description: '儿科诊疗', doctorsCount: 3 },
      { id: '4', departmentId: '4', name: '妇产科', description: '妇产科诊疗', doctorsCount: 6 }
    ]
  } catch (error) {
    ElMessage.error('获取科室列表失败')
  } finally {
    loading.value = false
  }
}

const loadDoctors = async (): Promise<void> => {
  loading.value = true
  try {
    doctors.value = [
      { 
        doctorId: '1', 
        name: '张医生', 
        title: '主任医师', 
        avatar: '', 
        rating: 4.9, 
        consultationCount: 1200, 
        specialty: ['心血管', '高血压'], 
        department: { id: '1', departmentId: '1', name: '内科', description: '内科', doctorsCount: 5 }
      }
    ]
  } catch (error) {
    ElMessage.error('获取医生列表失败')
  } finally {
    loading.value = false
  }
}

const loadMyAppointments = async (): Promise<void> => {
  loading.value = true
  try {
    myAppointments.value = []
  } catch (error) {
    ElMessage.error('获取预约列表失败')
  } finally {
    loading.value = false
  }
}

const selectDepartment = (dept: Department): void => {
  selectedDepartment.value = dept.departmentId
  activeTab.value = 'doctors'
  loadDoctors()
}

const viewDoctorSchedule = async (doctor: Doctor): Promise<void> => {
  ElMessage.info(`查看${doctor.name}的排班`)
}

const viewQueue = async (appointment: Appointment): Promise<void> => {
  ElMessage.info('查看排队信息')
}

const cancelAppointment = async (appointment: Appointment): Promise<void> => {
  ElMessageBox.confirm('确定要取消预约吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    appointment.status = 'cancelled'
    ElMessage.success('预约已取消')
  }).catch(() => {})
}

const viewAppointmentDetail = async (appointment: Appointment): Promise<void> => {
  ElMessage.info('查看预约详情')
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
}
</style>
