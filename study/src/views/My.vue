<template>
  <div class="my-container">
    <h2>个人中心</h2>
    <div class="my-card">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="个人信息" name="info">
          <div class="profile-section">
            <div class="avatar-section">
              <el-upload
                class="avatar-uploader"
                :auto-upload="false"
                :show-file-list="false"
                :on-change="handleAvatarChange"
                accept="image/*"
              >
                <el-avatar :size="120" :src="userForm.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"></el-avatar>
                <div class="avatar-tip">点击更换头像</div>
              </el-upload>
            </div>
            <el-form :model="userForm" label-width="100px" class="profile-form">
              <el-form-item label="用户名">
                <el-input v-model="userForm.username" disabled></el-input>
              </el-form-item>
              <el-form-item label="真实姓名">
                <el-input v-model="userForm.realName" placeholder="请输入真实姓名"></el-input>
              </el-form-item>
              <el-form-item label="性别">
                <el-radio-group v-model="userForm.gender">
                  <el-radio label="male">男</el-radio>
                  <el-radio label="female">女</el-radio>
                  <el-radio label="other">其他</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="年龄">
                <el-input-number v-model="userForm.age" :min="1" :max="150"></el-input-number>
              </el-form-item>
              <el-form-item label="手机号">
                <el-input v-model="userForm.phone" placeholder="请输入手机号"></el-input>
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input v-model="userForm.email" placeholder="请输入邮箱"></el-input>
              </el-form-item>
              <el-form-item label="身份证号">
                <el-input v-model="userForm.idCard" placeholder="请输入身份证号"></el-input>
              </el-form-item>
              <el-form-item label="地址">
                <el-input v-model="userForm.address" type="textarea" :rows="2" placeholder="请输入地址"></el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleSaveProfile" :loading="saving">保存信息</el-button>
              </el-form-item>
            </el-form>
          </div>
        </el-tab-pane>
        <el-tab-pane label="修改密码" name="password">
          <el-form :model="passwordForm" label-width="100px" class="password-form">
            <el-form-item label="旧密码">
              <el-input v-model="passwordForm.oldPassword" type="password" show-password placeholder="请输入旧密码"></el-input>
            </el-form-item>
            <el-form-item label="新密码">
              <el-input v-model="passwordForm.newPassword" type="password" show-password placeholder="请输入新密码"></el-input>
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input v-model="passwordForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getUserInfo, updateUserInfo, updateAvatar, uploadAvatar } from '@/api/user'

const router = useRouter()
const activeTab = ref('info')
const saving = ref(false)

const userForm = ref({
  username: '',
  realName: '',
  gender: '',
  age: null as number | null,
  phone: '',
  email: '',
  idCard: '',
  address: '',
  avatar: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const loadUserInfo = async () => {
  try {
    const res: any = await getUserInfo()
    if (res.code === 200) {
      const data = res.data
      userForm.value = {
        username: data.username || '',
        realName: data.realName || '',
        gender: data.gender || '',
        age: data.age || null,
        phone: data.phone || '',
        email: data.email || '',
        idCard: data.idCard || '',
        address: data.address || '',
        avatar: data.avatar || ''
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

const handleAvatarChange = async (file: any) => {
  try {
    const formData = new FormData()
    formData.append('file', file.raw)
    const uploadRes: any = await uploadAvatar(file.raw)
    if (uploadRes.code === 200) {
      const avatarUrl = uploadRes.data
      await updateAvatar(avatarUrl)
      userForm.value.avatar = avatarUrl
      ElMessage.success('头像更新成功')
    }
  } catch (error) {
    ElMessage.error('头像上传失败')
  }
}

const handleSaveProfile = async () => {
  saving.value = true
  try {
    const res: any = await updateUserInfo({
      realName: userForm.value.realName,
      gender: userForm.value.gender,
      age: userForm.value.age || undefined,
      phone: userForm.value.phone,
      email: userForm.value.email,
      idCard: userForm.value.idCard,
      address: userForm.value.address
    })
    if (res.code === 200) {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const handleChangePassword = () => {
  if (!passwordForm.value.oldPassword || !passwordForm.value.newPassword || !passwordForm.value.confirmPassword) {
    ElMessage.warning('请填写完整密码信息')
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  ElMessage.success('密码修改成功')
  passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
}

onMounted(() => {
  loadUserInfo()
})
</script>

<style scoped>
.my-container {
  padding: 20px;
}

.my-container h2 {
  margin-bottom: 20px;
  color: #333;
}

.my-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 24px;
}

.profile-section {
  display: flex;
  gap: 40px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 150px;
}

.avatar-uploader {
  text-align: center;
  cursor: pointer;
}

.avatar-tip {
  margin-top: 10px;
  font-size: 12px;
  color: #999;
}

.profile-form {
  flex: 1;
  max-width: 500px;
}

.password-form {
  max-width: 400px;
}

@media (max-width: 768px) {
  .profile-section {
    flex-direction: column;
    align-items: center;
  }

  .profile-form {
    width: 100%;
  }
}
</style>
