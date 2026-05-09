<template>
  <div class="ai-chat-container">
    <div class="chat-sidebar">
      <div class="sidebar-top">
        <el-button type="primary" class="new-chat-btn" @click="createNewSession">
          <el-icon><Plus /></el-icon>
          新建问诊
        </el-button>
      </div>
      <div class="session-list">
        <div
          v-for="session in sessions"
          :key="session.id"
          class="session-item"
          :class="{ active: currentSessionId === session.id }"
          @click="switchSession(session.id)"
        >
          <div class="session-title">{{ session.title }}</div>
          <div class="session-time">{{ formatTime(session.updatedAt) }}</div>
          <el-icon class="delete-icon" @click.stop="deleteSession(session.id)"><Delete /></el-icon>
        </div>
        <div v-if="sessions.length === 0" class="empty-sessions">
          暂无问诊记录
        </div>
      </div>
    </div>

    <div class="chat-main">
      <div v-if="currentSession" class="chat-area">
        <div class="chat-messages" ref="messagesRef">
          <div class="welcome-msg" v-if="currentMessages.length === 0">
            <div class="welcome-icon">
              <el-icon :size="48"><Monitor /></el-icon>
            </div>
            <h3>AI 智能问诊助手</h3>
            <p>请描述您的症状，我将为您提供初步的医疗建议。</p>
            <p class="tip">提示：AI建议仅供参考，如需确切诊断请前往在线问诊或线下就诊。</p>
          </div>
          <div
            v-for="msg in currentMessages"
            :key="msg.id"
            class="message-row"
            :class="msg.role === 'user' ? 'user-msg' : 'ai-msg'"
          >
            <div class="msg-avatar" :class="msg.role === 'user' ? 'user-avatar' : 'ai-avatar'">
              <el-icon :size="20">
                <UserFilled v-if="msg.role === 'user'" />
                <Monitor v-else />
              </el-icon>
            </div>
            <div class="msg-content">
              <div class="msg-bubble" :class="msg.role === 'user' ? 'user-bubble' : 'ai-bubble'">
                <div class="msg-text" v-html="msg.role === 'ai' ? formatAIResponse(msg.content) : msg.content"></div>
              </div>
              <div class="msg-time">{{ formatTime(msg.timestamp) }}</div>
            </div>
          </div>
          <div v-if="loading" class="message-row ai-msg">
            <div class="msg-avatar ai-avatar">
              <el-icon :size="20"><Monitor /></el-icon>
            </div>
            <div class="msg-content">
              <div class="msg-bubble ai-bubble">
                <div class="typing-indicator">
                  <span></span><span></span><span></span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="chat-input-area">
          <div class="input-wrapper">
            <el-input
              v-model="inputMessage"
              type="textarea"
              :rows="2"
              :autosize="{ minRows: 1, maxRows: 4 }"
              placeholder="请描述您的症状，例如：头痛、发烧、咳嗽等..."
              @keydown.enter.exact.prevent="sendMessage"
              :disabled="loading"
            />
            <el-button
              type="primary"
              circle
              :icon="Promotion"
              :disabled="!inputMessage.trim() || loading"
              @click="sendMessage"
              class="send-btn"
            />
          </div>
          <div class="input-footer">
            <span class="disclaimer">AI建议仅供参考，不构成医疗诊断</span>
          </div>
        </div>
      </div>

      <div v-else class="no-session">
        <el-icon :size="64" color="#c0c4cc"><ChatDotRound /></el-icon>
        <p>选择或新建一个问诊对话</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Delete, UserFilled, Monitor, Promotion, ChatDotRound } from '@element-plus/icons-vue'
import { sendChatMessage, type ChatMessage as AIChatMessage } from '@/api/aiChat'

interface ChatMessage {
  id: string
  role: 'user' | 'ai'
  content: string
  timestamp: number
}

interface ChatSession {
  id: string
  title: string
  messages: ChatMessage[]
  createdAt: number
  updatedAt: number
}

const STORAGE_KEY = 'ai_chat_sessions'

const sessions = ref<ChatSession[]>([])
const currentSessionId = ref<string>('')
const inputMessage = ref('')
const loading = ref(false)
const messagesRef = ref<HTMLElement | null>(null)

const currentSession = computed(() =>
  sessions.value.find((s) => s.id === currentSessionId.value) || null
)

const currentMessages = computed(() => currentSession.value?.messages || [])

const loadSessions = () => {
  try {
    const data = localStorage.getItem(STORAGE_KEY)
    if (data) {
      sessions.value = JSON.parse(data)
    }
  } catch {
    sessions.value = []
  }
}

const saveSessions = () => {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(sessions.value))
}

const createNewSession = () => {
  const session: ChatSession = {
    id: Date.now().toString(),
    title: '新问诊',
    messages: [],
    createdAt: Date.now(),
    updatedAt: Date.now()
  }
  sessions.value.unshift(session)
  currentSessionId.value = session.id
  saveSessions()
}

const switchSession = (id: string) => {
  currentSessionId.value = id
  nextTick(scrollToBottom)
}

const deleteSession = async (id: string) => {
  try {
    await ElMessageBox.confirm('确定要删除该问诊记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    sessions.value = sessions.value.filter((s) => s.id !== id)
    if (currentSessionId.value === id) {
      currentSessionId.value = sessions.value[0]?.id || ''
    }
    saveSessions()
  } catch {}
}

const scrollToBottom = () => {
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

const formatTime = (ts: number) => {
  const d = new Date(ts)
  const now = new Date()
  const isToday = d.toDateString() === now.toDateString()
  const timeStr = d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  if (isToday) return timeStr
  return d.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' }) + ' ' + timeStr
}

const formatAIResponse = (text: string): string => {
  return text
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
    .replace(/\n/g, '<br>')
}

const buildConversationHistory = (session: ChatSession): AIChatMessage[] => {
  return session.messages.map((msg) => ({
    role: msg.role === 'user' ? 'user' as const : 'assistant' as const,
    content: msg.content
  }))
}

const sendMessage = async () => {
  if (!inputMessage.value.trim() || loading.value) return

  if (!currentSessionId.value) {
    createNewSession()
  }

  const session = sessions.value.find((s) => s.id === currentSessionId.value)
  if (!session) return

  const userMsg: ChatMessage = {
    id: Date.now().toString(),
    role: 'user',
    content: inputMessage.value.trim(),
    timestamp: Date.now()
  }

  session.messages.push(userMsg)

  if (session.messages.length === 1) {
    const firstMsg = userMsg.content
    session.title = firstMsg.length > 20 ? firstMsg.substring(0, 20) + '...' : firstMsg
  }

  session.updatedAt = Date.now()
  inputMessage.value = ''
  saveSessions()

  loading.value = true
  await nextTick(scrollToBottom)

  try {
    const history = buildConversationHistory(session)
    const aiReply = await sendChatMessage(history)

    const aiMsg: ChatMessage = {
      id: (Date.now() + 1).toString(),
      role: 'ai',
      content: aiReply,
      timestamp: Date.now()
    }
    session.messages.push(aiMsg)
    session.updatedAt = Date.now()
    saveSessions()
  } catch (error: any) {
    const errorMsg = error?.message || 'AI服务暂时不可用，请稍后再试'
    ElMessage.error(errorMsg)
  } finally {
    loading.value = false
    nextTick(scrollToBottom)
  }
}

onMounted(() => {
  loadSessions()
  if (sessions.value.length > 0 && !currentSessionId.value) {
    currentSessionId.value = sessions.value[0].id
  }
})
</script>

<style scoped>
.ai-chat-container {
  display: flex;
  height: calc(100vh - 96px);
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.chat-sidebar {
  width: 260px;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  background: #fafafa;
}

.sidebar-top {
  padding: 16px;
  border-bottom: 1px solid #e8e8e8;
}

.new-chat-btn {
  width: 100%;
}

.session-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}

.session-item {
  padding: 12px;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  margin-bottom: 4px;
  transition: background 0.2s;
}

.session-item:hover {
  background: #e8e8e8;
}

.session-item.active {
  background: #d9ecff;
}

.session-title {
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  padding-right: 24px;
}

.session-time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.delete-icon {
  position: absolute;
  right: 8px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  opacity: 0;
  transition: opacity 0.2s;
}

.session-item:hover .delete-icon {
  opacity: 1;
}

.delete-icon:hover {
  color: #f56c6c;
}

.empty-sessions {
  text-align: center;
  color: #999;
  padding: 40px 0;
  font-size: 13px;
}

.chat-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.welcome-msg {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.welcome-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409eff 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-bottom: 20px;
}

.welcome-msg h3 {
  font-size: 20px;
  color: #333;
  margin: 0 0 12px 0;
}

.welcome-msg p {
  font-size: 14px;
  color: #666;
  margin: 0 0 8px 0;
}

.welcome-msg .tip {
  font-size: 12px;
  color: #999;
  background: #fffbe6;
  border: 1px solid #ffe58f;
  border-radius: 4px;
  padding: 6px 16px;
  margin-top: 8px;
}

.message-row {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  max-width: 80%;
}

.user-msg {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.ai-msg {
  align-self: flex-start;
}

.msg-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.ai-avatar {
  background: linear-gradient(135deg, #409eff 0%, #764ba2 100%);
  color: #fff;
}

.msg-content {
  display: flex;
  flex-direction: column;
}

.msg-bubble {
  padding: 10px 14px;
  border-radius: 12px;
  line-height: 1.6;
  word-break: break-word;
  font-size: 14px;
}

.user-bubble {
  background: #409eff;
  color: #fff;
  border-top-right-radius: 4px;
}

.ai-bubble {
  background: #f4f4f5;
  color: #333;
  border-top-left-radius: 4px;
}

.msg-text {
  white-space: pre-wrap;
}

.msg-time {
  font-size: 11px;
  color: #bbb;
  margin-top: 4px;
}

.user-msg .msg-time {
  text-align: right;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 4px 0;
}

.typing-indicator span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #999;
  animation: typing 1.2s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% { opacity: 0.3; transform: translateY(0); }
  30% { opacity: 1; transform: translateY(-4px); }
}

.chat-input-area {
  padding: 16px 24px;
  border-top: 1px solid #e8e8e8;
  background: #fff;
}

.input-wrapper {
  display: flex;
  align-items: flex-end;
  gap: 12px;
}

.input-wrapper :deep(.el-textarea__inner) {
  border-radius: 8px;
  resize: none;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.send-btn {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
}

.input-footer {
  margin-top: 8px;
}

.disclaimer {
  font-size: 11px;
  color: #bbb;
}

.no-session {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  gap: 12px;
}

.no-session p {
  font-size: 14px;
  margin: 0;
}
</style>
