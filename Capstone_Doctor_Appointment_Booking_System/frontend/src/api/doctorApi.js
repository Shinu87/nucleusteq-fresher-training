// Doctor API functions for searching doctors and fetching doctor details.

import axiosInstance from "./axiosInstance";

const BASE_PATH = "/doctors";

export function searchDoctors(filters) {
  return axiosInstance.get(BASE_PATH, { params: filters });
}

export function getDoctorById(doctorId) {
  return axiosInstance.get(`${BASE_PATH}/${doctorId}`);
}

const SELF_SERVICE_PATH = "/doctor";

// lets a doctor switch their own account between ACTIVE and INACTIVE
export function updateAccountStatus(accountStatus) {
  return axiosInstance.patch(`${SELF_SERVICE_PATH}/account-status`, {
    account_status: accountStatus,
  });
}
