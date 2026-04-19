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

  async mounted()
  {
    this.fetchInitialSubjects();
  },

  methods: {
    async fetchInitialSubjects()
    {
      try
      {
        const res = await apiClient.get('/teacher/subjects');
        this.subjects = res.data.data;
      }
      catch(error)
      {
        this.showStatus("Error loading subjects", true);
      }
    },

    logout()
    {
      localStorage.removeItem('token');
      localStorage.removeItem('userId');
      this.$router.push('/login');
    },

    async fetchClasses()
    {
      this.selectedClassId = null;
      this.students = [];
      try
      {
        const res = await apiClient.get(`/teacher/subjects/${this.selectedSubjectId}/classes`);
        this.availableClasses = res.data.data;
      }
      catch(error)
      {
        this.showStatus("Error loading classes", true);
      }
    },

    async fetchDiary()
    {
      if(!this.selectedSubjectId || !this.selectedClassId) return;
      try
      {
        const res = await apiClient.get(`/teacher/classes/${this.selectedClassId}/subjects/${this.selectedSubjectId}`);
        this.students = res.data.data.students.map(s => ({
          studentId: s.studentId,
          studentName: s.studentName,
          grades: s.grades ? s.grades.map(g => g.value) : []
        }));
      }
      catch(error)
      {
        this.showStatus("Error fetching diary", true);
      }
    },

    async addGrade(student, value)
    {
      try
      {
        const payload = {
          studentId: student.studentId,
          subjectId: this.selectedSubjectId,
          value: value
        };
        await apiClient.post('/teacher/grades', payload);
        student.grades.push(value);
        this.showStatus(`Created grade ${value} for ${student.studentName}`, false);
      }
      catch(error)
      {
        this.showStatus("Error creating grade", true);
      }
    },

    getColorClass(value)
    {
      const num = parseFloat(value);
      if(num >= 2.0 && num < 3.0) return "grade-2";
      if(num >= 3.0 && num < 3.5) return "grade-3";
      if(num >= 3.5 && num < 4.5) return "grade-4";
      if(num >= 4.5 && num < 5.5) return "grade-5";
      if(num >= 5.5 && num < 6.0) return "grade-6";
      return "";
    },

    showStatus(msg, isErr)
    {
      this.message = msg;
      this.isError = isErr;
      setTimeout(() => { this.message = ""; }, 3000);
    }
  }
};
</script>

<style scoped>
.dashboard-container {
  padding: 40px;
  max-width: 1300px;
  margin: 0 auto;
  color: #e0e0e0;
  background: #1a1a1a;
  min-height: 100vh;
  font-family: 'Segoe UI', Roboto, sans-serif;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header h2 {
  font-size: 24px;
  font-weight: 600;
  color: #fff;
}

.logout-btn {
  padding: 10px 20px;
  background: transparent;
  color: #ff4d4d;
  border: 1px solid #ff4d4d;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s;
}

.logout-btn:hover {
  background: #ff4d4d;
  color: white;
  box-shadow: 0 4px 12px rgba(255, 77, 77, 0.2);
}

.selection-bar {
  display: flex;
  gap: 25px;
  margin-bottom: 30px;
  background: #252525;
  padding: 25px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.3);
}

.select-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.select-group label {
  font-size: 12px;
  text-transform: uppercase;
  color: #888;
  letter-spacing: 1px;
}

select {
  padding: 12px;
  background: #333;
  color: white;
  border: 1px solid #444;
  border-radius: 8px;
  min-width: 250px;
  outline: none;
  transition: border-color 0.2s;
}

select:focus {
  border-color: #4caf50;
}

.table-wrapper {
  background: #252525;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0,0,0,0.3);
}

.grades-table {
  width: 100%;
  border-collapse: collapse;
}

.grades-table th {
  background: #2d2d2d;
  padding: 18px;
  text-align: left;
  font-size: 13px;
  color: #888;
  text-transform: uppercase;
}

.grades-table td {
  padding: 16px;
  border-bottom: 1px solid #333;
}

.grades-table tr:hover {
  background: rgba(255, 255, 255, 0.02);
}

.student-name {
  font-weight: 500;
  font-size: 15px;
}

.index-num {
  color: #666;
  font-weight: bold;
}

.grades-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.grade-badge {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  font-weight: bold;
  font-size: 14px;
}

.grade-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 36px;
  height: 36px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  transition: transform 0.2s, box-shadow 0.2s;
}

.action-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(0,0,0,0.3);
}

.btn-grade-2, .grade-2 { background-color: #ff4d4d !important; color: rgb(0, 0, 0) !important; }
.btn-grade-3, .grade-3 { background-color: #ffa500 !important; color: rgb(0, 0, 0) !important; }
.btn-grade-4, .grade-4 { background-color: #ffeb3b !important; color: rgb(0, 0, 0) !important; }
.btn-grade-5, .grade-5 { background-color: #90ee90 !important; color: rgb(0, 0, 0) !important; }
.btn-grade-6, .grade-6 { background-color: #006400 !important; color: rgb(0, 0, 0) !important; }

.placeholder-state, .empty-state {
  text-align: center;
  padding: 80px;
  background: rgba(255, 255, 255, 0.02);
  border: 2px dashed #333;
  border-radius: 12px;
  color: #666;
}

.empty-icon { font-size: 40px; margin-bottom: 10px; }

.message-box {
  position: fixed;
  bottom: 30px;
  right: 30px;
  padding: 15px 25px;
  border-radius: 10px;
  color: white;
  z-index: 1000;
  box-shadow: 0 10px 30px rgba(0,0,0,0.4);
}

.success { background: #4caf50; }
.error { background: #f44336; }

.slide-fade-enter-active { transition: all 0.3s ease-out; }
.slide-fade-leave-active { transition: all 0.2s cubic-bezier(1, 0.5, 0.8, 1); }
.slide-fade-enter-from, .slide-fade-leave-to {
  transform: translateX(20px);
  opacity: 0;
}
</style>
