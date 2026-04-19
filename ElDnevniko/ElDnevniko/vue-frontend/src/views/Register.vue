<template>
  <div class="container">
    <div class="card">
      <h2>Register</h2>

      <form @submit.prevent="registerUser">
        <label>Username</label>
        <input v-model="username" type="text" placeholder="Username..." required />

        <label>Email</label>
        <input v-model="email" type="email" placeholder="example@school.com" required />

        <label>Password</label>
        <input v-model="password" type="password" placeholder="••••••••" required />

        <label>Confirm Password</label>
        <input v-model="confirmPassword" type="password" placeholder="••••••••" required />

        <div v-if="message" :class="['message-box', isError ? 'error' : 'success']">
          {{ message }}
        </div>

        <button type="submit">Continue</button>
      </form>

      <div class="links">
        Already have an account? <router-link to="/login">Log in here</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import apiClient from '@/api/axios';
export default {
  data() {
    return {
      username: "",
      email: "",
      password: "",
      confirmPassword: "",
      message: "",
      isError: false
    };
  },

  methods: {
    async registerUser()
    {
      this.message = "";
      this.isError = false;
      if(this.password !== this.confirmPassword)
      {
        this.message = "Passwords do not match!";
        this.isError = true;
        return;
      }

      const user = {
        username: this.username,
        email: this.email,
        password: this.password
      };

      try
      {
            const response = await apiClient.post('/auth/register', user);
            const userId = response.data.data;

            localStorage.setItem("userId", userId);
            this.isError = false;
            this.message = "Successful registration! Redirecting...";
            this.$router.push("/role");
      }
      catch(error)
      {
            console.error("Registration failed:", error);
            this.isError = true;
            this.message = "Registration failed. Please try again.";
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
  font-family: 'Segoe UI', Tahoma, sans-serif;
  padding: 20px;
}

.card {
  background: white;
  padding: 40px;
  border-radius: 12px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 10px 25px rgba(0,0,0,0.2);
}

h2 {
  color: #000;
  text-align: center;
  margin-bottom: 25px;
  font-weight: bold;
}

label {
  display: block;
  color: #000;
  font-weight: 600;
  margin-bottom: 5px;
  font-size: 14px;
}

input {
  width: 100%;
  padding: 12px;
  margin-bottom: 20px;
  border: 1px solid #ccc;
  border-radius: 6px;
  box-sizing: border-box;
  color: #000;
  font-size: 15px;
  transition: border-color 0.2s;
}

input:focus {
  border-color: #1e3c72;
  outline: none;
}

input::placeholder {
  color: #888;
}

button {
  width: 100%;
  padding: 12px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  transition: background 0.2s, transform 0.1s;
  margin-top: 10px;
}

button:hover {
  background-color: #218838;
}

button:active {
  transform: scale(0.98);
}

.links {
  margin-top: 20px;
  text-align: center;
  color: #000;
  font-size: 14px;
}

.links a {
  color: #1e3c72;
  font-weight: bold;
  text-decoration: none;
}

.links a:hover {
  text-decoration: underline;
}

.message-box {
  padding: 10px;
  margin-bottom: 15px;
  border-radius: 5px;
  text-align: center;
  font-weight: bold;
  font-size: 13px;
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
