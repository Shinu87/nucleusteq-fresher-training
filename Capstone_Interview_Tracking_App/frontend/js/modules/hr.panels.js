// Validation rules for panel member form
const panelRules = [
  { id: "pName", label: "Full name", required: true, minLength: 2 },
  { id: "pEmail", label: "Email", required: true, type: "email" },
  { id: "pMobile", label: "Mobile", required: true, type: "phone" },
  { id: "pOrg", label: "Organization", required: true, minLength: 2 },
  { id: "pDesig", label: "Designation", required: true, minLength: 2 },
  { id: "pExpertise", label: "Expertise", required: true, type: "select" },
];

// HR mirror row email — filtered from HR-facing panel lists
const HR_EMAIL = "hr@company.com";

function isHrMirrorPanel(p) {
  return (p.email || "").toLowerCase() === HR_EMAIL;
}

// Load panel members
async function loadPanels() {
  try {
    const all = await apiGet("/panels");
    // Hide HR mirror row from the UI; backend uses it internally
    const panels = all.filter((p) => !isHrMirrorPanel(p));
    allPanels = panels;
    renderPanelTable(panels);
    renderPanelCheckboxes(panels);
    loadStats();
  } catch (e) {
    showToast("Failed to load panels", "error");
  }
}

// Render panel members table
function renderPanelTable(panels) {
  const tbody = document.getElementById("panelTableBody");
  if (!panels.length) {
    tbody.innerHTML =
      '<tr><td colspan="8" style="text-align:center;color:#aaa;padding:24px;">No panel members found.</td></tr>';
    return;
  }
  tbody.innerHTML = panels
    .map(
      (p, i) => `
    <tr>
      <td>${i + 1}</td>
      <td><strong>${p.name}</strong></td>
      <td>${p.email}</td>
      <td>${p.mobile}</td>
      <td>${p.organization}</td>
      <td>${p.designation}</td>
      <td><span class="badge badge-blue">${p.expertise || "—"}</span></td>
      <td>${
        p.active
          ? '<span class="badge badge-green">Active</span>'
          : '<span class="badge badge-red">Inactive</span>'
      }</td>
      <td>${
        !p.active
          ? `<button class="btn btn-sm btn-warn"
                   onclick="openActivatePanel(${p.id}, '${p.name.replace(/'/g, "\\'")}', '${p.email}')">
            Activate
           </button>`
          : "—"
      }</td>
    </tr>`,
    )
    .join("");
}

// Render active panel members as checkboxes for interview scheduling
function renderPanelCheckboxes(panels) {
  const container = document.getElementById("iPanelCheckboxes");
  if (!container) return;
  const active = panels.filter((p) => p.active);
  if (!active.length) {
    container.innerHTML =
      '<span style="color:#aaa;font-size:13px;">No active panel members available.</span>';
    return;
  }
  container.innerHTML = active
    .map(
      (p) => `
    <label class="skill-check">
      <input type="checkbox" class="panel-checkbox" value="${p.id}"/>
      <span>${p.name}${p.expertise ? ` <em style="color:#6366f1;font-size:12px;">(${p.expertise})</em>` : ""}</span>
    </label>`,
    )
    .join("");
}

// Add a new panel member
async function createPanel() {
  const msgEl = document.getElementById("pMsg");
  if (!validateAll(panelRules)) {
    showMsg(msgEl, "error", "Please fix the highlighted fields.");
    return;
  }

  const name = document.getElementById("pName").value.trim();
  const email = document.getElementById("pEmail").value.trim();
  const mobile = document.getElementById("pMobile").value.trim();
  const org = document.getElementById("pOrg").value.trim();
  const desig = document.getElementById("pDesig").value.trim();
  const expertise = document.getElementById("pExpertise").value.trim();

  if (!name || !email || !mobile || !org || !desig || !expertise) {
    showMsg(msgEl, "error", "Please fill all required fields.");
    return;
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
    showMsg(msgEl, "error", "Please enter a valid email address.");
    return;
  }
  if (!/^[6-9][0-9]{9}$/.test(mobile)) {
    showMsg(
      msgEl,
      "error",
      "Mobile must be a 10-digit number starting with 6-9.",
    );
    return;
  }

  try {
    await apiPost("/panels", {
      name,
      email,
      mobile,
      organization: org,
      designation: desig,
      expertise,
      active: false,
    });
    showMsg(
      msgEl,
      "success",
      "Panel member added. Click Activate to email them a password setup link.",
    );
    ["pName", "pEmail", "pMobile", "pOrg", "pDesig"].forEach((id) => {
      document.getElementById(id).value = "";
    });
    document.getElementById("pExpertise").value = "";
    clearAllErrors(panelRules);
    loadPanels();
    loadStats();
  } catch (e) {
    showMsg(msgEl, "error", e.message || "Failed to add panel member.");
  }
}

// Open activation confirmation card
function openActivatePanel(id, name, email) {
  pendingActivatePanelId = id;
  document.getElementById("activatePanelName").textContent = name;
  document.getElementById("activatePanelEmail").textContent = email;
  const card = document.getElementById("activatePanelCard");
  card.style.display = "block";
  card.scrollIntoView({ behavior: "smooth" });
}

// Confirm and send activation (backend emails password-setup link)
async function doActivatePanel() {
  const msgEl = document.getElementById("activateMsg");
  if (!pendingActivatePanelId) return;
  try {
    await apiPut(`/panels/${pendingActivatePanelId}/activate`);
    showMsg(
      msgEl,
      "success",
      "✅ Panel activated. Password-setup link emailed to the panel member.",
    );
    setTimeout(() => {
      document.getElementById("activatePanelCard").style.display = "none";
    }, 1800);
    loadPanels();
  } catch (e) {
    showMsg(msgEl, "error", e.message || "Activation failed.");
  }
}
