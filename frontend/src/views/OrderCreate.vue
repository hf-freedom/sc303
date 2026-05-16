<template>
  <div class="order-create">
    <h2>创建分期订单</h2>
    
    <el-card style="max-width: 600px; margin: 0 auto;">
      <el-form ref="formRef" :model="form" label-width="100px">
        <el-form-item label="分期金额">
          <el-input-number
            v-model="form.totalAmount"
            :min="100"
            :max="50000"
            :step="100"
            style="width: 100%;"
          />
        </el-form-item>
        
        <el-form-item label="分期期数">
          <el-select v-model="form.installments" placeholder="请选择期数" style="width: 100%;">
            <el-option
              v-for="n in availableInstallments"
              :key="n"
              :label="n + ' 期'"
              :value="n"
            />
          </el-select>
        </el-form-item>

        <el-form-item v-if="form.installments" label="费率预览">
          <div>
            <p>总金额: ¥ {{ form.totalAmount }}</p>
            <p>分期期数: {{ form.installments }} 期</p>
            <p>手续费率: {{ (getFeeRate() * 100).toFixed(1) }}%</p>
            <p>总手续费: ¥ {{ (form.totalAmount * getFeeRate()).toFixed(2) }}</p>
            <p style="font-weight: bold; color: #409EFF;">
              每期还款: ¥ {{ ((form.totalAmount * (1 + getFeeRate())) / form.installments).toFixed(2) }}
            </p>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" style="width: 100%;">
            创建分期订单
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import store from '../store'

export default {
  name: 'OrderCreate',
  setup() {
    const router = useRouter()
    const userId = store.userId
    const formRef = ref(null)
    const availableInstallments = ref([])
    const form = ref({
      totalAmount: 1000,
      installments: null
    })

    const fetchAvailableInstallments = async () => {
      try {
        const currentUserId = userId.value || 'user001'
        const res = await axios.get(`http://localhost:8008/api/installments/available?userId=${currentUserId}`)
        availableInstallments.value = res.data
        if (res.data.length > 0) {
          form.value.installments = res.data[0]
        }
      } catch (e) {
        console.error(e)
      }
    }

    const getFeeRate = () => {
      const n = form.value.installments
      if (n <= 3) return 0.02
      if (n <= 6) return 0.04
      if (n <= 12) return 0.07
      return 0.12
    }

    const submitForm = async () => {
      try {
        const currentUserId = userId.value || 'user001'
        const res = await axios.post('http://localhost:8008/api/order', {
          userId: currentUserId,
          totalAmount: form.value.totalAmount,
          installments: form.value.installments
        })
        if (res.data.success) {
          ElMessage.success('分期订单创建成功！')
          router.push('/order/list')
        } else {
          ElMessage.error(res.data.message || '创建失败')
        }
      } catch (e) {
        console.error('创建订单错误:', e)
        ElMessage.error(e.response?.data?.message || '创建失败')
      }
    }

    watch(userId, () => {
      fetchAvailableInstallments()
    })

    onMounted(() => {
      fetchAvailableInstallments()
    })

    return {
      formRef,
      form,
      availableInstallments,
      getFeeRate,
      submitForm
    }
  }
}
</script>

<style scoped>
.order-create {
  padding: 20px;
}
p {
  margin: 8px 0;
}
</style>
