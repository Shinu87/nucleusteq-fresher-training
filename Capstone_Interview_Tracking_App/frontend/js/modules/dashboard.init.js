//  Global state
const user = requireRole("HR");
let allCandidates = [];
let allJobs = [];
let allPanels = [];
let pendingActivatePanelId = null;

//  Sidebar
document.getElementById("sidebarName").textContent = user.name || "HR User";
document.getElementById("sidebarEmail").textContent = user.email || "";

//  Bootstrap
window.onload = () => {
  loadStats();
  loadSkillsForJD();
  loadJDList();
  loadCandidates();
  loadPanels();
  loadInterviews();
  onIStageChange();
  loadHrFeedbackOptions();

  // Wire real-time field-level validation for all HR forms
  wireFieldValidation(jdRules);
  wireFieldValidation(candidateRules);
  wireFieldValidation(panelRules);
  wireFieldValidation(interviewRules);
  wireFieldValidation(hrFeedbackRules);
};

//  Section navigation
function showSection(name) {
  document
    .querySelectorAll(".page-section")
    .forEach((s) => s.classList.remove("active"));
  document
    .querySelectorAll(".nav-item")
    .forEach((n) => n.classList.remove("active"));
  document.getElementById(`section-${name}`).classList.add("active");
  event.currentTarget.classList.add("active");
  const dd = document.getElementById("candidateDetailCard");
  if (dd) dd.style.display = "none";
}

//  Inline form message helper
function showMsg(el, type, text) {
  el.style.display = "block";
  el.className = `msg ${type}`;
  el.textContent = text;
  setTimeout(() => {
    el.style.display = "none";
  }, 4000);
}

//  Panel name input: letters only
document.getElementById("pName").addEventListener("input", function () {
  this.value = this.value.replace(/[^a-zA-Z\s]/g, "");
});
