import { defineStore } from 'pinia'
import { ref } from 'vue'

// 订单成本数据存储
export const useOrderCostStore = defineStore('orderCostStore', () => {
  // 成本列表
  const costs = ref([])
  
  // 加载状态
  const loading = ref(false)
  
  // API基础URL
  const API_BASE_URL = 'http://localhost:8080/api'
  
  // 获取订单成本列表
  const fetchCostsByOrderId = async (orderId) => {
    loading.value = true
    try {
      const response = await fetch(`${API_BASE_URL}/order-costs/order/${orderId}`)
      const result = await response.json()
      
      if (result.code === 200) {
        costs.value = result.data || []
        return result.data
      } else {
        throw new Error(result.message || '获取成本列表失败')
      }
    } catch (error) {
      console.error('获取成本列表失败:', error)
      // 如果API不可用，使用本地模拟数据
      costs.value = getMockCosts(orderId)
      return costs.value
    } finally {
      loading.value = false
    }
  }
  
  // 添加成本
  const addCost = async (costData) => {
    loading.value = true
    try {
      const response = await fetch(`${API_BASE_URL}/order-costs`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(costData)
      })
      
      const result = await response.json()
      
      if (result.code === 200) {
        costs.value.push(result.data)
        return result.data
      } else {
        throw new Error(result.message || '添加成本失败')
      }
    } catch (error) {
      console.error('添加成本失败:', error)
      // 如果API不可用，使用本地模拟
      const newCost = {
        ...costData,
        id: Date.now().toString(),
        createTime: new Date().toISOString()
      }
      costs.value.push(newCost)
      return newCost
    } finally {
      loading.value = false
    }
  }
  
  // 删除成本
  const deleteCost = async (costId, orderId) => {
    try {
      const response = await fetch(`${API_BASE_URL}/order-costs/${costId}?orderId=${orderId}`, {
        method: 'DELETE'
      })
      
      const result = await response.json()
      
      if (result.code === 200) {
        const index = costs.value.findIndex(cost => cost.id === costId)
        if (index !== -1) {
          costs.value.splice(index, 1)
        }
        return true
      } else {
        throw new Error(result.message || '删除成本失败')
      }
    } catch (error) {
      console.error('删除成本失败:', error)
      // 如果API不可用，使用本地删除
      const index = costs.value.findIndex(cost => cost.id === costId)
      if (index !== -1) {
        costs.value.splice(index, 1)
        return true
      }
      return false
    }
  }
  
  // 获取成本类型名称
  const getCostTypeName = (type) => {
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
  
  // 模拟数据
  const getMockCosts = (orderId) => {
    if (orderId === '1') {
      return [
        {
          id: '1',
          orderId: '1',
          costType: 'FUEL',
          costName: '加油费',
          amount: 800,
          costDate: '2024-12-09',
          description: '北京到上海往返加油',
          createTime: '2024-12-09T10:00:00'
        },
        {
          id: '2',
          orderId: '1',
          costType: 'TOLL',
          costName: '过路费',
          amount: 1200,
          costDate: '2024-12-09',
          description: '高速公路过路费',
          createTime: '2024-12-09T10:30:00'
        }
      ]
    }
    return []
  }
  
  return {
    costs,
    loading,
    fetchCostsByOrderId,
    addCost,
    deleteCost,
    getCostTypeName
  }
}, {
  persist: true
})
