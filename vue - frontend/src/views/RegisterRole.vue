<template>
  <div class="container">
    <div class="card">
      <h2>Избери роля</h2>

      <div class="role-buttons">
        <button
          :class="{ active: selectedRole === 'teacher' }"
          @click="selectRole('teacher')"
        >
          Учител
        </button>

        <button
          :class="{ active: selectedRole === 'student' }"
          @click="selectRole('student')"
        >
          Ученик
        </button>
      </div>

      <button class="continue-btn" @click="goNext">
        Continue
      </button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      selectedRole: ""
    };
  },

  methods: {
    selectRole(role) {
      this.selectedRole = role;
    },

    goNext() {
      if (!this.selectedRole) {
        alert("Моля избери роля!");
        return;
      }

      // запазваме ролята временно
      localStorage.setItem("role", this.selectedRole);

      // навигация
              if (this.selectedRole === "teacher") {
          this.$router.push("/teacher");
        } else {
          this.$router.push("/student");
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
</style>
