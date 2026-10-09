package com.college.sms.model;

import java.time.LocalDate;

public class Attendance {
    private int attendanceId;
    private int studentId;
    private String studentName;
    private LocalDate attendDate;
    private String status;
    private String subjectName;

    public Attendance() {
    }

    public Attendance(int studentId, LocalDate attendDate, String status, String subjectName) {
        this.studentId = studentId;
        this.attendDate = attendDate;
        this.status = status;
        this.subjectName = subjectName;
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public LocalDate getAttendDate() {
        return attendDate;
    }

    public void setAttendDate(LocalDate attendDate) {
        this.attendDate = attendDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @Override
    public String toString() {
        return attendanceId + " | Student#" + studentId + " | " + attendDate
                + " | " + status + " | " + subjectName;
    }
}
