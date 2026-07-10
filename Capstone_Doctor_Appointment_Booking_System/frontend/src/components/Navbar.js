import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { ROUTES } from "../constants/routes";
import { ROLES } from "../constants/roles";

function Navbar() {
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();

  // navbar links per role
  function handleLogout() {
    logout();
    navigate(ROUTES.LOGIN);
  }

  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-white app-navbar">
      <div className="container">
        <Link className="navbar-brand fw-bold" to={ROUTES.HOME}>
          🩺 MediConnect
        </Link>
        <div className="d-flex align-items-center gap-2">
          {isAuthenticated && user.role === ROLES.PATIENT && (
            <Link
              className="btn btn-outline-secondary btn-sm"
              to={ROUTES.DOCTOR_SEARCH}
            >
              Find Doctors
            </Link>
          )}
          {isAuthenticated ? (
            <div className="d-flex align-items-center gap-3">
              <span className="user-greeting">
                👋 Hi, <strong>{user.full_name}</strong>
              </span>

              <button
                className="btn btn-outline-danger btn-sm px-3"
                onClick={handleLogout}
              >
                Logout
              </button>
            </div>
          ) : (
            <div className="d-flex align-items-center gap-2">
              <Link
                className="btn btn-outline-success btn-sm px-3"
                to={ROUTES.LOGIN}
              >
                Login
              </Link>

              <Link
                className="btn btn-success btn-sm px-3"
                to={ROUTES.REGISTER_PATIENT}
              >
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
