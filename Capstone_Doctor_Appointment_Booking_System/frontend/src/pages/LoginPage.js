import { useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate, Link } from "react-router-dom";
import { toast } from "react-toastify";
import { login as loginApi } from "../api/authApi";
import { handleApiError } from "../utils/handleApiError";
import { useAuth } from "../context/AuthContext";
import { ROUTES } from "../constants/routes";
import { emailRules } from "../validations/authValidation";
import "bootstrap-icons/font/bootstrap-icons.css";
function LoginPage() {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm();
  const [submitting, setSubmitting] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  // role specific redirects
  async function onSubmit(data) {
    setSubmitting(true);
    try {
      const response = await loginApi(data);
      login(response.data.access_token, response.data.user);
      toast.success("Login successful");
      navigate(ROUTES.HOME);
    } catch (error) {
      toast.error(handleApiError(error));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="page-container">
      <div className="auth-card">
        <h3 className="section-title">
          <div className="text-center mb-4">
            <h2 className="auth-title">Welcome Back</h2>
            <p className="auth-subtitle">
              Sign in to access your Doctor Appointment Dashboard
            </p>
          </div>
        </h3>
        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="mb-3">
            <label className="form-label">Email</label>
            <input
              className="form-control"
              {...register("email", emailRules)}
            />
            {errors.email && (
              <div className="field-error">{errors.email.message}</div>
            )}
          </div>

          <div className="mb-3">
            <label className="form-label">Password</label>

            <div className="input-group">
              <input
                type={showPassword ? "text" : "password"}
                className="form-control"
                {...register("password", {
                  required: "Password is required",
                })}
              />

              <button
                type="button"
                className="btn btn-outline-secondary"
                onClick={() => setShowPassword(!showPassword)}
                tabIndex={-1}
              >
                <i
                  className={`bi ${showPassword ? "bi-eye-slash" : "bi-eye"}`}
                ></i>
              </button>
            </div>

            {errors.password && (
              <div className="field-error">{errors.password.message}</div>
            )}
          </div>

          <button
            type="submit"
            className="btn btn-primary w-100"
            disabled={submitting}
          >
            {submitting ? "Logging in..." : "Login"}
          </button>
        </form>
        <p className="mt-3 mb-0">
          New patient? <Link to={ROUTES.REGISTER_PATIENT}>Register here</Link>
        </p>
        <p className="mb-0">
          Are you a doctor? <Link to={ROUTES.REGISTER_DOCTOR}>Apply here</Link>
        </p>
      </div>
    </div>
  );
}

export default LoginPage;
