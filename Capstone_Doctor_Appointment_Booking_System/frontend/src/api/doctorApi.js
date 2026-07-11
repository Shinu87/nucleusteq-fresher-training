// Doctor API functions for searching doctors and fetching doctor details.

import axiosInstance from "./axiosInstance";

const BASE_PATH = "/doctors";

export function searchDoctors(filters) {
  return axiosInstance.get(BASE_PATH, { params: filters });
}

export function getDoctorById(doctorId) {
  return axiosInstance.get(`${BASE_PATH}/${doctorId}`);
}
