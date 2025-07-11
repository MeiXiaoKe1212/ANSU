import request from '../utils/request'

/**
 * 用户登录
 * @param {Object} loginData - 登录数据
 * @param {string} loginData.username - 用户名
 * @param {string} loginData.password - 密码
 */
export const login = (loginData) => {
  return request({
    url: '/auth/login',
    method: 'post',
    data: loginData
  })
}

/**
 * 用户注册
 * @param {Object} registerData - 注册数据
 * @param {string} registerData.username - 用户名
 * @param {string} registerData.password - 密码
 * @param {string} registerData.confirmPassword - 确认密码
 * @param {string} registerData.email - 邮箱
 * @param {string} registerData.phone - 手机号
 * @param {string} registerData.realName - 真实姓名
 */
export const register = (registerData) => {
  return request({
    url: '/auth/register',
    method: 'post',
    data: registerData
  })
}

/**
 * 获取当前用户信息
 */
export const getCurrentUser = () => {
  return request({
    url: '/auth/me',
    method: 'get'
  })
}

/**
 * 检查用户名是否可用
 * @param {string} username - 用户名
 */
export const checkUsername = (username) => {
  return request({
    url: '/auth/check-username',
    method: 'get',
    params: { username }
  })
}

/**
 * 检查邮箱是否可用
 * @param {string} email - 邮箱
 */
export const checkEmail = (email) => {
  return request({
    url: '/auth/check-email',
    method: 'get',
    params: { email }
  })
}

/**
 * 忘记密码
 * @param {Object} data - 忘记密码数据
 * @param {string} data.email - 邮箱
 */
export const forgotPassword = (data) => {
  return request({
    url: '/auth/forgot-password',
    method: 'post',
    data
  })
}

/**
 * 重置密码
 * @param {Object} data - 重置密码数据
 * @param {string} data.token - 重置令牌
 * @param {string} data.newPassword - 新密码
 * @param {string} data.confirmPassword - 确认密码
 */
export const resetPassword = (data) => {
  return request({
    url: '/auth/reset-password',
    method: 'post',
    data
  })
}

/**
 * 更新用户信息
 * @param {Object} data - 用户信息数据
 * @param {string} data.email - 邮箱
 * @param {string} data.phone - 手机号
 * @param {string} data.realName - 真实姓名
 * @param {string} data.avatar - 头像
 */
export const updateProfile = (data) => {
  return request({
    url: '/auth/profile',
    method: 'put',
    data
  })
}
