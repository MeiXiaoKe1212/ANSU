import request from '../utils/request'

/**
 * 根据订单ID获取事件列表
 * @param {number} orderId - 订单ID
 */
export const getOrderEventsByOrderId = (orderId) => {
  return request({
    url: `/order-events/order/${orderId}`,
    method: 'get'
  })
}

/**
 * 添加订单事件
 * @param {Object} eventData - 事件数据
 * @param {number} eventData.orderId - 订单ID
 * @param {string} eventData.eventType - 事件类型
 * @param {string} eventData.eventTitle - 事件标题
 * @param {string} eventData.eventDescription - 事件描述
 * @param {string} eventData.eventDate - 事件日期
 * @param {number} eventData.isResolved - 是否已解决 (0-未解决, 1-已解决)
 */
export const addOrderEvent = (eventData) => {
  return request({
    url: '/order-events',
    method: 'post',
    data: eventData
  })
}

/**
 * 更新订单事件
 * @param {number} id - 事件ID
 * @param {Object} eventData - 事件数据
 */
export const updateOrderEvent = (id, eventData) => {
  return request({
    url: `/order-events/${id}`,
    method: 'put',
    data: eventData
  })
}

/**
 * 删除订单事件
 * @param {number} id - 事件ID
 */
export const deleteOrderEvent = (id) => {
  return request({
    url: `/order-events/${id}`,
    method: 'delete'
  })
}

/**
 * 标记事件为已解决
 * @param {number} id - 事件ID
 * @param {string} resolutionDescription - 解决描述
 */
export const resolveOrderEvent = (id, resolutionDescription = '问题已解决') => {
  return request({
    url: `/order-events/${id}/resolve`,
    method: 'put',
    params: {
      resolutionDescription
    }
  })
}

/**
 * 获取事件类型名称
 * @param {string} type - 事件类型
 */
export const getEventTypeName = (type) => {
  const typeMap = {
    'DELAY': '延误',
    'ACCIDENT': '事故',
    'BREAKDOWN': '故障',
    'WEATHER': '天气',
    'TRAFFIC': '交通',
    'CUSTOMER': '客户问题',
    'OTHER': '其他'
  }
  return typeMap[type] || type
}
