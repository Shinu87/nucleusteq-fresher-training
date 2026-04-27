// getting form
const signupForm = document.getElementById("signupForm");

if (signupForm) {
  signupForm.addEventListener("submit", async function (e) {
    e.preventDefault();

    const msg = document.getElementById("message");

    // getting values
    let name = document.getElementById("name").value.trim();
    let email = document.getElementById("email").value.trim();
    let password = document.getElementById("password").value;

    msg.textContent = "";

    // validations

    if (name === "") {
      msg.textContent = "Name is required";
      return;
    }

    if (email === "") {
      msg.textContent = "Email is required";
      return;
    }

    let emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(email)) {
      msg.textContent = "Enter valid email";
      return;
    }

    if (password === "") {
      msg.textContent = "Password is required";
      return;
    }

    if (password.length < 6) {
      msg.textContent = "Password must be at least 6 characters";
      return;
    }

    let passPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).+$/;

    if (!passPattern.test(password)) {
      msg.textContent =
        "Password must have upper, lower, number and special char";
      return;
    }

    // send data using api.js

    let data = {
      name: name,
      email: email,
      password: password,
      role: "CANDIDATE",
    };

    try {
      await apiPost("/auth/signup", data); // using helper

      msg.textContent = "Account created! Redirecting...";
      msg.className = "msg success";

      setTimeout(() => {
        window.location.href = "login.html";
      }, 1200);
    } catch (err) {
      msg.textContent = err.message || "Signup failed";
      msg.className = "msg error";
    }
  });
}

// getting login form
const loginForm = document.getElementById("loginForm");

if (loginForm) {
  loginForm.addEventListener("submit", async function (e) {
    e.preventDefault(); // stop reload

    const msg = document.getElementById("message");

    // taking values
    let email = document.getElementById("email").value.trim();
    let password = document.getElementById("password").value;

    msg.textContent = "";

    //  validations

    if (email === "") {
      msg.textContent = "Email is required";
      return;
    }

    let emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailPattern.test(email)) {
      msg.textContent = "Enter valid email";
      return;
    }

    if (password === "") {
      msg.textContent = "Password is required";
      return;
    }

    // send data

    let data = {
      email: email,
      password: password,
    };

    try {
      // using api helper
      let result = await apiPost("/auth/login", data);

      // store user in local storage
      localStorage.setItem("user", JSON.stringify(result));

      // redirect based on role
      if (result.role === "HR") {
        window.location.href = "dashboard.html";
      } else if (result.role === "PANEL") {
        window.location.href = "panel-dashboard.html";
      } else {
        window.location.href = "candidate-portal.html";
      }
    } catch (err) {
      msg.textContent = err.message || "Login failed";
      msg.className = "msg error";
    }
  });
}
