"""
Password hashing helpers using BCrypt. We never store a plain text
password anywhere, only the hash that comes out of these functions.
"""

import bcrypt

def hash_password(plain_password: str) -> str:
    # bcrypt works with bytes, so we encode the string first
    password_bytes = plain_password.encode("utf-8")
    hashed_bytes = bcrypt.hashpw(password_bytes, bcrypt.gensalt())
    return hashed_bytes.decode("utf-8")


def verify_password(plain_password: str, password_hash: str) -> bool:
    # this compares a plain password against the stored hash
    return bcrypt.checkpw(plain_password.encode("utf-8"), password_hash.encode("utf-8"))