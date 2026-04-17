<template>
  <div class="container">

    <!-- LEFT: SUBJECTS -->
    <div class="subjects-box">

      <h3>My Subjects</h3>

      <div
        v-for="sub in subjects"
        :key="sub.id"
        class="subject-row"
        @click="selectSubject(sub)"
      >
        <span>{{ sub.name }}</span>
        <span>{{ getGrade(sub.id) }}</span>
      </div>

    </div>

    <!-- RIGHT: CHART + PROGRESS -->
    <div class="progress-box" v-if="selectedSubject">

      <h2>{{ selectedSubject.name }} Progress</h2>

      <!-- 🎯 PROGRESS BAR -->
      <div class="progress-bar">
        <div
          class="progress-fill"
          :style="{ width: progressPercent + '%' }"
        ></div>
      </div>

      <p>{{ progressPercent.toFixed(0) }}% improvement</p>

      <!-- 📈 CHART -->
      <canvas ref="chartCanvas"></canvas>

    </div>

  </div>
</template>

<script>
import apiClient from "@/api/axios";;
import {
  Chart,
  LineController,
  LineElement,
  PointElement,
  LinearScale,
  CategoryScale
} from "chart.js";

Chart.register(LineController, LineElement, PointElement, LinearScale, CategoryScale);

export default {
  data() {
    return {
      subjects: [],
      grades: [],
      selectedSubject: null,
      progressData: [],
      chart: null
    };
  },

  computed: {
    // 🎯 simple progress calc
    progressPercent() {
      if (!this.progressData.length) return 0;

      const first = this.progressData[0].value;
      const last = this.progressData[this.progressData.length - 1].value;

      return ((last - first) / 6) * 100 + 50; // normalize
    }
  },

  mounted() {
    this.fetchSubjects();
    this.fetchGrades();
  },

  methods: {

    async fetchSubjects() {
      const res = await apiClient.get("/api/student/subjects");
      this.subjects = res.data.data;
    },

    async fetchGrades() {
      const res = await apiClient.get("/api/student/grades");
      this.grades = res.data.data;
    },

    getGrade(subjectId) {
      const row = this.grades.find(g => g.subjectId === subjectId);
      return row ? row.averageGrade : "-";
    },

    async selectSubject(subject) {
      this.selectedSubject = subject;

      const res = await apiClient.get(
        `/api/student/subjects/${subject.id}/grade-graph`
      );

      this.progressData = res.data.data;

      this.renderChart();
    },

    // 📈 CHART RENDER
    renderChart() {
      const ctx = this.$refs.chartCanvas;

      if (this.chart) {
        this.chart.destroy();
      }

      this.chart = new Chart(ctx, {
        type: "line",
        data: {
          labels: this.progressData.map(p => p.date),
          datasets: [
            {
              label: "Grades",
              data: this.progressData.map(p => p.value),
              borderColor: "#4caf50",
              tension: 0.3
            }
          ]
        },
        options: {
          responsive: true,
          scales: {
            y: {
              min: 2,
              max: 6
            }
          }
        }
      });
    }
  }
};
</script>

<style scoped>
.container {
  display: flex;
  gap: 40px;
  padding: 30px;
  background: #1e1e1e;
  color: white;
  min-height: 100vh;
}

.subjects-box {
  width: 280px;
}

.subject-row {
  display: flex;
  justify-content: space-between;
  padding: 10px;
  cursor: pointer;
}

.subject-row:hover {
  background: #333;
}

/* 🎯 PROGRESS BAR */
.progress-bar {
  width: 100%;
  height: 18px;
  background: #333;
  border-radius: 10px;
  overflow: hidden;
  margin: 15px 0;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #4caf50, #8bc34a);
  width: 0%;
  transition: width 0.6s ease;
}
</style>
