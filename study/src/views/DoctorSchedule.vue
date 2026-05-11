<template>
  <div class="schedule-page">
    <div class="page-header">
      <h2 class="page-title">今日排班</h2>
    </div>

    <div class="schedule-toolbar card">
      <div class="toolbar-left">
        <div class="week-nav">
          <el-button :icon="ArrowLeft" circle @click="changeWeek(-1)" />
          <span class="week-range">{{ weekRangeText }}</span>
          <el-button :icon="ArrowRight" circle @click="changeWeek(1)" />
          <el-button type="primary" plain @click="goToday" style="margin-left: 12px">本周</el-button>
        </div>
        <el-radio-group v-model="viewMode" @change="handleViewModeChange" class="view-switch">
          <el-radio-button value="appointment">预约排班</el-radio-button>
          <el-radio-button value="consultation">在线问诊</el-radio-button>
        </el-radio-group>
      </div>
      <el-tag v-if="viewMode === 'appointment'" type="info" size="large">共 {{ totalAppointments }} 个预约</el-tag>
      <el-tag v-else type="info" size="large">共 {{ totalConsultations }} 个问诊</el-tag>
    </div>

    <div class="week-grid card" v-loading="loading">
      <div
        v-for="day in weekDays"
        :key="day.dateStr"
        class="day-column"
        :class="{ 'is-today': day.isToday, 'is-weekend': day.isWeekend }"
      >
        <div class="day-header">
          <span class="weekday-name">{{ day.weekdayName }}</span>
          <span class="date-text" :class="{ 'today-text': day.isToday }">{{ day.dateDisplay }}</span>
          <el-badge
            v-if="viewMode === 'appointment' && day.appointmentCount > 0"
            :value="day.appointmentCount"
            type="primary"
            class="day-badge"
          />
          <el-badge
            v-if="viewMode === 'consultation' && day.consultationCount > 0"
            :value="day.consultationCount"
            type="warning"
            class="day-badge"
          />
        </div>

        <div class="day-body">
          <template v-if="viewMode === 'appointment'">
            <div class="session-block">
              <div class="session-title">上午</div>
              <div
                v-for="slot in MORNING_SLOTS"
                :key="slot.key"
                class="slot-row"
                :class="{
                  'is-current': day.isToday && slot.key === currentSlotKey,
                  'has-apt': getCellAppointments(day.dateStr, slot).length > 0
                }"
              >
                <div class="slot-time">
                  <span v-if="day.isToday && slot.key === currentSlotKey" class="now-dot" />
                  <span class="time-text">{{ slot.label }}</span>
                </div>
                <div class="slot-content">
                  <div
                    v-for="apt in getCellAppointments(day.dateStr, slot)"
                    :key="apt.appointmentId"
                    class="apt-card"
                    :class="'status-' + apt.status"
                    @click="openDetail(apt)"
                  >
                    <span class="apt-name">{{ apt.patientName }}</span>
                    <el-tag :type="statusTagType(apt.status)" size="small" effect="dark" class="apt-tag">
                      {{ statusLabel(apt.status) }}
                    </el-tag>
                  </div>
                </div>
              </div>
            </div>

            <div class="session-divider">午休</div>

            <div class="session-block">
              <div class="session-title">下午</div>
              <div
                v-for="slot in AFTERNOON_SLOTS"
                :key="slot.key"
                class="slot-row"
                :class="{
                  'is-current': day.isToday && slot.key === currentSlotKey,
                  'has-apt': getCellAppointments(day.dateStr, slot).length > 0
                }"
              >
                <div class="slot-time">
                  <span v-if="day.isToday && slot.key === currentSlotKey" class="now-dot" />
                  <span class="time-text">{{ slot.label }}</span>
                </div>
                <div class="slot-content">
                  <div
                    v-for="apt in getCellAppointments(day.dateStr, slot)"
                    :key="apt.appointmentId"
                    class="apt-card"
                    :class="'status-' + apt.status"
                    @click="openDetail(apt)"
                  >
                    <span class="apt-name">{{ apt.patientName }}</span>
                    <el-tag :type="statusTagType(apt.status)" size="small" effect="dark" class="apt-tag">
                      {{ statusLabel(apt.status) }}
                    </el-tag>
                  </div>
                </div>
              </div>
            </div>
          </template>

          <template v-else>
            <div v-if="getDayConsultations(day.dateStr).length === 0" class="day-empty">
              暂无问诊
            </div>
            <div
              v-for="consult in getDayConsultations(day.dateStr)"
              :key="consult.id"
              class="consult-card"
              :class="'consult-status-' + consult.status"
              @click="openConsultDetail(consult)"
            >
              <div class="consult-card-header">
                <span class="consult-type">
                  {{ consult.consultationType === 'video' ? '视频' : '图文' }}
                </span>
                <el-tag :type="consultStatusTagType(consult.status)" size="small" effect="dark">
                  {{ consultStatusText(consult.status) }}
                </el-tag>
              </div>
              <div class="consult-card-body">
                <div class="consult-patient">{{ consult.patientName || '未知患者' }}</div>
                <div class="consult-symptom">{{ consult.symptom }}</div>
              </div>
              <div class="consult-card-footer">
                {{ formatConsultTime(consult.createdAt) }}
              </div>
            </div>
          </template>
        </div>
      </div>
    </div>

    <el-empty v-if="!loading && viewMode === 'appointment' && totalAppointments === 0" description="本周工作日暂无预约" />
    <el-empty v-if="!loading && viewMode === 'consultation' && totalConsultations === 0" description="本周暂无在线问诊" />

    <el-drawer
      v-model="drawerVisible"
      :title="drawerTitle"
      size="420px"
      direction="rtl"
    >
      <template v-if="selectedApt && drawerMode === 'appointment'">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="患者姓名">
            {{ selectedApt.patientName }}
          </el-descriptions-item>
          <el-descriptions-item label="联系电话">
            {{ selectedApt.patientPhone }}
          </el-descriptions-item>
          <el-descriptions-item label="预约日期">
            {{ selectedApt.appointmentDate }}
          </el-descriptions-item>
          <el-descriptions-item label="预约时间">
            {{ selectedApt.timeSlot?.startTime }} - {{ selectedApt.timeSlot?.endTime }}
          </el-descriptions-item>
          <el-descriptions-item label="症状描述">
            {{ selectedApt.symptoms || '无' }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusTagType(selectedApt.status)" size="large">
              {{ statusLabel(selectedApt.status) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        <div class="drawer-footer" v-if="selectedApt.status === 'pending' || selectedApt.status === 'confirmed'">
          <el-button
            v-if="selectedApt.status === 'pending'"
            type="success"
            @click="handleConfirm(selectedApt)"
          >
            确认接诊
          </el-button>
          <el-button
            v-if="selectedApt.status === 'confirmed'"
            type="primary"
            @click="handleComplete(selectedApt)"
          >
            完成就诊
          </el-button>
        </div>
      </template>

      <template v-if="selectedConsult && drawerMode === 'consultation'">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="患者姓名">
            {{ selectedConsult.patientName || '未知' }}
          </el-descriptions-item>
          <el-descriptions-item label="问诊编号">
            {{ selectedConsult.consultationNo }}
          </el-descriptions-item>
          <el-descriptions-item label="问诊类型">
            <el-tag size="small" :type="selectedConsult.consultationType === 'video' ? 'danger' : 'primary'">
              {{ selectedConsult.consultationType === 'video' ? '视频问诊' : '图文问诊' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="主诉">
            {{ selectedConsult.symptom }}
          </el-descriptions-item>
          <el-descriptions-item label="详细描述">
            {{ selectedConsult.description }}
          </el-descriptions-item>
          <el-descriptions-item label="既往病史" v-if="selectedConsult.medicalHistory">
            {{ selectedConsult.medicalHistory }}
          </el-descriptions-item>
          <el-descriptions-item label="当前用药" v-if="selectedConsult.currentMedication">
            {{ selectedConsult.currentMedication }}
          </el-descriptions-item>
          <el-descriptions-item label="诊断结果" v-if="selectedConsult.diagnosis">
            {{ selectedConsult.diagnosis }}
          </el-descriptions-item>
          <el-descriptions-item label="医生回复" v-if="selectedConsult.doctorReply">
            {{ selectedConsult.doctorReply }}
          </el-descriptions-item>
          <el-descriptions-item label="提交时间">
            {{ selectedConsult.createdAt }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="consultStatusTagType(selectedConsult.status)" size="large">
              {{ consultStatusText(selectedConsult.status) }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
        <div class="drawer-footer" v-if="selectedConsult.status === 'pending'">
          <el-button type="primary" @click="$router.push('/doctor/consultations')">
            前往处理
          </el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getDoctorSchedule, getDoctorConsultations } from '@/api/doctor'
import { updateAppointment } from '@/api/appointment'
import type { AppointmentVO } from '@/api/doctor'
import type { ConsultationVO } from '@/api/consultation'

type ViewMode = 'appointment' | 'consultation'

interface WeekDay {
  dateStr: string
  date: Date
  weekdayName: string
  dateDisplay: string
  isToday: boolean
  isWeekend: boolean
  appointmentCount: number
  consultationCount: number
}

interface SlotDef {
  key: string
  label: string
  startTime: string
  endTime: string
}

const WEEKDAY_NAMES = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']

const MORNING_SLOTS: SlotDef[] = [
  { key: '08:00-08:30', label: '08:00 - 08:30', startTime: '08:00', endTime: '08:30' },
  { key: '08:30-09:00', label: '08:30 - 09:00', startTime: '08:30', endTime: '09:00' },
  { key: '09:00-09:30', label: '09:00 - 09:30', startTime: '09:00', endTime: '09:30' },
  { key: '09:30-10:00', label: '09:30 - 10:00', startTime: '09:30', endTime: '10:00' },
  { key: '10:00-10:30', label: '10:00 - 10:30', startTime: '10:00', endTime: '10:30' },
  { key: '10:30-11:00', label: '10:30 - 11:00', startTime: '10:30', endTime: '11:00' },
  { key: '11:00-11:30', label: '11:00 - 11:30', startTime: '11:00', endTime: '11:30' },
]

const AFTERNOON_SLOTS: SlotDef[] = [
  { key: '14:00-14:30', label: '14:00 - 14:30', startTime: '14:00', endTime: '14:30' },
  { key: '14:30-15:00', label: '14:30 - 15:00', startTime: '14:30', endTime: '15:00' },
  { key: '15:00-15:30', label: '15:00 - 15:30', startTime: '15:00', endTime: '15:30' },
  { key: '15:30-16:00', label: '15:30 - 16:00', startTime: '15:30', endTime: '16:00' },
  { key: '16:00-16:30', label: '16:00 - 16:30', startTime: '16:00', endTime: '16:30' },
  { key: '16:30-17:00', label: '16:30 - 17:00', startTime: '16:30', endTime: '17:00' },
]

const loading = ref(false)
const weekOffset = ref(0)
const viewMode = ref<ViewMode>('appointment')
const scheduleData = ref<Record<string, AppointmentVO[]>>({})
const consultationData = ref<Record<string, ConsultationVO[]>>({})
const drawerVisible = ref(false)
const drawerMode = ref<'appointment' | 'consultation'>('appointment')
const selectedApt = ref<AppointmentVO | null>(null)
const selectedConsult = ref<ConsultationVO | null>(null)

const now = ref(new Date())
let nowTimer: ReturnType<typeof setInterval> | null = null

const drawerTitle = computed(() => {
  return drawerMode.value === 'appointment' ? '预约详情' : '问诊详情'
})

const statusLabel = (status: string) => {
  const map: Record<string, string> = {
    pending: '待确认',
    confirmed: '已确认',
    completed: '已完成',
    cancelled: '已取消',
  }
  return map[status] || status
}

const statusTagType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    confirmed: 'primary',
    completed: 'success',
    cancelled: 'info',
  }
  return map[status] || 'info'
}

const consultStatusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待回复',
    in_progress: '进行中',
    completed: '已完成',
    closed: '已关闭',
  }
  return map[status] || status
}

const consultStatusTagType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    in_progress: 'primary',
    completed: 'success',
    closed: 'info',
  }
  return map[status] || 'info'
}

const formatDateKey = (d: Date): string => {
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

const formatConsultTime = (time: string) => {
  if (!time) return ''
  const d = new Date(time)
  return `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

const getWeekStart = (offset: number): Date => {
  const today = new Date()
  const day = today.getDay()
  const diff = day === 0 ? -6 : 1 - day
  const monday = new Date(today)
  monday.setDate(today.getDate() + diff + offset * 7)
  monday.setHours(0, 0, 0, 0)
  return monday
}

const weekStart = computed(() => getWeekStart(weekOffset.value))

const weekDays = computed<WeekDay[]>(() => {
  const start = weekStart.value
  const todayStr = formatDateKey(now.value)
  return Array.from({ length: 7 }, (_, i) => {
    const d = new Date(start)
    d.setDate(start.getDate() + i)
    const dateStr = formatDateKey(d)
    return {
      dateStr,
      date: d,
      weekdayName: WEEKDAY_NAMES[i],
      dateDisplay: `${d.getMonth() + 1}/${d.getDate()}`,
      isToday: dateStr === todayStr,
      isWeekend: i >= 5,
      appointmentCount: (scheduleData.value[dateStr] || []).length,
      consultationCount: (consultationData.value[dateStr] || []).length,
    }
  })
})

const weekRangeText = computed(() => {
  const days = weekDays.value
  if (days.length === 0) return ''
  const first = days[0].date
  const last = days[days.length - 1].date
  const fy = first.getFullYear()
  const ly = last.getFullYear()
  if (fy === ly) {
    return `${fy}年${first.getMonth() + 1}月${first.getDate()}日 — ${last.getMonth() + 1}月${last.getDate()}日`
  }
  return `${fy}年${first.getMonth() + 1}月${first.getDate()}日 — ${ly}年${last.getMonth() + 1}月${last.getDate()}日`
})

const currentSlotKey = computed(() => {
  const h = now.value.getHours()
  const m = now.value.getMinutes()
  const mm = `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`
  const all = [...MORNING_SLOTS, ...AFTERNOON_SLOTS]
  for (const slot of all) {
    if (mm >= slot.startTime && mm < slot.endTime) {
      return slot.key
    }
  }
  return ''
})

const totalAppointments = computed(() => {
  return Object.values(scheduleData.value).reduce((sum, apts) => sum + apts.length, 0)
})

const totalConsultations = computed(() => {
  return Object.values(consultationData.value).reduce((sum, list) => sum + list.length, 0)
})

const getCellAppointments = (dateStr: string, slot: SlotDef): AppointmentVO[] => {
  const apts = scheduleData.value[dateStr] || []
  return apts.filter((a) => {
    const sTime = a.timeSlot?.startTime || ''
    return sTime >= slot.startTime && sTime < slot.endTime
  })
}

const getDayConsultations = (dateStr: string): ConsultationVO[] => {
  return consultationData.value[dateStr] || []
}

const fetchWeekSchedule = async () => {
  loading.value = true
  const days = weekDays.value
  try {
    const promises = days.map((day) =>
      getDoctorSchedule(day.dateStr).catch(() => ({ code: 200, data: [] }))
    )
    const results = await Promise.all(promises)
    const data: Record<string, AppointmentVO[]> = {}
    days.forEach((day, idx) => {
      const res = results[idx] as any
      data[day.dateStr] = res.code === 200 && Array.isArray(res.data) ? res.data : []
    })
    scheduleData.value = data
  } catch (e) {
    console.error('获取排班失败', e)
  } finally {
    loading.value = false
  }
}

const fetchWeekConsultations = async () => {
  loading.value = true
  try {
    const res: any = await getDoctorConsultations('', 1, 500)
    if (res.code === 200) {
      const list: ConsultationVO[] = res.data?.records || res.data || []
      const days = weekDays.value
      const weekStartStr = days[0]?.dateStr || ''
      const weekEndStr = days[days.length - 1]?.dateStr || ''
      const grouped: Record<string, ConsultationVO[]> = {}
      for (const day of days) {
        grouped[day.dateStr] = []
      }
      for (const consult of list) {
        if (!consult.createdAt) continue
        const dateKey = consult.createdAt.substring(0, 10)
        if (dateKey >= weekStartStr && dateKey <= weekEndStr && grouped[dateKey]) {
          grouped[dateKey].push(consult)
        }
      }
      consultationData.value = grouped
    }
  } catch (e) {
    console.error('获取问诊列表失败', e)
  } finally {
    loading.value = false
  }
}

const fetchCurrentData = () => {
  if (viewMode.value === 'appointment') {
    fetchWeekSchedule()
  } else {
    fetchWeekConsultations()
  }
}

const changeWeek = (direction: -1 | 1) => {
  weekOffset.value += direction
  fetchCurrentData()
}

const goToday = () => {
  weekOffset.value = 0
  fetchCurrentData()
  scrollToCurrentSlot()
}

const handleViewModeChange = () => {
  fetchCurrentData()
}

const scrollToCurrentSlot = () => {
  nextTick(() => {
    const el = document.querySelector('.day-column.is-today .slot-row.is-current')
    if (el) {
      el.scrollIntoView({ behavior: 'smooth', block: 'center' })
    }
  })
}

const openDetail = (apt: AppointmentVO) => {
  selectedApt.value = apt
  drawerMode.value = 'appointment'
  drawerVisible.value = true
}

const openConsultDetail = (consult: ConsultationVO) => {
  selectedConsult.value = consult
  drawerMode.value = 'consultation'
  drawerVisible.value = true
}

const handleConfirm = async (apt: AppointmentVO) => {
  try {
    await updateAppointment(String(apt.appointmentId), { status: 'confirmed' })
    ElMessage.success('已确认接诊')
    apt.status = 'confirmed'
  } catch {
  }
}

const handleComplete = async (apt: AppointmentVO) => {
  try {
    await updateAppointment(String(apt.appointmentId), { status: 'completed' })
    ElMessage.success('已完成就诊')
    apt.status = 'completed'
  } catch {
  }
}

onMounted(() => {
  fetchCurrentData()
  nowTimer = setInterval(() => {
    now.value = new Date()
  }, 30000)
  scrollToCurrentSlot()
})

onUnmounted(() => {
  if (nowTimer) clearInterval(nowTimer)
})
</script>

<style scoped>
.schedule-page {
  padding: 20px;
}

.schedule-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 12px;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.week-nav {
  display: flex;
  align-items: center;
  gap: 8px;
}

.week-range {
  min-width: 320px;
  text-align: center;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.view-switch {
  flex-shrink: 0;
}

.week-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
  padding: 8px;
}

.day-column {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  transition: border-color 0.3s, box-shadow 0.3s;
}

.day-column.is-today {
  border-color: #409eff;
  box-shadow: 0 0 0 1px #409eff;
}

.day-column.is-weekend .day-header {
  background: #fef0f0;
  color: #f56c6c;
}

.day-column.is-today.is-weekend .day-header {
  background: #409eff;
  color: #fff;
}

.day-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  padding: 10px 8px;
  background: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
}

.day-column.is-today .day-header {
  background: #409eff;
  color: #fff;
}

.weekday-name {
  font-weight: 600;
  font-size: 14px;
}

.date-text {
  font-size: 12px;
}

.day-column.is-today .date-text {
  color: #fff;
  font-weight: 600;
}

.day-badge {
  margin-left: 4px;
}

.day-body {
  max-height: calc(100vh - 240px);
  overflow-y: auto;
}

.day-empty {
  text-align: center;
  color: #c0c4cc;
  padding: 40px 0;
  font-size: 13px;
}

.session-block {
  padding: 0;
}

.session-title {
  padding: 6px 10px;
  font-size: 12px;
  font-weight: 600;
  color: #909399;
  background: #fafbfc;
  border-bottom: 1px solid #f2f3f5;
}

.slot-row {
  display: flex;
  border-bottom: 1px solid #f2f3f5;
  transition: background-color 0.3s;
  min-height: 36px;
}

.slot-row:last-child {
  border-bottom: none;
}

.slot-row.has-apt {
  min-height: 44px;
}

.slot-row.is-current {
  background: #ecf5ff;
}

.slot-row.is-current.has-apt {
  background: #d9ecff;
}

.slot-time {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 8px;
  min-width: 90px;
  flex-shrink: 0;
  border-right: 1px solid #f2f3f5;
  background: #fafbfc;
}

.day-column.is-today .slot-row.is-current .slot-time {
  background: #d9ecff;
}

.time-text {
  font-size: 11px;
  color: #909399;
  white-space: nowrap;
}

.slot-row.is-current .time-text {
  color: #409eff;
  font-weight: 600;
}

.now-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #f56c6c;
  animation: pulse 1.5s ease-in-out infinite;
  flex-shrink: 0;
}

@keyframes pulse {
  0%, 100% { opacity: 1; transform: scale(1); }
  50% { opacity: 0.5; transform: scale(1.4); }
}

.slot-content {
  flex: 1;
  padding: 4px 6px;
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  align-items: center;
}

.apt-card {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: transform 0.15s, box-shadow 0.15s;
  line-height: 1.6;
}

.apt-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
}

.apt-card.status-pending {
  background: #fdf6ec;
  border-left: 3px solid #e6a23c;
}

.apt-card.status-confirmed {
  background: #ecf5ff;
  border-left: 3px solid #409eff;
}

.apt-card.status-completed {
  background: #f0f9eb;
  border-left: 3px solid #67c23a;
}

.apt-card.status-cancelled {
  background: #f4f4f5;
  border-left: 3px solid #909399;
}

.apt-name {
  font-weight: 500;
  color: #303133;
}

.apt-tag {
  flex-shrink: 0;
}

.session-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 4px 0;
  background: #fafbfc;
  border-top: 1px dashed #dcdfe6;
  border-bottom: 1px dashed #dcdfe6;
  font-size: 11px;
  color: #c0c4cc;
  letter-spacing: 4px;
}

.consult-card {
  padding: 10px 12px;
  margin: 6px 8px;
  border-radius: 6px;
  cursor: pointer;
  transition: transform 0.15s, box-shadow 0.15s;
  border-left: 3px solid #409eff;
  background: #ecf5ff;
}

.consult-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.12);
}

.consult-card.consult-status-pending {
  background: #fdf6ec;
  border-left-color: #e6a23c;
}

.consult-card.consult-status-in_progress {
  background: #ecf5ff;
  border-left-color: #409eff;
}

.consult-card.consult-status-completed {
  background: #f0f9eb;
  border-left-color: #67c23a;
}

.consult-card.consult-status-closed {
  background: #f4f4f5;
  border-left-color: #909399;
}

.consult-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.consult-type {
  font-size: 11px;
  color: #909399;
}

.consult-card-body {
  margin-bottom: 6px;
}

.consult-patient {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 2px;
}

.consult-symptom {
  font-size: 12px;
  color: #606266;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.consult-card-footer {
  font-size: 11px;
  color: #c0c4cc;
}

.drawer-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}

@media (max-width: 768px) {
  .week-grid {
    grid-template-columns: 1fr;
  }

  .week-range {
    min-width: auto;
    font-size: 14px;
  }

  .schedule-toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .toolbar-left {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
