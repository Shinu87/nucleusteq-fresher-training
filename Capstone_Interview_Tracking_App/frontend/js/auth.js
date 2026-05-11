/* auth.js - login, signup and password setup functions */
// helper to read query parameters from URL (used in set-password page)
function getQueryParam(name) {
  const sp = new URLSearchParams(window.location.search);
  return sp.get(name);
}

function setMsg(type, text) {
  const msg = document.getElementById("message");
  if (!msg) return;
  msg.className = "msg " + type;
  msg.textContent = text;
}

document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll(".password-toggle").forEach((span) => {
    span.addEventListener("click", () => {
      const targetId = span.getAttribute("data-target");
      const input = document.getElementById(targetId);
      if (!input) return;
      if (input.type === "password") {
        input.type = "text";
        span.textContent = "🙈";
      } else {
        input.type = "password";
        span.textContent = "👁";
      }
    });
  });
});

/* signup form handler
   takes name, email, mobile, gender, dob
   password setup link is sent by backend */
const signupForm = document.getElementById("signupForm");
if (signupForm) {
  const dobInput = document.getElementById("dob");
  if (dobInput) {
    const today = new Date();
    const maxDob = new Date(
      today.getFullYear() - 18,
      today.getMonth(),
      today.getDate(),
    );
    dobInput.max = maxDob.toISOString().split("T")[0];
  }

  const signupRules = [
    { id: "name", label: "Full Name", required: true, minLength: 2 },
    { id: "email", label: "Email", required: true, type: "email" },
    { id: "mobile", label: "Mobile", required: true, type: "phone" },
    { id: "gender", label: "Gender", required: true, type: "select" },
    {
      id: "dob",
      label: "Date of Birth",
      required: true,
      requiredMsg: "Date of birth is required.",
      custom: (value) => {
        if (!value) return "Date of birth is required.";
        const dob = new Date(value);
        const today = new Date();
        const age18Date = new Date(
          dob.getFullYear() + 18,
          dob.getMonth(),
          dob.getDate(),
        );
        if (age18Date > today) {
          return "Candidate must be at least 18 years old.";
        }
        const age60Date = new Date(
          dob.getFullYear() + 60,
          dob.getMonth(),
          dob.getDate(),
        );
        if (age60Date <= today) {
          return "Candidate exceeds maximum eligible working age.";
        }
        return null;
      },
    },
  ];
  wireFieldValidation(signupRules);

  signupForm.addEventListener("submit", function (e) {
    e.preventDefault();

    if (!validateAll(signupRules)) {
      setMsg("error", "Please fix the highlighted fields.");
      return;
    }

    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("email").value.trim();
    const mobile = document.getElementById("mobile").value.trim();
    const gender = document.getElementById("gender").value;
    const dateOfBirth = document.getElementById("dob").value;

    const data = {
      name,
      email,
      mobile,
      gender,
      dateOfBirth,
      role: "CANDIDATE",
    };

    // call signup API
    apiPost("/auth/signup", data)
      .then(() => {
        setMsg(
          "success",
          "✅ Account created! Please check your email for a password setup link.",
        );
        signupForm.reset();
        clearAllErrors(signupRules);
      })
      .catch((err) => {
        setMsg("error", err.message || "Signup failed. Try again.");
      });
  });
}

/* login handler */
const loginForm = document.getElementById("loginForm");
if (loginForm) {
  const loginRules = [
    { id: "email", label: "Email", required: true, type: "email" },
    { id: "password", label: "Password", required: true },
  ];
  wireFieldValidation(loginRules);

  loginForm.addEventListener("submit", function (e) {
    e.preventDefault();

    if (!validateAll(loginRules)) {
      setMsg("error", "Please fix the highlighted fields.");
      return;
    }

    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;

    const encodedPassword = btoa(password);

    apiPost("/auth/login", {
      email: email,
      password: encodedPassword,
    })
      .then((result) => {
        localStorage.setItem(
          "user",
          JSON.stringify({
            ...result,
            password: password,
          }),
        );

        window.location.href = "index.html";
      })
      .catch((err) => {
        setMsg("error", err.message || "Login failed. Check credentials.");
      });
  });
}

/* set password using token from email link */
const setPasswordForm = document.getElementById("setPasswordForm");
if (setPasswordForm) {
  const token = getQueryParam("token");

  if (!token) {
    setMsg("error", "Invalid link - token is missing.");
    setPasswordForm.querySelector('button[type="submit"]').disabled = true;
  }

  const setPwdRules = [
    {
      id: "password",
      label: "Password",
      required: true,
      minLength: 6,
    },
    {
      id: "confirmPassword",
      label: "Confirm Password",
      required: true,
      watches: ["password"],
      custom: (value) => {
        const pwd = document.getElementById("password").value;
        if (pwd && value !== pwd) return "Passwords do not match.";
        return null;
      },
    },
  ];
  wireFieldValidation(setPwdRules);

  setPasswordForm.addEventListener("submit", function (e) {
    e.preventDefault();

    if (!validateAll(setPwdRules)) {
      setMsg("error", "Please fix the highlighted fields.");
      return;
    }

    const password = document.getElementById("password").value;
    const encodedPassword = btoa(password);

    // call API
    fetch("http://localhost:8080/api/auth/set-password", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        token,
        password: encodedPassword,
      }),
    })
      .then(async (res) => {
        if (!res.ok) {
          const err = await res.json().catch(() => ({}));
          throw err;
        }
        return res.json();
      })
      .then(() => {
        setMsg(
          "success",
          "✅ Password set successfully! Redirecting to login...",
        );

        setTimeout(() => {
          window.location.href = "login.html";
        }, 1500);
      })
      .catch((err) => {
        setMsg("error", err.message || "Failed to set password.");
      });
  });
}

document.getElementById("name").addEventListener("input", function () {
  this.value = this.value.replace(/[^a-zA-Z\s]/g, "");
});
