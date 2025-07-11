<template>
  <div>
    <t-navbar title="订单管理" fixed />
    
    <div class="order-container">
      <!-- 状态筛选 -->
      <div class="filter-tabs">
        <div 
          v-for="status in statusOptions" 
          :key="status.value"
          class="filter-tab"
          :class="{ active: currentStatus === status.value }"
          @click="filterByStatus(status.value)"
        >
          {{ status.label }}
        </div>
      </div>
      
      <!-- 订单列表 -->
      <div class="order-list">
        <div 
          v-for="order in filteredOrders" 
          :key="order.id"
          class="order-item"
          @click="viewOrderDetail(order)"
        >
          <div class="order-header">
            <span class="order-no">{{ order.orderNo }}</span>
            <span class="order-status" :class="getStatusClass(order.status)">
              {{ getStatusText(order.status) }}
            </span>
          </div>
          
          <div class="order-info">
            <div class="info-row">
              <span class="label">客户：</span>
              <span class="value">{{ order.customerName }}</span>
            </div>
            <div class="info-row">
              <span class="label">货物：</span>
              <span class="value">{{ order.goodsName || '未填写' }}</span>
            </div>
            <div class="info-row">
              <span class="label">费用：</span>
              <span class="value price">¥{{ order.transportFee }}</span>
            </div>
          </div>
          
          <div class="order-footer">
            <span class="create-time">{{ formatDate(order.createTime) }}</span>
            <t-icon name="chevron-right" />
          </div>
        </div>
        
        <div v-if="filteredOrders.length === 0" class="empty-state">
          <t-icon name="inbox" size="48px" />
          <p>暂无订单数据</p>
        </div>
      </div>
      
      <!-- 添加订单按钮 -->
      <div class="fab-container">
        <t-fab 
          icon="add" 
          @click="createOrder"
          style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAllOrders } from '../api/order'
import { Toast } from 'tdesign-mobile-vue'

const router = useRouter()

const orders = ref([])
const currentStatus = ref(0) // 0表示全部

const statusOptions = [
  { value: 0, label: '全部' },
  { value: 1, label: '待接单' },
  { value: 2, label: '已接单' },
  { value: 3, label: '运输中' },
  { value: 4, label: '已完成' },
  { value: 5, label: '已取消' }
]

// 根据状态筛选订单
const filteredOrders = computed(() => {
  if (currentStatus.value === 0) {
    return orders.value
  }
  return orders.value.filter(order => order.status === currentStatus.value)
})

// 获取订单列表
const fetchOrders = async () => {
  try {
    const response = await getAllOrders()
    if (response.code === 200) {
      orders.value = response.data || []
    }
  } catch (error) {
    console.error('获取订单失败:', error)
    Toast({
      message: '获取订单失败',
      theme: 'error'
    })
  }
}

// 按状态筛选
const filterByStatus = (status) => {
  currentStatus.value = status
}

// 查看订单详情
const viewOrderDetail = (order) => {
  router.push(`/order-detail/${order.id}`)
}

// 创建订单
const createOrder = () => {
  router.push('/create-order')
}

// 获取状态样式类
const getStatusClass = (status) => {
  const statusClasses = {
    1: 'status-pending',
    2: 'status-accepted',
    3: 'status-shipping',
    4: 'status-completed',
    5: 'status-cancelled'
  }
  return statusClasses[status] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const statusTexts = {
    1: '待接单',
    2: '已接单',
    3: '运输中',
    4: '已完成',
    5: '已取消'
  }
  return statusTexts[status] || '未知状态'
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getMonth() + 1}-${date.getDate()} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-container {
  padding-top: 56px; /* 为固定导航栏留出空间 */
  padding-bottom: 80px; /* 为底部导航留出空间 */
  min-height: 100vh;
  background: #f5f5f5;
}

.filter-tabs {
  display: flex;
  background: white;
  padding: 10px 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow-x: auto;
}

.filter-tab {
  padding: 8px 16px;
  margin-right: 10px;
  border-radius: 20px;
  background: #f0f0f0;
  color: #666;
  font-size: 14px;
  white-space: nowrap;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-tab.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.order-list {
  padding: 15px;
}

.order-item {
  background: white;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s;
}

.order-item:hover {
  transform: translateY(-2px);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.order-no {
  font-weight: 600;
  font-size: 16px;
  color: #333;
}

.order-status {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status-pending { background: #fff3cd; color: #856404; }
.status-accepted { background: #d4edda; color: #155724; }
.status-shipping { background: #cce5ff; color: #004085; }
.status-completed { background: #d1ecf1; color: #0c5460; }
.status-cancelled { background: #f8d7da; color: #721c24; }

.order-info {
  margin-bottom: 12px;
}

.info-row {
  display: flex;
  margin-bottom: 6px;
}

.info-row .label {
  width: 60px;
  color: #666;
  font-size: 14px;
}

.info-row .value {
  flex: 1;
  color: #333;
  font-size: 14px;
}

.info-row .price {
  color: #e74c3c;
  font-weight: 600;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}

.create-time {
  font-size: 12px;
  color: #999;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-state p {
  margin-top: 15px;
  font-size: 16px;
}

.fab-container {
  position: fixed;
  bottom: 100px;
  right: 20px;
  z-index: 1000;
}
</style>
