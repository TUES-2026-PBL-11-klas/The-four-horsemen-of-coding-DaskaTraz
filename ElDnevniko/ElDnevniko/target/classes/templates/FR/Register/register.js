document.getElementById("registerForm").addEventListener("submit", function (e) {
  e.preventDefault();

  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;
  const confirmPassword = document.getElementById("confirmPassword").value;

  if (password !== confirmPassword) {
    alert("Passwords do not match!");
    return;
  }

  const user = {
    email: email,
    password: password
  };

  fetch("/api/register", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(user)
  })
  .then(response => {
    if (response.ok) {
      alert("Registration successful!");
    } else {
      alert("Error during registration");
    }
  })
  .catch(error => console.error(error));
});