<template>
  <div class="admin-departments">
    <div class="page-header">
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        新增科室
      </el-button>
    </div>

    <el-table :data="departments" stripe border style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="科室名称" />
      <el-table-column prop="description" label="科室描述" />
      <el-table-column prop="doctorsCount" label="医生数量" width="100" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="showEditDialog(row)">编辑</el-button>
          <el-popconfirm title="确定删除该科室？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button type="danger" link>删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑科室' : '新增科室'"
      width="500px"
    >
      <el-form :model="form" label-width="80px" ref="formRef" :rules="rules">
        <el-form-item label="科室名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入科室名称" />
        </el-form-item>
        <el-form-item label="科室描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入科室描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, type FormInstance } from 'element-plus'
import { getDepartmentList, createDepartment, updateDepartment, deleteDepartment, type DepartmentForm } from '@/api/admin'

const departments = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()

const form = ref<DepartmentForm>({
  name: '',
  description: ''
})

const rules = {
  name: [{ required: true, message: '请输入科室名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入科室描述', trigger: 'blur' }]
}

const fetchDepartments = async () => {
  loading.value = true
  try {
    const res: any = await getDepartmentList()
    if (res.code === 200) {
      departments.value = res.data
    }
  } catch (e) {
    console.error('获取科室列表失败', e)
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => {
  isEdit.value = false
  form.value = { name: '', description: '' }
  dialogVisible.value = true
}

const showEditDialog = (row: any) => {
  isEdit.value = true
  form.value = { id: row.id, name: row.name, description: row.description }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateDepartment(form.value)
      ElMessage.success('更新成功')
    } else {
      await createDepartment(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchDepartments()
  } catch (e: any) {
    ElMessage.error(e.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await deleteDepartment(id)
    ElMessage.success('删除成功')
    fetchDepartments()
  } catch (e: any) {
    ElMessage.error(e.message || '删除失败')
  }
}

onMounted(fetchDepartments)
</script>

<style scoped>
.admin-departments {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}
</style>
