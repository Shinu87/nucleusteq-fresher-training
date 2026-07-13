"""
Routes for patients to search for doctors and view a doctor's profile.
"""

from beanie import PydanticObjectId
from fastapi import APIRouter, Depends, Query

from backend.constants.api_constants import APIPrefixes, APITags
from backend.middleware.auth import CurrentUser, get_current_user
from backend.schemas.response.appt_doctor_response import (
    AvailableSlotSummary,
    DoctorDetailResponse,
    DoctorSummaryResponse,
)
from backend.services.doctor_search_service import (
    DoctorSearchService,
    get_doctor_search_service,
)
from backend.utils.doctor_mapper import (
    to_detail_response,
    to_summary_response,
)
from backend.constants.specialization import Specialization

router = APIRouter(prefix=APIPrefixes.DOCTORS, tags=[APITags.DOCTOR_SEARCH])


@router.get("", response_model=list[DoctorSummaryResponse])
async def search_doctors(
    search: str | None = Query(default=None, description="Matches doctor name or specialization"),
    specialization: Specialization | None = Query(default=None, description="Exact specialization, e.g. Cardiologist"),
    min_experience: int | None = Query(default=None, ge=0),
    max_fee: float | None = Query(default=None, gt=0),
    current_user: CurrentUser = Depends(get_current_user),
    doctor_search_service: DoctorSearchService = Depends(get_doctor_search_service),
):
    doctors = await doctor_search_service.search_doctors(
        search=search,
        specialization=specialization,
        min_experience=min_experience,
        max_fee=max_fee,
    )
    return [to_summary_response(doctor) for doctor in doctors]


@router.get("/{doctor_id}", response_model=DoctorDetailResponse)
async def get_doctor_detail(
    doctor_id: PydanticObjectId,
    current_user: CurrentUser = Depends(get_current_user),
    doctor_search_service: DoctorSearchService = Depends(get_doctor_search_service),
):
    doctor, available_slots = await doctor_search_service.get_doctor_detail(doctor_id)

    return to_detail_response(
    doctor,
    available_slots,
    )