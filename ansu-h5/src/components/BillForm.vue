<template>
  <div>
    <t-popup :visible="visible" @update:visible="emit('update:visible', $event)" placement="bottom">
      <t-form 
        ref="form" 
        class="bill-form"
        :data="formData"
        @submit="onSubmit"
      >
        <div class="form-header">
          <h3>{{ isEdit ? '编辑账单' : '添加账单' }}</h3>
          <t-button theme="primary" size="small" @click="onClose">关闭</t-button>
        </div>
        
        <t-form-item label="账单名称" name="name" required>
          <t-input v-model="formData.name" placeholder="请输入账单名称" />
        </t-form-item>
        
        <t-divider>车辆信息</t-divider>
        <t-form-item label="车牌号" name="licensePlate">
          <t-input v-model="formData.licensePlate" placeholder="请输入车牌号" />
        </t-form-item>
        
        <t-form-item label="车辆类型" name="vehicleType">
          <t-input v-model="formData.vehicleType" placeholder="如：厢式货车、平板车等" />
        </t-form-item>
        
        <t-form-item label="车辆规格" name="vehicleSpec">
          <t-input v-model="formData.vehicleSpec" placeholder="如：4.2米、9.6米等" />
        </t-form-item>
        
        <t-divider>司机信息</t-divider>
        <t-form-item label="司机姓名" name="driverName">
          <t-input v-model="formData.driverName" placeholder="请输入司机姓名" />
        </t-form-item>
        
        <t-form-item label="联系电话" name="driverPhone">
          <t-input v-model="formData.driverPhone" placeholder="请输入司机电话" />
        </t-form-item>
        
        <t-divider>运输信息</t-divider>
        <t-form-item label="起点" name="startAddress">
          <t-input v-model="formData.startAddress" placeholder="请输入起点地址" />
        </t-form-item>
        
        <t-form-item label="终点" name="endAddress">
          <t-input v-model="formData.endAddress" placeholder="请输入终点地址" />
        </t-form-item>
        
        <t-form-item label="货物名称" name="cargoName">
          <t-input v-model="formData.cargoName" placeholder="请输入货物名称" />
        </t-form-item>
        
        <t-form-item label="货物重量(kg)" name="cargoWeight">
          <t-stepper v-model="formData.cargoWeight" />
        </t-form-item>
        
        <t-divider>调度信息</t-divider>
        <t-form-item label="是否外派" name="isOutsourced">
          <t-switch v-model="formData.isOutsourced" />
        </t-form-item>
        
        <template v-if="formData.isOutsourced">
          <t-form-item label="第三方平台" name="thirdPartyPlatform">
            <t-input v-model="formData.thirdPartyPlatform" placeholder="如：货拉拉、运满满等" />
          </t-form-item>
          
          <t-form-item label="第三方订单号" name="thirdPartyOrderId">
            <t-input v-model="formData.thirdPartyOrderId" placeholder="请输入第三方订单号" />
          </t-form-item>
        </template>
        
        <t-divider>费用信息</t-divider>
        <div class="cost-list">
          <t-cell>
            <template #title>
              <t-input v-model="newCostKey" placeholder="费用名称" />
            </template>
            <template #note>
              <t-input v-model="newCostValue" placeholder="金额" type="number" />
            </template>
            <template #right-icon>
              <t-button theme="primary" shape="circle" size="small" @click="addCostItem">
                <t-icon name="add" />
              </t-button>
            </template>
          </t-cell>
          
          <div v-if="costItems.length === 0" class="empty-costs">
            暂无费用项，请添加
          </div>
          
          <t-swipe-cell v-for="(item, index) in costItems" :key="index">
            <t-cell>
              <template #title>
                <span>{{ item.key }}</span>
              </template>
              <template #note>
                <span class="cost-value">¥{{ item.value }}</span>
              </template>
            </t-cell>
            <template #right>
              <t-button @click="removeCostItem(index)" shape="square" theme="danger">
                删除
              </t-button>
            </template>
          </t-swipe-cell>
        </div>
        
        <t-form-item label="总费用" name="totalCost">
          <div class="total-cost">¥{{ totalCost }}</div>
        </t-form-item>
        
        <t-form-item label="备注" name="remarks">
          <t-textarea v-model="formData.remarks" placeholder="请输入备注信息" />
        </t-form-item>
        
        <div class="form-footer">
          <t-button block theme="primary" type="submit">{{ isEdit ? '保存修改' : '添加账单' }}</t-button>
        </div>
      </t-form>
    </t-popup>
  </div>
</template>

<script setup>
import { ref, computed, reactive, watch } from 'vue'
import { Toast } from 'tdesign-mobile-vue'
import { Icon as TIcon } from 'tdesign-icons-vue-next'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  billData: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:visible', 'submit', 'close'])

const form = ref(null)
const isEdit = computed(() => Object.keys(props.billData).length > 0)

// 表单数据
const formData = reactive({
  name: '',
  licensePlate: '',
  vehicleType: '',
  vehicleSpec: '',
  driverName: '',
  driverPhone: '',
  startAddress: '',
  endAddress: '',
  cargoName: '',
  cargoWeight: 0,
  isOutsourced: false,
  thirdPartyPlatform: '',
  thirdPartyOrderId: '',
  remarks: ''
})

// 费用项管理
const costItems = ref([])
const newCostKey = ref('')
const newCostValue = ref('')

const totalCost = computed(() => {
  return costItems.value.reduce((sum, item) => sum + Number(item.value), 0).toFixed(2)
})

// 添加费用项
const addCostItem = () => {
  if (!newCostKey.value) {
    Toast({ message: '请输入费用名称', theme: 'warning' })
    return
  }
  
  if (!newCostValue.value) {
    Toast({ message: '请输入费用金额', theme: 'warning' })
    return
  }
  
  costItems.value.push({
    key: newCostKey.value,
    value: parseFloat(newCostValue.value).toFixed(2)
  })
  
  // 清空输入
  newCostKey.value = ''
  newCostValue.value = ''
}

// 移除费用项
const removeCostItem = (index) => {
  costItems.value.splice(index, 1)
}

// 如果是编辑模式，初始化表单数据
watch(() => props.billData, (newVal) => {
  if (newVal && Object.keys(newVal).length > 0) {
    // 复制账单数据到表单
    Object.keys(formData).forEach(key => {
      if (key in newVal) {
        formData[key] = newVal[key]
      }
    })
    
    // 初始化费用项
    if (newVal.costItems && Array.isArray(newVal.costItems)) {
      costItems.value = [...newVal.costItems]
    }
  } else {
    // 重置表单
    Object.keys(formData).forEach(key => {
      if (typeof formData[key] === 'boolean') {
        formData[key] = false
      } else if (typeof formData[key] === 'number') {
        formData[key] = 0
      } else {
        formData[key] = ''
      }
    })
    costItems.value = []
  }
}, { immediate: true, deep: true })

// 提交表单
const onSubmit = () => {
  // 表单验证
  if (!formData.name) {
    Toast({ message: '请输入账单名称', theme: 'warning' })
    return
  }
  
  // 构建提交数据
  const submitData = {
    ...formData,
    costItems: costItems.value,
    totalCost: parseFloat(totalCost.value)
  }
  
  // 提交到父组件
  emit('submit', submitData)
  
  // 关闭弹窗
  emit('update:visible', false)
}

// 关闭弹窗
const onClose = () => {
  emit('update:visible', false)
  emit('close')
}
</script>

<style scoped>
.bill-form {
  background-color: #fff;
  border-radius: 16px 16px 0 0;
  padding: 16px;
  max-height: 90vh;
  overflow-y: auto;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.form-header h3 {
  margin: 0;
}

.cost-list {
  margin: 16px 0;
  background-color: #fff;
  border-radius: 8px;
}

.empty-costs {
  padding: 16px;
  text-align: center;
  color: #999;
}

.cost-value {
  font-weight: bold;
}

.total-cost {
  font-size: 20px;
  font-weight: bold;
  color: #0052d9;
}

.form-footer {
  margin-top: 24px;
  padding-bottom: 24px;
}
</style> 