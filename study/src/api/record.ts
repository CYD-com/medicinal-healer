import request from '@/utils/request'
import type { RecordForm, RecordItem } from '@/types'

interface RecordParams {
  id?: string | number
  category?: string
  type?: string
  status?: string
  page?: number
  size?: number
  [key: string]: any
}

// 获取表格数据
export const getRecordList = (params: RecordParams) => {
  const filteredParams: RecordParams = {}
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      filteredParams[key] = value
    }
  })
  return request.get("/api/record/list", { params: filteredParams })
}

// 初始化测试数据
export const initRecordData = () => {
  return request.get("/api/record/init")
}

//添加数据
export const addRecord = (data: RecordForm) => {
  return request.post("/api/record/add", data)
}

//删除数据
export const deleteRecord = (id: string | number) => {
  return request.delete(`/api/record/delete/${id}`)
}

//更新数据
export const updateRecord = (id: string | number, data: RecordForm) => {
  return request.put(`/api/record/update/${id}`, data)
}