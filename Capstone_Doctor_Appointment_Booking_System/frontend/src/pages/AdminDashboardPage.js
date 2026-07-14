import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { toast } from "react-toastify";
import { getPlatformStats } from "../api/adminApi";
import { handleApiError } from "../utils/handleApiError";
import { ROUTES } from "../constants/routes";
import {
  FaUserShield,
  FaUserMd,
  FaCheckCircle,
  FaUsers,
  FaClipboardList,
  FaCalendarCheck,
  FaTimesCircle,
  FaUserCheck,
  FaPlaneDeparture,
} from "react-icons/fa";

function AdminDashboardPage() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  // load platform stats when the dashboard opens
  useEffect(() => {
    fetchStats();
  }, []);

  async function fetchStats() {
    setLoading(true);
    try {
      const response = await getPlatformStats();
      setStats(response.data);
    } catch (err) {
      toast.error(handleApiError(err));
    } finally {
      setLoading(false);
    }
  }

  const statCards = stats
    ? [
        {
          label: "Total Doctors",
          value: stats.total_doctors,
          icon: <FaUserMd className="text-primary mb-3" size={40} />,
        },
        {
          label: "Active Doctors",
          value: stats.active_doctors,
          icon: <FaUserCheck className="text-success mb-3" size={40} />,
        },
        {
          label: "Total Patients",
          value: stats.total_patients,
          icon: <FaUsers className="text-primary mb-3" size={40} />,
        },
        {
          label: "Total Appointments",
          value: stats.total_appointments,
          icon: <FaClipboardList className="text-success mb-3" size={40} />,
        },
        {
          label: "Completed Appointments",
          value: stats.completed_appointments,
          icon: <FaCheckCircle className="text-success mb-3" size={40} />,
        },
        {
          label: "Cancelled Appointments",
          value: stats.cancelled_appointments,
          icon: <FaTimesCircle className="text-danger mb-3" size={40} />,
        },
      ]
    : [];

  return (
    <div className="container py-4">
      <div className="row justify-content-center">
        <div className="col-lg-10">
          {/* Heading */}
          <div className="text-center mb-4">
            <h2 className="fw-bold text-primary">
              <FaUserShield className="me-2" />
              Admin Dashboard
            </h2>
            <p className="text-muted">
              Welcome to your dashboard. Monitor platform activity and manage
              doctors, patients and leave requests.
            </p>
          </div>

          {/* Loading State */}
          {loading && (
            <div className="text-center my-5">
              <p className="text-muted">Loading stats...</p>
            </div>
          )}

          {/* Stats Cards */}
          {!loading && stats && (
            <div className="row g-4">
              {statCards.map((card) => (
                <div className="col-md-4" key={card.label}>
                  <div className="card h-100 shadow-sm border-0 rounded-4 text-center">
                    <div className="card-body p-4">
                      {card.icon}
                      <h6 className="text-muted mb-1">{card.label}</h6>
                      <h3 className="fw-bold mb-0">{card.value}</h3>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Quick Actions */}
          <div className="row mt-5 g-4">
            <div className="col-md-4">
              <div className="card h-100 shadow-sm border-0 rounded-4 text-center">
                <div className="card-body p-4 d-flex flex-column align-items-center">
                  <FaUserMd className="text-primary mb-3" size={45} />
                  <h5 className="fw-bold">Doctor Approvals</h5>
                  <p className="text-muted">
                    Review and approve pending doctor registrations.
                  </p>
                  <Link
                    to={ROUTES.ADMIN_DOCTOR_APPROVALS}
                    className="btn btn-primary w-100 mt-auto"
                  >
                    Doctor Approvals
                  </Link>
                </div>
              </div>
            </div>

            <div className="col-md-4">
              <div className="card h-100 shadow-sm border-0 rounded-4 text-center">
                <div className="card-body p-4 d-flex flex-column align-items-center">
                  <FaUsers className="text-success mb-3" size={45} />
                  <h5 className="fw-bold">Patients</h5>
                  <p className="text-muted">View all registered patients.</p>
                  <Link
                    to={ROUTES.ADMIN_PATIENTS}
                    className="btn btn-success w-100 mt-auto"
                  >
                    Patients
                  </Link>
                </div>
              </div>
            </div>

            <div className="col-md-4">
              <div className="card h-100 shadow-sm border-0 rounded-4 text-center">
                <div className="card-body p-4 d-flex flex-column align-items-center">
                  <FaPlaneDeparture className="text-warning mb-3" size={45} />
                  <h5 className="fw-bold">Leave Requests</h5>
                  <p className="text-muted">
                    Review doctor emergency leave requests.
                  </p>
                  <Link
                    to={ROUTES.ADMIN_LEAVE_REQUESTS}
                    className="btn btn-warning text-dark w-100 mt-auto"
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

export default AdminDashboardPage;
