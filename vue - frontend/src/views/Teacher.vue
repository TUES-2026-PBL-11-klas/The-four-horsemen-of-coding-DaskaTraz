<template>
  <div class="container">
    <div class="card">
      <h2>Teacher Information</h2>

      <form @submit.prevent="finish">

        <label>Full Name</label>
        <input v-model="fullName" type="text" required />

        <label>Classes You Teach</label>

        <div class="selected-classes">
          <div
            class="class-tag"
            v-for="(c, index) in classes"
            :key="index"
          >
            {{ c }}
            <span @click="removeClass(index)">✖</span>
          </div>
        </div>

        <div class="select-row">
          <select v-model="grade">
            <option value="">Grade</option>
            <option>8</option>
            <option>9</option>
            <option>10</option>
            <option>11</option>
            <option>12</option>
          </select>

          <select v-model="letter">
            <option value="">Class</option>
            <option>A</option>
            <option>B</option>
            <option>C</option>
            <option>D</option>
          </select>

          <button type="button" @click="addClass">
            Add
          </button>
        </div>

        <label>Subject</label>
        <input v-model="subject" type="text" required />

        <button type="submit">Finish</button>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      fullName: "",
      grade: "",
      letter: "",
      subject: "",
      classes: []
    };
  },

  methods: {
    addClass() {
      if (!this.grade || !this.letter) {
        alert("Please select both grade and class!");
        return;
      }

      const combined = this.grade + this.letter;

      if (this.classes.includes(combined)) {
        alert("Already added!");
        return;
      }

      this.classes.push(combined);

      this.grade = "";
      this.letter = "";
    },

    removeClass(index) {
      this.classes.splice(index, 1);
    },

    finish() {
      const role = localStorage.getItem("role");
      const user = JSON.parse(localStorage.getItem("registerUser"));

      const teacherData = {
        ...user,
        role,
        fullName: this.fullName,
        subject: this.subject,
        classes: this.classes
      };

      console.log("Final Teacher:", teacherData);

      alert("Teacher registration complete!");

      this.$router.push("/");
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
  background: linear-gradient(135deg, #af6d1c, #ffd200);
}

.card {
  background: white;
  padding: 30px;
  border-radius: 15px;
  width: 420px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
}

.select-row {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.class-tag {
  background: #f7971e;
  color: white;
  padding: 5px 10px;
  border-radius: 20px;
  display: flex;
  gap: 5px;
  align-items: center;
}

.class-tag span {
  cursor: pointer;
  font-weight: bold;
}
</style>
