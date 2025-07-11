import request from '../utils/request'

/**
 * 分页获取订单列表
 * @param {Object} params - 查询参数
 * @param {number} params.page - 页码
 * @param {number} params.size - 每页数量
 */
export const getOrders = (params) => {
  return request({
    url: '/orders',
    method: 'get',
    params
  })
}

/**
 * 获取所有订单
 */
export const getAllOrders = () => {
  return request({
    url: '/orders/all',
    method: 'get'
  })
}

/**
 * 根据ID获取订单详情
 * @param {number} id - 订单ID
 */
export const getOrderById = (id) => {
  return request({
    url: `/orders/${id}`,
    method: 'get'
  })
}

/**
 * 创建订单
 * @param {Object} orderData - 订单数据
 */
export const createOrder = (orderData) => {
  return request({
    url: '/orders',
    method: 'post',
    data: orderData
  })
}

/**
 * 更新订单
 * @param {number} id - 订单ID
 * @param {Object} orderData - 订单数据
 */
export const updateOrder = (id, orderData) => {
  return request({
    url: `/orders/${id}`,
    method: 'put',
    data: orderData
  })
}

/**
 * 删除订单
 * @param {number} id - 订单ID
 */
export const deleteOrder = (id) => {
  return request({
    url: `/orders/${id}`,
    method: 'delete'
  })
}

/**
 * 根据状态获取订单
 * @param {number} status - 订单状态
 */
export const getOrdersByStatus = (status) => {
  return request({
    url: `/orders/status/${status}`,
    method: 'get'
  })
}
