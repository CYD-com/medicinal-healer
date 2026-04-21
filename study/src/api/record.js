import request from '@/utils/request'

// 获取表格数据
export const getRecordList = (params) => {
  const filteredParams = {};
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      filteredParams[key] = value;
    }
  });
  return request.get("/api/record/list", { params: filteredParams });
};

// 初始化测试数据
export const initRecordData = () => {
  return request.get("/api/record/init");
};

//添加数据
export const addRecord = (data) => {
  return request.post("/api/record/add", data);
};

//删除数据
export const deleteRecord = (id) => {
  return request.delete(`/api/record/delete/${id}`);
};

//更新数据
export const updateRecord = (id, data) => {
  return request.put(`/api/record/update/${id}`, data);
};