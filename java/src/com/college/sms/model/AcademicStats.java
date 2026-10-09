package com.college.sms.model;

/**
 * Assignment & practical completion progress per student.
 */
public class AcademicStats {

    private int statId;
    private int studentId;
    private String studentName;
    private String rollNo;
    private int assignmentsCompleted;
    private int totalAssignments;
    private int practicalsCompleted;
    private int totalPracticals;
    private String remarks;
    private String updatedAt;

    public AcademicStats() {
    }

    public AcademicStats(int studentId, int assignmentsCompleted, int totalAssignments,
                         int practicalsCompleted, int totalPracticals, String remarks) {
        this.studentId = studentId;
        this.assignmentsCompleted = assignmentsCompleted;
        this.totalAssignments = totalAssignments;
        this.practicalsCompleted = practicalsCompleted;
        this.totalPracticals = totalPracticals;
        this.remarks = remarks;
    }

    public int getStatId() {
        return statId;
    }

    public void setStatId(int statId) {
        this.statId = statId;
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

    public int getAssignmentsCompleted() {
        return assignmentsCompleted;
    }

    public void setAssignmentsCompleted(int assignmentsCompleted) {
        this.assignmentsCompleted = assignmentsCompleted;
    }

    public int getTotalAssignments() {
        return totalAssignments;
    }

    public void setTotalAssignments(int totalAssignments) {
        this.totalAssignments = totalAssignments;
    }

    public int getPracticalsCompleted() {
        return practicalsCompleted;
    }

    public void setPracticalsCompleted(int practicalsCompleted) {
        this.practicalsCompleted = practicalsCompleted;
    }

    public int getTotalPracticals() {
        return totalPracticals;
    }

    public void setTotalPracticals(int totalPracticals) {
        this.totalPracticals = totalPracticals;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public double getAssignmentPercentage() {
        if (totalAssignments <= 0) return 0.0;
        return Math.round(((double) assignmentsCompleted / totalAssignments) * 1000.0) / 10.0;
    }

    public double getPracticalPercentage() {
        if (totalPracticals <= 0) return 0.0;
        return Math.round(((double) practicalsCompleted / totalPracticals) * 1000.0) / 10.0;
    }
}
