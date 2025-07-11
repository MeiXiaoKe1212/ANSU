import request from '../utils/request'

/**
 * 根据订单ID获取成本列表
 * @param {number} orderId - 订单ID
 */
export const getOrderCostsByOrderId = (orderId) => {
  return request({
    url: `/order-costs/order/${orderId}`,
    method: 'get'
  })
}

/**
 * 添加订单成本
 * @param {Object} costData - 成本数据
 * @param {number} costData.orderId - 订单ID
 * @param {string} costData.costType - 成本类型
 * @param {string} costData.costName - 成本名称
 * @param {number} costData.amount - 金额
 * @param {string} costData.costDate - 发生日期
 * @param {string} costData.description - 描述
 */
export const addOrderCost = (costData) => {
  // 确保数据格式正确
  const submitData = {
    ...costData,
    amount: parseFloat(costData.amount)
  }

  return request({
    url: '/order-costs',
    method: 'post',
    data: submitData
  })
}

/**
 * 更新订单成本
 * @param {number} id - 成本记录ID
 * @param {Object} costData - 成本数据
 */
export const updateOrderCost = (id, costData) => {
  return request({
    url: `/order-costs/${id}`,
    method: 'put',
    data: costData
  })
}

/**
 * 删除订单成本
 * @param {number} id - 成本记录ID
 * @param {number} orderId - 订单ID
 */
export const deleteOrderCost = (id, orderId) => {
  return request({
    url: `/order-costs/${id}`,
    method: 'delete',
    params: {
      orderId
    }
  })
}

/**
 * 获取成本类型名称
 * @param {string} type - 成本类型
 */
export const getCostTypeName = (type) => {
  const typeMap = {
    'FUEL': '加油费',
    'TOLL': '过路费',
    'FINE': '违章费',
    'MAINTENANCE': '维修费',
    'OUTSOURCE': '外包费',
    'OTHER': '其他费用'
  }
  return typeMap[type] || type
}
