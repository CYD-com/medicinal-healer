import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, logout as logoutApi, register as registerApi } from '@/api/login'
import { useRequest } from '@/composables/useRequest'

interface UserInfo {
  id?: string
  username?: string
  role?: string
  [key: string]: any
}

interface LoginResponse {
  code: number
  msg: string
  data: {
    token: string
    role: string
  }
}

interface RegisterResponse {
  code: number
  msg: string
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || sessionStorage.getItem('token') || '')
  const username = ref<string>(localStorage.getItem('username') || sessionStorage.getItem('username') || '')
  const role = ref<string>(localStorage.getItem('role') || sessionStorage.getItem('role') || '')
  const userInfo = ref<UserInfo | null>(null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 'admin')

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

  const login = async (username: string, password: string, remember: boolean = false): Promise<boolean> => {
    const { showLoading, hideLoading, showError, showSuccess } = useRequest()
    
    try {
      showLoading('登录中...')
      const response: any = await loginApi(username, password)
      
      if (response.code === 200) {
        setToken(response.data.token, remember)
        setUsername(username, remember)
        setRole(response.data.role || 'user', remember)
        showSuccess(response.msg || '登录成功')
        return true
      } else {
        showError(response.msg || '登录失败')
        return false
      }
    } catch (error: any) {
      showError(error.message || '登录失败')
      return false
    } finally {
      hideLoading()
    }
  }

  const logout = async (): Promise<void> => {
    const { showSuccess, showError } = useRequest()
    
    try {
      const response: any = await logoutApi()
      showSuccess(response?.msg || '退出登录成功')
    } catch (error: any) {
      console.error('退出登录失败:', error)
      showError('退出登录失败')
    } finally {
      token.value = ''
      username.value = ''
      role.value = ''
      userInfo.value = null
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      sessionStorage.removeItem('token')
      sessionStorage.removeItem('username')
      sessionStorage.removeItem('role')
    }
  }

  const register = async (username: string, password: string, code?: string, uuid?: string): Promise<boolean> => {
    const { showLoading, hideLoading, showError, showSuccess } = useRequest()
    
    try {
      showLoading('注册中...')
      const response: any = await registerApi(username, password, code || '', uuid || '')
      
      if (response.code === 200) {
        showSuccess(response.msg || '注册成功')
        return true
      } else {
        showError(response.msg || '注册失败')
        return false
      }
    } catch (error: any) {
      showError(error.message || '注册失败')
      return false
    } finally {
      hideLoading()
    }
  }

  return {
    token,
    username,
    role,
    userInfo,
    isLoggedIn,
    isAdmin,
    setToken,
    setUsername,
    setRole,
    setUserInfo,
    login,
    logout,
    register
  }
})
