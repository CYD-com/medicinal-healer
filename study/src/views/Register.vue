<template>
  <el-row class="login-page">
    <el-col :span="12" class="left-section">
      <img src="@/assets/nb.jpg" alt="注册页面背景图" class="login-image" />
    </el-col>

    <el-col :span="12" class="right-section">
      <div class="login-form-wrapper">
        <h2 class="form-title">账户注册</h2>

        <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名"
              prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码（至少6位）"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="form.confirmPassword"
              type="password"
              placeholder="请确认密码"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item prop="code">
            <div class="captcha-wrapper">
              <el-input
                v-model="form.code"
                placeholder="请输入验证码"
                prefix-icon="Key"
                class="captcha-input"
              />
              <img
                :src="captchaImage"
                alt="验证码"
                class="captcha-image"
                @click="refreshCaptcha"
                title="点击刷新验证码"
              />
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              class="login-btn"
              @click="handleRegister"
              :loading="loading"
            >
              注册
            </el-button>
          </el-form-item>

          <div class="form-footer">
            <span>已有账户？</span>
            <router-link to="/login" class="register-link">立即登录</router-link>
          </div>
        </el-form>
      </div>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules, FormItemRule } from 'element-plus'
import type { Router } from 'vue-router'
import type { RegisterForm } from '@/types'
import { register, getCaptcha } from '@/api/login'

const router: Router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref<boolean>(false)
const captchaImage = ref<string>('')
const captchaUuid = ref<string>('')

const form = reactive<RegisterForm>({
  username: '',
  password: '',
  confirmPassword: '',
  code: ''
})

const rules: FormRules<RegisterForm> = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule: any, value: any, callback: any) => {
        if (value !== form.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
}

const refreshCaptcha = async (): Promise<void> => {
  try {
    const response: any = await getCaptcha()
    if (response.code === 200) {
      captchaImage.value = 'data:image/png;base64,' + response.data.image
      captchaUuid.value = response.data.uuid
    }
  } catch (error) {
    console.error('获取验证码失败:', error)
  }
}

const handleRegister = async (): Promise<void> => {
  try {
    await formRef.value?.validate()
    loading.value = true

    const res: any = await register(form.username, form.password, form.code, captchaUuid.value)
    
    if (res.code === 200) {
      ElMessage.success('注册成功，即将跳转到登录页面')
      setTimeout(() => {
        router.push('/login')
      }, 1500)
    } else {
      refreshCaptcha()
    }
  } catch (error) {
    console.error('注册错误:', error)
    refreshCaptcha()
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  refreshCaptcha()
})
</script>

<style scoped>
.login-page {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background-color: #1e3963;
}

.left-section {
  height: 100vh;
  overflow: hidden;
}

.login-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.right-section {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f9fafb;
}

.login-form-wrapper {
  width: 100%;
  max-width: 360px;
  padding: 2rem;
}

.form-title {
  font-size: 1.8rem;
  margin-bottom: 2rem;
  text-align: center;
  color: #1d2129;
}

.login-form {
  background: #fff;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.captcha-wrapper {
  display: flex;
  gap: 10px;
  width: 100%;
}

.captcha-input {
  flex: 1;
}

.captcha-image {
  width: 120px;
  height: 40px;
  cursor: pointer;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
}

.captcha-image:hover {
  opacity: 0.8;
}

.login-btn {
  width: 100%;
  height: 45px;
  margin-bottom: 1rem;
}

.form-footer {
  text-align: center;
  margin-top: 1rem;
  font-size: 14px;
  color: #666;
}

.register-link {
  color: #1890ff;
  margin-left: 5px;
  text-decoration: none;
}

.register-link:hover {
  text-decoration: underline;
}
</style>
