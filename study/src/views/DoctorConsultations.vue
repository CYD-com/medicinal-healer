<template>
  <div class="doctor-consultations">
    <div class="filter-bar">
      <el-radio-group v-model="statusFilter" @change="fetchConsultations">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="pending">待回复</el-radio-button>
        <el-radio-button label="in_progress">进行中</el-radio-button>
        <el-radio-button label="completed">已完成</el-radio-button>
      </el-radio-group>
    </div>

    <el-table :data="consultations" stripe border style="width: 100%" v-loading="loading">
      <el-table-column prop="consultationNo" label="问诊编号" width="180" />
      <el-table-column prop="symptom" label="主诉" show-overflow-tooltip />
      <el-table-column prop="description" label="详细描述" show-overflow-tooltip />
      <el-table-column label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.consultationType === 'video' ? 'danger' : 'primary'" size="small">
            {{ row.consultationType === 'video' ? '视频' : '图文' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusType(row.status)">{{ row.statusText }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="提交时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="showDetail(row)">查看详情</el-button>
          <el-button
            v-if="row.status === 'pending' || row.status === 'in_progress'"
            type="success"
            link
            @click="showReplyDialog(row)"
          >
            回复
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && consultations.length === 0" description="暂无问诊记录" />

    <el-dialog v-model="detailVisible" title="问诊详情" width="600px">
      <div v-if="currentConsultation" class="consultation-detail">
        <div class="detail-section">
          <h4>患者信息</h4>
          <p><strong>问诊编号：</strong>{{ currentConsultation.consulationNo }}</p>
          <p><strong>主诉：</strong>{{ currentConsultation.symptom }}</p>
          <p><strong>详细描述：</strong>{{ currentConsultation.description }}</p>
          <p v-if="currentConsultation.medicalHistory"><strong>既往病史：</strong>{{ currentConsultation.medicalHistory }}</p>
          <p v-if="currentConsultation.currentMedication"><strong>当前用药：</strong>{{ currentConsultation.currentMedication }}</p>
        </div>
        <div class="detail-section" v-if="currentConsultation.doctorReply">
          <h4>医生回复</h4>
          <p>{{ currentConsultation.doctorReply }}</p>
        </div>
        <div class="detail-section" v-if="currentConsultation.diagnosis">
          <h4>诊断结果</h4>
          <p>{{ currentConsultation.diagnosis }}</p>
        </div>
      </div>
    </el-dialog>

    <el-dialog v-model="replyVisible" title="回复问诊" width="560px">
      <el-form :model="replyForm" label-width="80px" ref="replyFormRef" :rules="replyRules">
        <el-form-item label="医生回复" prop="doctorReply">
          <el-input v-model="replyForm.doctorReply" type="textarea" :rows="4" placeholder="请输入诊断意见和回复" />
        </el-form-item>
        <el-form-item label="诊断结果" prop="diagnosis">
          <el-input v-model="replyForm.diagnosis" type="textarea" :rows="2" placeholder="请输入诊断结果" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="replyVisible = false">取消</el-button>
        <el-button type="success" @click="handleComplete" :loading="submitting">标记完成</el-button>
        <el-button type="primary" @click="handleReply" :loading="submitting">提交回复</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { getDoctorConsultations, replyConsultation, completeConsultation } from '@/api/doctor'

const consultations = ref<any[]>([])
const loading = ref(false)
const statusFilter = ref('')
const detailVisible = ref(false)
const replyVisible = ref(false)
const submitting = ref(false)
const currentConsultation = ref<any>(null)
const replyFormRef = ref<FormInstance>()

const replyForm = ref({
  id: 0,
  doctorReply: '',
  diagnosis: ''
})

const replyRules = {
  doctorReply: [{ required: true, message: '请输入回复内容', trigger: 'blur' }]
}

const statusType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    in_progress: 'primary',
    completed: 'success',
    closed: 'info'
  }
  return map[status] || 'info'
}

const fetchConsultations = async () => {
  loading.value = true
  try {
    const res: any = await getDoctorConsultations(statusFilter.value)
    if (res.code === 200) {
      consultations.value = res.data
    }
  } catch (e) {
    console.error('获取问诊列表失败', e)
  } finally {
    loading.value = false
  }
}

const showDetail = (row: any) => {
  currentConsultation.value = row
  detailVisible.value = true
}

const showReplyDialog = (row: any) => {
  replyForm.value = { id: row.id, doctorReply: row.doctorReply || '', diagnosis: row.diagnosis || '' }
  replyVisible.value = true
}

const handleReply = async () => {
  if (!replyFormRef.value) return
  await replyFormRef.value.validate()
  submitting.value = true
  try {
    await replyConsultation(replyForm.value.id, {
      doctorReply: replyForm.value.doctorReply,
      diagnosis: replyForm.value.diagnosis
    })
    ElMessage.success('回复成功')
    replyVisible.value = false
    fetchConsultations()
  } catch {
    // 错误提示已由全局拦截器处理
  } finally {
    submitting.value = false
  }
}

const handleComplete = async () => {
  if (!replyFormRef.value) return
  await replyFormRef.value.validate()
  submitting.value = true
  try {
    if (replyForm.value.doctorReply) {
      await replyConsultation(replyForm.value.id, {
        doctorReply: replyForm.value.doctorReply,
        diagnosis: replyForm.value.diagnosis
      })
    }
    await completeConsultation(replyForm.value.id, { diagnosis: replyForm.value.diagnosis || replyForm.value.doctorReply })
    ElMessage.success('问诊已完成')
    replyVisible.value = false
    fetchConsultations()
  } catch {
    // 错误提示已由全局拦截器处理
  } finally {
    submitting.value = false
  }
}

onMounted(fetchConsultations)
</script>

<style scoped>
.doctor-consultations {
  padding: 0;
}

.filter-bar {
  margin-bottom: 20px;
}

.consultation-detail {
  line-height: 1.8;
}

.detail-section {
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #eee;
}

.detail-section:last-child {
  border-bottom: none;
}

.detail-section h4 {
  color: #409eff;
  margin-bottom: 8px;
  font-size: 15px;
}

.detail-section p {
  margin: 4px 0;
  color: #606266;
  font-size: 14px;
}
</style>
