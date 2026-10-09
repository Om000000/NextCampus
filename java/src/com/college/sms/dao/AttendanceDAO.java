package com.college.sms.dao;

import com.college.sms.db.DBConnection;
import com.college.sms.model.Attendance;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AttendanceDAO {

    public boolean add(Attendance a) {
        String sql = "INSERT INTO attendance (student_id, attend_date, status, subject_name) VALUES (?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, a.getStudentId());
            ps.setDate(2, Date.valueOf(a.getAttendDate()));
            ps.setString(3, a.getStatus());
            ps.setString(4, a.getSubjectName());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Add attendance failed: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int attendanceId) {
        String sql = "DELETE FROM attendance WHERE attendance_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, attendanceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete attendance failed: " + e.getMessage());
            return false;
        }
    }

    public List<Attendance> getAll() {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, s.full_name FROM attendance a JOIN students s ON a.student_id = s.student_id ORDER BY a.attend_date DESC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("List attendance failed: " + e.getMessage());
        }
        return list;
    }

    public List<Attendance> getByStudent(int studentId) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.*, s.full_name FROM attendance a JOIN students s ON a.student_id = s.student_id WHERE a.student_id=? ORDER BY a.attend_date DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Attendance by student failed: " + e.getMessage());
        }
        return list;
    }

    private Attendance mapRow(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setAttendanceId(rs.getInt("attendance_id"));
        a.setStudentId(rs.getInt("student_id"));
        a.setStudentName(rs.getString("full_name"));
        try {
            Date d = rs.getDate("attend_date");
            if (d != null) {
                a.setAttendDate(d.toLocalDate());
            }
        } catch (SQLException ex) {
            String raw = rs.getString("attend_date");
            if (raw != null && !raw.isEmpty()) {
                a.setAttendDate(java.time.LocalDate.parse(raw.substring(0, 10)));
            }
        }
        a.setStatus(rs.getString("status"));
        a.setSubjectName(rs.getString("subject_name"));
        return a;
    }
}
