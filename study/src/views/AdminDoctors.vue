<template>
  <div class="admin-doctors">
    <div class="page-header">
      <el-button type="primary" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        新增医生
      </el-button>
    </div>

    <el-table :data="doctors" stripe border style="width: 100%" v-loading="loading">
      <el-table-column prop="doctorId" label="ID" width="80" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="title" label="职称" />
      <el-table-column label="科室">
        <template #default="{ row }">
          {{ row.department?.name || '-' }}
        </template>
      </el-table-column>
      <el-table-column label="擅长">
        <template #default="{ row }">
          <el-tag v-for="s in (row.specialty || []).slice(0, 2)" :key="s" size="small" style="margin: 2px">
            {{ s }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="rating" label="评分" width="80" />
      <el-table-column prop="consultationCount" label="问诊数" width="90" />
      <el-table-column prop="userId" label="绑定用户ID" width="110">
        <template #default="{ row }">
          {{ row.userId || '未绑定' }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="showEditDialog(row)">编辑</el-button>
          <el-popconfirm title="确定删除该医生？" @confirm="handleDelete(row.doctorId)">
            <template #reference>
              <el-button type="danger" link>删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑医生' : '新增医生'"
      width="560px"
    >
      <el-form :model="form" label-width="100px" ref="formRef" :rules="rules">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入医生姓名" />
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-select v-model="form.title" placeholder="请选择职称">
            <el-option label="主任医师" value="主任医师" />
            <el-option label="副主任医师" value="副主任医师" />
            <el-option label="主治医师" value="主治医师" />
            <el-option label="住院医师" value="住院医师" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属科室" prop="departmentId">
          <el-select v-model="form.departmentId" placeholder="请选择科室">
            <el-option v-for="dept in departments" :key="dept.id" :label="dept.name" :value="dept.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="擅长方向">
          <el-input v-model="form.specialty" placeholder="多个方向用顿号分隔，如：心血管、高血压" />
        </el-form-item>
        <el-form-item label="绑定用户ID">
          <el-input v-model.number="form.userId" placeholder="关联的系统用户ID（可选）" />
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
import { getDoctorList, createDoctor, updateDoctor, deleteDoctor, getDepartmentList, type DoctorForm } from '@/api/admin'

const doctors = ref<any[]>([])
const departments = ref<any[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()

const form = ref<DoctorForm>({
  name: '',
  title: '',
  departmentId: 0,
  specialty: '',
  userId: undefined
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  title: [{ required: true, message: '请选择职称', trigger: 'change' }],
  departmentId: [{ required: true, message: '请选择科室', trigger: 'change' }]
}

const fetchDoctors = async () => {
  loading.value = true
  try {
    const res: any = await getDoctorList()
    if (res.code === 200) {
      doctors.value = res.data
    }
  } catch (e) {
    console.error('获取医生列表失败', e)
  } finally {
    loading.value = false
  }
}

const fetchDepartments = async () => {
  try {
    const res: any = await getDepartmentList()
    if (res.code === 200) {
      departments.value = res.data
    }
  } catch (e) {
    console.error('获取科室列表失败', e)
  }
}

const showAddDialog = () => {
  isEdit.value = false
  form.value = { name: '', title: '', departmentId: 0, specialty: '', userId: undefined }
  dialogVisible.value = true
}

const showEditDialog = (row: any) => {
  isEdit.value = true
  form.value = {
    id: row.doctorId,
    name: row.name,
    title: row.title,
    departmentId: row.department?.id || 0,
    specialty: (row.specialty || []).join('、'),
    userId: row.userId
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateDoctor(form.value)
      ElMessage.success('更新成功')
    } else {
      await createDoctor(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchDoctors()
  } catch (e: any) {
    ElMessage.error(e.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id: number) => {
  try {
    await deleteDoctor(id)
    ElMessage.success('删除成功')
    fetchDoctors()
  } catch (e: any) {
    ElMessage.error(e.message || '删除失败')
  }
}

onMounted(() => {
  fetchDoctors()
  fetchDepartments()
})
</script>

<style scoped>
.admin-doctors {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}
</style>
