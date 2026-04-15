document.querySelectorAll(".row").forEach((row, index) => {
  const name = row.dataset.name;

  row.innerHTML = `${index + 1} | ${name} | `;

  for (let i = 2; i <= 6; i++) {
    const btn = document.createElement("button");
    btn.textContent = i;
    btn.classList.add(`btn-${i}`);
    btn.onclick = () => setGrade(row, i);
    row.appendChild(btn);
  }

  const input = document.createElement("input");
  input.className = "grade-input";
  input.value = "";

  // позволява само числа и 1 точка
  input.addEventListener("input", () => {
    input.value = input.value.replace(/[^0-9.]/g, "");

    // само една точка
    const parts = input.value.split(".");
    if (parts.length > 2) {
      input.value = parts[0] + "." + parts[1];
    }
  });

  // проверка при излизане
  input.addEventListener("blur", () => {
    if (input.value === "") return;

    const num = parseFloat(input.value);

    if (isNaN(num) || num < 2 || num > 6) {
      alert("Grade must be between 2 and 6!");
      input.value = "";
      return;
    }

    input.value = num.toFixed(2);
  });

  row.appendChild(input);
});

function setGrade(row, grade) {
  const input = row.querySelector(".grade-input");
  input.value = grade.toFixed(2);
}