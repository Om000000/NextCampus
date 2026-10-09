package com.college.sms.model;

public class PhonePresenty {
    private int presentyId;
    private String sessionId;
    private int studentId;
    private String rollNo;
    private String studentName;
    private String btDeviceName;
    private String bluetoothHash;
    private String verifyMethod;
    private String markedAt;

    public int getPresentyId() {
        return presentyId;
    }

    public void setPresentyId(int presentyId) {
        this.presentyId = presentyId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getBtDeviceName() {
        return btDeviceName;
    }

    public void setBtDeviceName(String btDeviceName) {
        this.btDeviceName = btDeviceName;
    }

    public String getBluetoothHash() {
        return bluetoothHash;
    }

    public void setBluetoothHash(String bluetoothHash) {
        this.bluetoothHash = bluetoothHash;
    }

    public String getVerifyMethod() {
        return verifyMethod;
    }

    public void setVerifyMethod(String verifyMethod) {
        this.verifyMethod = verifyMethod;
    }

    public String getMarkedAt() {
        return markedAt;
    }

    public void setMarkedAt(String markedAt) {
        this.markedAt = markedAt;
    }
}
