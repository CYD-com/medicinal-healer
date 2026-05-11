<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h2>控制台</h2>
      <p class="welcome-text">欢迎回来，{{ userStore.username }}医生</p>
    </div>

    <div class="stats-row">
      <div class="stat-card" @click="router.push('/doctor/schedule')">
        <div class="stat-icon" style="background: #e6f7ff; color: #1890ff;">
          <el-icon :size="28"><Calendar /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.todayAppointments }}</div>
          <div class="stat-label">今日预约</div>
        </div>
      </div>
      <div class="stat-card" @click="router.push('/doctor/consultations')">
        <div class="stat-icon" style="background: #fff7e6; color: #fa8c16;">
          <el-icon :size="28"><ChatDotRound /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.pendingConsultations }}</div>
          <div class="stat-label">待处理问诊</div>
        </div>
      </div>
      <div class="stat-card" @click="router.push('/doctor/prescriptions')">
        <div class="stat-icon" style="background: #f6ffed; color: #52c41a;">
          <el-icon :size="28"><Document /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.monthPrescriptions }}</div>
          <div class="stat-label">本月处方</div>
        </div>
      </div>
      <div class="stat-card" @click="router.push('/doctor/consultations')">
        <div class="stat-icon" style="background: #fff1f0; color: #f5222d;">
          <el-icon :size="28"><User /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.weekPatients }}</div>
          <div class="stat-label">本周就诊患者</div>
        </div>
      </div>
    </div>

    <div class="content-row">
      <div class="recent-card">
        <div class="card-header">
          <h3>今日预约</h3>
        </div>
        <div class="card-body">
          <div v-if="todayAppointments.length === 0" class="empty-tip">今日暂无预约</div>
          <div v-for="item in todayAppointments" :key="item.appointmentId" class="recent-item">
            <div class="item-left">
              <div class="item-title">{{ item.patientName }} - {{ item.symptoms || '无症状描述' }}</div>
              <div class="item-time">{{ item.timeSlot?.startTime }}-{{ item.timeSlot?.endTime }}</div>
            </div>
            <el-tag :type="getAptStatusType(item.status)" size="small">{{ getAptStatusText(item.status) }}</el-tag>
          </div>
        </div>
      </div>

      <div class="recent-card">
        <div class="card-header">
          <h3>待处理问诊</h3>
          <el-button type="primary" link @click="router.push('/doctor/consultations')">查看全部</el-button>
        </div>
        <div class="card-body">
          <div v-if="pendingConsultations.length === 0" class="empty-tip">暂无待处理问诊</div>
          <div v-for="item in pendingConsultations" :key="item.id" class="recent-item">
            <div class="item-left">
              <div class="item-title">{{ item.patientName || '未知患者' }} - {{ item.symptom }}</div>
              <div class="item-time">{{ formatTime(item.createdAt) }}</div>
            </div>
            <el-tag :type="getConsultStatusType(item.status)" size="small">{{ getConsultStatusText(item.status) }}</el-tag>
          </div>
        </div>
      </div>
    </div>

    <div class="quick-actions">
      <h3>快捷操作</h3>
      <div class="actions-grid">
        <div class="action-item" @click="router.push('/doctor/consultations')">
          <el-icon :size="32" color="#1890ff"><Calendar /></el-icon>
          <span>今日预约</span>
        </div>
        <div class="action-item" @click="router.push('/doctor/consultations')">
          <el-icon :size="32" color="#67c23a"><ChatDotRound /></el-icon>
          <span>处理问诊</span>
        </div>
        <div class="action-item" @click="router.push('/doctor/prescriptions')">
          <el-icon :size="32" color="#e6a23c"><Document /></el-icon>
          <span>开具处方</span>
        </div>
        <div class="action-item" @click="router.push('/my')">
          <el-icon :size="32" color="#f56c6c"><User /></el-icon>
          <span>个人中心</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Calendar, ChatDotRound, Document, User } from '@element-plus/icons-vue'
import { getDoctorTodayAppointments, getDoctorConsultations } from '@/api/doctor'
import { getPrescriptions } from '@/api/prescription'
import type { AppointmentVO } from '@/api/doctor'
import type { ConsultationVO } from '@/api/consultation'

const router = useRouter()
const userStore = useUserStore()

const stats = ref({
  todayAppointments: 0,
  pendingConsultations: 0,
  monthPrescriptions: 0,
  weekPatients: 0
})

const todayAppointments = ref<AppointmentVO[]>([])
const pendingConsultations = ref<ConsultationVO[]>([])

const loadStats = async () => {
  try {
    const [aptRes, consultRes, prescRes] = await Promise.allSettled([
      getDoctorTodayAppointments(),
      getDoctorConsultations('pending'),
      getPrescriptions({})
    ])

    if (aptRes.status === 'fulfilled') {
      const list = (aptRes.value as any)?.data || []
      stats.value.todayAppointments = list.length
      todayAppointments.value = list.slice(0, 5)
    }
    if (consultRes.status === 'fulfilled') {
      const list = (consultRes.value as any)?.data?.records || (consultRes.value as any)?.data || []
      stats.value.pendingConsultations = list.length
      pendingConsultations.value = list.slice(0, 5)
    }
    if (prescRes.status === 'fulfilled') {
      const list = (prescRes.value as any)?.data || []
      stats.value.monthPrescriptions = Array.isArray(list) ? list.length : 0
    }

    try {
      const allConsultRes: any = await getDoctorConsultations('')
      if (allConsultRes?.code === 200) {
        const allList = allConsultRes.data?.records || allConsultRes.data || []
        const patientSet = new Set<number>()
        for (const c of allList) {
          if (c.userId) patientSet.add(c.userId)
        }
        stats.value.weekPatients = patientSet.size
      }
    } catch {
      stats.value.weekPatients = 0
    }
  } catch {
  }
}

const formatTime = (time: string) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString('zh-CN')
}

const getAptStatusType = (status: string) => {
  const map: Record<string, string> = { pending: 'warning', confirmed: 'primary', completed: 'success', cancelled: 'danger' }
  return map[status] || 'info'
}
const getAptStatusText = (status: string) => {
  const map: Record<string, string> = { pending: '待确认', confirmed: '已确认', completed: '已完成', cancelled: '已取消' }
  return map[status] || status
}
const getConsultStatusType = (status: string) => {
  const map: Record<string, string> = { pending: 'warning', in_progress: 'primary', completed: 'success', closed: 'info' }
  return map[status] || 'info'
}
const getConsultStatusText = (status: string) => {
  const map: Record<string, string> = { pending: '待回复', in_progress: '进行中', completed: '已完成', closed: '已关闭' }
  return map[status] || status
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.dashboard-header {
  margin-bottom: 24px;
}

.dashboard-header h2 {
  color: #333;
  margin-bottom: 4px;
}

.welcome-text {
  color: #999;
  font-size: 14px;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 13px;
  color: #999;
  margin-top: 4px;
}

.content-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 24px;
}

.recent-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.card-header h3 {
  font-size: 15px;
  color: #333;
}

.card-body {
  padding: 12px 20px;
}

.recent-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f5f5f5;
}

.recent-item:last-child {
  border-bottom: none;
}

.item-title {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}

.item-time {
  font-size: 12px;
  color: #999;
}

.empty-tip {
  text-align: center;
  color: #ccc;
  padding: 30px 0;
  font-size: 14px;
}

.quick-actions {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 20px;
}

.quick-actions h3 {
  font-size: 15px;
  color: #333;
  margin-bottom: 16px;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px;
  border-radius: 8px;
  background: #fafafa;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
  color: #666;
}

.action-item:hover {
  background: #e6f7ff;
  color: #1890ff;
}

@media (max-width: 768px) {
  .stats-row, .actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .content-row {
    grid-template-columns: 1fr;
  }
}
</style>
