import request from '@/utils/request'
import { API_PATHS } from '@/constants'

export function getNameData() {
  return request({
    url: API_PATHS.HOME,
    method: 'get'
  })
}

export function updateNameData(data) {
  return request({
    url: `${API_PATHS.HOME}/${data.id}`,
    method: 'put',
    data
  })
}
