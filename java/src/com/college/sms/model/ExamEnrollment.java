package com.college.sms.model;

/**
 * Represents a student's internal exam form enrollment, status check, and allocated seat number.
 */
public class ExamEnrollment {

    private int enrollmentId;
    private int examId;
    private String examName;
    private String subjectName;
    private String examDate;
    private String startTime;
    private String endTime;
    private String roomNo;
    private double maxMarks;

    private int studentId;
    private String studentName;
    private String rollNo;
    private String course;
    private int semester;

    private String enrollmentDate;
    private String formStatus; // 'Pending', 'Approved', 'Rejected'
    private String seatNumber; // e.g. 'BBA5-S101'
    private String hallTicketStatus; // 'Generated', 'Not Generated'
    private String remarks;

    public ExamEnrollment() {}

    public ExamEnrollment(int enrollmentId, int examId, String examName, String subjectName,
                          String examDate, String startTime, String endTime, String roomNo,
                          double maxMarks, int studentId, String studentName, String rollNo,
                          String course, int semester, String enrollmentDate, String formStatus,
                          String seatNumber, String hallTicketStatus, String remarks) {
        this.enrollmentId = enrollmentId;
        this.examId = examId;
        this.examName = examName;
        this.subjectName = subjectName;
        this.examDate = examDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomNo = roomNo;
        this.maxMarks = maxMarks;
        this.studentId = studentId;
        this.studentName = studentName;
        this.rollNo = rollNo;
        this.course = course;
        this.semester = semester;
        this.enrollmentDate = enrollmentDate;
        this.formStatus = formStatus;
        this.seatNumber = seatNumber;
        this.hallTicketStatus = hallTicketStatus;
        this.remarks = remarks;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getExamDate() {
        return examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(double maxMarks) {
        this.maxMarks = maxMarks;
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

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
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

    public String getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(String enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getFormStatus() {
        return formStatus;
    }

    public void setFormStatus(String formStatus) {
        this.formStatus = formStatus;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getHallTicketStatus() {
        return hallTicketStatus;
    }

    public void setHallTicketStatus(String hallTicketStatus) {
        this.hallTicketStatus = hallTicketStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
