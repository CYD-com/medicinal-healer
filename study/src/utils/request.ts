import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import router from '@/router'

const request = axios.create({
  baseURL: '',
  timeout: 10000,
})

request.interceptors.request.use(
  (config) => {
    const userStore = useUserStore()
    const token = userStore.token
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
      config.headers['X-Token'] = token
    }
    return config
  },
  (error) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res && res.code !== undefined) {
      if (res.code !== 200) {
        ElMessage.error(res.msg || '请求失败')
        return Promise.reject(new Error(res.msg || '请求失败'))
      }
    }
    return res
  },
  (error) => {
    const status = error.response?.status
    const data = error.response?.data

    if (status === 401) {
      ElMessage.error(data?.msg || '登录已过期，请重新登录')
      const userStore = useUserStore()
      userStore.clearUser()
      router.push('/login')
      return Promise.reject(error)
    }

    if (status === 403) {
      ElMessage.error(data?.msg || '没有权限访问')
      return Promise.reject(error)
    }

    if (status >= 500) {
      ElMessage.error(data?.msg || '服务器异常，请稍后重试')
      return Promise.reject(error)
    }

    if (data?.msg) {
      ElMessage.error(data.msg)
    } else {
      ElMessage.error(error.message || '网络异常')
    }

    return Promise.reject(error)
  }
)

export default request
