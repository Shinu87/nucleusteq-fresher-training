import { useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate, Link } from "react-router-dom";
import { toast } from "react-toastify";
import { registerDoctor } from "../api/authApi";
import { handleApiError } from "../utils/handleApiError";
import { GENDERS } from "../constants/genders";
import { SPECIALIZATIONS } from "../constants/specializations";
import { ROUTES } from "../constants/routes";
import {
  fullNameRules,
  emailRules,
  phoneRules,
  genderRules,
  qualificationRules,
  specializationRules,
  experienceRules,
  licenseNumberRules,
  consultationFeeRules,
  clinicAddressRules,
} from "../validations/authValidation";

function DoctorRegisterPage() {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm();
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();

  // doctor sets a password later, from the approval email link
  async function onSubmit(data) {
    setSubmitting(true);
    try {
      await registerDoctor(data);
      toast.success(
        "Application submitted. You will be notified once approved by admin.",
      );
      navigate(ROUTES.LOGIN);
    } catch (error) {
      toast.error(handleApiError(error));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="page-container">
      <div className="auth-card wide">
        <h3 className="section-title">Doctor Registration</h3>
        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <div className="row">
            <div className="col-md-6 mb-3">
              <label className="form-label">Full Name</label>
              <input
                className="form-control"
                {...register("full_name", fullNameRules)}
              />
              {errors.full_name && (
                <div className="field-error">{errors.full_name.message}</div>
              )}
            </div>

            <div className="col-md-6 mb-3">
              <label className="form-label">Email</label>
              <input
                className="form-control"
                {...register("email", emailRules)}
              />
              {errors.email && (
                <div className="field-error">{errors.email.message}</div>
              )}
            </div>

            <div className="col-md-6 mb-3">
              <label className="form-label">Phone Number</label>
              <input
                className="form-control"
                {...register("phone_number", phoneRules)}
              />
              {errors.phone_number && (
                <div className="field-error">{errors.phone_number.message}</div>
              )}
            </div>

            <div className="col-md-6 mb-3">
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

            <div className="col-md-6 mb-3">
              <label className="form-label">Qualification</label>
              <input
                className="form-control"
                {...register("qualification", qualificationRules)}
              />
              {errors.qualification && (
                <div className="field-error">
                  {errors.qualification.message}
                </div>
              )}
            </div>

            <div className="col-md-6 mb-3">
              <label className="form-label">Specialization</label>
              <select
                className="form-select"
                {...register("specialization", specializationRules)}
              >
                <option value="">Select specialization</option>
                {SPECIALIZATIONS.map((item) => (
                  <option key={item} value={item}>
                    {item}
                  </option>
                ))}
              </select>
              {errors.specialization && (
                <div className="field-error">
                  {errors.specialization.message}
                </div>
              )}
            </div>

            <div className="col-md-6 mb-3">
              <label className="form-label">Experience (years)</label>
              <input
                type="number"
                className="form-control"
                {...register("experience_years", experienceRules)}
              />
              {errors.experience_years && (
                <div className="field-error">
                  {errors.experience_years.message}
                </div>
              )}
            </div>

            <div className="col-md-6 mb-3">
              <label className="form-label">License Number</label>
              <input
                className="form-control"
                {...register("license_number", licenseNumberRules)}
              />
              {errors.license_number && (
                <div className="field-error">
                  {errors.license_number.message}
                </div>
              )}
            </div>

            <div className="col-md-6 mb-3">
              <label className="form-label">Consultation Fee</label>
              <input
                type="number"
                step="0.01"
                className="form-control"
                {...register("consultation_fee", consultationFeeRules)}
              />
              {errors.consultation_fee && (
                <div className="field-error">
                  {errors.consultation_fee.message}
                </div>
              )}
            </div>

            <div className="col-12 mb-3">
              <label className="form-label">Clinic Address</label>
              <textarea
                className="form-control"
                rows="2"
                {...register("clinic_address", clinicAddressRules)}
              />
              {errors.clinic_address && (
                <div className="field-error">
                  {errors.clinic_address.message}
                </div>
              )}
            </div>
          </div>

          <p className="text-muted small mb-3">
            No password needed right now. Once admin approves your application,
            we'll email you a link to set one.
          </p>

          <button
            type="submit"
            className="btn btn-primary w-100"
            disabled={submitting}
          >
            {submitting ? "Submitting..." : "Submit Application"}
          </button>
        </form>
        <div className="mt-4 text-center">
          <p className="text-muted mb-2">Already have an account?</p>

          <Link to={ROUTES.LOGIN} className="btn btn-outline-primary px-4">
            Login
          </Link>
        </div>
      </div>
    </div>
  );
}

export default DoctorRegisterPage;
