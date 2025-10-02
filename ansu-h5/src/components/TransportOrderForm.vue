<template>
  <t-popup :visible="visible" @update:visible="updateVisible" placement="bottom" :close-on-overlay-click="false">
    <div class="order-form">
      <div class="form-header">
        <h3>{{ isEdit ? '编辑订单' : '新增订单' }}</h3>
        <t-button variant="text" size="small" @click="closeForm">
          <t-icon name="close" />
        </t-button>
      </div>

      <div class="form-content">
        <t-form ref="form" :data="formData" @submit="handleSubmit">
          <!-- 客户信息 -->
          <div class="form-section">
            <h4>客户信息</h4>
            <t-form-item label="企业名称" name="customerCompanyName" :rules="[{ required: true, message: '请输入企业名称' }]">
              <t-input v-model="formData.customerCompanyName" placeholder="请输入企业名称" />
            </t-form-item>
            
            <t-form-item label="联系人" name="customerContactPerson" :rules="[{ required: true, message: '请输入联系人' }]">
              <t-input v-model="formData.customerContactPerson" placeholder="请输入联系人姓名" />
            </t-form-item>
            
            <t-form-item label="联系电话" name="customerContactPhone" :rules="[{ required: true, message: '请输入联系电话' }]">
              <t-input v-model="formData.customerContactPhone" placeholder="请输入联系电话" />
            </t-form-item>
          </div>

          <!-- 运输信息 -->
          <div class="form-section">
            <h4>运输信息</h4>
            <t-form-item label="发送地点" name="startAddress" :rules="[{ required: true, message: '请输入发送地点' }]">
              <t-input v-model="formData.startAddress" placeholder="请输入发送地点" />
            </t-form-item>
            
            <t-form-item label="目标地点" name="endAddress" :rules="[{ required: true, message: '请输入目标地点' }]">
              <t-input v-model="formData.endAddress" placeholder="请输入目标地点" />
            </t-form-item>
            
            <t-form-item label="货物内容" name="cargoName" :rules="[{ required: true, message: '请输入货物内容' }]">
              <t-input v-model="formData.cargoName" placeholder="请输入货物内容" />
            </t-form-item>
            
            <t-form-item label="货物重量(吨)" name="cargoWeight">
              <t-input v-model="formData.cargoWeight" type="number" placeholder="请输入货物重量" />
            </t-form-item>
            
            <t-form-item label="货物体积(m³)" name="cargoVolume">
              <t-input v-model="formData.cargoVolume" type="number" placeholder="请输入货物体积" />
            </t-form-item>
          </div>

          <!-- 车辆信息 -->
          <div class="form-section">
            <h4>车辆信息</h4>
            <t-form-item label="是否外包">
              <t-switch v-model="formData.isOutsourced" />
            </t-form-item>
            
            <template v-if="formData.isOutsourced">
              <t-form-item label="第三方平台" name="thirdPartyPlatform">
                <t-input v-model="formData.thirdPartyPlatform" placeholder="如：货拉拉、快狗打车等" />
              </t-form-item>
              
              <t-form-item label="第三方订单号" name="thirdPartyOrderId">
                <t-input v-model="formData.thirdPartyOrderId" placeholder="请输入第三方订单号" />
              </t-form-item>
            </template>
            
            <template v-else>
              <t-form-item label="车牌号" name="licensePlate">
                <t-input v-model="formData.licensePlate" placeholder="请输入车牌号" />
              </t-form-item>
              
              <t-form-item label="车辆类型" name="vehicleType">
                <t-input v-model="formData.vehicleType" placeholder="如：厢式货车、平板车等" />
              </t-form-item>
              
              <t-form-item label="车辆规格" name="vehicleSpec">
                <t-input v-model="formData.vehicleSpec" placeholder="如：4.2米、6.8米等" />
              </t-form-item>
              
              <t-form-item label="司机姓名" name="driverName">
                <t-input v-model="formData.driverName" placeholder="请输入司机姓名" />
              </t-form-item>
              
              <t-form-item label="司机电话" name="driverPhone">
                <t-input v-model="formData.driverPhone" placeholder="请输入司机电话" />
              </t-form-item>
            </template>
          </div>

          <!-- 价格信息 -->
          <div class="form-section">
            <h4>价格信息</h4>
            <t-form-item label="报价金额" name="quotedPrice">
              <t-input v-model="formData.quotedPrice" type="number" placeholder="请输入报价金额" />
            </t-form-item>
            
            <t-form-item label="实际收款" name="actualPrice">
              <t-input v-model="formData.actualPrice" type="number" placeholder="请输入实际收款金额" />
            </t-form-item>
          </div>

          <!-- 备注 -->
          <div class="form-section">
            <t-form-item label="备注" name="remarks">
              <t-textarea v-model="formData.remarks" placeholder="请输入备注信息" :maxlength="500" />
            </t-form-item>
          </div>

          <div class="form-actions">
            <t-button theme="default" variant="outline" @click="closeForm">取消</t-button>
            <t-button theme="primary" type="submit" :loading="submitting">
              {{ isEdit ? '更新' : '创建' }}
            </t-button>
          </div>
        </t-form>
      </div>
    </div>
  </t-popup>
</template>

<script setup>
import { ref, computed, reactive, watch } from 'vue'
import { Toast } from 'tdesign-mobile-vue'

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  orderData: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['update:visible', 'submit', 'close'])

const form = ref(null)
const submitting = ref(false)
const isEdit = computed(() => Object.keys(props.orderData).length > 0)

// 更新visible状态
const updateVisible = (value) => {
  emit('update:visible', value)
}

// 表单数据
const formData = reactive({
  customerCompanyName: '',
  customerContactPerson: '',
  customerContactPhone: '',
  startAddress: '',
  endAddress: '',
  cargoName: '',
  cargoWeight: null,
  cargoVolume: null,
  isOutsourced: false,
  thirdPartyPlatform: '',
  thirdPartyOrderId: '',
  licensePlate: '',
  vehicleType: '',
  vehicleSpec: '',
  driverName: '',
  driverPhone: '',
  quotedPrice: null,
  actualPrice: null,
  remarks: ''
})

// 监听visible变化，重置表单
watch(() => props.visible, (newVal) => {
  if (newVal) {
    resetForm()
    if (isEdit.value) {
      Object.assign(formData, props.orderData)
    }
  }
})

// 监听isOutsourced变化，清空相关字段
watch(() => formData.isOutsourced, (newVal) => {
  if (newVal) {
    // 外包时清空自有车辆信息
    formData.licensePlate = ''
    formData.vehicleType = ''
    formData.vehicleSpec = ''
    formData.driverName = ''
    formData.driverPhone = ''
  } else {
    // 自有车辆时清空第三方信息
    formData.thirdPartyPlatform = ''
    formData.thirdPartyOrderId = ''
  }
})

const resetForm = () => {
  Object.keys(formData).forEach(key => {
    if (typeof formData[key] === 'boolean') {
      formData[key] = false
    } else if (typeof formData[key] === 'number') {
      formData[key] = null
    } else {
      formData[key] = ''
    }
  })
}

const handleSubmit = async ({ validateResult }) => {
  if (validateResult === true) {
    submitting.value = true
    
    try {
      // 处理数字字段
      const submitData = { ...formData }
      if (submitData.cargoWeight) {
        submitData.cargoWeight = parseFloat(submitData.cargoWeight)
      }
      if (submitData.cargoVolume) {
        submitData.cargoVolume = parseFloat(submitData.cargoVolume)
      }
      if (submitData.quotedPrice) {
        submitData.quotedPrice = parseFloat(submitData.quotedPrice)
      }
      if (submitData.actualPrice) {
        submitData.actualPrice = parseFloat(submitData.actualPrice)
      }
      
      // 转换isOutsourced为数字
      submitData.isOutsourced = submitData.isOutsourced ? 1 : 0
      
      emit('submit', submitData)
      // 不在这里显示成功消息，由父组件处理
      closeForm()
    } catch (error) {
      Toast.error(error.message || '操作失败')
    } finally {
      submitting.value = false
    }
  }
}

const closeForm = () => {
  emit('update:visible', false)
  emit('close')
}
</script>

<style scoped>
.order-form {
  background: white;
  border-radius: 12px 12px 0 0;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
  background: white;
  position: sticky;
  top: 0;
  z-index: 10;
}

.form-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
}

.form-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.form-section {
  margin-bottom: 24px;
}

.form-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  border-left: 3px solid #0052d9;
  padding-left: 8px;
}

.form-actions {
  display: flex;
  gap: 12px;
  padding-top: 20px;
  border-top: 1px solid #eee;
  position: sticky;
  bottom: 0;
  background: white;
  margin: 0 -20px -20px -20px;
  padding: 20px;
}

.form-actions .t-button {
  flex: 1;
}
</style>
