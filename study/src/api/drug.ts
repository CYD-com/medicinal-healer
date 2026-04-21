import request from '@/utils/request'

interface DrugQuery {
  drugName?: string
  category?: string
  type?: string
  page: number
  size: number
}

export function getDrugList(query: DrugQuery) {
  return request({
    url: '/api/drug/list',
    method: 'post',
    data: query
  })
}

export function getDrugDetail(id: string) {
  return request({
    url: `/api/drug/detail/${id}`,
    method: 'get'
  })
}

export function getInventorySummary() {
  return request({
    url: '/api/drug/inventory/summary',
    method: 'get'
  })
}

export function getInventoryList(page: number, size: number) {
  return request({
    url: '/api/drug/inventory/list',
    method: 'get',
    params: { page, size }
  })
}

export function getDrugCategories() {
  return request({
    url: '/api/drug/categories',
    method: 'get'
  })
}
