<template>
  <div class="doctor-consultations">
    <div class="filter-bar">
      <el-radio-group v-model="statusFilter" @change="fetchConsultations">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="pending">待回复</el-radio-button>
          <el-radio-button label="in_progress">进行中</el-radio-button>
        </el-radio-group>
    </div>

    <el-table :data="consultations" stripe border style="width: 100%" v-loading="loading">
      <el-table-column prop="consultationNo" label="问诊编号" width="180" />
      <el-table-column prop="patientName" label="患者姓名" width="120" />
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
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="showDetail(row)">查看详情</el-button>
          <el-button type="success" link @click="openChat(row)">回复</el-button>
          <el-button type="warning" link @click="showPrescriptionDialog(row)">开具处方</el-button>
        </template>
      </el-table-column>
    </el-table>

    <Pagination
      v-model:page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      @change="fetchConsultations"
    />

    <el-empty v-if="!loading && consultations.length === 0" description="暂无问诊记录" />

    <el-dialog v-model="detailVisible" title="问诊详情" width="600px">
      <div v-if="currentConsultation" class="consultation-detail">
        <div class="detail-section">
          <h4>患者信息</h4>
          <p><strong>问诊编号：</strong>{{ currentConsultation.consultationNo }}</p>
          <p><strong>主诉：</strong>{{ currentConsultation.symptom }}</p>
          <p><strong>详细描述：</strong>{{ currentConsultation.description }}</p>
          <p v-if="currentConsultation.medicalHistory"><strong>既往病史：</strong>{{ currentConsultation.medicalHistory }}</p>
          <p v-if="currentConsultation.currentMedication"><strong>当前用药：</strong>{{ currentConsultation.currentMedication }}</p>
        </div>
        <div class="detail-section" v-if="currentConsultation.patientMessage">
          <h4>患者补充</h4>
          <p>{{ currentConsultation.patientMessage }}</p>
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

    <el-dialog v-model="chatVisible" :title="'问诊 - ' + chatData.patientName" width="600px" top="5vh" destroy-on-close>
      <div class="wechat-chat">
        <div class="chat-header-bar">
          <span class="chat-patient-name">{{ chatData.patientName }}</span>
          <el-tag :type="statusType(chatData.status)" size="small">{{ statusText(chatData.status) }}</el-tag>
        </div>

        <div class="chat-messages" ref="chatBoxRef">
          <div class="chat-time">{{ formatTime(chatData.createdAt) }}</div>

          <div class="message-row patient-msg">
            <div class="msg-avatar patient-avatar">
              <el-icon :size="20"><User /></el-icon>
            </div>
            <div class="msg-bubble patient-bubble">
              <div class="msg-sender">患者</div>
              <div class="msg-text">{{ chatData.description }}</div>
              <div class="msg-tags" v-if="chatData.symptom">
                <el-tag size="small" type="info">{{ chatData.symptom }}</el-tag>
              </div>
            </div>
          </div>

          <div v-for="msg in chatMessages" :key="msg.id" class="message-row" :class="msg.senderRole === 'doctor' ? 'doctor-msg' : 'patient-msg'">
            <div class="msg-avatar" :class="msg.senderRole === 'doctor' ? 'doctor-avatar' : 'patient-avatar'">
              <el-icon :size="20"><User /></el-icon>
            </div>
            <div class="msg-bubble" :class="msg.senderRole === 'doctor' ? 'doctor-bubble' : 'patient-bubble'">
              <div class="msg-sender">{{ msg.senderRole === 'doctor' ? '医生' : '患者' }} · {{ msg.senderName }}</div>
              <div class="msg-text">{{ msg.content }}</div>
              <div class="msg-time">{{ formatTime(msg.createdAt) }}</div>
            </div>
          </div>

          <div v-if="chatData.status === 'pending' && chatMessages.length === 0" class="system-msg">
            <span>等待医生回复中...</span>
          </div>

          <div v-if="chatData.diagnosis" class="system-msg diagnosis-msg">
            <div class="diagnosis-card">
              <el-icon><Document /></el-icon>
              <span>诊断结果：{{ chatData.diagnosis }}</span>
            </div>
          </div>

          <div v-if="chatData.status === 'completed'" class="system-msg">
            <span>问诊已结束</span>
          </div>
        </div>

        <div v-if="chatData.status !== 'completed'" class="chat-input-area">
          <el-input
            v-model="chatInput"
            type="textarea"
            :rows="3"
            placeholder="输入回复内容..."
            @keydown.enter.ctrl="handleSendMessage"
          />
          <div class="input-tip">按 Ctrl + Enter 发送</div>
        </div>
      </div>

      <template #footer>
        <div class="chat-footer">
          <el-button @click="chatVisible = false">关闭</el-button>
          <template v-if="chatData.status === 'pending'">
            <el-button type="primary" @click="handleAccept" :loading="chatSending">接受问诊</el-button>
          </template>
          <template v-else-if="chatData.status === 'in_progress'">
            <el-button type="primary" @click="handleSendMessage" :loading="chatSending" :disabled="!chatInput.trim()">发送回复</el-button>
            <el-button type="success" @click="handleEndConsultation">结束问诊</el-button>
          </template>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="prescriptionVisible" title="开具处方" width="700px" top="5vh">
      <el-form :model="prescriptionForm" label-width="100px" ref="prescriptionFormRef" :rules="prescriptionRules">
        <el-form-item label="诊断结果" prop="diagnosis">
          <el-input v-model="prescriptionForm.diagnosis" type="textarea" :rows="2" placeholder="请输入诊断结果" />
        </el-form-item>
        <el-form-item label="医嘱">
          <el-input v-model="prescriptionForm.doctorAdvice" type="textarea" :rows="2" placeholder="请输入医嘱（可选）" />
        </el-form-item>
        <el-form-item label="药品列表" prop="items">
          <div class="drug-list">
            <div v-for="(item, index) in prescriptionForm.items" :key="index" class="drug-item">
              <el-select
                v-model="item.drugId"
                filterable
                remote
                reserve-keyword
                placeholder="搜索药品名称"
                :remote-method="loadDrugs"
                :loading="drugSearching"
                style="width: 180px"
                @change="(val: string) => handleDrugSelect(index, val)"
              >
                <el-option
                  v-for="drug in drugOptions"
                  :key="drug.id"
                  :label="drug.drugName + (drug.specification ? ' (' + drug.specification + ')' : '')"
                  :value="drug.id"
                />
              </el-select>
              <el-input v-model="item.specification" placeholder="规格" style="width: 100px" />
              <el-input v-model="item.dosage" placeholder="用量" style="width: 100px" />
              <el-input-number v-model="item.quantity" :min="1" :max="100" style="width: 100px" />
              <el-input v-model="item.unit" placeholder="单位" style="width: 70px" />
              <el-input-number v-model="item.unitPrice" :min="0" :precision="2" style="width: 100px" />
              <el-button type="danger" :icon="Delete" circle @click="removeDrug(index)" />
            </div>
            <el-button type="primary" link @click="addDrug" style="margin-top: 8px">
              <el-icon><Plus /></el-icon>
              添加药品
            </el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="prescriptionVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePrescriptionSubmit" :loading="prescriptionSubmitting">提交处方</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { Plus, Delete, User, FirstAidKit, Document } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus'
import { getDoctorConsultations, replyConsultation, completeConsultation } from '@/api/doctor'
import { getMessages, type ConsultationMessageVO } from '@/api/consultation'
import { createPrescription, type PrescriptionItemForm, type PrescriptionCreateForm } from '@/api/prescription'
import { searchDrugs, type DrugSearchResult } from '@/api/drug'
import Pagination from '@/components/Pagination.vue'

const consultations = ref<any[]>([])
const loading = ref(false)
const statusFilter = ref('')
const detailVisible = ref(false)
const currentConsultation = ref<any>(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const chatVisible = ref(false)
const chatData = ref<any>({})
const chatMessages = ref<ConsultationMessageVO[]>([])
const chatInput = ref('')
const chatSending = ref(false)
const chatBoxRef = ref<HTMLElement | null>(null)

const prescriptionVisible = ref(false)
const prescriptionSubmitting = ref(false)
const prescriptionFormRef = ref<FormInstance>()
const drugOptions = ref<DrugSearchResult[]>([])
const drugSearching = ref(false)

const emptyDrug = (): PrescriptionItemForm => ({
  drugId: '',
  drugName: '',
  specification: '',
  dosage: '',
  quantity: 1,
  unit: '',
  unitPrice: 0
})

const prescriptionForm = ref<PrescriptionCreateForm>({
  userId: undefined,
  diagnosis: '',
  doctorAdvice: '',
  items: [emptyDrug()]
})

const prescriptionRules = {
  diagnosis: [{ required: true, message: '请输入诊断结果', trigger: 'blur' }],
  items: [{ required: true, message: '请添加至少一个药品', trigger: 'change' }]
}

const loadDrugs = async (query?: string) => {
  drugSearching.value = true
  try {
    const res: any = await searchDrugs(query)
    if (res.code === 200) {
      drugOptions.value = res.data || []
    }
  } catch {
    console.error('获取药品列表失败')
  } finally {
    drugSearching.value = false
  }
}

const handleDrugSelect = (index: number, drugId: string) => {
  const drug = drugOptions.value.find(d => d.id === drugId)
  if (drug) {
    prescriptionForm.value.items[index].drugId = drug.id
    prescriptionForm.value.items[index].drugName = drug.drugName
    prescriptionForm.value.items[index].specification = drug.specification || ''
    prescriptionForm.value.items[index].unit = drug.unit || ''
    prescriptionForm.value.items[index].unitPrice = drug.price || 0
    prescriptionForm.value.items[index].dosage = drug.dosage || ''
  }
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

const statusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待回复',
    in_progress: '进行中',
    completed: '已完成',
    closed: '已关闭'
  }
  return map[status] || status
}

const formatTime = (time: string) => {
  if (!time) return ''
  const d = new Date(time)
  const now = new Date()
  const isToday = d.toDateString() === now.toDateString()
  const timeStr = d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  if (isToday) return timeStr
  return d.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' }) + ' ' + timeStr
}

const scrollToBottom = () => {
  nextTick(() => {
    if (chatBoxRef.value) {
      chatBoxRef.value.scrollTop = chatBoxRef.value.scrollHeight
    }
  })
}

const fetchConsultations = async () => {
  loading.value = true
  try {
    const res: any = await getDoctorConsultations(statusFilter.value, currentPage.value, pageSize.value)
    if (res.code === 200) {
      consultations.value = res.data?.records || res.data || []
      total.value = res.data?.total || 0
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

const openChat = async (row: any) => {
  chatData.value = { ...row }
  chatInput.value = ''
  chatMessages.value = []
  chatVisible.value = true
  await loadMessages(row.id)
  scrollToBottom()
}

const loadMessages = async (consultationId: number) => {
  try {
    const res: any = await getMessages(consultationId)
    if (res.code === 200) {
      chatMessages.value = res.data || []
    }
  } catch {
  }
}

const handleAccept = async () => {
  chatSending.value = true
  try {
    const res: any = await replyConsultation(chatData.value.id, { doctorReply: '已接受问诊，现在可以继续描述您的症状' })
    if (res.code === 200) {
      chatData.value = res.data
      await loadMessages(chatData.value.id)
      ElMessage.success('已接受问诊')
      scrollToBottom()
      fetchConsultations()
    }
  } catch {
  } finally {
    chatSending.value = false
  }
}

const handleSendMessage = async () => {
  if (!chatInput.value.trim()) return
  chatSending.value = true
  try {
    const res: any = await replyConsultation(chatData.value.id, {
      doctorReply: chatInput.value.trim(),
      diagnosis: chatData.value.diagnosis || undefined
    })
    if (res.code === 200) {
      chatData.value = res.data
      chatInput.value = ''
      await loadMessages(chatData.value.id)
      scrollToBottom()
      fetchConsultations()
    }
  } catch {
  } finally {
    chatSending.value = false
  }
}

const handleEndConsultation = async () => {
  try {
    const { value } = await ElMessageBox.prompt('请输入诊断结果', '结束问诊', {
      confirmButtonText: '确定结束',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入诊断结果',
      inputType: 'textarea'
    })
    chatSending.value = true
    const res: any = await completeConsultation(chatData.value.id, { diagnosis: value || '' })
    if (res.code === 200) {
      chatData.value = res.data
      ElMessage.success('问诊已结束')
      scrollToBottom()
      fetchConsultations()
    }
  } catch {
  } finally {
    chatSending.value = false
  }
}

const showPrescriptionDialog = (row: any) => {
  currentConsultation.value = row
  prescriptionForm.value = {
    userId: row.userId,
    diagnosis: row.diagnosis || '',
    doctorAdvice: '',
    items: [{ drugId: '', drugName: '', specification: '', dosage: '', quantity: 1, unit: '', unitPrice: 0 }]
  }
  prescriptionVisible.value = true
  loadDrugs()
}

const addDrug = () => {
  prescriptionForm.value.items.push(emptyDrug())
}

const removeDrug = (index: number) => {
  if (prescriptionForm.value.items.length <= 1) {
    ElMessage.warning('至少保留一个药品')
    return
  }
  prescriptionForm.value.items.splice(index, 1)
}

const handlePrescriptionSubmit = async () => {
  if (!prescriptionFormRef.value) return
  await prescriptionFormRef.value.validate()
  if (prescriptionForm.value.items.length === 0) {
    ElMessage.warning('请添加至少一个药品')
    return
  }
  prescriptionSubmitting.value = true
  try {
    await createPrescription(prescriptionForm.value)
    if (currentConsultation.value) {
      await completeConsultation(currentConsultation.value.id, { diagnosis: prescriptionForm.value.diagnosis })
    }
    ElMessage.success('处方开具成功')
    prescriptionVisible.value = false
    fetchConsultations()
  } catch {
  } finally {
    prescriptionSubmitting.value = false
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

.wechat-chat {
  display: flex;
  flex-direction: column;
  height: 520px;
  background: #ededed;
  border-radius: 6px;
  overflow: hidden;
}

.chat-header-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 16px;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
}

.chat-patient-name {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.chat-time {
  text-align: center;
  font-size: 12px;
  color: #999;
  padding: 4px 0;
}

.message-row {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  max-width: 85%;
}

.patient-msg {
  align-self: flex-start;
  flex-direction: row;
}

.doctor-msg {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.msg-avatar {
  width: 36px;
  height: 36px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.patient-avatar {
  background: #5b9bd5;
  color: #fff;
}

.doctor-avatar {
  background: #07c160;
  color: #fff;
}

.msg-bubble {
  padding: 8px 12px;
  border-radius: 6px;
  position: relative;
  line-height: 1.5;
  word-break: break-word;
}

.patient-bubble {
  background: #fff;
  color: #333;
  border-top-left-radius: 2px;
}

.patient-bubble::before {
  content: '';
  position: absolute;
  left: -6px;
  top: 10px;
  border: 6px solid transparent;
  border-right-color: #fff;
  border-left: none;
}

.doctor-bubble {
  background: #95ec69;
  color: #333;
  border-top-right-radius: 2px;
}

.doctor-bubble::before {
  content: '';
  position: absolute;
  right: -6px;
  top: 10px;
  border: 6px solid transparent;
  border-left-color: #95ec69;
  border-right: none;
}

.msg-sender {
  font-size: 12px;
  color: #999;
  margin-bottom: 2px;
}

.doctor-bubble .msg-sender {
  color: #6a8e3b;
}

.msg-text {
  font-size: 14px;
}

.msg-tags {
  margin-top: 4px;
}

.msg-time {
  font-size: 11px;
  color: #bbb;
  margin-top: 4px;
}

.system-msg {
  text-align: center;
  font-size: 12px;
  color: #999;
  padding: 4px 0;
}

.diagnosis-msg .diagnosis-card {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: #fffbe6;
  border: 1px solid #ffe58f;
  border-radius: 4px;
  padding: 6px 14px;
  font-size: 13px;
  color: #d48806;
}

.chat-input-area {
  padding: 10px 12px;
  background: #f5f5f5;
  border-top: 1px solid #e6e6e6;
}

.input-tip {
  font-size: 11px;
  color: #bbb;
  text-align: right;
  margin-top: 4px;
}

.chat-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.drug-list {
  width: 100%;
}

.drug-item {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
  align-items: center;
  flex-wrap: wrap;
}
</style>
