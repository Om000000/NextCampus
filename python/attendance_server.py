"""
Phone presenty server (Flask).
Students open this page on their phone (same Wi-Fi as the teacher PC).

Features & Verification:
1. Session must be active in the Java app.
2. Classroom hash must match the live rolling hash (SHA-256 with ~90s tolerance).
3. ONE ROLL NUMBER PER DEVICE (anti-proxy protection: device is bound to student).
4. LIVE TEACHER NOTICE / COMMENT displayed on student attendance page.
5. ASSIGNMENT & PRACTICAL COMPLETION STATS given by teacher shown to students.
6. Students can view marks for their registered roll number.
7. One presenty per student per session.
"""

from __future__ import annotations

import sqlite3
import time
from datetime import datetime

from flask import Flask, jsonify, render_template, request

import bt_hash
from db_path import DB_FILE

app = Flask(__name__)


def init_db():
    if not DB_FILE.exists():
        return
    try:
        con = sqlite3.connect(DB_FILE, timeout=30.0)
        con.execute("PRAGMA journal_mode = WAL")
        con.execute("PRAGMA synchronous = NORMAL")
        con.execute("PRAGMA busy_timeout = 30000")
        con.execute("""
            CREATE TABLE IF NOT EXISTS student_devices (
                device_id TEXT PRIMARY KEY,
                student_id INTEGER NOT NULL,
                roll_no TEXT NOT NULL,
                registered_at TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
            )
        """)
        con.execute("""
            CREATE TABLE IF NOT EXISTS classroom_notices (
                notice_id INTEGER PRIMARY KEY AUTOINCREMENT,
                notice_text TEXT NOT NULL,
                posted_by TEXT DEFAULT 'Teacher',
                posted_at TEXT DEFAULT CURRENT_TIMESTAMP,
                is_active INTEGER NOT NULL DEFAULT 1
            )
        """)
        con.execute("""
            CREATE TABLE IF NOT EXISTS student_academic_stats (
                stat_id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER NOT NULL UNIQUE,
                assignments_completed INTEGER NOT NULL DEFAULT 0,
                total_assignments INTEGER NOT NULL DEFAULT 5,
                practicals_completed INTEGER NOT NULL DEFAULT 0,
                total_practicals INTEGER NOT NULL DEFAULT 10,
                remarks TEXT DEFAULT 'In Progress',
                updated_at TEXT DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
            )
        """)
        con.execute("""
            CREATE TABLE IF NOT EXISTS users (
                user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                password TEXT NOT NULL,
                role TEXT NOT NULL,
                full_name TEXT NOT NULL,
                email TEXT,
                student_id INTEGER,
                created_at TEXT DEFAULT (datetime('now')),
                FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE SET NULL
            )
        """)
        con.execute("""
            CREATE TABLE IF NOT EXISTS books (
                book_id INTEGER PRIMARY KEY AUTOINCREMENT,
                isbn TEXT UNIQUE,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                category TEXT NOT NULL,
                total_copies INTEGER NOT NULL DEFAULT 1,
                available_copies INTEGER NOT NULL DEFAULT 1,
                shelf_location TEXT DEFAULT 'Section A1',
                created_at TEXT DEFAULT (datetime('now'))
            )
        """)
        con.execute("""
            CREATE TABLE IF NOT EXISTS book_issues (
                issue_id INTEGER PRIMARY KEY AUTOINCREMENT,
                book_id INTEGER NOT NULL,
                student_id INTEGER NOT NULL,
                roll_no TEXT NOT NULL,
                issue_date TEXT NOT NULL,
                due_date TEXT NOT NULL,
                return_date TEXT,
                fine_amount REAL DEFAULT 0.0,
                status TEXT NOT NULL DEFAULT 'ISSUED',
                remarks TEXT,
                FOREIGN KEY (book_id) REFERENCES books(book_id) ON DELETE CASCADE,
                FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
            )
        """)
        con.execute("""
            CREATE TABLE IF NOT EXISTS internal_exams (
                exam_id INTEGER PRIMARY KEY AUTOINCREMENT,
                exam_name TEXT NOT NULL,
                course TEXT NOT NULL DEFAULT 'BBA CA',
                semester INTEGER NOT NULL DEFAULT 5,
                subject_name TEXT NOT NULL,
                exam_date TEXT NOT NULL,
                start_time TEXT NOT NULL,
                end_time TEXT NOT NULL,
                room_no TEXT NOT NULL DEFAULT 'Exam Hall 101',
                max_marks REAL NOT NULL DEFAULT 40,
                status TEXT NOT NULL DEFAULT 'Upcoming',
                created_at TEXT DEFAULT (datetime('now'))
            )
        """)
        con.execute("""
            CREATE TABLE IF NOT EXISTS exam_enrollments (
                enrollment_id INTEGER PRIMARY KEY AUTOINCREMENT,
                exam_id INTEGER NOT NULL,
                student_id INTEGER NOT NULL,
                roll_no TEXT NOT NULL,
                enrollment_date TEXT DEFAULT (datetime('now')),
                form_status TEXT NOT NULL DEFAULT 'Pending',
                seat_number TEXT,
                hall_ticket_status TEXT DEFAULT 'Generated',
                remarks TEXT,
                UNIQUE(exam_id, student_id),
                FOREIGN KEY (exam_id) REFERENCES internal_exams(exam_id) ON DELETE CASCADE,
                FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE
            )
        """)
        con.commit()
        con.close()
    except Exception as e:
        print("init_db note:", e)

init_db()


def db():
    con = sqlite3.connect(DB_FILE, timeout=30.0)
    con.row_factory = sqlite3.Row
    con.execute("PRAGMA foreign_keys = ON")
    con.execute("PRAGMA busy_timeout = 30000")
    return con



def get_active_session(con):
    now = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    session = con.execute(
        "SELECT * FROM attendance_sessions WHERE is_active=1 ORDER BY started_at DESC LIMIT 1"
    ).fetchone()
    if not session:
        return None
    if session["ends_at"] and session["ends_at"] < now:
        con.execute("UPDATE attendance_sessions SET is_active=0 WHERE session_id=?", (session["session_id"],))
        con.commit()
        return None
    return session


def get_active_notice(con):
    row = con.execute(
        "SELECT notice_text, posted_by, posted_at FROM classroom_notices WHERE is_active=1 ORDER BY posted_at DESC LIMIT 1"
    ).fetchone()
    if row:
        return {
            "text": row["notice_text"],
            "posted_by": row["posted_by"],
            "posted_at": row["posted_at"]
        }
    return {
        "text": "Welcome to class! Submit assignments on time.",
        "posted_by": "Teacher",
        "posted_at": ""
    }


def get_student_academic_stats(con, student_id: int):
    row = con.execute(
        """
        SELECT assignments_completed, total_assignments, practicals_completed, total_practicals, remarks, updated_at
        FROM student_academic_stats WHERE student_id=?
        """,
        (student_id,)
    ).fetchone()
    if row:
        a_done = int(row["assignments_completed"])
        a_tot = int(row["total_assignments"])
        p_done = int(row["practicals_completed"])
        p_tot = int(row["total_practicals"])
        a_pct = round((a_done / a_tot) * 100, 1) if a_tot > 0 else 0.0
        p_pct = round((p_done / p_tot) * 100, 1) if p_tot > 0 else 0.0
        return {
            "has_stats": True,
            "assignments_completed": a_done,
            "total_assignments": a_tot,
            "assignment_percentage": a_pct,
            "practicals_completed": p_done,
            "total_practicals": p_tot,
            "practical_percentage": p_pct,
            "remarks": row["remarks"] or "In Progress",
            "updated_at": row["updated_at"]
        }
    return {
        "has_stats": False,
        "assignments_completed": 0,
        "total_assignments": 5,
        "assignment_percentage": 0.0,
        "practicals_completed": 0,
        "total_practicals": 10,
        "practical_percentage": 0.0,
        "remarks": "No completion records found yet.",
        "updated_at": ""
    }


def get_student_marks_data(con, student_id: int):
    rows = con.execute(
        "SELECT mark_id, subject_name, marks_obtained, max_marks, exam_type FROM marks WHERE student_id=? ORDER BY subject_name",
        (student_id,)
    ).fetchall()
    marks_list = []
    total_obtained = 0.0
    total_max = 0.0
    for r in rows:
        obt = float(r["marks_obtained"])
        mx = float(r["max_marks"])
        pct = round((obt / mx) * 100, 1) if mx > 0 else 0.0
        total_obtained += obt
        total_max += mx
        marks_list.append({
            "subject_name": r["subject_name"],
            "marks_obtained": obt,
            "max_marks": mx,
            "exam_type": r["exam_type"] or "Internal",
            "percentage": pct
        })
    overall_pct = round((total_obtained / total_max) * 100, 1) if total_max > 0 else 0.0
    return {
        "marks": marks_list,
        "total_obtained": total_obtained,
        "total_max": total_max,
        "overall_percentage": overall_pct
    }


@app.get("/")
def home():
    return render_template("presenty.html")


@app.get("/api/session")
def api_session():
    if not DB_FILE.exists():
        return jsonify({"active": False, "error": "Database not found. Run the Java app first."}), 200
    con = db()
    try:
        session = get_active_session(con)
        notice = get_active_notice(con)
        if not session:
            return jsonify({"active": False, "notice": notice})
        return jsonify(
            {
                "active": True,
                "session_id": session["session_id"],
                "subject": session["subject_name"],
                "beacon_name": session["beacon_name"],
                "ends_at": session["ends_at"],
                "notice": notice,
            }
        )
    finally:
        con.close()


@app.route("/api/notice", methods=["GET", "POST"])
def api_notice():
    if not DB_FILE.exists():
        return jsonify({"ok": False, "message": "Database not found."}), 500
    con = db()
    try:
        if request.method == "POST":
            data = request.get_json(silent=True) or {}
            txt = (data.get("notice_text") or "").strip()
            by = (data.get("posted_by") or "Teacher").strip()
            if not txt:
                return jsonify({"ok": False, "message": "Notice text is required."}), 400
            con.execute("UPDATE classroom_notices SET is_active=0 WHERE is_active=1")
            con.execute(
                "INSERT INTO classroom_notices (notice_text, posted_by, posted_at, is_active) VALUES (?, ?, datetime('now'), 1)",
                (txt, by)
            )
            con.commit()
            return jsonify({"ok": True, "message": "Notice updated successfully.", "notice": get_active_notice(con)})
        return jsonify({"ok": True, "notice": get_active_notice(con)})
    finally:
        con.close()


@app.get("/api/device-status")
def api_device_status():
    device_id = (request.args.get("device_id") or "").strip()
    if not device_id or not DB_FILE.exists():
        return jsonify({"bound": False})
    con = db()
    try:
        notice = get_active_notice(con)
        binding = con.execute(
            """
            SELECT d.device_id, d.roll_no, s.student_id, s.full_name, s.course, s.semester
            FROM student_devices d
            JOIN students s ON d.student_id = s.student_id
            WHERE d.device_id = ?
            """,
            (device_id,)
        ).fetchone()
        if binding:
            marks_info = get_student_marks_data(con, binding["student_id"])
            stats_info = get_student_academic_stats(con, binding["student_id"])
            return jsonify({
                "bound": True,
                "roll_no": binding["roll_no"],
                "student_name": binding["full_name"],
                "course": binding["course"],
                "semester": binding["semester"],
                "marks_data": marks_info,
                "academic_stats": stats_info,
                "notice": notice
            })
        return jsonify({"bound": False, "notice": notice})
    finally:
        con.close()


@app.route("/api/reset-devices", methods=["GET", "POST"])
def api_reset_devices():
    """Endpoint to unlock student devices by roll number, device ID, or all."""
    if not DB_FILE.exists():
        return jsonify({"ok": False, "message": "Database not found."}), 500

    roll = ""
    device_id = ""
    if request.method == "POST":
        data = request.get_json(silent=True) or {}
        roll = (data.get("roll_no") or "").strip().upper()
        device_id = (data.get("device_id") or "").strip()
    else:
        roll = (request.args.get("roll_no") or "").strip().upper()
        device_id = (request.args.get("device_id") or "").strip()

    con = db()
    try:
        if device_id:
            con.execute("DELETE FROM student_devices WHERE device_id = ?", (device_id,))
            con.execute("UPDATE phone_presenty SET bt_device_id = NULL WHERE bt_device_id = ?", (device_id,))
            con.commit()
            return jsonify({"ok": True, "message": f"Device {device_id} unlocked."})
        elif roll:
            con.execute("DELETE FROM student_devices WHERE upper(roll_no) = ?", (roll,))
            con.execute("UPDATE phone_presenty SET bt_device_id = NULL WHERE upper(roll_no) = ?", (roll,))
            con.commit()
            return jsonify({"ok": True, "message": f"Device lock cleared for Roll No {roll}."})
        else:
            con.execute("DELETE FROM student_devices")
            con.execute("UPDATE phone_presenty SET bt_device_id = NULL")
            con.commit()
            return jsonify({"ok": True, "message": "All student device locks cleared successfully."})
    finally:
        con.close()



@app.get("/api/student-marks")
def api_student_marks():
    roll = (request.args.get("roll_no") or "").strip().upper()
    device_id = (request.args.get("device_id") or "").strip()
    if not roll:
        return jsonify({"ok": False, "message": "Roll number is required."}), 400
    if not DB_FILE.exists():
        return jsonify({"ok": False, "message": "Database not found. Run Java app first."}), 500

    con = db()
    try:
        # Check if device is bound to a different roll number
        if device_id:
            bound = con.execute("SELECT * FROM student_devices WHERE device_id = ?", (device_id,)).fetchone()
            if bound and bound["roll_no"].upper() != roll:
                return jsonify({
                    "ok": False,
                    "message": f"This device is registered to Roll No {bound['roll_no']}. You cannot view details for another roll number on this device."
                }), 403

        student = con.execute(
            "SELECT * FROM students WHERE upper(trim(roll_no))=? OR upper(replace(roll_no, ' ', ''))=?",
            (roll, roll.replace(" ", "")),
        ).fetchone()
        if not student:
            return jsonify({"ok": False, "message": f"Student with roll number '{roll}' not found."}), 404

        marks_info = get_student_marks_data(con, student["student_id"])
        stats_info = get_student_academic_stats(con, student["student_id"])
        notice = get_active_notice(con)

        return jsonify({
            "ok": True,
            "student_name": student["full_name"],
            "roll_no": student["roll_no"],
            "course": student["course"],
            "semester": student["semester"],
            "academic_stats": stats_info,
            "notice": notice,
            **marks_info
        })
    finally:
        con.close()


@app.post("/api/presenty")
def api_presenty():
    data = request.get_json(silent=True) or {}
    roll = (data.get("roll_no") or "").strip()
    given_hash = (data.get("bluetooth_hash") or "").strip().upper()
    device_id = (data.get("device_id") or "").strip()
    bt_name = (data.get("bt_device_name") or "").strip()
    bt_id = (data.get("bt_device_id") or "").strip() or device_id
    client_proof = (data.get("student_proof") or "").strip().lower()

    if not roll:
        return jsonify({"ok": False, "message": "Roll number is required."}), 400
    if not given_hash:
        return jsonify({"ok": False, "message": "Classroom hash is required (look at teacher screen)."}), 400

    if not DB_FILE.exists():
        return jsonify({"ok": False, "message": "Database not found. Run Java app first."}), 500

    con = db()
    try:
        session = get_active_session(con)
        notice = get_active_notice(con)
        if not session:
            return jsonify({
                "ok": False,
                "message": "No active class session. Teacher must click 'Start Session' in the Java app first.",
            }), 400

        unix = int(time.time())
        if not bt_hash.matches_broadcast(given_hash, session["secret"], session["session_id"], unix):
            return jsonify({
                "ok": False,
                "message": "Classroom hash is invalid or expired. Please check the teacher screen for the latest hash.",
            }), 400

        student = con.execute(
            "SELECT * FROM students WHERE upper(trim(roll_no))=? OR upper(replace(roll_no, ' ', ''))=?",
            (roll.upper(), roll.upper().replace(" ", "")),
        ).fetchone()
        if not student:
            return jsonify({
                "ok": False,
                "message": f"Roll number '{roll}' not found in records. Ask teacher to add you first in the Students tab.",
            }), 400

        # === 1. DEVICE LOCK / ANTI-PROXY ENFORCEMENT ===
        dev_key = device_id or bt_id
        if dev_key:
            # Check 1: Is this device already bound to a different student?
            bound = con.execute("SELECT * FROM student_devices WHERE device_id = ?", (dev_key,)).fetchone()
            if bound and bound["student_id"] != student["student_id"]:
                return jsonify({
                    "ok": False,
                    "message": f"Proxy attendance blocked! This device is registered to Roll No {bound['roll_no']}. Only one roll number per device is allowed."
                }), 403

            # Check 2: Was this device already used for another student in this session?
            used_in_session = con.execute(
                "SELECT roll_no FROM phone_presenty WHERE session_id = ? AND bt_device_id = ? AND student_id != ?",
                (session["session_id"], dev_key, student["student_id"])
            ).fetchone()
            if used_in_session:
                return jsonify({
                    "ok": False,
                    "message": f"Proxy attendance blocked! This device already marked presenty for Roll No {used_in_session['roll_no']} in this session."
                }), 403

        # Verification proof check
        expected_proof = bt_hash.student_proof(given_hash, student["roll_no"], bt_id, session["session_id"])
        expected_proof_no_dev = bt_hash.student_proof(given_hash, student["roll_no"], "", session["session_id"])
        if client_proof and client_proof not in (expected_proof.lower(), expected_proof_no_dev.lower()):
            return jsonify({"ok": False, "message": "Verification proof mismatch. Please try again."}), 400

        beacon = (session["beacon_name"] or "").strip().upper()
        method = "HASH_ONLY"
        if bt_name:
            if beacon and beacon in bt_name.upper():
                method = "BLUETOOTH_NEARBY"
            else:
                return jsonify({
                    "ok": False,
                    "message": f"Nearby Bluetooth device '{bt_name}' does not match the class beacon ({session['beacon_name']}).",
                }), 400

        already = con.execute(
            "SELECT 1 FROM phone_presenty WHERE session_id=? AND student_id=?",
            (session["session_id"], student["student_id"]),
        ).fetchone()
        if already:
            return jsonify({
                "ok": False,
                "message": f"Presenty already marked for {student['full_name']} ({student['roll_no']}) in this session.",
            }), 400

        # Save presenty record
        con.execute(
            """
            INSERT INTO phone_presenty
            (session_id, student_id, roll_no, bt_device_name, bt_device_id, bluetooth_hash, student_proof, verify_method)
            VALUES (?,?,?,?,?,?,?,?)
            """,
            (
                session["session_id"],
                student["student_id"],
                student["roll_no"],
                bt_name or None,
                dev_key or None,
                given_hash,
                expected_proof,
                method,
            ),
        )
        today = datetime.now().strftime("%Y-%m-%d")
        con.execute(
            "INSERT INTO attendance (student_id, attend_date, status, subject_name) VALUES (?,?, 'Present', ?)",
            (student["student_id"], today, session["subject_name"]),
        )

        # Permanently bind this device to this student
        if dev_key:
            con.execute(
                "INSERT OR REPLACE INTO student_devices (device_id, student_id, roll_no) VALUES (?, ?, ?)",
                (dev_key, student["student_id"], student["roll_no"])
            )

        con.commit()

        # Retrieve marks and academic stats for this student
        marks_info = get_student_marks_data(con, student["student_id"])
        stats_info = get_student_academic_stats(con, student["student_id"])

        return jsonify({
            "ok": True,
            "message": f"Presenty successfully marked for {student['full_name']}!",
            "student": student["full_name"],
            "roll_no": student["roll_no"],
            "subject": session["subject_name"],
            "method": method,
            "marks_data": marks_info,
            "academic_stats": stats_info,
            "notice": notice
        })
    except sqlite3.IntegrityError:
        return jsonify({
            "ok": False,
            "message": f"Presenty already marked for {roll} in this session.",
        }), 400
    finally:
        con.close()


# ======================================================================
# ROLE-BASED AUTHENTICATION APIs
# ======================================================================

@app.post("/api/auth/login")
def api_auth_login():
    data = request.get_json(silent=True) or {}
    username = (data.get("username") or "").strip()
    password = (data.get("password") or "").strip()

    if not username or not password:
        return jsonify({"ok": False, "message": "Username and password are required."}), 400

    con = db()
    try:
        user = con.execute(
            "SELECT user_id, username, role, full_name, email, student_id FROM users WHERE lower(trim(username))=lower(trim(?)) AND password=?",
            (username, password)
        ).fetchone()

        if not user:
            return jsonify({"ok": False, "message": "Invalid username or password."}), 401

        student_data = None
        sid = user["student_id"]
        if sid:
            s_row = con.execute("SELECT * FROM students WHERE student_id=?", (sid,)).fetchone()
            if s_row:
                student_data = dict(s_row)
        else:
            # Check if username itself is a student roll number
            s_row = con.execute("SELECT * FROM students WHERE upper(trim(roll_no))=upper(trim(?))", (username,)).fetchone()
            if s_row:
                student_data = dict(s_row)

        return jsonify({
            "ok": True,
            "user": {
                "user_id": user["user_id"],
                "username": user["username"],
                "role": user["role"],
                "full_name": user["full_name"],
                "email": user["email"],
                "student_id": user["student_id"]
            },
            "student": student_data
        })
    finally:
        con.close()


@app.get("/api/auth/users")
def api_auth_users():
    con = db()
    try:
        rows = con.execute("SELECT user_id, username, role, full_name, email, student_id FROM users ORDER BY role, username").fetchall()
        return jsonify({"ok": True, "users": [dict(r) for r in rows]})
    finally:
        con.close()


# ======================================================================
# LIBRARY MANAGEMENT APIs
# ======================================================================

@app.get("/api/library/books")
def api_library_books():
    q = (request.args.get("q") or "").strip().lower()
    con = db()
    try:
        if q:
            rows = con.execute(
                """
                SELECT * FROM books 
                WHERE lower(title) LIKE ? OR lower(author) LIKE ? OR lower(category) LIKE ? OR lower(isbn) LIKE ?
                ORDER BY title ASC
                """,
                (f"%{q}%", f"%{q}%", f"%{q}%", f"%{q}%")
            ).fetchall()
        else:
            rows = con.execute("SELECT * FROM books ORDER BY title ASC").fetchall()
        return jsonify({"ok": True, "books": [dict(r) for r in rows]})
    finally:
        con.close()


@app.get("/api/library/my-books")
def api_library_my_books():
    roll = (request.args.get("roll_no") or "").strip().upper()
    if not roll:
        return jsonify({"ok": False, "message": "Roll number is required."}), 400
    con = db()
    try:
        rows = con.execute(
            """
            SELECT bi.*, b.title as book_title, b.author, b.shelf_location, s.full_name as student_name
            FROM book_issues bi
            JOIN books b ON bi.book_id = b.book_id
            JOIN students s ON bi.student_id = s.student_id
            WHERE upper(trim(bi.roll_no)) = ?
            ORDER BY bi.issue_id DESC
            """,
            (roll,)
        ).fetchall()
        today = datetime.now().strftime("%Y-%m-%d")
        issues_list = []
        for r in rows:
            d = dict(r)
            # Calculate late fine if overdue
            due = d.get("due_date") or ""
            ret = d.get("return_date") or today
            fine = d.get("fine_amount") or 0.0
            if d.get("status") != "RETURNED" and due and ret > due:
                try:
                    d1 = datetime.strptime(due, "%Y-%m-%d")
                    d2 = datetime.strptime(ret, "%Y-%m-%d")
                    days = max(0, (d2 - d1).days)
                    fine = days * 5.0
                except Exception:
                    pass
            d["computed_fine"] = fine
            issues_list.append(d)
        return jsonify({"ok": True, "issues": issues_list})
    finally:
        con.close()


@app.post("/api/library/issue")
def api_library_issue():
    data = request.get_json(silent=True) or {}
    book_id = data.get("book_id")
    roll = (data.get("roll_no") or "").strip().upper()
    due_days = int(data.get("due_days") or 14)

    if not book_id or not roll:
        return jsonify({"ok": False, "message": "Book ID and Student Roll Number are required."}), 400

    con = db()
    try:
        book = con.execute("SELECT * FROM books WHERE book_id=?", (book_id,)).fetchone()
        if not book or book["available_copies"] <= 0:
            return jsonify({"ok": False, "message": "Book is unavailable or out of stock."}), 400

        student = con.execute("SELECT * FROM students WHERE upper(trim(roll_no))=?", (roll,)).fetchone()
        if not student:
            return jsonify({"ok": False, "message": f"Student with roll number {roll} not found."}), 404

        today = datetime.now().strftime("%Y-%m-%d")
        due = (datetime.now().timestamp() + (due_days * 86400))
        due_str = datetime.fromtimestamp(due).strftime("%Y-%m-%d")

        con.execute(
            """
            INSERT INTO book_issues (book_id, student_id, roll_no, issue_date, due_date, status, remarks)
            VALUES (?, ?, ?, ?, ?, 'ISSUED', 'Issued via Web Portal')
            """,
            (book_id, student["student_id"], student["roll_no"], today, due_str)
        )
        con.execute("UPDATE books SET available_copies = available_copies - 1 WHERE book_id = ?", (book_id,))
        con.commit()
        return jsonify({"ok": True, "message": f"Book issued to {student['full_name']}! Due date: {due_str}."})
    finally:
        con.close()


@app.post("/api/library/return")
def api_library_return():
    data = request.get_json(silent=True) or {}
    issue_id = data.get("issue_id")
    fine = float(data.get("fine_amount") or 0.0)

    if not issue_id:
        return jsonify({"ok": False, "message": "Issue ID is required."}), 400

    con = db()
    try:
        issue = con.execute("SELECT * FROM book_issues WHERE issue_id=?", (issue_id,)).fetchone()
        if not issue or issue["status"] == "RETURNED":
            return jsonify({"ok": False, "message": "Invalid issue record or already returned."}), 400

        today = datetime.now().strftime("%Y-%m-%d")
        con.execute(
            "UPDATE book_issues SET return_date=?, fine_amount=?, status='RETURNED', remarks='Returned' WHERE issue_id=?",
            (today, fine, issue_id)
        )
        con.execute("UPDATE books SET available_copies = available_copies + 1 WHERE book_id = ?", (issue["book_id"],))
        con.commit()
        return jsonify({"ok": True, "message": "Book marked as RETURNED and copies restored."})
    finally:
        con.close()


# ======================================================================
# INTERNAL EXAM & SEAT ALLOCATION APIs
# ======================================================================

@app.get("/api/exams")
def api_exams():
    sem = request.args.get("semester")
    con = db()
    try:
        if sem:
            rows = con.execute("SELECT * FROM internal_exams WHERE semester=? ORDER BY exam_date ASC, start_time ASC", (sem,)).fetchall()
        else:
            rows = con.execute("SELECT * FROM internal_exams ORDER BY exam_date ASC, start_time ASC").fetchall()
        return jsonify({"ok": True, "exams": [dict(r) for r in rows]})
    finally:
        con.close()


@app.get("/api/exam-status")
def api_exam_status():
    roll = (request.args.get("roll_no") or "").strip().upper()
    if not roll:
        return jsonify({"ok": False, "message": "Roll number is required."}), 400

    con = db()
    try:
        student = con.execute("SELECT * FROM students WHERE upper(trim(roll_no))=?", (roll,)).fetchone()
        if not student:
            return jsonify({"ok": False, "message": f"Student with roll number {roll} not found."}), 404

        rows = con.execute(
            """
            SELECT ee.*, ie.exam_name, ie.subject_name, ie.exam_date, ie.start_time, ie.end_time,
                   ie.room_no, ie.max_marks, ie.status as exam_schedule_status
            FROM exam_enrollments ee
            JOIN internal_exams ie ON ee.exam_id = ie.exam_id
            WHERE ee.student_id = ?
            ORDER BY ie.exam_date ASC
            """,
            (student["student_id"],)
        ).fetchall()

        # Also get all upcoming exams for semester to see which ones are not yet enrolled
        available_exams = con.execute(
            "SELECT * FROM internal_exams WHERE semester=? ORDER BY exam_date ASC",
            (student["semester"],)
        ).fetchall()

        enrolled_exam_ids = {r["exam_id"] for r in rows}
        available_list = []
        for ax in available_exams:
            d = dict(ax)
            d["is_enrolled"] = d["exam_id"] in enrolled_exam_ids
            available_list.append(d)

        allocated_seat = None
        for r in rows:
            if r["seat_number"]:
                allocated_seat = r["seat_number"]
                break

        return jsonify({
            "ok": True,
            "student": dict(student),
            "enrollments": [dict(r) for r in rows],
            "available_exams": available_list,
            "allocated_seat": allocated_seat
        })
    finally:
        con.close()


@app.post("/api/exam-enroll")
def api_exam_enroll():
    data = request.get_json(silent=True) or {}
    roll = (data.get("roll_no") or "").strip().upper()
    exam_id = data.get("exam_id")

    if not roll or not exam_id:
        return jsonify({"ok": False, "message": "Roll number and Exam ID are required."}), 400

    con = db()
    try:
        student = con.execute("SELECT * FROM students WHERE upper(trim(roll_no))=?", (roll,)).fetchone()
        if not student:
            return jsonify({"ok": False, "message": f"Student with roll number {roll} not found."}), 404

        exam = con.execute("SELECT * FROM internal_exams WHERE exam_id=?", (exam_id,)).fetchone()
        if not exam:
            return jsonify({"ok": False, "message": "Exam not found."}), 404

        # Check existing enrollment
        existing = con.execute("SELECT * FROM exam_enrollments WHERE exam_id=? AND student_id=?", (exam_id, student["student_id"])).fetchone()
        if existing:
            return jsonify({
                "ok": True,
                "message": f"Already enrolled! Form Status: {existing['form_status']}. Seat No: {existing['seat_number'] or 'Pending'}",
                "status": existing["form_status"],
                "seat_number": existing["seat_number"]
            })

        con.execute(
            """
            INSERT INTO exam_enrollments (exam_id, student_id, roll_no, enrollment_date, form_status, hall_ticket_status, remarks)
            VALUES (?, ?, ?, datetime('now'), 'Pending', 'Not Generated', 'Self-enrolled via Student Portal')
            """,
            (exam_id, student["student_id"], student["roll_no"])
        )
        con.commit()
        return jsonify({
            "ok": True,
            "message": f"Internal Exam Form successfully submitted for {exam['subject_name']}!\nStatus: PENDING APPROVAL.",
            "status": "Pending"
        })
    finally:
        con.close()


@app.post("/api/exam-approve")
def api_exam_approve():
    data = request.get_json(silent=True) or {}
    enrollment_id = data.get("enrollment_id")
    form_status = (data.get("form_status") or "Approved").strip()
    seat_number = (data.get("seat_number") or "").strip()
    remarks = (data.get("remarks") or "Form verified and approved").strip()

    if not enrollment_id:
        return jsonify({"ok": False, "message": "Enrollment ID is required."}), 400

    con = db()
    try:
        ht = "Generated" if form_status.lower() == "approved" else "Not Generated"
        con.execute(
            "UPDATE exam_enrollments SET form_status=?, seat_number=?, hall_ticket_status=?, remarks=? WHERE enrollment_id=?",
            (form_status, seat_number or None, ht, remarks, enrollment_id)
        )
        con.commit()
        return jsonify({"ok": True, "message": f"Exam Form updated to {form_status}! Seat: {seat_number or 'N/A'}."})
    finally:
        con.close()


@app.post("/api/exam-auto-allocate")
def api_exam_auto_allocate():
    data = request.get_json(silent=True) or {}
    exam_id = data.get("exam_id")
    prefix = (data.get("prefix") or "BBA5-S").strip()

    if not exam_id:
        return jsonify({"ok": False, "message": "Exam ID is required."}), 400

    con = db()
    try:
        rows = con.execute(
            "SELECT enrollment_id FROM exam_enrollments WHERE exam_id=? ORDER BY roll_no ASC",
            (exam_id,)
        ).fetchall()

        seq = 101
        for r in rows:
            seat = f"{prefix}{seq}"
            con.execute(
                "UPDATE exam_enrollments SET seat_number=?, form_status='Approved', hall_ticket_status='Generated', remarks='Seat Allocated' WHERE enrollment_id=?",
                (seat, r["enrollment_id"])
            )
            seq += 1

        con.commit()
        return jsonify({"ok": True, "message": f"Successfully allocated sequential seat numbers to {len(rows)} students!"})
    finally:
        con.close()


@app.get("/api/hall-ticket")
def api_hall_ticket():
    enrollment_id = request.args.get("enrollment_id")
    roll = (request.args.get("roll_no") or "").strip().upper()

    con = db()
    try:
        if enrollment_id:
            ee = con.execute(
                """
                SELECT ee.*, ie.exam_name, ie.subject_name, ie.exam_date, ie.start_time, ie.end_time,
                       ie.room_no, ie.max_marks, s.full_name, s.course, s.semester, s.email
                FROM exam_enrollments ee
                JOIN internal_exams ie ON ee.exam_id = ie.exam_id
                JOIN students s ON ee.student_id = s.student_id
                WHERE ee.enrollment_id = ?
                """,
                (enrollment_id,)
            ).fetchone()
        elif roll:
            ee = con.execute(
                """
                SELECT ee.*, ie.exam_name, ie.subject_name, ie.exam_date, ie.start_time, ie.end_time,
                       ie.room_no, ie.max_marks, s.full_name, s.course, s.semester, s.email
                FROM exam_enrollments ee
                JOIN internal_exams ie ON ee.exam_id = ie.exam_id
                JOIN students s ON ee.student_id = s.student_id
                WHERE upper(trim(ee.roll_no)) = ? AND ee.form_status = 'Approved'
                ORDER BY ie.exam_date ASC LIMIT 1
                """,
                (roll,)
            ).fetchone()
        else:
            return jsonify({"ok": False, "message": "enrollment_id or roll_no required."}), 400

        if not ee:
            return jsonify({"ok": False, "message": "Approved hall ticket record not found."}), 404

        return jsonify({"ok": True, "hall_ticket": dict(ee)})
    finally:
        con.close()


def lan_ip():
    import socket

    # 1. Try connecting UDP to internet DNS
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        sock.connect(("8.8.8.8", 80))
        ip = sock.getsockname()[0]
        sock.close()
        if ip and not ip.startswith("127."):
            return ip
    except OSError:
        pass

    # 2. Try looking up host IP interfaces (works completely offline)
    try:
        hostname = socket.gethostname()
        for ip in socket.gethostbyname_ex(hostname)[2]:
            if not ip.startswith("127.") and not ip.startswith("169.254."):
                return ip
    except Exception:
        pass

    return "127.0.0.1"


if __name__ == "__main__":
    ip = lan_ip()
    print("=" * 64)
    print("       College Student Management System - Phone Presenty")
    print("=" * 64)
    print(f"  --> Students open on phone:  http://{ip}:5000")
    print(f"  --> Local browser test:      http://localhost:5000")
    print("  Note: Connect phone to the same Wi-Fi as this PC.")
    print("  Teacher must start a session in the Java app first.")
    print("  [Anti-Proxy: 1 Roll No per device is strictly enforced]")
    print("=" * 64)
    app.run(host="0.0.0.0", port=5000, debug=False)
