import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import Layout from '@/views/Layout.vue'
import Home from '@/views/Home.vue'
import About from '@/views/About.vue'
import My from '@/views/My.vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import Users from '@/views/Users.vue'
import Drugs from '@/views/drugs.vue'
import Appointments from '@/views/Appointments.vue'
import Consultations from '@/views/Consultations.vue'


const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: Layout,
    redirect: '/home',
    meta: { requiresAuth: true },
    children: [
      {
        path: '/home',
        name: 'home',
        component: Home,
        meta: { title: '首页' }
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
        meta: { title: '我的' }
      },
      {
        path: '/drugs',
        name: 'drugs',
        component: Drugs,
        meta: { title: '药品管理' }
      },
      {
        path: '/appointments',
        name: 'appointments',
        component: Appointments,
        meta: { title: '预约管理' }
      },
      {
        path: '/consultations',
        name: 'consultations',
        component: Consultations,
        meta: { title: '在线问诊' }
      },
      {
        path: '/users',
        name: 'users',
        component: Users,
        meta: { title: '用户管理' }
      }
    ]
  },
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'register',
    component: Register,
    meta: { title: '注册' }
  }
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token') || sessionStorage.getItem('token')
  const isLoggedIn = !!token

  if (to.meta.requiresAuth && !isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && isLoggedIn) {
    next('/home')
  } else {
    next()
  }
})

export default router
