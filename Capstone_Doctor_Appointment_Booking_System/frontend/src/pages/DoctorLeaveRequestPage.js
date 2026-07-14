import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "react-toastify";
import { requestLeave, getMyLeaveRequests } from "../api/leaveRequestApi";
import { handleApiError } from "../utils/handleApiError";
import { LEAVE_REQUEST_STATUS } from "../constants/leaveRequestStatus";
import {
  leaveDateRules,
  startTimeRules,
  endTimeRules,
  reasonRules,
} from "../validations/leaveRequestValidation";
import {
  FaCalendarAlt,
  FaClock,
  FaClipboardList,
  FaPaperPlane,
  FaInfoCircle,
  FaTimesCircle,
} from "react-icons/fa";

function DoctorLeaveRequestPage() {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm();
  const [submitting, setSubmitting] = useState(false);
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchRequests();
  }, []);

  async function fetchRequests() {
    setLoading(true);
    try {
      const response = await getMyLeaveRequests();
      setRequests(response.data);
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setLoading(false);
    }
  }

  // submit a new emergency leave request for admin review
  async function onSubmit(data) {
    setSubmitting(true);
    try {
      await requestLeave(data);
      toast.success("Leave request submitted");
      reset();
      await fetchRequests();
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setSubmitting(false);
    }
  }

  function statusClass(status) {
    if (status === LEAVE_REQUEST_STATUS.APPROVED) return "text-success";
    if (status === LEAVE_REQUEST_STATUS.REJECTED) return "text-danger";
    return "text-muted";
  }

  return (
    <div className="container py-4">
      {/* Heading */}
      <div className="text-center mb-4">
        <h2 className="fw-bold text-primary">
          <FaClipboardList className="me-2" />
          Leave Requests
        </h2>
        <p className="text-muted">
          Submit emergency leave requests and track their approval status.
        </p>
      </div>

      {/* Leave Form */}
      <div className="card shadow border-0 rounded-4 mb-5">
        <div className="card-body p-4">
          <h4 className="fw-bold mb-2">
            <FaPaperPlane className="me-2 text-primary" />
            Request Emergency Leave
          </h4>

          <div className="alert alert-warning">
            <FaInfoCircle className="me-2" />
            If approved, all appointments during this period will be cancelled
            automatically and patients will be notified.
          </div>

          <form className="row g-3" onSubmit={handleSubmit(onSubmit)}>
            <div className="col-lg-3 col-md-6">
              <label className="form-label fw-semibold">Date</label>

              <input
                type="date"
                className="form-control"
                {...register("date", leaveDateRules)}
              />

              {errors.date && (
                <div className="text-danger small mt-1">
                  {errors.date.message}
                </div>
              )}
            </div>

            <div className="col-lg-2 col-md-6">
              <label className="form-label fw-semibold">Start Time</label>

              <input
                type="time"
                className="form-control"
                {...register("start_time", startTimeRules)}
              />

              {errors.start_time && (
                <div className="text-danger small mt-1">
                  {errors.start_time.message}
                </div>
              )}
            </div>

            <div className="col-lg-2 col-md-6">
              <label className="form-label fw-semibold">End Time</label>

              <input
                type="time"
                className="form-control"
                {...register("end_time", endTimeRules)}
              />

              {errors.end_time && (
                <div className="text-danger small mt-1">
                  {errors.end_time.message}
                </div>
              )}
            </div>

            <div className="col-lg-3 col-md-6">
              <label className="form-label fw-semibold">Reason</label>

              <input
                type="text"
                className="form-control"
                placeholder="Enter reason"
                {...register("reason", reasonRules)}
              />

              {errors.reason && (
                <div className="text-danger small mt-1">
                  {errors.reason.message}
                </div>
              )}
            </div>

            <div className="col-lg-2 d-grid">
              <button className="btn btn-primary mt-lg-4" disabled={submitting}>
                {submitting ? "Submitting..." : "Submit Request"}
              </button>
            </div>
          </form>
        </div>
      </div>

      {/* My Requests */}
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h3 className="fw-bold">My Requests</h3>

        {!loading && (
          <span className="badge bg-primary fs-6">
            {requests.length} Request(s)
          </span>
        )}
      </div>

      {loading && (
        <div className="text-center py-5">
          <div className="spinner-border text-primary"></div>
          <p className="mt-3">Loading requests...</p>
        </div>
      )}

      {!loading && requests.length === 0 && (
        <div className="card shadow-sm border-0 rounded-4">
          <div className="card-body text-center py-5">
            <FaCalendarAlt size={45} className="text-secondary mb-3" />
            <h5>No Leave Requests</h5>
            <p className="text-muted">
              You haven't submitted any leave requests yet.
            </p>
          </div>
        </div>
      )}

      {!loading &&
        requests.map((req) => (
          <div key={req.id} className="card shadow-sm border-0 rounded-4 mb-3">
            <div className="card-body">
              <div className="d-flex justify-content-between align-items-center flex-wrap">
                <div>
                  <h5 className="fw-bold mb-2">
                    <FaCalendarAlt className="me-2 text-primary" />
                    {req.date}
                  </h5>

                  <p className="mb-2 text-muted">
                    <FaClock className="me-2" />
                    {req.start_time} - {req.end_time}
                  </p>

                  <p className="mb-2">
                    <strong>Reason:</strong> {req.reason}
                  </p>
                </div>

                <div>
                  {req.request_status === LEAVE_REQUEST_STATUS.APPROVED && (
                    <span className="badge bg-success fs-6">Approved</span>
                  )}

                  {req.request_status === LEAVE_REQUEST_STATUS.PENDING && (
                    <span className="badge bg-warning text-dark fs-6">
                      Pending
                    </span>
                  )}

                  {req.request_status === LEAVE_REQUEST_STATUS.REJECTED && (
                    <span className="badge bg-danger fs-6">Rejected</span>
                  )}
                </div>
              </div>

              {req.request_status === LEAVE_REQUEST_STATUS.REJECTED &&
                req.rejection_reason && (
                  <div className="alert alert-danger mt-3 mb-0">
                    <FaTimesCircle className="me-2" />
                    <strong>Rejection Reason:</strong> {req.rejection_reason}
                  </div>
                )}
            </div>
          </div>
        ))}
    </div>
  );
}

export default DoctorLeaveRequestPage;
