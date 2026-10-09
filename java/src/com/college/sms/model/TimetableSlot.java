package com.college.sms.model;

/**
 * College timetable slot for lectures.
 */
public class TimetableSlot {

    private int timetableId;
    private String dayOfWeek;
    private int lectureNumber;
    private String startTime;
    private String endTime;
    private String subjectName;
    private String teacherName;
    private String roomNo;
    private String lectureType;

    public TimetableSlot() {
    }

    public TimetableSlot(String dayOfWeek, int lectureNumber, String startTime, String endTime,
                         String subjectName, String teacherName, String roomNo, String lectureType) {
        this.dayOfWeek = dayOfWeek;
        this.lectureNumber = lectureNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subjectName = subjectName;
        this.teacherName = teacherName;
        this.roomNo = roomNo;
        this.lectureType = lectureType;
    }

    public int getTimetableId() {
        return timetableId;
    }

    public void setTimetableId(int timetableId) {
        this.timetableId = timetableId;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getLectureNumber() {
        return lectureNumber;
    }

    public void setLectureNumber(int lectureNumber) {
        this.lectureNumber = lectureNumber;
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

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getLectureType() {
        return lectureType;
    }

    public void setLectureType(String lectureType) {
        this.lectureType = lectureType;
    }

    public String getTimeSlot() {
        return startTime + " - " + endTime;
    }

    @Override
    public String toString() {
        return "Lec " + lectureNumber + " (" + getTimeSlot() + "): " + subjectName;
    }
}
