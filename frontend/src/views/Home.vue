<template>
  <div class="home">
    <h2>用户概览</h2>
    
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="8">
        <el-card>
          <div>
            <div class="card-title">信用评分</div>
            <div class="card-value" :style="{ color: getCreditColor(user.creditScore) }">
              {{ user.creditScore }}
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div>
            <div class="card-title">总风险额度</div>
            <div class="card-value">¥ {{ user.totalRiskLimit }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <div>
            <div class="card-title">已用额度</div>
            <div class="card-value">¥ {{ user.usedRiskAmount }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <span>还款提醒</span>
      </template>
      <el-alert
        v-for="(reminder, index) in reminders"
        :key="index"
        :title="reminder"
        :type="reminder.includes('逾期') ? 'error' : 'warning'"
        show-icon
        style="margin-bottom: 10px;"
      />
      <el-empty v-if="reminders.length === 0" description="暂无提醒" />
    </el-card>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import store from '../store'

export default {
  name: 'Home',
  setup() {
    const user = ref({ creditScore: 0, totalRiskLimit: 0, usedRiskAmount: 0 })
    const reminders = ref([])
    const userId = store.userId

    const fetchUser = async () => {
      try {
        const currentUserId = userId.value || 'user001'
        const res = await axios.get(`http://localhost:8008/api/user/${currentUserId}`)
        user.value = res.data
      } catch (e) {
        console.error(e)
      }
    }

    const fetchReminders = async () => {
      try {
        const currentUserId = userId.value || 'user001'
        const res = await axios.get(`http://localhost:8008/api/user/${currentUserId}/reminders`)
        reminders.value = res.data
      } catch (e) {
        console.error(e)
      }
    }

    const getCreditColor = (score) => {
      if (score >= 80) return '#67C23A'
      if (score >= 60) return '#E6A23C'
      return '#F56C6C'
    }

    watch(userId, () => {
      fetchUser()
      fetchReminders()
    })

    onMounted(() => {
      fetchUser()
      fetchReminders()
    })

    return { user, reminders, getCreditColor }
  }
}
</script>

<style scoped>
.card-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}
.card-value {
  font-size: 28px;
  font-weight: bold;
}
</style>
