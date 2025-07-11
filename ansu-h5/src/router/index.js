import { createRouter, createWebHistory } from 'vue-router'

// Lazy-loaded route components
const Login = () => import('../views/Login.vue')
const Register = () => import('../views/Register.vue')
const ForgotPassword = () => import('../views/ForgotPassword.vue')
const ResetPassword = () => import('../views/ResetPassword.vue')
const EditProfile = () => import('../views/EditProfile.vue')
const Layout = () => import('../layout/Layout.vue')

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: { requiresAuth: false }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: ForgotPassword,
    meta: { requiresAuth: false }
  },
  {
    path: '/reset-password',
    name: 'ResetPassword',
    component: ResetPassword,
    meta: { requiresAuth: false }
  },
  {
    path: '/edit-profile',
    name: 'EditProfile',
    component: EditProfile,
    meta: { requiresAuth: true }
  },
  {
    path: '/',
    component: Layout,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('../views/Home.vue'),
        meta: { title: '主页' }
      },
      {
        path: 'list',
        name: 'List',
        component: () => import('../views/List.vue'),
        meta: { title: '列表' }
      },
      {
        path: 'detail/:id',
        name: 'Detail',
        component: () => import('../views/DetailPage.vue'),
        meta: { title: '详情' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('../views/Profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard for authentication
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  // 如果访问需要认证的页面但没有token，跳转到登录页
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }

  // 如果已登录用户访问登录、注册或密码重置页面，跳转到首页
  if (token && (to.path === '/login' || to.path === '/register' || to.path === '/forgot-password' || to.path === '/reset-password')) {
    next('/home')
    return
  }

  next()
})

export default router 