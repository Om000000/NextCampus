"""
TY BBA CA Sem 5 – Python report module
Reads student / marks / attendance from SQLite and prints a summary report.
No MySQL or Workbench needed.
"""

import sqlite3

import pandas as pd

from db_path import DB_FILE


def get_connection():
    if not DB_FILE.exists():
        raise FileNotFoundError(
            f"Database not found: {DB_FILE}\n"
            "Run the Java app once first (java\\run.bat). It creates the database automatically."
        )
    con = sqlite3.connect(DB_FILE)
    con.execute("PRAGMA foreign_keys = ON")
    return con


def load_tables(con):
    students = pd.read_sql("SELECT * FROM students", con)
    marks = pd.read_sql(
        """
        SELECT m.mark_id, m.student_id, s.full_name, s.roll_no,
               m.subject_name, m.marks_obtained, m.max_marks, m.exam_type
        FROM marks m
        JOIN students s ON m.student_id = s.student_id
        """,
        con,
    )
    attendance = pd.read_sql(
        """
        SELECT a.attendance_id, a.student_id, s.full_name, s.roll_no,
               a.attend_date, a.status, a.subject_name
        FROM attendance a
        JOIN students s ON a.student_id = s.student_id
        """,
        con,
    )
    return students, marks, attendance


def print_report(students, marks, attendance):
    print("=" * 60)
    print("  COLLEGE STUDENT MANAGEMENT – SUMMARY REPORT")
    print("  (Python analysis module – TY BBA CA Sem 5)")
    print("=" * 60)

    print(f"\nTotal students : {len(students)}")
    print(f"Total mark rows: {len(marks)}")
    print(f"Attendance rows: {len(attendance)}")

    if not marks.empty:
        marks = marks.copy()
        marks["percentage"] = (marks["marks_obtained"] / marks["max_marks"]) * 100

        print("\n--- Average % by subject ---")
        by_subject = marks.groupby("subject_name")["percentage"].mean().round(2)
        print(by_subject.to_string())

        print("\n--- Average % by student ---")
        by_student = (
            marks.groupby(["roll_no", "full_name"])["percentage"]
            .mean()
            .round(2)
            .sort_values(ascending=False)
        )
        print(by_student.to_string())

        print("\n--- Top scorers (overall avg %) ---")
        print(by_student.head(3).to_string())

        print("\n--- Students below 60% average (need attention) ---")
        weak = by_student[by_student < 60]
        if weak.empty:
            print("None")
        else:
            print(weak.to_string())

    if not attendance.empty:
        print("\n--- Attendance status counts ---")
        print(attendance["status"].value_counts().to_string())

        present = attendance[attendance["status"] == "Present"].groupby("full_name").size()
        total = attendance.groupby("full_name").size()
        pct = ((present / total) * 100).fillna(0).round(1)
        print("\n--- Present % by student ---")
        print(pct.sort_values(ascending=False).to_string())

    print("\n" + "=" * 60)
    print("Report complete.")


def main():
    try:
        con = get_connection()
    except Exception as e:
        print("Could not open the SQLite database.")
        print("Error:", e)
        return

    try:
        students, marks, attendance = load_tables(con)
        print_report(students, marks, attendance)

        students.to_csv("students_export.csv", index=False)
        marks.to_csv("marks_export.csv", index=False)
        attendance.to_csv("attendance_export.csv", index=False)
        print("CSV files saved: students_export.csv, marks_export.csv, attendance_export.csv")
    finally:
        con.close()


if __name__ == "__main__":
    main()
