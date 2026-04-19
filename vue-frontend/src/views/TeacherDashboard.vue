<template>
  <div class="dashboard-container">
    <div class="header">
      <h2>Daskatraz — Teacher Panel</h2>
      <button @click="logout" class="logout-btn">Logout</button>
    </div>

    <div class="selection-bar">
      <div class="select-group">
        <label>Subject</label>
        <select v-model="selectedSubjectId" @change="fetchClasses">
          <option :value="null" disabled>Choose subject...</option>
          <option v-for="sub in subjects" :key="sub.id" :value="sub.id">
            {{ sub.subjectName }}
          </option>
        </select>
      </div>

      <div class="select-group" v-if="selectedSubjectId">
        <label>Class</label>
        <select v-model="selectedClassId" @change="fetchDiary">
          <option :value="null" disabled>Choose class...</option>
          <option v-for="cls in availableClasses" :key="cls.id" :value="cls.id">
            {{ cls.className }}
          </option>
        </select>
      </div>
    </div>

    <div v-if="students.length > 0" class="table-wrapper">
      <table class="grades-table">
        <thead>
          <tr>
            <th width="50">№</th>
            <th>Teacher</th>
            <th>Current Grades</th>
            <th class="actions-header">Add Grade</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(student, index) in students" :key="student.studentId">
            <td><span class="index-num">{{ index + 1 }}</span></td>
            <td class="student-name">{{ student.studentName }}</td>
            <td>
              <div class="grades-list">
                <span
                  v-for="(grade, gIdx) in student.grades"
                  :key="gIdx"
                  class="grade-badge"
                  :class="getColorClass(grade)"
                >
                  {{ grade }}
                </span>
              </div>
            </td>
            <td>
              <div class="grade-actions">
                <button
                  v-for="val in [2, 3, 4, 5, 6]"
                  :key="val"
                  @click="addGrade(student, val)"
                  :class="['action-btn', 'btn-grade-' + val]"
                >
                  {{ val }}
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-else-if="selectedClassId" class="empty-state">
      <div class="empty-icon">👥</div>
      <p>No students found for the selected class.</p>
    </div>

    <div v-else class="placeholder-state">
      <p>Choose a subject and class to load the diary.</p>
    </div>

    <transition name="slide-fade">
      <div v-if="message" :class="['message-box', isError ? 'error' : 'success']">
        {{ message }}
      </div>
    </transition>
  </div>
</template>

<script>
import apiClient from "@/api/axios";

export default {
  name: "TeacherDashboard",

  data() {
    return {
      subjects: [],
      availableClasses: [],
      students: [],
      selectedSubjectId: null,
      selectedClassId: null,
      message: "",
      isError: false,
    };
  },

  async mounted() {
    this.fetchInitialSubjects();
  },

  methods: {
    async fetchInitialSubjects() {
      try {
        const res = await apiClient.get('/teacher/subjects');
        this.subjects = res.data.data;
      } catch (error) {
        console.error(error);
        this.showStatus(
          error.response?.data?.message || "Error loading subjects",
          true
        );
      }
    },

    logout() {
      localStorage.removeItem('token');
      localStorage.removeItem('userId');
      this.$router.push('/login');
    },

    async fetchClasses() {
      this.selectedClassId = null;
      this.students = [];
      try {
        const res = await apiClient.get(`/teacher/subjects/${this.selectedSubjectId}/classes`);
        this.availableClasses = res.data.data;
      } catch (error) {
        console.error(error);
        this.showStatus(
          error.response?.data?.message || "Error loading classes",
          true
        );
      }
    },

    async fetchDiary() {
      if (!this.selectedSubjectId || !this.selectedClassId) return;
      try {
        const res = await apiClient.get(`/teacher/classes/${this.selectedClassId}/subjects/${this.selectedSubjectId}`);
        this.students = res.data.data.students.map(s => ({
          studentId: s.studentId,
          studentName: s.studentName,
          grades: s.grades ? s.grades.map(g => g.value) : []
        }));
      } catch (error) {
        console.error(error);
        this.showStatus(
          error.response?.data?.message || "Error fetching diary",
          true
        );
      }
    },

    async addGrade(student, value) {
      try {
        const payload = {
          studentId: student.studentId,
          subjectId: this.selectedSubjectId,
          value: value
        };
        await apiClient.post('/teacher/grades', payload);
        student.grades.push(value);
        this.showStatus(`Created grade ${value} for ${student.studentName}`, false);
      } catch (error) {
        console.error(error);
        this.showStatus(
          error.response?.data?.message || "Error creating grade",
          true
        );
      }
    },

    getColorClass(value) {
      const num = parseFloat(value);
      if (num >= 2.0 && num < 3.0) return "grade-2";
      if (num >= 3.0 && num < 3.5) return "grade-3";
      if (num >= 3.5 && num < 4.5) return "grade-4";
      if (num >= 4.5 && num < 5.5) return "grade-5";
      if (num >= 5.5 && num < 6.0) return "grade-6";
      return "";
    },

    showStatus(msg, isErr) {
      this.message = msg;
      this.isError = isErr;
      setTimeout(() => {
        this.message = "";
      }, 3000);
    }
  }
};
</script>
