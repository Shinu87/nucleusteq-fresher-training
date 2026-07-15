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
import MyAppointmentsPage from "../pages/MyAppointmentsPage";
import DoctorDashboardPage from "../pages/DoctorDashboardPage";
import DoctorAvailabilityPage from "../pages/DoctorAvailabilityPage";
import DoctorAppointmentsPage from "../pages/DoctorAppointmentsPage";
import { ROLES } from "../constants/roles";
import DoctorLeaveRequestPage from "../pages/DoctorLeaveRequestPage";
import BookingConfirmationPage from "../pages/BookingConfirmationPage";
import AdminDashboardPage from "../pages/AdminDashboardPage";
import AdminDoctorApprovalsPage from "../pages/AdminDoctorApprovalsPage";
import AdminPatientsPage from "../pages/AdminPatientsPage";
import AdminLeaveRequestsPage from "../pages/AdminLeaveRequestsPage";
import ProfilePage from "../pages/ProfilePage";
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
      /* only patients confirm payment and complete a booking */
      <Route
        path={ROUTES.BOOKING_CONFIRMATION}
        element={
          <ProtectedRoute allowedRoles={[ROLES.PATIENT]}>
            <BookingConfirmationPage />
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
      /* only patients can view their own appointments */
      <Route
        path={ROUTES.MY_APPOINTMENTS}
        element={
          <ProtectedRoute allowedRoles={[ROLES.PATIENT]}>
            <MyAppointmentsPage />
          </ProtectedRoute>
        }
      />
      /* only doctors can view their dashboard */
      <Route
        path={ROUTES.DOCTOR_DASHBOARD}
        element={
          <ProtectedRoute allowedRoles={[ROLES.DOCTOR]}>
            <DoctorDashboardPage />
          </ProtectedRoute>
        }
      />
      /* only doctors manage their own availability */
      <Route
        path={ROUTES.DOCTOR_AVAILABILITY}
        element={
          <ProtectedRoute allowedRoles={[ROLES.DOCTOR]}>
            <DoctorAvailabilityPage />
          </ProtectedRoute>
        }
      />
      /* only doctors view/act on their own appointments */
      <Route
        path={ROUTES.DOCTOR_APPOINTMENTS}
        element={
          <ProtectedRoute allowedRoles={[ROLES.DOCTOR]}>
            <DoctorAppointmentsPage />
          </ProtectedRoute>
        }
      />
      {/* only doctors request/view their own leave requests */}
      <Route
        path={ROUTES.DOCTOR_LEAVE_REQUESTS}
        element={
          <ProtectedRoute allowedRoles={[ROLES.DOCTOR]}>
            <DoctorLeaveRequestPage />
          </ProtectedRoute>
        }
      />
      {/* only admins view the admin dashboard */}
      <Route
        path={ROUTES.ADMIN_DASHBOARD}
        element={
          <ProtectedRoute allowedRoles={[ROLES.ADMIN]}>
            <AdminDashboardPage />
          </ProtectedRoute>
        }
      />
      {/* only admins review doctor applications */}
      <Route
        path={ROUTES.ADMIN_DOCTOR_APPROVALS}
        element={
          <ProtectedRoute allowedRoles={[ROLES.ADMIN]}>
            <AdminDoctorApprovalsPage />
          </ProtectedRoute>
        }
      />
      {/* only admins view the patient list */}
      <Route
        path={ROUTES.ADMIN_PATIENTS}
        element={
          <ProtectedRoute allowedRoles={[ROLES.ADMIN]}>
            <AdminPatientsPage />
          </ProtectedRoute>
        }
      />
      <Route path="*" element={<NotFoundPage />} />
      {/* only admins review doctor leave requests */}
      <Route
        path={ROUTES.ADMIN_LEAVE_REQUESTS}
        element={
          <ProtectedRoute allowedRoles={[ROLES.ADMIN]}>
            <AdminLeaveRequestsPage />
          </ProtectedRoute>
        }
      />
      {/* any authenticated user (patient, doctor, admin) can view their own profile */}
      <Route
        path={ROUTES.PROFILE}
        element={
          <ProtectedRoute>
            <ProfilePage />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
}

export default AppRoutes;
