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
function _handleAuthFailure(res, silent) {
  if (silent) return false;
  if (res.status === 401) {
    localStorage.removeItem("user");
    window.location.href = "login.html";
    return true;
  }
  return false;
}

async function apiGet(path, opts = {}) {
  const res = await fetch(`${BASE_URL}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...getAuthHeader(),
    },
  });

  if (_handleAuthFailure(res, opts.silent)) return;

  if (!res.ok) {
    const e = await res.json().catch(() => ({}));
    throw e;
  }
  return res.json();
}

async function apiPost(path, body, opts = {}) {
  const res = await fetch(`${BASE_URL}${path}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...getAuthHeader(),
    },
    body: JSON.stringify(body),
  });

  if (_handleAuthFailure(res, opts.silent)) return;

  if (!res.ok) {
    const e = await res.json().catch(() => ({}));
    throw e;
  }
  return res.json();
}

async function apiPut(path, body = null, opts = {}) {
  const res = await fetch(`${BASE_URL}${path}`, {
    method: "PUT",
    headers: {
      "Content-Type": "application/json",
      ...getAuthHeader(),
    },
    body: body !== null ? JSON.stringify(body) : undefined,
  });

  if (_handleAuthFailure(res, opts.silent)) return;

  if (!res.ok) {
    const e = await res.json().catch(() => ({}));
    throw e;
  }
  return res.json();
}

/**
 * For multipart uploads (resume).
 */
async function apiPostForm(path, formData, opts = {}) {
  const res = await fetch(`${BASE_URL}${path}`, {
    method: "POST",
    headers: { ...getAuthHeader() },
    body: formData,
  });

  if (_handleAuthFailure(res, opts.silent)) return;

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

/*  LOADER HELPERS  */

function _ensureLoaderOverlay() {
  let overlay = document.getElementById("appLoaderOverlay");
  if (overlay) return overlay;

  overlay = document.createElement("div");
  overlay.id = "appLoaderOverlay";
  overlay.className = "app-loader-overlay";
  overlay.innerHTML = `
    <div class="app-loader-box">
      <div class="app-loader-spinner"></div>
      <div class="app-loader-text" id="appLoaderText">Loading…</div>
    </div>`;
  document.body.appendChild(overlay);
  return overlay;
}

/**
 * Shows the full-screen loader overlay.
 */
function showLoader(label) {
  const overlay = _ensureLoaderOverlay();
  const txt = overlay.querySelector("#appLoaderText");
  if (txt) txt.textContent = label || "Loading…";
  overlay.classList.add("active");
}

/** Hides the full-screen loader overlay. */
function hideLoader() {
  const overlay = document.getElementById("appLoaderOverlay");
  if (overlay) overlay.classList.remove("active");
}

/**
 * Wraps an async function with a button-level loader: disables the
 * button, swaps its text to `label`, runs `fn`, then restores the
 * button (even when fn throws). Prevents accidental double-submits.
 */
async function withButtonLoader(target, label, fn) {
  let btn = null;

  /* Resolve the button element from the supplied target */
  if (typeof target === "string") {
    btn = document.getElementById(target);
  } else if (target && target.target && target.target.tagName) {
    btn = target.currentTarget || target.target;
  } else if (target && target.tagName) {
    btn = target;
  }

  /* If we couldn't find a button just run the function */
  if (!btn) return await fn();

  const originalText = btn.innerHTML;
  const originalDisabled = btn.disabled;

  btn.disabled = true;
  btn.classList.add("is-loading");
  btn.innerHTML = `<span class="btn-spinner"></span> ${label || "Please wait…"}`;

  try {
    return await fn();
  } finally {
    btn.disabled = originalDisabled;
    btn.classList.remove("is-loading");
    btn.innerHTML = originalText;
  }
}

/**
 * Wraps an async function with the full-screen overlay loader.
 * Always hides the loader, even when fn throws.
 */
async function withLoader(label, fn) {
  showLoader(label);
  try {
    return await fn();
  } finally {
    hideLoader();
  }
}
