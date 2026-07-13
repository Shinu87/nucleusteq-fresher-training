import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";
import { getDoctorAppointments } from "../api/appointmentApi";
import { listMySlots } from "../api/availabilityApi";
import { getMyLeaveRequests } from "../api/leaveRequestApi";
import { handleApiError } from "../utils/handleApiError";
import { APPOINTMENT_STATUS } from "../constants/appointmentStatus";
import { SLOT_STATUS } from "../constants/slotStatus";
import { LEAVE_REQUEST_STATUS } from "../constants/leaveRequestStatus";
import { ROUTES } from "../constants/routes";
import {
  FaUserMd,
  FaCalendarAlt,
  FaClipboardList,
  FaPlaneDeparture,
  FaCalendarCheck,
  FaClock,
} from "react-icons/fa";

function todayString() {
  return new Date().toISOString().slice(0, 10);
}

function DoctorDashboardPage() {
  const [appointments, setAppointments] = useState([]);
  const [slots, setSlots] = useState([]);
  const [leaveRequests, setLeaveRequests] = useState([]);
  const [statsLoading, setStatsLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  async function fetchStats() {
    setStatsLoading(true);
    try {
      const [appointmentsRes, slotsRes, leaveRequestsRes] = await Promise.all([
        getDoctorAppointments("", "asc"),
        listMySlots(),
        getMyLeaveRequests(),
      ]);
      setAppointments(appointmentsRes.data);
      setSlots(slotsRes.data);
      setLeaveRequests(leaveRequestsRes.data);
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setStatsLoading(false);
    }
  }

  const today = todayString();

  const todaysAppointmentsCount = appointments.filter(
    (a) =>
      a.appointment_date === today && a.status === APPOINTMENT_STATUS.BOOKED,
  ).length;

  const upcomingAppointmentsCount = appointments.filter(
    (a) => a.appointment_date > today && a.status === APPOINTMENT_STATUS.BOOKED,
  ).length;

  const availableSlotsCount = slots.filter(
    (s) => s.status === SLOT_STATUS.AVAILABLE,
  ).length;

  const pendingLeaveCount = leaveRequests.filter(
    (r) => r.request_status === LEAVE_REQUEST_STATUS.PENDING,
  ).length;

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
              Your appointments, availability and leave requests at a glance.
            </p>
          </div>

          {/* Quick Stats */}
          <div className="row g-4 mb-4">
            <div className="col-6 col-md-3">
              <Link
                to={ROUTES.DOCTOR_APPOINTMENTS}
                className="card h-100 shadow-sm border-0 rounded-4 text-center text-decoration-none"
              >
                <div className="card-body p-4">
                  <FaCalendarCheck className="text-primary mb-3" size={35} />
                  <h3 className="fw-bold mb-0">
                    {statsLoading ? "-" : todaysAppointmentsCount}
                  </h3>
                  <p className="text-muted mb-0">Today's Appointments</p>
                </div>
              </Link>
            </div>

            <div className="col-6 col-md-3">
              <Link
                to={ROUTES.DOCTOR_APPOINTMENTS}
                className="card h-100 shadow-sm border-0 rounded-4 text-center text-decoration-none"
              >
                <div className="card-body p-4">
                  <FaCalendarAlt className="text-primary mb-3" size={35} />
                  <h3 className="fw-bold mb-0">
                    {statsLoading ? "-" : upcomingAppointmentsCount}
                  </h3>
                  <p className="text-muted mb-0">Upcoming Appointments</p>
                </div>
              </Link>
            </div>

            <div className="col-6 col-md-3">
              <Link
                to={ROUTES.DOCTOR_AVAILABILITY}
                className="card h-100 shadow-sm border-0 rounded-4 text-center text-decoration-none"
              >
                <div className="card-body p-4">
                  <FaClock className="text-success mb-3" size={35} />
                  <h3 className="fw-bold mb-0">
                    {statsLoading ? "-" : availableSlotsCount}
                  </h3>
                  <p className="text-muted mb-0">Available Slots</p>
                </div>
              </Link>
            </div>

            <div className="col-6 col-md-3">
              <Link
                to={ROUTES.DOCTOR_LEAVE_REQUESTS}
                className="card h-100 shadow-sm border-0 rounded-4 text-center text-decoration-none"
              >
                <div className="card-body p-4">
                  <FaPlaneDeparture className="text-warning mb-3" size={35} />
                  <h3 className="fw-bold mb-0">
                    {statsLoading ? "-" : pendingLeaveCount}
                  </h3>
                  <p className="text-muted mb-0">Pending Leave Requests</p>
                </div>
              </Link>
            </div>
          </div>

          {/* Feature Cards */}
          <div className="row g-4">
            <div className="col-md-3">
              <div className="card h-100 shadow-sm border-0 rounded-4 text-center">
                <div className="card-body p-4">
                  <FaUserMd className="text-secondary mb-3" size={45} />
                  <h5 className="fw-bold">My Profile</h5>
                  <p className="text-muted">
                    View your details and manage account status.
                  </p>
                  <Link
                    to={ROUTES.PROFILE}
                    className="btn btn-outline-secondary w-100"
                  >
                    View Profile
                  </Link>
                </div>
              </div>
            </div>

            <div className="col-md-3">
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

            <div className="col-md-3">
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

            <div className="col-md-3">
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
