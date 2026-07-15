import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { ROUTES } from "../constants/routes";
import { ROLES } from "../constants/roles";
import {
  FaSearch,
  FaCalendarCheck,
  FaCheckCircle,
  FaHeartbeat,
} from "react-icons/fa";

function HomePage() {
  const { isAuthenticated, user } = useAuth();

  return (
    <div>
      {/* hero */}
      <section className="bg-primary bg-gradient text-white py-5">
        <div className="container py-4 text-center">
          <h1 className="fw-bold display-5 mb-3">
            <FaHeartbeat className="me-2" />
            Healthcare, made easy to book
          </h1>
          <p
            className="fs-5 mb-0"
            style={{ maxWidth: "650px", margin: "0 auto" }}
          >
            MediConnect connects patients with verified doctors so you can find
            the right specialist and book an appointment in minutes.
          </p>

          {!isAuthenticated && (
            <div className="d-flex justify-content-center gap-3 mt-4">
              <Link
                to={ROUTES.REGISTER_PATIENT}
                className="btn btn-light btn-lg px-4 fw-bold"
              >
                Book an Appointment
              </Link>
              <Link
                to={ROUTES.REGISTER_DOCTOR}
                className="btn btn-outline-light btn-lg px-4 fw-bold"
              >
                Join as a Doctor
              </Link>
            </div>
          )}

          {isAuthenticated && (
            <div className="mt-4">
              {user.role === ROLES.DOCTOR && (
                <Link
                  to={ROUTES.DOCTOR_DASHBOARD}
                  className="btn btn-light btn-lg px-4 fw-bold"
                >
                  Go to Dashboard
                </Link>
              )}
              {user.role === ROLES.ADMIN && (
                <Link
                  to={ROUTES.ADMIN_DASHBOARD}
                  className="btn btn-light btn-lg px-4 fw-bold"
                >
                  Go to Dashboard
                </Link>
              )}
              {user.role === ROLES.PATIENT && (
                <Link
                  to={ROUTES.DOCTOR_SEARCH}
                  className="btn btn-light btn-lg px-4 fw-bold"
                >
                  Find a Doctor
                </Link>
              )}
            </div>
          )}
        </div>
      </section>

      {/* features */}
      <section className="container py-5">
        <div className="row justify-content-center">
          <div className="col-lg-10">
            <h2 className="fw-bold text-primary text-center mb-2">
              Why patients choose us
            </h2>
            <p className="text-muted text-center mb-4">
              Everything you need to find the right doctor and get seen, without
              the hassle.
            </p>

            <div className="row g-4">
              <div className="col-md-4">
                <div className="card h-100 shadow-sm border-0 rounded-4 text-center">
                  <div className="card-body p-4">
                    <FaSearch className="text-primary mb-3" size={45} />
                    <h5 className="fw-bold">Search by specialty</h5>
                    <p className="text-muted mb-0">
                      Filter doctors by specialization, experience, and
                      consultation fee to find the right match for you.
                    </p>
                  </div>
                </div>
              </div>

              <div className="col-md-4">
                <div className="card h-100 shadow-sm border-0 rounded-4 text-center">
                  <div className="card-body p-4">
                    <FaCalendarCheck className="text-success mb-3" size={45} />
                    <h5 className="fw-bold">Book in a few clicks</h5>
                    <p className="text-muted mb-0">
                      View a doctor's open slots and reserve the one that fits
                      your schedule, no phone calls needed.
                    </p>
                  </div>
                </div>
              </div>

              <div className="col-md-4">
                <div className="card h-100 shadow-sm border-0 rounded-4 text-center">
                  <div className="card-body p-4">
                    <FaCheckCircle className="text-warning mb-3" size={45} />
                    <h5 className="fw-bold">Verified doctors only</h5>
                    <p className="text-muted mb-0">
                      Every doctor on the platform is reviewed and approved by
                      our admin team before they can accept bookings.
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* about */}
      <section className="container pb-5">
        <div className="row justify-content-center">
          <div className="col-lg-10">
            <div className="card shadow border-0 rounded-4">
              <div className="card-body p-4">
                <h2 className="fw-bold text-primary mb-3">About MediConnect</h2>
                <p className="text-muted mb-0">
                  MediConnect is a simple appointment booking platform built for
                  patients and doctors. Patients can search for doctors and book
                  available slots directly. Doctors manage their own
                  availability and appointments, and every doctor application is
                  reviewed by an admin before the doctor can go live on the
                  platform.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}

export default HomePage;
