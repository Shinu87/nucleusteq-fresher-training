// Load and render dashboard stat counters
async function loadStats() {
  try {
    const [jobs, candidates, panels, interviews] = await Promise.all([
      apiGet("/jobs"),
      apiGet("/candidates"),
      apiGet("/panels"),
      apiGet("/interviews"),
    ]);
    allJobs = jobs;
    allCandidates = candidates;
    allPanels = panels;

    document.getElementById("statJobs").textContent = jobs.length;
    document.getElementById("statCandidates").textContent = candidates.length;
    document.getElementById("statPanels").textContent = panels.length;
    document.getElementById("statInterviews").textContent = interviews.length;
    document.getElementById("statSelected").textContent = candidates.filter(
      (c) => c.status === "SELECTED",
    ).length;
    document.getElementById("statInProgress").textContent = candidates.filter(
      (c) => c.status === "IN_PROGRESS",
    ).length;
  } catch (e) {
    console.error("Failed to load dashboard stats:", e);

    document.getElementById("statJobs").textContent = "-";
    document.getElementById("statCandidates").textContent = "-";
    document.getElementById("statPanels").textContent = "-";
    document.getElementById("statInterviews").textContent = "-";
    document.getElementById("statSelected").textContent = "-";
    document.getElementById("statInProgress").textContent = "-";
  }
}
