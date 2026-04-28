<template>
  <div class="common-layout">
    <el-container>
      <el-aside width="200px">
        <el-scrollbar class="sidebar-scrollbar">
          <template v-if="userStore.isAdmin">
            <router-link to="/my" class="sidebar-item" active-class="active">
              我的
            </router-link>
            <router-link to="/users" class="sidebar-item" active-class="active">
              用户管理
            </router-link>
          </template>
          <template v-else>
            <router-link to="/home" class="sidebar-item" active-class="active">
              商品管理
            </router-link>
            <router-link to="/about" class="sidebar-item" active-class="active">
              关于
            </router-link>
            <router-link to="/my" class="sidebar-item" active-class="active">
              我的
            </router-link>
          </template>
          <router-link to="/drugs" class="sidebar-item" active-class="active">
            药品管理
          </router-link>
          <router-link to="/appointments" class="sidebar-item" active-class="active">
            预约挂号
          </router-link>
        </el-scrollbar>
      </el-aside>
      <el-container>
        <el-header>
          <div class="header-left">
            <span class="header-title">管理系统</span>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-icon><User /></el-icon>
                {{ userStore.username }}
                <el-tag v-if="userStore.isAdmin" type="danger" size="small" style="margin-left: 8px">管理员</el-tag>
                <el-tag v-else type="success" size="small" style="margin-left: 8px">用户</el-tag>
              </span>
              <template #dropdown>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        <el-main style="background: #fff;">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { User } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import type { Router } from 'vue-router'

const router: Router = useRouter()
const userStore = useUserStore()

const handleCommand = async (command: string): Promise<void> => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await userStore.logout()
      router.push('/login')
    } catch (error) {
      console.log('取消退出')
    }
  } else if (command === 'profile') {
    router.push('/my')
  }
}
</script>

<style scoped>
.common-layout {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  background-color: #1e3963;
}

.el-container {
  height: 100%;
}

:deep(.el-aside) {
  background-color: #2c3e50;
  transition: width 0.3s ease;
  box-shadow: 2px 0 6px rgba(0, 0, 0, 0.1);
}

.sidebar-scrollbar {
  height: 100%;
}

.sidebar-item {
  display: block;
  padding: 12px 16px;
  color: #b0c4de;
  text-decoration: none;
  border-left: 4px solid transparent;
  transition: all 0.3s ease;
}

.sidebar-item:hover {
  background-color: #34495e;
  color: #ffffff;
  border-left: 4px solid #409eff;
}

.active {
  background-color: #2980b9;
  color: #ffffff;
  border-left: 4px solid #409eff;
}

:deep(.el-header) {
  background-color: #d50606;
  border-bottom: 1px solid #eaecef;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px !important;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.header-left {
  display: flex;
  align-items: center;
}

.header-title {
  font-size: 20px;
  font-weight: bold;
  color: #ffffff;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #ffffff;
  font-size: 14px;
}

.user-info:hover {
  opacity: 0.8;
}
</style>
