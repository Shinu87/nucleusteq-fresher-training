// Validation rules for schedule interview form
const interviewRules = [
  { id: "iCandidateId", label: "Candidate", required: true, type: "select" },
  { id: "iStage", label: "Stage", required: true, type: "select" },
  {
    id: "iScheduledAt",
    label: "Date & time",
    required: true,
    custom: (value) => {
      if (!value) return null;
      const d = new Date(value);
      if (Number.isNaN(d.getTime()))
        return "Please enter a valid date and time.";
      if (d <= new Date()) return "Date cannot be in the past.";
      return null;
    },
  },
  { id: "iMeetingUrl", label: "Meeting URL", required: true, type: "url" },
];

// Load and render all interviews
async function loadInterviews() {
  try {
    const interviews = await apiGet("/interviews");
    renderInterviewTable(interviews);
    loadStats();
    loadHrFeedbackOptions();
  } catch (e) {
    showToast("Failed to load interviews", "error");
  }
}

// Render interview table rows
function renderInterviewTable(interviews) {
  const tbody = document.getElementById("interviewTableBody");
  if (!interviews.length) {
    tbody.innerHTML =
      '<tr><td colspan="7" style="text-align:center;color:#aaa;padding:24px;">No interviews scheduled.</td></tr>';
    return;
  }
  tbody.innerHTML = interviews
    .map(
      (iv, i) => `
    <tr>
      <td>${i + 1}</td>
      <td><strong>${iv.candidateName || iv.candidateId}</strong></td>
      <td>${stageBadge(iv.stage)}</td>
      <td>${fmtDate(iv.scheduledAt)}</td>
      <td>${(iv.panelNames || []).join(", ") || "—"}</td>
      <td>${interviewStatusBadge(iv.status)}</td>
      <td>${iv.meetingUrl ? `<a href="${iv.meetingUrl}" target="_blank">Open</a>` : "—"}</td>
    </tr>`,
    )
    .join("");
}

// Toggle panel checkboxes visibility based on selected stage
function onIStageChange() {
  const stage = document.getElementById("iStage").value;
  const panelGroup = document.getElementById("iPanelGroup");
  const hrNotice = document.getElementById("iHrNotice");
  const checkboxes = document.querySelectorAll(
    "#iPanelCheckboxes input[type=checkbox]",
  );

  if (stage === "HR") {
    if (panelGroup) panelGroup.style.display = "none";
    if (hrNotice) hrNotice.style.display = "block";
    // Disable checkboxes so HR stage submits no panel IDs
    checkboxes.forEach((cb) => {
      cb.checked = false;
      cb.disabled = true;
    });
  } else {
    if (panelGroup) panelGroup.style.display = "";
    if (hrNotice) hrNotice.style.display = "none";
    checkboxes.forEach((cb) => {
      cb.disabled = false;
    });
  }
}

// Get checked panel IDs
function getSelectedPanelIds() {
  return [...document.querySelectorAll("#iPanelCheckboxes input:checked")].map(
    (cb) => parseInt(cb.value),
  );
}

// Schedule a new interview
async function scheduleInterview() {
  const msgEl = document.getElementById("iMsg");
  if (!validateAll(interviewRules)) {
    showMsg(msgEl, "error", "Please fix the highlighted fields.");
    return;
  }

  const candidateId = document.getElementById("iCandidateId").value;
  const stage = document.getElementById("iStage").value;
  const scheduledAt = document.getElementById("iScheduledAt").value;
  const meetingUrl = document.getElementById("iMeetingUrl").value.trim();
  const focusArea = document.getElementById("iFocusArea").value.trim();

  // HR stage: backend auto-attaches HR as panel; send null
  const isHrStage = stage === "HR";
  const panelIds = isHrStage ? null : getSelectedPanelIds();

  if (!candidateId || !stage || !scheduledAt) {
    showMsg(msgEl, "error", "Candidate, Stage and Date are required.");
    return;
  }
  if (!meetingUrl) {
    showMsg(msgEl, "error", "Meeting URL is required.");
    return;
  }
  try {
    new URL(meetingUrl);
  } catch {
    showMsg(msgEl, "error", "Meeting URL must be a valid URL.");
    return;
  }

  if (!isHrStage && (panelIds.length < 1 || panelIds.length > 2)) {
    showMsg(msgEl, "error", "Select minimum 1 and maximum 2 panel members.");
    return;
  }
  if (new Date(scheduledAt) <= new Date()) {
    showMsg(msgEl, "error", "Scheduled date and time must be in the future.");
    return;
  }

  try {
    await apiPost("/interviews", {
      candidateId: parseInt(candidateId),
      stage,
      scheduledAt: scheduledAt + ":00",
      meetingUrl,
      focusArea,
      panelIds,
    });
    showMsg(
      msgEl,
      "success",
      isHrStage
        ? "HR interview scheduled. You can submit HR feedback from the Feedback section once the interview time has passed."
        : "Interview scheduled. Email with meeting URL sent to the panel.",
    );
    document.getElementById("iScheduledAt").value = "";
    document.getElementById("iMeetingUrl").value = "";
    document.getElementById("iFocusArea").value = "";
    document
      .querySelectorAll("#iPanelCheckboxes input")
      .forEach((cb) => (cb.checked = false));
    clearAllErrors(interviewRules);
    loadInterviews();
  } catch (e) {
    showMsg(msgEl, "error", e.message || "Failed to schedule interview.");
  }
}
