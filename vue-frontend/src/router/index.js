import { createRouter, createWebHistory } from 'vue-router'
import Register from '../views/Register.vue'
import RegisterRole from '../views/RegisterRole.vue'
import Student from '../views/Student.vue'
import Teacher from '../views/Teacher.vue'
import Login from '../views/Login.vue'
import EmailVerify from '../views/EmailVerify.vue'
import TeacherDashboard from '../views/TeacherDashboard.vue'
import StudentDashBoard from '../views/StudentDashboard.vue'
import { jwtDecode } from "jwt-decode";
const routes = [
  { path: '/', redirect: '/login' },
  { path: '/teacherDashboard', component: TeacherDashboard, meta: { requiresAuth: true , requiresRole: 'ROLE_TEACHER'} },
  { path: '/studentDashboard', component: StudentDashBoard, meta: { requiresAuth: true, requiresRole: 'ROLE_STUDENT' }},
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  { path: '/role', component: RegisterRole, beforeEnter: () => {
    if(!localStorage.getItem('userId'))
      return { path: '/register' };
  }},
  { path: '/student', component: Student, beforeEnter: () => {
    if(!localStorage.getItem('userId'))
      return { path: '/register' };
  }},
  { path: '/teacher', component: Teacher, beforeEnter: () => {
    if(!localStorage.getItem('userId'))
      return { path: '/register' };
  }},
  { path: '/verify', component: EmailVerify, beforeEnter: () => {
    if(!localStorage.getItem('userId'))
      return { path: '/register' };
  }}
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const token = localStorage.getItem('token');
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);

  if(requiresAuth && !token) return { path: '/login' };

  if(to.meta.requiresRole)
    {
    if(!token) return { path: '/login' };
    try
    {
      const decoded = jwtDecode(token);
      if(decoded.role !== to.meta.requiresRole)
      {
        return { path: '/login' };
      }
    }
    catch(e)
    {
      localStorage.removeItem('token');
      return { path: '/login' };
    }
  }

  if(token && ['/login', '/register'].includes(to.path))
    {
    try
    {
      const decoded = jwtDecode(token);

      if(decoded.role === 'ROLE_TEACHER')
      {
        return { path: '/teacherDashboard' };
      }
      else if(decoded.role === 'ROLE_STUDENT')
      {
        return { path: '/studentDashboard' };
      }
      else
      {
        localStorage.removeItem('token');
        return true;
      }

    }
    catch(e)
    {
      localStorage.removeItem('token');
      return true;
    }
  }

  return true;
});

export default router
