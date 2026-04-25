const signupForm = document.getElementById("signupForm");

if (signupForm) {
  signupForm.addEventListener("submit", function (event) {
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
      .then((response) => {
        if (!response.ok) {
          return response.json().then((err) => {
            throw err;
          });
        }
        return response.json();
      })
      .then((result) => {
        const message = document.getElementById("message");
        message.innerText = "Signup successful!";
        message.classList.remove("error");
        message.classList.add("success");
        console.log(result);
      })
      .catch((error) => {
        const message = document.getElementById("message");
        message.innerText = error.message || "Error during signup!";
        message.classList.remove("success");
        message.classList.add("error");
        console.error(error);
      });
  });
}

const loginForm = document.getElementById("loginForm");

if (loginForm) {
  loginForm.addEventListener("submit", function (event) {
    event.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const data = {
      email: email,
      password: password,
    };

    fetch("http://localhost:8080/api/auth/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    })
      .then((response) => {
        if (!response.ok) {
          return response.json().then((err) => {
            throw err;
          });
        }
        return response.json();
      })
      .then((result) => {
        alert("Login Successful!");
        console.log("Login success for:", result.email);
      })
      .catch((error) => {
        alert(error.message || "Login failed!");
        console.error(error);
      });
  });
}
