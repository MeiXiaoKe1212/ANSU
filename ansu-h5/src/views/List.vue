<template>
  <div>
    <t-navbar title="账单列表" :fixed="false"/>
    
    <div class="list-container">
      <div class="search-section">
        <div class="search-box">
          <input 
            type="text" 
            v-model="searchText" 
            placeholder="搜索账单..."
            @input="handleSearch"
          />
        </div>
        <t-button class="add-button" theme="primary" :icon="addIcon" @click="showAddBillForm">新增</t-button>
      </div>
      
      <div class="list-items">
        <div 
          v-for="bill in filteredBills" 
          :key="bill.id"
          class="list-item"
          @click="viewBillDetail(bill)"
        >
          <div class="item-left">
            <div class="item-title">{{ bill.name }}</div>
            <div class="item-desc">
              {{ bill.startAddress }} → {{ bill.endAddress }}
              <div class="cargo-info">
                {{ bill.cargoName }} 
                <span v-if="bill.cargoWeight > 0">{{ bill.cargoWeight }}kg</span>
              </div>
            </div>
          </div>
          <div class="item-right">
            <div class="item-time">{{ formatDate(bill.createTime) }}</div>
            <div class="item-status" :class="getBillStatusClass(bill)">
              <span class="bill-type">{{ bill.isOutsourced ? '外派' : '自有' }}</span>
              <span class="bill-cost">¥{{ bill.totalCost }}</span>
            </div>
          </div>
        </div>
        
        <div v-if="filteredBills.length === 0" class="empty-state">
          还没有账单记录，点击上方"新增"按钮添加
        </div>
      </div>
    </div>

    <!-- 账单表单 -->
    <bill-form 
      v-model:visible="billFormVisible"
      :bill-data="currentBillData"
      @submit="handleBillSubmit"
      @close="handleFormClose"
    />
  </div>
</template>

<script setup>
import { h, ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Toast } from 'tdesign-mobile-vue'
import { useBillStore } from '../stores/billStore'
import BillForm from '../components/BillForm.vue'
import { AddIcon } from 'tdesign-icons-vue-next'

const router = useRouter()
const billStore = useBillStore()
const searchText = ref('')
const addIcon =  () => h(AddIcon, { size: '18px' });
// 账单表单控制
const billFormVisible = ref(false)
const currentBillData = ref({})

// 过滤账单列表
const filteredBills = computed(() => {
  if (!searchText.value) {
    return billStore.bills
  }
  
  const searchLower = searchText.value.toLowerCase()
  return billStore.bills.filter(bill => {
    return (
      (bill.name && bill.name.toLowerCase().includes(searchLower)) ||
      (bill.startAddress && bill.startAddress.toLowerCase().includes(searchLower)) ||
      (bill.endAddress && bill.endAddress.toLowerCase().includes(searchLower)) ||
      (bill.cargoName && bill.cargoName.toLowerCase().includes(searchLower)) ||
      (bill.licensePlate && bill.licensePlate.toLowerCase().includes(searchLower)) ||
      (bill.driverName && bill.driverName.toLowerCase().includes(searchLower))
    )
  })
})

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 获取账单状态样式
const getBillStatusClass = (bill) => {
  if (bill.isOutsourced) {
    return 'outsourced'
  }
  return 'self-owned'
}

// 搜索处理
const handleSearch = () => {
  // 实际项目中可能需要防抖处理
}

// 查看账单详情
const viewBillDetail = (bill) => {
  router.push(`/detail/${bill.id}`)
}

// 显示添加账单表单
const showAddBillForm = () => {
  currentBillData.value = {}
  billFormVisible.value = true
}

// 处理账单提交
const handleBillSubmit = (formData) => {
  billStore.addBill(formData)
  Toast({ message: '账单添加成功', theme: 'success' })
}

// 处理表单关闭
const handleFormClose = () => {
  currentBillData.value = {}
}
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
  font-weight: 500;
  margin-bottom: 5px;
  color: #333;
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

.item-status {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  font-size: 12px;
}

.bill-type {
  padding: 2px 6px;
  border-radius: 10px;
  font-weight: 500;
  margin-bottom: 3px;
}

.bill-cost {
  font-weight: bold;
  font-size: 14px;
}

.outsourced .bill-type {
  background-color: #fff8e1;
  color: #f57f17;
}

.self-owned .bill-type {
  background-color: #e3f2fd;
  color: #1565c0;
}

.empty-state {
  text-align: center;
  padding: 30px;
  color: #999;
  background-color: #fff;
  border-radius: 8px;
}
</style> 