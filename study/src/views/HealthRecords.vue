<template>
  <div class="health-records-container">
    <h2>健康档案管理</h2>
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="档案概览" name="overview">
        <div class="overview-card" v-loading="overviewLoading">
          <div class="basic-info">
            <h3>基本信息</h3>
            <el-descriptions :column="3">
              <el-descriptions-item label="姓名">{{ overviewData.basicInfo?.realName || '--' }}</el-descriptions-item>
              <el-descriptions-item label="性别">{{ overviewData.basicInfo?.gender === 'male' ? '男' : overviewData.basicInfo?.gender === 'female' ? '女' : overviewData.basicInfo?.gender || '--' }}</el-descriptions-item>
              <el-descriptions-item label="年龄">{{ overviewData.basicInfo?.age ? overviewData.basicInfo.age + '岁' : '--' }}</el-descriptions-item>
              <el-descriptions-item label="身高">{{ overviewData.basicInfo?.height || '--' }}cm</el-descriptions-item>
              <el-descriptions-item label="体重">{{ overviewData.basicInfo?.weight || '--' }}kg</el-descriptions-item>
              <el-descriptions-item label="BMI">{{ overviewData.basicInfo?.bmi || '--' }}</el-descriptions-item>
              <el-descriptions-item label="血型">{{ overviewData.basicInfo?.bloodType || '--' }}型</el-descriptions-item>
              <el-descriptions-item label="婚姻状况">{{ overviewData.basicInfo?.maritalStatus === 'married' ? '已婚' : overviewData.basicInfo?.maritalStatus || '--' }}</el-descriptions-item>
              <el-descriptions-item label="联系方式">{{ overviewData.basicInfo?.phone || '--' }}</el-descriptions-item>
            </el-descriptions>
          </div>
          <div class="health-stats">
            <h3>健康统计</h3>
            <div class="stats-grid">
              <div class="stat-item">
                <el-icon class="stat-icon"><Calendar /></el-icon>
                <div class="stat-content">
                  <div class="stat-value">{{ overviewData.statistics?.totalVisits || 0 }}</div>
                  <div class="stat-label">总就诊次数</div>
                </div>
              </div>
              <div class="stat-item">
                <el-icon class="stat-icon"><Ticket /></el-icon>
                <div class="stat-content">
                  <div class="stat-value">{{ overviewData.statistics?.totalPrescriptions || 0 }}</div>
                  <div class="stat-label">处方数量</div>
                </div>
              </div>
              <div class="stat-item">
                <el-icon class="stat-icon"><Timer /></el-icon>
                <div class="stat-content">
                  <div class="stat-value">{{ overviewData.statistics?.healthScore || 0 }}</div>
                  <div class="stat-label">健康评分</div>
                </div>
              </div>
              <div class="stat-item">
                <el-icon class="stat-icon"><Clock /></el-icon>
                <div class="stat-content">
                  <div class="stat-value">{{ overviewData.statistics?.lastVisitDate || '--' }}</div>
                  <div class="stat-label">最近就诊</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="病史记录" name="medical-history">
        <div class="history-card" v-loading="medicalHistoryLoading">
          <el-collapse>
            <el-collapse-item title="既往病史">
              <el-table :data="pastDiseases" style="width: 100%">
                <el-table-column prop="diseaseName" label="疾病名称" width="180"></el-table-column>
                <el-table-column prop="diagnosisDate" label="诊断日期" width="180"></el-table-column>
                <el-table-column prop="diagnosisHospital" label="诊断医院"></el-table-column>
                <el-table-column prop="currentStatus" label="当前状态" width="120"></el-table-column>
                <el-table-column prop="treatment" label="治疗方式" width="120"></el-table-column>
              </el-table>
            </el-collapse-item>
            <el-collapse-item title="家族病史">
              <el-table :data="familyDiseases" style="width: 100%">
                <el-table-column prop="diseaseName" label="疾病名称" width="180"></el-table-column>
                <el-table-column prop="relation" label="关系" width="120"></el-table-column>
                <el-table-column prop="remark" label="备注"></el-table-column>
              </el-table>
            </el-collapse-item>
            <el-collapse-item title="过敏史">
              <el-table :data="allergies" style="width: 100%">
                <el-table-column prop="allergen" label="过敏原" width="180"></el-table-column>
                <el-table-column prop="reaction" label="反应" width="180"></el-table-column>
                <el-table-column prop="severity" label="严重程度" width="120"></el-table-column>
              </el-table>
            </el-collapse-item>
            <el-collapse-item title="手术史">
              <el-table :data="surgicalHistory" style="width: 100%">
                <el-table-column prop="surgeryName" label="手术名称" width="180"></el-table-column>
                <el-table-column prop="surgeryDate" label="手术日期" width="180"></el-table-column>
                <el-table-column prop="hospital" label="医院"></el-table-column>
                <el-table-column prop="recovery" label="恢复情况" width="120"></el-table-column>
              </el-table>
            </el-collapse-item>
          </el-collapse>
        </div>
      </el-tab-pane>
      <el-tab-pane label="就诊记录" name="visits">
        <div class="visits-card" v-loading="visitsLoading">
          <el-input v-model="visitSearch" placeholder="搜索就诊记录" :prefix-icon="Search" style="margin-bottom: 20px"></el-input>
          <el-table :data="filteredVisits" style="width: 100%">
            <el-table-column prop="visitDate" label="就诊日期" width="180"></el-table-column>
            <el-table-column prop="department" label="科室" width="120"></el-table-column>
            <el-table-column prop="doctor.name" label="医生" width="150"></el-table-column>
            <el-table-column prop="chiefComplaint" label="主诉"></el-table-column>
            <el-table-column prop="diagnosis" label="诊断" width="180"></el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button type="primary" size="small" @click="viewVisitDetail(scope.row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
      <el-tab-pane label="健康指标" name="indicators">
        <div class="indicators-card" v-loading="indicatorsLoading">
          <el-select v-model="indicatorType" placeholder="选择指标类型" style="width: 200px; margin-bottom: 20px">
            <el-option label="血压" value="bloodPressure"></el-option>
            <el-option label="血糖" value="bloodSugar"></el-option>
            <el-option label="体重" value="weight"></el-option>
            <el-option label="身高" value="height"></el-option>
          </el-select>
          <div class="chart-container" style="height: 200px; display: flex; align-items: center; justify-content: center; background: #f5f7fa; border-radius: 8px; color: #909399;">
            <span v-if="indicators.length === 0">暂无{{ indicatorLabel }}数据</span>
            <span v-else>共 {{ indicators.length }} 条{{ indicatorLabel }}记录</span>
          </div>
          <el-table :data="indicators" style="width: 100%; margin-top: 20px">
            <el-table-column prop="recordDate" label="记录日期" width="180"></el-table-column>
            <el-table-column prop="recordTime" label="记录时间" width="120"></el-table-column>
            <el-table-column v-if="indicatorType === 'bloodPressure'" prop="systolic" label="收缩压(mmHg)" width="120"></el-table-column>
            <el-table-column v-if="indicatorType === 'bloodPressure'" prop="diastolic" label="舒张压(mmHg)" width="120"></el-table-column>
            <el-table-column v-if="indicatorType === 'bloodSugar'" prop="value" label="血糖值(mmol/L)" width="120"></el-table-column>
            <el-table-column v-if="indicatorType === 'weight'" prop="value" label="体重(kg)" width="120"></el-table-column>
            <el-table-column v-if="indicatorType === 'height'" prop="value" label="身高(cm)" width="120"></el-table-column>
            <el-table-column prop="remark" label="备注"></el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="visitDetailVisible" title="就诊详情" width="600px">
      <el-descriptions :column="1" border v-if="visitDetail">
        <el-descriptions-item label="就诊日期">{{ visitDetail.visitDate || '--' }}</el-descriptions-item>
        <el-descriptions-item label="就诊类型">{{ visitDetail.visitType === 'outpatient' ? '门诊' : visitDetail.visitType === 'inpatient' ? '住院' : visitDetail.visitType || '--' }}</el-descriptions-item>
        <el-descriptions-item label="科室">{{ visitDetail.department || '--' }}</el-descriptions-item>
        <el-descriptions-item label="主诉">{{ visitDetail.chiefComplaint || '--' }}</el-descriptions-item>
        <el-descriptions-item label="诊断">{{ visitDetail.diagnosis || '--' }}</el-descriptions-item>
        <el-descriptions-item label="治疗方案">{{ visitDetail.treatment || '--' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Calendar, Ticket, Timer, Clock, Search } from '@element-plus/icons-vue'
import { healthRecordAPI } from '../api/healthRecord'

const activeTab = ref('overview')
const visitSearch = ref('')
const indicatorType = ref('bloodPressure')

const pastDiseases = ref([])
const familyDiseases = ref([])
const allergies = ref([])
const surgicalHistory = ref([])
const visits = ref([])
const indicators = ref([])
const overviewLoading = ref(false)
const medicalHistoryLoading = ref(false)
const visitsLoading = ref(false)
const indicatorsLoading = ref(false)
const overviewData = ref({})
const visitDetailVisible = ref(false)
const visitDetail = ref(null)

const indicatorLabel = computed(() => {
  const labels = { bloodPressure: '血压', bloodSugar: '血糖', weight: '体重', height: '身高' }
  return labels[indicatorType.value] || '健康'
})

const filteredVisits = computed(() => {
  if (!visitSearch.value) return visits.value
  const keyword = visitSearch.value.toLowerCase()
  return visits.value.filter(item =>
    (item.visitDate && item.visitDate.toLowerCase().includes(keyword)) ||
    (item.department && item.department.toLowerCase().includes(keyword)) ||
    (item.chiefComplaint && item.chiefComplaint.toLowerCase().includes(keyword)) ||
    (item.diagnosis && item.diagnosis.toLowerCase().includes(keyword))
  )
})

const loadOverview = async () => {
  overviewLoading.value = true
  try {
    const response = await healthRecordAPI.getOverview()
    overviewData.value = response
  } catch (error) {
    ElMessage.error('获取健康档案概览失败')
  } finally {
    overviewLoading.value = false
  }
}

const loadMedicalHistory = async () => {
  medicalHistoryLoading.value = true
  try {
    const response = await healthRecordAPI.getMedicalHistory()
    pastDiseases.value = response.pastDiseases || []
    familyDiseases.value = response.familyDiseases || []
    allergies.value = response.allergies || []
    surgicalHistory.value = response.surgicalHistory || []
  } catch (error) {
    ElMessage.error('获取病史记录失败')
  } finally {
    medicalHistoryLoading.value = false
  }
}

const loadVisits = async () => {
  visitsLoading.value = true
  try {
    const response = await healthRecordAPI.getVisits()
    visits.value = response.list || []
  } catch (error) {
    ElMessage.error('获取就诊记录失败')
  } finally {
    visitsLoading.value = false
  }
}

const loadIndicators = async () => {
  indicatorsLoading.value = true
  try {
    const response = await healthRecordAPI.getIndicators({
      type: indicatorType.value
    })
    indicators.value = response.records || []
  } catch (error) {
    ElMessage.error('获取健康指标记录失败')
  } finally {
    indicatorsLoading.value = false
  }
}

const viewVisitDetail = async (visit) => {
  try {
    const response = await healthRecordAPI.getVisitDetail(visit.visitId)
    visitDetail.value = response
    visitDetailVisible.value = true
  } catch (error) {
    ElMessage.error('获取就诊详情失败')
  }
}

const handleTabChange = (tabName) => {
  switch (tabName) {
    case 'overview':
      loadOverview()
      break
    case 'medical-history':
      loadMedicalHistory()
      break
    case 'visits':
      loadVisits()
      break
    case 'indicators':
      loadIndicators()
      break
  }
}

watch(indicatorType, () => {
  loadIndicators()
})

onMounted(() => {
  loadOverview()
  loadMedicalHistory()
  loadVisits()
  loadIndicators()
})
</script>

<style scoped>
.health-records-container {
  padding: 20px;
}

.health-records-container h2 {
  margin-bottom: 20px;
  color: #333;
}

.overview-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 24px;
}

.basic-info,
.health-stats {
  margin-bottom: 32px;
}

.basic-info h3,
.health-stats h3 {
  margin-bottom: 16px;
  color: #333;
  font-size: 16px;
  font-weight: bold;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.stat-item {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  font-size: 32px;
  color: #409EFF;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.history-card,
.visits-card,
.indicators-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 24px;
}

.chart-container {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
}

.chart-container img {
  max-height: 300px;
}

.el-collapse {
  margin-top: 16px;
}

.el-collapse-item__header {
  font-weight: bold;
}

.el-table {
  margin-top: 16px;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .overview-card,
  .history-card,
  .visits-card,
  .indicators-card {
    padding: 16px;
  }
}
</style>
