// panel-dashboard.js - panel member portal
const user = requireRole("PANEL");
let allInterviews = [];
let myPanelId = null;

// Feedback form field validation rules
const feedbackRules = [
  {
    id: "fbInterviewId",
    label: "Interview",
    required: true,
    type: "select",
  },
  {
    id: "fbComments",
    label: "Comments",
    required: true,
    minLength: 3,
  },
  {
    // rating is a hidden input updated by clicking stars
    id: "fbRating",
    label: "Rating",
    required: true,
    custom: (value) => {
      const r = parseInt(value);
      if (!r || r < 1 || r > 5) return "Please give a rating between 1 and 5.";
      return null;
    },
  },
  {
    id: "fbStatus",
    label: "Decision",
    required: true,
    type: "select",
  },
];

// fill sidebar with logged-in user info
document.getElementById("sidebarName").textContent =
  user.name || "Panel Member";
document.getElementById("sidebarEmail").textContent = user.email || "";

window.onload = async () => {
  await resolveMyPanelId();
  await loadAllInterviews();
  wireFieldValidation(feedbackRules);
};

// Section navigation
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

// auto detect panel id from logged-in user email
async function resolveMyPanelId() {
  try {
    const panels = await apiGet("/panels");
    const match = panels.find(
      (p) => (p.email || "").toLowerCase() === (user.email || "").toLowerCase(),
    );
    if (match) {
      myPanelId = match.id;
    } else {
      showToast("Your panel record could not be located.", "error");
    }
  } catch {
    showToast("Unable to fetch panel details. Please try again.", "error");
  }
}

// Load interviews
async function loadAllInterviews() {
  try {
    const interviews = await apiGet("/interviews");

    if (myPanelId) {
      allInterviews = interviews.filter((iv) =>
        (iv.panelIds || []).includes(myPanelId),
      );
    } else {
      allInterviews = interviews;
    }

    await renderPanelInterviews(allInterviews);
    populateInterviewDropdown(allInterviews);
  } catch (e) {
    showToast("Failed to load interviews", "error");
  }
}

async function renderPanelInterviews(interviews) {
  const tbody = document.getElementById("panelInterviewBody");
  if (!interviews.length) {
    tbody.innerHTML =
      '<tr><td colspan="8" style="text-align:center;color:#aaa;padding:24px;">No interviews assigned.</td></tr>';
    return;
  }

  // for each interview also check feedback submission status
  const rows = await Promise.all(
    interviews.map(async (iv, i) => {
      // panel can submit feedback when status is ONGOING or COMPLETED
      const canSubmit = iv.status === "ONGOING" || iv.status === "COMPLETED";

      let feedbackBadge = "";
      if (myPanelId && canSubmit) {
        try {
          const res = await apiGet(
            `/feedbacks/check?interviewId=${iv.id}&panelId=${myPanelId}`,
          );
          feedbackBadge = res.submitted
            ? '<span class="badge badge-green" style="margin-left:6px;font-size:11px;">Feedback Submitted</span>'
            : '<span class="badge badge-yellow" style="margin-left:6px;font-size:11px;">Feedback Pending</span>';
        } catch {
          console.warn("Feedback status check failed:", error);
        }
      } else if (iv.status === "SCHEDULED") {
        feedbackBadge =
          '<span class="badge badge-gray" style="margin-left:6px;font-size:11px;">Not Yet</span>';
      }

      // meeting url cell
      const meetingCell = iv.meetingUrl
        ? `<a href="${iv.meetingUrl}" target="_blank">Open</a>`
        : "—";

      return `
      <tr>
        <td>${i + 1}</td>
        <td><strong>${iv.candidateName || "Candidate #" + iv.candidateId}</strong></td>
        <td>${stageBadge(iv.stage)}</td>
        <td>${fmtDate(iv.scheduledAt)}</td>
        <td style="max-width:160px;word-break:break-word;">${iv.focusArea || "—"}</td>
        <td>${meetingCell}</td>
        <td>${interviewStatusBadge(iv.status)}${feedbackBadge}</td>
        <td>
          <button class="btn btn-sm btn-info"
                  onclick="viewCandidateProfile(${iv.candidateId})">Profile</button>
          ${
            canSubmit
              ? `<button class="btn btn-sm btn-primary" style="margin-left:4px;"
                       onclick="goSubmitFeedback(${iv.id})">Feedback</button>`
              : ""
          }
        </td>
      </tr>
    `;
    }),
  );

  tbody.innerHTML = rows.join("");
}

function populateInterviewDropdown(interviews) {
  const sel = document.getElementById("fbInterviewId");
  // only ONGOING/COMPLETED interviews can receive feedback
  const eligible = interviews.filter(
    (iv) => iv.status === "ONGOING" || iv.status === "COMPLETED",
  );
  if (eligible.length === 0) {
    sel.innerHTML =
      '<option value="">-- No interviews ready for feedback --</option>';
  } else {
    sel.innerHTML =
      '<option value="">-- Select Interview --</option>' +
      eligible
        .map(
          (iv) => `
          <option value="${iv.id}">
            ${iv.candidateName || "Candidate #" + iv.candidateId}
            – ${iv.stage} (${fmtDate(iv.scheduledAt)})
          </option>`,
        )
        .join("");
  }
}

// Open feedback form with selected interview pre-filled
function goSubmitFeedback(interviewId) {
  document
    .querySelectorAll(".page-section")
    .forEach((s) => s.classList.remove("active"));
  document
    .querySelectorAll(".nav-item")
    .forEach((n) => n.classList.remove("active"));
  document.getElementById("section-feedback").classList.add("active");
  if (document.querySelectorAll(".nav-item")[1]) {
    document.querySelectorAll(".nav-item")[1].classList.add("active");
  }
  document.getElementById("fbInterviewId").value = interviewId;
  window.scrollTo({ top: 0, behavior: "smooth" });
}

async function viewCandidateProfile(candidateId) {
  try {
    const c = await apiGet(`/candidates/${candidateId}`);
    const card = document.getElementById("candidateProfileCard");
    const content = document.getElementById("candidateProfileContent");

    // resume link (served via internal drive endpoint)
    const resumeLink = c.resumeUrl
      ? `<p style="margin-top:12px;font-size:13px;">📄
          <a href="${BASE_URL}/resumes/download?path=${encodeURIComponent(c.resumeUrl)}"
             target="_blank">Download Resume</a></p>`
      : '<p style="margin-top:12px;font-size:13px;color:#aaa;">No resume uploaded.</p>';

    content.innerHTML = `
      <div class="detail-grid">
        <div class="detail-item"><div class="di-label">Name</div><div class="di-value">${c.name}</div></div>
        <div class="detail-item"><div class="di-label">Email</div><div class="di-value">${c.email}</div></div>
        <div class="detail-item"><div class="di-label">Phone</div><div class="di-value">${c.phone}</div></div>
        <div class="detail-item"><div class="di-label">Organization</div><div class="di-value">${c.currentOrganization || "—"}</div></div>
        <div class="detail-item"><div class="di-label">Total Exp</div><div class="di-value">${c.totalExperience} yrs</div></div>
        <div class="detail-item"><div class="di-label">Relevant Exp</div><div class="di-value">${c.relevantExperience || "—"} yrs</div></div>
        <div class="detail-item"><div class="di-label">Current CTC</div><div class="di-value">${c.currentCTC ? "₹" + c.currentCTC + " LPA" : "—"}</div></div>
        <div class="detail-item"><div class="di-label">Expected CTC</div><div class="di-value">${c.expectedCTC ? "₹" + c.expectedCTC + " LPA" : "—"}</div></div>
        <div class="detail-item"><div class="di-label">Notice Period</div><div class="di-value">${c.noticePeriod || "—"}</div></div>
        <div class="detail-item"><div class="di-label">Preferred Location</div><div class="di-value">${c.preferredLocation || "—"}</div></div>
      </div>
      ${resumeLink}
    `;
    card.style.display = "block";
    card.scrollIntoView({ behavior: "smooth" });
  } catch (e) {
    showToast("Failed to load candidate profile", "error");
  }
}

// Rating stars
function setRating(val) {
  document.getElementById("fbRating").value = val;
  document.querySelectorAll(".star").forEach((s) => {
    s.classList.toggle("selected", parseInt(s.dataset.v) <= val);
  });
  const msg = fvValidateRule(
    feedbackRules.find((r) => r.id === "fbRating"),
    feedbackRules,
  );
  fvSetError("fbRating", msg);
}

// Submit feedback
async function submitFeedback() {
  const msgEl = document.getElementById("fbMsg");

  const panelId = myPanelId;

  if (!panelId) {
    showMsg(
      msgEl,
      "error",
      "Could not detect your panel record. Please log out and log in again.",
    );
    return;
  }

  if (!validateAll(feedbackRules)) {
    showMsg(msgEl, "error", "Please fix the highlighted fields.");
    return;
  }

  const interviewId = document.getElementById("fbInterviewId").value;
  const comments = document.getElementById("fbComments").value.trim();
  const rating = parseInt(document.getElementById("fbRating").value);
  const status = document.getElementById("fbStatus").value;

  // pre-check duplicate feedback
  try {
    const check = await apiGet(
      `/feedbacks/check?interviewId=${interviewId}&panelId=${panelId}`,
    );
    if (check.submitted) {
      showMsg(msgEl, "error", "Feedback already submitted for this interview.");
      return;
    }
  } catch {
    console.warn("Feedback check failed:", error);
  }

  try {
    await apiPost("/feedbacks", {
      interviewId: parseInt(interviewId),
      panelId: parseInt(panelId),
      comments,
      strengths: document.getElementById("fbStrengths").value.trim(),
      weaknesses: document.getElementById("fbWeaknesses").value.trim(),
      areasCovered: document.getElementById("fbAreasCovered").value.trim(),
      rating,
      status,
    });
    showMsg(msgEl, "success", "✅ Feedback submitted successfully.");
    clearFeedbackForm();
    await loadAllInterviews();
  } catch (e) {
    showMsg(msgEl, "error", e.message || "Failed to submit feedback.");
  }
}

function clearFeedbackForm() {
  ["fbComments", "fbStrengths", "fbWeaknesses", "fbAreasCovered"].forEach(
    (id) => {
      document.getElementById(id).value = "";
    },
  );
  document.getElementById("fbInterviewId").value = "";
  document.getElementById("fbRating").value = "0";
  document
    .querySelectorAll(".star")
    .forEach((s) => s.classList.remove("selected"));
  clearAllErrors(feedbackRules);
}

function showMsg(el, type, text) {
  el.style.display = "block";
  el.className = `msg ${type}`;
  el.textContent = text;
  setTimeout(() => {
    el.style.display = "none";
  }, 5000);
}
