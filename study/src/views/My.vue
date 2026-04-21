<template>
  <div class="user-center">
    <h2>个人中心</h2>
    <el-card class="user-card">
      <div class="user-avatar">
        <el-avatar :size="100" :src="userInfo.avatar || ''">
          <el-icon><User /></el-icon>
        </el-avatar>
        <el-button type="primary" size="small" class="avatar-upload-btn" @click="avatarInputRef.click()">
          更换头像
        </el-button>
        <input
          ref="avatarInputRef"
          type="file"
          accept="image/*"
          style="display: none"
          @change="handleAvatarUpload"
        />
      </div>
      <el-descriptions :column="2" border class="user-info">
        <el-descriptions-item label="用户名">
          {{ userStore.username }}
        </el-descriptions-item>
        <el-descriptions-item label="邮箱">
          {{ userInfo.email || '暂无' }}
        </el-descriptions-item>
        <el-descriptions-item label="性别">
          {{ userInfo.gender || '暂无' }}
        </el-descriptions-item>
        <el-descriptions-item label="年龄">
          {{ userInfo.age || '暂无' }}
        </el-descriptions-item>
        <el-descriptions-item label="注册时间" :span="2">
          {{ userInfo.registerDate || '暂无' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { User } from '@element-plus/icons-vue'
import { uploadAvatar, updateAvatar, getUserInfo } from '@/api/user'
import { ElMessage } from 'element-plus'
import { API_BASE_URL } from '@/constants'

const userStore = useUserStore()
const userInfo = ref({
  email: 'user@example.com',
  gender: '男',
  age: 25,
  registerDate: '2024-01-01',
  avatar: ''
})

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const res = await getUserInfo()
    if (res.code === 200) {
      userInfo.value = {
        ...userInfo.value,
        avatar: res.data.avatar ? API_BASE_URL + res.data.avatar : '',
        email: res.data.email || 'user@example.com',
        gender: res.data.gender || '男',
        age: res.data.age || 25,
        registerDate: res.data.createTime || '2024-01-01'
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 页面加载时获取用户信息
onMounted(() => {
  fetchUserInfo()
})

const avatarInputRef = ref(null)

const handleAvatarUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  try {
    // 上传头像
    const uploadRes = await uploadAvatar(file)
    if (uploadRes.code === 200) {
      const avatarUrl = uploadRes.data
      
      // 更新用户头像
      const updateRes = await updateAvatar(avatarUrl)
      if (updateRes.code === 200) {
        userInfo.value.avatar = API_BASE_URL + avatarUrl
        ElMessage.success('头像更新成功')
      } else {
        ElMessage.error('更新头像失败')
      }
    } else {
      ElMessage.error('上传头像失败')
    }
  } catch (error) {
    ElMessage.error('上传失败，请重试')
  } finally {
    // 清空input value，允许重复选择同一文件
    event.target.value = ''
  }
}
</script>

<style scoped>
.user-center {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

h2 {
  margin-bottom: 20px;
  color: #333;
}

.user-card {
  margin-top: 20px;
}

.user-avatar {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.avatar-upload-btn {
  margin-top: 10px;
}

.user-info {
  margin-top: 20px;
}
</style>
