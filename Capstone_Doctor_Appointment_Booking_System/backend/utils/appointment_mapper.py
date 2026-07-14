from backend.schemas.response.appointment_response import AppointmentResponse


def to_appointment_response(appointment) -> AppointmentResponse:
    return AppointmentResponse(
        id=str(appointment.id),
        patient_id=str(appointment.patient_id),
        patient_name=appointment.patient_name,
        doctor_id=str(appointment.doctor_id),
        doctor_name=appointment.doctor_name,
        slot_id=str(appointment.slot_id),
        appointment_date=appointment.appointment_date,
        start_time=appointment.start_time,
        end_time=appointment.end_time,
        status=appointment.status,
        payment_status=appointment.payment_status,
        consultation_fee=appointment.consultation_fee,
        booked_at=appointment.booked_at,
        cancelled_at=appointment.cancelled_at,
        completed_at=appointment.completed_at,
    )