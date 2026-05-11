// Validation rules for candidate form
const candidateRules = [
  { id: "cName", label: "Full name", required: true, minLength: 2 },
  { id: "cEmail", label: "Email", required: true, type: "email" },
  { id: "cPhone", label: "Phone", required: true, type: "phone" },
  {
    id: "cDob",
    label: "Date of Birth",
    required: true,
    type: "date",
    custom: (value) => {
      if (!value) return "Date of birth is required.";
      const dob = new Date(value),
        today = new Date();
      if (dob >= today) return "Date of birth must be in the past.";
      let age = today.getFullYear() - dob.getFullYear();
      const m = today.getMonth() - dob.getMonth();
      if (m < 0 || (m === 0 && today.getDate() < dob.getDate())) age--;
      if (age < 18)
        return "Candidate must be at least 18 years old to apply for jobs";
      if (age > 60) return "Candidate exceeds maximum eligible working age";
      return null;
    },
  },
  {
    id: "cTotalExp",
    label: "Total experience",
    required: true,
    type: "number",
    min: 0,
    max: 60,
  },
  {
    id: "cRelExp",
    label: "Relevant experience",
    type: "number",
    min: 0,
    max: 60,
    watches: ["cTotalExp"],
    custom: (value) => {
      if (value === "") return null;
      const total = parseInt(document.getElementById("cTotalExp").value, 10);
      const rel = parseInt(value, 10);
      if (!Number.isNaN(total) && !Number.isNaN(rel) && rel > total)
        return "Relevant experience cannot exceed total experience.";
      return null;
    },
  },
  { id: "cCurCTC", label: "Current CTC", type: "number", min: 0, max: 1000 },
  { id: "cExpCTC", label: "Expected CTC", type: "number", min: 0, max: 1000 },
  {
    id: "cResumeUrl",
    label: "Resume URL",
    custom: (value) => {
      if (!value) return null;
      try {
        new URL(value);
      } catch {
        return "Resume URL must be a valid URL (https://…).";
      }
      return null;
    },
  },
  { id: "cJobId", label: "Job", required: true, type: "select" },
  { id: "cStage", label: "Initial stage", required: true, type: "select" },
  { id: "cStatus", label: "Status", required: true, type: "select" },
];

// Stage pipeline definition
const STAGE_PIPELINE = ["PROFILING", "SCREENING", "L1", "L2", "HR"];
const FEEDBACK_GATED_STAGES = ["L1", "L2", "HR"];

// Load candidates and populate dropdowns
async function loadCandidates() {
  try {
    const candidates = await apiGet("/candidates");
    allCandidates = candidates;
    renderCandidateTable(candidates);
    loadCandidateDropdowns(candidates);
    loadStats();
  } catch (e) {
    showToast("Failed to load candidates", "error");
  }
}

// Render candidate table rows
function renderCandidateTable(candidates) {
  const tbody = document.getElementById("candidateTableBody");
  if (!candidates.length) {
    tbody.innerHTML =
      '<tr><td colspan="7" style="text-align:center;color:#aaa;padding:24px;">No candidates found.</td></tr>';
    return;
  }
  tbody.innerHTML = candidates
    .map(
      (c, i) => `
    <tr>
      <td>${i + 1}</td>
      <td><strong>${c.name}</strong></td>
      <td>${c.email}</td>
      <td>${stageBadge(c.currentStage)}</td>
      <td>${statusBadge(c.status)}</td>
      <td>${c.jobId || "—"}</td>
      <td style="white-space:nowrap;">
        <button class="btn btn-sm btn-info" onclick="viewCandidate(${c.id})">View</button>
        ${
          c.status !== "REJECTED" && c.status !== "SELECTED"
            ? `${stageAdvanceDropdown(c, "sm")}
             <button class="btn btn-sm btn-danger" style="margin-left:4px;" onclick="rejectCandidate(${c.id})">Reject</button>`
            : ""
        }
      </td>
    </tr>`,
    )
    .join("");
}

// Filter candidates by stage/status/job
function filterCandidates() {
  const stage = document.getElementById("filterStage").value;
  const status = document.getElementById("filterStatus").value;
  const jobId = document.getElementById("filterJob").value;
  let filtered = allCandidates;
  if (stage) filtered = filtered.filter((c) => c.currentStage === stage);
  if (status) filtered = filtered.filter((c) => c.status === status);
  if (jobId) filtered = filtered.filter((c) => String(c.jobId) === jobId);
  renderCandidateTable(filtered);
}

// Populate candidate dropdowns for interview scheduling and feedback
function loadCandidateDropdowns(candidates) {
  const iSel = document.getElementById("iCandidateId");
  if (iSel) {
    iSel.innerHTML =
      '<option value="">-- Select Candidate --</option>' +
      candidates
        .map((c) => `<option value="${c.id}">${c.name} (${c.email})</option>`)
        .join("");
  }
  const fSel = document.getElementById("fbCandidateFilter");
  if (fSel) {
    fSel.innerHTML =
      '<option value="">-- Select Candidate --</option>' +
      candidates
        .map((c) => `<option value="${c.id}">${c.name}</option>`)
        .join("");
  }
}

// Create a new candidate
async function createCandidate() {
  const msgEl = document.getElementById("cMsg");
  if (!validateAll(candidateRules)) {
    showMsg(msgEl, "error", "Please fix the highlighted fields.");
    return;
  }

  const name = document.getElementById("cName").value.trim();
  const email = document.getElementById("cEmail").value.trim();
  const phone = document.getElementById("cPhone").value.trim();
  const dobRaw = document.getElementById("cDob").value;
  const totalExp = document.getElementById("cTotalExp").value;
  const jobId = document.getElementById("cJobId").value;

  if (!name || !email || !phone || !dobRaw || !totalExp || !jobId) {
    showMsg(
      msgEl,
      "error",
      "Fill all required fields (Name, Email, Phone, Date of Birth, Total Exp, Job).",
    );
    return;
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    showMsg(msgEl, "error", "Please enter a valid email address.");
    return;
  }
  if (!/^[6-9][0-9]{9}$/.test(phone)) {
    showMsg(
      msgEl,
      "error",
      "Phone must be a 10-digit number starting with 6-9.",
    );
    return;
  }

  // Age eligibility check
  const dob = new Date(dobRaw),
    today = new Date();
  if (dob >= today) {
    showMsg(msgEl, "error", "Date of birth must be in the past.");
    return;
  }
  let derivedAge = today.getFullYear() - dob.getFullYear();
  const monthDiff = today.getMonth() - dob.getMonth();
  if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < dob.getDate()))
    derivedAge--;
  if (derivedAge < 18) {
    showMsg(
      msgEl,
      "error",
      "Candidate must be at least 18 years old to apply for jobs",
    );
    return;
  }
  if (derivedAge > 60) {
    showMsg(msgEl, "error", "Candidate exceeds maximum eligible working age");
    return;
  }

  const body = {
    name,
    email,
    phone,
    dateOfBirth: dobRaw,
    currentOrganization: document.getElementById("cOrg").value.trim(),
    totalExperience: parseInt(totalExp),
    relevantExperience: parseInt(document.getElementById("cRelExp").value) || 0,
    currentCTC: parseFloat(document.getElementById("cCurCTC").value) || null,
    expectedCTC: parseFloat(document.getElementById("cExpCTC").value) || null,
    noticePeriod: document.getElementById("cNotice").value.trim(),
    preferredLocation: document.getElementById("cPrefLoc").value.trim(),
    source: document.getElementById("cSource").value.trim(),
    resumeUrl: document.getElementById("cResumeUrl").value.trim(),
    jobId: parseInt(jobId),
    currentStage: document.getElementById("cStage").value,
    status: document.getElementById("cStatus").value,
  };

  try {
    await apiPost("/candidates", body);
    showMsg(msgEl, "success", "Candidate added successfully!");
    clearCandidateForm();
    loadCandidates();
  } catch (e) {
    showMsg(msgEl, "error", e.message || "Failed to add candidate.");
  }
}

// Clear candidate form fields and errors
function clearCandidateForm() {
  [
    "cName",
    "cEmail",
    "cPhone",
    "cDob",
    "cOrg",
    "cTotalExp",
    "cRelExp",
    "cCurCTC",
    "cExpCTC",
    "cNotice",
    "cPrefLoc",
    "cSource",
    "cResumeUrl",
  ].forEach((id) => {
    const el = document.getElementById(id);
    if (el) el.value = "";
  });
  clearAllErrors(candidateRules);
}

async function downloadResume(path) {
  try {
    const url = `${BASE_URL}/resumes/download?path=${encodeURIComponent(path)}`;

    const response = await fetch(
      `${BASE_URL}/resumes/download?path=${encodeURIComponent(path)}`,
      {
        method: "GET",
        headers: {
          ...getAuthHeader(),
        },
      },
    );

    if (!response.ok) {
      throw new Error("Download failed");
    }

    const blob = await response.blob();

    const link = document.createElement("a");
    link.href = window.URL.createObjectURL(blob);
    link.download = "resume.pdf";
    document.body.appendChild(link);
    link.click();
    link.remove();
  } catch (err) {
    console.error(err);
    showToast("Failed to download resume", "error");
  }
}

// View candidate detail card
async function viewCandidate(id) {
  try {
    const c = await apiGet(`/candidates/${id}`);
    const interviews = await apiGet(`/interviews/candidate/${id}`);
    let feedbacks = [];
    try {
      feedbacks = await apiGet(`/feedbacks/candidate/${id}`);
    } catch (_e) {
      feedbacks = [];
    }

    const card = document.getElementById("candidateDetailCard");
    const content = document.getElementById("candidateDetailContent");
    const jobTitle =
      allJobs.find((j) => j.id === c.jobId)?.title || `Job #${c.jobId}`;

    // Build stage pipeline progress
    const stages = ["PROFILING", "SCREENING", "L1", "L2", "HR"];
    const curIdx = stages.indexOf(c.currentStage);
    const stageHtml = stages
      .map(
        (s, i) => `
      <div class="stage-step">
        <div class="stage-dot ${i < curIdx ? "done" : i === curIdx ? "active" : ""}">
          ${i < curIdx ? "✓" : i + 1}
        </div>
        <span class="stage-label">${s}</span>
      </div>`,
      )
      .join("");

    // Build interview table
    const intHtml = interviews.length
      ? interviews
          .map(
            (iv) => `
          <tr>
            <td>${iv.stage}</td>
            <td>${fmtDate(iv.scheduledAt)}</td>
            <td>${(iv.panelNames || []).join(", ") || "—"}</td>
            <td>${interviewStatusBadge(iv.status)}</td>
            <td>${iv.meetingUrl ? `<a href="${iv.meetingUrl}" target="_blank">Open</a>` : "—"}</td>
          </tr>`,
          )
          .join("")
      : '<tr><td colspan="5" style="text-align:center;color:#aaa;">No interviews yet.</td></tr>';

    content.innerHTML = `
      <div class="detail-section">
        <h4>Stage Progress</h4>
        <div class="stage-progress">${stageHtml}</div>
        <p style="margin-top:8px;">Status: ${statusBadge(c.status)}</p>
      </div>
      <div class="detail-section">
        <h4>Personal Info</h4>
        <div class="detail-grid">
          <div class="detail-item"><div class="di-label">Name</div><div class="di-value">${c.name}</div></div>
          <div class="detail-item"><div class="di-label">Email</div><div class="di-value">${c.email}</div></div>
          <div class="detail-item"><div class="di-label">Phone</div><div class="di-value">${c.phone}</div></div>
          <div class="detail-item"><div class="di-label">Age</div><div class="di-value">${c.age ?? "—"}</div></div>
          <div class="detail-item"><div class="di-label">Organization</div><div class="di-value">${c.currentOrganization || "—"}</div></div>
          <div class="detail-item"><div class="di-label">Total Exp</div><div class="di-value">${c.totalExperience} yrs</div></div>
          <div class="detail-item"><div class="di-label">Relevant Exp</div><div class="di-value">${c.relevantExperience || "—"} yrs</div></div>
          <div class="detail-item"><div class="di-label">Current CTC</div><div class="di-value">${c.currentCTC ? "₹" + c.currentCTC + " LPA" : "—"}</div></div>
          <div class="detail-item"><div class="di-label">Expected CTC</div><div class="di-value">${c.expectedCTC ? "₹" + c.expectedCTC + " LPA" : "—"}</div></div>
          <div class="detail-item"><div class="di-label">Notice Period</div><div class="di-value">${c.noticePeriod || "—"}</div></div>
          <div class="detail-item"><div class="di-label">Preferred Location</div><div class="di-value">${c.preferredLocation || "—"}</div></div>
          <div class="detail-item"><div class="di-label">Source</div><div class="di-value">${c.source || "—"}</div></div>
          <div class="detail-item"><div class="di-label">Applied Job</div><div class="di-value">${jobTitle}</div></div>
        </div>
        ${
          c.resumeUrl
            ? `<p style="margin-top:10px;font-size:13px;">
         📄 Resume:
         <button 
            onclick="downloadResume('${c.resumeUrl}')"
            style="background:none;border:none;color:#007bff;cursor:pointer;padding:0;font:inherit;">
            View Resume
         </button>
       </p>`
            : ""
        }
      </div>
      <div class="detail-section">
        <h4>Interview Schedule</h4>
        <div class="table-wrapper">
          <table>
            <thead><tr><th>Stage</th><th>Scheduled At</th><th>Panel</th><th>Status</th><th>Meeting</th></tr></thead>
            <tbody>${intHtml}</tbody>
          </table>
        </div>
      </div>
      ${feedbackPendingBanner(c, interviews, feedbacks)}
      <div style="margin-top:16px;display:flex;gap:10px;flex-wrap:wrap;align-items:center;">
        ${
          c.status === "IN_PROGRESS"
            ? `${stageAdvanceDropdown(c, "lg", computeFeedbackInfo(c, interviews, feedbacks))}
             <button class="btn btn-danger" onclick="rejectCandidate(${c.id})">✕ Reject</button>`
            : ""
        }
        <button class="btn" style="background:#e0e0ee;"
                onclick="document.getElementById('candidateDetailCard').style.display='none'">Close</button>
      </div>`;

    card.style.display = "block";
    card.scrollIntoView({ behavior: "smooth" });
  } catch (e) {
    showToast("Failed to load candidate details", "error");
  }
}

// Get next valid stage in pipeline
function getNextStage(currentStage) {
  const idx = STAGE_PIPELINE.indexOf(currentStage);
  if (idx === -1) return null;
  if (idx === STAGE_PIPELINE.length - 1) return "SELECTED";
  return STAGE_PIPELINE[idx + 1];
}

// Compute feedback completeness info for a candidate
function computeFeedbackInfo(candidate, interviews, feedbacks) {
  const stage = candidate.currentStage;
  if (!FEEDBACK_GATED_STAGES.includes(stage))
    return { gated: false, complete: true, assigned: 0, submitted: 0 };

  const interview = (interviews || []).find((iv) => iv.stage === stage);
  if (!interview)
    return { gated: true, complete: false, assigned: 0, submitted: 0 };

  const assigned = (interview.panelNames || []).length;
  const submitted = (feedbacks || []).filter(
    (f) => f.interviewId === interview.id,
  ).length;
  return {
    gated: true,
    complete: assigned > 0 && submitted >= assigned,
    assigned,
    submitted,
  };
}

// Render pending feedback warning banner
function feedbackPendingBanner(candidate, interviews, feedbacks) {
  if (candidate.status !== "IN_PROGRESS") return "";
  const info = computeFeedbackInfo(candidate, interviews, feedbacks);
  if (!info.gated || info.complete) return "";
  const detail =
    info.assigned === 0
      ? `No interview is scheduled for the current stage (${candidate.currentStage}).`
      : `Submitted ${info.submitted} of ${info.assigned} feedback(s) for the current ${candidate.currentStage} interview.`;
  return `
    <div style="margin-top:12px;padding:12px 16px;border-radius:8px;
                background:#fffbeb;border:1.5px solid #fcd34d;color:#92400e;font-size:13px;">
      <strong>⚠ Feedback pending.</strong>
      ${detail} Stage advancement will be blocked by the server until all assigned panels submit feedback.
    </div>`;
}

// Build stage advance dropdown for table row or detail card
function stageAdvanceDropdown(c, size = "sm", feedbackInfo = null) {
  if (c.status === "REJECTED" || c.status === "SELECTED") return "";

  const next = getNextStage(c.currentStage);
  const selectId = `stageSel_${c.id}`,
    btnId = `stageBtn_${c.id}`;
  const btnClass = size === "sm" ? "btn btn-sm btn-success" : "btn btn-success";
  const selectStyle =
    size === "sm"
      ? "padding:5px 8px;border:1.5px solid #e0e0ee;border-radius:6px;font-size:12px;background:#fff;margin-left:4px;min-width:150px;"
      : "padding:9px 12px;border:1.5px solid #e0e0ee;border-radius:6px;font-size:13px;background:#fff;min-width:200px;";

  if (!next) {
    return `<select disabled style="${selectStyle}" title="No further stage available">
              <option>Current: ${c.currentStage}</option>
            </select>`;
  }

  const ALL_STAGES = [...STAGE_PIPELINE, "SELECTED"];
  const curIdx = ALL_STAGES.indexOf(c.currentStage);
  const optionRows = ALL_STAGES.map((stage, i) => {
    const isCurrent = stage === c.currentStage,
      isNext = stage === next;
    let label = stage === "SELECTED" ? "SELECTED (final)" : stage;
    if (isCurrent) label += "  — current";
    else if (isNext)
      label += stage === "SELECTED" ? "  — mark as final" : "  — next";
    else if (i < curIdx) label += "  — past";
    else label += "  — locked";
    return `<option value="${stage}" ${isNext ? "" : "disabled"}>${label}</option>`;
  }).join("");

  const feedbackBlocks =
    feedbackInfo && feedbackInfo.gated && !feedbackInfo.complete;
  const lockedTitle = feedbackBlocks
    ? `Cannot advance until all panel feedback is submitted (${feedbackInfo.submitted}/${feedbackInfo.assigned})`
    : "Select a stage above to enable";
  const lockedFlag = feedbackBlocks ? "1" : "0";

  return `
    <select id="${selectId}" style="${selectStyle}" title="Select next stage"
            onchange="onStageDropdownChange('${selectId}', '${btnId}', '${lockedFlag}')">
      <option value="" selected>— Select stage —</option>
      ${optionRows}
    </select>
    <button id="${btnId}" type="button" class="${btnClass}" disabled
            style="margin-left:4px;opacity:0.55;cursor:not-allowed;"
            title="${lockedTitle}"
            onclick="submitStageAdvance(${c.id}, '${selectId}', '${btnId}', '${c.currentStage}')">
      ${size === "sm" ? "Move" : "⬆ Advance Stage"}
    </button>`;
}

// Enable/disable advance button based on dropdown selection
function onStageDropdownChange(selectId, btnId, lockedFlag) {
  const sel = document.getElementById(selectId);
  const btn = document.getElementById(btnId);
  if (!sel || !btn) return;
  if (lockedFlag === "1") {
    btn.disabled = true;
    btn.style.opacity = "0.55";
    btn.style.cursor = "not-allowed";
    return;
  }
  const hasValidSelection = !!sel.value;
  btn.disabled = !hasValidSelection;
  btn.style.opacity = hasValidSelection ? "1" : "0.55";
  btn.style.cursor = hasValidSelection ? "pointer" : "not-allowed";
  btn.title = hasValidSelection
    ? "Click to advance the candidate"
    : "Select a stage above to enable";
}

// Submit stage advancement to backend
async function submitStageAdvance(id, selectId, btnId, currentStage) {
  const sel = document.getElementById(selectId);
  const btn = document.getElementById(btnId);
  if (!sel || (btn && btn.disabled)) return;

  const target = sel.value;
  const expected = getNextStage(currentStage);

  if (!target) {
    showToast("Please select the next stage.", "error");
    return;
  }
  if (target !== expected) {
    showToast(
      `Invalid transition: ${currentStage} → ${target}. Only ${expected} is allowed.`,
      "error",
    );
    return;
  }

  const promptMsg =
    target === "SELECTED"
      ? "Mark this candidate as SELECTED (final stage)?"
      : `Advance this candidate from ${currentStage} to ${target}?`;
  if (!confirm(promptMsg)) return;

  if (btn) {
    btn.disabled = true;
    btn.style.opacity = "0.55";
    btn.style.cursor = "not-allowed";
  }

  try {
    await apiPut(`/candidates/${id}/advance`);
    showToast(
      target === "SELECTED"
        ? "Candidate marked as SELECTED!"
        : `Moved to ${target}!`,
      "success",
    );
    sel.value = "";
    loadCandidates();
    const card = document.getElementById("candidateDetailCard");
    if (card) card.style.display = "none";
  } catch (e) {
    showToast(e.message || "Failed to advance candidate.", "error");
    if (btn) {
      const stillValid = !!sel.value;
      btn.disabled = !stillValid;
      btn.style.opacity = stillValid ? "1" : "0.55";
      btn.style.cursor = stillValid ? "pointer" : "not-allowed";
    }
  }
}

// Legacy advance entry point
async function advanceCandidate(id) {
  if (!confirm("Advance this candidate to the next stage?")) return;
  try {
    await apiPut(`/candidates/${id}/advance`);
    showToast("Candidate advanced!", "success");
    loadCandidates();
    document.getElementById("candidateDetailCard").style.display = "none";
  } catch (e) {
    showToast(e.message || "Failed to advance candidate.", "error");
  }
}

// Reject a candidate
async function rejectCandidate(id) {
  if (!confirm("Reject this candidate?")) return;
  try {
    await apiPut(`/candidates/${id}/reject`);
    showToast("Candidate rejected.", "info");
    loadCandidates();
    document.getElementById("candidateDetailCard").style.display = "none";
  } catch (e) {
    showToast(e.message || "Failed to reject candidate.", "error");
  }
}
