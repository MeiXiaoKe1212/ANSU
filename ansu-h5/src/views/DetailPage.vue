<template>
  <div class="detail-page">
    <t-navbar title="订单详情" fixed left-arrow @left-click="handleBack" />

    <div class="detail-container" v-if="!loading">
      <template v-if="order">
        <!-- 订单头部信息 -->
        <div class="order-header" :class="{ 'outsourced': order.isOutsourced, 'self-owned': !order.isOutsourced }">
          <div class="order-title">
            <h2>{{ order.orderNo }}</h2>
            <div class="order-meta">
              <span class="order-date">{{ formatDate(order.createTime) }}</span>
              <span class="order-type">{{ order.isOutsourced ? '外包' : '自有' }}</span>
            </div>
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
              <div class="cost-card" v-for="cost in costs" :key="cost.id">
                <div class="cost-header">
                  <div class="cost-name">{{ cost.costName }}</div>
                  <div class="cost-amount">¥{{ cost.amount }}</div>
                </div>
                <div class="cost-body">
                  <div class="cost-type">{{ getCostTypeName(cost.costType) }}</div>
                  <div class="cost-time">{{ formatDate(cost.costDate) }}</div>
                </div>
                <div class="cost-desc" v-if="cost.description">{{ cost.description }}</div>
                <div class="cost-actions">
                  <t-button size="small" theme="primary" variant="text" class="delete-btn" @click="deleteCost(cost.id)">
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
              <div class="event-card" v-for="event in events" :key="event.id" :class="{ 'resolved': event.isResolved }">
                <div class="event-header">
                  <div class="event-title">{{ event.eventTitle }}</div>
                  <div class="event-status">
                    <span class="event-type">{{ getEventTypeName(event.eventType) }}</span>
                    <span class="impact-badge" :class="getImpactLevelClass(event.impactLevel)">
                      {{ getImpactLevelName(event.impactLevel) }}
                    </span>
                  </div>
                </div>
                <div class="event-body">
                  <div class="event-time">{{ formatDateTime(event.eventTime) }}</div>
                  <div class="event-location" v-if="event.location">📍 {{ event.location }}</div>
                </div>
                <div class="event-desc" v-if="event.eventDescription">{{ event.eventDescription }}</div>

                <!-- 已解决的事件 -->
                <div v-if="event.isResolved" class="event-resolution">
                  <div class="resolution-status">✅ 已解决</div>
                  <div class="resolution-time">{{ formatDateTime(event.resolutionTime) }}</div>
                  <div class="resolution-desc" v-if="event.resolutionDescription">{{ event.resolutionDescription }}</div>
                </div>

                <!-- 未解决的事件操作 -->
                <div v-else class="event-actions">
                  <t-button size="small" theme="primary" variant="text" class="resolve-btn" @click="resolveEvent(event.id)">
                    标记解决
                  </t-button>
                  <t-button size="small" theme="primary" variant="text" class="delete-btn" @click="deleteEvent(event.id)">
                    删除
                  </t-button>
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
          <t-button theme="primary" block class="delete-order-btn" @click="handleDelete">删除订单</t-button>
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

    <!-- 添加成本表单 -->
    <add-cost-form
      v-model:visible="costFormVisible"
      :order-id="orderId"
      @submit="handleCostSubmit"
      @close="handleCostFormClose"
    />

    <!-- 添加事件表单 -->
    <add-event-form
      v-model:visible="eventFormVisible"
      :order-id="orderId"
      @submit="handleEventSubmit"
      @close="handleEventFormClose"
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
import AddCostForm from '../components/AddCostForm.vue'
import AddEventForm from '../components/AddEventForm.vue'

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
const costFormVisible = ref(false)
const eventFormVisible = ref(false)
const deleteConfirmVisible = ref(false)

// 获取订单详情
const fetchOrderDetail = async () => {
  try {
    loading.value = true
    const orderData = await orderStore.fetchOrderById(orderId.value)
    order.value = orderData

    // 获取成本列表并重新计算利润
    await fetchCostsAndCalculateProfit()

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

// 获取成本并重新计算利润
const fetchCostsAndCalculateProfit = async () => {
  try {
    // 强制从API获取最新成本数据，不使用缓存
    const costData = await costStore.fetchCostsByOrderId(orderId.value)
    costs.value = costData

    // 重新计算总成本
    const totalCost = costs.value.reduce((sum, cost) => sum + (parseFloat(cost.amount) || 0), 0)

    // 更新订单的成本和利润信息
    if (order.value) {
      order.value.totalCost = totalCost

      // 重新计算利润
      const actualPrice = parseFloat(order.value.actualPrice) || 0
      const profit = actualPrice - totalCost
      order.value.profit = profit

      // 重新计算利润率
      if (actualPrice > 0) {
        const profitRate = (profit / actualPrice * 100).toFixed(2)
        order.value.profitRate = parseFloat(profitRate)
      } else {
        order.value.profitRate = 0
      }
    }
  } catch (error) {
    console.error('获取成本数据失败:', error)
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
    await orderStore.updateTransportStatus(orderId.value, status, '手动更新状态')
    // 重新获取订单详情以确保数据同步
    await fetchOrderDetail()
    Toast({ message: '运输状态更新成功', theme: 'success' })
  } catch (error) {
    console.error('更新运输状态失败:', error)
    Toast({ message: error.message || '运输状态更新失败', theme: 'error' })
  }
}

// 更新款项状态
const updatePaymentStatus = async (status) => {
  try {
    await orderStore.updatePaymentStatus(orderId.value, status, '手动更新状态')
    // 重新获取订单详情以确保数据同步
    await fetchOrderDetail()
    Toast({ message: '款项状态更新成功', theme: 'success' })
  } catch (error) {
    console.error('更新款项状态失败:', error)
    Toast({ message: error.message || '款项状态更新失败', theme: 'error' })
  }
}

// 获取成本类型名称
const getCostTypeName = (type) => {
  return costStore.getCostTypeName(type)
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
  costFormVisible.value = true
}

// 处理成本提交
const handleCostSubmit = async (formData) => {
  try {
    await costStore.addCost(formData)
    // 重新获取成本并计算利润
    await fetchCostsAndCalculateProfit()
    Toast({ message: '成本添加成功', theme: 'success' })
  } catch (error) {
    Toast({ message: error.message || '添加失败', theme: 'error' })
  }
}

// 处理成本表单关闭
const handleCostFormClose = () => {
  costFormVisible.value = false
}

// 显示添加事件表单
const showAddEventForm = () => {
  eventFormVisible.value = true
}

// 处理事件提交
const handleEventSubmit = async (formData) => {
  try {
    await eventStore.addEvent(formData)
    // 重新获取事件列表
    const eventData = await eventStore.fetchEventsByOrderId(orderId.value)
    events.value = eventData
    Toast({ message: '事件添加成功', theme: 'success' })
  } catch (error) {
    Toast({ message: error.message || '添加失败', theme: 'error' })
  }
}

// 处理事件表单关闭
const handleEventFormClose = () => {
  eventFormVisible.value = false
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
    // 重新获取成本并计算利润
    await fetchCostsAndCalculateProfit()
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
  color: white;
  padding: 16px;
  border-radius: 12px;
  margin-bottom: 16px;
  position: relative;
}

/* 自有订单背景 */
.order-header.self-owned {
  background: linear-gradient(135deg, #0052d9, #1890ff);
}

/* 外包订单背景 */
.order-header.outsourced {
  background: linear-gradient(135deg, #ff7a45, #ffa940);
}

.order-title {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.order-title h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.order-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.order-date {
  font-size: 12px;
  opacity: 0.8;
}

.order-type {
  font-size: 12px;
  font-weight: 600;
  background-color: rgba(255, 255, 255, 0.2);
  padding: 2px 8px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
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
  max-height: 400px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.cost-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #f0f0f0;
  transition: box-shadow 0.2s ease;
}

.cost-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.cost-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.cost-name {
  font-weight: 600;
  color: #333;
  font-size: 16px;
}

.cost-amount {
  font-weight: 700;
  color: #ff6b35;
  font-size: 18px;
}

.cost-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.cost-type {
  font-size: 12px;
  color: #666;
  background-color: #f5f7fa;
  padding: 4px 8px;
  border-radius: 4px;
}

.cost-time {
  font-size: 12px;
  color: #999;
}

.cost-desc {
  font-size: 13px;
  color: #666;
  line-height: 1.4;
  margin-bottom: 8px;
  background-color: #f8f9fa;
  padding: 8px;
  border-radius: 4px;
}

.cost-actions {
  display: flex;
  justify-content: flex-end;
}

/* 事件列表 */
.event-list {
  max-height: 500px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.event-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid #f0f0f0;
  transition: box-shadow 0.2s ease;
}

.event-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.event-card.resolved {
  border-left: 4px solid #52c41a;
  background-color: #f6ffed;
}

.event-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.event-title {
  font-weight: 600;
  color: #333;
  font-size: 16px;
  flex: 1;
}

.event-status {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.event-type {
  font-size: 12px;
  color: #666;
  background-color: #f5f7fa;
  padding: 4px 8px;
  border-radius: 4px;
}

.impact-badge {
  font-size: 11px;
  padding: 4px 8px;
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

.event-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.event-time {
  font-size: 12px;
  color: #999;
}

.event-location {
  font-size: 12px;
  color: #0052d9;
  font-weight: 500;
}

.event-desc {
  font-size: 13px;
  color: #666;
  line-height: 1.4;
  margin-bottom: 12px;
  background-color: #f8f9fa;
  padding: 8px;
  border-radius: 4px;
}

.event-resolution {
  background-color: #f0f9ff;
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 8px;
  border-left: 3px solid #52c41a;
}

.resolution-status {
  color: #2e7d32;
  font-weight: 600;
  font-size: 14px;
  margin-bottom: 4px;
}

.resolution-time {
  color: #666;
  font-size: 12px;
  margin-bottom: 4px;
}

.resolution-desc {
  color: #666;
  font-size: 13px;
  line-height: 1.4;
}

.event-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
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

/* 按钮颜色样式 */
.delete-btn {
  color: #ff4d4f !important;
}

.resolve-btn {
  color: #52c41a !important;
}

.delete-order-btn {
  background-color: #ff4d4f !important;
  border-color: #ff4d4f !important;
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