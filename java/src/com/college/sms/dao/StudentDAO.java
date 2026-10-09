package com.college.sms.dao;

import com.college.sms.db.DBConnection;
import com.college.sms.model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public boolean add(Student s) {
        String sql = "INSERT INTO students (roll_no, full_name, course, semester, email, phone) VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getRollNo());
            ps.setString(2, s.getFullName());
            ps.setString(3, s.getCourse());
            ps.setInt(4, s.getSemester());
            ps.setString(5, s.getEmail());
            ps.setString(6, s.getPhone());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Add student failed: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Student s) {
        String sql = "UPDATE students SET roll_no=?, full_name=?, course=?, semester=?, email=?, phone=? WHERE student_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getRollNo());
            ps.setString(2, s.getFullName());
            ps.setString(3, s.getCourse());
            ps.setInt(4, s.getSemester());
            ps.setString(5, s.getEmail());
            ps.setString(6, s.getPhone());
            ps.setInt(7, s.getStudentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update student failed: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(int studentId) {
        String sql = "DELETE FROM students WHERE student_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete student failed: " + e.getMessage());
            return false;
        }
    }

    public Student findById(int studentId) {
        String sql = "SELECT * FROM students WHERE student_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Find student failed: " + e.getMessage());
        }
        return null;
    }

    public List<Student> findByName(String namePart) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE full_name LIKE ? ORDER BY student_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "%" + namePart + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Search student failed: " + e.getMessage());
        }
        return list;
    }

    public Student findByRollNo(String rollNo) {
        String sql = "SELECT * FROM students WHERE roll_no=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Find by roll no failed: " + e.getMessage());
        }
        return null;
    }

    public List<Student> getAll() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY student_id";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("List students failed: " + e.getMessage());
        }
        return list;
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setRollNo(rs.getString("roll_no"));
        s.setFullName(rs.getString("full_name"));
        s.setCourse(rs.getString("course"));
        s.setSemester(rs.getInt("semester"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        return s;
    }
}
