<template>
  <el-row class="login-page">
    <el-col :span="12" class="left-section">
      <img src="@/assets/nb.jpg" alt="登录页面背景图" class="login-image" />
    </el-col>

    <el-col :span="12" class="right-section">
      <div class="login-form-wrapper">
        <h2 class="form-title">账户登录</h2>

        <el-form :model="form" :rules="rules" ref="formRef" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="form.username"
              placeholder="请输入用户名（示例：user_I683il）"
              prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码（示例：qht5kcKJ）"
              prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              class="login-btn"
              @click="handleLogin"
              :loading="loading"
            >
              登录
            </el-button>
          </el-form-item>

          <div class="form-footer">
            <span>还没有账户？</span>
            <router-link to="/register" class="register-link">立即注册</router-link>
          </div>
        </el-form>
      </div>
    </el-col>
  </el-row>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import type { FormInstance, FormRules } from 'element-plus'
import type { Router } from 'vue-router'
import type { LoginForm } from '@/types'

const router: Router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref<boolean>(false)

const form = reactive<LoginForm & { remember: boolean }>({
  username: '',
  password: '',
  remember: false
})

const rules: FormRules<LoginForm> = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async (): Promise<void> => {
  try {
    await formRef.value?.validate()
    loading.value = true

    const success = await userStore.login(
      form.username, 
      form.password, 
      form.remember
    )
    
    if (success) {
      router.push('/home')
    }
  } catch (error) {
    console.error('登录错误:', error)
  } finally {
    loading.value = false
  }
}
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
