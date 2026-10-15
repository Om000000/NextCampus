package com.college.sms.dao;

import com.college.sms.db.DBConnection;
import com.college.sms.model.ExamEnrollment;
import com.college.sms.model.InternalExam;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for Internal Exam Scheduling, Form Enrollment, and Seat Number Allocation.
 */
public class ExamDAO {

    public List<InternalExam> getAllExams() {
        List<InternalExam> list = new ArrayList<>();
        String sql = "SELECT exam_id, exam_name, course, semester, subject_name, exam_date, start_time, end_time, room_no, max_marks, status "
                   + "FROM internal_exams ORDER BY exam_date ASC, start_time ASC";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapExam(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<InternalExam> getExamsBySemester(int semester) {
        List<InternalExam> list = new ArrayList<>();
        String sql = "SELECT exam_id, exam_name, course, semester, subject_name, exam_date, start_time, end_time, room_no, max_marks, status "
                   + "FROM internal_exams WHERE semester = ? ORDER BY exam_date ASC, start_time ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, semester);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapExam(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addExam(InternalExam exam) {
        String sql = "INSERT INTO internal_exams (exam_name, course, semester, subject_name, exam_date, start_time, end_time, room_no, max_marks, status) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, exam.getExamName());
            ps.setString(2, exam.getCourse());
            ps.setInt(3, exam.getSemester());
            ps.setString(4, exam.getSubjectName());
            ps.setString(5, exam.getExamDate());
            ps.setString(6, exam.getStartTime());
            ps.setString(7, exam.getEndTime());
            ps.setString(8, exam.getRoomNo());
            ps.setDouble(9, exam.getMaxMarks());
            ps.setString(10, exam.getStatus() != null ? exam.getStatus() : "Upcoming");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateExam(InternalExam exam) {
        String sql = "UPDATE internal_exams SET exam_name=?, course=?, semester=?, subject_name=?, exam_date=?, "
                   + "start_time=?, end_time=?, room_no=?, max_marks=?, status=? WHERE exam_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, exam.getExamName());
            ps.setString(2, exam.getCourse());
            ps.setInt(3, exam.getSemester());
            ps.setString(4, exam.getSubjectName());
            ps.setString(5, exam.getExamDate());
            ps.setString(6, exam.getStartTime());
            ps.setString(7, exam.getEndTime());
            ps.setString(8, exam.getRoomNo());
            ps.setDouble(9, exam.getMaxMarks());
            ps.setString(10, exam.getStatus() != null ? exam.getStatus() : "Upcoming");
            ps.setInt(11, exam.getExamId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteExam(int examId) {
        String sql = "DELETE FROM internal_exams WHERE exam_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, examId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ExamEnrollment> getAllEnrollments() {
        return queryEnrollments(
                "SELECT ee.*, ie.exam_name, ie.subject_name, ie.exam_date, ie.start_time, ie.end_time, "
                + "ie.room_no, ie.max_marks, s.full_name as student_name, s.course, s.semester "
                + "FROM exam_enrollments ee "
                + "JOIN internal_exams ie ON ee.exam_id = ie.exam_id "
                + "JOIN students s ON ee.student_id = s.student_id "
                + "ORDER BY ie.exam_date ASC, ee.seat_number ASC, s.roll_no ASC"
        );
    }

    public List<ExamEnrollment> getEnrollmentsByExam(int examId) {
        String sql = "SELECT ee.*, ie.exam_name, ie.subject_name, ie.exam_date, ie.start_time, ie.end_time, "
                   + "ie.room_no, ie.max_marks, s.full_name as student_name, s.course, s.semester "
                   + "FROM exam_enrollments ee "
                   + "JOIN internal_exams ie ON ee.exam_id = ie.exam_id "
                   + "JOIN students s ON ee.student_id = s.student_id "
                   + "WHERE ee.exam_id = ? "
                   + "ORDER BY ee.seat_number ASC, s.roll_no ASC";
        List<ExamEnrollment> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEnrollment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ExamEnrollment> getStudentEnrollments(int studentId) {
        String sql = "SELECT ee.*, ie.exam_name, ie.subject_name, ie.exam_date, ie.start_time, ie.end_time, "
                   + "ie.room_no, ie.max_marks, s.full_name as student_name, s.course, s.semester "
                   + "FROM exam_enrollments ee "
                   + "JOIN internal_exams ie ON ee.exam_id = ie.exam_id "
                   + "JOIN students s ON ee.student_id = s.student_id "
                   + "WHERE ee.student_id = ? "
                   + "ORDER BY ie.exam_date ASC";
        List<ExamEnrollment> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEnrollment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ExamEnrollment> getStudentEnrollmentsByRoll(String rollNo) {
        String sql = "SELECT ee.*, ie.exam_name, ie.subject_name, ie.exam_date, ie.start_time, ie.end_time, "
                   + "ie.room_no, ie.max_marks, s.full_name as student_name, s.course, s.semester "
                   + "FROM exam_enrollments ee "
                   + "JOIN internal_exams ie ON ee.exam_id = ie.exam_id "
                   + "JOIN students s ON ee.student_id = s.student_id "
                   + "WHERE lower(trim(ee.roll_no)) = lower(trim(?)) "
                   + "ORDER BY ie.exam_date ASC";
        List<ExamEnrollment> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEnrollment(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean enrollStudent(int examId, int studentId, String rollNo, String remarks) {
        String sql = "INSERT INTO exam_enrollments (exam_id, student_id, roll_no, enrollment_date, form_status, hall_ticket_status, remarks) "
                   + "VALUES (?, ?, ?, datetime('now'), 'Pending', 'Not Generated', ?) "
                   + "ON CONFLICT(exam_id, student_id) DO UPDATE SET form_status='Pending', enrollment_date=datetime('now'), remarks=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ps.setInt(2, studentId);
            ps.setString(3, rollNo);
            ps.setString(4, remarks != null ? remarks : "Enrolled online");
            ps.setString(5, remarks != null ? remarks : "Enrolled online");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEnrollmentStatus(int enrollmentId, String formStatus, String seatNumber, String remarks) {
        String sql = "UPDATE exam_enrollments SET form_status=?, seat_number=?, hall_ticket_status=?, remarks=? "
                   + "WHERE enrollment_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, formStatus);
            ps.setString(2, seatNumber);
            String ht = "Approved".equalsIgnoreCase(formStatus) ? "Generated" : "Not Generated";
            ps.setString(3, ht);
            ps.setString(4, remarks);
            ps.setInt(5, enrollmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Automatically assigns sequential seat numbers to all enrolled students for this exam.
     * Example: BBA5-S101, BBA5-S102, BBA5-S103...
     */
    public synchronized int autoAllocateSeatNumbers(int examId, String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            prefix = "BBA5-S";
        }
        String selectSql = "SELECT enrollment_id FROM exam_enrollments WHERE exam_id = ? ORDER BY roll_no ASC";
        String updateSql = "UPDATE exam_enrollments SET seat_number = ?, form_status = 'Approved', hall_ticket_status = 'Generated', remarks = 'Seat Allocated' WHERE enrollment_id = ?";

        int count = 0;
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                List<Integer> ids = new ArrayList<>();
                try (PreparedStatement psSelect = con.prepareStatement(selectSql)) {
                    psSelect.setInt(1, examId);
                    try (ResultSet rs = psSelect.executeQuery()) {
                        while (rs.next()) {
                            ids.add(rs.getInt("enrollment_id"));
                        }
                    }
                }

                try (PreparedStatement psUp = con.prepareStatement(updateSql)) {
                    int seq = 101;
                    for (int id : ids) {
                        String seatNo = prefix + seq;
                        psUp.setString(1, seatNo);
                        psUp.setInt(2, id);
                        psUp.addBatch();
                        seq++;
                        count++;
                    }
                    psUp.executeBatch();
                }

                con.commit();
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    private List<ExamEnrollment> queryEnrollments(String sql) {
        List<ExamEnrollment> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapEnrollment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private InternalExam mapExam(ResultSet rs) throws SQLException {
        return new InternalExam(
                rs.getInt("exam_id"),
                rs.getString("exam_name"),
                rs.getString("course"),
                rs.getInt("semester"),
                rs.getString("subject_name"),
                rs.getString("exam_date"),
                rs.getString("start_time"),
                rs.getString("end_time"),
                rs.getString("room_no"),
                rs.getDouble("max_marks"),
                rs.getString("status")
        );
    }

    private ExamEnrollment mapEnrollment(ResultSet rs) throws SQLException {
        return new ExamEnrollment(
                rs.getInt("enrollment_id"),
                rs.getInt("exam_id"),
                rs.getString("exam_name"),
                rs.getString("subject_name"),
                rs.getString("exam_date"),
                rs.getString("start_time"),
                rs.getString("end_time"),
                rs.getString("room_no"),
                rs.getDouble("max_marks"),
                rs.getInt("student_id"),
                rs.getString("student_name"),
                rs.getString("roll_no"),
                rs.getString("course"),
                rs.getInt("semester"),
                rs.getString("enrollment_date"),
                rs.getString("form_status"),
                rs.getString("seat_number"),
                rs.getString("hall_ticket_status"),
                rs.getString("remarks")
        );
    }
}
