import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { ROUTES } from "../constants/routes";
import { ROLES } from "../constants/roles";
import {
  FaStethoscope,
  FaSearch,
  FaClipboardList,
  FaTachometerAlt,
  FaUserCircle,
  FaSignOutAlt,
  FaSignInAlt,
  FaUserPlus,
} from "react-icons/fa";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle.min.js";

function Navbar() {
  const { isAuthenticated, user, logout } = useAuth();

  // navbar links per role
  function handleLogout() {
    logout();
    // full page reload so no stale cached data
    window.location.href = ROUTES.HOME;
  }

  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-white app-navbar shadow-sm">
      <div className="container">
        <Link
          className="navbar-brand fw-bold d-flex align-items-center text-primary"
          to={ROUTES.HOME}
        >
          <FaStethoscope className="me-2" />
          MediConnect
        </Link>

        <div className="d-flex align-items-center gap-2">
          {isAuthenticated && user.role === ROLES.PATIENT && (
            <>
              <Link
                className="btn btn-outline-primary btn-sm rounded-pill px-3"
                to={ROUTES.DOCTOR_SEARCH}
              >
                <FaSearch className="me-2" />
                Find Doctors
              </Link>
              <Link
                className="btn btn-outline-primary btn-sm rounded-pill px-3"
                to={ROUTES.MY_APPOINTMENTS}
              >
                <FaClipboardList className="me-2" />
                My Appointments
              </Link>
            </>
          )}

          {isAuthenticated && user.role === ROLES.DOCTOR && (
            <Link
              className="btn btn-outline-primary btn-sm rounded-pill px-3"
              to={ROUTES.DOCTOR_DASHBOARD}
            >
              <FaTachometerAlt className="me-2" />
              Dashboard
            </Link>
          )}

          {isAuthenticated ? (
            <div className="dropdown">
              <button
                className="btn bg-primary-subtle text-primary border border-primary-subtle btn-sm dropdown-toggle rounded-pill px-3"
                type="button"
                data-bs-toggle="dropdown"
                aria-expanded="false"
              >
                <FaUserCircle className="me-2" />
                {user.full_name}
              </button>

              <ul className="dropdown-menu dropdown-menu-end shadow-sm border-0 rounded-4 mt-2">
                <li>
                  <span className="dropdown-item-text text-muted small">
                    {user.email}
                  </span>
                </li>

                <li>
                  <hr className="dropdown-divider" />
                </li>

                <li>
                  <button
                    className="dropdown-item text-danger"
                    onClick={handleLogout}
                  >
                    <FaSignOutAlt className="me-2" />
                    Logout
                  </button>
                </li>
              </ul>
            </div>
          ) : (
            <div className="d-flex align-items-center gap-2">
              <Link
                className="btn btn-outline-success btn-sm rounded-pill px-3"
                to={ROUTES.LOGIN}
              >
                <FaSignInAlt className="me-2" />
                Login
              </Link>

              <Link
                className="btn btn-success btn-sm rounded-pill px-3"
                to={ROUTES.REGISTER_PATIENT}
              >
                <FaUserPlus className="me-2" />
                Register
              </Link>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
