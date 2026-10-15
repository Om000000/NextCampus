package com.college.sms.model;

/**
 * Represents an Internal Examination schedule.
 */
public class InternalExam {

    private int examId;
    private String examName;
    private String course;
    private int semester;
    private String subjectName;
    private String examDate;
    private String startTime;
    private String endTime;
    private String roomNo;
    private double maxMarks;
    private String status; // 'Upcoming', 'Ongoing', 'Completed'

    public InternalExam() {}

    public InternalExam(int examId, String examName, String course, int semester, String subjectName,
                        String examDate, String startTime, String endTime, String roomNo,
                        double maxMarks, String status) {
        this.examId = examId;
        this.examName = examName;
        this.course = course;
        this.semester = semester;
        this.subjectName = subjectName;
        this.examDate = examDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomNo = roomNo;
        this.maxMarks = maxMarks;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return subjectName + " (" + examDate + " " + startTime + ")";
    }
}
