import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, register as registerApi, getCurrentUser, refreshToken as refreshTokenApi } from '../api/auth'

export const useUserStore = defineStore('userStore', () => {
  // 用户信息
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || '')
  const tenantId = ref(localStorage.getItem('tenantId') || null)
  const isLoggedIn = ref(!!token.value)

  // 登录
  const login = async (loginData) => {
    try {
      const response = await loginApi(loginData)
      
      if (response.code === 200) {
        const { token: newToken, user: userInfo, tenantId: userTenantId } = response.data

        // 保存token、用户信息和租户信息
        token.value = newToken
        user.value = userInfo
        tenantId.value = userTenantId
        isLoggedIn.value = true

        // 持久化存储
        localStorage.setItem('token', newToken)
        localStorage.setItem('tenantId', userTenantId)
        localStorage.setItem('username', userInfo.username)

        return { success: true, message: '登录成功' }
      } else {
        return { success: false, message: response.message || '登录失败' }
      }
    } catch (error) {
      console.error('登录错误:', error)
      return { success: false, message: error.message || '登录失败' }
    }
  }

  // 注册
  const register = async (registerData) => {
    try {
      const response = await registerApi(registerData)
      
      if (response.code === 200) {
        return { success: true, message: '注册成功', data: response.data }
      } else {
        return { success: false, message: response.message || '注册失败' }
      }
    } catch (error) {
      console.error('注册错误:', error)
      return { success: false, message: error.message || '注册失败' }
    }
  }

  // 获取用户信息
  const fetchUserInfo = async () => {
    try {
      if (!token.value) {
        return { success: false, message: '未登录' }
      }

      const response = await getCurrentUser()
      
      if (response.code === 200) {
        const { user: userInfo, tenantId: userTenantId } = response.data
        user.value = userInfo
        tenantId.value = userTenantId

        // 更新本地存储
        localStorage.setItem('tenantId', userTenantId)

        return { success: true, data: userInfo }
      } else {
        return { success: false, message: response.message || '获取用户信息失败' }
      }
    } catch (error) {
      console.error('获取用户信息错误:', error)
      return { success: false, message: error.message || '获取用户信息失败' }
    }
  }

  // 登出
  const logout = () => {
    user.value = null
    token.value = ''
    tenantId.value = null
    isLoggedIn.value = false

    // 清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('tenantId')
    localStorage.removeItem('username')
  }

  // 刷新Token
  const refreshToken = async () => {
    try {
      const response = await refreshTokenApi()

      if (response.code === 200) {
        const { token: newToken } = response.data
        token.value = newToken
        localStorage.setItem('token', newToken)
        return { success: true, token: newToken }
      } else {
        return { success: false, message: response.message || 'Token刷新失败' }
      }
    } catch (error) {
      console.error('Token刷新错误:', error)
      return { success: false, message: error.message || 'Token刷新失败' }
    }
  }

  // 检查登录状态
  const checkLoginStatus = () => {
    const storedToken = localStorage.getItem('token')
    const storedTenantId = localStorage.getItem('tenantId')

    if (storedToken) {
      token.value = storedToken
      tenantId.value = storedTenantId
      isLoggedIn.value = true
      // 可以在这里验证token是否有效
      fetchUserInfo()
    } else {
      logout()
    }
  }

  return {
    user,
    token,
    tenantId,
    isLoggedIn,
    login,
    register,
    fetchUserInfo,
    refreshToken,
    logout,
    checkLoginStatus
  }
}, {
  // 启用持久化
  persist: {
    key: 'user-store',
    storage: localStorage,
    paths: ['user', 'token', 'tenantId', 'isLoggedIn']
  }
})
