"""
One-time bootstrap script to create the very first ADMIN account.

There is no public "register as admin" endpoint anywhere in this project
 - an admin has to already exist to approve or
reject doctors later on, so the very first admin has to be created this
way, directly against the database, instead of through the API.

"""

import asyncio

from fastapi import HTTPException, status

from backend.constants.roles import Role
from backend.database.connection import close_mongo_connection, connect_to_mongo
from backend.models.user import User
from backend.utils.security import hash_password


async def create_admin(full_name: str, email: str, password: str, phone_number: str) -> User:
    """
    Creates a single ADMIN user.
    """
    existing_user = await User.find_one(User.email == email)
    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Email is already registered",
        )

    admin_user = User(
        full_name=full_name,
        email=email,
        password_hash=hash_password(password),
        phone_number=phone_number,
        role=Role.ADMIN,
    )
    await admin_user.insert()
    return admin_user


async def _main() -> None:
    print(" Create the first ADMIN account ")
    full_name = input("Full name: ").strip()
    email = input("Email: ").strip()
    password = input("Password (8-12 chars, 1 uppercase letter, 1 special char): ").strip()
    phone_number = input("Phone number (10 digits): ").strip()

    await connect_to_mongo()
    try:
        admin_user = await create_admin(full_name, email, password, phone_number)
        print(f"\nAdmin account created successfully for {admin_user.email}")
    finally:
        await close_mongo_connection()


if __name__ == "__main__":
    asyncio.run(_main())