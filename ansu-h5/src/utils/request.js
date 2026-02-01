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
  error => {
    console.error('响应错误:', error)
    
    let message = '网络错误'
    
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 400:
          message = data.message || '请求参数错误'
          break
        case 401:
          message = '未授权，请重新登录'
          // 清除token并跳转到登录页
          localStorage.removeItem('token')
          localStorage.removeItem('username')
          window.location.href = '/login'
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
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时'
    } else if (error.message.includes('Network Error')) {
      message = '网络连接异常'
    }
    
    Toast({
      message,
      theme: 'error'
    })
    
    return Promise.reject(error)
  }
)

export default request
