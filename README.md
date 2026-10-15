# NextCampus – College Student Management System

**TY BBA (CA) – Semester 5 Academic Project**

![NextCampus Icon](nextcampus_icon.png)

- **Branding**: NextCampus – modern college ERP & student lifecycle portal
- **Aesthetic**: Integrated Dark Theme (Midnight Navy `#0f172a`, Slate `#1e293b`, Emerald `#10b981`, Tech Cyan `#38bdf8`)
- **App Icon**: Dedicated NextCampus graduation & tech crest (`nextcampus_icon.png`)
- **Technologies**: **Java** (Swing Dark GUI + Console RBAC) and **Python** (Flask Mobile Web Portal + Analytics)
- **Database**: Zero-configuration **SQLite** (`database/college_sms.db` — **no MySQL Server/XAMPP required**)

---

## 🚀 Quick Launch

- **Double-click** `run_NextCampus.bat` in the root folder to compile and launch the application locally!
- Window Title: **"NextCampus – College Student Management System"**
- Login Dialog: **"NextCampus – Role-Based Login"** with one-click demo credentials for viva demonstration.

---

## 🐳 Docker Containerization

NextCampus is fully containerized with **Docker** and **Docker Compose**, bundling **OpenJDK 21**, **Python 3**, and **SQLite** into zero-config containers!

### 1. One-Click Docker Runner

- **Windows**: Double-click `docker-run.bat`
- **Linux / macOS / WSL**: Run `./docker-run.sh`

### 2. Docker Compose Commands

| Service | Command | Access / Purpose |
| :--- | :--- | :--- |
| **Web Portal & Phone Presenty** | `docker compose up -d web` | **http://localhost:5000** (Mobile attendance, hall ticket, student marks) |
| **Interactive Console CLI** | `docker compose run --rm console` | Full CLI menu with HOD, Teacher, Librarian & Student RBAC |
| **In-Browser Swing Desktop GUI** | `docker compose --profile desktop up -d desktop` | **http://localhost:6080/vnc.html** (NextCampus desktop GUI directly in any browser!) |
| **Stop All Containers** | `docker compose --profile desktop down` | Gracefully terminates all containers |
| **View Live Logs** | `docker compose logs -f` | Real-time server and transaction logs |

### 3. Persistent Data Storage

All services share `./database:/app/database`. Any attendance, internal marks, library issues, or exam seat allocations are permanently persisted in `database/college_sms.db`.

### 4. Standalone Docker Commands (Without Compose)

```bash
# Build image
docker build -t nextcampus:latest .

# Run Web Portal
docker run -d -p 5000:5000 -v "${PWD}/database:/app/database" --name nextcampus-web nextcampus:latest

# Run Interactive Console CLI
docker run -it --rm -v "${PWD}/database:/app/database" nextcampus:latest console
```

---

## Project Overview

A complete institutional student management platform featuring:

1. **Role-Based Access Control (RBAC)**: HOD (Admin), Teacher, Librarian, and Student accounts with tailored dashboards.
2. **Library Management System (LMS)**: Catalog search, book issue, returns, and ₹5/day overdue fine calculation.
3. **Internal Exam Management**: Exam form status check, 1-click enrollment, **Allocated Seat Numbers** (`BBA5-S101`, `BBA5-S102`), and printable **Digital Hall Ticket / Admit Card**.
4. **Smart Anti-Proxy Attendance**: Bluetooth rolling cryptographic token verification and hardware device locking.
5. **Academic Progress & Marks**: Assignment certification, lab journals, teacher notices, and real-time GPA tracking.

---

## Folder Structure

```
ty-bba-ca-sem5/
├── run_NextCampus.bat          # 1-Click root launcher for NextCampus
├── nextcampus_icon.png         # NextCampus application icon
├── database/
│   ├── schema.sql              # Table schema & seed data
│   └── college_sms.db          # Embedded SQLite database
├── java/
│   ├── lib/                    # sqlite-jdbc driver
│   ├── run.bat                 # Java compile and launch script
│   └── src/com/college/sms/
│       ├── model/              # User, Student, Book, ExamEnrollment, etc.
│       ├── dao/                # UserDAO, LibraryDAO, ExamDAO, etc.
│       ├── ui/                 # MainFrame, LoginDialog, StudentExamPanel, etc.
│       └── util/               # AppIcon, Theme (Dark Mode), BluetoothHash
├── python/
│   ├── attendance_server.py    # Flask mobile portal (port 5000)
│   ├── templates/presenty.html # Responsive mobile web app
│   └── static/icon.png         # Web portal NextCampus favicon
└── README.md
```

---

## Prerequisites

1. **JDK 8+** (Java)
2. **Python 3.8+**
3. **sqlite-jdbc** JAR (one download, no installer)  
   https://github.com/xerial/sqlite-jdbc/releases  
   Place `sqlite-jdbc-*.jar` in `java/lib/`

You do **not** need MySQL, MySQL Workbench, or XAMPP.

---

## Setup steps

### 1. Add the SQLite driver

Download `sqlite-jdbc-....jar` and copy it into `java/lib/`.

### 2. Run the Java application

Double-click `java/run.bat`  
or:

```bat
cd java
run.bat
```

Choose **1** for Swing GUI or **2** for Console.

The first run creates `database/college_sms.db` with sample students automatically.

### 3. Run Phone Presenty (Mobile Attendance)

1. In the Java app (Swing GUI), switch to the **"Phone Presenty"** tab.
2. Click **"▶ Start Session"** (this generates the live 8-character classroom hash).
3. Click **"🚀 Launch Phone Server"** (or run `run_phone_presenty.bat`).
4. Students connect their phones to the **same Wi-Fi** and open the URL shown on screen (e.g., `http://192.168.x.x:5000`).
5. Students enter their **Roll Number** and the **Live Hash** shown on the teacher's screen, then tap **"Verify & Mark Present"**.
6. Attendance updates automatically in both the phone table and the main Attendance tab!

### 4. Run Python reports

```bat
cd python
pip install -r requirements.txt
python export_and_report.py
python charts.py
```

Python uses the same `college_sms.db` file (built-in `sqlite3` module — no extra database software).

---

## 🔐 Role-Based Access Control (RBAC) Logins

| Role | Default Username | Default Password | Access & Functionality |
|------|------------------|------------------|------------------------|
| **HOD (Admin)** | `admin` (or `hod`) | `admin123` (or `hod123`) | **Full System Authority**: Students, Marks, Attendance, Academic Progress, Timetable, Full Library Management, Exam Scheduling, Exam Form Approvals, Auto Seat Number Allocation, and User Accounts Management. |
| **Teacher** | `teacher` | `teacher123` | **Academic Authority**: Students directory, Marks entry, Attendance & Bluetooth presenty sessions, Classroom notices, Internal exam verification, Library catalog viewer. |
| **Librarian** | `librarian` | `library123` | **Library Authority**: Book catalog (Add/Edit/Delete), Issue books to students, Return books & calculate overdue fines (₹5/day), View active/overdue issues. |
| **Student** | `BBA001` (or `student`) | `bba001` (or `student123`) | **Student Portal**: Personal profile, Attendance & marks, Upcoming internal exams, **Exam Form Status Check & Enrollment**, **Allocated Seat Number display**, **Digital Hall Ticket / Admit Slip**, and Borrowed library books tracker. |

*(Students can also sign in using their assigned roll numbers, e.g. `BBA001`/`bba001`, `BBA002`/`bba002`, `18`/`18`).*

---

## 📚 Library Management System (LMS)

- **Book Catalog**: Real-time tracking of total vs available copies, ISBN, Author, Category, and Shelf location.
- **Issue System**: Validates stock availability, decrements copy count, registers issue date and 14-day due date.
- **Return & Fine System**: Restores available copy count upon return; automatically computes late fine (₹5 per day overdue).
- **Student View**: Students can check all books borrowed by their roll number, due dates, and fine alerts.

---

## 🎓 Internal Exam Form Status, Enrollment & Seat Allocation

- **Exam Scheduling**: HOD / Teachers schedule internal exams (Course, Semester 5, Subject, Date, Time, Room, Max Marks).
- **Student Exam Enrollment**: Students view upcoming/current exams for their semester and submit their Exam Form online with 1 click.
- **Form Status Check**: Real-time status display (`Pending`, `Approved`, `Rejected`).
- **Seat Number Allocation**:
  - **Auto-Allocation**: Generates sequential seat numbers (e.g. `BBA5-S101`, `BBA5-S102`...) for all enrolled students ordered by roll number.
  - **Manual Allocation**: Custom seat numbers can be assigned or updated by the exam cell.
- **Digital Hall Ticket / Admit Card**: Once approved, students can view and print their official Admit Card showing student details, subject, exam date, room/hall, and allocated seat number!

---

## Features by subject

| Subject | What it covers |
|---------|----------------|
| **Java** | OOP, JDBC (SQLite), Swing GUI (Login dialog, dynamic role-based tabs, LibraryPanel, ExamManagementPanel, StudentExamPanel), Console I/O, CRUD |
| **Python** | Flask server with REST API (`/api/auth/login`, `/api/exams`, `/api/exam-status`, `/api/library/books`, `/api/hall-ticket`), SQLite3, Pandas analysis, Charts |

---

## Viva tips (short)

1. **Role-Based Security**: Different user types (HOD, Teacher, Librarian, Student) have dedicated permissions and interfaces in both Java GUI, Console, and Web.
2. **Library Management**: SQLite handles ACID transactions ensuring copy counts cannot become negative.
3. **Internal Exam & Seat Numbers**: Solves manual exam seat allocation in colleges; auto-generates sequential seat numbers and digital hall tickets.
4. **Anti-Proxy Attendance**: Bluetooth rolling hash (30s) + device locking ensures 1 roll number per physical device.
5. **No External Server Required**: Everything runs on embedded SQLite (`database/college_sms.db`).

---

## Author

TY BBA CA – Semester 5  
College Student Management System

