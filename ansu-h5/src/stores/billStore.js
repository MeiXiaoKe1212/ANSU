import { defineStore } from 'pinia'
import { ref } from 'vue'

// 账单数据存储
export const useBillStore = defineStore('billStore', () => {
  // 账单列表
  const bills = ref([])
  
  // 生成唯一ID
  const generateId = () => {
    return Date.now().toString(36) + Math.random().toString(36).substring(2, 9)
  }
  
  // 添加新账单
  const addBill = (bill) => {
    // 添加ID和创建时间
    const newBill = {
      ...bill,
      id: generateId(),
      createTime: new Date().toISOString(),
      status: '待处理'
    }
    bills.value.unshift(newBill) // 添加到列表前面
    return newBill
  }
  
  // 编辑账单
  const updateBill = (id, updatedData) => {
    const index = bills.value.findIndex(bill => bill.id === id)
    if (index !== -1) {
      bills.value[index] = {
        ...bills.value[index],
        ...updatedData,
        updateTime: new Date().toISOString()
      }
      return true
    }
    return false
  }
  
  // 删除账单
  const deleteBill = (id) => {
    const index = bills.value.findIndex(bill => bill.id === id)
    if (index !== -1) {
      bills.value.splice(index, 1)
      return true
    }
    return false
  }
  
  // 获取单个账单
  const getBill = (id) => {
    return bills.value.find(bill => bill.id === id) || null
  }
  
  return {
    bills,
    addBill,
    updateBill,
    deleteBill,
    getBill
  }
}, {
  // 启用持久化
  persist: true
}) 