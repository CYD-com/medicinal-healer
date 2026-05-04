import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const BASE_URL = ''

export function useRequest() {
  const loading = ref(false)
  const error = ref<any>(null)

  const request = async <T>(
    url: string,
    options: RequestInit = {}
  ): Promise<T | null> => {
    loading.value = true
    error.value = null

    try {
      const token = localStorage.getItem('token') || sessionStorage.getItem('token')
      const headers: Record<string, string> = {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        ...(token ? { 'Authorization': `Bearer ${token}`, 'X-Token': token } : {}),
        ...(options.headers as Record<string, string> || {})
      }

      const response = await fetch(`${BASE_URL}${url}`, {
        ...options,
        headers,
        credentials: 'include'
      })

      if (response.status === 401) {
        localStorage.removeItem('token')
        sessionStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        sessionStorage.removeItem('userInfo')
        window.location.href = '/login'
        return null
      }

      if (response.status === 403) {
        ElMessage.error('没有权限访问')
        return null
      }

      if (response.status >= 500) {
        throw new Error('服务器错误，请稍后重试')
      }

      const data = await response.json()

      if (response.status !== 200) {
        throw new Error(data.msg || data.message || '请求失败')
      }

      return data as T
    } catch (err: any) {
      error.value = err
      ElMessage.error(err.message || '请求失败，请稍后重试')
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    error,
    request
  }
}
