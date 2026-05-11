/* 
  home.js - Public homepage
*/

// no requireRole here — homepage is open to everyone
const currentUser = getUser();

/*  render the topbar based on who is viewing  */
function renderTopbar() {
  const wrap = document.getElementById("topbarActions");
  if (!wrap) return;

  if (!currentUser) {
    wrap.innerHTML = `
      <a class="home-btn home-btn-ghost" href="signup.html">Sign Up</a>
      <a class="home-btn" href="login.html">Login</a>
    `;
    return;
  }

  // logged-in
  const role = currentUser.role;
  let dashHref = "login.html";
  let dashLabel = "Go to Dashboard";
  if (role === "HR") {
    dashHref = "dashboard.html";
    dashLabel = "HR Dashboard";
  } else if (role === "PANEL") {
    dashHref = "panel-dashboard.html";
    dashLabel = "Panel Dashboard";
  } else if (role === "CANDIDATE") {
    dashHref = "candidate-portal.html";
    dashLabel = "My Portal";
  }

  wrap.innerHTML = `
    <span class="home-user-pill">
      ${escapeHtml(currentUser.name || currentUser.email || "User")}
      <small>${role}</small>
    </span>
    <a class="home-btn home-btn-ghost" href="${dashHref}">${dashLabel}</a>
    <button class="home-btn" onclick="logout()">Logout</button>
  `;
}

/*  adjust according  to the current viewer  */
function renderHero() {
  if (!currentUser) return;

  const sub = document.getElementById("heroSubtitle");
  if (!sub) return;

  if (currentUser.role === "CANDIDATE") {
    sub.textContent =
      "Welcome back! Pick a role below and tap Apply Now to start your application.";
  } else if (currentUser.role === "HR") {
    sub.textContent =
      "You are signed in as HR. Listings below reflect what candidates currently see.";
  } else if (currentUser.role === "PANEL") {
    sub.textContent =
      "You are signed in as a Panel member. This is the public view of active jobs.";
  }
}

/* 
   ACTIVE JOBS
*/
async function loadActiveJobs() {
  const container = document.getElementById("homeJobList");
  const countEl = document.getElementById("jobsCount");

  try {
    const jobs = await apiGet("/jobs/active", { silent: true });
    countEl.textContent = jobs.length;

    if (!jobs.length) {
      container.innerHTML =
        '<div class="empty-state"><div class="empty-icon">💼</div>' +
        "<p>No active jobs right now. Please check back later.</p></div>";
      return;
    }

    container.innerHTML = jobs.map(renderJobCard).join("");
  } catch (e) {
    countEl.textContent = "0";
    container.innerHTML =
      '<div class="empty-state"><div class="empty-icon">⚠️</div>' +
      "<p>Failed to load jobs. Please try again later.</p></div>";
  }
}

/* the action area is role-dependent  */
function renderJobCard(j) {
  const skills = (j.skills || [])
    .map((s) => `<span class="jd-skill-tag">${escapeHtml(s)}</span>`)
    .join("");

  const jobType = (j.jobType || "").replace("_", " ");
  let actionHtml;
  if (currentUser && currentUser.role === "CANDIDATE") {
    actionHtml = `<button class="btn-apply" onclick="applyToJob(${j.id})">Apply Now →</button>`;
  } else if (
    currentUser &&
    (currentUser.role === "HR" || currentUser.role === "PANEL")
  ) {
    actionHtml = `<div class="btn-disabled-note">Apply is available to candidates only</div>`;
  } else {
    actionHtml = `<button class="btn-apply" onclick="promptLoginToApply()">Login to Apply →</button>`;
  }

  return `
    <div class="jd-card">
      <h3>${escapeHtml(j.title || "")}</h3>
      <p>${escapeHtml(j.description || "")}</p>
      <p><b>📍</b> ${escapeHtml(j.location || "—")} &nbsp;|&nbsp; <b>🏷</b> ${escapeHtml(jobType || "—")}</p>
      <p><b>Exp:</b> ${j.minExperience ?? "—"}–${j.maxExperience ?? "—"} yrs &nbsp;|&nbsp;
         <b>💰</b> ₹${j.minSalary ?? "—"}–₹${j.maxSalary ?? "—"} LPA</p>
      <div class="jd-skills">${skills}</div>
      ${actionHtml}
    </div>
  `;
}

/*  candidate clicked Apply */
function applyToJob(jobId) {
  window.location.href = `candidate-portal.html?jobId=${jobId}`;
}

/*  anonymous user clicked Apply  */
function promptLoginToApply() {
  showToast("Please login as a candidate to apply.", "info");
  setTimeout(() => {
    window.location.href = "login.html";
  }, 900);
}

/*  tiny HTML escape so titles/descs don't break layout  */
function escapeHtml(str) {
  if (str === null || str === undefined) return "";
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");
}

/*  boot  */
window.addEventListener("DOMContentLoaded", () => {
  renderTopbar();
  renderHero();
  loadActiveJobs();
});
