import request from '@/utils/request'

// 获取药品列表
export function getDrugList(query) {
  return request({
    url: '/api/drug/list',
    method: 'post',
    data: query
  })
}

// 获取药品详情
export function getDrugDetail(id) {
  return request({
    url: `/api/drug/detail/${id}`,
    method: 'get'
  })
}

// 获取库存概览
export function getInventorySummary() {
  return request({
    url: '/api/drug/inventory/summary',
    method: 'get'
  })
}

// 获取库存列表
export function getInventoryList(page, size) {
  return request({
    url: '/api/drug/inventory/list',
    method: 'get',
    params: { page, size }
  })
}

// 获取药品分类
export function getDrugCategories() {
  return request({
    url: '/api/drug/categories',
    method: 'get'
  })
}
