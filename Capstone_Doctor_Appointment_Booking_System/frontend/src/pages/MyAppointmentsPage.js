// Displays the patients appointments and allows filtering or cancellation of booked appointments.

import { useEffect, useState } from "react";
import { toast } from "react-toastify";
import { getMyAppointments, cancelAppointment } from "../api/appointmentApi";
import { handleApiError } from "../utils/handleApiError";
import {
  APPOINTMENT_STATUS,
  APPOINTMENT_STATUS_OPTIONS,
} from "../constants/appointmentStatus";

function MyAppointmentsPage() {
  const [appointments, setAppointments] = useState([]);
  const [statusFilter, setStatusFilter] = useState("");
  const [loading, setLoading] = useState(true);
  const [cancellingId, setCancellingId] = useState(null);

  // reload the list whenever the filter changes
  useEffect(() => {
    fetchAppointments();
  }, [statusFilter]);

  async function fetchAppointments() {
    setLoading(true);
    try {
      const response = await getMyAppointments(statusFilter);
      setAppointments(response.data);
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setLoading(false);
    }
  }

  async function handleCancel(appointmentId) {
    setCancellingId(appointmentId);
    try {
      await cancelAppointment(appointmentId);
      toast.success("Appointment cancelled");
      await fetchAppointments();
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setCancellingId(null);
    }
  }

  return (
    <div className="page-container">
      <div className="card border-0 shadow-sm rounded-4 mb-4">
        <div className="card-body d-flex justify-content-between align-items-center py-3">
          <div>
            <h4 className="fw-bold mb-1">
              <i className="bi bi-calendar2-check text-primary me-2"></i>
              My Appointments
            </h4>
            <small className="text-muted">
              Track and manage your scheduled consultations.
            </small>
          </div>

          <span className="badge bg-primary rounded-pill fs-6 px-3 py-2">
            {appointments.length} Appointment
            {appointments.length !== 1 ? "s" : ""}
          </span>
        </div>
      </div>
      <div className="mb-4" style={{ maxWidth: "220px" }}>
        <label className="form-label">Filter by status</label>
        <select
          className="form-select"
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value)}
        >
          {APPOINTMENT_STATUS_OPTIONS.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
      </div>

      {loading && <p>Loading appointments...</p>}

      {!loading && appointments.length === 0 && (
        <p>You don't have any appointments yet.</p>
      )}

      <div className="row">
        {!loading &&
          appointments.map((appt) => (
            <div key={appt.id} className="col-12 col-md-6 col-lg-4 mb-4">
              <div className="card shadow-sm border-0 rounded-4 h-100">
                <div className="card-body p-4">
                  {/* Header */}
                  <div className="d-flex justify-content-between align-items-start mb-3">
                    <div>
                      <h5 className="fw-bold mb-1">
                        <i className="bi bi-person-circle text-primary me-2"></i>
                        Dr. {appt.doctor_name}
                      </h5>
                      <small className="text-muted">
                        Scheduled Consultation
                      </small>
                    </div>

                    <span
                      className={`badge rounded-pill px-3 py-2 ${
                        appt.status === APPOINTMENT_STATUS.BOOKED
                          ? "bg-success"
                          : appt.status === APPOINTMENT_STATUS.CANCELLED
                            ? "bg-danger"
                            : "bg-secondary"
                      }`}
                    >
                      {appt.status === "NO_SHOW"
                        ? "Did Not Attend"
                        : appt.status}{" "}
                    </span>
                  </div>

                  <hr />

                  {/* Appointment Details */}
                  <div className="mb-3">
                    <div className="d-flex align-items-center mb-3">
                      <i className="bi bi-calendar-event text-primary me-3 fs-5"></i>
                      <div>
                        <small className="text-muted d-block">Date</small>
                        <span>{appt.appointment_date}</span>
                      </div>
                    </div>

                    <div className="d-flex align-items-center mb-3">
                      <i className="bi bi-clock text-primary me-3 fs-5"></i>
                      <div>
                        <small className="text-muted d-block">Time</small>
                        <span>
                          {appt.start_time} - {appt.end_time}
                        </span>
                      </div>
                    </div>

                    <div className="d-flex align-items-center">
                      <i className="bi bi-cash-coin text-success me-3 fs-5"></i>
                      <div>
                        <small className="text-muted d-block">
                          Consultation Fee
                        </small>
                        <span className="fw-semibold">
                          ₹{appt.consultation_fee}
                        </span>
                      </div>
                    </div>
                  </div>

                  {/* Cancel Button */}
                  {appt.status === APPOINTMENT_STATUS.BOOKED && (
                    <button
                      className="btn btn-outline-danger rounded-pill w-100"
                      disabled={cancellingId === appt.id}
                      onClick={() => handleCancel(appt.id)}
                    >
                      <i className="bi bi-x-circle me-2"></i>
                      {cancellingId === appt.id
                        ? "Cancelling..."
                        : "Cancel Appointment"}
                    </button>
                  )}
                </div>
              </div>
            </div>
          ))}
      </div>
    </div>
  );
}

export default MyAppointmentsPage;
