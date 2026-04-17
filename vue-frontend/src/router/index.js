import { createRouter, createWebHistory } from 'vue-router'

import Register from '../views/Register.vue'
import RegisterRole from '../views/RegisterRole.vue'
import Student from '../views/Student.vue'
import StudentDashboard from '../views/StudentDashboard.vue'
import Teacher from '../views/Teacher.vue'
import TeacherDashboard from '../views/TeacherDashboard.vue'
import Login from '../views/Login.vue'
import EmailVerify from '../views/EmailVerify.vue'
import Dashboard from '../views/Dashboard.vue'

const routes = [

  // 0 - HOME
  {
    path: '/',
    name: 'home',
    component: Dashboard,
    meta: { requiresAuth: true }
  },

  // 1 - TEACHER LOGIN
  {
    path: '/teacher',
    name: 'teacher-login',
    component: Teacher
  },

  // 2 - TEACHER DASHBOARD
  {
    path: '/teacher/dashboard',
    name: 'teacher-dashboard',
    component: TeacherDashboard,
    meta: { requiresAuth: true }
  },

  // 3 - STUDENT SELECT CLASS
  {
    path: '/student',
    name: 'student-select',
    component: Student,
    meta: { requiresAuth: true }
  },

  // 4 - STUDENT DASHBOARD (grades + graph)meta: { requiresAuth: true }
  {
    path: '/student/dashboard',
    name: 'student-dashboard',
    component: StudentDashboard,
  },

  // 5 - AUTH
  {
    path: '/login',
    name: 'login',
    component: Login
  },

  {
    path: '/register',
    name: 'register',
    component: Register
  },

  {
    path: '/role',
    name: 'role',
    component: RegisterRole
  },

  {
    path: '/verify',
    name: 'verify',
    component: EmailVerify
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  const requiresAuth = to.matched.some(r => r.meta.requiresAuth)

  if (requiresAuth && !token) {
    return { name: 'login' }
  }

  if (
    token &&
    ['login', 'register', 'role'].includes(to.name)
  ) {
    return { name: 'home' }
  }

  return true
})

export default router
