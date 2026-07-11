// Defines all application routes using React Router.

import { Routes, Route } from "react-router-dom";
import { ROUTES } from "../constants/routes";
import ProtectedRoute from "../components/ProtectedRoute";
import HomePage from "../pages/HomePage";
import NotFoundPage from "../pages/NotFoundPage";
import LoginPage from "../pages/LoginPage";
import PatientRegisterPage from "../pages/PatientRegisterPage";
import DoctorRegisterPage from "../pages/DoctorRegisterPage";
import SetPasswordPage from "../pages/SetPasswordPage";
import DoctorSearchPage from "../pages/DoctorSearchPage";
import DoctorProfilePage from "../pages/DoctorProfilePage";

function AppRoutes() {
  return (
    <Routes>
      <Route path={ROUTES.HOME} element={<HomePage />} />
      <Route path={ROUTES.LOGIN} element={<LoginPage />} />
      <Route path={ROUTES.REGISTER_PATIENT} element={<PatientRegisterPage />} />
      <Route path={ROUTES.REGISTER_DOCTOR} element={<DoctorRegisterPage />} />
      <Route path={ROUTES.SET_PASSWORD} element={<SetPasswordPage />} />
      /* doctor search/profile need a logged in user, any role */
      <Route
        path={ROUTES.DOCTOR_SEARCH}
        element={
          <ProtectedRoute>
            <DoctorSearchPage />
          </ProtectedRoute>
        }
      />
      <Route
        path={ROUTES.DOCTOR_DETAIL}
        element={
          <ProtectedRoute>
            <DoctorProfilePage />
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}

export default AppRoutes;
