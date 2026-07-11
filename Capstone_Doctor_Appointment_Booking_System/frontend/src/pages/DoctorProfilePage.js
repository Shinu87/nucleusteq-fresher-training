import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getDoctorById } from "../api/doctorApi";
import { handleApiError } from "../utils/handleApiError";

function DoctorProfilePage() {
  const { doctorId } = useParams();
  const [doctor, setDoctor] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // load the doctor detail when the page opens
  useEffect(() => {
    fetchDoctor();
  }, [doctorId]);

  async function fetchDoctor() {
    setLoading(true);
    setError("");
    try {
      const response = await getDoctorById(doctorId);
      setDoctor(response.data);
    } catch (err) {
      setError(handleApiError(err));
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <div className="page-container d-flex flex-column align-items-center justify-content-center py-5">
        <div
          className="spinner-border text-primary mb-3"
          role="status"
          aria-hidden="true"
        ></div>
        <p className="text-muted mb-0">Loading doctor profile...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="page-container">
        <p className="text-danger">{error}</p>
      </div>
    );
  }

  return (
    <div className="page-container">
      <div className="card mb-4">
        <div className="card-body">
          <h4 className="mb-1">Dr. {doctor.full_name}</h4>
          <p className="text-muted mb-2">{doctor.specialization}</p>
          <p className="mb-1">{doctor.qualification}</p>
          <p className="mb-1">{doctor.experience_years} years of experience</p>
          <p className="mb-1">Consultation Fee: ₹{doctor.consultation_fee}</p>
          <p className="mb-0 text-muted">{doctor.clinic_address}</p>
        </div>
      </div>

      <h5 className="section-title">Available Slots</h5>
      {doctor.available_slots.length === 0 && (
        <p>No available slots right now.</p>
      )}
      <div className="row">
        {doctor.available_slots.map((slot) => (
          <div key={slot.id} className="col-md-3 mb-3">
            <div className="card text-center">
              <div className="card-body py-2">
                <div>{slot.slot_date}</div>
                <div className="text-muted">
                  {slot.start_time} - {slot.end_time}
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default DoctorProfilePage;
