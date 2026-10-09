package com.college.sms.model;

public class Marks {
    private int markId;
    private int studentId;
    private String studentName;
    private String subjectName;
    private double marksObtained;
    private double maxMarks;
    private String examType;

    public Marks() {
    }

    public Marks(int studentId, String subjectName, double marksObtained, double maxMarks, String examType) {
        this.studentId = studentId;
        this.subjectName = subjectName;
        this.marksObtained = marksObtained;
        this.maxMarks = maxMarks;
        this.examType = examType;
    }

    public int getMarkId() {
        return markId;
    }

    public void setMarkId(int markId) {
        this.markId = markId;
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

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public double getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(double marksObtained) {
        this.marksObtained = marksObtained;
    }

    public double getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(double maxMarks) {
        this.maxMarks = maxMarks;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public double getPercentage() {
        if (maxMarks == 0) {
            return 0;
        }
        return (marksObtained / maxMarks) * 100.0;
    }

    @Override
    public String toString() {
        return markId + " | Student#" + studentId + " | " + subjectName + " | "
                + marksObtained + "/" + maxMarks + " | " + examType;
    }
}
