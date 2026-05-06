/* error element helpers */

function fvErrorEl(fieldId, create = false) {
  const el = document.getElementById(fieldId);
  if (!el) return null;

  const anchor = el.closest(".password-wrapper") || el;

  const next = anchor.nextElementSibling;
  if (next && next.classList && next.classList.contains("field-error")) {
    return next;
  }
  if (!create) return null;

  const small = document.createElement("small");
  small.className = "field-error";
  small.id = `${fieldId}-error`;
  small.setAttribute("role", "alert");
  anchor.parentNode.insertBefore(small, anchor.nextSibling);
  return small;
}

function fvSetError(fieldId, message) {
  const field = document.getElementById(fieldId);
  if (!field) return;
  const errEl = fvErrorEl(fieldId, true);
  if (message) {
    errEl.textContent = message;
    field.classList.add("input-invalid");
    field.setAttribute("aria-invalid", "true");
  } else {
    errEl.textContent = "";
    field.classList.remove("input-invalid");
    field.removeAttribute("aria-invalid");
  }
}

function fvClearError(fieldId) {
  fvSetError(fieldId, "");
}

/* validators  */

function fvValidateRule(rule, allRules) {
  const el = document.getElementById(rule.id);
  if (!el) return null;

  const label = rule.label || rule.id;

  // FILE inputs
  if (rule.type === "file") {
    const file = el.files && el.files[0];
    if (rule.required && !file) return `${label} is required.`;
    if (!file) return null;
    if (rule.accept && rule.accept.length && !rule.accept.includes(file.type)) {
      return (
        rule.acceptMsg || `${label} must be a ${rule.accept.join(" / ")} file.`
      );
    }
    if (rule.maxSizeMB && file.size > rule.maxSizeMB * 1024 * 1024) {
      return `${label} must be under ${rule.maxSizeMB}MB.`;
    }
    return null;
  }

  let value = el.value;
  if (typeof value === "string") value = value.trim();

  if (
    rule.required &&
    (value === "" || value === null || value === undefined)
  ) {
    return rule.requiredMsg || `${label} is required.`;
  }

  if (value === "" || value === null || value === undefined) return null;

  switch (rule.type) {
    case "email":
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
        return "Please enter a valid email address.";
      }
      break;
    case "phone":
      if (!/^[6-9][0-9]{9}$/.test(value)) {
        return `${label} must be a 10-digit number starting with 6-9.`;
      }
      break;
    case "url":
      try {
        new URL(value);
      } catch {
        return `${label} must be a valid URL (https://…).`;
      }
      break;
    case "number": {
      const num = Number(value);
      if (Number.isNaN(num)) return `${label} must be a number.`;
      if (rule.min !== undefined && num < rule.min)
        return `${label} cannot be less than ${rule.min}.`;
      if (rule.max !== undefined && num > rule.max)
        return `${label} cannot be more than ${rule.max}.`;
      break;
    }
    case "select":
      break;
    case "text":
    default:
      if (rule.minLength && value.length < rule.minLength) {
        return `${label} must be at least ${rule.minLength} characters.`;
      }
      break;
  }

  if (rule.minLength && value.length < rule.minLength) {
    return `${label} must be at least ${rule.minLength} characters.`;
  }

  if (rule.pattern && !rule.pattern.test(value)) {
    return rule.patternMsg || `${label} is not in the expected format.`;
  }

  if (typeof rule.custom === "function") {
    const allValues = {};
    (allRules || [rule]).forEach((r) => {
      const e = document.getElementById(r.id);
      if (!e) return;
      allValues[r.id] = r.type === "file" ? e.files && e.files[0] : e.value;
    });
    const msg = rule.custom(value, allValues);
    if (msg) return msg;
  }

  return null;
}

function wireFieldValidation(rules) {
  if (!Array.isArray(rules)) return;
  const dirty = new Set();

  rules.forEach((rule) => {
    const el = document.getElementById(rule.id);
    if (!el) return;

    const runValidation = () => {
      const msg = fvValidateRule(rule, rules);
      fvSetError(rule.id, msg);
      if (msg) dirty.add(rule.id);
      return !msg;
    };

    el.addEventListener("blur", runValidation);

    const liveEvent =
      el.tagName === "SELECT" || rule.type === "file" ? "change" : "input";
    el.addEventListener(liveEvent, () => {
      if (dirty.has(rule.id)) runValidation();
    });

    if (Array.isArray(rule.watches)) {
      rule.watches.forEach((otherId) => {
        const other = document.getElementById(otherId);
        if (!other) return;
        other.addEventListener("input", () => {
          if (dirty.has(rule.id)) runValidation();
        });
      });
    }
  });
}

/* final-pass for submit handlers */

function validateAll(rules) {
  let firstBadId = null;
  rules.forEach((rule) => {
    const msg = fvValidateRule(rule, rules);
    fvSetError(rule.id, msg);
    if (msg && !firstBadId) firstBadId = rule.id;
  });
  if (firstBadId) {
    const el = document.getElementById(firstBadId);
    if (el && typeof el.focus === "function") el.focus();
    return false;
  }
  return true;
}

/*  reset all errors for a rule set */
function clearAllErrors(rules) {
  rules.forEach((r) => fvClearError(r.id));
}
