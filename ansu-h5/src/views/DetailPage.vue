<template>
  <div class="detail-page">
    <t-navbar title="账单详情" fixed left-arrow @left-click="handleBack" />
    
    <div class="detail-container">
      <template v-if="bill">
        <!-- 账单标题卡片 -->
        <div class="detail-card bill-header-card">
          <h2 class="detail-title">{{ bill.name }}</h2>
          <div class="detail-header">
            <span class="bill-status" :class="{ 'outsourced': bill.isOutsourced }">
              {{ bill.isOutsourced ? '外派' : '自有' }}
            </span>
            <span class="bill-date">{{ formatDate(bill.createTime) }}</span>
          </div>
        </div>
        
        <!-- 车辆信息卡片 -->
        <div class="detail-card">
          <div class="card-title">
            <t-icon name="car" size="20px" />
            <span>车辆信息</span>
          </div>
          <div class="detail-section">
            <div class="detail-item" v-if="bill.licensePlate">
              <div class="item-label">车牌号</div>
              <div class="item-value">{{ bill.licensePlate }}</div>
            </div>
            <div class="detail-item" v-if="bill.vehicleType">
              <div class="item-label">车辆类型</div>
              <div class="item-value">{{ bill.vehicleType }}</div>
            </div>
            <div class="detail-item" v-if="bill.vehicleSpec">
              <div class="item-label">车辆规格</div>
              <div class="item-value">{{ bill.vehicleSpec }}</div>
            </div>
          </div>
        </div>
        
        <!-- 司机信息卡片 -->
        <div class="detail-card">
          <div class="card-title">
            <t-icon name="user" size="20px" />
            <span>司机信息</span>
          </div>
          <div class="detail-section">
            <div class="detail-item" v-if="bill.driverName">
              <div class="item-label">司机姓名</div>
              <div class="item-value">{{ bill.driverName }}</div>
            </div>
            <div class="detail-item" v-if="bill.driverPhone">
              <div class="item-label">联系电话</div>
              <div class="item-value">{{ bill.driverPhone }}</div>
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
            <div class="detail-item" v-if="bill.startAddress">
              <div class="item-label">起点</div>
              <div class="item-value">{{ bill.startAddress }}</div>
            </div>
            <div class="detail-item" v-if="bill.endAddress">
              <div class="item-label">终点</div>
              <div class="item-value">{{ bill.endAddress }}</div>
            </div>
            <div class="detail-item" v-if="bill.cargoName">
              <div class="item-label">货物名称</div>
              <div class="item-value">{{ bill.cargoName }}</div>
            </div>
            <div class="detail-item" v-if="bill.cargoWeight">
              <div class="item-label">货物重量</div>
              <div class="item-value">{{ bill.cargoWeight }}kg</div>
            </div>
          </div>
        </div>
        
        <!-- 外派信息卡片 -->
        <div class="detail-card" v-if="bill.isOutsourced">
          <div class="card-title">
            <t-icon name="share" size="20px" />
            <span>外派信息</span>
          </div>
          <div class="detail-section">
            <div class="detail-item" v-if="bill.thirdPartyPlatform">
              <div class="item-label">第三方平台</div>
              <div class="item-value">{{ bill.thirdPartyPlatform }}</div>
            </div>
            <div class="detail-item" v-if="bill.thirdPartyOrderId">
              <div class="item-label">第三方订单号</div>
              <div class="item-value">{{ bill.thirdPartyOrderId }}</div>
            </div>
          </div>
        </div>
        
        <!-- 费用信息卡片 -->
        <div class="detail-card">
          <div class="card-title">
            <t-icon name="money-circle" size="20px" />
            <span>费用信息</span>
          </div>
          <div class="detail-section costs">
            <div v-if="bill.costItems && bill.costItems.length > 0">
              <div class="cost-item" v-for="(item, index) in bill.costItems" :key="index">
                <span class="cost-key">{{ item.key }}</span>
                <span class="cost-value">¥{{ item.value }}</span>
              </div>
            </div>
            <div v-else class="empty-costs">
              暂无费用项
            </div>
            
            <div class="total-cost">
              <span class="cost-key">合计</span>
              <span class="total-value">¥{{ bill.totalCost }}</span>
            </div>
          </div>
        </div>
        
        <!-- 备注卡片 -->
        <div class="detail-card" v-if="bill.remarks">
          <div class="card-title">
            <t-icon name="chat" size="20px" />
            <span>备注</span>
          </div>
          <div class="detail-section">
            <div class="remarks">{{ bill.remarks }}</div>
          </div>
        </div>
        
        <!-- 操作按钮 -->
        <div class="action-buttons">
          <t-button theme="primary" block @click="handleEdit">编辑账单</t-button>
          <t-button theme="danger" block @click="handleDelete">删除账单</t-button>
        </div>
      </template>
      
      <div v-else class="not-found">
        <t-empty description="找不到账单信息" />
        <t-button theme="primary" @click="handleBack">返回列表</t-button>
      </div>
    </div>
    
    <!-- 账单表单 -->
    <bill-form 
      v-model:visible="billFormVisible"
      :bill-data="currentBillData"
      @submit="handleBillSubmit"
      @close="handleFormClose"
    />
    
    <!-- 删除确认对话框 -->
    <t-dialog
      v-model="deleteConfirmVisible"
      title="确认删除"
      content="确定要删除此账单吗？此操作不可撤销。"
      :confirm-btn="{ content: '删除', theme: 'danger' }"
      @confirm="confirmDelete"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useBillStore } from '../stores/billStore'
import { Toast, Icon } from 'tdesign-mobile-vue'
import BillForm from '../components/BillForm.vue'

const router = useRouter()
const route = useRoute()
const billStore = useBillStore()

// 账单ID
const billId = computed(() => route.params.id)

// 从store中获取账单数据
const bill = computed(() => {
  return billStore.getBill(billId.value)
})

// 表单控制
const billFormVisible = ref(false)
const currentBillData = ref({})
const deleteConfirmVisible = ref(false)

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 返回上一页
const handleBack = () => {
  router.back()
}

// 编辑账单
const handleEdit = () => {
  currentBillData.value = { ...bill.value }
  billFormVisible.value = true
}

// 处理账单提交
const handleBillSubmit = (formData) => {
  billStore.updateBill(billId.value, formData)
  Toast({ message: '账单更新成功', theme: 'success' })
}

// 处理表单关闭
const handleFormClose = () => {
  currentBillData.value = {}
}

// 删除账单
const handleDelete = () => {
  deleteConfirmVisible.value = true
}

// 确认删除
const confirmDelete = () => {
  billStore.deleteBill(billId.value)
  Toast({ message: '账单已删除', theme: 'success' })
  router.replace('/list')
}

// 如果没有找到账单数据，可能跳转到列表页面
onMounted(() => {
  if (!billId.value || !bill.value) {
    Toast({ message: '找不到账单信息', theme: 'warning' })
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

.bill-header-card {
  background: linear-gradient(135deg, #0052d9, #1890ff);
  color: white;
}

.bill-header-card .detail-title {
  color: white;
  font-size: 22px;
  margin-top: 0;
  margin-bottom: 10px;
}

.bill-header-card .bill-date {
  color: rgba(255, 255, 255, 0.85);
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

.costs .cost-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px dashed #eee;
}

.cost-key {
  color: #666;
}

.cost-value {
  font-weight: 500;
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

.not-found {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 0;
  gap: 20px;
}
</style> 