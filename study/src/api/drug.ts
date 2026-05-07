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

export interface DrugSearchResult {
  id: string
  drugName: string
  specification: string
  unit: string
  price: number
  dosage?: string
  stock: number
}

export function searchDrugs(drugName?: string) {
  return request({
    url: '/api/drug/search',
    method: 'get',
    params: { drugName, size: 50 }
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

export function createDrug(data: any) {
  return request({
    url: '/api/drug/create',
    method: 'post',
    data
  })
}

export function deleteDrug(id: string) {
  return request({
    url: `/api/drug/delete/${id}`,
    method: 'delete'
  })
}

export function updateDrug(id: string, data: any) {
  return request({
    url: `/api/drug/update`,
    method: 'put',
    data: { id, ...data }
  })
}
