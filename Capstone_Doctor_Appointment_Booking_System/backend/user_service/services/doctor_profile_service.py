"""
Business logic for the doctor profile and approval workflow:

    Doctor Registration -> PENDING_APPROVAL -> Admin Approve/Reject
        -> Setup Password Email -> Doctor Creates Password -> ACTIVE

This is kept separate from auth_service.py because it deals with a
different document (DoctorProfile) and a different concern (admin
review), even though it shares the same User collection underneath.
"""

import logging
from datetime import datetime, timezone

from fastapi import HTTPException, status

from user_service.clients.appointment_client import sync_doctor_to_appointment_service
from user_service.clients.notification_client import send_setup_password_notification
from user_service.constants.account_status import AccountStatus
from user_service.constants.approval_status import ApprovalStatus
from user_service.constants.roles import Role
from user_service.models.doctor_profile import DoctorProfile
from user_service.models.user import User
from user_service.schemas.request.doctor_request import DoctorRegisterRequest
from user_service.utils.token_utils import (
    generate_setup_token,
    get_setup_token_expiry,
    hash_setup_token,
)

logger = logging.getLogger(__name__)


async def submit_doctor_application(payload: DoctorRegisterRequest) -> tuple[User, DoctorProfile]:
    """
    Step 1 of the workflow: a doctor submits their application.

    Creates a User (role=DOCTOR, account_status=PENDING_APPROVAL, no
    password yet) and a linked DoctorProfile (approval_status=PENDING_APPROVAL).
    The doctor CANNOT log in after this - there's no password set, and
    even if there were, authenticate_user blocks anything that isn't
    ACTIVE.
    """
    existing_user = await User.find_one(User.email == payload.email)
    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Email is already registered",
        )

    existing_license = await DoctorProfile.find_one(
        DoctorProfile.license_number == payload.license_number
    )
    if existing_license:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="This license number is already registered",
        )

    new_user = User(
        full_name=payload.full_name,
        email=payload.email,
        password_hash=None,  # no password yet - set after approval
        phone_number=payload.phone_number,
        role=Role.DOCTOR,
        account_status=AccountStatus.PENDING_APPROVAL,
    )
    await new_user.insert()

    new_profile = DoctorProfile(
        user_id=new_user.id,
        qualification=payload.qualification,
        specialization=payload.specialization,
        experience_years=payload.experience_years,
        license_number=payload.license_number,
        consultation_fee=payload.consultation_fee,
        clinic_address=payload.clinic_address,
        approval_status=ApprovalStatus.PENDING_APPROVAL,
    )
    await new_profile.insert()

    logger.info("New doctor application submitted: %s", new_user.email)
    return new_user, new_profile


async def list_doctor_applications(approval_status: ApprovalStatus | None = None) -> list[dict]:
    """
    Returns doctor applications for the admin review screen
    """
    query = DoctorProfile.find_all()
    if approval_status is not None:
        query = DoctorProfile.find(DoctorProfile.approval_status == approval_status)

    profiles = await query.to_list()

    results = []
    for profile in profiles:
        user = await User.get(profile.user_id)
        if user is not None:
            results.append({"user": user, "profile": profile})
    return results


async def _get_profile_and_user(doctor_profile_id) -> tuple[DoctorProfile, User]:
    profile = await DoctorProfile.get(doctor_profile_id)
    if profile is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Doctor application not found")

    user = await User.get(profile.user_id)
    if user is None:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail="Linked user account not found")

    return profile, user


async def approve_doctor(doctor_profile_id, admin_id) -> tuple[User, DoctorProfile]:
    """
    Step 2 (approve path) of the workflow:
      1. Mark the application APPROVED.
      2. Generate a one-time setup token (store only its hash).
      3. Ask Notification Service to "send" the setup-password email.
      4. Sync the doctor into Appointment Service's searchable doctor list.

    The user's account_status stays PENDING_APPROVAL until they actually
    finish setting their password - approval alone does not let them log in.
    """
    profile, user = await _get_profile_and_user(doctor_profile_id)

    if profile.approval_status != ApprovalStatus.PENDING_APPROVAL:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"This application has already been {profile.approval_status.value.lower()}",
        )

    raw_token = generate_setup_token()
    profile.approval_status = ApprovalStatus.APPROVED
    profile.reviewed_by = admin_id
    profile.reviewed_at = datetime.now(timezone.utc)
    profile.setup_token_hash = hash_setup_token(raw_token)
    profile.setup_token_expiry = get_setup_token_expiry()
    await profile.save()

    await send_setup_password_notification(user.email, raw_token)
    await sync_doctor_to_appointment_service(user, profile)

    logger.info("Doctor application approved: %s", user.email)
    return user, profile


async def reject_doctor(doctor_profile_id, admin_id, reason: str | None) -> tuple[User, DoctorProfile]:
    """
    Step 2 (reject path): marks both the profile and the user account as
    rejected. A rejected doctor can never log in - account_status stays
    REJECTED permanently for that account.
    """
    profile, user = await _get_profile_and_user(doctor_profile_id)

    if profile.approval_status != ApprovalStatus.PENDING_APPROVAL:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"This application has already been {profile.approval_status.value.lower()}",
        )

    profile.approval_status = ApprovalStatus.REJECTED
    profile.reviewed_by = admin_id
    profile.reviewed_at = datetime.now(timezone.utc)
    await profile.save()

    user.account_status = AccountStatus.REJECTED
    await user.save()

    logger.info("Doctor application rejected: %s (reason: %s)", user.email, reason)
    return user, profile