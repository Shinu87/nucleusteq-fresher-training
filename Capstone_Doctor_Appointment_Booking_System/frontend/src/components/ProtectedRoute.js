// Protects routes by checking user authentication and role permissions.

import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { ROUTES } from "../constants/routes";

function ProtectedRoute({ children, allowedRoles }) {
  const { isAuthenticated, user } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to={ROUTES.HOME} replace />;
  }

  // if this route is restricted to certain roles and user role doesn't match, it will send them home page
  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <Navigate to={ROUTES.HOME} replace />;
  }

  return children;
}

export default ProtectedRoute;
