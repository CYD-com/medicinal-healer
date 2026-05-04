<template>
  <div class="admin-statistics">
    <div class="stats-grid">
      <div class="stat-card" v-for="item in statCards" :key="item.label">
        <div class="stat-icon" :style="{ background: item.color }">
          <el-icon :size="28"><component :is="item.icon" /></el-icon>
        </div>
        <div class="stat-info">
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-label">{{ item.label }}</div>
        </div>
      </div>
    </div>

    <div class="charts-row">
      <el-card shadow="hover" class="chart-card">
        <template #header>
          <span>问诊状态分布</span>
        </template>
        <div class="consultation-stats">
          <div class="consult-item">
            <span class="consult-dot pending"></span>
            <span class="consult-label">待回复</span>
            <span class="consult-count">{{ stats.pendingConsultations || 0 }}</span>
          </div>
          <div class="consult-item">
            <span class="consult-dot in-progress"></span>
            <span class="consult-label">进行中</span>
            <span class="consult-count">{{ stats.inProgressConsultations || 0 }}</span>
          </div>
          <div class="consult-item">
            <span class="consult-dot completed"></span>
            <span class="consult-label">已完成</span>
            <span class="consult-count">{{ stats.completedConsultations || 0 }}</span>
          </div>
        </div>
        <el-progress
          v-if="stats.totalConsultations"
          :percentage="completedPercent"
          :format="() => `完成率 ${completedPercent}%`"
          :stroke-width="20"
          style="margin-top: 20px"
        />
      </el-card>

      <el-card shadow="hover" class="chart-card">
        <template #header>
          <span>系统概览</span>
        </template>
        <div class="overview-list">
          <div class="overview-item">
            <span>注册用户数</span>
            <el-tag type="primary" size="large">{{ stats.totalUsers || 0 }}</el-tag>
          </div>
          <div class="overview-item">
            <span>医生总数</span>
            <el-tag type="success" size="large">{{ stats.totalDoctors || 0 }}</el-tag>
          </div>
          <div class="overview-item">
            <span>科室总数</span>
            <el-tag type="warning" size="large">{{ stats.totalDepartments || 0 }}</el-tag>
          </div>
          <div class="overview-item">
            <span>预约总数</span>
            <el-tag type="danger" size="large">{{ stats.totalAppointments || 0 }}</el-tag>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { User, Avatar, OfficeBuilding, Calendar, ChatDotRound } from '@element-plus/icons-vue'
import { getStatistics, type StatisticsData } from '@/api/admin'

const stats = ref<StatisticsData>({
  totalUsers: 0,
  totalDoctors: 0,
  totalDepartments: 0,
  totalAppointments: 0,
  pendingConsultations: 0,
  inProgressConsultations: 0,
  completedConsultations: 0,
  totalConsultations: 0
})

const completedPercent = computed(() => {
  if (!stats.value.totalConsultations) return 0
  return Math.round((stats.value.completedConsultations / stats.value.totalConsultations) * 100)
})

const statCards = computed(() => [
  { label: '注册用户', value: stats.value.totalUsers, icon: User, color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
  { label: '医生总数', value: stats.value.totalDoctors, icon: Avatar, color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
  { label: '科室数量', value: stats.value.totalDepartments, icon: OfficeBuilding, color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
  { label: '预约总数', value: stats.value.totalAppointments, icon: Calendar, color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)' },
  { label: '问诊总数', value: stats.value.totalConsultations, icon: ChatDotRound, color: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' }
])

onMounted(async () => {
  try {
    const res: any = await getStatistics()
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (e) {
    console.error('获取统计数据失败', e)
  }
})
</script>

<style scoped>
.admin-statistics {
  padding: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  line-height: 1.2;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.chart-card {
  border-radius: 12px;
}

.consultation-stats {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.consult-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.consult-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.consult-dot.pending { background: #e6a23c; }
.consult-dot.in-progress { background: #409eff; }
.consult-dot.completed { background: #67c23a; }

.consult-label {
  flex: 1;
  font-size: 14px;
  color: #606266;
}

.consult-count {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.overview-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.overview-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 8px;
  font-size: 14px;
  color: #606266;
}

@media (max-width: 768px) {
  .charts-row {
    grid-template-columns: 1fr;
  }
}
</style>
