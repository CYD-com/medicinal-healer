import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, logout as logoutApi, register as registerApi } from '@/api/login'
import { useRequest } from '@/composables/useRequest'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || sessionStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || sessionStorage.getItem('username') || '')
  const role = ref(localStorage.getItem('role') || sessionStorage.getItem('role') || '')
  const userInfo = ref(null)

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => role.value === 'admin')

  const setToken = (newToken, remember = false) => {
    token.value = newToken
    if (remember) {
      localStorage.setItem('token', newToken)
    } else {
      sessionStorage.setItem('token', newToken)
    }
  }

  const setUsername = (newUsername, remember = false) => {
    username.value = newUsername
    if (remember) {
      localStorage.setItem('username', newUsername)
    } else {
      sessionStorage.setItem('username', newUsername)
    }
  }

  const setRole = (newRole, remember = false) => {
    role.value = newRole
    if (remember) {
      localStorage.setItem('role', newRole)
    } else {
      sessionStorage.setItem('role', newRole)
    }
  }

  const setUserInfo = (info) => {
    userInfo.value = info
  }

  const login = async (username, password, remember = false) => {
    const { showLoading, hideLoading, showError, showSuccess } = useRequest()
    
    try {
      showLoading('登录中...')
      const response = await loginApi(username, password)
      
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
    } catch (error) {
      showError(error.message || '登录失败')
      return false
    } finally {
      hideLoading()
    }
  }

  const logout = async () => {
    const { showSuccess, showError } = useRequest()
    
    try {
      const response = await logoutApi()
      showSuccess(response?.msg || '退出登录成功')
    } catch (error) {
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

  const register = async (username, password) => {
    const { showLoading, hideLoading, showError, showSuccess } = useRequest()
    
    try {
      showLoading('注册中...')
      const response = await registerApi(username, password)
      
      if (response.code === 200) {
        showSuccess(response.msg || '注册成功')
        return true
      } else {
        showError(response.msg || '注册失败')
        return false
      }
    } catch (error) {
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
