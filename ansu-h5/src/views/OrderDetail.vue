<template>
  <div>
    <t-navbar title="订单详情" fixed>
      <template #left>
        <t-icon name="chevron-left" @click="handleBack" />
      </template>
      <template #right>
        <t-icon name="edit" @click="editOrder" v-if="order && order.status < 4" />
      </template>
    </t-navbar>
    
    <div class="order-detail-container" v-if="order">
      <!-- 订单状态卡片 -->
      <div class="status-card">
        <div class="status-icon" :class="getStatusClass(order.status)">
          <t-icon :name="getStatusIcon(order.status)" size="24px" />
        </div>
        <div class="status-info">
          <h3>{{ getStatusText(order.status) }}</h3>
          <p>订单号：{{ order.orderNo }}</p>
        </div>
      </div>
      
      <!-- 客户信息 -->
      <div class="info-section">
        <h4>客户信息</h4>
        <div class="info-item">
          <span class="label">客户姓名</span>
          <span class="value">{{ order.customerName }}</span>
        </div>
        <div class="info-item" v-if="order.customerPhone">
          <span class="label">联系电话</span>
          <span class="value">
            <a :href="`tel:${order.customerPhone}`">{{ order.customerPhone }}</a>
          </span>
        </div>
      </div>
      
      <!-- 货物信息 -->
      <div class="info-section">
        <h4>货物信息</h4>
        <div class="info-item">
          <span class="label">货物名称</span>
          <span class="value">{{ order.goodsName || '未填写' }}</span>
        </div>
        <div class="info-item" v-if="order.goodsWeight">
          <span class="label">货物重量</span>
          <span class="value">{{ order.goodsWeight }} kg</span>
        </div>
        <div class="info-item" v-if="order.goodsVolume">
          <span class="label">货物体积</span>
          <span class="value">{{ order.goodsVolume }} m³</span>
        </div>
      </div>
      
      <!-- 地址信息 -->
      <div class="info-section">
        <h4>运输信息</h4>
        <div class="address-item">
          <div class="address-label">
            <t-icon name="location" />
            <span>取货地址</span>
          </div>
          <div class="address-value">{{ order.pickupAddress }}</div>
        </div>
        <div class="address-item">
          <div class="address-label">
            <t-icon name="flag" />
            <span>送货地址</span>
          </div>
          <div class="address-value">{{ order.deliveryAddress }}</div>
        </div>
      </div>
      
      <!-- 费用信息 -->
      <div class="info-section">
        <h4>费用信息</h4>
        <div class="info-item">
          <span class="label">运输费用</span>
          <span class="value price">¥{{ order.transportFee }}</span>
        </div>
      </div>
      
      <!-- 备注信息 -->
      <div class="info-section" v-if="order.remark">
        <h4>备注信息</h4>
        <div class="remark-content">{{ order.remark }}</div>
      </div>
      
      <!-- 订单时间 -->
      <div class="info-section">
        <h4>订单时间</h4>
        <div class="info-item">
          <span class="label">创建时间</span>
          <span class="value">{{ formatDateTime(order.createTime) }}</span>
        </div>
        <div class="info-item" v-if="order.updateTime !== order.createTime">
          <span class="label">更新时间</span>
          <span class="value">{{ formatDateTime(order.updateTime) }}</span>
        </div>
      </div>
      
      <!-- 操作按钮 -->
      <div class="action-buttons" v-if="order.status < 4">
        <t-button 
          v-if="order.status === 1" 
          theme="primary" 
          block 
          @click="acceptOrder"
        >
          接单
        </t-button>
        <t-button 
          v-if="order.status === 2" 
          theme="primary" 
          block 
          @click="startShipping"
        >
          开始运输
        </t-button>
        <t-button 
          v-if="order.status === 3" 
          theme="success" 
          block 
          @click="completeOrder"
        >
          完成订单
        </t-button>
        <t-button 
          v-if="order.status < 3" 
          theme="danger" 
          variant="outline" 
          block 
          @click="cancelOrder"
          style="margin-top: 10px;"
        >
          取消订单
        </t-button>
      </div>
    </div>
    
    <div v-else class="loading-container">
      <t-loading text="加载中..." />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getOrderById, updateOrder } from '../api/order'
import { Toast, Dialog } from 'tdesign-mobile-vue'

const router = useRouter()
const route = useRoute()

const order = ref(null)

// 获取订单详情
const fetchOrderDetail = async () => {
  try {
    const orderId = route.params.id
    const response = await getOrderById(orderId)
    if (response.code === 200) {
      order.value = response.data
    } else {
      Toast({
        message: '订单不存在',
        theme: 'error'
      })
      router.back()
    }
  } catch (error) {
    console.error('获取订单详情失败:', error)
    Toast({
      message: '获取订单详情失败',
      theme: 'error'
    })
  }
}

// 返回上一页
const handleBack = () => {
  router.back()
}

// 编辑订单
const editOrder = () => {
  router.push(`/edit-order/${order.value.id}`)
}

// 更新订单状态
const updateOrderStatus = async (status, message) => {
  try {
    const response = await updateOrder(order.value.id, {
      ...order.value,
      status
    })
    
    if (response.code === 200) {
      order.value.status = status
      Toast({
        message,
        theme: 'success'
      })
    }
  } catch (error) {
    console.error('更新订单状态失败:', error)
    Toast({
      message: '操作失败',
      theme: 'error'
    })
  }
}

// 接单
const acceptOrder = () => {
  Dialog({
    title: '确认接单',
    content: '确定要接受这个订单吗？',
    confirmBtn: { content: '确定', theme: 'primary' },
    cancelBtn: { content: '取消' },
    onConfirm: () => {
      updateOrderStatus(2, '接单成功')
    }
  })
}

// 开始运输
const startShipping = () => {
  Dialog({
    title: '开始运输',
    content: '确定要开始运输吗？',
    confirmBtn: { content: '确定', theme: 'primary' },
    cancelBtn: { content: '取消' },
    onConfirm: () => {
      updateOrderStatus(3, '运输已开始')
    }
  })
}

// 完成订单
const completeOrder = () => {
  Dialog({
    title: '完成订单',
    content: '确定要完成这个订单吗？',
    confirmBtn: { content: '确定', theme: 'success' },
    cancelBtn: { content: '取消' },
    onConfirm: () => {
      updateOrderStatus(4, '订单已完成')
    }
  })
}

// 取消订单
const cancelOrder = () => {
  Dialog({
    title: '取消订单',
    content: '确定要取消这个订单吗？此操作不可撤销。',
    confirmBtn: { content: '确定', theme: 'danger' },
    cancelBtn: { content: '取消' },
    onConfirm: () => {
      updateOrderStatus(5, '订单已取消')
    }
  })
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

// 获取状态图标
const getStatusIcon = (status) => {
  const statusIcons = {
    1: 'time',
    2: 'check-circle',
    3: 'swap',
    4: 'check-circle-filled',
    5: 'close-circle'
  }
  return statusIcons[status] || 'help-circle'
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

// 格式化日期时间
const formatDateTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

onMounted(() => {
  fetchOrderDetail()
})
</script>

<style scoped>
.order-detail-container {
  padding: 15px;
  padding-top: 71px; /* 为固定导航栏留出空间 */
  padding-bottom: 20px;
  background: #f5f5f5;
  min-height: 100vh;
}

.status-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.status-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.status-pending { background: #fff3cd; color: #856404; }
.status-accepted { background: #d4edda; color: #155724; }
.status-shipping { background: #cce5ff; color: #004085; }
.status-completed { background: #d1ecf1; color: #0c5460; }
.status-cancelled { background: #f8d7da; color: #721c24; }

.status-info h3 {
  margin: 0 0 5px 0;
  font-size: 18px;
  color: #333;
}

.status-info p {
  margin: 0;
  font-size: 14px;
  color: #666;
}

.info-section {
  background: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.info-section h4 {
  margin: 0 0 15px 0;
  font-size: 16px;
  color: #333;
  font-weight: 600;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  color: #666;
  font-size: 14px;
}

.info-item .value {
  color: #333;
  font-size: 14px;
  text-align: right;
}

.info-item .value a {
  color: #0052d9;
  text-decoration: none;
}

.info-item .price {
  color: #e74c3c;
  font-weight: 600;
  font-size: 16px;
}

.address-item {
  margin-bottom: 15px;
}

.address-item:last-child {
  margin-bottom: 0;
}

.address-label {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  color: #666;
  font-size: 14px;
}

.address-label span {
  margin-left: 8px;
}

.address-value {
  color: #333;
  font-size: 14px;
  line-height: 1.5;
  padding-left: 24px;
}

.remark-content {
  color: #333;
  font-size: 14px;
  line-height: 1.6;
  background: #f8f9fa;
  padding: 12px;
  border-radius: 8px;
}

.action-buttons {
  margin-top: 20px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 50vh;
}
</style>
