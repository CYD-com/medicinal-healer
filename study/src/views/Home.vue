<template>
  <div class="home-page">
    <div class="table-header">
      <h3>记录列表</h3>
      <el-button type="primary" @click="handleAdd">添加记录</el-button>
      <el-button type="primary" @click="handleInit">初始化测试数据</el-button>
    </div>

    <div class="search-form">
      <el-form :model="searchForm" inline>
        <el-form-item label="分类">
          <el-input v-model="searchForm.category" placeholder="请输入分类" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-input v-model="searchForm.type" placeholder="请输入类型" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-input v-model="searchForm.status" placeholder="请输入状态" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="tableData" border stripe>
      <el-table-column type="index" label="序号" width="80" :index="indexMethod" />
      <el-table-column prop="category" label="分类" />
      <el-table-column prop="type" label="类型" />
      <el-table-column prop="status" label="状态" />
      <el-table-column prop="amount" label="金额" />
      <el-table-column prop="time" label="时间" width="180" />
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-button type="text" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="text" danger @click="handleDelete(scope.row.id)"
            >删除</el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="page"
      v-model:page-size="size"
      :total="total"
      layout="total, prev, pager, next, jumper"
      @size-change="getList"
      @current-change="getList"
      style="margin-top: 20px; text-align: right"
    />
  </div>

  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑记录' : '添加记录'">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="分类" prop="category">
        <el-input v-model="form.category" />
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-input v-model="form.type" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-input v-model="form.status" />
      </el-form-item>
      <el-form-item label="金额" prop="amount">
        <el-input v-model.number="form.amount" />
      </el-form-item>
      <el-form-item label="时间" prop="time">
        <el-date-picker
          v-model="form.time"
          type="date"
          value-format="YYYY-MM-DD"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确认</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue"
import { ElMessage } from "element-plus"
import type { FormInstance, FormRules } from "element-plus"
import type { RecordItem, RecordForm, SearchForm } from "@/types"
import {
  getRecordList,
  initRecordData,
  addRecord,
  deleteRecord,
  updateRecord,
} from "@/api/record"

const tableData = ref<RecordItem[]>([])
const page = ref<number>(1)
const size = ref<number>(10)
const total = ref<number>(0)
const dialogVisible = ref<boolean>(false)
const isEdit = ref<boolean>(false)
const editId = ref<number | null>(null)
const formRef = ref<FormInstance>()
const form = ref<RecordForm>({
  category: "",
  type: "",
  status: "",
  amount: 0,
  time: "",
})
const searchForm = ref<SearchForm>({
  category: "",
  type: "",
  status: "",
})
const rules = ref<FormRules<RecordForm>>({
  category: [{ required: true, message: "请输入分类", trigger: "blur" }],
  type: [{ required: true, message: "请输入类型", trigger: "blur" }],
  status: [{ required: true, message: "请输入状态", trigger: "blur" }],
  amount: [{ required: true, message: "请输入金额", trigger: "blur" }],
  time: [{ required: true, message: "请输入时间", trigger: "blur" }],
})

// 计算序号
const indexMethod = (index: number): number => {
  return (page.value - 1) * size.value + index + 1
}

// 获取列表
const getList = async (): Promise<void> => {
  const params = {
    page: page.value,
    size: size.value,
    ...searchForm.value,
  }
  const res: any = await getRecordList(params)
  if (res.code === 200) {
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  }
}

// 搜索
const handleSearch = (): void => {
  page.value = 1
  getList()
}

// 重置搜索
const handleReset = (): void => {
  searchForm.value = {
    category: "",
    type: "",
    status: "",
  }
  page.value = 1
  getList()
}

// 初始化数据
const handleInit = async (): Promise<void> => {
  const res: any = await initRecordData()
  ElMessage.success(res.msg)
  getList()
}

const handleAdd = (): void => {
  isEdit.value = false
  editId.value = null
  form.value = {
    category: "",
    type: "",
    status: "",
    amount: 0,
    time: "",
  }
  dialogVisible.value = true
}

const handleSubmit = async (): Promise<void> => {
  try {
    await formRef.value?.validate()
    if (isEdit.value && editId.value) {
      await updateRecord(String(editId.value), form.value)
      ElMessage.success("编辑成功")
    } else {
      await addRecord(form.value)
      ElMessage.success("添加成功")
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(error instanceof Error ? error.message : "操作失败")
    }
  }
}

const handleCancel = (): void => {
  formRef.value?.resetFields()
}

// 编辑
const handleEdit = (row: RecordItem): void => {
  isEdit.value = true
  editId.value = row.id
  form.value = {
    category: row.category,
    type: row.type,
    status: row.status,
    amount: row.amount,
    time: row.time,
  }
  dialogVisible.value = true
}

// 删除
const handleDelete = async (id: number): Promise<void> => {
  try {
    const res: any = await deleteRecord(id)
    ElMessage.success("删除成功")
    getList()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : "删除失败")
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.home-page {
  padding: 20px;
}
.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.search-form {
  background: #f5f7fa;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}
</style>
