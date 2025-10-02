import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi, register as registerApi, getCurrentUser, refreshToken as refreshTokenApi } from '../api/auth'

export const useUserStore = defineStore('userStore', () => {
  // 用户信息
  const user = ref(null)
  const token = ref(localStorage.getItem('token') || '')
  const refreshToken = ref(localStorage.getItem('refreshToken') || '')
  const isLoggedIn = ref(!!token.value)

  // 登录
  const login = async (loginData) => {
    try {
      const response = await loginApi(loginData)
      
      if (response.code === 200) {
        const { accessToken, refreshToken: newRefreshToken, user: userInfo } = response.data
        
        // 保存token和用户信息
        token.value = accessToken
        refreshToken.value = newRefreshToken
        user.value = userInfo
        isLoggedIn.value = true
        
        // 保存到本地存储
        localStorage.setItem('token', accessToken)
        localStorage.setItem('refreshToken', newRefreshToken)
        localStorage.setItem('username', userInfo.username)
        localStorage.setItem('realName', userInfo.realName || userInfo.username)
        
        return { success: true, message: response.message, data: response.data }
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
        return { success: true, message: response.message || '注册成功', data: response.data }
      } else {
        return { success: false, message: response.message || '注册失败' }
      }
    } catch (error) {
      console.error('注册错误:', error)
      return { success: false, message: error.message || '注册失败' }
    }
  }

  // 刷新Token
  const refreshAccessToken = async () => {
    try {
      if (!refreshToken.value) {
        throw new Error('没有刷新令牌')
      }

      const response = await refreshTokenApi({ refreshToken: refreshToken.value })
      
      if (response.code === 200) {
        const { accessToken, refreshToken: newRefreshToken, user: userInfo } = response.data
        
        // 更新token和用户信息
        token.value = accessToken
        refreshToken.value = newRefreshToken
        user.value = userInfo
        
        // 更新本地存储
        localStorage.setItem('token', accessToken)
        localStorage.setItem('refreshToken', newRefreshToken)
        localStorage.setItem('realName', userInfo.realName || userInfo.username)
        
        return { success: true, data: response.data }
      } else {
        // 刷新失败，清除所有信息
        logout()
        return { success: false, message: response.message || 'Token刷新失败' }
      }
    } catch (error) {
      console.error('Token刷新错误:', error)
      logout()
      return { success: false, message: error.message || 'Token刷新失败' }
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
        user.value = response.data
        return { success: true, data: response.data }
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
    refreshToken.value = ''
    isLoggedIn.value = false
    
    // 清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('username')
    localStorage.removeItem('realName')
  }

  // 检查登录状态
  const checkLoginStatus = () => {
    const storedToken = localStorage.getItem('token')
    const storedRefreshToken = localStorage.getItem('refreshToken')
    
    if (storedToken && storedRefreshToken) {
      token.value = storedToken
      refreshToken.value = storedRefreshToken
      isLoggedIn.value = true
      // 获取用户信息
      fetchUserInfo()
    } else {
      logout()
    }
  }

  return {
    user,
    token,
    refreshToken,
    isLoggedIn,
    login,
    register,
    refreshAccessToken,
    fetchUserInfo,
    logout,
    checkLoginStatus
  }
}, {
  // 启用持久化
  persist: {
    key: 'user-store',
    storage: localStorage,
    paths: ['user', 'token', 'refreshToken', 'isLoggedIn']
  }
})
