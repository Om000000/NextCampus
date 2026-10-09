"""
Optional charts for viva / project demo.
Requires: pip install matplotlib pandas
No MySQL needed — uses the same SQLite file as Java.
"""

import sqlite3

import matplotlib.pyplot as plt
import pandas as pd

from db_path import DB_FILE


def main():
    if not DB_FILE.exists():
        print("Database not found:", DB_FILE)
        print("Run the Java app once first (java\\run.bat).")
        return

    con = sqlite3.connect(DB_FILE)
    marks = pd.read_sql(
        """
        SELECT s.full_name, m.subject_name, m.marks_obtained, m.max_marks
        FROM marks m
        JOIN students s ON m.student_id = s.student_id
        """,
        con,
    )
    attendance = pd.read_sql("SELECT status FROM attendance", con)
    con.close()

    if marks.empty:
        print("No marks data to chart.")
        return

    marks["percentage"] = (marks["marks_obtained"] / marks["max_marks"]) * 100

    by_subject = marks.groupby("subject_name")["percentage"].mean()
    plt.figure(figsize=(8, 4))
    by_subject.plot(kind="bar", color="#2c5f8a")
    plt.title("Average Percentage by Subject")
    plt.ylabel("Average %")
    plt.xlabel("Subject")
    plt.xticks(rotation=20, ha="right")
    plt.tight_layout()
    plt.savefig("chart_subject_avg.png")
    print("Saved chart_subject_avg.png")

    by_student = marks.groupby("full_name")["percentage"].mean().sort_values(ascending=False)
    plt.figure(figsize=(8, 4))
    by_student.plot(kind="bar", color="#3d8b6e")
    plt.title("Average Percentage by Student")
    plt.ylabel("Average %")
    plt.xlabel("Student")
    plt.xticks(rotation=25, ha="right")
    plt.tight_layout()
    plt.savefig("chart_student_avg.png")
    print("Saved chart_student_avg.png")

    if not attendance.empty:
        counts = attendance["status"].value_counts()
        plt.figure(figsize=(5, 5))
        counts.plot(kind="pie", autopct="%1.1f%%", startangle=90)
        plt.title("Attendance Status Distribution")
        plt.ylabel("")
        plt.tight_layout()
        plt.savefig("chart_attendance_pie.png")
        print("Saved chart_attendance_pie.png")

    print("Open the PNG files to show charts in your project demo.")


if __name__ == "__main__":
    main()
