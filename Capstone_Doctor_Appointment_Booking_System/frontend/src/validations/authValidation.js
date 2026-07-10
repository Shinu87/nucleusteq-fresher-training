// Validation rules for patient and doctor registration forms.

import {
  NAME_REGEX,
  PHONE_REGEX,
  PASSWORD_REGEX,
  MIN_PATIENT_AGE,
  MIN_EXPERIENCE_YEARS,
  MAX_EXPERIENCE_YEARS,
} from "../constants/validationConstants";
import { calculateAge } from "../utils/calculateAge";

export const fullNameRules = {
  required: "Full name is required",
  pattern: {
    value: NAME_REGEX,
    message: "Name should only contain letters and spaces",
  },
};

export const emailRules = {
  required: "Email is required",
  pattern: { value: /^\S+@\S+\.\S+$/, message: "Enter a valid email address" },
};

export const phoneRules = {
  required: "Phone number is required",
  pattern: {
    value: PHONE_REGEX,
    message: "Phone number must be exactly 10 digits",
  },
};

export const passwordRules = {
  required: "Password is required",
  pattern: {
    value: PASSWORD_REGEX,
    message:
      "Password must be 8-12 characters with 1 uppercase letter and 1 special character",
  },
};

export const genderRules = {
  required: "Please select a gender",
};

export const dobRules = {
  required: "Date of birth is required",
  validate: (value) => {
    const dobDate = new Date(value);
    if (dobDate > new Date()) {
      return "Date of birth cannot be in the future";
    }
    if (calculateAge(value) < MIN_PATIENT_AGE) {
      return `You must be at least ${MIN_PATIENT_AGE} years old to register`;
    }
    return true;
  },
};

// doctor register form
export const qualificationRules = { required: "Qualification is required" };

export const specializationRules = {
  required: "Please select a specialization",
};

export const experienceRules = {
  required: "Experience is required",
  min: {
    value: MIN_EXPERIENCE_YEARS,
    message: "Experience cannot be negative",
  },
  max: {
    value: MAX_EXPERIENCE_YEARS,
    message: `Experience cannot be more than ${MAX_EXPERIENCE_YEARS} years`,
  },
};

export const licenseNumberRules = { required: "License number is required" };

export const consultationFeeRules = {
  required: "Consultation fee is required",
  min: { value: 1, message: "Consultation fee must be greater than 0" },
};

export const clinicAddressRules = { required: "Clinic address is required" };

export function confirmPasswordRules(getPasswordValue) {
  return {
    required: "Please confirm your password",
    validate: (value) =>
      value === getPasswordValue() || "Passwords do not match",
  };
}
