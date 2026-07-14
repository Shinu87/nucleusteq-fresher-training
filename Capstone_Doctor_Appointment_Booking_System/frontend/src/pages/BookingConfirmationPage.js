import { useState } from "react";
import { useLocation, useNavigate, Link } from "react-router-dom";
import { toast } from "react-toastify";
import { bookAppointment } from "../api/appointmentApi";
import { handleApiError } from "../utils/handleApiError";
import { ROUTES } from "../constants/routes";

// UI-only options, no real payment gateway is involved
const PAYMENT_METHODS = [
  { id: "UPI", icon: "📱", description: "Pay using any UPI app" },
  {
    id: "Credit/Debit Card",
    icon: "💳",
    description: "Visa, Mastercard, RuPay",
  },
  { id: "Net Banking", icon: "🏦", description: "All major banks supported" },
  { id: "Wallet", icon: "👛", description: "Paytm, PhonePe, Amazon Pay" },
];

function BookingConfirmationPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const booking = location.state;

  const [paymentMethod, setPaymentMethod] = useState("");
  const [agreedToTerms, setAgreedToTerms] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");

  if (!booking) {
    return (
      <div className="page-container">
        <p>No booking selected.</p>
        <Link to={ROUTES.DOCTOR_SEARCH} className="btn btn-primary">
          Find a Doctor
        </Link>
      </div>
    );
  }

  async function handlePayAndConfirm() {
    setSubmitting(true);
    setError("");
    try {
      await bookAppointment(booking.slotId);
      toast.success("Appointment booked successfully");
      navigate(ROUTES.MY_APPOINTMENTS);
    } catch (err) {
      setError(handleApiError(err));
    } finally {
      setSubmitting(false);
    }
  }

  const canPay = paymentMethod && agreedToTerms && !submitting;

  return (
    <div className="page-container">
      <div className="d-flex align-items-center justify-content-between mb-3">
        <button
          type="button"
          className="btn btn-link text-decoration-none px-0"
          onClick={() => navigate(-1)}
        >
          &larr; Back
        </button>
        <span className="text-muted small">Step 2 of 2 &middot; Payment</span>
      </div>

      <h3 className="section-title mb-4">Confirm &amp; Pay</h3>

      <div className="row g-4">
        {/* left column: payment method selection */}
        <div className="col-lg-7 order-2 order-lg-1">
          <div className="card mb-4">
            <div className="card-body">
              <h5 className="mb-3">Select Payment Method</h5>

              {PAYMENT_METHODS.map((method) => {
                const isSelected = paymentMethod === method.id;
                return (
                  <label
                    key={method.id}
                    htmlFor={method.id}
                    className={`d-flex align-items-center p-3 mb-2 border rounded ${
                      isSelected
                        ? "border-primary bg-primary-subtle"
                        : "border-secondary-subtle"
                    }`}
                    style={{ cursor: "pointer" }}
                  >
                    <input
                      type="radio"
                      className="form-check-input me-3"
                      id={method.id}
                      name="paymentMethod"
                      value={method.id}
                      checked={isSelected}
                      onChange={(e) => setPaymentMethod(e.target.value)}
                    />
                    <span className="fs-5 me-3">{method.icon}</span>
                    <span className="flex-grow-1">
                      <span className="d-block fw-semibold">{method.id}</span>
                      <span className="d-block text-muted small">
                        {method.description}
                      </span>
                    </span>
                  </label>
                );
              })}
            </div>
          </div>

          {/* terms acknowledgement */}
          <div className="form-check mb-4">
            <input
              type="checkbox"
              className="form-check-input"
              id="agreeTerms"
              checked={agreedToTerms}
              onChange={(e) => setAgreedToTerms(e.target.checked)}
            />
            <label
              className="form-check-label small text-muted"
              htmlFor="agreeTerms"
            >
              I agree to the appointment terms and cancellation policy.
              Appointments can be rescheduled or cancelled up to 2 hours before
              the slot time.
            </label>
          </div>
        </div>

        {/* right column: order summary */}
        <div className="col-lg-5 order-1 order-lg-2">
          <div className="card" style={{ position: "sticky", top: "16px" }}>
            <div className="card-body">
              <h5 className="mb-3">Appointment Summary</h5>

              <div className="d-flex align-items-center mb-3">
                <div
                  className="d-flex align-items-center justify-content-center rounded-circle flex-shrink-0"
                  style={{
                    width: "48px",
                    height: "48px",
                    background: "#e0f2fe",
                    color: "#0369a1",
                    fontWeight: 700,
                  }}
                >
                  {booking.doctorName?.charAt(0)}
                </div>
                <div className="ms-3">
                  <div className="fw-semibold">Dr. {booking.doctorName}</div>
                  <div className="text-muted small">
                    {booking.specialization}
                  </div>
                </div>
              </div>

              <div className="d-flex justify-content-between small mb-1">
                <span className="text-muted">Date</span>
                <span className="fw-semibold">{booking.slotDate}</span>
              </div>
              <div className="d-flex justify-content-between small mb-3">
                <span className="text-muted">Time</span>
                <span className="fw-semibold">
                  {booking.startTime} - {booking.endTime}
                </span>
              </div>

              <hr />

              <div className="d-flex justify-content-between mb-1">
                <span className="text-muted">Consultation Fee</span>
                <span>₹{booking.consultationFee}</span>
              </div>
              <div className="d-flex justify-content-between fw-bold fs-5 mb-3">
                <span>Amount Payable</span>
                <span>₹{booking.consultationFee}</span>
              </div>

              {error && (
                <div className="alert alert-danger py-2 small mb-3">
                  {error}
                </div>
              )}

              <button
                className="btn btn-primary w-100 py-2"
                disabled={!canPay}
                onClick={handlePayAndConfirm}
              >
                {submitting ? (
                  <>
                    <span
                      className="spinner-border spinner-border-sm me-2"
                      role="status"
                      aria-hidden="true"
                    ></span>
                    Processing...
                  </>
                ) : (
                  `Pay ₹${booking.consultationFee} & Confirm`
                )}
              </button>

              <p className="text-center text-muted small mt-2 mb-0">
                🔒 This is a demo checkout &mdash; no real payment is processed.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default BookingConfirmationPage;
