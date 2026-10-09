package com.college.sms.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/**
 * Classroom Bluetooth hashing (same rules as Python bt_hash.py).
 *
 * Teacher "broadcast" hash changes every 30 seconds:
 *   SHA-256(secret | session_id | time_slot) → first 8 hex chars
 *
 * Student proof (phone):
 *   SHA-256(broadcast_hash | roll_no | bt_device_id | session_id)
 */
public final class BluetoothHash {

    public static final int SLOT_SECONDS = 30;

    private BluetoothHash() {
    }

    public static long timeSlot(long unixSeconds) {
        return unixSeconds / SLOT_SECONDS;
    }

    public static String broadcastHash(String secret, String sessionId, long unixSeconds) {
        long slot = timeSlot(unixSeconds);
        return sha256Hex(secret + "|" + sessionId + "|" + slot).substring(0, 8).toUpperCase(Locale.ROOT);
    }

    public static boolean matchesBroadcast(String given, String secret, String sessionId, long unixSeconds) {
        if (given == null) {
            return false;
        }
        String g = given.trim().toUpperCase(Locale.ROOT);
        String next = broadcastHash(secret, sessionId, unixSeconds + SLOT_SECONDS);
        String now = broadcastHash(secret, sessionId, unixSeconds);
        String prev1 = broadcastHash(secret, sessionId, unixSeconds - SLOT_SECONDS);
        String prev2 = broadcastHash(secret, sessionId, unixSeconds - 2 * SLOT_SECONDS);
        return g.equals(now) || g.equals(prev1) || g.equals(prev2) || g.equals(next);
    }

    public static String studentProof(String broadcastHash, String rollNo, String btDeviceId, String sessionId) {
        String device = btDeviceId == null ? "" : btDeviceId.trim();
        return sha256Hex(
                broadcastHash.trim().toUpperCase(Locale.ROOT)
                        + "|" + rollNo.trim().toUpperCase(Locale.ROOT)
                        + "|" + device
                        + "|" + sessionId);
    }

    public static String sha256Hex(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
