import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import {
  listDoctorApplications,
  approveDoctor,
  rejectDoctor,
} from "../api/adminApi";
import { handleApiError } from "../utils/handleApiError";
import {
  APPROVAL_STATUS,
  APPROVAL_STATUS_OPTIONS,
} from "../constants/approvalStatus";
import {
  FaUserMd,
  FaEnvelope,
  FaPhone,
  FaIdCard,
  FaMoneyBillWave,
  FaMapMarkerAlt,
  FaCheckCircle,
  FaTimesCircle,
  FaHourglassHalf,
  FaArrowLeft,
} from "react-icons/fa";

function AdminDoctorApprovalsPage() {
  const navigate = useNavigate();
  const [applications, setApplications] = useState([]);
  const [statusFilter, setStatusFilter] = useState(APPROVAL_STATUS.PENDING);
  const [loading, setLoading] = useState(true);
  const [actingId, setActingId] = useState(null);

  useEffect(() => {
    fetchApplications();
  }, [statusFilter]);

  async function fetchApplications() {
    setLoading(true);
    try {
      const response = await listDoctorApplications(statusFilter);
      setApplications(response.data);
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setLoading(false);
    }
  }

  async function handleApprove(doctorProfileId) {
    setActingId(doctorProfileId);
    try {
      await approveDoctor(doctorProfileId);
      toast.success("Doctor approved, setup email sent");
      await fetchApplications();
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setActingId(null);
    }
  }

  async function handleReject(doctorProfileId) {
    const reason = window.prompt("Reason for rejection (optional):", "");
    if (reason === null) return;

    setActingId(doctorProfileId);
    try {
      await rejectDoctor(doctorProfileId, reason);
      toast.success("Doctor application rejected");
      await fetchApplications();
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setActingId(null);
    }
  }

  function renderStatusBadge(status) {
    if (status === APPROVAL_STATUS.PENDING) {
      return (
        <span className="badge bg-warning text-dark fs-6">
          <FaHourglassHalf className="me-1" />
          {status}
        </span>
      );
    }
    if (status === APPROVAL_STATUS.REJECTED) {
      return (
        <span className="badge bg-danger fs-6">
          <FaTimesCircle className="me-1" />
          {status}
        </span>
      );
    }
    return (
      <span className="badge bg-success fs-6">
        <FaCheckCircle className="me-1" />
        {status}
      </span>
    );
  }

  return (
    <div className="container py-4">
      <div className="row justify-content-center">
        <div className="col-lg-11">
          {/* Back Button */}
          <button
            className="btn btn-outline-secondary btn-sm mb-3"
            onClick={() => navigate(-1)}
          >
            <FaArrowLeft className="me-2" />
            Back
          </button>

          {/* Heading */}
          <div className="text-center mb-4">
            <h2 className="fw-bold text-primary">
              <FaUserMd className="me-2" />
              Doctor Approvals
            </h2>
            <p className="text-muted">
              Review pending doctor applications and manage approvals.
            </p>
          </div>

          {/* Filter */}
          <div className="card shadow-sm border-0 rounded-4 mb-4">
            <div className="card-body p-4">
              <label className="form-label fw-bold">Filter by status</label>
              <select
                className="form-select"
                style={{ maxWidth: "250px" }}
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value)}
              >
                {APPROVAL_STATUS_OPTIONS.map((option) => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Loading / Empty States */}
          {loading && (
            <div className="text-center my-5">
              <p className="text-muted">Loading applications...</p>
            </div>
          )}

          {!loading && applications.length === 0 && (
            <div className="text-center my-5">
              <p className="text-muted">No applications found.</p>
            </div>
          )}

          {/* Application Cards */}
          {!loading && applications.length > 0 && (
            <div className="row g-4">
              {applications.map((app) => (
                <div key={app.doctor_profile_id} className="col-md-6 col-lg-4">
                  <div className="card h-100 shadow border-0 rounded-4">
                    <div className="card-body p-4 d-flex flex-column">
                      <div className="text-center mb-3">
                        <div
                          className="rounded-circle bg-primary text-white d-flex justify-content-center align-items-center mx-auto mb-2"
                          style={{
                            width: "70px",
                            height: "70px",
                            fontSize: "30px",
                          }}
                        >
                          <FaUserMd />
                        </div>
                        <h5 className="fw-bold mb-1">Dr. {app.full_name}</h5>
                        {renderStatusBadge(app.approval_status)}
                      </div>

                      <p className="text-muted text-center mb-3">
                        {app.specialization} • {app.qualification} •{" "}
                        {app.experience_years} yrs experience
                      </p>

                      <div className="mb-3">
                        <p className="mb-2">
                          <FaEnvelope className="text-primary me-2" />
                          {app.email}
                        </p>
                        <p className="mb-2">
                          <FaPhone className="text-success me-2" />
                          {app.phone_number}
                        </p>
                        <p className="mb-2">
                          <FaIdCard className="text-primary me-2" />
                          License: {app.license_number}
                        </p>
                        <p className="mb-2">
                          <FaMoneyBillWave className="text-success me-2" />₹
                          {app.consultation_fee}
                        </p>
                        <p className="mb-0">
                          <FaMapMarkerAlt className="text-danger me-2" />
                          {app.clinic_address}
                        </p>
                      </div>

                      {app.approval_status === APPROVAL_STATUS.PENDING && (
                        <div className="mt-auto d-flex gap-2">
                          <button
                            className="btn btn-outline-success w-100"
                            disabled={actingId === app.doctor_profile_id}
                            onClick={() => handleApprove(app.doctor_profile_id)}
                          >
                            {actingId === app.doctor_profile_id
                              ? "..."
                              : "Approve"}
                          </button>
                          <button
                            className="btn btn-outline-danger w-100"
                            disabled={actingId === app.doctor_profile_id}
                            onClick={() => handleReject(app.doctor_profile_id)}
                          >
                            {actingId === app.doctor_profile_id
                              ? "..."
                              : "Reject"}
                          </button>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default AdminDoctorApprovalsPage;
