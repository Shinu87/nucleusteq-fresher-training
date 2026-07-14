// Appointment status values, matching the backend AppointmentStatus enum.

export const APPOINTMENT_STATUS = {
  BOOKED: "BOOKED",
  CANCELLED: "CANCELLED",
  COMPLETED: "COMPLETED",
  NO_SHOW: "NO_SHOW",
};

// used to build the filter dropdown on the My Appointments page
export const APPOINTMENT_STATUS_OPTIONS = [
  { label: "All", value: "" },
  { label: "Booked", value: APPOINTMENT_STATUS.BOOKED },
  { label: "Cancelled", value: APPOINTMENT_STATUS.CANCELLED },
  { label: "Completed", value: APPOINTMENT_STATUS.COMPLETED },
  { label: "No Show", value: APPOINTMENT_STATUS.NO_SHOW },
];
