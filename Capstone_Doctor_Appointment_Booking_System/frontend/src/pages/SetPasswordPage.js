import { useState } from "react";
import { useForm } from "react-hook-form";
import { useParams, useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import { setPassword as setPasswordApi } from "../api/authApi";
import { handleApiError } from "../utils/handleApiError";
import { ROUTES } from "../constants/routes";
import { passwordRules, confirmPasswordRules } from "../validations/authValidation";

function SetPasswordPage() {
  const { token } = useParams();
  const {
    register,
    handleSubmit,
    getValues,
    formState: { errors },
  } = useForm();
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  async function onSubmit(data) {
    setSubmitting(true);
    try {
      await setPasswordApi({ token, new_password: data.new_password });
      toast.success("Password set successfully. Please login.");
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
        <h3 className="section-title">Set Your Password</h3>
        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="mb-3">
            <label className="form-label">New Password</label>
            <input type="password" className="form-control" {...register("new_password", passwordRules)} />
            {errors.new_password && <div className="field-error">{errors.new_password.message}</div>}
          </div>

          <div className="mb-3">
            <label className="form-label">Confirm Password</label>
            <input
              type="password"
              className="form-control"
              {...register("confirm_password", confirmPasswordRules(() => getValues("new_password")))}
            />
            {errors.confirm_password && (
              <div className="field-error">{errors.confirm_password.message}</div>
            )}
          </div>

          <button type="submit" className="btn btn-primary w-100" disabled={submitting}>
            {submitting ? "Saving..." : "Set Password"}
          </button>
        </form>
      </div>
    </div>
  );
}

export default SetPasswordPage;
