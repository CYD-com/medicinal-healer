export const API_BASE_URL = import.meta.env.VITE_APP_API_BASE_URL || 'http://localhost:8080'

// API 路径常量
export const API_PATHS = {
  LOGIN: '/api/user/login',
  HOME: '/home',
  USER_INFO: '/user/info'
}

// HTTP 状态码常量
export const HTTP_STATUS = {
  SUCCESS: 200,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  SERVER_ERROR: 500
}

// 响应状态码常量
export const RESPONSE_CODE = {
  SUCCESS: 200,
  ERROR: 500
}
