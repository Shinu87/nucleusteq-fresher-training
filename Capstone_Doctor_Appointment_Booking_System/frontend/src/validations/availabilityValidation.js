// Validation rules for the doctor availability forms.

import {
  MIN_SLOT_DURATION_MINUTES,
  MAX_SLOT_DURATION_MINUTES,
} from "../constants/validationConstants";

export const slotDateRules = {
  required: "Date is required",
};

export const startTimeRules = {
  required: "Start time is required",
};

export const endTimeRules = {
  required: "End time is required",
};

export const durationMinutesRules = {
  required: "Slot duration is required",
  min: {
    value: MIN_SLOT_DURATION_MINUTES,
    message: `Duration must be at least ${MIN_SLOT_DURATION_MINUTES} minutes`,
  },
  max: {
    value: MAX_SLOT_DURATION_MINUTES,
    message: `Duration cannot be more than ${MAX_SLOT_DURATION_MINUTES} minutes`,
  },
};
