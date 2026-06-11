/* =====================================================
   jd.js – Job Descriptions (standalone / legacy page)
   Used by jd.html for backwards compatibility.
   Main functionality now lives in dashboard.js.
   ===================================================== */

const API_URL = "http://localhost:8080/api/jobs";
let selectedJobType = "FULL_TIME";

function loadJobs() {
  fetch(API_URL)
    .then((res) => res.json())
    .then((data) => {
      let container = document.getElementById("jd-list");
      container.innerHTML = "<h2>Available Jobs</h2>";

      if (!data.length) {
        container.innerHTML +=
          '<p style="color:rgba(255,255,255,0.7);text-align:center;margin-top:20px;">No jobs posted yet.</p>';
        return;
      }

      data.forEach((job) => {
        const user = JSON.parse(localStorage.getItem("user") || "{}");
        const applyBtn =
          user.role !== "HR"
            ? `<button class="btn-apply" onclick="window.location.href='candidate-portal.html'">Apply Now</button>`
            : "";

        container.innerHTML += `
          <div class="jd-card">
            <h3>${job.title}</h3>
            <p>${job.description}</p>
            <p><b>📍 Location:</b> ${job.location}</p>
            <p><b>Experience:</b> ${job.minExperience} – ${job.maxExperience} yrs</p>
            <p><b>Skills:</b> ${job.skills.join(", ") || "N/A"}</p>
            <p><b>Job Type:</b> ${job.jobType?.replace("_", " ")}</p>
            <p><b>💰 Salary:</b> ₹${job.minSalary} – ₹${job.maxSalary} LPA</p>
            ${applyBtn}
          </div>
        `;
      });
    })
    .catch(() => {
      document.getElementById("jd-list").innerHTML +=
        '<p style="color:#ff6b6b;">Failed to load jobs.</p>';
    });
}

function getSelectedSkills() {
  const checkboxes = document.querySelectorAll(
    "#skills-container input:checked",
  );
  return [...checkboxes].map((cb) => parseInt(cb.value));
}

function createJD() {
  const data = {
    title: document.getElementById("title").value,
    description: document.getElementById("description").value,
    minExperience: parseInt(document.getElementById("minExp").value),
    maxExperience: parseInt(document.getElementById("maxExp").value),
    minSalary: parseFloat(document.getElementById("minSalary").value),
    maxSalary: parseFloat(document.getElementById("maxSalary").value),
    location: document.getElementById("location").value,
    jobType: document.getElementById("jobType")
      ? document.getElementById("jobType").value
      : "FULL_TIME",
    skillIds: getSelectedSkills(),
  };

  fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  })
    .then((res) => {
      if (!res.ok)
        return res.json().then((e) => {
          throw e;
        });
      return res.json();
    })
    .then(() => {
      alert("JD Created Successfully!");
      loadJobs();
    })
    .catch((e) => alert(e.message || "Failed to create JD"));
}

function loadSkills() {
  fetch("http://localhost:8080/api/skills")
    .then((res) => res.json())
    .then((data) => {
      const container = document.getElementById("skills-container");
      container.innerHTML = "";
      data.forEach((skill) => {
        container.innerHTML += `
          <label class="skill-item">
            <input type="checkbox" value="${skill.id}"/>
            ${skill.name}
          </label>
        `;
      });
    });
}
