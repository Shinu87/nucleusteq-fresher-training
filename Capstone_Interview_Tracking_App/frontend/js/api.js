/* api.js  shared helpers for all pages */

const BASE_URL = "http://localhost:8080/api";

/*  AUTH GUARD */
function requireRole(...roles) {
  const user = getUser();
  if (!user) {
    window.location.href = "login.html";
    return null;
  }
  if (roles.length && !roles.includes(user.role)) {
    alert("Access denied.");
    window.location.href = "login.html";
    return null;
  }
  return user;
}

function getUser() {
  try {
    return JSON.parse(localStorage.getItem("user"));
  } catch {
    return null;
  }
}

/**
 * Builds the Authorization header from stored credentials.
 * Credentials are stored at login as { email, password }.
 * Every API call re-sends them as HTTP Basic auth so the
 * AuthFilter can authenticate and populate the SecurityContext.
 */
function getAuthHeader() {
  const user = getUser();

  if (!user || !user.email || !user.password) return {};

  const encoded = btoa(
    unescape(encodeURIComponent(user.email + ":" + user.password)),
  );
  return {
    Authorization: "Basic " + encoded,
  };
}

function logout() {
  localStorage.removeItem("user");
  window.location.href = "login.html";
}

/* fetch helper functions */
async function apiGet(path) {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...getAuthHeader(),
    },
  });

  if (res.status === 401 || res.status === 403) {
    localStorage.removeItem("user");
    window.location.href = "login.html";
    return;
  }

  if (!res.ok) {
    const e = await res.json().catch(() => ({}));
    throw e;
  }
  return res.json();
}

async function apiPost(path, body) {
  const res = await fetch(`${BASE_URL}${path}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...getAuthHeader(),
    },
    body: JSON.stringify(body),
  });

  if (!res.ok) {
    const e = await res.json().catch(() => ({}));
    throw e;
  }
  return res.json();
}

async function apiPut(path, body = null) {
  const res = await fetch(`${BASE_URL}${path}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      ...getAuthHeader(),
    },
    body: body !== null ? JSON.stringify(body) : undefined,
  });

  if (res.status === 401 || res.status === 403) {
    localStorage.removeItem("user");
    window.location.href = "login.html";
    return;
  }

  if (!res.ok) {
    const e = await res.json().catch(() => ({}));
    throw e;
  }
  return res.json();
}

/* toast message styles */
function showToast(msg, type = "info") {
  let container = document.getElementById("toast-container");
  if (!container) {
    container = document.createElement("div");
    container.id = "toast-container";
    document.body.appendChild(container);
  }
  const t = document.createElement("div");
  t.className = `toast ${type}`;
  t.textContent = msg;
  container.appendChild(t);
  setTimeout(() => t.remove(), 3400);
}

/* badge helper styles */
function stageBadge(stage) {
  const map = {
    PROFILING: "badge-gray",
    SCREENING: "badge-blue",
    L1: "badge-purple",
    L2: "badge-orange",
    HR: "badge-yellow",
  };
  return `<span class="badge ${map[stage] || "badge-gray"}">${stage}</span>`;
}

function statusBadge(status) {
  const map = {
    IN_PROGRESS: "badge-blue",
    SELECTED: "badge-green",
    REJECTED: "badge-red",
  };
  return `<span class="badge ${map[status] || "badge-gray"}">${status?.replace("_", " ")}</span>`;
}

/** Interview status badge. */
function interviewStatusBadge(s) {
  const map = {
    SCHEDULED: "badge-blue",
    ONGOING: "badge-purple",
    COMPLETED: "badge-green",
    CANCELLED: "badge-red",
  };
  return `<span class="badge ${map[s] || "badge-gray"}">${s || "—"}</span>`;
}

function feedbackStatusBadge(s) {
  return s === "SELECTED"
    ? `<span class="badge badge-green">SELECTED</span>`
    : `<span class="badge badge-red">REJECTED</span>`;
}

function fmtDate(dt) {
  if (!dt) return "—";
  return new Date(dt).toLocaleString("en-IN", {
    dateStyle: "medium",
    timeStyle: "short",
  });
}

function starsHtml(rating) {
  let s = "";
  for (let i = 1; i <= 5; i++)
    s += `<span style="color:${i <= rating ? "#f39c12" : "#ddd"};font-size:16px;">★</span>`;
  return s;
}
