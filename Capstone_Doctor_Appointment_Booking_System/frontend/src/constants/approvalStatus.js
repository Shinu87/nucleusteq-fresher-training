// Doctor application approval status values, matching the backend
// ApprovalStatus enum.

export const APPROVAL_STATUS = {
  PENDING: "PENDING",
  APPROVED: "APPROVED",
  REJECTED: "REJECTED",
};

// used to build the filter dropdown on the admin doctor approvals page
export const APPROVAL_STATUS_OPTIONS = [
  { label: "Pending", value: APPROVAL_STATUS.PENDING },
  { label: "Approved", value: APPROVAL_STATUS.APPROVED },
  { label: "Rejected", value: APPROVAL_STATUS.REJECTED },
  { label: "All", value: "" },
];
