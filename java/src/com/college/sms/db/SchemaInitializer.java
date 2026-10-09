package com.college.sms.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates SQLite tables and loads sample data on first run.
 */
public class SchemaInitializer {

    public static void createTablesAndSampleData(Connection con) throws SQLException {
        try (Statement st = con.createStatement()) {
            st.execute(
                    "CREATE TABLE IF NOT EXISTS students ("
                            + "student_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + "roll_no TEXT NOT NULL UNIQUE,"
                            + "full_name TEXT NOT NULL,"
                            + "course TEXT NOT NULL,"
                            + "semester INTEGER NOT NULL,"
                            + "email TEXT,"
                            + "phone TEXT,"
                            + "created_at TEXT DEFAULT (datetime('now'))"
                            + ")");

            st.execute(
                    "CREATE TABLE IF NOT EXISTS marks ("
                            + "mark_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + "student_id INTEGER NOT NULL,"
                            + "subject_name TEXT NOT NULL,"
                            + "marks_obtained REAL NOT NULL,"
                            + "max_marks REAL NOT NULL DEFAULT 100,"
                            + "exam_type TEXT DEFAULT 'Internal',"
                            + "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE"
                            + ")");

            st.execute(
                    "CREATE TABLE IF NOT EXISTS attendance ("
                            + "attendance_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + "student_id INTEGER NOT NULL,"
                            + "attend_date TEXT NOT NULL,"
                            + "status TEXT NOT NULL DEFAULT 'Present',"
                            + "subject_name TEXT,"
                            + "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE"
                            + ")");

            st.execute(
                    "CREATE TABLE IF NOT EXISTS attendance_sessions ("
                            + "session_id TEXT PRIMARY KEY,"
                            + "subject_name TEXT NOT NULL,"
                            + "beacon_name TEXT NOT NULL,"
                            + "secret TEXT NOT NULL,"
                            + "started_at TEXT NOT NULL,"
                            + "ends_at TEXT NOT NULL,"
                            + "is_active INTEGER NOT NULL DEFAULT 1"
                            + ")");

            st.execute(
                    "CREATE TABLE IF NOT EXISTS phone_presenty ("
                            + "presenty_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + "session_id TEXT NOT NULL,"
                            + "student_id INTEGER NOT NULL,"
                            + "roll_no TEXT NOT NULL,"
                            + "bt_device_name TEXT,"
                            + "bt_device_id TEXT,"
                            + "bluetooth_hash TEXT NOT NULL,"
                            + "student_proof TEXT NOT NULL,"
                            + "verify_method TEXT NOT NULL,"
                            + "marked_at TEXT DEFAULT (datetime('now')),"
                            + "UNIQUE(session_id, student_id),"
                            + "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE"
                            + ")");

            st.execute(
                    "CREATE TABLE IF NOT EXISTS student_devices ("
                            + "device_id TEXT PRIMARY KEY,"
                            + "student_id INTEGER NOT NULL,"
                            + "roll_no TEXT NOT NULL,"
                            + "registered_at TEXT DEFAULT CURRENT_TIMESTAMP,"
                            + "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE"
                            + ")");

            st.execute(
                    "CREATE TABLE IF NOT EXISTS classroom_notices ("
                            + "notice_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + "notice_text TEXT NOT NULL,"
                            + "posted_by TEXT DEFAULT 'Teacher',"
                            + "posted_at TEXT DEFAULT CURRENT_TIMESTAMP,"
                            + "is_active INTEGER NOT NULL DEFAULT 1"
                            + ")");

            st.execute(
                    "CREATE TABLE IF NOT EXISTS student_academic_stats ("
                            + "stat_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + "student_id INTEGER NOT NULL UNIQUE,"
                            + "assignments_completed INTEGER NOT NULL DEFAULT 0,"
                            + "total_assignments INTEGER NOT NULL DEFAULT 5,"
                            + "practicals_completed INTEGER NOT NULL DEFAULT 0,"
                            + "total_practicals INTEGER NOT NULL DEFAULT 10,"
                            + "remarks TEXT DEFAULT 'In Progress',"
                            + "updated_at TEXT DEFAULT CURRENT_TIMESTAMP,"
                            + "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE"
                            + ")");

            st.execute(
                    "CREATE TABLE IF NOT EXISTS timetable ("
                            + "timetable_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + "day_of_week TEXT NOT NULL,"
                            + "lecture_number INTEGER NOT NULL,"
                            + "start_time TEXT NOT NULL,"
                            + "end_time TEXT NOT NULL,"
                            + "subject_name TEXT NOT NULL,"
                            + "teacher_name TEXT NOT NULL,"
                            + "room_no TEXT DEFAULT 'Room 302',"
                            + "lecture_type TEXT DEFAULT 'Theory'"
                            + ")");

            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM students")) {
                rs.next();
                if (rs.getInt(1) > 0) {
                    return;
                }
            }

            st.execute("INSERT INTO students (roll_no, full_name, course, semester, email, phone) VALUES "
                    + "('BBA001', 'Aarav Sharma', 'BBA CA', 5, 'aarav@college.edu', '9876543210'),"
                    + "('BBA002', 'Priya Patel', 'BBA CA', 5, 'priya@college.edu', '9876543211'),"
                    + "('BBA003', 'Rohan Deshmukh', 'BBA CA', 5, 'rohan@college.edu', '9876543212'),"
                    + "('BBA004', 'Sneha Kulkarni', 'BBA CA', 5, 'sneha@college.edu', '9876543213'),"
                    + "('BBA005', 'Vikram Singh', 'BBA CA', 5, 'vikram@college.edu', '9876543214')");

            st.execute("INSERT INTO marks (student_id, subject_name, marks_obtained, max_marks, exam_type) VALUES "
                    + "(1, 'Java Programming', 78, 100, 'Internal'),"
                    + "(1, 'Python Programming', 85, 100, 'Internal'),"
                    + "(1, 'Database Systems', 72, 100, 'Internal'),"
                    + "(2, 'Java Programming', 88, 100, 'Internal'),"
                    + "(2, 'Python Programming', 91, 100, 'Internal'),"
                    + "(2, 'Database Systems', 80, 100, 'Internal'),"
                    + "(3, 'Java Programming', 65, 100, 'Internal'),"
                    + "(3, 'Python Programming', 70, 100, 'Internal'),"
                    + "(3, 'Database Systems', 68, 100, 'Internal'),"
                    + "(4, 'Java Programming', 92, 100, 'Internal'),"
                    + "(4, 'Python Programming', 89, 100, 'Internal'),"
                    + "(4, 'Database Systems', 94, 100, 'Internal'),"
                    + "(5, 'Java Programming', 55, 100, 'Internal'),"
                    + "(5, 'Python Programming', 60, 100, 'Internal'),"
                    + "(5, 'Database Systems', 58, 100, 'Internal')");

            st.execute("INSERT INTO attendance (student_id, attend_date, status, subject_name) VALUES "
                    + "(1, '2026-09-01', 'Present', 'Java Programming'),"
                    + "(1, '2026-09-02', 'Present', 'Python Programming'),"
                    + "(1, '2026-09-03', 'Absent', 'Database Systems'),"
                    + "(2, '2026-09-01', 'Present', 'Java Programming'),"
                    + "(2, '2026-09-02', 'Late', 'Python Programming'),"
                    + "(2, '2026-09-03', 'Present', 'Database Systems'),"
                    + "(3, '2026-09-01', 'Absent', 'Java Programming'),"
                    + "(3, '2026-09-02', 'Present', 'Python Programming'),"
                    + "(3, '2026-09-03', 'Present', 'Database Systems'),"
                    + "(4, '2026-09-01', 'Present', 'Java Programming'),"
                    + "(4, '2026-09-02', 'Present', 'Python Programming'),"
                    + "(4, '2026-09-03', 'Present', 'Database Systems'),"
                    + "(5, '2026-09-01', 'Late', 'Java Programming'),"
                    + "(5, '2026-09-02', 'Absent', 'Python Programming'),"
                    + "(5, '2026-09-03', 'Present', 'Database Systems')");
        }
    }
}
