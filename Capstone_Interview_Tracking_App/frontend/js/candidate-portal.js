// check user role (only candidate allowed)
const user = requireRole("CANDIDATE");

let allJobs = []; // store all jobs

// set sidebar user info
document.getElementById("sidebarName").textContent = user.name || "Candidate";
document.getElementById("sidebarEmail").textContent = user.email || "";

// load jobs when page loads
window.onload = async () => {
  await loadJobsForCandidate();
};

// JOBS

// function to get all jobs for candidate
async function loadJobsForCandidate() {
  try {
    const jobs = await apiGet("/jobs"); // get jobs from backend

    allJobs = jobs; // save jobs

    renderJobCards(jobs); // show jobs in UI
    populateJobDropdown(jobs); // fill dropdown
  } catch (e) {
    showToast("Failed to load jobs", "error"); // error message
  }
}

// function to show job cards on page
function renderJobCards(jobs) {
  const container = document.getElementById("candidateJobList");

  // if no jobs found
  if (!jobs.length) {
    container.innerHTML =
      '<div class="empty-state"><div class="empty-icon">💼</div><p>No jobs available right now.</p></div>';
    return;
  }

  // create job cards
  container.innerHTML = jobs
    .map(
      (j) => `
    <div class="jd-card">
      <h3>${j.title}</h3>
      <p>${j.description}</p>

      <!-- job details -->
      <p>
        <b>Location</b> ${j.location} &nbsp;|&nbsp;
        <b>JobType</b> ${j.jobType?.replace("_", " ")}
      </p>

      <p>
        <b>Exp:</b> ${j.minExperience}–${j.maxExperience} yrs
        &nbsp;|&nbsp;
        <b>Salary</b> ₹${j.minSalary}–₹${j.maxSalary} LPA
      </p>

      <!-- skills section -->
      <div class="jd-skills">
        ${(j.skills || [])
          .map((s) => `<span class="jd-skill-tag">${s}</span>`)
          .join("")}
      </div>

      <!-- apply button -->
      <button class="btn-apply" onclick="goApply(${j.id})">
        Apply Now →
      </button>
    </div>
  `,
    )
    .join("");
}

// fill job dropdown in apply form
function populateJobDropdown(jobs) {
  const sel = document.getElementById("aJobId");

  // default option
  sel.innerHTML =
    '<option value="">-- Select a Job --</option>' +
    jobs
      .map((j) => `<option value="${j.id}">${j.title} – ${j.location}</option>`)
      .join("");
}

// when user clicks "Apply Now"
function goApply(jobId) {
  // find selected job from list
  const job = allJobs.find((j) => j.id === jobId);

  // hide all pages
  document
    .querySelectorAll(".page-section")
    .forEach((s) => s.classList.remove("active"));

  // remove active from nav
  document
    .querySelectorAll(".nav-item")
    .forEach((n) => n.classList.remove("active"));

  // show apply section
  document.getElementById("section-apply").classList.add("active");
  document.querySelectorAll(".nav-item")[1].classList.add("active");

  // set job id in form
  document.getElementById("applyJobId").value = jobId;
  document.getElementById("aJobId").value = jobId;

  // if job found, show job details
  if (job) {
    document.getElementById("applyJobInfo").style.display = "block";

    document.getElementById("applyJobTitle").textContent =
      `${job.title} — ${job.location}`;

    document.getElementById("applyJobDesc").textContent = job.description;

    document.getElementById("jobSelectGroup").style.display = "none";
  }

  // auto fill user details
  document.getElementById("aEmail").value = user.email || "";
  document.getElementById("aName").value = user.name || "";

  // scroll to top
  window.scrollTo({ top: 0, behavior: "smooth" });
}

// apply form submit function
async function submitApplication() {
  const msgEl = document.getElementById("applyMsg");

  // getting form values
  const name = document.getElementById("aName").value.trim();
  const email = document.getElementById("aEmail").value.trim();
  const phone = document.getElementById("aPhone").value.trim();
  const totalExp = document.getElementById("aTotalExp").value;

  const jobId =
    document.getElementById("aJobId").value ||
    document.getElementById("applyJobId").value;

  const resumeFile = document.getElementById("aResumeFile")?.files[0];

  // check required fields
  if (!name || !email || !phone || !totalExp || !jobId) {
    showMsg(
      msgEl,
      "error",
      "Please fill all required fields (Name, Email, Phone, Total Experience, Job).",
    );
    return;
  }

  // check resume file type and size
  if (resumeFile) {
    if (resumeFile.type !== "application/pdf") {
      showMsg(msgEl, "error", "Resume must be a PDF file.");
      return;
    }

    if (resumeFile.size > 10 * 1024 * 1024) {
      showMsg(msgEl, "error", "Resume file size must be under 10MB.");
      return;
    }
  }

  // creating request body
  const body = {
    name,
    email,
    phone,
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
    // send application to backend
    const result = await apiPost("/candidates", body);

    // if resume uploaded
    if (resumeFile) {
      await uploadResume(result.id, resumeFile, msgEl);
    } else {
      showMsg(
        msgEl,
        "success",
        `Application submitted! Switch to "My Progress" to track your status.`,
      );
    }
  } catch (e) {
    // error message
    showMsg(
      msgEl,
      "error",
      e.message ||
        "Submission failed. An active application may already exist for your email.",
    );
  }
}

// resume upload function
// uploads resume file to backend
async function uploadResume(candidateId, file, msgEl) {
  try {
    const formData = new FormData();
    formData.append("file", file);

    // sending file to backend
    const res = await fetch(`${BASE_URL}/candidates/${candidateId}/resume`, {
      method: "POST",
      body: formData,
    });

    // check response
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || "Resume upload failed");
    }

    // success message
    showMsg(
      msgEl,
      "success",
      'Application submitted with resume! Switch to "My Progress" to track your status.',
    );
  } catch (e) {
    // error while uploading resume
    showMsg(
      msgEl,
      "warning",
      `Application submitted but resume upload failed: ${e.message}. You can re-upload later.`,
    );
  }
}

/* helper function */

// function to show messages on UI
function showMsg(el, type, text) {
  el.style.display = "block"; // make message visible
  el.className = `msg ${type}`; // set message type (success/error/warning)
  el.textContent = text; // set message text
}
