<template>
  <div class="container">
    <div class="card">
      <h2>Student Information</h2>
      <form @submit.prevent="finish">
        <label>Choose your class</label>
        <select v-model="selectedClassId" required>
          <option value="" disabled>Choose from the list...</option>
          <option v-for="c in availableClasses" :key="c.id" :value="c.id">
            {{ c.className }}
          </option>
        </select>

        <div v-if="message" :class="['message-box', isError ? 'error' : 'success']">
          {{ message }}
        </div>
        <button type="submit">Finish Registration</button>
      </form>
    </div>
  </div>
</template>

<script>
import apiClient from '@/api/axios';

export default {
  data() {
    return {
      availableClasses: [],
      selectedClassId: "",
      message: "",
      isError: false
    };
  },
  async mounted()
  {
    this.message = "";
    this.isError = false;
    try
    {
      const response = await apiClient.get('/auth/classes');
      this.availableClasses = response.data.data;
    }
    catch(error)
    {
      console.error("Error loading classes:", error);
      this.isError = true;
      this.message = "Failed to load classes. Please refresh the page.";
    }
  },
  methods:
  {
    async finish()
    {
      this.message = "";
      this.isError = false;
      const payload = {
        userId: localStorage.getItem("userId"),
        schoolClassId: this.selectedClassId
      };

      try
      {
        await apiClient.post('/auth/register-student', payload);
        this.message = "Registration successful! Redirecting to email verification...";
        this.isError = false;
        this.$router.push("/verify")
      }
      catch(error)
      {
        console.error("Student registration failed:", error);
        this.isError = true;
        this.message = "Student registration failed. Please try again.";
      }
    }
  }
};
</script>

<style scoped>
.container { min-height: 100vh; display: flex; justify-content: center; align-items: center; background: linear-gradient(135deg, #36d1dc, #5b86e5); font-family: 'Segoe UI', sans-serif; }
.card { background: white; padding: 40px; border-radius: 12px; width: 380px; box-shadow: 0 10px 25px rgba(0,0,0,0.2); }
h2 { color: #000; text-align: center; margin-bottom: 25px; }
label { display: block; color: #000; font-weight: 600; margin-bottom: 8px; font-size: 14px; }
select {
  width: 100%;
  padding: 12px;
  margin-bottom: 25px;
  border: 1px solid #ccc;
  border-radius: 6px;
  color: #000;
  font-size: 15px;
  background-color: #fff;
}
button {
  width: 100%; padding: 12px; background-color: #28a745; color: white; border: none; border-radius: 6px; cursor: pointer; font-size: 16px; font-weight: bold;
}
.message-box { padding: 10px; margin-bottom: 15px; border-radius: 5px; text-align: center; font-weight: bold; }
.error { background-color: #ffe6e6; color: #d9534f; border: 1px solid #d9534f; }
.success { background-color: #e6ffe6; color: #28a745; border: 1px solid #28a745; }
</style>
