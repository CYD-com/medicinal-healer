<template>
  <div class="layout-container">
    <aside class="sidebar">
      <div class="sidebar-header">
        <h3>社区医疗服务系统</h3>
      </div>
      <nav class="sidebar-nav">
        <template v-if="isAdmin">
          <router-link to="/admin/statistics" class="sidebar-item" active-class="active">
            数据统计
          </router-link>
          <router-link to="/admin/departments" class="sidebar-item" active-class="active">
            科室管理
          </router-link>
          <router-link to="/users" class="sidebar-item" active-class="active">
            用户管理
          </router-link>
          <router-link to="/drugs" class="sidebar-item" active-class="active">
            药品管理
          </router-link>
          <router-link to="/admin/approvals" class="sidebar-item" active-class="active">
            审批管理
          </router-link>
          <router-link to="/my" class="sidebar-item" active-class="active">
            个人中心
          </router-link>
        </template>
        <template v-else-if="isDoctor">
          <router-link to="/doctor/schedule" class="sidebar-item" active-class="active">
            今日排班
          </router-link>
          <router-link to="/doctor/consultations" class="sidebar-item" active-class="active">
            处理问诊
          </router-link>
          <router-link to="/doctor/prescriptions" class="sidebar-item" active-class="active">
            开具处方
          </router-link>
          <router-link to="/my" class="sidebar-item" active-class="active">
            个人中心
          </router-link>
        </template>
        <template v-else>
          <router-link to="/dashboard" class="sidebar-item" active-class="active">
            工作台
          </router-link>
          <router-link to="/appointments" class="sidebar-item" active-class="active">
            预约挂号
          </router-link>
          <router-link to="/ai-chat" class="sidebar-item" active-class="active">
            AI问诊
          </router-link>
          <router-link to="/consultations" class="sidebar-item" active-class="active">
            在线问诊
          </router-link>
          <router-link to="/prescriptions" class="sidebar-item" active-class="active">
            我的处方
          </router-link>
          <router-link to="/healthRecords" class="sidebar-item" active-class="active">
            健康档案
          </router-link>
          <router-link to="/my" class="sidebar-item" active-class="active">
            个人中心
          </router-link>
        </template>
      </nav>
    </aside>

    <main class="main-content">
      <div class="header-bar">
        <div class="page-title">
          <span v-if="route.meta.title" class="title-text">{{ route.meta.title }}</span>
        </div>
        <div class="user-info" v-if="userStore.token">
          <el-tag :type="roleTagType" size="small" style="margin-right: 8px">{{ roleLabel }}</el-tag>
          <div class="user-wrapper">
            <div class="user-avatar">
              <el-icon :size="20"><UserFilled /></el-icon>
            </div>
            <span class="username">{{ userStore.user?.username }}</span>
          </div>
          <el-dropdown @command="handleCommand">
            <el-button type="primary" link class="logout-btn">
              退出
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <div class="content-area">
        <router-view></router-view>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { UserFilled, ArrowDown } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isAdmin = computed(() => userStore.user?.role === 'admin')
const isDoctor = computed(() => userStore.user?.role === 'doctor')

const roleLabel = computed(() => {
  if (isAdmin.value) return '管理员'
  if (isDoctor.value) return '医生'
  return '患者'
})

const roleTagType = computed(() => {
  if (isAdmin.value) return 'danger'
  if (isDoctor.value) return 'success'
  return 'primary'
})

const handleCommand = (command: string) => {
  if (command === 'profile') {
    router.push('/my')
  } else if (command === 'logout') {
    userStore.clearUser()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  min-height: 100vh;
  background: #f5f5f5;
}

.sidebar {
  width: 220px;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-header h3 {
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  text-align: center;
  letter-spacing: 1px;
}

.sidebar-nav {
  padding: 12px 0;
  flex: 1;
}

.sidebar-item {
  display: block;
  padding: 12px 24px;
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s;
  position: relative;
  margin: 2px 0;
}

.sidebar-item:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
}

.sidebar-item.active {
  color: #409eff;
  background: rgba(64, 158, 255, 0.15);
}

.sidebar-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 60%;
  background: #409eff;
  border-radius: 0 2px 2px 0;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  height: 56px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 0;
  z-index: 100;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.title-text {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.username {
  font-size: 14px;
  color: #333;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.logout-btn {
  display: flex;
  align-items: center;
  font-size: 13px;
}

.content-area {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

@media (max-width: 768px) {
  .sidebar {
    width: 100%;
    height: auto;
  }
  .layout-container {
    flex-direction: column;
  }
}
</style>
