import { createRouter, createWebHistory } from 'vue-router'
import Register from '../views/Register.vue'
import RegisterRole from '../views/RegisterRole.vue'
import Student from '../views/Student.vue'
import Teacher from '../views/Teacher.vue'


const routes = [
  { path: '/', redirect: '/register' },

  { path: '/register', component: Register },
  { path: '/role', component: RegisterRole },
  { path: '/student', component: Student },
  { path: '/teacher', component: Teacher }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
