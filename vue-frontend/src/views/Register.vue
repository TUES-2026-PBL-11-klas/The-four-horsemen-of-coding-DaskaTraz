<template>
  <div class="register-container">
    <h2>Register</h2>

    <form @submit.prevent="registerUser">
      <input v-model="username" type="text" placeholder="Username" required />
      <input v-model="email" type="email" placeholder="Email" required />
      <input v-model="password" type="password" placeholder="Password" required />
      <input v-model="confirmPassword" type="password" placeholder="Confirm Password" required />
      <div v-if="message" :class="['message-box', isError ? 'error' : 'success']">
        {{ message }}
      </div>
      <button type="submit">Continue</button>
    </form>
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

      // запазваме данните временно
      try
      {
            const response = await apiClient.post('/auth/register', user);
            const userId = response.data.data;

            localStorage.setItem("userId", userId);
            this.isError = false;
            this.message = "Successful registration! Redirecting...";
            // 🔥 ТУК ПРЕХВЪРЛЯМЕ КЪМ STEP 2
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
.register-container {
  width: 320px;
  margin: 100px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 10px;
}

input {
  width: 100%;
  margin-bottom: 10px;
  padding: 8px;
}

button {
  width: 100%;
  padding: 10px;
  background: #42b983;
  color: white;
  border: none;
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
