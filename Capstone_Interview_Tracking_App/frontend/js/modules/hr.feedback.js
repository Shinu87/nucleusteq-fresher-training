// Validation rules for HR feedback form
const hrFeedbackRules = [
  { id: "hrFbInterviewId", label: "HR interview", required: true, type: "select" },
  { id: "hrFbComments",   label: "Comments",     required: true, minLength: 3 },
  {
    id: "hrFbRating", label: "Rating", required: true,
    custom: (value) => {
      const r = parseInt(value, 10);
      if (!r || r < 1 || r > 5) return "Please give a rating between 1 and 5.";
      return null;
    },
  },
  { id: "hrFbStatus", label: "Decision", required: true, type: "select" },
];

// Load feedback for a selected candidate
async function loadFeedbackForCandidate() {
  const candidateId = document.getElementById("fbCandidateFilter").value;
  const container   = document.getElementById("feedbackContent");

  if (!candidateId) {
    container.innerHTML =
      '<div class="empty-state"><div class="empty-icon">📝</div><p>Select a candidate to view feedback.</p></div>';
    return;
  }

  try {
    const feedbacks = await apiGet(`/feedbacks/candidate/${candidateId}`);
    if (!feedbacks.length) {
      container.innerHTML =
        '<div class="empty-state"><div class="empty-icon">📝</div><p>No feedback found for this candidate.</p></div>';
      return;
    }
    container.innerHTML = feedbacks.map(f => `
      <div class="card" style="margin-bottom:16px;">
        <div style="display:flex;justify-content:space-between;align-items:flex-start;flex-wrap:wrap;gap:10px;margin-bottom:12px;">
          <div>
            <strong style="font-size:15px;color:#333;">
              Interview #${f.interviewId} &nbsp;|&nbsp; Panel: ${f.panelName || "#" + f.panelId}
            </strong>
          </div>
          <div>
            ${feedbackStatusBadge(f.status)}
            <span style="margin-left:8px;">${starsHtml(f.rating)}</span>
          </div>
        </div>
        <div class="detail-grid">
          <div class="detail-item"><div class="di-label">Comments</div><div class="di-value">${f.comments}</div></div>
          <div class="detail-item"><div class="di-label">Areas Covered</div><div class="di-value">${f.areasCovered || "—"}</div></div>
          <div class="detail-item"><div class="di-label">Strengths</div><div class="di-value">${f.strengths || "—"}</div></div>
          <div class="detail-item"><div class="di-label">Weaknesses</div><div class="di-value">${f.weaknesses || "—"}</div></div>
        </div>
      </div>`).join("");
  } catch (e) {
    container.innerHTML =
      '<div class="empty-state"><div class="empty-icon">⚠️</div><p>Failed to load feedback.</p></div>';
  }
}

// Resolve HR mirror panel ID
async function getHrPanelId() {
  try {
    const all    = await apiGet("/panels");
    const mirror = all.find(isHrMirrorPanel);
    return mirror ? mirror.id : null;
  } catch { return null; }
}

// Populate HR feedback dropdown with eligible HR-stage interviews
async function loadHrFeedbackOptions() {
  const card   = document.getElementById("hrFeedbackCard");
  const select = document.getElementById("hrFbInterviewId");
  if (!card || !select) return;

  try {
    const [interviews, hrPanelId] = await Promise.all([apiGet("/interviews"), getHrPanelId()]);
    const now          = new Date();
    const hrInterviews = (interviews || []).filter(
      iv => iv.stage === "HR" && iv.scheduledAt && new Date(iv.scheduledAt) <= now
    );

    // Check which interviews still need HR feedback
    const eligible = [];
    if (hrPanelId !== null) {
      for (const iv of hrInterviews) {
        try {
          const res = await apiGet(`/feedbacks/check?interviewId=${iv.id}&panelId=${hrPanelId}`);
          if (!res.submitted) eligible.push(iv);
        } catch { eligible.push(iv); }
      }
    }

    if (!eligible.length) {
      card.style.display  = "none";
      select.innerHTML    = '<option value="">-- Select HR Interview --</option>';
      return;
    }

    card.style.display = "block";
    select.innerHTML =
      '<option value="">-- Select HR Interview --</option>' +
      eligible.map(iv =>
        `<option value="${iv.id}">${iv.candidateName || "Candidate #" + iv.candidateId} — scheduled ${fmtDate(iv.scheduledAt)}</option>`
      ).join("");
  } catch { card.style.display = "none"; }
}

// Set HR feedback star rating
function setHrRating(val) {
  const ratingEl = document.getElementById("hrFbRating");
  ratingEl.value = val;
  document.querySelectorAll("#hrRatingStars .star")
    .forEach(s => s.classList.toggle("selected", parseInt(s.dataset.v) <= val));
  ratingEl.dispatchEvent(new Event("input", { bubbles: true }));
  ratingEl.dispatchEvent(new Event("blur",  { bubbles: true }));
}

// Clear HR feedback form
function clearHrFeedbackForm() {
  ["hrFbComments", "hrFbStrengths", "hrFbWeaknesses", "hrFbAreasCovered"].forEach(id => {
    const el = document.getElementById(id); if (el) el.value = "";
  });
  const sel    = document.getElementById("hrFbInterviewId"); if (sel)    sel.value    = "";
  const status = document.getElementById("hrFbStatus");       if (status) status.value = "";
  const rating = document.getElementById("hrFbRating");       if (rating) rating.value = "0";
  document.querySelectorAll("#hrRatingStars .star").forEach(s => s.classList.remove("selected"));
  clearAllErrors(hrFeedbackRules);
}

// Submit HR feedback via existing POST /feedbacks endpoint
async function submitHrFeedback() {
  const msgEl = document.getElementById("hrFbMsg");
  if (!validateAll(hrFeedbackRules)) {
    showMsg(msgEl, "error", "Please fix the highlighted fields.");
    return;
  }

  const interviewId   = document.getElementById("hrFbInterviewId").value;
  const comments      = document.getElementById("hrFbComments").value.trim();
  const strengths     = document.getElementById("hrFbStrengths").value.trim();
  const weaknesses    = document.getElementById("hrFbWeaknesses").value.trim();
  const areasCovered  = document.getElementById("hrFbAreasCovered").value.trim();
  const rating        = parseInt(document.getElementById("hrFbRating").value);
  const status        = document.getElementById("hrFbStatus").value;

  if (!interviewId)  { showMsg(msgEl, "error", "Please select an HR interview."); return; }
  if (!comments)     { showMsg(msgEl, "error", "Comments are required."); return; }
  if (!rating || rating < 1 || rating > 5) { showMsg(msgEl, "error", "Please give a rating between 1 and 5."); return; }
  if (status !== "SELECTED" && status !== "REJECTED") {
    showMsg(msgEl, "error", "Please choose a decision (Selected / Rejected).");
    return;
  }

  const hrPanelId = await getHrPanelId();
  if (hrPanelId === null) {
    showMsg(msgEl, "error", "HR panel record could not be resolved. Try reloading the page.");
    return;
  }

  try {
    await apiPost("/feedbacks", {
      interviewId: parseInt(interviewId),
      panelId: parseInt(hrPanelId),
      comments, strengths, weaknesses, areasCovered, rating, status,
    });
    showMsg(msgEl, "success", "✅ HR feedback submitted successfully.");
    clearHrFeedbackForm();
    await loadHrFeedbackOptions();
    loadCandidates();
  } catch (e) {
    showMsg(msgEl, "error", e.message || "Failed to submit HR feedback.");
  }
}
