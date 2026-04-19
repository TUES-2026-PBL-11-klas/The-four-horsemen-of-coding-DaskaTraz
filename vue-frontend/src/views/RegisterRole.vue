<template>
  <div class="container">
    <div class="card">
      <h2>Choose Role</h2>

      <div class="role-buttons">
        <button
          :class="{ active: selectedRole === 'teacher' }"
          @click="selectRole('teacher')"
        >
          Teacher
        </button>

        <button
          :class="{ active: selectedRole === 'student' }"
          @click="selectRole('student')"
        >
          Student
        </button>
      </div>
      <div v-if="message" :class="['message-box', isError ? 'error' : 'success']">
        {{ message }}
      </div>
      <button class="continue-btn" @click="goNext">
        Continue
      </button>
    </div>
  </div>
</template>

<script>
import apiClient from '@/api/axios';
export default {
  data() {
    return {
      selectedRole: "",
      message: "",
      isError: false
    };
  },

  methods: {
    selectRole(role)
    {
      this.selectedRole = role;
    },

    async goNext()
    {
      this.message = "";
      this.isError = false;
      if(!this.selectedRole)
      {
        this.message = "Please select a role to continue!";
        this.isError = true;
        return;
      }

      const payload = {
        userId: localStorage.getItem("userId"),
        userRole: this.selectedRole.toUpperCase()
      };

      try
      {
        await apiClient.post('/auth/choose-role', payload);
        if(this.selectedRole === "teacher")
        {
          this.$router.push("/teacher");
        }
        else
        {
          this.$router.push("/student");
        }
      }
      catch(error)
      {
        console.error("Role selection failed:", error);
        this.isError = true;
        this.message = "Role selection failed. Please try again.";
      }
    }
  }
};
</script>

<style scoped>
.container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #ff9966, #ff5e62);
}

.card {
  background: white;
  padding: 30px;
  border-radius: 15px;
  width: 400px;
  text-align: center;
  color: black;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
}

.role-buttons {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.role-buttons button {
  flex: 1;
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #ccc;
  background: white;
  cursor: pointer;
  transition: 0.3s;
}

.role-buttons button.active {
  background: #ff5e62;
  color: white;
  border-color: #ff5e62;
}

.continue-btn {
  width: 100%;
  padding: 10px;
  background: #ff5e62;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.continue-btn:hover {
  background: #e14b50;
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
