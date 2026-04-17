import { createRouter, createWebHistory } from 'vue-router'
import Register from '../views/Register.vue'
import RegisterRole from '../views/RegisterRole.vue'
import Student from '../views/Student.vue'
import Teacher from '../views/Teacher.vue'
import Login from '../views/Login.vue'
import EmailVerify from '../views/EmailVerify.vue'
import Dashboard from '../views/Dashboard.vue'

const routes = [
  { path: '/', component: Dashboard, meta: { requiresAuth: true } },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/role', component: RegisterRole },
  { path: '/student', component: Student },
  { path: '/teacher', component: Teacher },
  { path: '/verify', component: EmailVerify }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token');

  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);

  if(requiresAuth && !token)
  {
    return { path: '/login' };
  }

  if(token && (to.path === '/login' || to.path === '/register' || to.path === '/role'))
  {
    return { path: '/' };
  }

  return true;

})


export default router
