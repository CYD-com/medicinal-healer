<template>
  <div class="consultation-container">
    <div class="page-header">
      <h2>在线问诊</h2>
      <div class="header-actions">
        <el-button type="primary" @click="showNewConsultationDialog">
          <el-icon><Plus /></el-icon>
          发起问诊
        </el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="custom-tabs">
      <el-tab-pane label="我的问诊" name="my-consultations">
        <div class="consultation-list">
          <div v-if="consultations.length === 0" class="empty-state">
            <el-empty description="暂无问诊记录" />
          </div>
          <div v-else class="consultation-cards">
            <div v-for="item in consultations" :key="item.id" class="consultation-card" @click="openChatDialog(item)">
              <div class="card-header">
                <div class="doctor-info">
                  <div class="doctor-avatar">
                    <el-icon :size="24"><User /></el-icon>
                  </div>
                  <div class="doctor-details">
                    <h4>{{ item.doctor?.name }}</h4>
                    <span>{{ item.doctor?.departmentName }} · {{ item.doctor?.title }}</span>
                  </div>
                </div>
                <el-tag :type="getStatusType(item.status)">
                  {{ getStatusText(item.status) }}
                </el-tag>
              </div>
              <div class="card-body">
                <p class="symptom">症状描述：{{ item.symptom }}</p>
                <div class="meta-info">
                  <span><el-icon><Clock /></el-icon> {{ formatTime(item.createdAt) }}</span>
                  <span v-if="item.consultationFee"><el-icon><Money /></el-icon> ¥{{ item.consultationFee }}</span>
                </div>
              </div>
              <div class="card-footer">
                <el-button type="primary" link size="small" @click.stop="openChatDialog(item)">
                  查看对话
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </el-tab-pane>

      <el-tab-pane label="发起问诊" name="new-consultation">
        <div class="new-consultation-form">
          <div class="symptom-selector">
            <h3>选择症状</h3>
            <div class="symptom-tags">
              <el-tag v-for="symptom in symptomList" :key="symptom" 
                      :class="{ 'is-selected': selectedSymptoms.includes(symptom) }"
                      @click="toggleSymptom(symptom)" effect="plain" class="symptom-tag">
                {{ symptom }}
              </el-tag>
            </div>
          </div>

          <div class="form-section">
            <h3>选择医生</h3>
            <div class="doctor-list">
              <div v-for="doctor in availableDoctors" :key="doctor.id" 
                   class="doctor-card" 
                   :class="{ 'is-selected': selectedDoctor === doctor.id }"
                   @click="selectedDoctor = doctor.id">
                <div class="doctor-avatar">
                  <el-icon :size="32"><User /></el-icon>
                </div>
                <div class="doctor-info">
                  <h4>{{ doctor.name }}</h4>
                  <p class="title">{{ doctor.title }}</p>
                  <p class="specialty">{{ doctor.specialty }}</p>
                </div>
              </div>
            </div>
          </div>

          <div class="form-section">
            <h3>症状详情</h3>
            <el-input v-model="consultationForm.symptomDescription" 
                      type="textarea" 
                      :rows="4"
                      placeholder="请详细描述您的症状、持续时间、严重程度等..." />
          </div>

          <div class="form-actions">
            <el-button type="primary" size="large" @click="submitConsultation" 
                       :disabled="!selectedDoctor || selectedSymptoms.length === 0">
              提交问诊
            </el-button>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="chatDialogVisible" :title="chatTitle" width="600px" top="5vh">
      <div class="chat-container">
        <div class="chat-header-info">
          <el-descriptions :column="2" border size="small">
            <el-descriptions-item label="患者">{{ chatConsultation?.patientName }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(chatConsultation?.status || '')">
                {{ getStatusText(chatConsultation?.status || '') }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="chat-messages" ref="chatMessagesRef">
          <div class="chat-time">{{ formatTime(chatConsultation?.createdAt || '') }}</div>
          
          <div class="message patient">
            <div class="message-content">
              <div class="message-label">患者描述</div>
              <div class="message-text">{{ chatConsultation?.description }}</div>
            </div>
          </div>

          <template v-if="chatConsultation?.status === 'in_progress' || chatConsultation?.status === 'completed'">
            <div class="message doctor">
              <div class="message-content">
                <div class="message-label">{{ chatConsultation?.doctor?.name }} 医生</div>
                <div class="message-text">{{ chatConsultation?.doctorReply || '暂无回复' }}</div>
              </div>
            </div>
          </template>

          <template v-if="chatConsultation?.status === 'completed'">
            <div class="message system">
              <div class="message-content">
                <div class="message-text">诊断结果：{{ chatConsultation?.diagnosis }}</div>
              </div>
            </div>
            <div class="message system">
              <div class="message-content">
                <div class="message-text">处方建议：{{ chatConsultation?.prescriptionSuggestion }}</div>
              </div>
            </div>
          </template>

          <template v-if="chatConsultation?.status === 'pending'">
            <div class="message system">
              <div class="message-content">
                <div class="message-text">等待医生回复中...</div>
              </div>
            </div>
          </template>
        </div>

        <div v-if="chatConsultation?.status === 'in_progress'" class="chat-input-area">
          <el-input v-model="replyMessage" type="textarea" :rows="3" placeholder="输入回复内容..." />
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="chatDialogVisible = false">关闭</el-button>
          <template v-if="chatConsultation?.status === 'pending'">
            <el-button type="success" @click="acceptConsultation">接受问诊</el-button>
          </template>
          <template v-else-if="chatConsultation?.status === 'in_progress'">
            <el-button type="primary" @click="sendReply">发送回复</el-button>
            <el-button type="success" @click="completeConsultation">结束问诊</el-button>
          </template>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, User, Clock, Money } from '@element-plus/icons-vue'
import { getConsultations, createConsultation, updateConsultation } from '@/api/consultation'
import type { ConsultationVO } from '@/api/consultation'

const activeTab = ref('my-consultations')
const consultations = ref<ConsultationVO[]>([])

const symptomList = ['发烧', '咳嗽', '头痛', '胃痛', '皮疹', '关节疼痛', '失眠', '视力问题', '其他']

const availableDoctors = ref([
  { id: 1, name: '张医生', title: '主任医师', specialty: '内科常见病' },
  { id: 2, name: '李医生', title: '副主任医师', specialty: '心血管疾病' },
  { id: 3, name: '王医生', title: '主治医师', specialty: '呼吸系统疾病' },
  { id: 4, name: '赵医生', title: '住院医师', specialty: '皮肤科' }
])

const selectedSymptoms = ref<string[]>([])
const selectedDoctor = ref<number | null>(null)
const consultationForm = ref({
  symptomDescription: '',
  consultationType: 'text' as 'text' | 'video'
})

const chatDialogVisible = ref(false)
const chatConsultation = ref<ConsultationVO | null>(null)
const replyMessage = ref('')
const chatMessagesRef = ref<HTMLElement | null>(null)

const chatTitle = ref('问诊详情')

const formatTime = (time: string) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    in_progress: 'primary',
    completed: 'success',
    closed: 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待回复',
    in_progress: '进行中',
    completed: '已完成',
    closed: '已关闭'
  }
  return map[status] || status
}

const toggleSymptom = (symptom: string) => {
  const index = selectedSymptoms.value.indexOf(symptom)
  if (index > -1) {
    selectedSymptoms.value.splice(index, 1)
  } else {
    selectedSymptoms.value.push(symptom)
  }
}

const loadConsultations = async () => {
  try {
    const res = await getConsultations({}) as any
    consultations.value = res.data || []
  } catch (error) {
    console.error('加载问诊列表失败:', error)
  }
}

const showNewConsultationDialog = () => {
  activeTab.value = 'new-consultation'
}

const submitConsultation = async () => {
  if (!selectedDoctor.value || selectedSymptoms.value.length === 0) {
    ElMessage.warning('请选择医生和症状')
    return
  }

  if (!consultationForm.value.symptomDescription.trim()) {
    ElMessage.warning('请填写症状详情')
    return
  }

  try {
    const data = {
      doctorId: selectedDoctor.value,
      symptom: selectedSymptoms.value.join('、'),
      description: consultationForm.value.symptomDescription,
      consultationType: consultationForm.value.consultationType
    }

    await createConsultation(data)
    ElMessage.success('问诊提交成功')
    activeTab.value = 'my-consultations'
    loadConsultations()
  } catch (error) {
    console.error('提交问诊失败:', error)
  }
}

const openChatDialog = (consultation: ConsultationVO) => {
  chatConsultation.value = consultation
  chatTitle.value = `问诊 - ${consultation.doctor?.name} 医生`
  replyMessage.value = ''
  chatDialogVisible.value = true
  nextTick(() => {
    if (chatMessagesRef.value) {
      chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight
    }
  })
}

const sendReply = async () => {
  if (!replyMessage.value.trim() || !chatConsultation.value) {
    return
  }

  try {
    await updateConsultationStatus(chatConsultation.value.id, 'in_progress', replyMessage.value)
    ElMessage.success('回复发送成功')
    loadConsultations()
    chatDialogVisible.value = false
  } catch (error) {
    console.error('发送回复失败:', error)
  }
}

const acceptConsultation = async () => {
  if (!chatConsultation.value) return
  try {
    await updateConsultationStatus(chatConsultation.value.id, 'in_progress')
    ElMessage.success('已接受问诊')
    loadConsultations()
    chatDialogVisible.value = false
  } catch (error) {
    console.error('接受问诊失败:', error)
  }
}

const completeConsultation = async () => {
  if (!chatConsultation.value) return
  try {
    const { value } = await ElMessageBox.prompt('请输入诊断结果', '结束问诊', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPlaceholder: '请输入诊断结果'
    })
    
    await updateConsultationStatus(chatConsultation.value.id, 'completed', '', value || '')
    ElMessage.success('问诊已结束')
    loadConsultations()
    chatDialogVisible.value = false
  } catch (error) {
    if (error !== 'cancel') {
      console.error('结束问诊失败:', error)
    }
  }
}

const updateConsultationStatus = async (id: number, status: string, reply?: string, diagnosis?: string) => {
  try {
    const data: any = { status }
    if (reply) data.doctorReply = reply
    if (diagnosis) data.diagnosis = diagnosis
    await updateConsultation(id, data)
  } catch (error) {
    throw error
  }
}

onMounted(() => {
  loadConsultations()
})
</script>

<style scoped>
.consultation-container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  color: #333;
  margin: 0;
}

.custom-tabs {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.consultation-cards {
  display: grid;
  gap: 16px;
}

.consultation-card {
  background: #fff;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.consultation-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.doctor-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.doctor-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.doctor-details h4 {
  margin: 0 0 4px 0;
  font-size: 15px;
  color: #333;
}

.doctor-details span {
  font-size: 12px;
  color: #999;
}

.card-body .symptom {
  font-size: 14px;
  color: #666;
  margin: 0 0 8px 0;
}

.meta-info {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #999;
}

.meta-info span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-footer {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
}

.new-consultation-form {
  max-width: 800px;
  margin: 0 auto;
}

.symptom-selector {
  margin-bottom: 24px;
}

.symptom-selector h3,
.form-section h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 12px;
}

.symptom-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.symptom-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.symptom-tag.is-selected {
  background-color: #409eff;
  color: white;
  border-color: #409eff;
}

.form-section {
  margin-bottom: 24px;
}

.doctor-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 12px;
}

.doctor-card {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s;
  text-align: center;
}

.doctor-card.is-selected {
  border-color: #409eff;
  background-color: #f0f7ff;
}

.doctor-card .doctor-avatar {
  width: 48px;
  height: 48px;
  margin: 0 auto 12px;
}

.doctor-card .doctor-info h4 {
  margin: 0 0 4px 0;
  font-size: 15px;
  color: #333;
}

.doctor-card .title {
  font-size: 12px;
  color: #999;
  margin: 0 0 4px 0;
}

.doctor-card .specialty {
  font-size: 12px;
  color: #666;
  margin: 0;
}

.form-actions {
  text-align: center;
  margin-top: 24px;
}

.empty-state {
  padding: 40px;
  text-align: center;
}

.chat-container {
  display: flex;
  flex-direction: column;
  height: 500px;
}

.chat-header-info {
  margin-bottom: 16px;
  flex-shrink: 0;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 16px;
}

.chat-time {
  text-align: center;
  font-size: 12px;
  color: #999;
  margin-bottom: 16px;
}

.message {
  margin-bottom: 16px;
  display: flex;
}

.message.patient {
  justify-content: flex-end;
}

.message.doctor {
  justify-content: flex-start;
}

.message.system {
  justify-content: center;
}

.message-content {
  max-width: 80%;
  padding: 12px 16px;
  border-radius: 12px;
}

.message.patient .message-content {
  background: #409eff;
  color: white;
  border-bottom-right-radius: 4px;
}

.message.doctor .message-content {
  background: white;
  color: #333;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.message.system .message-content {
  background: #fff3cd;
  color: #856404;
  text-align: center;
  font-size: 13px;
}

.message-label {
  font-size: 12px;
  opacity: 0.8;
  margin-bottom: 4px;
}

.message.doctor .message-label {
  color: #409eff;
}

.message-text {
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.chat-input-area {
  flex-shrink: 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
