// Availability API functions for a doctor managing their own slots.

import axiosInstance from "./axiosInstance";

const BASE_PATH = "/doctor/availability/slots";

export function createSlot(data) {
  return axiosInstance.post(BASE_PATH, data);
}

export function generateSlots(data) {
  return axiosInstance.post(`${BASE_PATH}/generate`, data);
}

export function blockRange(data) {
  return axiosInstance.post(`${BASE_PATH}/block-range`, data);
}

export function listMySlots() {
  return axiosInstance.get(BASE_PATH);
}

export function updateSlot(slotId, data) {
  return axiosInstance.patch(`${BASE_PATH}/${slotId}`, data);
}

export function deleteSlot(slotId) {
  return axiosInstance.delete(`${BASE_PATH}/${slotId}`);
}
