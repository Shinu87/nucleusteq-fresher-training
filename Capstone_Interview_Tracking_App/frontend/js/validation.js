/* =====================================================
   validation.js - shared per-field validation helper
   -----------------------------------------------------
   Each page declares an array of "rule" objects and calls
   wireFieldValidation(rules). The helper:

   • on blur          -> validates that field
   • on input/change  -> re-validates AFTER the field has
                         already failed once (so we don't
                         shout at the user while they're
                         typing the very first time)
   • renders a <small class="field-error"> directly below
     the field with the message
   • toggles a .input-invalid class on the field itself
   • exposes validateAll(rules) to be called from existing
     submit handlers as a final line of defence

   Rule shape:
   {
     id:       "fieldId"      // required - the input/select/textarea element id
     label:    "Full Name"    // optional - used in default messages
     required: true,          // optional
     type:     "email" | "phone" | "url" | "text" | "number" | "select" | "file"
     min:      0,             // optional - for numbers
     max:      99,            // optional - for numbers
     minLength: 6,            // optional - for text/password
     accept:   ["application/pdf"],   // optional - for files
     maxSizeMB: 10,                   // optional - for files
     pattern:  /regex/,
     patternMsg: "...",
     custom:   (value, allValues) => "msg" | null
   }
====================================================== */

/* ── error element helpers ───────────────────────────── */

function fvErrorEl(fieldId, create = false) {
  const el = document.getElementById(fieldId);
  if (!el) return null;

  // password fields live inside a .password-wrapper; the error
  // should attach AFTER the wrapper, not after the bare input.
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

/* ── pure validators (return string msg or null) ─────── */

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

  // everything else is value-based
  let value = el.value;
  if (typeof value === "string") value = value.trim();

  // required check
  if (
    rule.required &&
    (value === "" || value === null || value === undefined)
  ) {
    return rule.requiredMsg || `${label} is required.`;
  }

  // optional empty -> nothing more to check
  if (value === "" || value === null || value === undefined) return null;

  // type-specific built-ins
  switch (rule.type) {
    case "email":
      if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)) {
        return "Please enter a valid email address.";
      }
      break;
    case "phone":
      // 10-digit Indian style starting 6-9
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
      // empty handled by required above
      break;
    case "text":
    default:
      if (rule.minLength && value.length < rule.minLength) {
        return `${label} must be at least ${rule.minLength} characters.`;
      }
      break;
  }

  // generic minLength (for password etc., even when type omitted)
  if (rule.minLength && value.length < rule.minLength) {
    return `${label} must be at least ${rule.minLength} characters.`;
  }

  // pattern
  if (rule.pattern && !rule.pattern.test(value)) {
    return rule.patternMsg || `${label} is not in the expected format.`;
  }

  // cross-field / custom validator
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

/* ── wire blur + smart-input listeners ───────────────── */

function wireFieldValidation(rules) {
  if (!Array.isArray(rules)) return;

  // remember which fields have already failed once - we only
  // start re-validating on every keystroke AFTER that.
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

    // BLUR -> always validate on field exit
    el.addEventListener("blur", runValidation);

    // INPUT/CHANGE -> only after the field has failed once.
    // This is the polite UX: clear the red as soon as the user
    // starts fixing it, but don't yell while they're still
    // filling it in for the first time.
    const liveEvent =
      el.tagName === "SELECT" || rule.type === "file" ? "change" : "input";
    el.addEventListener(liveEvent, () => {
      if (dirty.has(rule.id)) runValidation();
    });

    // cross-field rules (e.g. confirmPassword vs password):
    // when a "watches" partner changes, re-run this rule too.
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

/* ── final-pass for submit handlers ──────────────────── */

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

/* ── reset all errors for a rule set (used after success) ── */
function clearAllErrors(rules) {
  rules.forEach((r) => fvClearError(r.id));
}
