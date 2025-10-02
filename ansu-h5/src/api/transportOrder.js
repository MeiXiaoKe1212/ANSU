import request from '../utils/request'

/**
 * 分页查询运输订单
 * @param {Object} params - 查询参数
 * @param {number} params.current - 当前页码
 * @param {number} params.size - 每页大小
 * @param {string} params.keyword - 关键词
 * @param {string} params.transportStatus - 运输状态
 * @param {string} params.paymentStatus - 款项状态
 */
export const getTransportOrderPage = (params) => {
  return request({
    url: '/transport-orders/page',
    method: 'get',
    params: {
      current: params.current || 1,
      size: params.size || 10,
      ...(params.keyword && { keyword: params.keyword }),
      ...(params.transportStatus && { transportStatus: params.transportStatus }),
      ...(params.paymentStatus && { paymentStatus: params.paymentStatus })
    }
  })
}

/**
 * 根据ID查询订单详情
 * @param {number} id - 订单ID
 */
export const getTransportOrderById = (id) => {
  return request({
    url: `/transport-orders/${id}`,
    method: 'get'
  })
}

/**
 * 创建运输订单
 * @param {Object} orderData - 订单数据
 */
export const createTransportOrder = (orderData) => {
  // 确保数据格式正确
  const submitData = {
    ...orderData,
    // 确保数字字段正确转换
    cargoWeight: orderData.cargoWeight ? parseFloat(orderData.cargoWeight) : null,
    cargoVolume: orderData.cargoVolume ? parseFloat(orderData.cargoVolume) : null,
    quotedPrice: orderData.quotedPrice ? parseFloat(orderData.quotedPrice) : null,
    actualPrice: orderData.actualPrice ? parseFloat(orderData.actualPrice) : null,
    // 确保布尔字段正确转换
    isOutsourced: orderData.isOutsourced ? 1 : 0
  }

  return request({
    url: '/transport-orders',
    method: 'post',
    data: submitData
  })
}

/**
 * 更新运输订单
 * @param {number} id - 订单ID
 * @param {Object} orderData - 订单数据
 */
export const updateTransportOrder = (id, orderData) => {
  return request({
    url: `/transport-orders/${id}`,
    method: 'put',
    data: orderData
  })
}

/**
 * 删除运输订单
 * @param {number} id - 订单ID
 */
export const deleteTransportOrder = (id) => {
  return request({
    url: `/transport-orders/${id}`,
    method: 'delete'
  })
}

/**
 * 更新运输状态
 * @param {number} id - 订单ID
 * @param {string} status - 新状态
 * @param {string} reason - 原因（可选）
 */
export const updateTransportStatus = (id, status, reason) => {
  return request({
    url: `/transport-orders/${id}/transport-status`,
    method: 'put',
    params: {
      status,
      ...(reason && { reason })
    }
  })
}

/**
 * 更新款项状态
 * @param {number} id - 订单ID
 * @param {string} status - 新状态
 * @param {string} reason - 原因（可选）
 */
export const updatePaymentStatus = (id, status, reason) => {
  return request({
    url: `/transport-orders/${id}/payment-status`,
    method: 'put',
    params: {
      status,
      ...(reason && { reason })
    }
  })
}

/**
 * 获取订单统计数据
 */
export const getTransportOrderStatistics = () => {
  return request({
    url: '/transport-orders/statistics',
    method: 'get'
  })
}

/**
 * 获取月度收入统计
 */
export const getMonthlyRevenue = () => {
  return request({
    url: '/transport-orders/monthly-revenue',
    method: 'get'
  })
}
