<template>
  <div class="container">
    <div class="card">
      <h2>Teacher Information</h2>

      <form @submit.prevent="finish">

        <div class="assignment-builder">
          <label>Choose Subject</label>
          <select v-model="currentSubjectId">
            <option value="" disabled>Choose...</option>
            <option v-for="s in subjects" :key="s.id" :value="s.id">
              {{ s.subjectName }}
            </option>
          </select>

          <label>Choose Classes <br><small>(Hold Ctrl/Cmd to select multiple)</small></label>
          <select v-model="currentClassIds" multiple class="multi-select">
            <option v-for="c in classes" :key="c.id" :value="c.id">
              {{ c.className }}
            </option>
          </select>

          <button type="button" @click="addAssignment" class="add-btn">
            Add Subject to Schedule
          </button>
        </div>

        <div v-if="assignments.length > 0" class="selected-assignments">
          <h4>Your Schedule:</h4>
          <div class="class-tag" v-for="(assignment, index) in assignments" :key="index">
            <strong>{{ getSubjectName(assignment.subjectId) }}</strong>:
            {{ getClassNames(assignment.classIds) }}
            <span class="remove" @click="removeAssignment(index)">✖</span>
          </div>
        </div>

        <div v-if="message" :class="['message-box', isError ? 'error' : 'success']">
          {{ message }}
        </div>
        <button type="submit" class="finish-btn" :disabled="assignments.length === 0">
          Finish Registration
        </button>

      </form>
    </div>
  </div>
</template>

<script>
import apiClient from '@/api/axios';

export default {
  data() {
    return {
      subjects: [],
      classes: [],
      assignments: [],
      currentSubjectId: "",
      currentClassIds: [],
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
      const [subjectsRes, classesRes] = await Promise.all([
        apiClient.get('/auth/subjects'),
        apiClient.get('/auth/classes')
      ]);

      this.subjects = subjectsRes.data.data;
      this.classes = classesRes.data.data;
    }
    catch(error)
    {
      console.error("Error loading data:", error);
      this.isError = true;
      this.message = "Failed to load data. Please refresh the page.";
    }
  },
  methods: {
    addAssignment()
    {
    if(!this.currentSubjectId || this.currentClassIds.length === 0)
    {
      this.message = "Моля, избери предмет и поне един клас.";
      this.isError = true;
      return;
    }

    const existingIndex = this.assignments.findIndex(a => a.subjectId === this.currentSubjectId);

    if(existingIndex !== -1)
    {
      const combined = [...this.assignments[existingIndex].classIds, ...this.currentClassIds];

      this.assignments[existingIndex].classIds = [...new Set(combined)];

      this.message = "Класовете бяха обновени за този предмет.";
      this.isError = false;
    }
    else
    {
      // Ако е нов предмет, го добавяме нормално
      this.assignments.push({
        subjectId: this.currentSubjectId,
        classIds: [...this.currentClassIds]
      });
      this.message = "";
      this.isError = false;
    }

    this.currentSubjectId = "";
    this.currentClassIds = [];
  },
    removeAssignment(index)
    {
      this.assignments.splice(index, 1);
    },
    getSubjectName(id)
    {
      const sub = this.subjects.find(s => s.id === id);
      return sub ? sub.subjectName : '';
    },
    getClassNames(ids)
    {
      return this.classes
        .filter(c => ids.includes(c.id))
        .map(c => c.className)
        .join(', ');
    },
    async finish()
    {
      this.message = "";
      this.isError = false;

      const userIdRaw = localStorage.getItem("userId");
      const userId = userIdRaw ? parseInt(userIdRaw, 10) : null;

      const formattedAssignments = this.assignments.map(a => ({
        subjectId: parseInt(a.subjectId, 10),
        classIds: a.classIds.map(id => parseInt(id, 10))
      }));

      const payload = {
        userId: userId,
        assignments: formattedAssignments
      };

      console.log("Final Payload to Server:", payload);
      try
      {
        const response = await apiClient.post('/auth/register-teacher', payload);

        this.message = "Успешна регистрация! Проверете имейла си.";
        this.isError = false;

        setTimeout(() => {
          this.$router.push("/verify");
        }, 1500);
      }
      catch(error)
      {
        console.error("Error registering teacher:", error);
        this.isError = true;

        const serverMessage = error.response?.data?.message;
        this.message = serverMessage || "Error registering teacher.";
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
