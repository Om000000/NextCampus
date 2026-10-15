package com.college.sms.model;

/**
 * System user for Role-Based Access Control.
 * Roles: HOD (Admin), TEACHER, LIBRARIAN, STUDENT.
 */
public class User {

    private int userId;
    private String username;
    private String password;
    private String role; // HOD, TEACHER, LIBRARIAN, STUDENT
    private String fullName;
    private String email;
    private Integer studentId;

    public User() {}

    public User(int userId, String username, String password, String role, String fullName, String email, Integer studentId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role != null ? role.toUpperCase() : "STUDENT";
        this.fullName = fullName;
        this.email = email;
        this.studentId = studentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role != null ? role.toUpperCase() : "STUDENT";
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public boolean isHOD() {
        return "HOD".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role);
    }

    public boolean isTeacher() {
        return "TEACHER".equalsIgnoreCase(role);
    }

    public boolean isLibrarian() {
        return "LIBRARIAN".equalsIgnoreCase(role);
    }

    public boolean isStudent() {
        return "STUDENT".equalsIgnoreCase(role);
    }

    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}
