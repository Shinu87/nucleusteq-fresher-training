import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getDoctorById } from "../api/doctorApi";
import { handleApiError } from "../utils/handleApiError";
import { toast } from "react-toastify";
import { bookAppointment } from "../api/appointmentApi";
import { useAuth } from "../context/AuthContext";
import { ROLES } from "../constants/roles";
import { ROUTES } from "../constants/routes";

function DoctorProfilePage() {
  const { doctorId } = useParams();
  const [doctor, setDoctor] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const { user } = useAuth();
  const [bookingSlotId, setBookingSlotId] = useState(null);
  const navigate = useNavigate();

  const isPatient = user?.role === ROLES.PATIENT;
  const [filterDate, setFilterDate] = useState("");
  const [filterTimeOfDay, setFilterTimeOfDay] = useState("all");

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

  // book the clicked slot then refresh the list so it disappears
  async function handleSelectSlot(slot) {
    navigate(ROUTES.BOOKING_CONFIRMATION, {
      state: {
        slotId: slot.id,
        doctorName: doctor.full_name,
        specialization: doctor.specialization,
        slotDate: slot.slot_date,
        startTime: slot.start_time,
        endTime: slot.end_time,
        consultationFee: doctor.consultation_fee,
      },
    });
  }

  function getTimeOfDay(startTime) {
    const hour = Number(startTime.split(":")[0]);
    if (hour < 12) return "morning";
    if (hour < 17) return "afternoon";
    return "evening";
  }

  function handleClearFilters() {
    setFilterDate("");
    setFilterTimeOfDay("all");
  }

  let filteredSlots = [];
  if (doctor) {
    filteredSlots = doctor.available_slots.filter((slot) => {
      if (filterDate && slot.slot_date !== filterDate) {
        return false;
      }
      if (
        filterTimeOfDay !== "all" &&
        getTimeOfDay(slot.start_time) !== filterTimeOfDay
      ) {
        return false;
      }
      return true;
    });
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
      {/*  Doctor info card  */}
      <div className="card shadow-sm border-0 rounded-4 mb-4">
        <div className="card-body p-4">
          <div className="row align-items-center">
            {/* Doctor Avatar */}
            <div className="col-md-2 text-center mb-3 mb-md-0">
              <div
                className="bg-primary text-white rounded-circle d-inline-flex align-items-center justify-content-center"
                style={{
                  width: "90px",
                  height: "90px",
                  fontSize: "2rem",
                  fontWeight: "bold",
                }}
              >
                {doctor.full_name?.charAt(0).toUpperCase()}
              </div>
            </div>

            {/* Doctor Details */}
            <div className="col-md-7">
              <h3 className="fw-bold mb-2">Dr. {doctor.full_name}</h3>

              <span className="badge bg-primary mb-3">
                {doctor.specialization}
              </span>

              <div className="mb-2">
                <i className="bi bi-mortarboard-fill text-primary me-2"></i>
                <strong>Qualification:</strong> {doctor.qualification}
              </div>

              <div className="mb-2">
                <i className="bi bi-briefcase-fill text-primary me-2"></i>
                <strong>Experience:</strong> {doctor.experience_years} Years
              </div>

              <div>
                <i className="bi bi-geo-alt-fill text-danger me-2"></i>
                <strong>Clinic:</strong> {doctor.clinic_address}
              </div>
            </div>

            {/* Fee Card */}
            <div className="col-md-3 mt-3 mt-md-0">
              <div className="card bg-light border-0 text-center">
                <div className="card-body">
                  <small className="text-muted d-block">Consultation Fee</small>

                  <h3 className="text-success fw-bold my-2">
                    ₹{doctor.consultation_fee}
                  </h3>

                  <span className="badge bg-success">Available</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="card shadow-sm border-0 rounded-4 mb-4">
        <div className="card-body py-3">
          <h4 className="fw-bold mb-1">
            <i className="bi bi-calendar-week text-primary me-2"></i>
            Available Slots
          </h4>
          <small className="text-muted">
            Select an available slot to book your consultation.
          </small>
        </div>
      </div>
      {doctor.available_slots.length === 0 && (
        <p>No available slots right now.</p>
      )}

      {!isPatient && doctor.available_slots.length > 0 && (
        <p className="text-muted small">Only patients can book appointments.</p>
      )}

      {doctor.available_slots.length > 0 && (
        <div className="card mb-3 bg-light border-0">
          <div className="card-body">
            <div className="row g-3 align-items-end">
              {/* date filter */}
              <div className="col-md-4">
                <label className="form-label">Date</label>
                <input
                  type="date"
                  className="form-control"
                  value={filterDate}
                  onChange={(e) => setFilterDate(e.target.value)}
                />
              </div>

              {/* time of day filter */}
              <div className="col-md-4">
                <label className="form-label">Time of Day</label>
                <select
                  className="form-select"
                  value={filterTimeOfDay}
                  onChange={(e) => setFilterTimeOfDay(e.target.value)}
                >
                  <option value="all">All</option>
                  <option value="morning">Morning</option>
                  <option value="afternoon">Afternoon</option>
                  <option value="evening">Evening</option>
                </select>
              </div>

              {/* clear filters button */}
              <div className="col-md-4">
                <button
                  type="button"
                  className="btn btn-outline-secondary w-100"
                  onClick={handleClearFilters}
                >
                  Clear Filters
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {doctor.available_slots.length > 0 && (
        <p className="text-muted small">
          Showing {filteredSlots.length} of {doctor.available_slots.length}{" "}
          slots
        </p>
      )}

      {doctor.available_slots.length > 0 && filteredSlots.length === 0 && (
        <p className="text-muted">No slots found for the selected filters.</p>
      )}

      <div className="row g-4">
        {filteredSlots.map((slot) => (
          <div key={slot.id} className="col-12 col-md-6 col-lg-4">
            <div className="card shadow-sm border-0 rounded-4 h-100">
              <div className="card-body d-flex flex-column p-4">
                {/* Header */}
                <div className="d-flex justify-content-between align-items-start mb-3">
                  <div>
                    <h6 className="fw-bold mb-1">
                      <i className="bi bi-calendar-event text-primary me-2"></i>
                      {slot.slot_date}
                    </h6>
                    <small className="text-muted">Available Appointment</small>
                  </div>

                  <span className="badge bg-success rounded-pill">
                    Available
                  </span>
                </div>

                {/* Time */}
                <div className="row g-2 mb-4">
                  <div className="col-6">
                    <div className="bg-light rounded-3 text-center p-3 h-100">
                      <i className="bi bi-play-circle-fill text-success fs-4"></i>
                      <div className="small text-muted mt-2">Start Time</div>
                      <div className="fw-bold fs-5">{slot.start_time}</div>
                    </div>
                  </div>

                  <div className="col-6">
                    <div className="bg-light rounded-3 text-center p-3 h-100">
                      <i className="bi bi-stop-circle-fill text-danger fs-4"></i>
                      <div className="small text-muted mt-2">End Time</div>
                      <div className="fw-bold fs-5">{slot.end_time}</div>
                    </div>
                  </div>
                </div>

                {/* Button */}
                {isPatient && (
                  <button
                    className="btn btn-primary rounded-pill mt-auto"
                    onClick={() => handleSelectSlot(slot)}
                  >
                    <i className="bi bi-calendar-check me-2"></i>
                    Book Appointment
                  </button>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default DoctorProfilePage;
