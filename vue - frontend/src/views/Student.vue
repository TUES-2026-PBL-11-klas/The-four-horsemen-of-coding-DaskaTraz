<template>
  <div class="container">
    <div class="card">
      <h2>Student Information</h2>

      <form @submit.prevent="finish">
        <label>Full Name</label>
        <input v-model="fullName" type="text" required />

        <label>Grade</label>
        <select v-model="grade" required>
          <option value="">Select grade</option>
          <option>8th Grade</option>
          <option>9th Grade</option>
          <option>10th Grade</option>
          <option>11th Grade</option>
          <option>12th Grade</option>
        </select>

        <label>Class</label>
        <select v-model="classLetter" required>
          <option value="">Select class</option>
          <option>A</option>
          <option>B</option>
          <option>C</option>
          <option>D</option>
        </select>

        <label>Class Number</label>
        <input v-model="classNumber" type="number" min="1" max="40" />

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
      classLetter: "",
      classNumber: ""
    };
  },

  methods: {
    finish() {
      const role = localStorage.getItem("role");
      const user = JSON.parse(localStorage.getItem("registerUser"));

      const studentData = {
        ...user,
        role,
        fullName: this.fullName,
        grade: this.grade,
        classLetter: this.classLetter,
        classNumber: this.classNumber
      };

      console.log("Final Student:", studentData);

      alert("Registration complete!");

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
  background: linear-gradient(135deg, #36d1dc, #5b86e5);
}

.card {
  background: white;
  padding: 30px;
  border-radius: 15px;
  width: 400px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
}

input, select {
  width: 100%;
  padding: 10px;
  margin-bottom: 15px;
}
</style>
