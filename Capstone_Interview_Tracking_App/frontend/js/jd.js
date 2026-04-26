const API_URL = "http://localhost:8080/api/jobs";

function loadJobs() {
  fetch(API_URL)
    .then((res) => res.json())
    .then((data) => {
      let container = document.getElementById("jd-list");
      container.innerHTML = "<h2>Available Jobs</h2>";

      data.forEach((job) => {
        container.innerHTML += `
            <div class="jd-card">
                <h3>${job.title}</h3>

                <p>${job.description}</p>

                <p><b>Location:</b> ${job.location}</p>

                <p><b>Experience:</b> ${job.minExperience} - ${job.maxExperience} years</p>

                <p><b>Skills:</b> ${job.skills.join(", ")}</p>

                <p><b>Job Type:</b> ${job.jobType}</p>

                <p><b>Salary:</b> ₹${job.minSalary} - ₹${job.maxSalary}</p>
            </div>
        `;
      });
    });
}

function getSelectedSkills() {
  const checkboxes = document.querySelectorAll(
    "#skills-container input:checked",
  );

  const selected = [];

  checkboxes.forEach((cb) => {
    selected.push(parseInt(cb.value));
  });

  return selected;
}

function createJD() {
  const data = {
    title: document.getElementById("title").value,
    description: document.getElementById("description").value,
    minExperience: document.getElementById("minExp").value,
    maxExperience: document.getElementById("maxExp").value,
    minSalary: document.getElementById("minSalary").value,
    maxSalary: document.getElementById("maxSalary").value,
    location: document.getElementById("location").value,
    jobType: "FULL_TIME",
    skillIds: getSelectedSkills(),
  };

  fetch(API_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  })
    .then((res) => res.json())
    .then((res) => {
      alert("JD Created Successfully!");
      loadJobs();
    });
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
                        <input type="checkbox" value="${skill.id}" />
                        ${skill.name}
                    </label>
                `;
      });
    });
}
