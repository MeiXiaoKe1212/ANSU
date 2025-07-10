<template>
  <div>
    <t-navbar title="订单列表" :fixed="false"/>

    <div class="list-container">
      <div class="search-section">
        <div class="search-box">
          <input
            type="text"
            v-model="searchText"
            placeholder="搜索订单..."
            @input="handleSearch"
          />
        </div>
        <t-button class="add-button" theme="primary" :icon="addIcon" @click="showAddOrderForm">新增</t-button>
      </div>

      <!-- 状态筛选 -->
      <div class="filter-section">
        <div class="filter-group">
          <label>运输状态:</label>
          <t-select v-model="transportStatusFilter" placeholder="全部状态" clearable>
            <t-option value="CREATED" label="已创建" />
            <t-option value="DEPARTED" label="已出发" />
            <t-option value="TRANSPORTING" label="运输中" />
            <t-option value="EXCEPTION" label="异常" />
            <t-option value="DELIVERED" label="已送达" />
          </t-select>
        </div>
        <div class="filter-group">
          <label>款项状态:</label>
          <t-select v-model="paymentStatusFilter" placeholder="全部状态" clearable>
            <t-option value="UNPAID" label="未付款" />
            <t-option value="PREPAID" label="已预付费" />
            <t-option value="PAID" label="已结款" />
          </t-select>
        </div>
      </div>

      <div class="list-items">
        <div
          v-for="order in filteredOrders"
          :key="order.id"
          class="list-item"
          @click="viewOrderDetail(order)"
        >
          <div class="item-left">
            <div class="item-title">{{ order.orderNo }}</div>
            <div class="item-subtitle">{{ order.customerCompanyName }}</div>
            <div class="item-desc">
              {{ order.startAddress }} → {{ order.endAddress }}
              <div class="cargo-info">
                {{ order.cargoName }}
                <span v-if="order.cargoWeight > 0">{{ order.cargoWeight }}吨</span>
              </div>
            </div>
          </div>
          <div class="item-right">
            <div class="item-time">{{ formatDate(order.createTime) }}</div>
            <div class="status-badges">
              <span class="status-badge transport" :class="getTransportStatusClass(order.transportStatus)">
                {{ getTransportStatusText(order.transportStatus) }}
              </span>
              <span class="status-badge payment" :class="getPaymentStatusClass(order.paymentStatus)">
                {{ getPaymentStatusText(order.paymentStatus) }}
              </span>
            </div>
            <div class="item-status">
              <span class="order-type">{{ order.isOutsourced === 1 ? '外包' : '自有' }}</span>
              <span class="order-amount">¥{{ order.actualPrice || order.quotedPrice || 0 }}</span>
            </div>
          </div>
        </div>

        <div v-if="filteredOrders.length === 0" class="empty-state">
          还没有订单记录，点击上方"新增"按钮添加
        </div>
      </div>
    </div>

    <!-- 运输订单表单 -->
    <transport-order-form
      v-model:visible="orderFormVisible"
      :order-data="currentOrderData"
      @submit="handleOrderSubmit"
      @close="handleFormClose"
    />
  </div>
</template>

<script setup>
import { h, ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Toast } from 'tdesign-mobile-vue'
import { useTransportOrderStore } from '../stores/transportOrderStore'
import TransportOrderForm from '../components/TransportOrderForm.vue'
import { AddIcon } from 'tdesign-icons-vue-next'

const router = useRouter()
const orderStore = useTransportOrderStore()
const searchText = ref('')
const transportStatusFilter = ref('')
const paymentStatusFilter = ref('')
const addIcon = () => h(AddIcon, { size: '18px' })

// 订单表单控制
const orderFormVisible = ref(false)
const currentOrderData = ref({})

// 过滤订单列表
const filteredOrders = computed(() => {
  let orders = orderStore.orders

  // 搜索过滤
  if (searchText.value) {
    const searchLower = searchText.value.toLowerCase()
    orders = orders.filter(order => {
      return (
        (order.orderNo && order.orderNo.toLowerCase().includes(searchLower)) ||
        (order.customerCompanyName && order.customerCompanyName.toLowerCase().includes(searchLower)) ||
        (order.startAddress && order.startAddress.toLowerCase().includes(searchLower)) ||
        (order.endAddress && order.endAddress.toLowerCase().includes(searchLower)) ||
        (order.cargoName && order.cargoName.toLowerCase().includes(searchLower)) ||
        (order.licensePlate && order.licensePlate.toLowerCase().includes(searchLower)) ||
        (order.driverName && order.driverName.toLowerCase().includes(searchLower))
      )
    })
  }

  // 运输状态过滤
  if (transportStatusFilter.value) {
    orders = orders.filter(order => order.transportStatus === transportStatusFilter.value)
  }

  // 款项状态过滤
  if (paymentStatusFilter.value) {
    orders = orders.filter(order => order.paymentStatus === paymentStatusFilter.value)
  }

  return orders
})

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 获取运输状态样式
const getTransportStatusClass = (status) => {
  const statusMap = {
    'CREATED': 'status-created',
    'DEPARTED': 'status-departed',
    'TRANSPORTING': 'status-transporting',
    'EXCEPTION': 'status-exception',
    'DELIVERED': 'status-delivered'
  }
  return statusMap[status] || ''
}

// 获取运输状态文本
const getTransportStatusText = (status) => {
  const statusMap = {
    'CREATED': '已创建',
    'DEPARTED': '已出发',
    'TRANSPORTING': '运输中',
    'EXCEPTION': '异常',
    'DELIVERED': '已送达'
  }
  return statusMap[status] || status
}

// 获取款项状态样式
const getPaymentStatusClass = (status) => {
  const statusMap = {
    'UNPAID': 'payment-unpaid',
    'PREPAID': 'payment-prepaid',
    'PAID': 'payment-paid'
  }
  return statusMap[status] || ''
}

// 获取款项状态文本
const getPaymentStatusText = (status) => {
  const statusMap = {
    'UNPAID': '未付款',
    'PREPAID': '已预付费',
    'PAID': '已结款'
  }
  return statusMap[status] || status
}

// 搜索处理
const handleSearch = () => {
  // 实际项目中可能需要防抖处理
}

// 查看订单详情
const viewOrderDetail = (order) => {
  router.push(`/detail/${order.id}`)
}

// 显示添加订单表单
const showAddOrderForm = () => {
  currentOrderData.value = {}
  orderFormVisible.value = true
}

// 处理订单提交
const handleOrderSubmit = async (formData) => {
  try {
    await orderStore.createOrder(formData)
    Toast({ message: '订单创建成功', theme: 'success' })
  } catch (error) {
    Toast({ message: error.message || '创建失败', theme: 'error' })
  }
}

// 处理表单关闭
const handleFormClose = () => {
  currentOrderData.value = {}
}

// 组件挂载时获取订单列表
onMounted(() => {
  orderStore.fetchOrders().catch(error => {
    console.error('获取订单列表失败:', error)
  })
})
</script>

<style scoped>
.list-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 15px;
  padding-bottom: 80px; /* 为悬浮按钮留出空间 */
}

.search-section {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.search-box {
  flex: 1;
}

.search-box input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.add-button {
  margin-left: 10px;
}

.filter-section {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 140px;
}

.filter-group label {
  font-size: 14px;
  color: #666;
  white-space: nowrap;
}

.list-items {
  flex: 1;
}

.list-item {
  display: flex;
  justify-content: space-between;
  padding: 15px;
  background-color: #fff;
  border-radius: 8px;
  margin-bottom: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s;
}

.list-item:hover {
  transform: translateY(-2px);
}

.item-left {
  flex: 1;
}

.item-title {
  font-weight: 600;
  margin-bottom: 4px;
  color: #333;
  font-size: 16px;
}

.item-subtitle {
  font-size: 14px;
  color: #0052d9;
  margin-bottom: 4px;
  font-weight: 500;
}

.item-desc {
  font-size: 14px;
  color: #666;
}

.cargo-info {
  font-size: 12px;
  color: #888;
  margin-top: 3px;
}

.item-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  padding-left: 15px;
}

.item-time {
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
}

.status-badges {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-bottom: 8px;
}

.status-badge {
  padding: 2px 6px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 500;
  text-align: center;
  min-width: 50px;
}

/* 运输状态样式 */
.status-created {
  background-color: #f0f0f0;
  color: #666;
}

.status-departed {
  background-color: #e3f2fd;
  color: #1565c0;
}

.status-transporting {
  background-color: #fff3e0;
  color: #ef6c00;
}

.status-exception {
  background-color: #ffebee;
  color: #c62828;
}

.status-delivered {
  background-color: #e8f5e8;
  color: #2e7d32;
}

/* 款项状态样式 */
.payment-unpaid {
  background-color: #ffebee;
  color: #c62828;
}

.payment-prepaid {
  background-color: #fff3e0;
  color: #ef6c00;
}

.payment-paid {
  background-color: #e8f5e8;
  color: #2e7d32;
}

.item-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  gap: 8px;
}

.order-type {
  padding: 2px 6px;
  border-radius: 10px;
  font-weight: 500;
  background-color: #f5f5f5;
  color: #666;
}

.order-amount {
  font-weight: bold;
  font-size: 14px;
  color: #0052d9;
}

.empty-state {
  text-align: center;
  padding: 30px;
  color: #999;
  background-color: #fff;
  border-radius: 8px;
}
</style> 