import axios from 'axios'
import router from '@/router/index.js'
const apiClient = axios.create({
  baseURL: "/api",
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
})

apiClient.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if(token)
  {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
},
error => {
  return Promise.reject(error);
});

apiClient.interceptors.response.use(
  response => response,
  error => {
    const status = error.response ? error.response.status : null;

    if(status === 401)
    {
      localStorage.removeItem('token');
      if(router.currentRoute.value.path !== '/login')
      {
        router.push('/login');
      }
    }
    else if(status === 403)
    {
      console.error("Access denied!");
      router.push('/');
    }
    else if (status >= 500)
    {
      console.error("Server error. Please try again later.");
    }

    return Promise.reject(error);
  }
);

export default apiClient
