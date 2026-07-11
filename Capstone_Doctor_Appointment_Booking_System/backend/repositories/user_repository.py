"""
Repository layer for the User document.
"""

from typing import Optional

from beanie import PydanticObjectId

from backend.models.user import User


class UserRepository:

    async def find_by_email(self, email: str) -> Optional[User]:
        return await User.find_one(User.email == email)

    async def get_by_id(self, user_id: PydanticObjectId) -> Optional[User]:
        return await User.get(user_id)

    async def insert(self, user: User) -> User:
        await user.insert()
        return user

    async def save(self, user: User) -> User:
        await user.save()
        return user


def get_user_repository() -> UserRepository:
    return UserRepository()