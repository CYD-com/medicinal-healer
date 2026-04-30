<template>
  <div class="consultations-container">
    <h2>在线问诊</h2>
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="发起问诊" name="initiate">
        <div class="initiate-card">
          <el-form :model="consultForm" :rules="consultRules" ref="consultFormRef" label-width="100px">
            <el-form-item label="选择医生" prop="doctorId">
              <el-select v-model="consultForm.doctorId" placeholder="请选择医生">
                <el-option v-for="doctor in doctors" :key="doctor.doctorId" :label="doctor.name + ' - ' + doctor.title" :value="doctor.doctorId"></el-option>
              </el-select>
            </el-form-item>
            <el-form-item label="问诊类型" prop="consultationType">
              <el-radio-group v-model="consultForm.consultationType">
                <el-radio label="text">图文问诊</el-radio>
                <el-radio label="video">视频问诊</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="主诉" prop="symptom">
              <el-input v-model="consultForm.symptom" placeholder="请简要描述症状" maxlength="100"></el-input>
            </el-form-item>
            <el-form-item label="详细描述" prop="description">
              <el-input v-model="consultForm.description" type="textarea" placeholder="请详细描述症状、持续时间等" :rows="4"></el-input>
            </el-form-item>
            <el-form-item label="上传图片">
              <el-upload
                class="upload-demo"
                action="#"
                :auto-upload="false"
                :on-change="handleImageChange"
                :limit="3"
                :file-list="imageList"
              >
                <el-button type="primary"><el-icon><Plus /></el-icon> 点击上传</el-button>
              </el-upload>
            </el-form-item>
            <el-form-item label="既往病史">
              <el-input v-model="consultForm.medicalHistory" type="textarea" placeholder="请填写既往病史" :rows="2"></el-input>
            </el-form-item>
            <el-form-item label="当前用药">
              <el-input v-model="consultForm.currentMedication" type="textarea" placeholder="请填写当前用药情况" :rows="2"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitConsultation" :loading="loading">提交问诊</el-button>
            </el-form-item>
          </el-form>
        </div>
      </el-tab-pane>
      <el-tab-pane label="我的问诊" name="my-consultations">
        <div class="my-consultations-card">
          <el-input v-model="consultationSearch" placeholder="搜索问诊" prefix-icon="el-icon-search" style="margin-bottom: 20px"></el-input>
          <el-select v-model="consultationStatus" placeholder="选择状态" style="width: 200px; margin-bottom: 20px" @change="loadConsultations">
            <el-option label="全部" value=""></el-option>
            <el-option label="待回复" value="pending"></el-option>
            <el-option label="进行中" value="in_progress"></el-option>
            <el-option label="已完成" value="completed"></el-option>
            <el-option label="已关闭" value="closed"></el-option>
          </el-select>
          <el-table :data="myConsultations" style="width: 100%">
            <el-table-column prop="consultationNo" label="问诊编号" width="180"></el-table-column>
            <el-table-column label="医生" width="150">
              <template #default="scope">
                <div class="doctor-info">
                  <el-avatar :size="32" :src="scope.row.doctor?.avatar"></el-avatar>
                  <span class="doctor-name">{{ scope.row.doctor?.name }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="symptom" label="主诉"></el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="创建时间" width="180">
              <template #default="scope">
                {{ formatDateTime(scope.row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <el-button type="primary" size="small" @click="viewConsultation(scope.row)">查看</el-button>
                <el-button v-if="scope.row.status === 'pending'" type="danger" size="small" @click="handleCancel(scope.row)">取消</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog title="问诊详情" v-model="dialogVisible" width="600px">
      <div v-if="selectedConsultation" class="consultation-detail">
        <div class="detail-row">
          <span class="label">问诊编号：</span>
          <span>{{ selectedConsultation.consultationNo }}</span>
        </div>
        <div class="detail-row">
          <span class="label">医生：</span>
          <span>{{ selectedConsultation.doctor?.name }} - {{ selectedConsultation.doctor?.title }}</span>
        </div>
        <div class="detail-row">
          <span class="label">问诊类型：</span>
          <span>{{ selectedConsultation.consultationType === 'text' ? '图文问诊' : '视频问诊' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">状态：</span>
          <el-tag :type="getStatusType(selectedConsultation.status)">{{ getStatusText(selectedConsultation.status) }}</el-tag>
        </div>
        <div class="detail-row">
          <span class="label">主诉：</span>
          <span>{{ selectedConsultation.symptom }}</span>
        </div>
        <div class="detail-row">
          <span class="label">详细描述：</span>
          <span>{{ selectedConsultation.description }}</span>
        </div>
        <div class="detail-row" v-if="selectedConsultation.medicalHistory">
          <span class="label">既往病史：</span>
          <span>{{ selectedConsultation.medicalHistory }}</span>
        </div>
        <div class="detail-row" v-if="selectedConsultation.currentMedication">
          <span class="label">当前用药：</span>
          <span>{{ selectedConsultation.currentMedication }}</span>
        </div>
        <div class="detail-row">
          <span class="label">创建时间：</span>
          <span>{{ formatDateTime(selectedConsultation.createdAt) }}</span>
        </div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElForm, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { Doctor } from '@/types'
import { getDoctors } from '@/api/appointment'
import { createConsultation, getConsultations, getConsultationById, cancelConsultation, type ConsultationCreateForm, type ConsultationVO } from '@/api/consultation'

const activeTab = ref('initiate')
const loading = ref(false)
const consultationSearch = ref('')
const consultationStatus = ref('')
const consultFormRef = ref<InstanceType<typeof ElForm>>()
const dialogVisible = ref(false)
const selectedConsultation = ref<ConsultationVO | null>(null)

const consultForm = reactive<ConsultationCreateForm>({
  doctorId: 0,
  consultationType: 'text',
  symptom: '',
  description: '',
  images: '',
  medicalHistory: '',
  currentMedication: ''
})

const consultRules = {
  doctorId: [
    { required: true, message: '请选择医生', trigger: 'blur' }
  ],
  symptom: [
    { required: true, message: '请填写主诉', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请详细描述症状', trigger: 'blur' }
  ]
}

const imageList = ref<any[]>([])
const doctors = ref<Doctor[]>([])
const myConsultations = ref<ConsultationVO[]>([])

const loadDoctors = async () => {
  try {
    const res: any = await getDoctors()
    doctors.value = res.data || []
  } catch (error) {
    console.error('获取医生列表失败:', error)
    ElMessage.error('获取医生列表失败')
  }
}

const loadConsultations = async () => {
  try {
    const res: any = await getConsultations({ status: consultationStatus.value || undefined })
    myConsultations.value = res.data || []
  } catch (error) {
    console.error('获取问诊列表失败:', error)
    ElMessage.error('获取问诊列表失败')
  }
}

const handleTabChange = (tabName: string) => {
  if (tabName === 'my-consultations') {
    loadConsultations()
  }
}

const handleImageChange = (file: any, fileList: any[]) => {
  imageList.value = fileList
}

const submitConsultation = async () => {
  if (!consultFormRef.value) return
  
  consultFormRef.value.validate((valid) => {
    if (!valid) {
      return
    }
    
    loading.value = true
    createConsultation({
      doctorId: consultForm.doctorId,
      consultationType: consultForm.consultationType,
      symptom: consultForm.symptom,
      description: consultForm.description,
      images: consultForm.images,
      medicalHistory: consultForm.medicalHistory,
      currentMedication: consultForm.currentMedication
    }).then(() => {
      ElMessage.success('问诊提交成功')
      consultForm.doctorId = 0
      consultForm.consultationType = 'text'
      consultForm.symptom = ''
      consultForm.description = ''
      consultForm.images = ''
      consultForm.medicalHistory = ''
      consultForm.currentMedication = ''
      imageList.value = []
    }).catch((error) => {
      console.error('提交问诊失败:', error)
      ElMessage.error('提交失败，请稍后重试')
    }).finally(() => {
      loading.value = false
    })
  })
}

const viewConsultation = async (consultation: ConsultationVO) => {
  try {
    const res: any = await getConsultationById(consultation.id)
    selectedConsultation.value = res.data
    dialogVisible.value = true
  } catch (error) {
    console.error('获取问诊详情失败:', error)
    ElMessage.error('获取问诊详情失败')
  }
}

const handleCancel = async (consultation: ConsultationVO) => {
  try {
    await ElMessageBox.confirm(
      '确定要取消这个问诊吗？',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await cancelConsultation(consultation.id)
    ElMessage.success('取消成功')
    loadConsultations()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('取消问诊失败:', error)
      ElMessage.error('取消失败，请稍后重试')
    }
  }
}

const formatDateTime = (dateTime: string) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

const getStatusType = (status: string) => {
  const statusMap: Record<string, string> = {
    pending: 'warning',
    in_progress: 'primary',
    completed: 'success',
    closed: 'info'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    pending: '待回复',
    in_progress: '进行中',
    completed: '已完成',
    closed: '已关闭'
  }
  return statusMap[status] || status
}

onMounted(() => {
  loadDoctors()
})
</script>

<style scoped>
.consultations-container {
  padding: 20px;
}

.consultations-container h2 {
  margin-bottom: 20px;
  color: #333;
}

.initiate-card,
.my-consultations-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 24px;
}

.el-form-item {
  margin-bottom: 20px;
}

.doctor-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.doctor-name {
  font-size: 14px;
  color: #333;
}

.upload-demo {
  margin-top: 8px;
}

.consultation-detail {
  padding: 10px 0;
}

.detail-row {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.detail-row:last-child {
  border-bottom: none;
}

.detail-row .label {
  font-weight: bold;
  color: #666;
  min-width: 100px;
}

@media (max-width: 768px) {
  .initiate-card,
  .my-consultations-card {
    padding: 16px;
  }
}
</style>