import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { login as loginApi, logout as logoutApi, register as registerApi } from '@/api/login'

interface UserInfo {
  id?: string
  username?: string
  role?: string
  [key: string]: any
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || sessionStorage.getItem('token') || '')
  const username = ref<string>(localStorage.getItem('username') || sessionStorage.getItem('username') || '')
  const role = ref<string>(localStorage.getItem('role') || sessionStorage.getItem('role') || '')
  const userInfo = ref<UserInfo | null>(null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 'admin')
  const isDoctor = computed(() => role.value === 'doctor')
  const user = computed(() => ({
    id: userInfo.value?.id,
    username: username.value,
    role: role.value
  }))

  const setToken = (newToken: string, remember: boolean = false) => {
    token.value = newToken
    if (remember) {
      localStorage.setItem('token', newToken)
    } else {
      sessionStorage.setItem('token', newToken)
    }
  }

  const setUsername = (newUsername: string, remember: boolean = false) => {
    username.value = newUsername
    if (remember) {
      localStorage.setItem('username', newUsername)
    } else {
      sessionStorage.setItem('username', newUsername)
    }
  }

  const setRole = (newRole: string, remember: boolean = false) => {
    role.value = newRole
    if (remember) {
      localStorage.setItem('role', newRole)
    } else {
      sessionStorage.setItem('role', newRole)
    }
  }

  const setUserInfo = (info: UserInfo | null) => {
    userInfo.value = info
  }

  const clearUser = () => {
    token.value = ''
    username.value = ''
    role.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
    localStorage.removeItem('userInfo')
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('username')
    sessionStorage.removeItem('role')
    sessionStorage.removeItem('userInfo')
  }

  const login = async (username: string, password: string, remember: boolean = false, loginRole?: string): Promise<boolean> => {
    try {
      const response: any = await loginApi(username, password, loginRole)

      if (response.code === 200) {
        setToken(response.data.token, remember)
        setUsername(username, remember)
        setRole(response.data.role || 'user', remember)
        ElMessage.success(response.msg || '登录成功')
        return true
      } else {
        ElMessage.error(response.msg || '登录失败')
        return false
      }
    } catch (error: any) {
      ElMessage.error(error.message || '登录失败')
      return false
    }
  }

  const logout = async (): Promise<void> => {
    try {
      const response: any = await logoutApi()
      ElMessage.success(response?.msg || '退出登录成功')
    } catch (error: any) {
      console.error('退出登录失败:', error)
    } finally {
      clearUser()
    }
  }

  const register = async (username: string, password: string, code?: string, uuid?: string): Promise<boolean> => {
    try {
      const response: any = await registerApi(username, password, code || '', uuid || '')

      if (response.code === 200) {
        ElMessage.success(response.msg || '注册成功')
        return true
      } else {
        ElMessage.error(response.msg || '注册失败')
        return false
      }
    } catch (error: any) {
      ElMessage.error(error.message || '注册失败')
      return false
    }
  }

  return {
    token,
    username,
    role,
    userInfo,
    isLoggedIn,
    isAdmin,
    isDoctor,
    user,
    setToken,
    setUsername,
    setRole,
    setUserInfo,
    clearUser,
    login,
    logout,
    register
  }
})
