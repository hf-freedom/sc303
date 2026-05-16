<template>
  <div id="app">
    <el-container>
      <el-header>
        <div class="header">
          <h1>分期付款账单系统</h1>
          <div class="user-info">
            <span>当前用户: {{ userId }}</span>
            <el-input v-model="userId" placeholder="输入用户ID" style="width: 150px; margin-left: 10px;" />
          </div>
        </div>
      </el-header>
      <el-container>
        <el-aside width="200px">
          <el-menu :router="true" default-active="/">
            <el-menu-item index="/">
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="/order/create">
              <span>创建分期</span>
            </el-menu-item>
            <el-menu-item index="/order/list">
              <span>订单列表</span>
            </el-menu-item>
            <el-menu-item index="/bill/list">
              <span>账单列表</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        <el-main>
          <router-view :userId="userId" />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import store from './store'

export default {
  name: 'App',
  setup() {
    return { userId: store.userId }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
}
.el-header {
  background-color: #409EFF;
  color: white;
  line-height: 60px;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header h1 {
  margin: 0;
  font-size: 24px;
}
.el-aside {
  background-color: #f5f5f5;
  border-right: 1px solid #e6e6e6;
}
.el-main {
  background-color: #fff;
  padding: 20px;
}
</style>
