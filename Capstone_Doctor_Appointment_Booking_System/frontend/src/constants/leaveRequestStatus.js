// Leave request status values, matching the backend LeaveRequestStatus enum.

export const LEAVE_REQUEST_STATUS = {
  PENDING: "PENDING",
  APPROVED: "APPROVED",
  REJECTED: "REJECTED",
};

// used to build the filter dropdown on the admin leave requests page
export const LEAVE_REQUEST_STATUS_OPTIONS = [
  { label: "Pending", value: LEAVE_REQUEST_STATUS.PENDING },
  { label: "Approved", value: LEAVE_REQUEST_STATUS.APPROVED },
  { label: "Rejected", value: LEAVE_REQUEST_STATUS.REJECTED },
  { label: "All", value: "" },
];
