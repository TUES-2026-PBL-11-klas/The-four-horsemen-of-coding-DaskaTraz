<template>
  <div class="container">
    <div class="card">
      <h2> Teacher Information</h2>

      <form @submit.prevent="finish">
        <div class="assignment-builder">
          <label>1. Select Subject</label>
          <select v-model="currentSubjectId" @change="fetchAvailableClasses" required>
            <option value="" disabled>Select Subject...</option>
            <option v-for="s in subjects" :key="s.id" :value="s.id">
              {{ s.subjectName }}
            </option>
          </select>

          <div v-if="currentSubjectId">
            <label>2. Select Available Classes</label>
            <small>(Hold Ctrl/Cmd for multiple selections)</small>
            <select v-model="currentClassIds" multiple class="multi-select">
              <option v-for="c in classes" :key="c.id" :value="c.id">
                {{ c.className }}
              </option>
            </select>
            <p v-if="classes.length === 0" class="error-text">There are no available classes for this subject.</p>
          </div>

          <button type="button" @click="addAssignment" class="add-btn" :disabled="!currentSubjectId || currentClassIds.length === 0">
            Add to assignments
          </button>
        </div>

        <div v-if="assignments.length > 0" class="selected-assignments">
          <h4>Your Assignments:</h4>
          <div class="class-tag" v-for="(assignment, index) in assignments" :key="index">
            <strong>{{ getSubjectName(assignment.subjectId) }}</strong>:
            {{ getSelectedClassNames(assignment) }}
            <span class="remove" @click="removeAssignment(index)">✖</span>
          </div>
        </div>

        <div v-if="message" :class="['message-box', isError ? 'error' : 'success']">{{ message }}</div>

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
      allClassesNames: [],
      assignments: [],
      currentSubjectId: "",
      currentClassIds: [],
      message: "",
      isError: false
    };
  },
  async mounted() {
    try {
      const [subRes, classNamesRes] = await Promise.all([
        apiClient.get('/auth/subjects'),
        apiClient.get('/auth/classes')
      ]);
      this.subjects = subRes.data.data;
      this.allClassesNames = classNamesRes.data.data;
    }
    catch(error)
    {
      this.isError = true;
      this.message = "Error loading data.";
    }
  },
  methods: {
    async fetchAvailableClasses()
    {
      if(!this.currentSubjectId) return;
      this.currentClassIds = [];
      try
      {
        const response = await apiClient.get(`/auth/subjects/${this.currentSubjectId}/available-classes`);
        this.classes = response.data.data;
      }
      catch(error)
      {
        console.error("Error loading available classes:", error);
      }
    },

    addAssignment()
    {
      const existing = this.assignments.find(a => a.subjectId === this.currentSubjectId);
      if(existing)
      {
        existing.classIds = [...new Set([...existing.classIds, ...this.currentClassIds])];
      }
      else
      {
        this.assignments.push({
          subjectId: this.currentSubjectId,
          classIds: [...this.currentClassIds]
        });
      }
      this.currentSubjectId = "";
      this.currentClassIds = [];
      this.classes = [];
    },
    getSelectedClassNames(assignment)
    {
      return this.allClassesNames
        .filter(c => assignment.classIds.includes(c.id))
        .map(c => c.className)
        .join(', ');
    },
    getSubjectName(id)
    {
      const sub = this.subjects.find(s => s.id === id);
      return sub ? sub.subjectName : '';
    },
    removeAssignment(index)
    {
      this.assignments.splice(index, 1);
    },
    async finish()
    {
    }
  }
};
</script>

<style scoped>
.container { min-height: 100vh; display: flex; justify-content: center; align-items: center; background: linear-gradient(135deg, #af6d1c, #ffd200); font-family: 'Segoe UI', sans-serif; }
.card.wide {
  width: 90%;
  max-width: 800px;
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0,0,0,0.2);
}
label { display: block; color: #000; font-weight: 600; margin-bottom: 5px; font-size: 13px; text-transform: uppercase; }
.builder {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  align-items: start;
}

.add-btn, .finish-btn, .message-box, h2 {
  grid-column: span 2;
}
select {
  width: 100%; padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 6px; color: #000;
}
.multi-select { height: 100px; }

.add-btn { background: #1e3c72; color: white; border: none; padding: 8px; border-radius: 4px; cursor: pointer; width: 100%; margin-bottom: 20px; }

.preview-area { background: #f9f9f9; padding: 15px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #eee; }
.assignment-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 8px; border-bottom: 1px solid #ddd; color: #000; font-size: 14px;
}
.remove { color: #d9534f; cursor: pointer; font-weight: bold; padding: 0 5px; }

.finish-btn {
  width: 100%; padding: 14px; background-color: #28a745; color: white; border: none; border-radius: 6px; cursor: pointer; font-weight: bold; font-size: 16px;
}
.finish-btn:disabled { background-color: #ccc; cursor: not-allowed; }

.message-box { padding: 10px; margin-top: 15px; border-radius: 5px; text-align: center; font-weight: bold; }
.error { background-color: #ffe6e6; color: #d9534f; border: 1px solid #d9534f; }
.success { background-color: #e6ffe6; color: #28a745; border: 1px solid #28a745; }
</style>
