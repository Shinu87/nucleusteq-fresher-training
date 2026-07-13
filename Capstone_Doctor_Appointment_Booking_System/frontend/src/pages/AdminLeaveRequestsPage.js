import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import {
  listLeaveRequests,
  approveLeaveRequest,
  rejectLeaveRequest,
} from "../api/adminApi";
import { handleApiError } from "../utils/handleApiError";
import {
  LEAVE_REQUEST_STATUS,
  LEAVE_REQUEST_STATUS_OPTIONS,
} from "../constants/leaveRequestStatus";
import {
  FaPlaneDeparture,
  FaUserMd,
  FaCalendarAlt,
  FaClock,
  FaCommentDots,
  FaCheckCircle,
  FaTimesCircle,
  FaHourglassHalf,
  FaArrowLeft,
} from "react-icons/fa";

function AdminLeaveRequestsPage() {
  const navigate = useNavigate();
  const [requests, setRequests] = useState([]);
  const [statusFilter, setStatusFilter] = useState(
    LEAVE_REQUEST_STATUS.PENDING,
  );
  const [loading, setLoading] = useState(true);
  const [actingId, setActingId] = useState(null);

  useEffect(() => {
    fetchRequests();
  }, [statusFilter]);

  async function fetchRequests() {
    setLoading(true);
    try {
      const response = await listLeaveRequests(statusFilter);
      setRequests(response.data);
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setLoading(false);
    }
  }

  async function handleApprove(leaveRequestId) {
    setActingId(leaveRequestId);
    try {
      await approveLeaveRequest(leaveRequestId);
      toast.success("Leave approved, affected appointments cancelled");
      await fetchRequests();
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setActingId(null);
    }
  }

  async function handleReject(leaveRequestId) {
    const reason = window.prompt("Reason for rejection (required):", "");
    if (reason === null) return;
    if (!reason.trim()) {
      toast.error("A rejection reason is required");
      return;
    }

    setActingId(leaveRequestId);
    try {
      await rejectLeaveRequest(leaveRequestId, reason);
      toast.success("Leave request rejected");
      await fetchRequests();
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setActingId(null);
    }
  }

  function renderStatusBadge(status) {
    if (status === LEAVE_REQUEST_STATUS.PENDING) {
      return (
        <span className="badge bg-warning text-dark fs-6">
          <FaHourglassHalf className="me-1" />
          {status}
        </span>
      );
    }
    if (status === LEAVE_REQUEST_STATUS.REJECTED) {
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
              <FaPlaneDeparture className="me-2" />
              Leave Requests
            </h2>
            <p className="text-muted">
              Review doctor emergency leave requests and manage approvals.
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
                {LEAVE_REQUEST_STATUS_OPTIONS.map((option) => (
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
              <p className="text-muted">Loading requests...</p>
            </div>
          )}

          {!loading && requests.length === 0 && (
            <div className="text-center my-5">
              <p className="text-muted">No leave requests found.</p>
            </div>
          )}

          {/* Leave Request Cards */}
          {!loading && requests.length > 0 && (
            <div className="row g-4">
              {requests.map((req) => (
                <div key={req.id} className="col-md-6 col-lg-4">
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
                        <h6 className="text-muted mb-1">
                          Doctor ID: {req.doctor_id}
                        </h6>
                        {renderStatusBadge(req.request_status)}
                      </div>

                      <div className="mb-3">
                        <p className="mb-2">
                          <FaCalendarAlt className="text-primary me-2" />
                          {req.date}
                        </p>
                        <p className="mb-2">
                          <FaClock className="text-success me-2" />
                          {req.start_time} - {req.end_time}
                        </p>
                        <p className="mb-2">
                          <FaCommentDots className="text-primary me-2" />
                          {req.reason}
                        </p>
                        {req.request_status === LEAVE_REQUEST_STATUS.REJECTED &&
                          req.rejection_reason && (
                            <p className="mb-0 text-danger">
                              <FaTimesCircle className="me-2" />
                              Rejected: {req.rejection_reason}
                            </p>
                          )}
                      </div>

                      {req.request_status === LEAVE_REQUEST_STATUS.PENDING && (
                        <div className="mt-auto d-flex gap-2">
                          <button
                            className="btn btn-outline-success w-100"
                            disabled={actingId === req.id}
                            onClick={() => handleApprove(req.id)}
                          >
                            {actingId === req.id ? "..." : "Approve"}
                          </button>
                          <button
                            className="btn btn-outline-danger w-100"
                            disabled={actingId === req.id}
                            onClick={() => handleReject(req.id)}
                          >
                            {actingId === req.id ? "..." : "Reject"}
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

export default AdminLeaveRequestsPage;
