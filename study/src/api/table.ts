import request from '@/utils/request'
import { API_PATHS } from '@/constants'

interface NameData {
  id: string
  name: string
  [key: string]: any
}

export function getNameData() {
  return request({
    url: API_PATHS.HOME,
    method: 'get'
  })
}

export function updateNameData(data: NameData) {
  return request({
    url: `${API_PATHS.HOME}/${data.id}`,
    method: 'put',
    data
  })
}