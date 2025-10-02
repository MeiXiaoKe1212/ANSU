import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '../utils/request'
import * as transportOrderApi from '../api/transportOrder'

// 运输订单数据存储
export const useTransportOrderStore = defineStore('transportOrderStore', () => {
  // 订单列表
  const orders = ref([])

  // 加载状态
  const loading = ref(false)
  
  // 生成唯一ID（临时使用，实际应该由后端生成）
  const generateId = () => {
    return Date.now().toString(36) + Math.random().toString(36).substring(2, 9)
  }
  
  // 获取单个订单详情
  const fetchOrderById = async (id) => {
    loading.value = true
    try {
      const response = await transportOrderApi.getTransportOrderById(id)
      return response.data
    } catch (error) {
      console.error('获取订单详情失败:', error)
      // 如果API不可用，从本地查找
      const localOrder = orders.value.find(order => order.id == id)
      if (localOrder) {
        return localOrder
      }
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取订单列表
  const fetchOrders = async (params = {}) => {
    loading.value = true
    try {
      const response = await transportOrderApi.getTransportOrderPage(params)
      orders.value = response.data.records || []
      return response.data
    } catch (error) {
      console.error('获取订单列表失败:', error)
      // 如果API不可用，使用本地模拟数据
      if (orders.value.length === 0) {
        orders.value = getMockOrders()
      }
      throw error
    } finally {
      loading.value = false
    }
  }
  
  // 创建订单
  const createOrder = async (orderData) => {
    loading.value = true
    try {
      console.log('创建订单数据:', orderData) // 调试日志

      const response = await transportOrderApi.createTransportOrder(orderData)

      console.log('创建订单响应:', response) // 调试日志

      // 重新获取列表以确保数据同步
      await fetchOrders()
      return response.data
    } catch (error) {
      console.error('创建订单失败:', error)
      // 抛出错误，不进行本地缓存
      throw error
    } finally {
      loading.value = false
    }
  }
  
  // 更新订单
  const updateOrder = async (id, orderData) => {
    loading.value = true
    try {
      const response = await transportOrderApi.updateTransportOrder(id, orderData)

      // 更新本地缓存
      const index = orders.value.findIndex(order => order.id === id)
      if (index !== -1) {
        orders.value[index] = response.data
      }

      return response.data
    } catch (error) {
      console.error('更新订单失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }
  
  // 更新运输状态
  const updateTransportStatus = async (id, status, reason = '') => {
    try {
      const response = await transportOrderApi.updateTransportStatus(id, status, reason)

      console.log('更新运输状态响应:', response) // 调试日志

      // 更新本地缓存
      const index = orders.value.findIndex(order => order.id == id)
      if (index !== -1) {
        orders.value[index].transportStatus = status
      }

      return true
    } catch (error) {
      console.error('更新运输状态失败:', error)
      throw error
    }
  }
  
  // 更新款项状态
  const updatePaymentStatus = async (id, status, reason = '') => {
    try {
      const response = await transportOrderApi.updatePaymentStatus(id, status, reason)

      console.log('更新款项状态响应:', response) // 调试日志

      // 更新本地缓存
      const index = orders.value.findIndex(order => order.id == id)
      if (index !== -1) {
        orders.value[index].paymentStatus = status
      }

      return true
    } catch (error) {
      console.error('更新款项状态失败:', error)
      throw error
    }
  }
  
  // 删除订单
  const deleteOrder = async (id) => {
    try {
      const response = await transportOrderApi.deleteTransportOrder(id)

      const index = orders.value.findIndex(order => order.id === id)
      if (index !== -1) {
        orders.value.splice(index, 1)
      }
      return true
    } catch (error) {
      console.error('删除订单失败:', error)
      // 如果API不可用，使用本地删除
      const index = orders.value.findIndex(order => order.id === id)
      if (index !== -1) {
        orders.value.splice(index, 1)
        return true
      }
      return false
    }
  }
  
  // 获取统计数据
  const getStatistics = async () => {
    try {
      const response = await transportOrderApi.getTransportOrderStatistics()
      return response.data
    } catch (error) {
      console.error('获取统计数据失败:', error)
      // 如果API不可用，使用本地计算
      return calculateLocalStatistics()
    }
  }
  
  // 生成订单编号 - 格式：TD{yyyyMMdd}{HHMMSS} (16位)
  const generateOrderNo = () => {
    const prefix = 'TD'
    const timestamp = new Date().toISOString().replace(/[-:T.]/g, '').slice(0, 14)
    return `${prefix}${timestamp}` // TD + 14位时间戳 = 16位
  }
  
  // 本地统计计算
  const calculateLocalStatistics = () => {
    const totalOrders = orders.value.length
    const totalRevenue = orders.value.reduce((sum, order) => sum + (order.actualPrice || 0), 0)
    const totalProfit = orders.value.reduce((sum, order) => sum + (order.profit || 0), 0)
    
    const statusCount = orders.value.reduce((acc, order) => {
      acc[order.transportStatus] = (acc[order.transportStatus] || 0) + 1
      return acc
    }, {})
    
    return {
      totalOrders,
      totalRevenue,
      totalProfit,
      statusCount
    }
  }
  
  // 模拟数据
  const getMockOrders = () => {
    return [
      {
        id: '1',
        orderNo: 'TO20241210001',
        customerCompanyName: '测试物流公司',
        customerContactPerson: '张三',
        customerContactPhone: '13800138000',
        startAddress: '北京市朝阳区',
        endAddress: '上海市浦东新区',
        cargoName: '电子产品',
        cargoWeight: 5.5,
        licensePlate: '京A12345',
        vehicleType: '厢式货车',
        driverName: '李四',
        driverPhone: '13900139000',
        transportStatus: 'CREATED',
        paymentStatus: 'UNPAID',
        isOutsourced: 0,
        quotedPrice: 5000,
        actualPrice: 5000,
        totalCost: 3000,
        profit: 2000,
        profitRate: 40,
        createTime: new Date().toISOString(),
        remarks: '测试订单'
      }
    ]
  }
  
  return {
    orders,
    loading,
    fetchOrders,
    fetchOrderById,
    createOrder,
    updateOrder,
    updateTransportStatus,
    updatePaymentStatus,
    deleteOrder,
    getStatistics
  }
}, {
  persist: true
})
