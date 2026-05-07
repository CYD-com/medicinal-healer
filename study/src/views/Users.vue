<template>
  <div class="users-container">
    <h2>用户管理</h2>
    <div class="users-card">
      <div class="toolbar">
        <el-input v-model="searchKeyword" placeholder="搜索用户名/姓名/手机号" prefix-icon="el-icon-search" style="width: 300px; margin-right: 16px" clearable @keyup.enter="loadUserList" @clear="loadUserList" />
        <el-button type="primary" @click="loadUserList">搜索</el-button>
        <el-button type="success" @click="showAddDialog">添加用户</el-button>
      </div>
      <el-table :data="userList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="username" label="用户名" width="120"></el-table-column>
        <el-table-column prop="realName" label="真实姓名" width="120">
          <template #default="scope">
            {{ scope.row.realName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="scope">
            {{ getGenderText(scope.row.gender) }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130">
          <template #default="scope">
            {{ scope.row.phone || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" width="180">
          <template #default="scope">
            {{ scope.row.email || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="role" label="角色" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'admin' ? 'danger' : 'success'">
              {{ scope.row.role === 'admin' ? '管理员' : scope.row.role === 'doctor' ? '医生' : '用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="scope">
            <el-button
              v-if="!isCurrentUser(scope.row)"
              :type="scope.row.status === 1 ? 'warning' : 'success'"
              size="small"
              @click="toggleStatus(scope.row)"
            >
              {{ scope.row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button type="primary" size="small" @click="editUser(scope.row)">编辑</el-button>
            <el-button v-if="!isCurrentUser(scope.row)" type="danger" size="small" @click="deleteUser(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <Pagination
        v-model:page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        @change="loadUserList"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="userForm" label-width="100px">
        <el-form-item label="用户名" required>
          <el-input v-model="userForm.username" placeholder="请输入用户名" :disabled="isEdit"></el-input>
        </el-form-item>
        <el-form-item label="密码" v-if="!isEdit" required>
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="userForm.realName" placeholder="请输入真实姓名"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="userForm.gender">
            <el-radio label="male">男</el-radio>
            <el-radio label="female">女</el-radio>
            <el-radio label="other">其他</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="userForm.age" :min="1" :max="150"></el-input-number>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="userForm.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="userForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="userForm.idCard" placeholder="请输入身份证号"></el-input>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="userForm.address" type="textarea" :rows="2" placeholder="请输入地址"></el-input>
        </el-form-item>
        <el-form-item label="角色" required>
          <el-select v-model="userForm.role" placeholder="请选择角色" :disabled="isEdit && userForm.id !== null && String(userForm.id) === String(userStore.user?.id)">
            <el-option label="用户" value="user"></el-option>
            <el-option label="医生" value="doctor"></el-option>
            <el-option label="管理员" value="admin"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="userForm.status" :active-value="1" :inactive-value="0"></el-switch>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, addUser, updateUser, deleteUser as deleteUserApi } from '@/api/user'
import { useUserStore } from '@/stores/user'
import Pagination from '@/components/Pagination.vue'

const userStore = useUserStore()

const isCurrentUser = (user: UserItem) => {
  return String(user.id) === String(userStore.user?.id)
}

interface UserItem {
  id: number
  username: string
  realName?: string
  gender?: string
  age?: number
  phone?: string
  email?: string
  idCard?: string
  address?: string
  avatar?: string
  role: string
  status?: number
  createTime?: string
  updateTime?: string
}

const userList = ref<UserItem[]>([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('添加用户')
const isEdit = ref(false)
const searchKeyword = ref('')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const userForm = ref({
  id: null as number | null,
  username: '',
  password: '',
  realName: '',
  gender: '',
  age: null as number | null,
  phone: '',
  email: '',
  idCard: '',
  address: '',
  role: 'user',
  status: 1
})

const loadUserList = async () => {
  loading.value = true
  try {
    const res: any = await getUserList({ page: currentPage.value, size: pageSize.value, keyword: searchKeyword.value })
    if (res.code === 200) {
      userList.value = res.data?.records || res.data || []
      total.value = res.data?.total || 0
    }
  } catch {
    // 错误提示已由全局拦截器处理
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => {
  isEdit.value = false
  dialogTitle.value = '添加用户'
  userForm.value = {
    id: null,
    username: '',
    password: '',
    realName: '',
    gender: '',
    age: null,
    phone: '',
    email: '',
    idCard: '',
    address: '',
    role: 'user',
    status: 1
  }
  dialogVisible.value = true
}

const editUser = (user: UserItem) => {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  userForm.value = {
    id: user.id,
    username: user.username,
    password: '',
    realName: user.realName || '',
    gender: user.gender || '',
    age: user.age || null,
    phone: user.phone || '',
    email: user.email || '',
    idCard: user.idCard || '',
    address: user.address || '',
    role: user.role,
    status: user.status ?? 1
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!userForm.value.username) {
    ElMessage.warning('请输入用户名')
    return
  }
  if (!isEdit.value && !userForm.value.password) {
    ElMessage.warning('请输入密码')
    return
  }

  try {
    if (isEdit.value) {
      const { id, realName, gender, age, phone, email, idCard, address, role, status } = userForm.value
      const updateData: any = { realName, gender, age, phone, email, idCard, address, role, status }
      if (isCurrentUser({ id: id! } as UserItem)) {
        delete updateData.role
      }
      await updateUser(String(id!), updateData)
      ElMessage.success('更新成功')
    } else {
      const { username, password, realName, gender, age, phone, email, idCard, address, role, status } = userForm.value
      await addUser({ username, password, realName, gender, age, phone, email, idCard, address, role, status })
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadUserList()
  } catch {
    // 错误提示已由全局拦截器处理
  }
}

const deleteUser = async (user: UserItem) => {
  if (isCurrentUser(user)) {
    ElMessage.warning('不能删除自己的账号')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要删除用户 "${user.username}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteUserApi(String(user.id))
    ElMessage.success('删除成功')
    loadUserList()
  } catch (error) {
    if (error === 'cancel') return
    // 错误提示已由全局拦截器处理
  }
}

const toggleStatus = async (user: UserItem) => {
  if (isCurrentUser(user)) {
    ElMessage.warning('不能禁用自己的账号')
    return
  }
  const newStatus = user.status === 1 ? 0 : 1
  const actionText = newStatus === 0 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(
      `确定要${actionText}用户 "${user.username}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    await updateUser(String(user.id), { status: newStatus })
    ElMessage.success(`${actionText}成功`)
    loadUserList()
  } catch (error) {
    if (error === 'cancel') return
    // 错误提示已由全局拦截器处理
  }
}

const getGenderText = (gender?: string) => {
  const map: Record<string, string> = {
    male: '男',
    female: '女',
    other: '其他'
  }
  return gender ? map[gender] || '-' : '-'
}

const formatDateTime = (dateTime?: string) => {
  if (!dateTime) return '-'
  return new Date(dateTime).toLocaleString('zh-CN')
}

onMounted(() => {
  loadUserList()
})
</script>

<style scoped>
.users-container {
  padding: 20px;
}

.users-container h2 {
  margin-bottom: 20px;
  color: #333;
}

.users-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 24px;
}

.toolbar {
  margin-bottom: 16px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

</style>

