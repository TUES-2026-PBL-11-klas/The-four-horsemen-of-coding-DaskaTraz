let selectedSubject = "";

function toggleSubjects() {
  const list = document.getElementById("subjectsList");
  const classes = document.getElementById("classesBox");

  const isOpen = list.style.display === "block";

  if (isOpen) {
    list.style.display = "none";
    classes.style.display = "none";
  } else {
    list.style.display = "block";
  }
}

function showClasses(subject) {
  selectedSubject = subject;
  document.getElementById("classesBox").style.display = "flex";
}

function selectClass(grade) {
  const box = document.getElementById("selectionBox");
  const btn = document.getElementById("addBtn");
  const list = document.getElementById("subjectsList");
  const classes = document.getElementById("classesBox");

  box.value = selectedSubject + " " + grade + " клас";
  btn.style.display = "block";

  // 🔥 ЗАТВАРЯ ВСИЧКО
  list.style.display = "none";
  classes.style.display = "none";
}

function goToPage() {
  window.location.href = "grades.html";
}