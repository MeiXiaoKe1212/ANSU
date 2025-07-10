import { createRouter, createWebHistory } from 'vue-router'

// Lazy-loaded route components
const Login = () => import('../views/Login.vue')
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
        meta: { title: '订单列表' }
      },
      {
        path: 'detail/:id',
        name: 'Detail',
        component: () => import('../views/DetailPage.vue'),
        meta: { title: '详情' }
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
  const isAuthenticated = localStorage.getItem('token')
  
  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router 