/* api.js  –  common functions for all pages */

const BASE_URL = "http://localhost:8080/api";

/* checking user role */
function requireRole(...roles) {
  const user = getUser();

  // if user not logged in, go to login page
  if (!user) {
    window.location.href = "login.html";
    return null;
  }

  // if role not allowed
  if (roles.length && !roles.includes(user.role)) {
    alert("Access denied");
    window.location.href = "login.html";
    return null;
  }

  return user;
}

/*  get user from localStorage  */
function getUser() {
  try {
    return JSON.parse(localStorage.getItem("user"));
  } catch {
    return null; // if error, return null
  }
}

/*  logout function */
function logout() {
  localStorage.removeItem("user"); // remove user data
  window.location.href = "login.html"; // go to login page
}

/* GET request  */
async function apiGet(path) {
  const res = await fetch(`${BASE_URL}${path}`);

  // if error from backend
  if (!res.ok) {
    const e = await res.json().catch(() => ({}));
    throw e;
  }

  return res.json();
}

/* POST request */
async function apiPost(path, body) {
  const res = await fetch(`${BASE_URL}${path}`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });

  // check error
  if (!res.ok) {
    const e = await res.json().catch(() => ({}));
    throw e;
  }

  return res.json();
}

/* PUT request */
async function apiPut(path, body = null) {
  const options = {
    method: "PUT",
  };

  // if body is present, add headers + body
  if (body !== null) {
    options.headers = { "Content-Type": "application/json" };
    options.body = JSON.stringify(body);
  }

  const res = await fetch(`${BASE_URL}${path}`, options);

  // error check
  if (!res.ok) {
    const e = await res.json().catch(() => ({}));
    throw e;
  }

  return res.json();
}

/* toast message */
function showToast(msg, type = "info") {
  let container = document.getElementById("toast-container");

  // create container if not present
  if (!container) {
    container = document.createElement("div");
    container.id = "toast-container";
    document.body.appendChild(container);
  }

  // create toast
  const t = document.createElement("div");
  t.className = `toast ${type}`;
  t.textContent = msg;

  container.appendChild(t);

  // remove after some time
  setTimeout(() => {
    t.remove();
  }, 3400);
}
