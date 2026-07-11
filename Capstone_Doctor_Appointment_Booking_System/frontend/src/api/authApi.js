// Authentication API functions for user registration, login, and profile management.

import axiosInstance from "./axiosInstance";
import { API_ENDPOINTS } from "../constants/apiEndpoints";

export function registerPatient(data) {
  return axiosInstance.post(API_ENDPOINTS.REGISTER_PATIENT, data);
}

export function registerDoctor(data) {
  return axiosInstance.post(API_ENDPOINTS.REGISTER_DOCTOR, data);
}

export function setPassword(data) {
  return axiosInstance.post(API_ENDPOINTS.SET_PASSWORD, data);
}

export function login(data) {
  return axiosInstance.post(API_ENDPOINTS.LOGIN, data);
}

export function getMyProfile() {
  return axiosInstance.get(API_ENDPOINTS.MY_PROFILE);
}
