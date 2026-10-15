package com.college.sms.dao;

import com.college.sms.db.DBConnection;
import com.college.sms.model.AcademicStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AcademicStatsDAO {

    public List<AcademicStats> getAll() {
        List<AcademicStats> list = new ArrayList<>();
        String sql = "SELECT s.student_id, s.roll_no, s.full_name, "
                + "COALESCE(a.stat_id, 0) AS stat_id, "
                + "COALESCE(a.assignments_completed, 0) AS assignments_completed, "
                + "COALESCE(a.total_assignments, 5) AS total_assignments, "
                + "COALESCE(a.practicals_completed, 0) AS practicals_completed, "
                + "COALESCE(a.total_practicals, 10) AS total_practicals, "
                + "COALESCE(a.remarks, 'In Progress') AS remarks, "
                + "COALESCE(a.updated_at, '') AS updated_at "
                + "FROM students s "
                + "LEFT JOIN student_academic_stats a ON s.student_id = a.student_id "
                + "ORDER BY s.roll_no";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                AcademicStats a = new AcademicStats();
                a.setStudentId(rs.getInt("student_id"));
                a.setRollNo(rs.getString("roll_no"));
                a.setStudentName(rs.getString("full_name"));
                a.setStatId(rs.getInt("stat_id"));
                a.setAssignmentsCompleted(rs.getInt("assignments_completed"));
                a.setTotalAssignments(rs.getInt("total_assignments"));
                a.setPracticalsCompleted(rs.getInt("practicals_completed"));
                a.setTotalPracticals(rs.getInt("total_practicals"));
                a.setRemarks(rs.getString("remarks"));
                a.setUpdatedAt(rs.getString("updated_at"));
                list.add(a);
            }
        } catch (SQLException e) {
            System.err.println("Get all academic stats failed: " + e.getMessage());
        }
        return list;
    }

    public boolean saveOrUpdate(AcademicStats stats) {
        String sql = "INSERT INTO student_academic_stats "
                + "(student_id, assignments_completed, total_assignments, practicals_completed, total_practicals, remarks, updated_at) "
                + "VALUES (?, ?, ?, ?, ?, ?, datetime('now')) "
                + "ON CONFLICT(student_id) DO UPDATE SET "
                + "assignments_completed = excluded.assignments_completed, "
                + "total_assignments = excluded.total_assignments, "
                + "practicals_completed = excluded.practicals_completed, "
                + "total_practicals = excluded.total_practicals, "
                + "remarks = excluded.remarks, "
                + "updated_at = datetime('now')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, stats.getStudentId());
            ps.setInt(2, stats.getAssignmentsCompleted());
            ps.setInt(3, stats.getTotalAssignments());
            ps.setInt(4, stats.getPracticalsCompleted());
            ps.setInt(5, stats.getTotalPracticals());
            ps.setString(6, stats.getRemarks());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Save academic stats failed: " + e.getMessage());
            return false;
        }
    }

    public AcademicStats getStatsByStudentId(int studentId) {
        String sql = "SELECT s.student_id, s.roll_no, s.full_name, "
                + "COALESCE(a.stat_id, 0) AS stat_id, "
                + "COALESCE(a.assignments_completed, 0) AS assignments_completed, "
                + "COALESCE(a.total_assignments, 5) AS total_assignments, "
                + "COALESCE(a.practicals_completed, 0) AS practicals_completed, "
                + "COALESCE(a.total_practicals, 10) AS total_practicals, "
                + "COALESCE(a.remarks, 'In Progress') AS remarks, "
                + "COALESCE(a.updated_at, '') AS updated_at "
                + "FROM students s "
                + "LEFT JOIN student_academic_stats a ON s.student_id = a.student_id "
                + "WHERE s.student_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AcademicStats a = new AcademicStats();
                    a.setStudentId(rs.getInt("student_id"));
                    a.setRollNo(rs.getString("roll_no"));
                    a.setStudentName(rs.getString("full_name"));
                    a.setStatId(rs.getInt("stat_id"));
                    a.setAssignmentsCompleted(rs.getInt("assignments_completed"));
                    a.setTotalAssignments(rs.getInt("total_assignments"));
                    a.setPracticalsCompleted(rs.getInt("practicals_completed"));
                    a.setTotalPracticals(rs.getInt("total_practicals"));
                    a.setRemarks(rs.getString("remarks"));
                    a.setUpdatedAt(rs.getString("updated_at"));
                    return a;
                }
            }
        } catch (SQLException e) {
            System.err.println("Get academic stats by student id failed: " + e.getMessage());
        }
        return null;
    }

    public String getActiveNotice() {
        String sql = "SELECT notice_text FROM classroom_notices WHERE is_active=1 ORDER BY posted_at DESC LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getString("notice_text");
            }
        } catch (SQLException e) {
            System.err.println("Get active notice failed: " + e.getMessage());
        }
        return "Welcome to class! Submit assignments on time.";
    }

    public boolean setNotice(String noticeText, String postedBy) {
        String deactivate = "UPDATE classroom_notices SET is_active=0 WHERE is_active=1";
        String insert = "INSERT INTO classroom_notices (notice_text, posted_by, posted_at, is_active) VALUES (?, ?, datetime('now'), 1)";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (Statement st = con.createStatement();
                 PreparedStatement ps = con.prepareStatement(insert)) {
                st.executeUpdate(deactivate);
                ps.setString(1, noticeText.trim());
                ps.setString(2, postedBy != null ? postedBy.trim() : "Teacher");
                ps.executeUpdate();
                con.commit();
                return true;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Set notice failed: " + e.getMessage());
            return false;
        }
    }
}
