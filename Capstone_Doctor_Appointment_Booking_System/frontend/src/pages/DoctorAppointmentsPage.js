import { useEffect, useMemo, useState } from "react";
import { toast } from "react-toastify";
import {
  getDoctorAppointments,
  completeAppointment,
  markNoShow,
} from "../api/appointmentApi";
import { handleApiError } from "../utils/handleApiError";
import {
  APPOINTMENT_STATUS,
  APPOINTMENT_STATUS_OPTIONS,
} from "../constants/appointmentStatus";
import {
  FaUserInjured,
  FaCalendarAlt,
  FaClock,
  FaMoneyBillWave,
  FaCheckCircle,
  FaTimesCircle,
  FaClipboardList,
  FaFilter,
  FaCalendarDay,
  FaCalendarWeek,
  FaBan,
} from "react-icons/fa";

//  date filter helpers

function toDateInputValue(date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

function getTodayDateString() {
  return toDateInputValue(new Date());
}

const DATE_FILTERS = {
  ALL: "all",
  TODAY: "today",
  UPCOMING: "upcoming",
  CUSTOM: "custom",
};

const STATUS_THEME = {
  [APPOINTMENT_STATUS.BOOKED]: {
    bg: "#eaf4fb",
    border: "#0d6efd",
    badgeClass: "bg-primary",
    label: "Booked",
  },
  [APPOINTMENT_STATUS.COMPLETED]: {
    bg: "#eaf7f0",
    border: "#198754",
    badgeClass: "bg-success",
    label: "Completed",
  },
  [APPOINTMENT_STATUS.NO_SHOW]: {
    bg: "#f6eef2",
    border: "#a8557a",
    badgeClass: "bg-danger",
    label: "No Show",
  },
  [APPOINTMENT_STATUS.CANCELLED]: {
    bg: "#f2f2f2",
    border: "#6c757d",
    badgeClass: "bg-secondary",
    label: "Cancelled",
  },
};

const DEFAULT_THEME = {
  bg: "#fdf2e3",
  border: "#ffc107",
  badgeClass: "bg-warning text-dark",
  label: null,
};

function DoctorAppointmentsPage() {
  const [appointments, setAppointments] = useState([]);
  const [statusFilter, setStatusFilter] = useState("");
  const [sort, setSort] = useState("asc");
  const [loading, setLoading] = useState(true);
  const [actingId, setActingId] = useState(null);

  //  date filter state
  const [dateMode, setDateMode] = useState(DATE_FILTERS.ALL);
  const [selectedDate, setSelectedDate] = useState(getTodayDateString());

  useEffect(() => {
    fetchAppointments();
  }, [statusFilter, sort]);

  async function fetchAppointments() {
    setLoading(true);
    try {
      const response = await getDoctorAppointments(statusFilter, sort);
      setAppointments(response.data);
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setLoading(false);
    }
  }

  // the appointment time must already be in the past to complete/no-show it
  function isPast(appointment) {
    const appointmentEnd = new Date(
      `${appointment.appointment_date}T${appointment.end_time}`,
    );
    return appointmentEnd < new Date();
  }

  async function handleComplete(appointmentId) {
    setActingId(appointmentId);
    try {
      await completeAppointment(appointmentId);
      toast.success("Marked as completed");
      await fetchAppointments();
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setActingId(null);
    }
  }

  async function handleNoShow(appointmentId) {
    setActingId(appointmentId);
    try {
      await markNoShow(appointmentId);
      toast.success("Marked as no-show");
      await fetchAppointments();
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setActingId(null);
    }
  }

  //  date filter handlers
  function handleQuickDateFilter(mode) {
    setDateMode(mode);
    if (mode === DATE_FILTERS.TODAY) {
      setSelectedDate(getTodayDateString());
    }
  }

  function handleDatePickerChange(value) {
    setSelectedDate(value);
    setDateMode(DATE_FILTERS.CUSTOM);
  }

  //  derive the date-filtered list
  const filteredAppointments = useMemo(() => {
    if (dateMode === DATE_FILTERS.ALL) return appointments;

    if (dateMode === DATE_FILTERS.UPCOMING) {
      const today = getTodayDateString();
      return appointments.filter((appt) => appt.appointment_date >= today);
    }

    return appointments.filter(
      (appt) => appt.appointment_date === selectedDate,
    );
  }, [appointments, dateMode, selectedDate]);

  return (
    <div className="container py-4">
      {/* Header */}
      <div className="text-center mb-4">
        <h2 className="fw-bold text-primary">
          <FaClipboardList className="me-2" />
          My Appointments
        </h2>
        <p className="text-muted">
          View and manage all your patient appointments.
        </p>
      </div>

      {/* Filters */}
      <div className="card shadow-sm border-0 rounded-4 mb-4">
        <div className="card-body">
          <h5 className="fw-bold mb-3">
            <FaFilter className="me-2 text-primary" />
            Filter Appointments
          </h5>

          <div className="row">
            <div className="col-md-4 mb-3">
              <label className="form-label fw-semibold">Status</label>

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

            <div className="col-md-4 mb-3">
              <label className="form-label fw-semibold">Sort by Date</label>

              <select
                className="form-select"
                value={sort}
                onChange={(e) => setSort(e.target.value)}
              >
                <option value="asc">Oldest First</option>

                <option value="desc">Newest First</option>
              </select>
            </div>

            <div className="col-md-4 mb-3">
              <label className="form-label fw-semibold">Pick a Date</label>

              <input
                type="date"
                className="form-control"
                value={selectedDate}
                onChange={(e) => handleDatePickerChange(e.target.value)}
              />
            </div>
          </div>

          <div className="row align-items-center mt-1">
            <div className="col-md-8 mb-3 mb-md-0">
              <div
                className="btn-group"
                role="group"
                aria-label="Quick date filters"
              >
                <button
                  type="button"
                  className={`btn btn-sm ${
                    dateMode === DATE_FILTERS.TODAY
                      ? "btn-primary"
                      : "btn-outline-primary"
                  }`}
                  onClick={() => handleQuickDateFilter(DATE_FILTERS.TODAY)}
                >
                  <FaCalendarDay className="me-2" />
                  Today
                </button>

                <button
                  type="button"
                  className={`btn btn-sm ${
                    dateMode === DATE_FILTERS.UPCOMING
                      ? "btn-primary"
                      : "btn-outline-primary"
                  }`}
                  onClick={() => handleQuickDateFilter(DATE_FILTERS.UPCOMING)}
                >
                  <FaCalendarWeek className="me-2" />
                  Upcoming
                </button>

                <button
                  type="button"
                  className={`btn btn-sm ${
                    dateMode === DATE_FILTERS.ALL
                      ? "btn-primary"
                      : "btn-outline-primary"
                  }`}
                  onClick={() => handleQuickDateFilter(DATE_FILTERS.ALL)}
                >
                  All Dates
                </button>
              </div>
            </div>

            <div className="col-md-4 text-md-end">
              <span className="badge bg-primary fs-6">
                Showing: {filteredAppointments.length} / {appointments.length}
              </span>
            </div>
          </div>
        </div>
      </div>

      {/* Loading */}

      {loading && (
        <div className="text-center py-5">
          <div className="spinner-border text-primary"></div>
          <p className="mt-3">Loading appointments...</p>
        </div>
      )}

      {/* Empty */}

      {!loading && appointments.length === 0 && (
        <div className="card shadow-sm border-0 rounded-4">
          <div className="card-body text-center py-5">
            <FaClipboardList size={50} className="text-secondary mb-3" />

            <h4>No Appointments Found</h4>

            <p className="text-muted">
              There are no appointments matching the selected filter.
            </p>
          </div>
        </div>
      )}

      {/* No results after date filtering */}

      {!loading &&
        appointments.length > 0 &&
        filteredAppointments.length === 0 && (
          <div className="card shadow-sm border-0 rounded-4">
            <div className="card-body text-center py-5">
              <FaCalendarAlt size={50} className="text-secondary mb-3" />

              <h4>No Appointments On This Date</h4>

              <p className="text-muted">
                Try a different date or switch to "All Dates".
              </p>
            </div>
          </div>
        )}

      {/* Appointment Cards */}

      {!loading && filteredAppointments.length > 0 && (
        <div className="row g-4">
          {filteredAppointments.map((appt) => {
            const canAct =
              appt.status === APPOINTMENT_STATUS.BOOKED && isPast(appt);

            const theme = STATUS_THEME[appt.status] || DEFAULT_THEME;
            const badgeLabel = theme.label || appt.status;

            return (
              <div key={appt.id} className="col-lg-6 col-xl-4">
                <div
                  className="card shadow-sm border-0 rounded-4 h-100"
                  style={{
                    backgroundColor: theme.bg,
                    borderLeft: `5px solid ${theme.border}`,
                  }}
                >
                  <div className="card-body d-flex flex-column">
                    <div className="d-flex align-items-center mb-3">
                      <div
                        className="rounded-circle bg-white d-inline-flex align-items-center justify-content-center me-3 shadow-sm flex-shrink-0"
                        style={{
                          width: 52,
                          height: 52,
                          fontSize: 22,
                          color: theme.border,
                        }}
                      >
                        <FaUserInjured />
                      </div>
                      <div>
                        <h5 className="fw-bold mb-1">{appt.patient_name}</h5>
                        <span
                          className={`badge ${theme.badgeClass}`}
                          style={{ fontSize: 12 }}
                        >
                          {badgeLabel}
                        </span>
                      </div>
                    </div>

                    <div className="mb-1">
                      <FaCalendarAlt className="text-primary me-2" />
                      <strong>Date:</strong> {appt.appointment_date}
                    </div>

                    <div className="mb-1">
                      <FaClock className="text-success me-2" />
                      <strong>Time:</strong> {appt.start_time} - {appt.end_time}
                    </div>

                    <div className="mb-3">
                      <FaMoneyBillWave className="text-warning me-2" />
                      <strong>Consultation Fee:</strong> ₹
                      {appt.consultation_fee}
                    </div>

                    <div className="mt-auto">
                      {canAct ? (
                        <div className="d-grid gap-2">
                          <button
                            className="btn btn-success"
                            disabled={actingId === appt.id}
                            onClick={() => handleComplete(appt.id)}
                          >
                            <FaCheckCircle className="me-2" />
                            {actingId === appt.id
                              ? "Processing..."
                              : "Complete Appointment"}
                          </button>

                          <button
                            className="btn btn-outline-danger"
                            disabled={actingId === appt.id}
                            onClick={() => handleNoShow(appt.id)}
                          >
                            <FaTimesCircle className="me-2" />
                            {actingId === appt.id
                              ? "Processing..."
                              : "Did Not Attend"}
                          </button>
                        </div>
                      ) : (
                        <div className="alert alert-light border py-2 px-3 mb-0 d-inline-flex align-items-center small">
                          <FaBan className="me-2 text-secondary" />
                          No actions available
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}

export default DoctorAppointmentsPage;
