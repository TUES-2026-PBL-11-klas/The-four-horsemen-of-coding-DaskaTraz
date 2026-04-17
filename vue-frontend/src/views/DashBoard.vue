<template>
  <div style="padding: 20px; text-align: center;">
    <h1>Welcome in Dazkatraz!</h1>
    <p>Successful login.</p>
    <button @click="logout" style="padding: 10px; background: red; color: white; border: none; border-radius: 5px; cursor: pointer;">
      exit
    </button>
  </div>
</template>

<script>
export default {
  methods: {
    logout() {
      localStorage.removeItem("token");
      this.$router.push("/login");
    }
  }
}
</script>
