<template>
  <div class="detail-page">
    <t-navbar title="订单详情" fixed left-arrow @left-click="handleBack" />

    <div class="detail-container" v-if="!loading">
      <template v-if="order">
        <!-- 订单标题卡片 -->
        <div class="detail-card order-header-card">
          <h2 class="detail-title">{{ order.orderNo }}</h2>
          <div class="detail-subtitle">{{ order.customerCompanyName }}</div>
          <div class="detail-header">
            <div class="status-badges">
              <span class="status-badge transport" :class="getTransportStatusClass(order.transportStatus)">
                运输: {{ getTransportStatusText(order.transportStatus) }}
              </span>
              <span class="status-badge payment" :class="getPaymentStatusClass(order.paymentStatus)">
                款项: {{ getPaymentStatusText(order.paymentStatus) }}
              </span>
            </div>
            <span class="order-date">{{ formatDate(order.createTime) }}</span>
          </div>
        </div>

        <!-- 客户信息卡片 -->
        <div class="detail-card">
          <div class="card-title">
            <t-icon name="user" size="20px" />
            <span>客户信息</span>
          </div>
          <div class="detail-section">
            <div class="detail-item">
              <div class="item-label">企业名称</div>
              <div class="item-value">{{ order.customerCompanyName }}</div>
            </div>
            <div class="detail-item">
              <div class="item-label">联系人</div>
              <div class="item-value">{{ order.customerContactPerson }}</div>
            </div>
            <div class="detail-item">
              <div class="item-label">联系电话</div>
              <div class="item-value">{{ order.customerContactPhone }}</div>
            </div>
          </div>
        </div>

        <!-- 运输信息卡片 -->
        <div class="detail-card">
          <div class="card-title">
            <t-icon name="swap" size="20px" />
            <span>运输信息</span>
          </div>
          <div class="detail-section">
            <div class="detail-item">
              <div class="item-label">起点</div>
              <div class="item-value">{{ order.startAddress }}</div>
            </div>
            <div class="detail-item">
              <div class="item-label">终点</div>
              <div class="item-value">{{ order.endAddress }}</div>
            </div>
            <div class="detail-item">
              <div class="item-label">货物名称</div>
              <div class="item-value">{{ order.cargoName }}</div>
            </div>
            <div class="detail-item" v-if="order.cargoWeight">
              <div class="item-label">货物重量</div>
              <div class="item-value">{{ order.cargoWeight }}吨</div>
            </div>
            <div class="detail-item" v-if="order.cargoVolume">
              <div class="item-label">货物体积</div>
              <div class="item-value">{{ order.cargoVolume }}m³</div>
            </div>
          </div>
        </div>

        <!-- 车辆信息卡片 -->
        <div class="detail-card" v-if="!order.isOutsourced">
          <div class="card-title">
            <t-icon name="car" size="20px" />
            <span>车辆信息</span>
          </div>
          <div class="detail-section">
            <div class="detail-item" v-if="order.licensePlate">
              <div class="item-label">车牌号</div>
              <div class="item-value">{{ order.licensePlate }}</div>
            </div>
            <div class="detail-item" v-if="order.vehicleType">
              <div class="item-label">车辆类型</div>
              <div class="item-value">{{ order.vehicleType }}</div>
            </div>
            <div class="detail-item" v-if="order.vehicleSpec">
              <div class="item-label">车辆规格</div>
              <div class="item-value">{{ order.vehicleSpec }}</div>
            </div>
          </div>
        </div>

        <!-- 司机信息卡片 -->
        <div class="detail-card" v-if="!order.isOutsourced && (order.driverName || order.driverPhone)">
          <div class="card-title">
            <t-icon name="user-circle" size="20px" />
            <span>司机信息</span>
          </div>
          <div class="detail-section">
            <div class="detail-item" v-if="order.driverName">
              <div class="item-label">司机姓名</div>
              <div class="item-value">{{ order.driverName }}</div>
            </div>
            <div class="detail-item" v-if="order.driverPhone">
              <div class="item-label">联系电话</div>
              <div class="item-value">{{ order.driverPhone }}</div>
            </div>
          </div>
        </div>

        <!-- 外包信息卡片 -->
        <div class="detail-card" v-if="order.isOutsourced">
          <div class="card-title">
            <t-icon name="share" size="20px" />
            <span>外包信息</span>
          </div>
          <div class="detail-section">
            <div class="detail-item" v-if="order.thirdPartyPlatform">
              <div class="item-label">第三方平台</div>
              <div class="item-value">{{ order.thirdPartyPlatform }}</div>
            </div>
            <div class="detail-item" v-if="order.thirdPartyOrderId">
              <div class="item-label">第三方订单号</div>
              <div class="item-value">{{ order.thirdPartyOrderId }}</div>
            </div>
          </div>
        </div>

        <!-- 价格信息卡片 -->
        <div class="detail-card">
          <div class="card-title">
            <t-icon name="money-circle" size="20px" />
            <span>价格信息</span>
          </div>
          <div class="detail-section">
            <div class="detail-item" v-if="order.quotedPrice">
              <div class="item-label">报价金额</div>
              <div class="item-value price">¥{{ order.quotedPrice }}</div>
            </div>
            <div class="detail-item" v-if="order.actualPrice">
              <div class="item-label">实际收款</div>
              <div class="item-value price">¥{{ order.actualPrice }}</div>
            </div>
            <div class="detail-item">
              <div class="item-label">总成本</div>
              <div class="item-value cost">¥{{ order.totalCost || 0 }}</div>
            </div>
            <div class="detail-item">
              <div class="item-label">利润</div>
              <div class="item-value" :class="{ 'profit': (order.profit || 0) > 0, 'loss': (order.profit || 0) < 0 }">
                ¥{{ order.profit || 0 }}
              </div>
            </div>
            <div class="detail-item" v-if="order.profitRate">
              <div class="item-label">利润率</div>
              <div class="item-value" :class="{ 'profit': order.profitRate > 0, 'loss': order.profitRate < 0 }">
                {{ order.profitRate }}%
              </div>
            </div>
          </div>
        </div>

        <!-- 成本明细卡片 -->
        <div class="detail-card">
          <div class="card-title">
            <t-icon name="chart-pie" size="20px" />
            <span>成本明细</span>
            <t-button size="small" theme="primary" variant="text" @click="showAddCostForm">
              添加成本
            </t-button>
          </div>
          <div class="detail-section costs">
            <div v-if="costs && costs.length > 0">
              <div class="cost-item" v-for="cost in costs" :key="cost.id">
                <div class="cost-info">
                  <span class="cost-name">{{ cost.costName }}</span>
                  <span class="cost-date">{{ formatDate(cost.costDate) }}</span>
                  <span class="cost-desc" v-if="cost.description">{{ cost.description }}</span>
                </div>
                <div class="cost-actions">
                  <span class="cost-value">¥{{ cost.amount }}</span>
                  <t-button size="small" theme="danger" variant="text" @click="deleteCost(cost.id)">
                    删除
                  </t-button>
                </div>
              </div>
            </div>
            <div v-else class="empty-costs">
              暂无成本记录
            </div>
          </div>
        </div>

        <!-- 时间信息卡片 -->
        <div class="detail-card" v-if="order.departureTime || order.arrivalTime">
          <div class="card-title">
            <t-icon name="time" size="20px" />
            <span>时间信息</span>
          </div>
          <div class="detail-section">
            <div class="detail-item" v-if="order.departureTime">
              <div class="item-label">出发时间</div>
              <div class="item-value">{{ formatDateTime(order.departureTime) }}</div>
            </div>
            <div class="detail-item" v-if="order.arrivalTime">
              <div class="item-label">到达时间</div>
              <div class="item-value">{{ formatDateTime(order.arrivalTime) }}</div>
            </div>
          </div>
        </div>

        <!-- 备注卡片 -->
        <div class="detail-card" v-if="order.remarks">
          <div class="card-title">
            <t-icon name="chat" size="20px" />
            <span>备注</span>
          </div>
          <div class="detail-section">
            <div class="remarks">{{ order.remarks }}</div>
          </div>
        </div>

        <!-- 状态操作按钮 -->
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
import { Toast } from 'tdesign-mobile-vue'
import TransportOrderForm from '../components/TransportOrderForm.vue'

const router = useRouter()
const route = useRoute()
const orderStore = useTransportOrderStore()
const costStore = useOrderCostStore()

// 订单ID
const orderId = computed(() => route.params.id)

// 响应式数据
const order = ref(null)
const costs = ref([])
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
    await orderStore.updateTransportStatus(orderId.value, status, '手动更新状态')
    order.value.transportStatus = status
    Toast({ message: '状态更新成功', theme: 'success' })
  } catch (error) {
    Toast({ message: error.message || '状态更新失败', theme: 'error' })
  }
}

// 更新款项状态
const updatePaymentStatus = async (status) => {
  try {
    await orderStore.updatePaymentStatus(orderId.value, status, '手动更新状态')
    order.value.paymentStatus = status
    Toast({ message: '状态更新成功', theme: 'success' })
  } catch (error) {
    Toast({ message: error.message || '状态更新失败', theme: 'error' })
  }
}

// 显示添加成本表单
const showAddCostForm = () => {
  // TODO: 实现添加成本表单
  Toast({ message: '添加成本功能开发中', theme: 'warning' })
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
  padding: 15px;
  padding-bottom: 30px;
  padding-top: 56px; /* 为固定导航栏留出空间 */
}

.detail-card {
  background-color: #fff;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.order-header-card {
  background: linear-gradient(135deg, #0052d9, #1890ff);
  color: white;
}

.order-header-card .detail-title {
  color: white;
  font-size: 20px;
  margin-top: 0;
  margin-bottom: 8px;
  font-weight: 600;
}

.order-header-card .detail-subtitle {
  color: rgba(255, 255, 255, 0.9);
  font-size: 16px;
  margin-bottom: 12px;
}

.order-header-card .order-date {
  color: rgba(255, 255, 255, 0.85);
  font-size: 14px;
}

.order-header-card .status-badges {
  margin-bottom: 8px;
}

.order-header-card .status-badge {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.card-title {
  display: flex;
  align-items: center;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.card-title span {
  margin-left: 8px;
  flex: 1;
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

.detail-title {
  font-size: 20px;
  margin-top: 0;
  margin-bottom: 10px;
  color: #333;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.bill-status {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  background-color: rgba(255, 255, 255, 0.25);
  color: white;
}

.bill-status.outsourced {
  background-color: rgba(255, 255, 255, 0.25);
}

.bill-date {
  font-size: 14px;
  color: #666;
}

.detail-section {
  margin: 0;
}

.detail-item {
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px dashed #f0f0f0;
  padding-bottom: 8px;
}

.detail-item:last-child {
  margin-bottom: 0;
  border-bottom: none;
}

.item-label {
  font-size: 14px;
  color: #666;
}

.item-value {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.item-value.price {
  color: #0052d9;
  font-weight: 600;
}

.item-value.cost {
  color: #ff6b35;
  font-weight: 600;
}

.item-value.profit {
  color: #52c41a;
  font-weight: 600;
}

.item-value.loss {
  color: #ff4d4f;
  font-weight: 600;
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