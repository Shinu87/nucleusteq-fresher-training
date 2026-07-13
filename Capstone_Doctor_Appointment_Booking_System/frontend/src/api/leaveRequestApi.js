// Leave request API functions for a doctor requesting and viewing their own leave.

import axiosInstance from "./axiosInstance";

const BASE_PATH = "/doctor/leave-request";

export function requestLeave(data) {
  return axiosInstance.post(BASE_PATH, data);
}

export function getMyLeaveRequests() {
  return axiosInstance.get(BASE_PATH);
}
