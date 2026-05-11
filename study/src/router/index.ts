import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Layout from '@/views/Layout.vue'
import Dashboard from '@/views/Dashboard.vue'
import About from '@/views/About.vue'
import My from '@/views/My.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import Users from '@/views/Users.vue'
import Drugs from '@/views/drugs.vue'
import Appointments from '@/views/Appointments.vue'
import Consultations from '@/views/Consultations.vue'
import Prescriptions from '@/views/Prescriptions.vue'
import HealthRecords from '@/views/HealthRecords.vue'
import AdminStatistics from '@/views/AdminStatistics.vue'
import AdminDepartments from '@/views/AdminDepartments.vue'
import AdminDoctors from '@/views/AdminDoctors.vue'
import DoctorDashboard from '@/views/DoctorDashboard.vue'

import DoctorConsultations from '@/views/DoctorConsultations.vue'
import DoctorPrescriptions from '@/views/DoctorPrescriptions.vue'
import AIChat from '@/views/AIChat.vue'
import ApprovalManagement from '@/views/ApprovalManagement.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: '/dashboard',
        name: 'dashboard',
        component: Dashboard,
        meta: { title: '工作台', roles: ['user'] }
      },
      {
        path: '/about',
        name: 'about',
        component: About,
        meta: { title: '关于' }
      },
      {
        path: '/my',
        name: 'my',
        component: My,
        meta: { title: '个人中心' }
      },
      {
        path: '/drugs',
        name: 'drugs',
        component: Drugs,
        meta: { title: '药品管理', roles: ['admin'] }
      },
      {
        path: '/appointments',
        name: 'appointments',
        component: Appointments,
        meta: { title: '预约挂号', roles: ['user', 'admin'] }
      },
      {
        path: '/consultations',
        name: 'consultations',
        component: Consultations,
        meta: { title: '在线问诊', roles: ['user', 'admin'] }
      },
      {
        path: '/prescriptions',
        name: 'prescriptions',
        component: Prescriptions,
        meta: { title: '处方管理', roles: ['user', 'admin'] }
      },
      {
        path: '/healthRecords',
        name: 'healthRecords',
        component: HealthRecords,
        meta: { title: '健康档案', roles: ['user', 'admin'] }
      },
      {
        path: '/users',
        name: 'users',
        component: Users,
        meta: { title: '用户管理', roles: ['admin'] }
      },
      {
        path: '/admin/statistics',
        name: 'adminStatistics',
        component: AdminStatistics,
        meta: { title: '数据统计', roles: ['admin'] }
      },
      {
        path: '/admin/departments',
        name: 'adminDepartments',
        component: AdminDepartments,
        meta: { title: '科室管理', roles: ['admin'] }
      },
      {
        path: '/admin/doctors',
        name: 'adminDoctors',
        component: AdminDoctors,
        meta: { title: '医生管理', roles: ['admin'] }
      },
      {
        path: '/admin/approvals',
        name: 'adminApprovals',
        component: ApprovalManagement,
        meta: { title: '审批管理', roles: ['admin'] }
      },
      {
        path: '/doctor/dashboard',
        name: 'doctorDashboard',
        component: DoctorDashboard,
        meta: { title: '控制台', roles: ['doctor'] }
      },
      {
        path: '/doctor/consultations',
        name: 'doctorConsultations',
        component: DoctorConsultations,
        meta: { title: '处理问诊', roles: ['doctor'] }
      },
      {
        path: '/doctor/prescriptions',
        name: 'doctorPrescriptions',
        component: DoctorPrescriptions,
        meta: { title: '开具处方', roles: ['doctor'] }
      },
      {
        path: '/ai-chat',
        name: 'aiChat',
        component: AIChat,
        meta: { title: 'AI问诊', roles: ['user'] }
      }
    ]
  },
  {
    path: '/login',
    name: 'login',
    component: Login
  },
  {
    path: '/register',
    name: 'register',
    component: Register
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token') || sessionStorage.getItem('token')
  const role = localStorage.getItem('role') || sessionStorage.getItem('role') || ''

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!token) {
      next('/login')
      return
    }

    const requiredRoles = to.meta.roles as string[] | undefined
    if (requiredRoles && !requiredRoles.includes(role)) {
      if (role === 'admin') {
        next('/admin/statistics')
      } else if (role === 'doctor') {
        next('/doctor/dashboard')
      } else {
        next('/dashboard')
      }
      return
    }

    next()
  } else {
    if ((to.path === '/login' || to.path === '/register') && token) {
      if (role === 'admin') {
        next('/admin/statistics')
      } else if (role === 'doctor') {
        next('/doctor/dashboard')
      } else {
        next('/dashboard')
      }
    } else {
      next()
    }
  }
})

export default router
