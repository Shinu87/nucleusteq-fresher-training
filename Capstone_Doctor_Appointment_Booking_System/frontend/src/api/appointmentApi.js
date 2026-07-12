// Appointment API functions for patients and doctors managing appointments.

import axiosInstance from "./axiosInstance";

const BASE_PATH = "/appointments";

export function bookAppointment(slotId) {
  return axiosInstance.post(BASE_PATH, { slot_id: slotId });
}

export function getMyAppointments(statusFilter) {
  const params = statusFilter ? { appointment_status: statusFilter } : {};
  return axiosInstance.get(`${BASE_PATH}/my-appointments`, { params });
}

export function cancelAppointment(appointmentId) {
  return axiosInstance.patch(`${BASE_PATH}/${appointmentId}/cancel`);
}

// doctor side: view own appointments and update their outcome
export function getDoctorAppointments(statusFilter, sort) {
  const params = { sort };
  if (statusFilter) params.appointment_status = statusFilter;
  return axiosInstance.get(`${BASE_PATH}/doctor/appointments`, { params });
}

export function completeAppointment(appointmentId) {
  return axiosInstance.patch(`${BASE_PATH}/${appointmentId}/complete`);
}

export function markNoShow(appointmentId) {
  return axiosInstance.patch(`${BASE_PATH}/${appointmentId}/no-show`);
}
