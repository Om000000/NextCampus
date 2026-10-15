package com.college.sms.dao;

import com.college.sms.db.DBConnection;
import com.college.sms.model.Marks;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MarksDAO {

    public boolean add(Marks m) {
        String sql = "INSERT INTO marks (student_id, subject_name, marks_obtained, max_marks, exam_type) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, m.getStudentId());
            ps.setString(2, m.getSubjectName());
            ps.setDouble(3, m.getMarksObtained());
            ps.setDouble(4, m.getMaxMarks());
            ps.setString(5, m.getExamType());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Add marks failed: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int markId) {
        String sql = "DELETE FROM marks WHERE mark_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, markId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete marks failed: " + e.getMessage());
            return false;
        }
    }

    public List<Marks> getAll() {
        List<Marks> list = new ArrayList<>();
        String sql = "SELECT m.*, s.full_name FROM marks m JOIN students s ON m.student_id = s.student_id ORDER BY m.mark_id";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("List marks failed: " + e.getMessage());
        }
        return list;
    }

    public List<Marks> getByStudent(int studentId) {
        List<Marks> list = new ArrayList<>();
        String sql = "SELECT m.*, s.full_name FROM marks m JOIN students s ON m.student_id = s.student_id WHERE m.student_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Marks by student failed: " + e.getMessage());
        }
        return list;
    }

    public List<Marks> getMarksByStudentId(int studentId) {
        return getByStudent(studentId);
    }

    private Marks mapRow(ResultSet rs) throws SQLException {
        Marks m = new Marks();
        m.setMarkId(rs.getInt("mark_id"));
        m.setStudentId(rs.getInt("student_id"));
        m.setStudentName(rs.getString("full_name"));
        m.setSubjectName(rs.getString("subject_name"));
        m.setMarksObtained(rs.getDouble("marks_obtained"));
        m.setMaxMarks(rs.getDouble("max_marks"));
        m.setExamType(rs.getString("exam_type"));
        return m;
    }
}
