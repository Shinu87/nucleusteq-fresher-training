/* candidate portal script - candidate view */
const user = requireRole("CANDIDATE");
let allJobs = [];

function dobIneligibilityReason(dob) {
  if (!dob) return "Date of birth is missing on your profile.";
  const dobDate = new Date(dob);
  const today = new Date();
  const age18Date = new Date(
    dobDate.getFullYear() + 18,
    dobDate.getMonth(),
    dobDate.getDate(),
  );
  if (age18Date > today) return "Candidate must be at least 18 years old.";
  const age60Date = new Date(
    dobDate.getFullYear() + 60,
    dobDate.getMonth(),
    dobDate.getDate(),
  );
  if (age60Date <= today)
    return "Candidate exceeds maximum eligible working age.";
  return null;
}

/* per field validation rules for the Apply form */
const applyRules = [
  { id: "aName", label: "Full Name", required: true, minLength: 2 },
  { id: "aPhone", label: "Phone", required: true, type: "phone" },
  {
    id: "aDob",
    label: "Date of Birth",
    required: true,
    requiredMsg: "Date of birth is required.",
    custom: (value) => dobIneligibilityReason(value),
  },
  {
    id: "aTotalExp",
    label: "Total Experience",
    required: true,
    type: "number",
    min: 0,
    max: 60,
  },
  {
    id: "aRelExp",
    label: "Relevant Experience",
    type: "number",
    min: 0,
    max: 60,
    custom: (value) => {
      const total = parseInt(document.getElementById("aTotalExp").value);
      if (!Number.isNaN(total) && parseInt(value) > total) {
        return "Relevant experience cannot exceed total experience.";
      }
      return null;
    },
  },
  { id: "aCurCTC", label: "Current CTC", type: "number", min: 0, max: 1000 },
  { id: "aExpCTC", label: "Expected CTC", type: "number", min: 0, max: 1000 },
  { id: "aJobId", label: "Job", required: true, type: "select" },
  {
    id: "aResumeFile",
    label: "Resume",
    type: "file",
    accept: ["application/pdf"],
    acceptMsg: "Resume must be a PDF file.",
    maxSizeMB: 10,
  },
];

// fill sidebar with the logged-in user's name and email
document.getElementById("sidebarName").textContent = user.name || "Candidate";
document.getElementById("sidebarEmail").textContent = user.email || "";

// auto-actions on page load
window.onload = async () => {
  document.getElementById("aEmail").value = user.email || "";
  document.getElementById("aName").value = user.name || "";

  // Set max date on DOB picker to today minus 18 years
  const dobField = document.getElementById("aDob");
  if (dobField) {
    const maxDob = new Date();
    maxDob.setFullYear(maxDob.getFullYear() - 18);
    dobField.max = maxDob.toISOString().split("T")[0];
    if (user.dateOfBirth) dobField.value = user.dateOfBirth;
  }

  refreshAgeEligibilityBanner();

  wireFieldValidation(applyRules);

  await loadJobsForCandidate();

  const sp = new URLSearchParams(window.location.search);
  const incomingJobId = sp.get("jobId");
  if (incomingJobId && allJobs.some((j) => String(j.id) === incomingJobId)) {
    goApply(parseInt(incomingJobId));
  }

  loadMyProgress();
};

/**
 * Shows or hides the age-eligibility banner based on the logged-in
 * user's age. Called on load and after the apply form is rendered.
 */
function refreshAgeEligibilityBanner() {
  const banner = document.getElementById("ageEligibilityBanner");
  if (!banner) return;
  const reason = dobIneligibilityReason(user.dateOfBirth);
  if (reason) {
    banner.style.display = "block";
    banner.innerHTML =
      `<strong>⚠️ You are not eligible to apply for jobs.</strong><br/>` +
      `${reason} (Your date of birth on file: ${user.dateOfBirth ?? "—"}). ` +
      `If this looks wrong, please contact support.`;
  } else {
    banner.style.display = "none";
  }
}

/* Section navigation */
function showSection(name, el) {
  document
    .querySelectorAll(".page-section")
    .forEach((s) => s.classList.remove("active"));
  document
    .querySelectorAll(".nav-item")
    .forEach((n) => n.classList.remove("active"));
  document.getElementById(`section-${name}`).classList.add("active");
  el.classList.add("active");
}

/* jobs section */
async function loadJobsForCandidate() {
  try {
    const jobs = await apiGet("/jobs/active");
    allJobs = jobs;
    renderJobCards(jobs);
    populateJobDropdown(jobs);
  } catch (e) {
    showToast("Failed to load jobs", "error");
  }
}

function renderJobCards(jobs) {
  const container = document.getElementById("candidateJobList");
  if (!jobs.length) {
    container.innerHTML =
      '<div class="empty-state"><div class="empty-icon">💼</div>' +
      "<p>No jobs available right now.</p></div>";
    return;
  }
  // Whether the logged-in candidate is age eligible to apply at all.
  const ineligibleReason = dobIneligibilityReason(user.dateOfBirth);

  container.innerHTML = jobs
    .map(
      (j) => `
    <div class="jd-card">
      <h3>${j.title}</h3>
      <p>${j.description}</p>
      <p><b>📍</b> ${j.location} &nbsp;|&nbsp; <b>🏷</b> ${j.jobType?.replace("_", " ")}</p>
      <p><b>Exp:</b> ${j.minExperience}–${j.maxExperience} yrs &nbsp;|&nbsp;
         <b>💰</b> ₹${j.minSalary}–₹${j.maxSalary} LPA</p>
      <div class="jd-skills">
        ${(j.skills || []).map((s) => `<span class="jd-skill-tag">${s}</span>`).join("")}
      </div>
      ${
        ineligibleReason
          ? `<button class="btn-apply" disabled
                     title="${ineligibleReason.replace(/"/g, "&quot;")}"
                     style="opacity:0.55;cursor:not-allowed;">
               Apply Now →
             </button>`
          : `<button class="btn-apply" onclick="goApply(${j.id})">Apply Now →</button>`
      }
    </div>
  `,
    )
    .join("");
}

function populateJobDropdown(jobs) {
  const sel = document.getElementById("aJobId");
  sel.innerHTML =
    '<option value="">-- Select a Job --</option>' +
    jobs
      .map((j) => `<option value="${j.id}">${j.title} – ${j.location}</option>`)
      .join("");
}

/*  click "Apply" on a job card  jump to apply form */
function goApply(jobId) {
  const reason = dobIneligibilityReason(user.dateOfBirth);
  if (reason) {
    alert(reason);
    return;
  }

  const job = allJobs.find((j) => j.id === jobId);

  document
    .querySelectorAll(".page-section")
    .forEach((s) => s.classList.remove("active"));
  document
    .querySelectorAll(".nav-item")
    .forEach((n) => n.classList.remove("active"));
  document.getElementById("section-apply").classList.add("active");
  document.querySelectorAll(".nav-item")[1].classList.add("active");

  document.getElementById("applyJobId").value = jobId;
  document.getElementById("aJobId").value = jobId;

  if (job) {
    document.getElementById("applyJobInfo").style.display = "block";
    document.getElementById("applyJobTitle").textContent =
      `${job.title} — ${job.location}`;
    document.getElementById("applyJobDesc").textContent = job.description;
    document.getElementById("jobSelectGroup").style.display = "none";
  }

  // email is read only, comes from logged in user
  document.getElementById("aEmail").value = user.email || "";
  document.getElementById("aName").value = user.name || "";

  window.scrollTo({ top: 0, behavior: "smooth" });
}

/* application submit handler */
async function submitApplication() {
  const msgEl = document.getElementById("applyMsg");

  // ALWAYS use the logged in email the field is read-only
  const email = (user.email || "").trim();
  if (!email) {
    showMsg(msgEl, "error", "Email missing - please log in again.");
    return;
  }

  // Final age eligibility check before submit. Mirrors the backend rule.
  const reason = dobIneligibilityReason(user.dateOfBirth);
  if (reason) {
    showMsg(msgEl, "error", reason);
    return;
  }

  // per field validation (errors render under each field)
  if (!validateAll(applyRules)) {
    showMsg(msgEl, "error", "Please fix the highlighted fields.");
    return;
  }

  const name = document.getElementById("aName").value.trim();
  const phone = document.getElementById("aPhone").value.trim();
  const totalExp = document.getElementById("aTotalExp").value;
  const dateOfBirth = document.getElementById("aDob").value || user.dateOfBirth;
  const jobId =
    document.getElementById("aJobId").value ||
    document.getElementById("applyJobId").value;
  const resumeFile = document.getElementById("aResumeFile").files[0];

  // build payload matches backend CandidateRequestDTO
  const body = {
    name,
    email,
    phone,
    dateOfBirth,
    currentOrganization: document.getElementById("aOrg").value.trim(),
    totalExperience: parseInt(totalExp),
    relevantExperience: parseInt(document.getElementById("aRelExp").value) || 0,
    currentCTC: parseFloat(document.getElementById("aCurCTC").value) || null,
    expectedCTC: parseFloat(document.getElementById("aExpCTC").value) || null,
    noticePeriod: document.getElementById("aNotice").value.trim(),
    preferredLocation: document.getElementById("aPrefLoc").value.trim(),
    source: document.getElementById("aSource").value.trim(),
    jobId: parseInt(jobId),
    currentStage: "PROFILING",
    status: "IN_PROGRESS",
  };

  try {
    const result = await apiPost("/candidates", body);

    // upload resume after candidate row is created
    if (resumeFile) {
      await uploadResume(result.id, resumeFile, msgEl);
    } else {
      showMsg(
        msgEl,
        "success",
        `✅ Application submitted! Your Candidate ID: ${result.id}.`,
      );
    }

    // refresh progress automatically so My Progress tab shows latest data
    loadMyProgress();
  } catch (e) {
    showMsg(
      msgEl,
      "error",
      e.message ||
        "Submission failed. You may have already applied or have an active application.",
    );
  }
}

/* Resume Upload (internal drive) */
async function uploadResume(candidateId, file, msgEl, opts) {
  const successMsg =
    (opts && opts.successMsg) ||
    `✅ Application submitted with resume! Your Candidate ID: ${candidateId}.`;
  const errorPrefix =
    (opts && opts.errorPrefix) ||
    `⚠️ Application submitted (ID: ${candidateId}) but resume upload failed`;
  const errorLevel = (opts && opts.errorLevel) || "warning";

  try {
    const formData = new FormData();
    formData.append("file", file);

    await apiPostForm(`/candidates/${candidateId}/resume`, formData, {
      silent: true,
    });

    showMsg(msgEl, "success", successMsg);
    return true;
  } catch (e) {
    showMsg(
      msgEl,
      errorLevel,
      `${errorPrefix}: ${e.message || "upload failed"}.`,
    );
    return false;
  }
}

/**
 * Handler for the Upload / Replace Resume button on the progress page.
 */
async function uploadResumeFromProfile(candidateId) {
  const fileInput = document.getElementById("profileResumeFile");
  const msgEl = document.getElementById("profileResumeMsg");
  const file = fileInput && fileInput.files && fileInput.files[0];

  if (!file) {
    showMsg(msgEl, "error", "Please choose a PDF file first.");
    return;
  }
  if (file.type !== "application/pdf") {
    showMsg(msgEl, "error", "Resume must be a PDF file.");
    return;
  }
  if (file.size > 10 * 1024 * 1024) {
    showMsg(msgEl, "error", "Resume must be under 10MB.");
    return;
  }

  // Same uploadResume function used by the apply flow only the
  // user facing message wording is tailored for the profile page.
  const ok = await uploadResume(candidateId, file, msgEl, {
    successMsg: "✅ Resume uploaded successfully!",
    errorPrefix: "⚠️ Resume upload failed",
    errorLevel: "error",
  });

  if (ok) {
    loadMyProgress();
  }
}

/*  re-apply: send candidate to Browse Jobs to start fresh  */
function goToBrowseJobsForReapply() {
  document
    .querySelectorAll(".page-section")
    .forEach((s) => s.classList.remove("active"));
  document
    .querySelectorAll(".nav-item")
    .forEach((n) => n.classList.remove("active"));
  document.getElementById("section-jobs").classList.add("active");
  document.querySelectorAll(".nav-item")[0].classList.add("active");
  window.scrollTo({ top: 0, behavior: "smooth" });
  showToast("Select a job and submit a fresh application.", "success");
}

/* MY PROGRESS */
async function loadMyProgress() {
  const container = document.getElementById("progressContent");
  if (!container) return;

  const email = (user.email || "").trim();
  if (!email) {
    container.innerHTML =
      '<div class="empty-state"><div class="empty-icon">⚠️</div>' +
      "<p>Email missing - please log in again.</p></div>";
    return;
  }

  // show loading state
  container.innerHTML =
    '<div class="empty-state"><div class="empty-icon">⏳</div>' +
    "<p>Loading your application status…</p></div>";

  try {
    // hit the email based tracking endpoint
    const res = await fetch(
      `${BASE_URL}/candidates/me/application?email=${encodeURIComponent(email)}`,
      {
        headers: {
          "Content-Type": "application/json",
          ...getAuthHeader(),
        },
      },
    );

    if (res.status === 404) {
      // candidate has no application yet
      container.innerHTML = `
        <div class="empty-state">
          <div class="empty-icon">📋</div>
          <p>You haven't applied yet.</p>
          <button class="btn" style="margin-top:12px;"
                  onclick="goToBrowseJobsForReapply()">Browse Jobs →</button>
        </div>`;
      return;
    }

    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || "Failed to load progress");
    }

    const data = await res.json();
    renderProgress(data);
  } catch (e) {
    container.innerHTML = `<div class="empty-state"><div class="empty-icon">⚠️</div>
       <p>Could not load progress: ${e.message || "unknown error"}</p></div>`;
  }
}

/* render the progress data returned by /me/application */
function renderProgress(data) {
  const container = document.getElementById("progressContent");

  // build the stage step pipeline
  const stages = ["PROFILING", "SCREENING", "L1", "L2", "HR"];
  const curIdx = stages.indexOf(data.currentStage);

  const stageHtml = stages
    .map(
      (s, i) => `
    <div class="stage-step">
      <div class="stage-dot ${
        i < curIdx ? "done" : i === curIdx ? "active" : ""
      }">
        ${i < curIdx ? "✓" : i + 1}
      </div>
      <span class="stage-label">${s}</span>
    </div>
  `,
    )
    .join("");

  // interview list (no feedback content shown to candidate)
  const intHtml = (data.interviews || []).length
    ? data.interviews
        .map(
          (iv) => `
        <div class="detail-item" style="margin-bottom:10px;">
          <div class="di-label">${iv.stage} Interview</div>
          <div class="di-value">
            📅 ${fmtDate(iv.scheduledAt)}<br/>
            👥 ${(iv.panelNames || []).join(", ") || "TBD"}<br/>
            ${interviewStatusBadge(iv.interviewStatus)}
            ${
              iv.feedbackSubmitted
                ? '<span class="badge badge-green" style="margin-left:6px;">Feedback Submitted</span>'
                : ""
            }
          </div>
        </div>
      `,
        )
        .join("")
    : '<p style="color:#aaa;font-size:13px;">No interviews scheduled yet.</p>';

  // re-apply box appears only when application is REJECTED
  const reApplyBox =
    data.applicationStatus === "REJECTED"
      ? `
      <div class="detail-section"
           style="border:1.5px solid #f59e0b;border-radius:10px;padding:16px;margin-top:16px;">
        <h4>🔄 Re-Apply for a New Job</h4>
        <p style="font-size:13px;color:#888;margin-bottom:12px;">
          Your previous application was rejected. You can apply afresh now.
        </p>
        <button class="btn btn-warning"
                onclick="goToBrowseJobsForReapply()">Browse Jobs & Apply Fresh →</button>
      </div>`
      : "";

  // big visible derivedStatus pill at the top
  const derivedHtml = `
    <div style="display:flex;align-items:center;gap:10px;margin-bottom:14px;">
      <span style="font-weight:600;color:#666;">Application Status:</span>
      <span class="badge ${derivedStatusClass(data.derivedStatus)}"
            style="font-size:14px;padding:6px 14px;">
        ${data.derivedStatus || "Applied"}
      </span>
    </div>`;

  container.innerHTML = `
    <div class="detail-section">
      ${derivedHtml}
      <div class="stage-progress">${stageHtml}</div>
    </div>

    <div class="detail-section">
      <h4>Your Details</h4>
      <div class="detail-grid">
        <div class="detail-item">
          <div class="di-label">Name</div>
          <div class="di-value">${data.candidateName || "—"}</div>
        </div>
        <div class="detail-item">
          <div class="di-label">Email</div>
          <div class="di-value">${data.candidateEmail || "—"}</div>
        </div>
        <div class="detail-item">
          <div class="di-label">Stage</div>
          <div class="di-value">${stageBadge(data.currentStage)}</div>
        </div>
        <div class="detail-item">
          <div class="di-label">Job</div>
          <div class="di-value">${data.jobTitle || data.jobId || "—"}</div>
        </div>
        <div class="detail-item">
          <div class="di-label">Resume</div>
          <div class="di-value">
            <div style="margin-bottom:6px;">
              ${data.resumeUploaded ? "✅ Uploaded" : "❌ Not uploaded"}
            </div>
            <!-- Reuses the existing POST /api/candidates/{id}/resume
                 endpoint via uploadResumeFromProfile(). Same backend,
                 same validations, same storage. The widget is shown for
                 every logged-in candidate (self-registered AND
                 HR-onboarded) so they can attach a resume the first
                 time, or replace it later. -->
            <input type="file" id="profileResumeFile" accept=".pdf"
                   style="display:block;margin-bottom:6px;font-size:12px;" />
            <button class="btn btn-sm" style="font-size:12px;"
                    onclick="uploadResumeFromProfile(${data.candidateId})">
              ${data.resumeUploaded ? "Replace Resume" : "Upload Resume"}
            </button>
            <div id="profileResumeMsg" style="margin-top:6px;font-size:12px;"></div>
          </div>
        </div>
      </div>
    </div>

    <div class="detail-section">
      <h4>Interview Schedule</h4>
      ${intHtml}
    </div>

    ${reApplyBox}
  `;
}

/* pick a colored badge class for the derived status pill */
function derivedStatusClass(label) {
  switch ((label || "").toLowerCase()) {
    case "rejected":
      return "badge-red";
    case "completed":
      return "badge-green";
    case "l1":
      return "badge-purple";
    case "l2":
      return "badge-orange";
    case "hr":
      return "badge-yellow";
    case "screening":
      return "badge-blue";
    case "applied":
    default:
      return "badge-gray";
  }
}

/* ── small message helper for forms on this page ── */
function showMsg(el, type, text) {
  el.style.display = "block";
  el.className = `msg ${type}`;
  el.textContent = text;
}
