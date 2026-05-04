import request from '@/utils/request'
import { API_PATHS } from '@/constants'

export function login(username: string, password: string, loginRole?: string) {
  return request({
    url: API_PATHS.LOGIN,
    method: 'post',
    data: { username, password, loginRole }
  })
}

export function register(username: string, password: string, code: string, uuid: string) {
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
