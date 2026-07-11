// Common validation rules and limits used across the application.

export const NAME_REGEX = /^[A-Za-z\s]{2,}$/;
export const PHONE_REGEX = /^\d{10}$/;

// 8-12 chars, at least 1 uppercase letter, at least 1 special character
export const PASSWORD_REGEX =
  /^(?=.*[A-Z])(?=.*[!@#$%^&*(),.?":{}|<>])[A-Za-z\d!@#$%^&*(),.?":{}|<>]{8,12}$/;

export const MIN_PATIENT_AGE = 15;
export const MIN_EXPERIENCE_YEARS = 0;
export const MAX_EXPERIENCE_YEARS = 70;
