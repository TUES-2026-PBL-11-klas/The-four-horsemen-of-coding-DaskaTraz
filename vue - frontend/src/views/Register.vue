<template>
  <div class="register-container">
    <h2>Register</h2>

    <form @submit.prevent="registerUser">
      <input v-model="email" type="email" placeholder="Email" required />
      <input v-model="password" type="password" placeholder="Password" required />
      <input v-model="confirmPassword" type="password" placeholder="Confirm Password" required />

      <button type="submit">Continue</button>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      email: "",
      password: "",
      confirmPassword: ""
    };
  },

  methods: {
      registerUser() {
  if (this.password !== this.confirmPassword) {
    alert("Passwords do not match!");
    return;
  }

  const user = {
    email: this.email,
    password: this.password
  };

  // запазваме данните временно
  localStorage.setItem("registerUser", JSON.stringify(user));

  // 🔥 ТУК ПРЕХВЪРЛЯМЕ КЪМ STEP 2
  this.$router.push("/role");
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
</style>
