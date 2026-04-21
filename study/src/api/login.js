import request from '@/utils/request'
import { API_PATHS } from '@/constants'

export function login(username, password) {
  return request({
    url: API_PATHS.LOGIN,
    method: 'post',
    data: { username, password }
  })
}

export function register(username, password, code, uuid) {
  return request({
    url: '/api/user/register',
    method: 'post',
    data: { username, password, code, uuid }
  })
}

export function logout() {
  return request({
    url: '/api/user/logout',
    method: 'post'
  })
}

export function getCaptcha() {
  return request({
    url: '/api/captcha/generate',
    method: 'get'
  })
}
