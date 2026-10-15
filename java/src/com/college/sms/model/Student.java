package com.college.sms.model;

public class Student {
    private int studentId;
    private String rollNo;
    private String fullName;
    private String course;
    private int semester;
    private String email;
    private String phone;

    public Student() {
    }

    public Student(String rollNo, String fullName, String course, int semester, String email, String phone) {
        this.rollNo = rollNo;
        this.fullName = fullName;
        this.course = course;
        this.semester = semester;
        this.email = email;
        this.phone = phone;
    }

    public Student(int studentId, String rollNo, String fullName, String course, int semester, String email, String phone) {
        this.studentId = studentId;
        this.rollNo = rollNo;
        this.fullName = fullName;
        this.course = course;
        this.semester = semester;
        this.email = email;
        this.phone = phone;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return studentId + " | " + rollNo + " | " + fullName + " | " + course + " Sem-" + semester;
    }
}
