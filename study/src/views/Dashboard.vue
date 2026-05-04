<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <h2>工作台</h2>
      <p class="welcome-text">欢迎回来，{{ userStore.username }}</p>
    </div>

    <div class="stats-row">
      <div class="stat-card" @click="router.push('/appointments')">
        <div class="stat-icon" style="background: #e6f7ff; color: #1890ff;">
          <el-icon :size="28"><Calendar /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.pendingAppointments }}</div>
          <div class="stat-label">待就诊预约</div>
        </div>
      </div>
      <div class="stat-card" @click="router.push('/consultations')">
        <div class="stat-icon" style="background: #fff7e6; color: #fa8c16;">
          <el-icon :size="28"><ChatDotRound /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.activeConsultations }}</div>
          <div class="stat-label">进行中问诊</div>
        </div>
      </div>
      <div class="stat-card" @click="router.push('/prescriptions')">
        <div class="stat-icon" style="background: #f6ffed; color: #52c41a;">
          <el-icon :size="28"><Document /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.totalPrescriptions }}</div>
          <div class="stat-label">处方数量</div>
        </div>
      </div>
      <div class="stat-card" @click="router.push('/healthRecords')">
        <div class="stat-icon" style="background: #fff1f0; color: #f5222d;">
          <el-icon :size="28"><FirstAidKit /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ stats.healthScore }}</div>
          <div class="stat-label">健康评分</div>
        </div>
      </div>
    </div>

    <div class="content-row">
      <div class="recent-card">
        <div class="card-header">
          <h3>最近预约</h3>
          <el-button type="primary" link @click="router.push('/appointments')">查看全部</el-button>
        </div>
        <div class="card-body">
          <div v-if="recentAppointments.length === 0" class="empty-tip">暂无预约</div>
          <div v-for="item in recentAppointments" :key="item.appointmentId" class="recent-item">
            <div class="item-left">
              <div class="item-title">{{ item.doctor?.name }} - {{ item.department }}</div>
              <div class="item-time">{{ item.appointmentDate }} {{ item.timeSlot?.startTime }}-{{ item.timeSlot?.endTime }}</div>
            </div>
            <el-tag :type="getStatusType(item.status)" size="small">{{ getStatusText(item.status) }}</el-tag>
          </div>
        </div>
      </div>

      <div class="recent-card">
        <div class="card-header">
          <h3>最近问诊</h3>
          <el-button type="primary" link @click="router.push('/consultations')">查看全部</el-button>
        </div>
        <div class="card-body">
          <div v-if="recentConsultations.length === 0" class="empty-tip">暂无问诊</div>
          <div v-for="item in recentConsultations" :key="item.id" class="recent-item">
            <div class="item-left">
              <div class="item-title">{{ item.doctor?.name }} - {{ item.symptom }}</div>
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
        <div class="action-item" @click="router.push('/appointments')">
          <el-icon :size="32" color="#409eff"><Calendar /></el-icon>
          <span>预约挂号</span>
        </div>
        <div class="action-item" @click="router.push('/consultations')">
          <el-icon :size="32" color="#67c23a"><ChatDotRound /></el-icon>
          <span>在线问诊</span>
        </div>
        <div class="action-item" @click="router.push('/healthRecords')">
          <el-icon :size="32" color="#e6a23c"><FirstAidKit /></el-icon>
          <span>健康档案</span>
        </div>
        <div class="action-item" @click="router.push('/prescriptions')">
          <el-icon :size="32" color="#f56c6c"><Document /></el-icon>
          <span>我的处方</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Calendar, ChatDotRound, Document, FirstAidKit } from '@element-plus/icons-vue'
import { getAppointments } from '@/api/appointment'
import { getConsultations } from '@/api/consultation'
import { getPrescriptions } from '@/api/prescription'
import { healthRecordAPI } from '@/api/healthRecord'
import type { Appointment } from '@/types'
import type { ConsultationVO } from '@/api/consultation'

const router = useRouter()
const userStore = useUserStore()

const stats = ref({
  pendingAppointments: 0,
  activeConsultations: 0,
  totalPrescriptions: 0,
  healthScore: 0
})

const recentAppointments = ref<Appointment[]>([])
const recentConsultations = ref<ConsultationVO[]>([])

const loadStats = async () => {
  try {
    const [appointRes, consultRes, prescRes] = await Promise.allSettled([
      getAppointments({ status: 'confirmed', page: 1, size: 5 }),
      getConsultations({ status: 'in_progress' }),
      getPrescriptions({})
    ])

    if (appointRes.status === 'fulfilled') {
      const records = (appointRes.value as any)?.data?.records || []
      stats.value.pendingAppointments = records.length
      recentAppointments.value = records.slice(0, 3)
    }
    if (consultRes.status === 'fulfilled') {
      const list = (consultRes.value as any)?.data || []
      stats.value.activeConsultations = list.length
      recentConsultations.value = list.slice(0, 3)
    }
    if (prescRes.status === 'fulfilled') {
      stats.value.totalPrescriptions = (prescRes.value as any)?.data?.total || 0
    }

    try {
      const overview = await healthRecordAPI.getOverview()
      stats.value.healthScore = (overview as any)?.statistics?.healthScore || 0
    } catch {
      stats.value.healthScore = 0
    }
  } catch {
  }
}

const formatTime = (time: string) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString('zh-CN')
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = { pending: 'warning', confirmed: 'primary', completed: 'success', cancelled: 'danger' }
  return map[status] || 'info'
}
const getStatusText = (status: string) => {
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
