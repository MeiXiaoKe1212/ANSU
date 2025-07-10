<template>
  <div class="detail-page">
    <t-navbar title="订单详情" fixed left-arrow @left-click="handleBack" />

    <div class="detail-container" v-if="!loading">
      <template v-if="order">
        <!-- 订单头部信息 -->
        <div class="order-header">
          <div class="order-title">
            <h2>{{ order.orderNo }}</h2>
            <span class="order-date">{{ formatDate(order.createTime) }}</span>
          </div>
          <div class="order-customer">{{ order.customerCompanyName }}</div>
          <div class="status-row">
            <span class="status-badge transport" :class="getTransportStatusClass(order.transportStatus)">
              运输: {{ getTransportStatusText(order.transportStatus) }}
            </span>
            <span class="status-badge payment" :class="getPaymentStatusClass(order.paymentStatus)">
              款项: {{ getPaymentStatusText(order.paymentStatus) }}
            </span>
          </div>
        </div>

        <!-- 基本信息区域 -->
        <div class="info-section">
          <h3 class="section-title">基本信息</h3>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">联系人:</span>
              <span class="value">{{ order.customerContactPerson }}</span>
            </div>
            <div class="info-item">
              <span class="label">电话:</span>
              <span class="value">{{ order.customerContactPhone }}</span>
            </div>
            <div class="info-item full-width">
              <span class="label">起点:</span>
              <span class="value">{{ order.startAddress }}</span>
            </div>
            <div class="info-item full-width">
              <span class="label">终点:</span>
              <span class="value">{{ order.endAddress }}</span>
            </div>
            <div class="info-item">
              <span class="label">货物:</span>
              <span class="value">{{ order.cargoName }}</span>
            </div>
            <div class="info-item">
              <span class="label">重量:</span>
              <span class="value">{{ order.cargoWeight || '-' }}吨</span>
            </div>

            <!-- 车辆信息（仅自有车辆） -->
            <template v-if="!order.isOutsourced">
              <div class="info-item">
                <span class="label">车牌:</span>
                <span class="value">{{ order.licensePlate || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="label">司机:</span>
                <span class="value">{{ order.driverName || '-' }}</span>
              </div>
            </template>

            <!-- 外包信息 -->
            <template v-if="order.isOutsourced">
              <div class="info-item">
                <span class="label">平台:</span>
                <span class="value">{{ order.thirdPartyPlatform || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="label">外包单号:</span>
                <span class="value">{{ order.thirdPartyOrderId || '-' }}</span>
              </div>
            </template>
          </div>
        </div>

        <!-- 价格信息区域 -->
        <div class="info-section">
          <h3 class="section-title">价格信息</h3>
          <div class="price-grid">
            <div class="price-item">
              <span class="label">报价:</span>
              <span class="value price">¥{{ order.quotedPrice || 0 }}</span>
            </div>
            <div class="price-item">
              <span class="label">实收:</span>
              <span class="value price">¥{{ order.actualPrice || 0 }}</span>
            </div>
            <div class="price-item">
              <span class="label">成本:</span>
              <span class="value cost">¥{{ order.totalCost || 0 }}</span>
            </div>
            <div class="price-item">
              <span class="label">利润:</span>
              <span class="value" :class="{ 'profit': (order.profit || 0) > 0, 'loss': (order.profit || 0) < 0 }">
                ¥{{ order.profit || 0 }}
              </span>
            </div>
          </div>
        </div>

        <!-- 成本明细区域 -->
        <div class="info-section">
          <div class="section-header">
            <h3 class="section-title">成本明细</h3>
            <t-button size="small" theme="primary" variant="text" @click="showAddCostForm">
              添加成本
            </t-button>
          </div>
          <div class="cost-list">
            <div v-if="costs && costs.length > 0">
              <div class="cost-item" v-for="cost in costs" :key="cost.id">
                <div class="cost-main">
                  <span class="cost-name">{{ cost.costName }}</span>
                  <span class="cost-amount">¥{{ cost.amount }}</span>
                </div>
                <div class="cost-detail">
                  <span class="cost-time">{{ formatDateTime(cost.createTime) }}</span>
                  <span class="cost-desc" v-if="cost.description">{{ cost.description }}</span>
                  <t-button size="small" theme="danger" variant="text" @click="deleteCost(cost.id)">
                    删除
                  </t-button>
                </div>
              </div>
            </div>
            <div v-else class="empty-state">
              暂无成本记录
            </div>
          </div>
        </div>

        <!-- 运输事件区域 -->
        <div class="info-section">
          <div class="section-header">
            <h3 class="section-title">运输事件</h3>
            <t-button size="small" theme="primary" variant="text" @click="showAddEventForm">
              添加事件
            </t-button>
          </div>
          <div class="event-list">
            <div v-if="events && events.length > 0">
              <div class="event-item" v-for="event in events" :key="event.id">
                <div class="event-header">
                  <span class="event-title">{{ event.eventTitle }}</span>
                  <span class="event-type">{{ getEventTypeName(event.eventType) }}</span>
                  <span class="impact-badge" :class="getImpactLevelClass(event.impactLevel)">
                    {{ getImpactLevelName(event.impactLevel) }}
                  </span>
                </div>
                <div class="event-detail">
                  <div class="event-time">{{ formatDateTime(event.eventTime) }}</div>
                  <div class="event-location" v-if="event.location">📍 {{ event.location }}</div>
                  <div class="event-desc" v-if="event.eventDescription">{{ event.eventDescription }}</div>

                  <!-- 已解决的事件 -->
                  <div v-if="event.isResolved" class="event-resolution">
                    <div class="resolution-time">✅ {{ formatDateTime(event.resolutionTime) }} 已解决</div>
                    <div class="resolution-desc" v-if="event.resolutionDescription">{{ event.resolutionDescription }}</div>
                  </div>

                  <!-- 未解决的事件操作 -->
                  <div v-else class="event-actions">
                    <t-button size="small" theme="success" variant="text" @click="resolveEvent(event.id)">
                      标记解决
                    </t-button>
                    <t-button size="small" theme="danger" variant="text" @click="deleteEvent(event.id)">
                      删除
                    </t-button>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="empty-state">
              暂无运输事件
            </div>
          </div>
        </div>

        <!-- 备注信息 -->
        <div class="info-section" v-if="order.remarks">
          <h3 class="section-title">备注信息</h3>
          <div class="remarks-content">{{ order.remarks }}</div>
        </div>

        <!-- 时间信息 -->
        <div class="info-section" v-if="order.departureTime || order.arrivalTime">
          <h3 class="section-title">时间信息</h3>
          <div class="time-grid">
            <div class="time-item" v-if="order.departureTime">
              <span class="label">出发:</span>
              <span class="value">{{ formatDateTime(order.departureTime) }}</span>
            </div>
            <div class="time-item" v-if="order.arrivalTime">
              <span class="label">到达:</span>
              <span class="value">{{ formatDateTime(order.arrivalTime) }}</span>
            </div>
          </div>
        </div>

        <!-- 状态操作区域 -->
        <div class="status-actions">
          <div class="action-group">
            <h4>运输状态</h4>
            <div class="status-buttons">
              <t-button
                size="small"
                :theme="order.transportStatus === 'CREATED' ? 'primary' : 'default'"
                @click="updateTransportStatus('CREATED')"
              >
                已创建
              </t-button>
              <t-button
                size="small"
                :theme="order.transportStatus === 'DEPARTED' ? 'primary' : 'default'"
                @click="updateTransportStatus('DEPARTED')"
              >
                已出发
              </t-button>
              <t-button
                size="small"
                :theme="order.transportStatus === 'TRANSPORTING' ? 'primary' : 'default'"
                @click="updateTransportStatus('TRANSPORTING')"
              >
                运输中
              </t-button>
              <t-button
                size="small"
                :theme="order.transportStatus === 'DELIVERED' ? 'primary' : 'default'"
                @click="updateTransportStatus('DELIVERED')"
              >
                已送达
              </t-button>
            </div>
          </div>

          <div class="action-group">
            <h4>款项状态</h4>
            <div class="status-buttons">
              <t-button
                size="small"
                :theme="order.paymentStatus === 'UNPAID' ? 'primary' : 'default'"
                @click="updatePaymentStatus('UNPAID')"
              >
                未付款
              </t-button>
              <t-button
                size="small"
                :theme="order.paymentStatus === 'PREPAID' ? 'primary' : 'default'"
                @click="updatePaymentStatus('PREPAID')"
              >
                已预付费
              </t-button>
              <t-button
                size="small"
                :theme="order.paymentStatus === 'PAID' ? 'primary' : 'default'"
                @click="updatePaymentStatus('PAID')"
              >
                已结款
              </t-button>
            </div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <t-button theme="primary" block @click="handleEdit">编辑订单</t-button>
          <t-button theme="danger" block @click="handleDelete">删除订单</t-button>
        </div>
      </template>

      <div v-else class="not-found">
        <t-empty description="找不到订单信息" />
        <t-button theme="primary" @click="handleBack">返回列表</t-button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-else class="loading-container">
      <t-loading text="加载中..." />
    </div>

    <!-- 订单表单 -->
    <transport-order-form
      v-model:visible="orderFormVisible"
      :order-data="currentOrderData"
      @submit="handleOrderSubmit"
      @close="handleFormClose"
    />

    <!-- 删除确认对话框 -->
    <t-dialog
      v-model="deleteConfirmVisible"
      title="确认删除"
      content="确定要删除此订单吗？此操作不可撤销。"
      :confirm-btn="{ content: '删除', theme: 'danger' }"
      @confirm="confirmDelete"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useTransportOrderStore } from '../stores/transportOrderStore'
import { useOrderCostStore } from '../stores/orderCostStore'
import { useOrderEventStore } from '../stores/orderEventStore'
import { Toast } from 'tdesign-mobile-vue'
import TransportOrderForm from '../components/TransportOrderForm.vue'

const router = useRouter()
const route = useRoute()
const orderStore = useTransportOrderStore()
const costStore = useOrderCostStore()
const eventStore = useOrderEventStore()

// 订单ID
const orderId = computed(() => route.params.id)

// 响应式数据
const order = ref(null)
const costs = ref([])
const events = ref([])
const loading = ref(true)

// 表单控制
const orderFormVisible = ref(false)
const currentOrderData = ref({})
const deleteConfirmVisible = ref(false)

// 获取订单详情
const fetchOrderDetail = async () => {
  try {
    loading.value = true
    const orderData = await orderStore.fetchOrderById(orderId.value)
    order.value = orderData

    // 获取成本列表
    const costData = await costStore.fetchCostsByOrderId(orderId.value)
    costs.value = costData

    // 获取事件列表
    const eventData = await eventStore.fetchEventsByOrderId(orderId.value)
    events.value = eventData
  } catch (error) {
    Toast({ message: error.message || '获取订单详情失败', theme: 'error' })
    router.replace('/list')
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 格式化日期时间
const formatDateTime = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${formatDate(dateString)} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
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

// 返回上一页
const handleBack = () => {
  router.back()
}

// 更新运输状态
const updateTransportStatus = async (status) => {
  try {
    const success = await orderStore.updateTransportStatus(orderId.value, status, '手动更新状态')
    if (success) {
      order.value.transportStatus = status
      Toast({ message: '运输状态更新成功', theme: 'success' })
    } else {
      Toast({ message: '运输状态更新失败', theme: 'error' })
    }
  } catch (error) {
    console.error('更新运输状态失败:', error)
    Toast({ message: error.message || '运输状态更新失败', theme: 'error' })
  }
}

// 更新款项状态
const updatePaymentStatus = async (status) => {
  try {
    const success = await orderStore.updatePaymentStatus(orderId.value, status, '手动更新状态')
    if (success) {
      order.value.paymentStatus = status
      Toast({ message: '款项状态更新成功', theme: 'success' })
    } else {
      Toast({ message: '款项状态更新失败', theme: 'error' })
    }
  } catch (error) {
    console.error('更新款项状态失败:', error)
    Toast({ message: error.message || '款项状态更新失败', theme: 'error' })
  }
}

// 获取事件类型名称
const getEventTypeName = (type) => {
  return eventStore.getEventTypeName(type)
}

// 获取影响程度名称
const getImpactLevelName = (level) => {
  return eventStore.getImpactLevelName(level)
}

// 获取影响程度样式
const getImpactLevelClass = (level) => {
  return eventStore.getImpactLevelClass(level)
}

// 显示添加成本表单
const showAddCostForm = () => {
  // TODO: 实现添加成本表单
  Toast({ message: '添加成本功能开发中', theme: 'warning' })
}

// 显示添加事件表单
const showAddEventForm = () => {
  // TODO: 实现添加事件表单
  Toast({ message: '添加事件功能开发中', theme: 'warning' })
}

// 解决事件
const resolveEvent = async (eventId) => {
  // 简化版本，直接标记为已解决
  try {
    await eventStore.resolveEvent(eventId, '问题已解决')
    Toast({ message: '事件已标记为解决', theme: 'success' })
  } catch (error) {
    Toast({ message: error.message || '操作失败', theme: 'error' })
  }
}

// 删除事件
const deleteEvent = async (eventId) => {
  try {
    await eventStore.deleteEvent(eventId)
    events.value = events.value.filter(event => event.id !== eventId)
    Toast({ message: '事件删除成功', theme: 'success' })
  } catch (error) {
    Toast({ message: error.message || '删除失败', theme: 'error' })
  }
}

// 删除成本
const deleteCost = async (costId) => {
  try {
    await costStore.deleteCost(costId, orderId.value)
    costs.value = costs.value.filter(cost => cost.id !== costId)
    // 重新获取订单信息以更新利润计算
    await fetchOrderDetail()
    Toast({ message: '成本删除成功', theme: 'success' })
  } catch (error) {
    Toast({ message: error.message || '删除失败', theme: 'error' })
  }
}

// 编辑订单
const handleEdit = () => {
  currentOrderData.value = { ...order.value }
  orderFormVisible.value = true
}

// 处理订单提交
const handleOrderSubmit = async (formData) => {
  try {
    await orderStore.updateOrder(orderId.value, formData)
    await fetchOrderDetail() // 重新获取最新数据
    Toast({ message: '订单更新成功', theme: 'success' })
  } catch (error) {
    Toast({ message: error.message || '更新失败', theme: 'error' })
  }
}

// 处理表单关闭
const handleFormClose = () => {
  currentOrderData.value = {}
}

// 删除订单
const handleDelete = () => {
  deleteConfirmVisible.value = true
}

// 确认删除
const confirmDelete = async () => {
  try {
    await orderStore.deleteOrder(orderId.value)
    Toast({ message: '订单已删除', theme: 'success' })
    router.replace('/list')
  } catch (error) {
    Toast({ message: error.message || '删除失败', theme: 'error' })
  }
}

// 组件挂载时获取订单详情
onMounted(() => {
  if (orderId.value) {
    fetchOrderDetail()
  } else {
    Toast({ message: '订单ID无效', theme: 'error' })
    router.replace('/list')
  }
})
</script>

<style scoped>
.detail-page {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.detail-container {
  padding: 12px;
  padding-bottom: 100px;
  padding-top: 56px; /* 为固定导航栏留出空间 */
}

/* 订单头部 */
.order-header {
  background: linear-gradient(135deg, #0052d9, #1890ff);
  color: white;
  padding: 16px;
  border-radius: 12px;
  margin-bottom: 16px;
}

.order-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.order-title h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.order-date {
  font-size: 12px;
  opacity: 0.8;
}

.order-customer {
  font-size: 16px;
  margin-bottom: 12px;
  opacity: 0.9;
}

.status-row {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

/* 信息区域 */
.info-section {
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
}

.section-title {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  border-left: 3px solid #0052d9;
  padding-left: 8px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-header .section-title {
  margin: 0;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-item .label {
  font-size: 12px;
  color: #666;
}

.info-item .value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* 价格网格 */
.price-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.price-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background-color: #f8f9fa;
  border-radius: 6px;
}

.price-item .label {
  font-size: 12px;
  color: #666;
}

.price-item .value {
  font-size: 14px;
  font-weight: 600;
}

.price-item .value.price {
  color: #0052d9;
}

.price-item .value.cost {
  color: #ff6b35;
}

.price-item .value.profit {
  color: #52c41a;
}

.price-item .value.loss {
  color: #ff4d4f;
}

.status-badges {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  text-align: center;
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

/* 成本列表 */
.cost-list {
  max-height: 300px;
  overflow-y: auto;
}

.cost-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.cost-item:last-child {
  border-bottom: none;
}

.cost-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.cost-name {
  font-weight: 500;
  color: #333;
}

.cost-amount {
  font-weight: 600;
  color: #ff6b35;
}

.cost-detail {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #666;
}

.cost-time {
  flex: 1;
}

.cost-desc {
  flex: 2;
  margin: 0 8px;
}

/* 事件列表 */
.event-list {
  max-height: 400px;
  overflow-y: auto;
}

.event-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.event-item:last-child {
  border-bottom: none;
}

.event-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.event-title {
  font-weight: 500;
  color: #333;
}

.event-type {
  font-size: 12px;
  color: #666;
  background-color: #f0f0f0;
  padding: 2px 6px;
  border-radius: 4px;
}

.impact-badge {
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: 500;
}

.impact-low {
  background-color: #e8f5e8;
  color: #2e7d32;
}

.impact-medium {
  background-color: #fff3e0;
  color: #ef6c00;
}

.impact-high {
  background-color: #ffebee;
  color: #c62828;
}

.event-detail {
  font-size: 12px;
  color: #666;
  line-height: 1.4;
}

.event-time {
  margin-bottom: 4px;
}

.event-location {
  margin-bottom: 4px;
  color: #0052d9;
}

.event-desc {
  margin-bottom: 8px;
  color: #333;
}

.event-resolution {
  background-color: #f0f9ff;
  padding: 8px;
  border-radius: 4px;
  margin-top: 8px;
}

.resolution-time {
  color: #2e7d32;
  font-weight: 500;
  margin-bottom: 4px;
}

.resolution-desc {
  color: #666;
}

.event-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

/* 备注内容 */
.remarks-content {
  background-color: #f8f9fa;
  padding: 12px;
  border-radius: 6px;
  color: #333;
  line-height: 1.5;
}

/* 时间网格 */
.time-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.time-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 8px 12px;
  background-color: #f8f9fa;
  border-radius: 6px;
}

.time-item .label {
  font-size: 12px;
  color: #666;
}

.time-item .value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

/* 空状态 */
.empty-state {
  text-align: center;
  color: #999;
  padding: 20px;
  font-size: 14px;
}

.costs .cost-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 12px 0;
  border-bottom: 1px dashed #eee;
}

.cost-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.cost-name {
  font-weight: 500;
  color: #333;
}

.cost-date {
  font-size: 12px;
  color: #999;
}

.cost-desc {
  font-size: 12px;
  color: #666;
}

.cost-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.cost-value {
  font-weight: 600;
  color: #ff6b35;
}

.empty-costs {
  padding: 10px 0;
  color: #999;
  text-align: center;
}

.total-cost {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.total-value {
  font-size: 20px;
  font-weight: bold;
  color: #0052d9;
}

.remarks {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 20px;
}

.status-actions {
  background-color: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.action-group {
  margin-bottom: 20px;
}

.action-group:last-child {
  margin-bottom: 0;
}

.action-group h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.status-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 50vh;
}

.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 0;
  gap: 20px;
}
</style> 