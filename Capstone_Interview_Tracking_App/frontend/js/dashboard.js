// dashboard.js – HR Dashboard logic

const user = requireRole("HR");
let allCandidates = [];
let allJobs = [];

window.onload = () => {
  loadStats();
  loadSkillsForJD();
  loadJDList();
};

// navigation function to switch pages
function showSection(name) {
  // remove active class from all sections
  document
    .querySelectorAll(".page-section")
    .forEach((s) => s.classList.remove("active"));

  // remove active class from all nav items
  document
    .querySelectorAll(".nav-item")
    .forEach((n) => n.classList.remove("active"));

  // show selected section
  document.getElementById(`section-${name}`).classList.add("active");

  // set active class to clicked nav item
  event.currentTarget.classList.add("active");

  // hide candidate detail card when changing page
  const dd = document.getElementById("candidateDetailCard");
  if (dd) dd.style.display = "none";
}

// function to load dashboard stats
async function loadStats() {
  try {
    // get jobs and candidates together
    const [jobs, candidates] = await Promise.all([
      apiGet("/jobs"),
      apiGet("/candidates"),
    ]);

    // store data
    allJobs = jobs;
    allCandidates = candidates;

    // set counts in UI
    document.getElementById("statJobs").textContent = jobs.length;
    document.getElementById("statCandidates").textContent = candidates.length;

    // count selected candidates
    document.getElementById("statSelected").textContent = candidates.filter(
      (c) => c.status === "SELECTED",
    ).length;

    // count in progress candidates
    document.getElementById("statInProgress").textContent = candidates.filter(
      (c) => c.status === "IN_PROGRESS",
    ).length;
  } catch (e) {
    showToast("Failed to load dashboard stats", "error");
  }
}

// function to load skills for job description page
async function loadSkillsForJD() {
  try {
    // get all skills from backend
    const skills = await apiGet("/skills");

    const container = document.getElementById("jdSkillsContainer");

    // show skills as selectable chips
    container.innerHTML = skills
      .map(
        (s) =>
          `<label class="skill-chip" id="sc-${s.id}" onclick="toggleSkill(${s.id}, this)">
        <input type="checkbox" value="${s.id}"/> ${s.name}
       </label>`,
      )
      .join("");
  } catch (e) {
    showToast("Failed to load skills", "error");
  }
}

// toggle skill selection UI + checkbox state
function toggleSkill(id, el) {
  el.classList.toggle("selected");
  el.querySelector("input").checked = el.classList.contains("selected");
}

// get all selected skill ids
function getSelectedSkillIds() {
  return [...document.querySelectorAll("#jdSkillsContainer input:checked")].map(
    (cb) => parseInt(cb.value),
  );
}

// function to create new job description
async function createJD() {
  // get form values
  const title = document.getElementById("jdTitle").value.trim();
  const desc = document.getElementById("jdDesc").value.trim();
  const location = document.getElementById("jdLocation").value.trim();
  const minExp = document.getElementById("jdMinExp").value;
  const maxExp = document.getElementById("jdMaxExp").value;
  const minSal = document.getElementById("jdMinSal").value;
  const maxSal = document.getElementById("jdMaxSal").value;
  const jobType = document.getElementById("jdJobType").value;

  // get selected skills
  const skillIds = getSelectedSkillIds();

  const msgEl = document.getElementById("jdMsg");

  // check required fields
  if (
    !title ||
    !desc ||
    !location ||
    !minExp ||
    !maxExp ||
    !minSal ||
    !maxSal
  ) {
    showMsg(msgEl, "error", "Please fill all required fields.");
    return;
  }

  try {
    // send job data to backend
    await apiPost("/jobs", {
      title,
      description: desc,
      location,
      minExperience: parseInt(minExp),
      maxExperience: parseInt(maxExp),
      minSalary: parseFloat(minSal),
      maxSalary: parseFloat(maxSal),
      jobType,
      skillIds,
    });

    // success message
    showMsg(msgEl, "success", "Job Description created successfully!");

    // reset form and refresh data
    clearJDForm();
    loadJDList();
    loadStats();
    loadJobDropdown();
  } catch (e) {
    // error message
    showMsg(msgEl, "error", e.message || "Failed to create JD.");
  }
}

// function to reset JD form
function clearJDForm() {
  // clear all input fields
  [
    "jdTitle",
    "jdDesc",
    "jdLocation",
    "jdMinExp",
    "jdMaxExp",
    "jdMinSal",
    "jdMaxSal",
  ].forEach((id) => {
    document.getElementById(id).value = "";
  });

  // unselect all skills
  document.querySelectorAll("#jdSkillsContainer .skill-chip").forEach((el) => {
    el.classList.remove("selected");
    el.querySelector("input").checked = false;
  });
}

// function to load all job descriptions
async function loadJDList() {
  try {
    // get jobs from backend
    const jobs = await apiGet("/jobs");

    allJobs = jobs;

    const container = document.getElementById("jdList");

    // if no jobs found
    if (!jobs.length) {
      container.innerHTML =
        '<div class="empty-state"><div class="empty-icon">💼</div><p>No jobs created yet.</p></div>';
      return;
    }

    // show job cards
    container.innerHTML = jobs
      .map(
        (j) => `
      <div class="jd-card">
        <h3>${j.title}</h3>
        <p>${j.description}</p>

        <p>
          <b>📍</b> ${j.location} &nbsp;|&nbsp;
          <b>🏷</b> ${j.jobType?.replace("_", " ")}
        </p>

        <p>
          <b>Exp:</b> ${j.minExperience}–${j.maxExperience} yrs
          &nbsp;|&nbsp;
          <b>Salary:</b> ₹${j.minSalary}–₹${j.maxSalary} LPA
        </p>

        <!-- skills -->
        <div class="jd-skills">
          ${(j.skills || [])
            .map((s) => `<span class="jd-skill-tag">${s}</span>`)
            .join("")}
        </div>

        <p style="color:#888;font-size:12px;margin-top:8px;">
          ID: ${j.id}
        </p>
      </div>
    `,
      )
      .join("");

    // update dropdown and filters
    loadJobDropdown(jobs);
    loadJobFilter(jobs);
  } catch (e) {
    // error message
    showToast("Failed to load jobs", "error");
  }
}

// function to load job dropdown
function loadJobDropdown(jobs) {
  const sel = document.getElementById("cJobId");

  // if dropdown not found, stop
  if (!sel) return;

  const j = jobs || allJobs;

  // add default option + job options
  sel.innerHTML =
    '<option value="">-- Select Job --</option>' +
    j
      .map(
        (jb) => `<option value="${jb.id}">${jb.title} (ID:${jb.id})</option>`,
      )
      .join("");
}

/* ── Helpers ─────────────────────────────────────────── */
function showMsg(el, type, text) {
  el.style.display = "block";
  el.className = `msg ${type}`;
  el.textContent = text;
  setTimeout(() => {
    el.style.display = "none";
  }, 4000);
}
