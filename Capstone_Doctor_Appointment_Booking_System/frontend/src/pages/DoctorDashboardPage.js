import { useState } from "react";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";
import { useAuth } from "../context/AuthContext";
import { updateAccountStatus } from "../api/doctorApi";
import { handleApiError } from "../utils/handleApiError";
import { ACCOUNT_STATUS } from "../constants/accountStatus";
import { ROUTES } from "../constants/routes";
import {
  FaUserMd,
  FaEnvelope,
  FaPhone,
  FaCheckCircle,
  FaTimesCircle,
  FaCalendarAlt,
  FaClipboardList,
  FaPlaneDeparture,
} from "react-icons/fa";

function DoctorDashboardPage() {
  const { user } = useAuth();
  const [accountStatus, setAccountStatus] = useState(user.account_status);
  const [updating, setUpdating] = useState(false);

  const isActive = accountStatus === ACCOUNT_STATUS.ACTIVE;

  // toggle between ACTIVE and INACTIVE
  async function handleToggleStatus() {
    const nextStatus = isActive
      ? ACCOUNT_STATUS.INACTIVE
      : ACCOUNT_STATUS.ACTIVE;

    if (nextStatus === ACCOUNT_STATUS.INACTIVE) {
      const confirmed = window.confirm(
        "Going inactive hides you from patient search. Continue?",
      );
      if (!confirmed) return;
    }

    setUpdating(true);
    try {
      const response = await updateAccountStatus(nextStatus);
      setAccountStatus(response.data.account_status);
      toast.success(`Account is now ${response.data.account_status}`);
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setUpdating(false);
    }
  }

  return (
    <div className="container py-4">
      <div className="row justify-content-center">
        <div className="col-lg-10">
          {/* Heading */}
          <div className="text-center mb-4">
            <h2 className="fw-bold text-primary">
              <FaUserMd className="me-2" />
              Doctor Dashboard
            </h2>
            <p className="text-muted">
              Welcome to your dashboard. Manage your profile, appointments and
              availability.
            </p>
          </div>

          {/* Doctor Profile Card */}
          <div className="card shadow border-0 rounded-4">
            <div className="card-body p-4">
              <div className="row align-items-center">
                <div className="col-md-2 text-center mb-3 mb-md-0">
                  <div
                    className="rounded-circle bg-primary text-white d-flex justify-content-center align-items-center mx-auto"
                    style={{
                      width: "90px",
                      height: "90px",
                      fontSize: "40px",
                    }}
                  >
                    <FaUserMd />
                  </div>
                </div>

                <div className="col-md-10">
                  <h3 className="fw-bold mb-3">Dr. {user.full_name}</h3>

                  <div className="row">
                    <div className="col-md-6 mb-2">
                      <FaEnvelope className="text-primary me-2" />
                      {user.email}
                    </div>

                    <div className="col-md-6 mb-2">
                      <FaPhone className="text-success me-2" />
                      {user.phone_number}
                    </div>
                  </div>

                  <div className="mt-3">
                    <strong>Account Status : </strong>

                    {isActive ? (
                      <span className="badge bg-success fs-6">
                        <FaCheckCircle className="me-1" />
                        {accountStatus}
                      </span>
                    ) : (
                      <span className="badge bg-danger fs-6">
                        <FaTimesCircle className="me-1" />
                        {accountStatus}
                      </span>
                    )}
                  </div>

                  <div className="mt-4">
                    <button
                      className={`btn ${
                        isActive ? "btn-outline-danger" : "btn-outline-success"
                      } px-4`}
                      disabled={updating}
                      onClick={handleToggleStatus}
                    >
                      {updating
                        ? "Updating..."
                        : isActive
                          ? "Go Inactive"
                          : "Go Active"}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Feature Cards */}
          <div className="row mt-5 g-4">
            <div className="col-md-4">
              <div className="card h-100 shadow-sm border-0 rounded-4 text-center">
                <div className="card-body p-4">
                  <FaCalendarAlt className="text-primary mb-3" size={45} />

                  <h5 className="fw-bold">Availability</h5>

                  <p className="text-muted">
                    Create, update and manage your available slots.
                  </p>

                  <Link
                    to={ROUTES.DOCTOR_AVAILABILITY}
                    className="btn btn-primary w-100"
                  >
                    Manage Availability
                  </Link>
                </div>
              </div>
            </div>

            <div className="col-md-4">
              <div className="card h-100 shadow-sm border-0 rounded-4 text-center">
                <div className="card-body p-4">
                  <FaClipboardList className="text-success mb-3" size={45} />

                  <h5 className="fw-bold">Appointments</h5>

                  <p className="text-muted">
                    View and manage all your booked appointments.
                  </p>

                  <Link
                    to={ROUTES.DOCTOR_APPOINTMENTS}
                    className="btn btn-success w-100"
                  >
                    My Appointments
                  </Link>
                </div>
              </div>
            </div>

            <div className="col-md-4">
              <div className="card h-100 shadow-sm border-0 rounded-4 text-center">
                <div className="card-body p-4">
                  <FaPlaneDeparture className="text-warning mb-3" size={45} />

                  <h5 className="fw-bold">Leave Requests</h5>

                  <p className="text-muted">
                    Submit and track your emergency leave requests.
                  </p>

                  <Link
                    to={ROUTES.DOCTOR_LEAVE_REQUESTS}
                    className="btn btn-warning text-dark w-100"
                  >
                    Leave Requests
                  </Link>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default DoctorDashboardPage;
