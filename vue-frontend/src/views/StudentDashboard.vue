<template>
  <div class="container">

    <div class="subjects-box">
      <h3>Subjects</h3>

      <div
        v-for="sub in subjects"
        :key="sub.id"
        class="subject-row"
        :class="{ active: selectedSubject && selectedSubject.id === sub.id }"
        @click="selectSubject(sub)"
      >
        <span class="subject-name">{{ sub.subjectName }}</span>
        <span class="subject-avg" :class="avgClass(getAverage(sub.id))">
          {{ getAverage(sub.id) }}
        </span>
      </div>

      <div v-if="subjects.length === 0" class="empty">
        No subjects found.
      </div>

      <button @click="logout" class="logout-btn">Logout</button>
    </div>

    <div class="main-box">

      <div v-if="!selectedSubject" class="placeholder">
        Select a subject from the left menu to view your grades.
      </div>

      <div v-else>
        <h2>{{ selectedSubject.subjectName }}</h2>

        <div class="grades-section">
          <h4>Current Grades</h4>
          <div v-if="currentGrades.length === 0" class="empty">No grades available.</div>
          <div class="grades-row">
            <div
              v-for="(grade, index) in currentGrades"
              :key="index"
              class="grade-container"
              @mouseenter="activeGradeIndex = index"
              @mouseleave="activeGradeIndex = null"
            >
              <span class="grade-badge" :class="gradeClass(grade.value)">
                {{ grade.value }}
              </span>

              <transition name="fade">
                <div v-if="activeGradeIndex === index" class="grade-info-popup">
                  <div class="popup-content">
                    <p><strong>Teacher:</strong><br> {{ grade.teacherName || 'Unknown' }}</p>
                    <p><strong>Date:</strong><br> {{ formatDate(grade.dateAdded || grade.date) }}</p>
                  </div>
                  <div class="popup-arrow"></div>
                </div>
              </transition>
            </div>
          </div>
        </div>

        <div class="chart-section">
          <h4>Average Grade progress</h4>
          <div class="chart-wrapper">
            <canvas ref="chartCanvas"></canvas>
          </div>
        </div>
      </div>

    </div>

  </div>
</template>

<script>
import apiClient from "@/api/axios";
import {
  Chart,
  LineController,
  LineElement,
  PointElement,
  LinearScale,
  CategoryScale,
  Tooltip,
  Legend,
  Filler
} from "chart.js";

Chart.register(LineController, LineElement, PointElement, LinearScale, CategoryScale, Tooltip, Legend, Filler);

export default {
  data() {
    return {
      subjects: [],
      gradesMap: {},
      selectedSubject: null,
      progressData: [],
      chart: null,
      activeGradeIndex: null
    };
  },

  computed: {
    currentGrades() {
      if(!this.selectedSubject) return [];
      const row = this.gradesMap[this.selectedSubject.subjectName];
      return row ? row.grades : [];
    }
  },

  async mounted()
  {
    try
    {
      const [subjectsRes, gradesRes] = await Promise.all([
        apiClient.get("/student/subjects"),
        apiClient.get("/student/grades")
      ]);
      this.subjects = subjectsRes.data.data;

      const gradeRows = gradesRes.data.data;
      gradeRows.forEach(row => {
        this.gradesMap[row.subjectName] = row;
      });
    }
    catch(e)
    {
      console.error("Error loading student dashboard data:", e);
    }
  },

  methods: {
    formatDate(date)
    {
      if(!date) return "";
      const d = new Date(date);
      return d.toLocaleString('bg-BG',
      {
        day: '2-digit', month: '2-digit', year: 'numeric',
        hour: '2-digit', minute: '2-digit'
      });
    },

    logout()
    {
      localStorage.removeItem('token');
      localStorage.removeItem('userId');
      this.$router.push('/login');
    },

    getAverage(subjectId)
    {
      const subject = this.subjects.find(s => s.id === subjectId);
      if(!subject) return "-";
      const row = this.gradesMap[subject.subjectName];
      if(!row || !row.grades || row.grades.length === 0) return "-";
      return row.averageGrade.toFixed(2);
    },

    getColorClass(value)
    {
      if(!value || value === "-") return "";
      const num = parseFloat(value);
      if(num >= 2.0 && num < 3.0) return "grade-2";
      if(num >= 3.0 && num < 3.5) return "grade-3";
      if(num >= 3.5 && num < 4.5) return "grade-4";
      if(num >= 4.5 && num < 5.5) return "grade-5";
      if(num >= 5.5 && num <= 6.0) return "grade-6";
      return "";
    },

    avgClass(avg)
    {
      return this.getColorClass(avg);
    },

    gradeClass(val)
    {
      return this.getColorClass(val);
    },

    async selectSubject(subject)
    {
      this.selectedSubject = subject;
      this.progressData = [];

      if(this.chart)
      {
        this.chart.destroy();
        this.chart = null;
      }

      try
      {
        const res = await apiClient.get(`/student/subjects/${subject.id}/grade-graph`);
        this.progressData = res.data.data || [];
        this.$nextTick(() => { this.renderChart(); });
      }
      catch(e)
      {
        this.$nextTick(() => { this.renderChart(); });
      }
    },

    renderChart()
    {
      const ctx = this.$refs.chartCanvas;
      if(!ctx) return;

      const labels = this.progressData.map(p => new Date(p.date).toLocaleDateString('bg-BG'));
      const dataPoints = this.progressData.map(p => p.movingAverage);

      this.chart = new Chart(ctx, {
        type: "line",
        data: {
          labels: labels,
          datasets: [{
            label: "Average Grade",
            data: dataPoints,
            borderColor: "#4caf50",
            backgroundColor: "rgba(76, 175, 80, 0.1)",
            borderWidth: 3,
            tension: 0.4,
            fill: true,
            pointRadius: dataPoints.length > 0 ? 5 : 0,
            pointBackgroundColor: "#4caf50"
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          scales: {
            y: {
              min: 2,
              max: 6,
              ticks: { stepSize: 1, color: "#aaa" },
              grid: { color: "rgba(255, 255, 255, 0.05)" }
            },
            x: {
              ticks: { color: "#aaa" },
              grid: { display: false }
            }
          },
          plugins: {
            legend: { display: false },
            tooltip: { enabled: dataPoints.length > 0 }
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
  gap: 30px;
  padding: 30px;
  background: #1a1a1a;
  color: #e0e0e0;
  min-height: 100vh;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.subjects-box {
  width: 280px;
  flex-shrink: 0;
  background: #252525;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.3);
  height: fit-content;
}

.subjects-box h3 {
  margin-bottom: 20px;
  font-size: 14px;
  color: #888;
  text-transform: uppercase;
  letter-spacing: 1.5px;
}

.subject-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 15px;
  border-radius: 8px;
  cursor: pointer;
  margin-bottom: 8px;
  transition: all 0.2s ease;
  background: rgba(255, 255, 255, 0.02);
}

.subject-row:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateX(5px);
}

.subject-row.active {
  background: #333;
  border-left: 4px solid #4caf50;
}

.logout-btn {
  width: 100%;
  margin-top: 25px;
  padding: 12px;
  background: transparent;
  color: #ff4d4d;
  border: 1px solid #ff4d4d;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background: #ff4d4d;
  color: white;
  box-shadow: 0 4px 12px rgba(255, 77, 77, 0.3);
}

.main-box {
  flex: 1;
  background: #252525;
  padding: 35px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.3);
}

.placeholder {
  color: #666;
  margin-top: 100px;
  text-align: center;
  font-size: 16px;
  border: 2px dashed #333;
  padding: 50px;
  border-radius: 12px;
}

.grades-section h4, .chart-section h4 {
  color: #888;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 1px;
  margin-bottom: 15px;
}

.grades-row {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 40px;
}

.grade-container {
  position: relative;
}

.grade-badge {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  font-weight: bold;
  font-size: 16px;
  cursor: pointer;
  transition: transform 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 4px 8px rgba(0,0,0,0.2);
}

.grade-badge:hover {
  transform: scale(1.15);
  z-index: 10;
}

.grade-info-popup {
  position: absolute;
  top: 120%;
  left: 50%;
  transform: translateX(-50%);
  z-index: 100;
  background: #333;
  color: #fff;
  padding: 12px;
  border-radius: 8px;
  font-size: 11px;
  width: 160px;
  box-shadow: 0 10px 25px rgba(0,0,0,0.5);
  border: 1px solid #444;
}

.popup-content p {
  margin: 5px 0;
  line-height: 1.4;
}

.popup-arrow {
  position: absolute;
  top: -6px;
  left: 50%;
  transform: translateX(-50%);
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-bottom: 6px solid #333;
}

.chart-wrapper {
  height: 300px;
  background: rgba(255, 255, 255, 0.02);
  padding: 15px;
  border-radius: 10px;
}

.grade-2 { background-color: #ff4d4d !important; color: rgb(0, 0, 0) !important; }
.grade-3 { background-color: #ffa500 !important; color: rgb(0, 0, 0) !important; }
.grade-4 { background-color: #ffeb3b !important; color: rgb(0, 0, 0) !important; }
.grade-5 { background-color: #90ee90 !important; color: rgb(0, 0, 0) !important; }
.grade-6 { background-color: #006400 !important; color: rgb(0, 0, 0) !important; }

.subject-avg {
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
  min-width: 45px;
  text-align: center;
}

.empty { color: #555; font-style: italic; margin-bottom: 10px; }

.fade-enter-active, .fade-leave-active { transition: opacity 0.2s; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>
