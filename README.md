# TY BBA CA – Semester 5 Project

**Student College Management System**

Subjects used: **Java** (Swing GUI + Console) and **Python** (reports & analysis)  
Database: **SQLite** (a single file — **no MySQL Workbench required**)

---

## Project overview

A college student management system where you can:

- Add, update, delete, and search students
- Record marks and attendance
- View data in a Java Swing desktop app or console menu
- Generate reports and charts using Python scripts
- Students mark **presenty from their phone**; the app checks a **Bluetooth hash** so fake attendance from outside class is rejected

---

## Folder structure

```
ty-bba-ca-sem5/
├── database/
│   ├── schema.sql              # Table design (reference)
│   └── college_sms.db          # Created automatically on first Java run
├── java/
│   ├── lib/                    # Put sqlite-jdbc-*.jar here
│   ├── run.bat                 # Compile + run
│   └── src/com/college/sms/
├── python/
│   ├── requirements.txt
│   ├── export_and_report.py
│   ├── charts.py
│   ├── attendance_server.py    # Phone presenty page
│   └── run_presenty.bat
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

## Features by subject

| Subject | What it covers |
|---------|----------------|
| **Java** | OOP, JDBC (SQLite), Swing GUI, Console I/O, CRUD |
| **Python** | sqlite3, pandas analysis, printed report, charts |

---

## Viva tips (short)

1. Java handles day-to-day CRUD (students, marks, attendance).
2. Data is stored in SQLite tables: `students`, `marks`, `attendance` (file `college_sms.db`).
3. SQLite is a file-based database — no server like MySQL Workbench is needed.
4. Python is used for summary reports and graphs.
5. Swing is for a desktop demo; Console shows logic clearly for viva.

---

## Author

TY BBA CA – Semester 5  
College Student Management System
