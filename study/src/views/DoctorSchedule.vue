<template>
  <div class="doctor-schedule">
    <div class="schedule-header">
      <div class="date-picker">
        <el-date-picker
          v-model="selectedDate"
          type="date"
          placeholder="选择日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="fetchSchedule"
        />
        <el-button type="primary" @click="fetchToday" style="margin-left: 12px">今日排班</el-button>
      </div>
      <el-tag type="info" size="large">
        共 {{ appointments.length }} 个预约
      </el-tag>
    </div>

    <el-table :data="appointments" stripe border style="width: 100%" v-loading="loading">
      <el-table-column prop="patientName" label="患者姓名" width="120" />
      <el-table-column prop="patientPhone" label="联系电话" width="140" />
      <el-table-column label="预约时间" width="180">
        <template #default="{ row }">
          {{ row.timeSlot?.startTime }} - {{ row.timeSlot?.endTime }}
        </template>
      </el-table-column>
      <el-table-column prop="symptoms" label="症状描述" show-overflow-tooltip />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 'pending'"
            type="success"
            link
            @click="handleConfirm(row)"
          >
            确认接诊
          </el-button>
          <el-button
            v-if="row.status === 'confirmed'"
            type="primary"
            link
            @click="handleComplete(row)"
          >
            完成就诊
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && appointments.length === 0" description="暂无预约" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDoctorTodayAppointments, getDoctorSchedule } from '@/api/doctor'
import { updateAppointment } from '@/api/appointment'

const appointments = ref<any[]>([])
const loading = ref(false)
const selectedDate = ref('')

const statusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待确认',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status] || status
}

const statusType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    confirmed: 'primary',
    completed: 'success',
    cancelled: 'info'
  }
  return map[status] || 'info'
}

const fetchToday = async () => {
  selectedDate.value = ''
  loading.value = true
  try {
    const res: any = await getDoctorTodayAppointments()
    if (res.code === 200) {
      appointments.value = res.data
    }
  } catch (e) {
    console.error('获取今日排班失败', e)
  } finally {
    loading.value = false
  }
}

const fetchSchedule = async () => {
  if (!selectedDate.value) {
    fetchToday()
    return
  }
  loading.value = true
  try {
    const res: any = await getDoctorSchedule(selectedDate.value)
    if (res.code === 200) {
      appointments.value = res.data
    }
  } catch (e) {
    console.error('获取排班失败', e)
  } finally {
    loading.value = false
  }
}

const handleConfirm = async (row: any) => {
  try {
    await updateAppointment(String(row.appointmentId), { status: 'confirmed' } as any)
    ElMessage.success('已确认接诊')
    row.status = 'confirmed'
  } catch (e: any) {
    ElMessage.error(e.message || '操作失败')
  }
}

const handleComplete = async (row: any) => {
  try {
    await updateAppointment(String(row.appointmentId), { status: 'completed' } as any)
    ElMessage.success('已完成就诊')
    row.status = 'completed'
  } catch (e: any) {
    ElMessage.error(e.message || '操作失败')
  }
}

onMounted(fetchToday)
</script>

<style scoped>
.doctor-schedule {
  padding: 0;
}

.schedule-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.date-picker {
  display: flex;
  align-items: center;
}
</style>
