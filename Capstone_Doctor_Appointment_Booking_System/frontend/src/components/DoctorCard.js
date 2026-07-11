import { Link } from "react-router-dom";

function DoctorCard({ doctor }) {
  return (
    <div
      className="card h-100 border-0"
      style={{
        borderRadius: "18px",
        boxShadow: "0 8px 20px rgba(15, 118, 110, 0.12)",
        transition: "0.3s ease",
      }}
    >
      <div className="card-body d-flex flex-column p-4">
        <div className="d-flex align-items-center mb-3">
          <div
            style={{
              width: "60px",
              height: "60px",
              borderRadius: "50%",
              background: "#d1fae5",
              color: "#0f766e",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              fontWeight: "bold",
              fontSize: "22px",
            }}
          >
            {doctor.full_name.charAt(0)}
          </div>

          <div className="ms-3">
            <h5 className="mb-1 fw-bold">{doctor.full_name}</h5>

            <span
              className="badge"
              style={{
                backgroundColor: "#e0f2fe",
                color: "#0369a1",
                fontWeight: "500",
              }}
            >
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
          <strong>💰 Consultation Fee:</strong>
          <span
            style={{
              color: "#0f766e",
              fontWeight: "600",
            }}
          >
            {" "}
            ₹{doctor.consultation_fee}
          </span>
        </div>

        <div
          className="mb-4"
          style={{
            color: "#64748b",
            fontSize: "0.95rem",
          }}
        >
          📍 {doctor.clinic_address}
        </div>

        <Link
          to={`/doctors/${doctor.id}`}
          className="btn mt-auto"
          style={{
            backgroundColor: "#0f766e",
            color: "#fff",
            borderRadius: "10px",
            padding: "10px",
            fontWeight: "600",
          }}
        >
          View Profile
        </Link>
      </div>
    </div>
  );
}

export default DoctorCard;
