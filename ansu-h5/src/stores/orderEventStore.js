import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '../utils/request'

// 订单事件数据存储
export const useOrderEventStore = defineStore('orderEventStore', () => {
  // 事件列表
  const events = ref([])
  
  // 加载状态
  const loading = ref(false)
  

  
  // 获取订单事件列表
  const fetchEventsByOrderId = async (orderId) => {
    loading.value = true
    try {
      const response = await request({
        url: `/order-events/order/${orderId}`,
        method: 'get'
      })

      events.value = response.data || []
      return response.data
    } catch (error) {
      console.error('获取事件列表失败:', error)
      // 如果API不可用，使用本地模拟数据
      events.value = getMockEvents(orderId)
      return events.value
    } finally {
      loading.value = false
    }
  }
  
  // 添加事件
  const addEvent = async (eventData) => {
    loading.value = true
    try {
      console.log('发送事件数据到API:', eventData) // 调试日志

      const response = await fetch(`${API_BASE_URL}/order-events`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(eventData)
      })

      const result = await response.json()
      console.log('API响应:', result) // 调试日志

      if (result.code === 200) {
        events.value.unshift(result.data)
        return result.data
      } else {
        throw new Error(result.message || '添加事件失败')
      }
    } catch (error) {
      console.error('添加事件失败:', error)
      // 如果API不可用，使用本地模拟
      const newEvent = {
        ...eventData,
        id: Date.now().toString(),
        createTime: new Date().toISOString(),
        isResolved: 0
      }
      events.value.unshift(newEvent)
      return newEvent
    } finally {
      loading.value = false
    }
  }
  
  // 解决事件
  const resolveEvent = async (eventId, resolutionDescription) => {
    try {
      const response = await fetch(`${API_BASE_URL}/order-events/${eventId}/resolve?resolutionDescription=${encodeURIComponent(resolutionDescription)}`, {
        method: 'PUT'
      })
      
      const result = await response.json()
      
      if (result.code === 200) {
        const index = events.value.findIndex(event => event.id === eventId)
        if (index !== -1) {
          events.value[index].isResolved = 1
          events.value[index].resolutionTime = new Date().toISOString()
          events.value[index].resolutionDescription = resolutionDescription
        }
        return true
      } else {
        throw new Error(result.message || '解决事件失败')
      }
    } catch (error) {
      console.error('解决事件失败:', error)
      // 如果API不可用，使用本地更新
      const index = events.value.findIndex(event => event.id === eventId)
      if (index !== -1) {
        events.value[index].isResolved = 1
        events.value[index].resolutionTime = new Date().toISOString()
        events.value[index].resolutionDescription = resolutionDescription
        return true
      }
      return false
    }
  }
  
  // 删除事件
  const deleteEvent = async (eventId) => {
    try {
      const response = await fetch(`${API_BASE_URL}/order-events/${eventId}`, {
        method: 'DELETE'
      })
      
      const result = await response.json()
      
      if (result.code === 200) {
        const index = events.value.findIndex(event => event.id === eventId)
        if (index !== -1) {
          events.value.splice(index, 1)
        }
        return true
      } else {
        throw new Error(result.message || '删除事件失败')
      }
    } catch (error) {
      console.error('删除事件失败:', error)
      // 如果API不可用，使用本地删除
      const index = events.value.findIndex(event => event.id === eventId)
      if (index !== -1) {
        events.value.splice(index, 1)
        return true
      }
      return false
    }
  }
  
  // 获取事件类型名称
  const getEventTypeName = (type) => {
    const typeMap = {
      'DRIVER_REST': '司机休息',
      'ACCIDENT': '交通事故',
      'BREAKDOWN': '车辆故障',
      'DELAY': '运输延误',
      'WEATHER': '天气影响',
      'OTHER': '其他事件'
    }
    return typeMap[type] || type
  }
  
  // 获取影响程度名称
  const getImpactLevelName = (level) => {
    const levelMap = {
      'LOW': '轻微',
      'MEDIUM': '中等',
      'HIGH': '严重'
    }
    return levelMap[level] || level
  }
  
  // 获取影响程度样式
  const getImpactLevelClass = (level) => {
    const classMap = {
      'LOW': 'impact-low',
      'MEDIUM': 'impact-medium',
      'HIGH': 'impact-high'
    }
    return classMap[level] || ''
  }
  
  // 模拟数据
  const getMockEvents = (orderId) => {
    if (orderId === '1') {
      return [
        {
          id: '1',
          orderId: '1',
          eventType: 'DRIVER_REST',
          eventTitle: '司机休息',
          eventDescription: '司机在服务区休息2小时',
          eventTime: '2024-12-09T14:30:00',
          location: '京沪高速服务区',
          impactLevel: 'LOW',
          isResolved: 1,
          resolutionTime: '2024-12-09T16:30:00',
          resolutionDescription: '休息完毕，继续行程',
          createTime: '2024-12-09T14:30:00'
        }
      ]
    }
    return []
  }
  
  return {
    events,
    loading,
    fetchEventsByOrderId,
    addEvent,
    resolveEvent,
    deleteEvent,
    getEventTypeName,
    getImpactLevelName,
    getImpactLevelClass
  }
}, {
  persist: true
})
