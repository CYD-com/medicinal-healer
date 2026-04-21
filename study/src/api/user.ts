import request from '@/utils/request'

export function getUserList() {
  return request({
    url: '/api/user/list',
    method: 'get'
  })
}

export function addUser(username: string, password: string, role: string) {
  return request({
    url: '/api/user/add',
    method: 'post',
    data: { username, password, role }
  })
}

export function updateUser(id: string, role: string) {
  return request({
    url: `/api/user/update/${id}`,
    method: 'put',
    data: { id, role }
  })
}

export function deleteUser(id: string) {
  return request({
    url: `/api/user/delete/${id}`,
    method: 'delete'
  })
}

export function updateUserRole(id: string, role: string) {
  return request({
    url: '/api/user/role',
    method: 'put',
    data: { id, role }
  })
}

export function uploadAvatar(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/api/user/avatar',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function updateAvatar(avatar: string) {
  return request({
    url: '/api/user/avatar',
    method: 'put',
    data: { avatar }
  })
}

export function getUserInfo() {
  return request({
    url: '/api/user/info',
    method: 'get'
  })
}