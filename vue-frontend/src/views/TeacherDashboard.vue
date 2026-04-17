<template>
  <div class="container">

    <!-- SUBJECT BOX -->
    <div class="subjects-box">

      <input :value="selection" placeholder="Избери..." readonly />

      <button @click="showGradesUI = true">
        Add grades
      </button>

      <button @click="toggleSubjects">Sub</button>

      <div class="menu-wrapper">

        <!-- SUBJECTS -->
        <ul v-show="showSubjects">
          <li v-for="sub in subjects" :key="sub">
            <button @click="selectSubject(sub)">
              {{ sub }}
            </button>
          </li>
        </ul>

        <!-- CLASSES -->
        <div v-show="showClassesBox">
          <button v-for="g in grades" :key="g" @click="selectClass(g)">
            {{ g }} клас
          </button>
        </div>

      </div>
    </div>

    <!-- STUDENTS + GRADES -->
    <div class="grades-box">

      <div v-for="(student, index) in students" :key="index" class="row">
        {{ index + 1 }} | {{ student.name }} |

        <!-- 👉 показва се САМО след Add grades -->
        <template v-if="showGradesUI">

          <button
            v-for="g in gradeButtons"
            :key="g"
            :class="'btn-' + g"
            @click="setGrade(student, g)"
          >
            {{ g }}
          </button>

          <input
            v-model="student.grade"
            class="grade-input"
            type="text"
          />

        </template>
      </div>

      <!-- SUBMIT -->
      <button v-if="showGradesUI" class="submit-btn" @click="submitGrades">
        Submit Grades
      </button>

    </div>

  </div>
</template>

<script>
export default {
  data() {
    return {
      subjects: ["Math", "English", "Biology"],
      grades: [8, 9, 10, 11, 12],
      gradeButtons: [2, 3, 4, 5, 6],

      selectedSubject: "",
      selectedClass: "",

      showSubjects: false,
      showClassesBox: false,
      showGradesUI: false,

      students: [
        { name: "Nikolai", grade: "" },
        { name: "Kristian", grade: "" },
        { name: "Student", grade: "" }
      ]
    };
  },

  computed: {
    selection() {
      if (!this.selectedSubject || !this.selectedClass) return "";
      return `${this.selectedSubject} ${this.selectedClass} клас`;
    }
  },

  methods: {
    toggleSubjects() {
      this.showSubjects = !this.showSubjects;
      this.showClassesBox = false;
    },

    selectSubject(sub) {
      this.selectedSubject = sub;
      this.showClassesBox = true;
    },

    selectClass(g) {
      this.selectedClass = g;
      this.showSubjects = false;
      this.showClassesBox = false;
    },

    setGrade(student, grade) {
      student.grade = grade.toFixed(2);
    },

    submitGrades() {
      console.log("Grades:", this.students);
      alert("Grades submitted!");
    }
  }
};
</script>

<style scoped>
.container {
  display: flex;
  gap: 80px;
  padding: 40px;
  background: #1e1e1e;
  color: white;
  min-height: 100vh;
}

.subjects-box {
  border: 2px dashed #aaa;
  padding: 10px;
  width: 200px;
}

.menu-wrapper {
  display: flex;
  gap: 10px;
}

.grades-box {
  margin-left: 100px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* ROW */
.row {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* BUTTONS */
.row button {
  width: 35px;
  height: 35px;
  border: none;
  color: white;
  cursor: pointer;
  border-radius: 6px;
}

/* COLORS */
.btn-2 { background: red; }
.btn-3 { background: orange; }
.btn-4 { background: gold; color: black; }
.btn-5 { background: green; }
.btn-6 { background: lightgreen; color: black; }

/* INPUT */
.grade-input {
  width: 90px;
  height: 35px;
  text-align: center;
  border-radius: 6px;
  border: 1px solid #aaa;
  background: transparent;
  color: white;
}

/* SUBMIT */
.submit-btn {
  margin-top: 20px;
  width: 100%;
  padding: 10px;
  background: transparent;
  border: 1px solid #aaa;
  color: white;
  cursor: pointer;
}
</style>
