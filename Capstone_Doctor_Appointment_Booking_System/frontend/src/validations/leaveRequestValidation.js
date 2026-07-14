// Validation rules for the doctor leave request form.

export {
  slotDateRules as leaveDateRules,
  startTimeRules,
  endTimeRules,
} from "./availabilityValidation";

export const reasonRules = {
  required: "Reason is required",
};
