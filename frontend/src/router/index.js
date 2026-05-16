import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import OrderCreate from '../views/OrderCreate.vue'
import OrderList from '../views/OrderList.vue'
import BillList from '../views/BillList.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/order/create',
    name: 'OrderCreate',
    component: OrderCreate
  },
  {
    path: '/order/list',
    name: 'OrderList',
    component: OrderList
  },
  {
    path: '/bill/list',
    name: 'BillList',
    component: BillList
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
