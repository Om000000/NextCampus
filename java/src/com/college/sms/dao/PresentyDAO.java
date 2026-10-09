package com.college.sms.dao;

import com.college.sms.db.DBConnection;
import com.college.sms.model.AttendanceSession;
import com.college.sms.model.PhonePresenty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PresentyDAO {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public AttendanceSession startSession(String subject, String beaconName, int minutes) {
        closeAllActive();
        AttendanceSession s = new AttendanceSession();
        s.setSessionId(UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        s.setSubjectName(subject);
        s.setBeaconName(beaconName);
        s.setSecret(UUID.randomUUID().toString().replace("-", ""));
        LocalDateTime now = LocalDateTime.now();
        s.setStartedAt(now.format(FMT));
        s.setEndsAt(now.plusMinutes(minutes).format(FMT));
        s.setActive(true);

        String sql = "INSERT INTO attendance_sessions (session_id, subject_name, beacon_name, secret, started_at, ends_at, is_active) "
                + "VALUES (?,?,?,?,?,?,1)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getSessionId());
            ps.setString(2, s.getSubjectName());
            ps.setString(3, s.getBeaconName());
            ps.setString(4, s.getSecret());
            ps.setString(5, s.getStartedAt());
            ps.setString(6, s.getEndsAt());
            ps.executeUpdate();
            return s;
        } catch (SQLException e) {
            System.err.println("Start session failed: " + e.getMessage());
            return null;
        }
    }

    public void closeAllActive() {
        String sql = "UPDATE attendance_sessions SET is_active=0 WHERE is_active=1";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Close sessions failed: " + e.getMessage());
        }
    }

    public AttendanceSession getActive() {
        String sql = "SELECT * FROM attendance_sessions WHERE is_active=1 ORDER BY started_at DESC LIMIT 1";
        AttendanceSession session = null;
        boolean isExpired = false;
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                session = mapSession(rs);
                String now = LocalDateTime.now().format(FMT);
                if (session.getEndsAt() != null && session.getEndsAt().compareTo(now) < 0) {
                    isExpired = true;
                    session = null;
                }
            }
        } catch (SQLException e) {
            System.err.println("Get active session failed: " + e.getMessage());
        }

        if (isExpired) {
            expireOld();
        }
        return session;
    }

    public List<PhonePresenty> listForSession(String sessionId) {
        List<PhonePresenty> list = new ArrayList<>();
        String sql = "SELECT p.*, s.full_name FROM phone_presenty p "
                + "JOIN students s ON p.student_id = s.student_id "
                + "WHERE p.session_id=? ORDER BY p.marked_at";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PhonePresenty p = new PhonePresenty();
                    p.setPresentyId(rs.getInt("presenty_id"));
                    p.setSessionId(rs.getString("session_id"));
                    p.setStudentId(rs.getInt("student_id"));
                    p.setRollNo(rs.getString("roll_no"));
                    p.setStudentName(rs.getString("full_name"));
                    p.setBtDeviceName(rs.getString("bt_device_name"));
                    p.setBluetoothHash(rs.getString("bluetooth_hash"));
                    p.setVerifyMethod(rs.getString("verify_method"));
                    p.setMarkedAt(rs.getString("marked_at"));
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("List presenty failed: " + e.getMessage());
        }
        return list;
    }

    private void expireOld() {
        String now = LocalDateTime.now().format(FMT);
        String sql = "UPDATE attendance_sessions SET is_active=0 WHERE is_active=1 AND ends_at < ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, now);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Expire sessions failed: " + e.getMessage());
        }
    }

    private AttendanceSession mapSession(ResultSet rs) throws SQLException {
        AttendanceSession s = new AttendanceSession();
        s.setSessionId(rs.getString("session_id"));
        s.setSubjectName(rs.getString("subject_name"));
        s.setBeaconName(rs.getString("beacon_name"));
        s.setSecret(rs.getString("secret"));
        s.setStartedAt(rs.getString("started_at"));
        s.setEndsAt(rs.getString("ends_at"));
        s.setActive(rs.getInt("is_active") == 1);
        return s;
    }

    public boolean resetDeviceBinding(String rollNo) {
        String cleanRoll = rollNo.trim().toUpperCase();
        try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM student_devices WHERE upper(roll_no) = ?")) {
                ps.setString(1, cleanRoll);
                ps.executeUpdate();
            }
            try (PreparedStatement ps2 = con.prepareStatement("UPDATE phone_presenty SET bt_device_id = NULL WHERE upper(roll_no) = ?")) {
                ps2.setString(1, cleanRoll);
                ps2.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Reset device binding failed: " + e.getMessage());
            return false;
        }
    }

    public boolean resetAllDeviceBindings() {
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            st.executeUpdate("DELETE FROM student_devices");
            st.executeUpdate("UPDATE phone_presenty SET bt_device_id = NULL");
            return true;
        } catch (SQLException e) {
            System.err.println("Reset all device bindings failed: " + e.getMessage());
            return false;
        }
    }
}
