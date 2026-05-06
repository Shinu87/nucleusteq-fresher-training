// Validation rules for Job Description form
const jdRules = [
  { id: "jdTitle", label: "Job title", required: true, minLength: 2 },
  { id: "jdLocation", label: "Location", required: true, minLength: 2 },
  { id: "jdDesc", label: "Job description", required: true, minLength: 5 },
  {
    id: "jdMinExp",
    label: "Min experience",
    required: true,
    type: "number",
    min: 0,
    max: 60,
  },
  {
    id: "jdMaxExp",
    label: "Max experience",
    required: true,
    type: "number",
    min: 0,
    max: 60,
    watches: ["jdMinExp"],
    custom: (value) => {
      const minRaw = document.getElementById("jdMinExp").value;
      if (minRaw === "" || value === "") return null;
      const min = parseInt(minRaw, 10),
        max = parseInt(value, 10);
      if (!Number.isNaN(min) && !Number.isNaN(max) && min > max)
        return "Max experience cannot be less than min experience.";
      return null;
    },
  },
  {
    id: "jdMinSal",
    label: "Min salary",
    required: true,
    type: "number",
    min: 0,
  },
  {
    id: "jdMaxSal",
    label: "Max salary",
    required: true,
    type: "number",
    min: 0,
    watches: ["jdMinSal"],
    custom: (value) => {
      const minRaw = document.getElementById("jdMinSal").value;
      if (minRaw === "" || value === "") return null;
      const min = parseFloat(minRaw),
        max = parseFloat(value);
      if (!Number.isNaN(min) && !Number.isNaN(max) && min > max)
        return "Max salary cannot be less than min salary.";
      return null;
    },
  },
  { id: "jdJobType", label: "Job type", required: true, type: "select" },
];

// Load skills as checkboxes for JD form
async function loadSkillsForJD() {
  try {
    const skills = await apiGet("/skills");
    const container = document.getElementById("jdSkillsContainer");
    container.innerHTML = skills
      .map(
        (s) => `
        <label class="skill-check">
          <input type="checkbox" value="${s.id}"/>
          <span>${s.name}</span>
        </label>`,
      )
      .join("");
  } catch (e) {
    console.error("Failed to load skills:", e);
  }
}

// Get checked skill IDs
function getSelectedSkillIds() {
  return [...document.querySelectorAll("#jdSkillsContainer input:checked")].map(
    (cb) => parseInt(cb.value),
  );
}

// Create a new Job Description
async function createJD() {
  const msgEl = document.getElementById("jdMsg");
  if (!validateAll(jdRules)) {
    showMsg(msgEl, "error", "Please fix the highlighted fields.");
    return;
  }

  const title = document.getElementById("jdTitle").value.trim();
  const desc = document.getElementById("jdDesc").value.trim();
  const location = document.getElementById("jdLocation").value.trim();
  const minExp = document.getElementById("jdMinExp").value;
  const maxExp = document.getElementById("jdMaxExp").value;
  const minSal = document.getElementById("jdMinSal").value;
  const maxSal = document.getElementById("jdMaxSal").value;
  const jobType = document.getElementById("jdJobType").value;
  const skillIds = getSelectedSkillIds();

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
  if (parseInt(minExp) > parseInt(maxExp)) {
    showMsg(
      msgEl,
      "error",
      "Min experience cannot be greater than max experience.",
    );
    return;
  }
  if (parseFloat(minSal) > parseFloat(maxSal)) {
    showMsg(msgEl, "error", "Min salary cannot be greater than max salary.");
    return;
  }
  if (skillIds.length === 0) {
    showMsg(msgEl, "error", "Please select at least one required skill.");
    return;
  }

  try {
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
    showMsg(msgEl, "success", "Job Description created successfully!");
    clearJDForm();
    loadJDList();
    loadStats();
    loadJobDropdown();
  } catch (e) {
    showMsg(msgEl, "error", e.message || "Failed to create JD.");
  }
}

// Clear JD form fields and errors
function clearJDForm() {
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
  document.querySelectorAll("#jdSkillsContainer input").forEach((cb) => {
    cb.checked = false;
  });
  clearAllErrors(jdRules);
}

// Load and render job list
async function loadJDList() {
  try {
    const jobs = await apiGet("/jobs");
    allJobs = jobs;
    const container = document.getElementById("jdList");

    if (!jobs.length) {
      container.innerHTML =
        '<div class="empty-state"><div class="empty-icon">💼</div><p>No jobs created yet.</p></div>';
      return;
    }

    container.innerHTML = jobs
      .map(
        (j) => `
      <div class="jd-card" style="${!j.active ? "opacity:0.7;border-left:4px solid #ef4444;" : ""}">
        <div style="display:flex;justify-content:space-between;align-items:flex-start;flex-wrap:wrap;gap:8px;">
          <h3>${j.title}
            ${
              !j.active
                ? '<span style="font-size:12px;background:#ef4444;color:#fff;padding:2px 8px;border-radius:12px;margin-left:6px;">DEACTIVATED</span>'
                : ""
            }
          </h3>
          ${
            j.active
              ? `<button class="btn btn-sm" style="background:#ef4444;color:#fff;font-size:12px;padding:4px 12px;border-radius:6px;cursor:pointer;"
                       onclick="deactivateJob(${j.id}, '${j.title.replace(/'/g, "\\'")}')">Deactivate</button>`
              : `<button class="btn btn-sm" style="background:#16a34a;color:#fff;font-size:12px;padding:4px 12px;border-radius:6px;cursor:pointer;"
                       onclick="activateJob(${j.id}, '${j.title.replace(/'/g, "\\'")}')">Activate</button>`
          }
        </div>
        <p>${j.description}</p>
        <p><b>📍</b> ${j.location} &nbsp;|&nbsp; <b>🏷</b> ${j.jobType?.replace("_", " ")}</p>
        <p><b>Exp:</b> ${j.minExperience}–${j.maxExperience} yrs &nbsp;|&nbsp;
           <b>Salary:</b> ₹${j.minSalary}–₹${j.maxSalary} LPA</p>
        <div class="jd-skills">
          ${(j.skills || []).map((s) => `<span class="jd-skill-tag">${s}</span>`).join("")}
        </div>
        <p style="color:#888;font-size:12px;margin-top:8px;">ID: ${j.id}</p>
      </div>`,
      )
      .join("");

    loadJobDropdown(jobs);
    loadJobFilter(jobs);
  } catch (e) {
    showToast("Failed to load jobs", "error");
  }
}

// Populate job dropdown for candidate form
function loadJobDropdown(jobs) {
  const sel = document.getElementById("cJobId");
  if (!sel) return;
  const j = jobs || allJobs;
  sel.innerHTML =
    '<option value="">-- Select Job --</option>' +
    j
      .filter((jb) => jb.active)
      .map(
        (jb) => `<option value="${jb.id}">${jb.title} (ID:${jb.id})</option>`,
      )
      .join("");
}

// Populate job filter dropdown
function loadJobFilter(jobs) {
  const sel = document.getElementById("filterJob");
  if (!sel) return;
  const j = jobs || allJobs;
  sel.innerHTML =
    '<option value="">All Jobs</option>' +
    j.map((jb) => `<option value="${jb.id}">${jb.title}</option>`).join("");
}

// Deactivate a job
async function deactivateJob(jobId, jobTitle) {
  if (
    !confirm(
      `Deactivate "${jobTitle}"?\n\nThis job will no longer appear in Browse Jobs.`,
    )
  )
    return;
  try {
    await apiPut(`/jobs/${jobId}/deactivate`, {});
    showToast(`Job "${jobTitle}" deactivated.`, "success");
    loadJDList();
    loadStats();
  } catch (e) {
    showToast(e.message || "Failed to deactivate job.", "error");
  }
}

// Re-activate a deactivated job
async function activateJob(jobId, jobTitle) {
  if (
    !confirm(
      `Re-activate "${jobTitle}"?\n\nThe job will appear again in Browse Jobs.`,
    )
  )
    return;
  try {
    await apiPut(`/jobs/${jobId}/activate`, {});
    showToast(`Job "${jobTitle}" re-activated.`, "success");
    loadJDList();
    loadStats();
  } catch (e) {
    showToast(e.message || "Failed to activate job.", "error");
  }
}
