import axios from 'axios'
import { Toast } from 'tdesign-mobile-vue'

// 创建axios实例
const request = axios.create({
  baseURL: 'http://localhost:8080/api', // 后端API地址
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json'
  }
})

// 是否正在刷新token
let isRefreshing = false
// 失败队列
let failedQueue = []

// 处理队列
const processQueue = (error, token = null) => {
  failedQueue.forEach(({ resolve, reject }) => {
    if (error) {
      reject(error)
    } else {
      resolve(token)
    }
  })
  
  failedQueue = []
}

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const { data } = response
    
    // 如果响应成功
    if (data.code === 200) {
      return data
    }
    
    // 如果是业务错误
    Toast({
      message: data.message || '请求失败',
      theme: 'error'
    })
    
    return Promise.reject(new Error(data.message || '请求失败'))
  },
  async error => {
    const originalRequest = error.config
    
    if (error.response) {
      const { status, data } = error.response
      
      // 如果是401错误且不是刷新token的请求
      if (status === 401 && !originalRequest._retry && !originalRequest.url.includes('/auth/refresh')) {
        if (isRefreshing) {
          // 如果正在刷新token，将请求加入队列
          return new Promise((resolve, reject) => {
            failedQueue.push({ resolve, reject })
          }).then(token => {
            originalRequest.headers.Authorization = `Bearer ${token}`
            return request(originalRequest)
          }).catch(err => {
            return Promise.reject(err)
          })
        }

        originalRequest._retry = true
        isRefreshing = true

        const refreshToken = localStorage.getItem('refreshToken')
        
        if (refreshToken) {
          try {
            // 尝试刷新token
            const response = await axios.post('http://localhost:8080/api/auth/refresh', {
              refreshToken: refreshToken
            })
            
            if (response.data.code === 200) {
              const { accessToken, refreshToken: newRefreshToken } = response.data.data
              
              // 更新本地存储
              localStorage.setItem('token', accessToken)
              localStorage.setItem('refreshToken', newRefreshToken)
              
              // 更新请求头
              originalRequest.headers.Authorization = `Bearer ${accessToken}`
              
              // 处理队列中的请求
              processQueue(null, accessToken)
              
              isRefreshing = false
              
              // 重新发送原始请求
              return request(originalRequest)
            } else {
              throw new Error('Token刷新失败')
            }
          } catch (refreshError) {
            // 刷新失败，清除token并跳转到登录页
            processQueue(refreshError, null)
            isRefreshing = false
            
            localStorage.removeItem('token')
            localStorage.removeItem('refreshToken')
            localStorage.removeItem('username')
            localStorage.removeItem('realName')
            
            Toast({
              message: '登录已过期，请重新登录',
              theme: 'error'
            })
            
            // 跳转到登录页
            setTimeout(() => {
              window.location.href = '/login'
            }, 1000)
            
            return Promise.reject(refreshError)
          }
        } else {
          // 没有refreshToken，直接跳转到登录页
          isRefreshing = false
          localStorage.removeItem('token')
          localStorage.removeItem('refreshToken')
          localStorage.removeItem('username')
          localStorage.removeItem('realName')
          
          Toast({
            message: '未授权，请重新登录',
            theme: 'error'
          })
          
          setTimeout(() => {
            window.location.href = '/login'
          }, 1000)
          
          return Promise.reject(error)
        }
      }
      
      // 处理其他HTTP错误
      let message = '网络错误'
      
      switch (status) {
        case 400:
          message = data.message || '请求参数错误'
          break
        case 403:
          message = '拒绝访问'
          break
        case 404:
          message = '请求地址不存在'
          break
        case 500:
          message = data.message || '服务器内部错误'
          break
        default:
          message = data.message || `连接错误${status}`
      }
      
      Toast({
        message,
        theme: 'error'
      })
    } else if (error.code === 'ECONNABORTED') {
      Toast({
        message: '请求超时',
        theme: 'error'
      })
    } else if (error.message.includes('Network Error')) {
      Toast({
        message: '网络连接异常',
        theme: 'error'
      })
    }
    
    return Promise.reject(error)
  }
)

export default request
