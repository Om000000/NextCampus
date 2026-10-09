package com.college.sms.dao;

import com.college.sms.db.DBConnection;
import com.college.sms.model.LectureAttendance;
import com.college.sms.model.TimetableSlot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TimetableDAO {

    public List<TimetableSlot> getSlots(String dayFilter) {
        List<TimetableSlot> list = new ArrayList<>();
        String sql;
        boolean hasFilter = dayFilter != null && !dayFilter.trim().isEmpty() && !dayFilter.equalsIgnoreCase("All Days");

        if (hasFilter) {
            sql = "SELECT * FROM timetable WHERE upper(day_of_week) = upper(?) ORDER BY lecture_number";
        } else {
            sql = "SELECT * FROM timetable ORDER BY "
                    + "CASE upper(day_of_week) "
                    + "WHEN 'MONDAY' THEN 1 "
                    + "WHEN 'TUESDAY' THEN 2 "
                    + "WHEN 'WEDNESDAY' THEN 3 "
                    + "WHEN 'THURSDAY' THEN 4 "
                    + "WHEN 'FRIDAY' THEN 5 "
                    + "WHEN 'SATURDAY' THEN 6 "
                    + "ELSE 7 END, lecture_number";
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (hasFilter) {
                ps.setString(1, dayFilter.trim());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TimetableSlot s = new TimetableSlot();
                    s.setTimetableId(rs.getInt("timetable_id"));
                    s.setDayOfWeek(rs.getString("day_of_week"));
                    s.setLectureNumber(rs.getInt("lecture_number"));
                    s.setStartTime(rs.getString("start_time"));
                    s.setEndTime(rs.getString("end_time"));
                    s.setSubjectName(rs.getString("subject_name"));
                    s.setTeacherName(rs.getString("teacher_name"));
                    s.setRoomNo(rs.getString("room_no"));
                    s.setLectureType(rs.getString("lecture_type"));
                    list.add(s);
                }
            }
        } catch (SQLException e) {
            System.err.println("Get timetable slots failed: " + e.getMessage());
        }
        return list;
    }

    public boolean addSlot(TimetableSlot s) {
        String sql = "INSERT INTO timetable (day_of_week, lecture_number, start_time, end_time, subject_name, teacher_name, room_no, lecture_type) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getDayOfWeek());
            ps.setInt(2, s.getLectureNumber());
            ps.setString(3, s.getStartTime());
            ps.setString(4, s.getEndTime());
            ps.setString(5, s.getSubjectName());
            ps.setString(6, s.getTeacherName());
            ps.setString(7, s.getRoomNo());
            ps.setString(8, s.getLectureType());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Add timetable slot failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateSlot(TimetableSlot s) {
        String sql = "UPDATE timetable SET day_of_week=?, lecture_number=?, start_time=?, end_time=?, "
                + "subject_name=?, teacher_name=?, room_no=?, lecture_type=? WHERE timetable_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getDayOfWeek());
            ps.setInt(2, s.getLectureNumber());
            ps.setString(3, s.getStartTime());
            ps.setString(4, s.getEndTime());
            ps.setString(5, s.getSubjectName());
            ps.setString(6, s.getTeacherName());
            ps.setString(7, s.getRoomNo());
            ps.setString(8, s.getLectureType());
            ps.setInt(9, s.getTimetableId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update timetable slot failed: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteSlot(int timetableId) {
        String sql = "DELETE FROM timetable WHERE timetable_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, timetableId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete timetable slot failed: " + e.getMessage());
            return false;
        }
    }

    public List<LectureAttendance> getLectureAttendance(String dateStr, String subjectName) {
        List<LectureAttendance> list = new ArrayList<>();
        String sql = "SELECT s.student_id, s.roll_no, s.full_name, "
                + "COALESCE(att.status, 'Absent') AS status, "
                + "COALESCE(att.attendance_id, 0) AS attendance_id, "
                + "COALESCE(pp.verify_method, CASE WHEN att.status IS NOT NULL THEN 'Manual' ELSE '-' END) AS verify_method, "
                + "COALESCE(pp.marked_at, '') AS marked_at "
                + "FROM students s "
                + "LEFT JOIN ("
                + "    SELECT student_id, MAX(attendance_id) AS attendance_id, status "
                + "    FROM attendance "
                + "    WHERE attend_date = ? AND upper(subject_name) = upper(?) "
                + "    GROUP BY student_id"
                + ") att ON s.student_id = att.student_id "
                + "LEFT JOIN ("
                + "    SELECT student_id, MAX(marked_at) AS marked_at, verify_method "
                + "    FROM phone_presenty "
                + "    WHERE date(marked_at) = ? "
                + "    GROUP BY student_id"
                + ") pp ON s.student_id = pp.student_id "
                + "ORDER BY s.roll_no";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dateStr.trim());
            ps.setString(2, subjectName.trim());
            ps.setString(3, dateStr.trim());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LectureAttendance la = new LectureAttendance();
                    la.setStudentId(rs.getInt("student_id"));
                    la.setRollNo(rs.getString("roll_no"));
                    la.setStudentName(rs.getString("full_name"));
                    la.setStatus(rs.getString("status"));
                    la.setAttendanceId(rs.getInt("attendance_id"));
                    la.setAttendDate(dateStr.trim());
                    la.setSubjectName(subjectName.trim());
                    la.setVerifyMethod(rs.getString("verify_method"));
                    la.setMarkedAt(rs.getString("marked_at"));
                    list.add(la);
                }
            }
        } catch (SQLException e) {
            System.err.println("Get lecture attendance failed: " + e.getMessage());
        }
        return list;
    }

    public int[] getLectureAttendanceSummary(String dateStr, String subjectName) {
        int total = 0;
        int present = 0;
        int absent = 0;
        for (LectureAttendance la : getLectureAttendance(dateStr, subjectName)) {
            total++;
            if ("Present".equalsIgnoreCase(la.getStatus()) || "Late".equalsIgnoreCase(la.getStatus())) {
                present++;
            } else {
                absent++;
            }
        }
        return new int[]{total, present, absent};
    }

    public boolean setStudentAttendance(int studentId, String dateStr, String subjectName, String status) {
        try (Connection con = DBConnection.getConnection()) {
            if ("Absent".equalsIgnoreCase(status)) {
                String del = "DELETE FROM attendance WHERE student_id=? AND attend_date=? AND upper(subject_name)=upper(?)";
                try (PreparedStatement ps = con.prepareStatement(del)) {
                    ps.setInt(1, studentId);
                    ps.setString(2, dateStr.trim());
                    ps.setString(3, subjectName.trim());
                    ps.executeUpdate();
                    return true;
                }
            } else {
                String check = "SELECT attendance_id FROM attendance WHERE student_id=? AND attend_date=? AND upper(subject_name)=upper(?)";
                try (PreparedStatement ps = con.prepareStatement(check)) {
                    ps.setInt(1, studentId);
                    ps.setString(2, dateStr.trim());
                    ps.setString(3, subjectName.trim());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int aid = rs.getInt(1);
                            String upd = "UPDATE attendance SET status=? WHERE attendance_id=?";
                            try (PreparedStatement ps2 = con.prepareStatement(upd)) {
                                ps2.setString(1, status.trim());
                                ps2.setInt(2, aid);
                                return ps2.executeUpdate() > 0;
                            }
                        } else {
                            String ins = "INSERT INTO attendance (student_id, attend_date, status, subject_name) VALUES (?, ?, ?, ?)";
                            try (PreparedStatement ps2 = con.prepareStatement(ins)) {
                                ps2.setInt(1, studentId);
                                ps2.setString(2, dateStr.trim());
                                ps2.setString(3, status.trim());
                                ps2.setString(4, subjectName.trim());
                                return ps2.executeUpdate() > 0;
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Set student attendance failed: " + e.getMessage());
            return false;
        }
    }

    public boolean markAllPresent(String dateStr, String subjectName) {
        List<LectureAttendance> list = getLectureAttendance(dateStr, subjectName);
        for (LectureAttendance la : list) {
            setStudentAttendance(la.getStudentId(), dateStr, subjectName, "Present");
        }
        return true;
    }
}
