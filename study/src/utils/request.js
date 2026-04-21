import axios from 'axios'
import { API_BASE_URL } from '@/constants'
import router from '@/router'

const service = axios.create({
  baseURL: API_BASE_URL,
  timeout: 5000
})

service.interceptors.request.use(
  (config) => {
    // 登录和注册请求不添加 token 和 role
    if (config.url && (config.url.includes('/login') || config.url.includes('/register'))) {
      return config
    }
    
    const token = localStorage.getItem('token') || sessionStorage.getItem('token')
    const role = localStorage.getItem('role') || sessionStorage.getItem('role')
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
      config.headers['X-Token'] = token
    }
    
    if (role) {
      config.headers['X-User-Role'] = role
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    if (error.response) {
      const status = error.response.status
      const message = error.response.data?.msg || error.message
      
      if (status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('username')
        localStorage.removeItem('role')
        sessionStorage.removeItem('token')
        sessionStorage.removeItem('username')
        sessionStorage.removeItem('role')
        router.push('/login')
      }
      
      return Promise.reject({
        status: status,
        message: message,
        data: error.response.data
      })
    }
    return Promise.reject({
      status: 0,
      message: error.message || '网络错误'
    })
  }
)

export default service
