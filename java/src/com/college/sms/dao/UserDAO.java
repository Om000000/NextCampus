package com.college.sms.dao;

import com.college.sms.db.DBConnection;
import com.college.sms.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for User authentication and role-based accounts.
 */
public class UserDAO {

    public User authenticate(String username, String password) {
        if (username == null || password == null) return null;
        String sql = "SELECT user_id, username, password, role, full_name, email, student_id FROM users "
                   + "WHERE lower(trim(username)) = lower(trim(?)) AND password = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT user_id, username, password, role, full_name, email, student_id FROM users ORDER BY role, username";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password, role, full_name, email, student_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getUsername().trim());
            ps.setString(2, user.getPassword().trim());
            ps.setString(3, user.getRole().toUpperCase().trim());
            ps.setString(4, user.getFullName().trim());
            ps.setString(5, user.getEmail() != null ? user.getEmail().trim() : null);
            if (user.getStudentId() != null) {
                ps.setInt(6, user.getStudentId());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username=?, password=?, role=?, full_name=?, email=?, student_id=? WHERE user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getUsername().trim());
            ps.setString(2, user.getPassword().trim());
            ps.setString(3, user.getRole().toUpperCase().trim());
            ps.setString(4, user.getFullName().trim());
            ps.setString(5, user.getEmail() != null ? user.getEmail().trim() : null);
            if (user.getStudentId() != null) {
                ps.setInt(6, user.getStudentId());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            ps.setInt(7, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT user_id, username, password, role, full_name, email, student_id FROM users "
                   + "WHERE lower(trim(username)) = lower(trim(?))";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User mapRow(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String uname = rs.getString("username");
        String pass = rs.getString("password");
        String role = rs.getString("role");
        String fullName = rs.getString("full_name");
        String email = rs.getString("email");
        int sid = rs.getInt("student_id");
        Integer studentId = rs.wasNull() ? null : sid;
        return new User(userId, uname, pass, role, fullName, email, studentId);
    }

    /**
     * Returns the list of registered teacher names from the software users table.
     */
    public List<String> getTeacherNames() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT full_name FROM users WHERE upper(role) IN ('TEACHER', 'HOD', 'ADMIN') ORDER BY full_name";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                String name = rs.getString("full_name");
                if (name != null && !name.trim().isEmpty() && !list.contains(name.trim())) {
                    list.add(name.trim());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (list.isEmpty()) {
            list.add("Prof. D.S.Morde");
            list.add("Prof. A.S.Tanpure (HOD)");
        }
        return list;
    }
}
