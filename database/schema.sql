-- SQLite schema (reference only).
-- You do NOT need MySQL Workbench.
-- The Java app creates this database automatically as college_sms.db

CREATE TABLE IF NOT EXISTS students (
    student_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    roll_no      TEXT NOT NULL UNIQUE,
    full_name    TEXT NOT NULL,
    course       TEXT NOT NULL,
    semester     INTEGER NOT NULL,
    email        TEXT,
    phone        TEXT,
    created_at   TEXT DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS marks (
    mark_id        INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id     INTEGER NOT NULL,
    subject_name   TEXT NOT NULL,
    marks_obtained REAL NOT NULL,
    max_marks      REAL NOT NULL DEFAULT 100,
    exam_type      TEXT DEFAULT 'Internal',
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS attendance_sessions (
    session_id   TEXT PRIMARY KEY,
    subject_name TEXT NOT NULL,
    beacon_name  TEXT NOT NULL,
    secret       TEXT NOT NULL,
    started_at   TEXT NOT NULL,
    ends_at      TEXT NOT NULL,
    is_active    INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS phone_presenty (
    presenty_id    INTEGER PRIMARY KEY AUTOINCREMENT,
    session_id     TEXT NOT NULL,
    student_id     INTEGER NOT NULL,
    roll_no        TEXT NOT NULL,
    bt_device_name TEXT,
    bt_device_id   TEXT,
    bluetooth_hash TEXT NOT NULL,
    student_proof  TEXT NOT NULL,
    verify_method  TEXT NOT NULL,
    marked_at      TEXT DEFAULT (datetime('now')),
    UNIQUE(session_id, student_id)
);

CREATE TABLE IF NOT EXISTS attendance (
    attendance_id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id    INTEGER NOT NULL,
    attend_date   TEXT NOT NULL,
    status        TEXT NOT NULL DEFAULT 'Present',
    subject_name  TEXT,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS student_devices (
    device_id     TEXT PRIMARY KEY,
    student_id    INTEGER NOT NULL,
    roll_no       TEXT NOT NULL,
    registered_at TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS classroom_notices (
    notice_id     INTEGER PRIMARY KEY AUTOINCREMENT,
    notice_text   TEXT NOT NULL,
    posted_by     TEXT DEFAULT 'Teacher',
    posted_at     TEXT DEFAULT CURRENT_TIMESTAMP,
    is_active     INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS student_academic_stats (
    stat_id               INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id            INTEGER NOT NULL UNIQUE,
    assignments_completed INTEGER NOT NULL DEFAULT 0,
    total_assignments     INTEGER NOT NULL DEFAULT 5,
    practicals_completed  INTEGER NOT NULL DEFAULT 0,
    total_practicals      INTEGER NOT NULL DEFAULT 10,
    remarks               TEXT DEFAULT 'In Progress',
    updated_at            TEXT DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS timetable (
    timetable_id   INTEGER PRIMARY KEY AUTOINCREMENT,
    day_of_week    TEXT NOT NULL,
    lecture_number INTEGER NOT NULL,
    start_time     TEXT NOT NULL,
    end_time       TEXT NOT NULL,
    subject_name   TEXT NOT NULL,
    teacher_name   TEXT NOT NULL,
    room_no        TEXT DEFAULT 'Room 302',
    lecture_type   TEXT DEFAULT 'Theory'
);

-- Role-based Authentication (HOD/Admin, Teacher, Librarian, Student)
CREATE TABLE IF NOT EXISTS users (
    user_id     INTEGER PRIMARY KEY AUTOINCREMENT,
    username    TEXT NOT NULL UNIQUE,
    password    TEXT NOT NULL,
    role        TEXT NOT NULL,
    full_name   TEXT NOT NULL,
    email       TEXT,
    student_id  INTEGER,
    created_at  TEXT DEFAULT (datetime('now')),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE SET NULL
);

-- Library Management System
CREATE TABLE IF NOT EXISTS books (
    book_id          INTEGER PRIMARY KEY AUTOINCREMENT,
    isbn             TEXT UNIQUE,
    title            TEXT NOT NULL,
    author           TEXT NOT NULL,
    category         TEXT NOT NULL,
    total_copies     INTEGER NOT NULL DEFAULT 1,
    available_copies INTEGER NOT NULL DEFAULT 1,
    shelf_location   TEXT DEFAULT 'Section A1',
    created_at       TEXT DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS book_issues (
    issue_id     INTEGER PRIMARY KEY AUTOINCREMENT,
    book_id      INTEGER NOT NULL,
    student_id   INTEGER NOT NULL,
    roll_no      TEXT NOT NULL,
    issue_date   TEXT NOT NULL,
    due_date     TEXT NOT NULL,
    return_date  TEXT,
    fine_amount  REAL DEFAULT 0.0,
    status       TEXT NOT NULL DEFAULT 'ISSUED',
    remarks      TEXT,
    FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);

-- Internal Exam Management & Seat Allocation
CREATE TABLE IF NOT EXISTS internal_exams (
    exam_id       INTEGER PRIMARY KEY AUTOINCREMENT,
    exam_name     TEXT NOT NULL,
    course        TEXT NOT NULL DEFAULT 'BBA CA',
    semester      INTEGER NOT NULL DEFAULT 5,
    subject_name  TEXT NOT NULL,
    exam_date     TEXT NOT NULL,
    start_time    TEXT NOT NULL,
    end_time      TEXT NOT NULL,
    room_no       TEXT NOT NULL DEFAULT 'Exam Hall 101',
    max_marks     REAL NOT NULL DEFAULT 40,
    status        TEXT NOT NULL DEFAULT 'Upcoming',
    created_at    TEXT DEFAULT (datetime('now'))
);

CREATE TABLE IF NOT EXISTS exam_enrollments (
    enrollment_id      INTEGER PRIMARY KEY AUTOINCREMENT,
    exam_id            INTEGER NOT NULL,
    student_id         INTEGER NOT NULL,
    roll_no            TEXT NOT NULL,
    enrollment_date    TEXT DEFAULT (datetime('now')),
    form_status        TEXT NOT NULL DEFAULT 'Pending',
    seat_number        TEXT,
    hall_ticket_status TEXT DEFAULT 'Generated',
    remarks            TEXT,
    UNIQUE(exam_id, student_id),
    FOREIGN KEY (exam_id) REFERENCES internal_exams(exam_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
);



