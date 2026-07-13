// Admin API functions for the admin dashboard and management pages.

import axiosInstance from "./axiosInstance";

const BASE_PATH = "/admin";

export function getPlatformStats() {
  return axiosInstance.get(`${BASE_PATH}/stats`);
}

// doctor application review
export function listDoctorApplications(approvalStatus) {
  const params = approvalStatus ? { approval_status: approvalStatus } : {};
  return axiosInstance.get(`${BASE_PATH}/doctors`, { params });
}

export function approveDoctor(doctorProfileId) {
  return axiosInstance.post(`${BASE_PATH}/doctors/${doctorProfileId}/approve`);
}

export function rejectDoctor(doctorProfileId, reason) {
  return axiosInstance.post(`${BASE_PATH}/doctors/${doctorProfileId}/reject`, {
    reason,
  });
}

export function listPatients(accountStatus) {
  const params = accountStatus ? { account_status: accountStatus } : {};
  return axiosInstance.get(`${BASE_PATH}/patients`, { params });
}

// leave request review
export function listLeaveRequests(requestStatus) {
  const params = requestStatus ? { request_status: requestStatus } : {};
  return axiosInstance.get(`${BASE_PATH}/leave-requests`, { params });
}

export function approveLeaveRequest(leaveRequestId) {
  return axiosInstance.patch(
    `${BASE_PATH}/leave-requests/${leaveRequestId}/approve`,
  );
}

export function rejectLeaveRequest(leaveRequestId, rejectionReason) {
  return axiosInstance.patch(
    `${BASE_PATH}/leave-requests/${leaveRequestId}/reject`,
    {
      rejection_reason: rejectionReason,
    },
  );
}
