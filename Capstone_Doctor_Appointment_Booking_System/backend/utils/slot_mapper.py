from backend.schemas.response.slot_response import SlotResponse


def to_slot_response(slot) -> SlotResponse:
    return SlotResponse(
        id=str(slot.id),
        doctor_id=str(slot.doctor_id),
        slot_date=slot.slot_date,
        start_time=slot.start_time,
        end_time=slot.end_time,
        status=slot.status,
        created_at=slot.created_at,
    )