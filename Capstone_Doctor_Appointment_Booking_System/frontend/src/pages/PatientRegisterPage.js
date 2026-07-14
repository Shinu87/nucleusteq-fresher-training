import { useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate, Link } from "react-router-dom";
import { toast } from "react-toastify";
import { registerPatient } from "../api/authApi";
import { handleApiError } from "../utils/handleApiError";
import { GENDERS } from "../constants/genders";
import { ROUTES } from "../constants/routes";
import {
  fullNameRules,
  emailRules,
  phoneRules,
  passwordRules,
  genderRules,
  dobRules,
} from "../validations/authValidation";

function PatientRegisterPage() {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm();
  const [submitting, setSubmitting] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();

  // send the form data to backend and redirect to login on success
  async function onSubmit(data) {
    setSubmitting(true);
    try {
      await registerPatient(data);
      toast.success("Registration successful. Please login.");
      navigate(ROUTES.LOGIN);
    } catch (error) {
      toast.error(handleApiError(error));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="page-container">
      <div className="auth-card">
        <h3 className="section-title">Patient Registration</h3>
        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="mb-3">
            <label className="form-label">Full Name</label>
            <input
              className="form-control"
              {...register("full_name", fullNameRules)}
            />
            {errors.full_name && (
              <div className="field-error">{errors.full_name.message}</div>
            )}
          </div>

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
            <label className="form-label">Phone Number</label>
            <input
              className="form-control"
              {...register("phone_number", phoneRules)}
            />
            {errors.phone_number && (
              <div className="field-error">{errors.phone_number.message}</div>
            )}
          </div>

          <div className="mb-3">
            <label className="form-label">Gender</label>
            <select
              className="form-select"
              {...register("gender", genderRules)}
            >
              <option value="">Select gender</option>
              <option value={GENDERS.MALE}>Male</option>
              <option value={GENDERS.FEMALE}>Female</option>
              <option value={GENDERS.OTHER}>Other</option>
            </select>
            {errors.gender && (
              <div className="field-error">{errors.gender.message}</div>
            )}
          </div>

          <div className="mb-3">
            <label className="form-label">Date of Birth</label>
            <input
              type="date"
              className="form-control"
              {...register("date_of_birth", dobRules)}
            />
            {errors.date_of_birth && (
              <div className="field-error">{errors.date_of_birth.message}</div>
            )}
          </div>

          <div className="mb-3">
            <label className="form-label">Password</label>

            <div className="input-group">
              <input
                type={showPassword ? "text" : "password"}
                className="form-control"
                {...register("password", passwordRules)}
              />

              <button
                type="button"
                className="btn btn-outline-secondary"
                onClick={() => setShowPassword((prev) => !prev)}
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
            {submitting ? "Registering..." : "Register"}
          </button>
        </form>
        <div className="mt-4 text-center">
          <p className="text-muted mb-2">Already have an account?</p>

          <Link to={ROUTES.LOGIN} className="btn btn-outline-primary w-100">
            Login
          </Link>
        </div>
      </div>
    </div>
  );
}

export default PatientRegisterPage;
