<template>
  <div class="bill-list">
    <h2>账单列表</h2>
    
    <el-table :data="bills" border style="width: 100%;">
      <el-table-column prop="orderId" label="订单ID" width="180" />
      <el-table-column prop="period" label="期数" width="80" />
      <el-table-column prop="principal" label="本金">
        <template #default="{ row }">¥ {{ row.principal }}</template>
      </el-table-column>
      <el-table-column prop="fee" label="手续费">
        <template #default="{ row }">¥ {{ row.fee }}</template>
      </el-table-column>
      <el-table-column prop="penalty" label="罚金">
        <template #default="{ row }">¥ {{ row.penalty }}</template>
      </el-table-column>
      <el-table-column prop="totalAmount" label="应还金额">
        <template #default="{ row }">¥ {{ row.totalAmount }}</template>
      </el-table-column>
      <el-table-column prop="dueDate" label="到期日" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'PAID' ? 'success' : row.status === 'OVERDUE' ? 'danger' : 'warning'">
            {{ row.status === 'PAID' ? '已还款' : row.status === 'OVERDUE' ? '已逾期' : '待还款' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button
            v-if="row.status !== 'PAID'"
            size="small"
            type="primary"
            @click="payBill(row)"
          >
            还款
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import store from '../store'

export default {
  name: 'BillList',
  setup() {
    const userId = store.userId
    const bills = ref([])

    const fetchBills = async () => {
      try {
        const currentUserId = userId.value || 'user001'
        const res = await axios.get(`http://localhost:8008/api/user/${currentUserId}/bills`)
        bills.value = res.data
      } catch (e) {
        console.error(e)
      }
    }

    const payBill = async (bill) => {
      try {
        await ElMessageBox.confirm(`确认还款 ¥${bill.totalAmount}?`, '提示')
        const res = await axios.post(`http://localhost:8008/api/bill/${bill.billId}/pay`, {
          amount: bill.totalAmount
        })
        if (res.data.success) {
          ElMessage.success('还款成功！')
          fetchBills()
        }
      } catch (e) {
        if (e !== 'cancel') {
          ElMessage.error(e.response?.data?.message || '还款失败')
        }
      }
    }

    watch(userId, () => {
      fetchBills()
    })

    onMounted(() => {
      fetchBills()
    })

    return { bills, payBill }
  }
}
</script>

<style scoped>
.bill-list {
  padding: 20px;
}
</style>
