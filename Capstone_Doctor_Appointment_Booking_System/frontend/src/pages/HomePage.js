import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { ROUTES } from "../constants/routes";
import { ROLES } from "../constants/roles";

function HomePage() {
  const { isAuthenticated, user } = useAuth();

  return (
    <div>
      <section className="hero-section">
        <div className="page-container text-center">
          <h1 className="hero-title">Healthcare, made easy to book</h1>
          <p className="hero-subtitle">
            MediConnect connects patients with verified doctors so you can find
            the right specialist and book an appointment in minutes.
          </p>

          {!isAuthenticated && (
            <div className="d-flex justify-content-center gap-3 mt-4">
              <Link
                to={ROUTES.REGISTER_PATIENT}
                className="btn btn-primary btn-lg px-4"
              >
                Book an Appointment
              </Link>
              <Link
                to={ROUTES.REGISTER_DOCTOR}
                className="btn btn-outline-success btn-lg px-4"
              >
                Join as a Doctor
              </Link>
            </div>
          )}

          {isAuthenticated && (
            <div className="mt-4">
              {user.role === ROLES.DOCTOR ? (
                <Link
                  to={ROUTES.DOCTOR_DASHBOARD}
                  className="btn btn-primary btn-lg px-4"
                >
                  Go to Dashboard
                </Link>
              ) : (
                <Link
                  to={ROUTES.DOCTOR_SEARCH}
                  className="btn btn-primary btn-lg px-4"
                >
                  Find a Doctor
                </Link>
              )}
            </div>
          )}
        </div>
      </section>

      <section className="page-container">
        <h2 className="section-title text-center">Why patients choose us</h2>
        <div className="row g-4 mt-2">
          <div className="col-md-4">
            <div className="feature-card">
              <div className="feature-icon">🔍</div>
              <h5>Search by specialty</h5>
              <p className="text-muted mb-0">
                Filter doctors by specialization, experience, and consultation
                fee to find the right match for you.
              </p>
            </div>
          </div>

          <div className="col-md-4">
            <div className="feature-card">
              <div className="feature-icon">📅</div>
              <h5>Book in a few clicks</h5>
              <p className="text-muted mb-0">
                View a doctor's open slots and reserve the one that fits your
                schedule, no phone calls needed.
              </p>
            </div>
          </div>

          <div className="col-md-4">
            <div className="feature-card">
              <div className="feature-icon">✅</div>
              <h5>Verified doctors only</h5>
              <p className="text-muted mb-0">
                Every doctor on the platform is reviewed and approved by our
                admin team before they can accept bookings.
              </p>
            </div>
          </div>
        </div>
      </section>

      <section className="page-container">
        <div className="about-card">
          <h2 className="section-title">About MediConnect</h2>
          <p className="text-muted mb-0">
            MediConnect is a simple appointment booking platform built for
            patients and doctors. Patients can search for doctors and book
            available slots directly. Doctors manage their own availability and
            appointments, and every doctor application is reviewed by an admin
            before the doctor can go live on the platform.
          </p>
        </div>
      </section>
    </div>
  );
}

export default HomePage;
