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

        const token = response.data.token || response.data.data?.token || response.data;

        localStorage.setItem("token", token);

        this.$router.push("/");
      }
      catch (error)
      {
        console.error("Login Error:", error);
        this.isError = true;
        this.message = "Wrong email or password!";
      }
    }
  }
};
</script>

<style scoped>
.container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #1e3c72, #2a5298);
}
.card {
  background: white;
  padding: 30px;
  border-radius: 10px;
  width: 350px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.2);
}
input {
  width: 100%;
  padding: 10px;
  margin: 10px 0 20px 0;
  border: 1px solid #ccc;
  border-radius: 5px;
}
button {
  width: 100%;
  padding: 10px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
}
.links {
  margin-top: 15px;
  text-align: center;
  font-size: 14px;
}


.message-box {
  padding: 10px;
  margin-bottom: 15px;
  border-radius: 5px;
  text-align: center;
  font-weight: bold;
}
.error {
  background-color: #ffe6e6;
  color: #d9534f;
  border: 1px solid #d9534f;
}
.success {
  background-color: #e6ffe6;
  color: #28a745;
  border: 1px solid #28a745;
}
</style>
