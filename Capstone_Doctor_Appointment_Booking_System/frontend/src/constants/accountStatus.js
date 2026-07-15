// Account status values, matching the backend AccountStatus enum.

export const ACCOUNT_STATUS = {
  ACTIVE: "ACTIVE",
  INACTIVE: "INACTIVE",
};

// used to build the filter dropdown on the admin patients page
export const ACCOUNT_STATUS_OPTIONS = [
  { label: "All", value: "" },
  { label: "Active", value: ACCOUNT_STATUS.ACTIVE },
  { label: "Inactive", value: ACCOUNT_STATUS.INACTIVE },
];
