"""
Routes for patients (or anyone logged in) to search for doctors and view
a doctor's profile.
"""

from beanie import PydanticObjectId
from fastapi import APIRouter, Depends, Query

from appointment_service.middleware.auth import CurrentUser, get_current_user
from appointment_service.schemas.response.doctor_response import (
    AvailableSlotSummary,
    DoctorDetailResponse,
    DoctorSummaryResponse,
)
from appointment_service.services import doctor_search_service

router = APIRouter(prefix="/doctors", tags=["Doctor Search"])


def _to_summary_response(doctor) -> DoctorSummaryResponse:
    return DoctorSummaryResponse(
        id=str(doctor.id),
        full_name=doctor.full_name,
        specialization=doctor.specialization,
        qualification=doctor.qualification,
        experience_years=doctor.experience_years,
        consultation_fee=doctor.consultation_fee,
        clinic_address=doctor.clinic_address,
    )


@router.get("", response_model=list[DoctorSummaryResponse])
async def search_doctors(
    search: str | None = Query(default=None, description="Matches doctor name or specialization"),
    specialization: str | None = Query(default=None, description="Exact specialization, e.g. Cardiologist"),
    min_experience: int | None = Query(default=None, ge=0),
    max_fee: float | None = Query(default=None, gt=0),
    current_user: CurrentUser = Depends(get_current_user),
):
    doctors = await doctor_search_service.search_doctors(
        search=search,
        specialization=specialization,
        min_experience=min_experience,
        max_fee=max_fee,
    )
    return [_to_summary_response(doctor) for doctor in doctors]


@router.get("/{doctor_id}", response_model=DoctorDetailResponse)
async def get_doctor_detail(
    doctor_id: PydanticObjectId,
    current_user: CurrentUser = Depends(get_current_user),
):
    doctor, available_slots = await doctor_search_service.get_doctor_detail(doctor_id)

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