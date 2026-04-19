<template>
  <div class="container">
    <div class="card">
      <h2>Login</h2>
      <form @submit.prevent="loginUser">
        <label>Email</label>
        <input v-model="email" type="email" placeholder="Enter email..." required />

        <label>Password</label>
        <input v-model="password" type="password" placeholder="••••••••" required />

        <div v-if="message" :class="['message-box', isError ? 'error' : 'success']">
            {{ message }}
        </div>
        <button type="submit">Enter</button>
      </form>
      <div class="links">
        Don't have an account? <router-link to="/register">Register</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from '@/api/axios';
import {jwtDecode} from "jwt-decode";
export default {
  data() {
    return {
      email: "",
      password: "",
      message: "",
      isError: false
    };
  },
  methods: {
async loginUser()
    {
      this.message = "";
      this.isError = false;
      try
      {
        const payload = {
          email: this.email,
          password: this.password
        };

        const response = await apiClient.post('/auth/login', payload);

        const token = response.data.data;
        localStorage.setItem("token", token);
        const decoded = jwtDecode(token);

        const rawRole = decoded.role;

        const userRole = rawRole.replace('ROLE_', '');

        if(userRole == "STUDENT")
        {
          this.$router.push("/studentDashboard");
        }
        else if(userRole == "TEACHER")
        {
          this.$router.push("/teacherDashboard");
        }
      }
      catch(error)
      {
        console.error("Login Error:", error);
        this.isError = true;
        if(error.response)
        {
          const backendMessage = error.response.data.message;

          if(backendMessage && error.response.status === 401)
          {
            this.message = "Your account is not activated. Please confirm your email!";
          }
          else
          {
            this.message = "Wrong email or password";
          }
        }
        else
        {
          this.message = "An error occurred while connecting to the server.";
        }
      }
    }
  }
};
</script>

<style scoped>
.container {
  min-height: 100vh;
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1e3c72, #2a5298);
  padding: 20px;
  box-sizing: border-box;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.card {
  background: white;
  padding: 40px;
  border-radius: 16px;
  width: 100%;
  max-width: 450px;
  box-shadow: 0 15px 35px rgba(0,0,0,0.3);
  box-sizing: border-box;
}

h2 {
  color: #333;
  text-align: center;
  margin-bottom: 25px;
  font-size: 28px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 600;
  font-size: 14px;
}

input, select {
  width: 100%;
  padding: 12px 15px;
  margin-bottom: 20px;
  border: 2px solid #eee;
  border-radius: 8px;
  box-sizing: border-box;
  font-size: 16px;
  transition: border-color 0.3s;
}

input:focus, select:focus {
  border-color: #1e3c72;
  outline: none;
}

button {
  width: 100%;
  padding: 14px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  transition: background 0.3s, transform 0.2s;
}

button:hover {
  background-color: #218838;
  transform: translateY(-2px);
}

.message-box {
  padding: 12px;
  margin-bottom: 20px;
  border-radius: 6px;
  text-align: center;
  font-weight: 500;
}

.error { background: #ffebee; color: #c62828; border: 1px solid #ef9a9a; }
.success { background: #e8f5e9; color: #2e7d32; border: 1px solid #a5d6a7; }

.links {
  margin-top: 20px;
  text-align: center;
  font-size: 14px;
  color: #666;
}

.links a { color: #1e3c72; text-decoration: none; font-weight: bold; }
</style>
