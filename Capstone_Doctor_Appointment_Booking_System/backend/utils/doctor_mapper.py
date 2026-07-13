from backend.schemas.response.appt_doctor_response import (
    AvailableSlotSummary,
    DoctorDetailResponse,
    DoctorSummaryResponse,
)


def to_summary_response(doctor) -> DoctorSummaryResponse:
    return DoctorSummaryResponse(
        id=str(doctor.id),
        full_name=doctor.full_name,
        specialization=doctor.specialization,
        qualification=doctor.qualification,
        experience_years=doctor.experience_years,
        consultation_fee=doctor.consultation_fee,
        clinic_address=doctor.clinic_address,
    )


def to_detail_response(
    doctor,
    available_slots,
) -> DoctorDetailResponse:
    return DoctorDetailResponse(
        id=str(doctor.id),
        full_name=doctor.full_name,
        specialization=doctor.specialization,
        qualification=doctor.qualification,
        experience_years=doctor.experience_years,
        consultation_fee=doctor.consultation_fee,
        clinic_address=doctor.clinic_address,
        available_slots=[
            AvailableSlotSummary(
                id=str(slot.id),
                slot_date=slot.slot_date,
                start_time=slot.start_time,
                end_time=slot.end_time,
            )
            for slot in available_slots
        ],
    )