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

            st.execute(
                    "CREATE TABLE IF NOT EXISTS users ("
                            + "user_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + "username TEXT NOT NULL UNIQUE,"
                            + "password TEXT NOT NULL,"
                            + "role TEXT NOT NULL," // HOD, TEACHER, LIBRARIAN, STUDENT
                            + "full_name TEXT NOT NULL,"
                            + "email TEXT,"
                            + "student_id INTEGER,"
                            + "created_at TEXT DEFAULT (datetime('now')),"
                            + "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE SET NULL"
                            + ")");

            st.execute(
                    "CREATE TABLE IF NOT EXISTS books ("
                            + "book_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + "isbn TEXT UNIQUE,"
                            + "title TEXT NOT NULL,"
                            + "author TEXT NOT NULL,"
                            + "category TEXT NOT NULL,"
                            + "total_copies INTEGER NOT NULL DEFAULT 1,"
                            + "available_copies INTEGER NOT NULL DEFAULT 1,"
                            + "shelf_location TEXT DEFAULT 'Section A1',"
                            + "created_at TEXT DEFAULT (datetime('now'))"
                            + ")");

            st.execute(
                    "CREATE TABLE IF NOT EXISTS book_issues ("
                            + "issue_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + "book_id INTEGER NOT NULL,"
                            + "student_id INTEGER NOT NULL,"
                            + "roll_no TEXT NOT NULL,"
                            + "issue_date TEXT NOT NULL,"
                            + "due_date TEXT NOT NULL,"
                            + "return_date TEXT,"
                            + "fine_amount REAL DEFAULT 0.0,"
                            + "status TEXT NOT NULL DEFAULT 'ISSUED',"
                            + "remarks TEXT,"
                            + "FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,"
                            + "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE"
                            + ")");

            st.execute(
                    "CREATE TABLE IF NOT EXISTS internal_exams ("
                            + "exam_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + "exam_name TEXT NOT NULL,"
                            + "course TEXT NOT NULL DEFAULT 'BBA CA',"
                            + "semester INTEGER NOT NULL DEFAULT 5,"
                            + "subject_name TEXT NOT NULL,"
                            + "exam_date TEXT NOT NULL,"
                            + "start_time TEXT NOT NULL,"
                            + "end_time TEXT NOT NULL,"
                            + "room_no TEXT NOT NULL DEFAULT 'Exam Hall 101',"
                            + "max_marks REAL NOT NULL DEFAULT 40,"
                            + "status TEXT NOT NULL DEFAULT 'Upcoming',"
                            + "created_at TEXT DEFAULT (datetime('now'))"
                            + ")");

            st.execute(
                    "CREATE TABLE IF NOT EXISTS exam_enrollments ("
                            + "enrollment_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + "exam_id INTEGER NOT NULL,"
                            + "student_id INTEGER NOT NULL,"
                            + "roll_no TEXT NOT NULL,"
                            + "enrollment_date TEXT DEFAULT (datetime('now')),"
                            + "form_status TEXT NOT NULL DEFAULT 'Pending',"
                            + "seat_number TEXT,"
                            + "hall_ticket_status TEXT DEFAULT 'Generated',"
                            + "remarks TEXT,"
                            + "UNIQUE(exam_id, student_id),"
                            + "FOREIGN KEY (exam_id) REFERENCES internal_exams(exam_id) ON DELETE CASCADE,"
                            + "FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE"
                            + ")");

            // Sample students if none exist
            boolean hasStudents = false;
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM students")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    hasStudents = true;
                }
            }

            if (!hasStudents) {
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

            // Sample users if none exist
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    st.execute("INSERT INTO users (username, password, role, full_name, email, student_id) VALUES "
                            + "('admin', 'admin123', 'HOD', 'Prof. Dr. V. K. Sharma (HOD)', 'hod.bbaca@college.edu', NULL),"
                            + "('hod', 'hod123', 'HOD', 'Prof. Dr. V. K. Sharma (HOD)', 'hod.bbaca@college.edu', NULL),"
                            + "('teacher', 'teacher123', 'TEACHER', 'Prof. Sunita Rao', 'sunita.rao@college.edu', NULL),"
                            + "('librarian', 'library123', 'LIBRARIAN', 'Mr. Ramesh Joshi (Chief Librarian)', 'library@college.edu', NULL),"
                            + "('student', 'student123', 'STUDENT', 'Aarav Sharma', 'aarav@college.edu', 1),"
                            + "('BBA001', 'bba001', 'STUDENT', 'Aarav Sharma', 'aarav@college.edu', 1),"
                            + "('BBA002', 'bba002', 'STUDENT', 'Priya Patel', 'priya@college.edu', 2),"
                            + "('BBA003', 'bba003', 'STUDENT', 'Rohan Deshmukh', 'rohan@college.edu', 3),"
                            + "('BBA004', 'bba004', 'STUDENT', 'Sneha Kulkarni', 'sneha@college.edu', 4),"
                            + "('BBA005', 'bba005', 'STUDENT', 'Vikram Singh', 'vikram@college.edu', 5),"
                            + "('18', '18', 'STUDENT', 'Omkar', 'omkar@college.edu', 6)");
                }
            }

            // Sample books if none exist
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM books")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    st.execute("INSERT INTO books (isbn, title, author, category, total_copies, available_copies, shelf_location) VALUES "
                            + "('978-0071809252', 'Java: The Complete Reference', 'Herbert Schildt', 'Computer Science', 5, 4, 'Shelf CS-01'),"
                            + "('978-1593279288', 'Python Crash Course', 'Eric Matthes', 'Computer Science', 6, 5, 'Shelf CS-02'),"
                            + "('978-0078022159', 'Database System Concepts', 'Abraham Silberschatz', 'Computer Science', 4, 3, 'Shelf CS-03'),"
                            + "('978-0132350884', 'Clean Code', 'Robert C. Martin', 'Computer Science', 3, 3, 'Shelf CS-04'),"
                            + "('978-0070144897', 'Principles of Management', 'Harold Koontz, Heinz Weihrich', 'Management', 5, 5, 'Shelf MG-01'),"
                            + "('978-1285094069', 'Business Communication: Process and Product', 'Mary Ellen Guffey', 'Management', 4, 4, 'Shelf MG-02'),"
                            + "('978-9332549449', 'Web Technologies: TCP/IP to Java', 'Achyut Godbole, Atul Kahate', 'Computer Science', 4, 4, 'Shelf CS-05'),"
                            + "('978-0133591620', 'Modern Operating Systems', 'Andrew S. Tanenbaum', 'Computer Science', 3, 2, 'Shelf CS-06')");
                }
            }

            // Sample book issues if none exist
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM book_issues")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    st.execute("INSERT INTO book_issues (book_id, student_id, roll_no, issue_date, due_date, return_date, fine_amount, status, remarks) VALUES "
                            + "(1, 1, 'BBA001', '2026-10-01', '2026-10-15', NULL, 0.0, 'ISSUED', 'Semester reference book'),"
                            + "(2, 2, 'BBA002', '2026-10-05', '2026-10-19', NULL, 0.0, 'ISSUED', 'Python project preparation'),"
                            + "(3, 3, 'BBA003', '2026-09-15', '2026-09-29', '2026-10-02', 15.0, 'RETURNED', 'Returned 3 days late, fine cleared')");
                }
            }

            // Sample internal exams if none exist
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM internal_exams")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    st.execute("INSERT INTO internal_exams (exam_name, course, semester, subject_name, exam_date, start_time, end_time, room_no, max_marks, status) VALUES "
                            + "('Internal Exam 1 (Winter 2026)', 'BBA CA', 5, 'Java Programming', '2026-10-28', '10:00 AM', '11:30 AM', 'Exam Hall 101', 40, 'Upcoming'),"
                            + "('Internal Exam 1 (Winter 2026)', 'BBA CA', 5, 'Python Programming', '2026-10-29', '10:00 AM', '11:30 AM', 'Exam Hall 101', 40, 'Upcoming'),"
                            + "('Internal Exam 1 (Winter 2026)', 'BBA CA', 5, 'Database Systems', '2026-10-30', '10:00 AM', '11:30 AM', 'Exam Hall 102', 40, 'Upcoming'),"
                            + "('Mid-Term Lab Assessment', 'BBA CA', 5, 'Advanced Java Lab', '2026-11-04', '01:30 PM', '04:30 PM', 'Computer Lab 3', 50, 'Upcoming')");
                }
            }

            // Sample exam enrollments with seat numbers if none exist
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM exam_enrollments")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    st.execute("INSERT INTO exam_enrollments (exam_id, student_id, roll_no, enrollment_date, form_status, seat_number, hall_ticket_status, remarks) VALUES "
                            + "(1, 1, 'BBA001', '2026-10-10 10:30:00', 'Approved', 'BBA5-S101', 'Generated', 'Eligibility verified - 82% attendance'),"
                            + "(1, 2, 'BBA002', '2026-10-10 11:15:00', 'Approved', 'BBA5-S102', 'Generated', 'Eligibility verified - 90% attendance'),"
                            + "(1, 3, 'BBA003', '2026-10-11 09:20:00', 'Pending', NULL, 'Not Generated', 'Exam form under review'),"
                            + "(2, 1, 'BBA001', '2026-10-10 10:30:00', 'Approved', 'BBA5-S101', 'Generated', 'Eligibility verified'),"
                            + "(2, 2, 'BBA002', '2026-10-10 11:15:00', 'Approved', 'BBA5-S102', 'Generated', 'Eligibility verified'),"
                            + "(3, 1, 'BBA001', '2026-10-10 10:30:00', 'Approved', 'BBA5-S101', 'Generated', 'Eligibility verified')");
                }
            }
        }
    }
}
