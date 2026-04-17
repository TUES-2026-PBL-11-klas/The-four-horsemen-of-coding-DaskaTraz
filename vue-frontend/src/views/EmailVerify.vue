<template>
  <div class="container">
    <div class="card">
      <h2>Confirm Email</h2>
      <p>Enter the 6-digit code we sent to your email.</p>

      <form @submit.prevent="verifyCode">
        <input
          v-model="tokenCode"
          type="text"
          maxlength="6"
          placeholder="enter code"
          class="code-input"
          required
        />

        <div v-if="message" :class="['message-box', isError ? 'error' : 'success']">
            {{ message }}
        </div>
        <button type="submit">Confirm</button>
      </form>
    </div>
  </div>
</template>

<script>
import apiClient from '@/api/axios';

export default {
  data() {
    return {
      tokenCode: "",
        message: "",
        isError: false
    };
  },
  methods: {
   async verifyCode()
   {
    this.message = "";
    this.isError = false;
    try
    {
        const response = await apiClient.post('/auth/verify', null, {
        params: {
            userId: localStorage.getItem("userId"),
            token: this.tokenCode
        }
        });

        if(response.data.success)
        {
            this.message = "Successful verification!";
            this.isError = false;
            setTimeout(() => {
                this.$router.push('/login');
            }, 1500);
        }
    }
    catch(error)
    {
        console.error("Error:", error.response?.data?.message || error.message);
        this.message = "Invalid code, please try again.";
        this.isError = true;
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
  background: linear-gradient(135deg, #11998e, #38ef7d);
}
.card {
  background: white;
  padding: 30px;
  border-radius: 10px;
  width: 350px;
  text-align: center;
  box-shadow: 0 4px 15px rgba(0,0,0,0.2);
}
.code-input {
  width: 100%;
  padding: 15px;
  margin: 20px 0;
  font-size: 24px;
  text-align: center;
  letter-spacing: 5px;
  border: 2px solid #ccc;
  border-radius: 5px;
}
button {
  width: 100%;
  padding: 10px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
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
