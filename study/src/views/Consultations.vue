﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿﻿<template>
  <div class="consultation-container">
    <div class="page-header">
      <h2>在线问诊</h2>
      <div class="header-actions" v-if="userStore.isPatient">
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
          <Pagination
            v-model:page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            @change="loadConsultations"
          />
        </div>
      </el-tab-pane>

      <el-tab-pane label="发起问诊" name="new-consultation" v-if="userStore.isPatient">
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

    <el-dialog v-model="chatDialogVisible" :title="chatTitle" width="600px" top="5vh" destroy-on-close>
      <div class="wechat-chat">
        <div class="chat-header-bar">
          <span class="chat-doctor-name">{{ chatConsultation?.doctor?.name }} 医生</span>
          <el-tag :type="getStatusType(chatConsultation?.status || '')" size="small">
            {{ getStatusText(chatConsultation?.status || '') }}
          </el-tag>
        </div>

        <div class="chat-messages" ref="chatMessagesRef">
          <div class="chat-time">{{ formatTime(chatConsultation?.createdAt || '') }}</div>

          <div class="message-row patient-msg">
            <div class="msg-avatar patient-avatar">
              <el-icon :size="20"><User /></el-icon>
            </div>
            <div class="msg-bubble patient-bubble">
              <div class="msg-sender">我的描述</div>
              <div class="msg-text">{{ chatConsultation?.description }}</div>
            </div>
          </div>

          <div v-for="msg in chatMessages" :key="msg.id" class="message-row" :class="msg.senderRole === 'doctor' ? 'doctor-msg' : 'patient-msg'">
            <div class="msg-avatar" :class="msg.senderRole === 'doctor' ? 'doctor-avatar' : 'patient-avatar'">
              <el-icon :size="20"><User /></el-icon>
            </div>
            <div class="msg-bubble" :class="msg.senderRole === 'doctor' ? 'doctor-bubble' : 'patient-bubble'">
              <div class="msg-sender">{{ msg.senderRole === 'doctor' ? '医生' : '我' }} · {{ msg.senderName }}</div>
              <div class="msg-text">{{ msg.content }}</div>
              <div class="msg-time">{{ formatTime(msg.createdAt) }}</div>
            </div>
          </div>

          <div v-if="chatConsultation?.status === 'pending' && chatMessages.length === 0" class="system-msg">
            <span>等待医生回复中...</span>
          </div>

          <div v-if="chatConsultation?.status === 'completed' && chatConsultation?.diagnosis" class="system-msg diagnosis-msg">
            <div class="diagnosis-card">
              <el-icon><Document /></el-icon>
              <span>诊断结果：{{ chatConsultation.diagnosis }}</span>
            </div>
          </div>

          <div v-if="chatConsultation?.status === 'completed'" class="system-msg">
            <span>问诊已结束</span>
          </div>
        </div>

        <div v-if="(chatConsultation?.status === 'in_progress' || chatConsultation?.status === 'pending')" class="chat-input-area">
          <el-input v-model="patientMessage" type="textarea" :rows="3" placeholder="输入回复内容..." @keydown.enter.ctrl="sendPatientMessage" />
          <div class="input-tip">按 Ctrl + Enter 发送</div>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="chatDialogVisible = false">关闭</el-button>
          <template v-if="(chatConsultation?.status === 'in_progress' || chatConsultation?.status === 'pending')">
            <el-button type="primary" @click="sendPatientMessage" :disabled="!patientMessage.trim()">发送</el-button>
          </template>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, User, Clock, Money, FirstAidKit, Document } from '@element-plus/icons-vue'
import { getConsultations, createConsultation, cancelConsultation, patientReply, getMessages, type ConsultationMessageVO } from '@/api/consultation'
import { replyConsultation, completeConsultation as completeConsultationApi } from '@/api/doctor'
import type { ConsultationVO } from '@/api/consultation'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const activeTab = ref('my-consultations')
const consultations = ref<ConsultationVO[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

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
const chatMessages = ref<ConsultationMessageVO[]>([])
const replyMessage = ref('')
const patientMessage = ref('')
const chatMessagesRef = ref<HTMLElement | null>(null)

const chatTitle = ref('问诊详情')

const formatTime = (time: string) => {
  if (!time) return ''
  const d = new Date(time)
  const now = new Date()
  const isToday = d.toDateString() === now.toDateString()
  const timeStr = d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  if (isToday) return timeStr
  return d.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' }) + ' ' + timeStr
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
    const res = await getConsultations({ page: currentPage.value, size: pageSize.value }) as any
    consultations.value = res.data?.records || res.data || []
    total.value = res.data?.total || 0
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

const openChatDialog = async (consultation: ConsultationVO) => {
  chatConsultation.value = consultation
  chatTitle.value = `问诊 - ${consultation.doctor?.name} 医生`
  replyMessage.value = ''
  patientMessage.value = ''
  chatMessages.value = []
  chatDialogVisible.value = true
  await loadMessages(consultation.id)
  nextTick(() => {
    if (chatMessagesRef.value) {
      chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight
    }
  })
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

const sendReply = async () => {
  if (!replyMessage.value.trim() || !chatConsultation.value) {
    return
  }

  try {
    await replyConsultation(chatConsultation.value.id, { doctorReply: replyMessage.value })
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
    await replyConsultation(chatConsultation.value.id, { doctorReply: '已接受问诊' })
    ElMessage.success('已接受问诊')
    loadConsultations()
    chatDialogVisible.value = false
  } catch (error) {
    console.error('接受问诊失败:', error)
  }
}

const sendPatientMessage = async () => {
  if (!patientMessage.value.trim() || !chatConsultation.value) {
    return
  }

  try {
    await patientReply(chatConsultation.value.id, patientMessage.value)
    ElMessage.success('回复发送成功')
    patientMessage.value = ''
    await loadMessages(chatConsultation.value.id)
    loadConsultations()
    nextTick(() => {
      if (chatMessagesRef.value) {
        chatMessagesRef.value.scrollTop = chatMessagesRef.value.scrollHeight
      }
    })
  } catch (error) {
    console.error('发送回复失败:', error)
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
    
    await completeConsultationApi(chatConsultation.value.id, { diagnosis: value || '' })
    ElMessage.success('问诊已结束')
    loadConsultations()
    chatDialogVisible.value = false
  } catch (error) {
    if (error !== 'cancel') {
      console.error('结束问诊失败:', error)
    }
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

.chat-doctor-name {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
