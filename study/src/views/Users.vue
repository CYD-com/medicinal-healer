<template>
  <div class="users-page">
    <div class="table-header">
      <h3>用户列表</h3>
      <div class="header-right">
        <el-input
          v-model="searchKeyword"
          placeholder="请输入用户名搜索"
          clearable
          style="width: 200px; margin-right: 10px;"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button type="primary" @click="handleAdd">添加用户</el-button>
      </div>
    </div>

    <el-table :data="tableData" border stripe>
      <el-table-column prop="id" label="序号" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="role" label="角色">
        <template #default="scope">
          <el-tag v-if="scope.row.role === 'admin'" type="danger">管理员</el-tag>
          <el-tag v-else type="success">用户</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="180">
        <template #default="scope">
          <el-button type="text" @click="handleToggleRole(scope.row)" :disabled="scope.row.username === userStore.username">
            {{ scope.row.role === 'admin' ? '设为用户' : '设为管理员' }}
          </el-button>
          <el-button type="text" danger @click="handleDelete(scope.row.id)" :disabled="scope.row.username === userStore.username">删除</el-button>
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

  <el-dialog v-model="dialogVisible" title="添加用户">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="form.password" type="password" />
      </el-form-item>
      <el-form-item label="角色" prop="role">
        <el-select v-model="form.role" placeholder="请选择角色">
          <el-option label="用户" value="user" />
          <el-option label="管理员" value="admin" />
        </el-select>
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
import { ElMessage, ElMessageBox } from "element-plus"
import type { FormInstance, FormRules } from "element-plus"
import { useUserStore } from '@/stores/user'
import type { UserForm, UserItem } from '@/types'
import { getUserList, addUser, deleteUser, updateUserRole } from '@/api/user'

const userStore = useUserStore()
const tableData = ref<UserItem[]>([])
const allTableData = ref<UserItem[]>([])
const page = ref<number>(1)
const size = ref<number>(10)
const total = ref<number>(0)
const dialogVisible = ref<boolean>(false)
const searchKeyword = ref<string>("")
const formRef = ref<FormInstance>()
const form = ref<UserForm>({
  username: "",
  password: "",
  role: "user",
})
const rules = ref<FormRules<UserForm>>({
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
  role: [{ required: true, message: "请选择角色", trigger: "change" }],
})

const getList = async (): Promise<void> => {
  try {
    const response: any = await getUserList()
    if (response.code === 200) {
      allTableData.value = response.data || []
      filterData()
    } else {
      ElMessage.error(response.msg || '获取用户列表失败')
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败')
    console.error('获取用户列表失败:', error)
  }
}

const filterData = (): void => {
  let filteredData = allTableData.value
  
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.trim().toLowerCase()
    filteredData = filteredData.filter(user => 
      user.username.toLowerCase().includes(keyword)
    )
  }
  
  total.value = filteredData.length
  
  // 分页
  const start = (page.value - 1) * size.value
  const end = start + size.value
  tableData.value = filteredData.slice(start, end)
}

const handleSearch = (): void => {
  page.value = 1
  filterData()
}

const handleReset = (): void => {
  searchKeyword.value = ""
  page.value = 1
  filterData()
}

const handleAdd = (): void => {
  form.value = {
    username: "",
    password: "",
    role: "user",
  }
  dialogVisible.value = true
}

const handleSubmit = async (): Promise<void> => {
  try {
    await formRef.value?.validate()
    
    const response: any = await addUser(form.value.username, form.value.password, form.value.role)
    if (response.code === 200) {
      ElMessage.success('添加成功')
    } else {
      ElMessage.error(response.msg || '添加失败')
    }
    
    dialogVisible.value = false
    getList()
  } catch (error) {
    ElMessage.error("操作失败")
  }
}

const handleCancel = (): void => {
  formRef.value?.resetFields()
  dialogVisible.value = false
}

const handleToggleRole = async (row: UserItem): Promise<void> => {
  try {
    const newRole = row.role === 'admin' ? 'user' : 'admin'
    const action = newRole === 'admin' ? '设为管理员' : '设为用户'
    await ElMessageBox.confirm(`确定要将用户 ${row.username} ${action}吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response: any = await updateUserRole(row.id, newRole)
    if (response.code === 200) {
      ElMessage.success("角色修改成功")
      getList()
    } else {
      ElMessage.error(response.msg || '角色修改失败')
    }
  } catch (error) {
    console.log('取消操作')
  }
}

const handleDelete = async (id: string): Promise<void> => {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    const response: any = await deleteUser(id)
    if (response.code === 200) {
      ElMessage.success("删除成功")
      getList()
    } else {
      ElMessage.error(response.msg || '删除失败')
    }
  } catch (error) {
    console.log('取消删除')
  }
}

onMounted(() => {
  getList()
})
</script>

<style scoped>
.users-page {
  padding: 20px;
}
.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.header-right {
  display: flex;
  align-items: center;
}
</style>
