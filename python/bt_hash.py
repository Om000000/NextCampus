"""Bluetooth classroom hashing — must match Java BluetoothHash.java."""

import hashlib

SLOT_SECONDS = 30


def time_slot(unix_seconds: int) -> int:
    return int(unix_seconds) // SLOT_SECONDS


def sha256_hex(text: str) -> str:
    return hashlib.sha256(text.encode("utf-8")).hexdigest()


def broadcast_hash(secret: str, session_id: str, unix_seconds: int) -> str:
    slot = time_slot(unix_seconds)
    return sha256_hex(f"{secret}|{session_id}|{slot}")[:8].upper()


def matches_broadcast(given: str, secret: str, session_id: str, unix_seconds: int) -> bool:
    if not given:
        return False
    g = given.strip().upper()
    valid = [
        broadcast_hash(secret, session_id, unix_seconds + SLOT_SECONDS),      # next slot (+30s drift)
        broadcast_hash(secret, session_id, unix_seconds),                     # current slot
        broadcast_hash(secret, session_id, unix_seconds - SLOT_SECONDS),      # previous slot (-30s)
        broadcast_hash(secret, session_id, unix_seconds - 2 * SLOT_SECONDS),  # 2 slots ago (-60s)
    ]
    return g in valid


def student_proof(broadcast: str, roll_no: str, bt_device_id: str, session_id: str) -> str:
    device = (bt_device_id or "").strip()
    return sha256_hex(
        f"{broadcast.strip().upper()}|{roll_no.strip().upper()}|{device}|{session_id}"
    )
