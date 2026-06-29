"""
Internal-only routes - never called by a browser, only by other services.

These are deliberately NOT included under the public /api/v1 prefix and
are tagged separately.
"""

from fastapi import APIRouter, Depends

from appointment_service.middleware.internal_auth import require_internal_api_key
from appointment_service.schemas.request.internal_request import DoctorSyncRequest
from appointment_service.services.doctor_sync_service import upsert_doctor

router = APIRouter(
    prefix="/internal",
    tags=["Internal - Service to Service"],
    dependencies=[Depends(require_internal_api_key)],
)


@router.post("/doctors")
async def sync_doctor(payload: DoctorSyncRequest):
    """
    Called by User Service whenever a doctor is approved (or their
    profile changes). Creates or updates our local Doctor
    read-model so this doctor becomes searchable/bookable here.
    """
    doctor = await upsert_doctor(payload)
    return {"status": "synced", "doctor_id": str(doctor.id)}