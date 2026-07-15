import { Link } from "react-router-dom";

function DoctorCard({ doctor }) {
  return (
    <div className="d-flex flex-column h-100">
      <div className="d-flex align-items-center mb-3">
        <div
          className="d-flex align-items-center justify-content-center bg-primary bg-opacity-10 text-primary fw-bold rounded-circle"
          style={{ width: "60px", height: "60px", fontSize: "22px" }}
        >
          {doctor.full_name.charAt(0)}
        </div>

        <div className="ms-3">
          <h5 className="mb-1 fw-bold">{doctor.full_name}</h5>
          <span className="badge bg-info-subtle text-info-emphasis fw-medium">
            {doctor.specialization}
          </span>
        </div>
      </div>

      <div className="mb-2">
        <strong>🎓 Qualification:</strong> {doctor.qualification}
      </div>

      <div className="mb-2">
        <strong>💼 Experience:</strong> {doctor.experience_years} Years
      </div>

      <div className="mb-2">
        <strong>💰 Consultation Fee:</strong>{" "}
        <span className="text-primary fw-semibold">
          ₹{doctor.consultation_fee}
        </span>
      </div>

      <div className="text-muted mb-4" style={{ fontSize: "0.95rem" }}>
        📍 {doctor.clinic_address}
      </div>

      <Link
        to={`/doctors/${doctor.id}`}
        className="btn btn-primary mt-auto fw-semibold"
      >
        Book Appointment
      </Link>
    </div>
  );
}

export default DoctorCard;
