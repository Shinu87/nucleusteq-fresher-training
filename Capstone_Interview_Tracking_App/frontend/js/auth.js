document
  .getElementById("signupForm")
  .addEventListener("submit", function (event) {
    event.preventDefault();

    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const data = {
      name: name,
      email: email,
      password: password,
      role: "CANDIDATE",
    };

    fetch("http://localhost:8080/api/auth/signup", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    })
      .then((response) => response.json())
      .then((result) => {
        document.getElementById("message").innerText = "Signup successful!";
      })
      .catch((error) => {
        document.getElementById("message").innerText = "Error during signup!";
        console.error("Error:", error);
      });
  });
