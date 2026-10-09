package com.college.sms;

import com.college.sms.dao.AttendanceDAO;
import com.college.sms.dao.MarksDAO;
import com.college.sms.dao.StudentDAO;
import com.college.sms.model.Attendance;
import com.college.sms.model.Marks;
import com.college.sms.model.Student;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Console-based menu for viva-friendly demo.
 */
public class ConsoleApp {

    private final Scanner sc;
    private final StudentDAO studentDAO = new StudentDAO();
    private final MarksDAO marksDAO = new MarksDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();

    public ConsoleApp(Scanner sc) {
        this.sc = sc;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n----- MAIN MENU -----");
            System.out.println("1. Students");
            System.out.println("2. Marks");
            System.out.println("3. Attendance");
            System.out.println("0. Exit");
            System.out.print("Choice: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    studentMenu();
                    break;
                case "2":
                    marksMenu();
                    break;
                case "3":
                    attendanceMenu();
                    break;
                case "0":
                    running = false;
                    System.out.println("Thank you!");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void studentMenu() {
        System.out.println("\n-- Students --");
        System.out.println("1. Add  2. List  3. Search  4. Update  5. Delete  0. Back");
        System.out.print("Choice: ");
        String c = sc.nextLine().trim();
        switch (c) {
            case "1":
                addStudent();
                break;
            case "2":
                listStudents();
                break;
            case "3":
                searchStudents();
                break;
            case "4":
                updateStudent();
                break;
            case "5":
                deleteStudent();
                break;
            default:
                break;
        }
    }

    private void addStudent() {
        System.out.print("Roll No: ");
        String roll = sc.nextLine().trim();
        System.out.print("Full Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Course: ");
        String course = sc.nextLine().trim();
        System.out.print("Semester: ");
        int sem = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Phone: ");
        String phone = sc.nextLine().trim();

        Student s = new Student(roll, name, course, sem, email, phone);
        System.out.println(studentDAO.add(s) ? "Student added." : "Failed to add student.");
    }

    private void listStudents() {
        List<Student> list = studentDAO.getAll();
        if (list.isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        System.out.println("ID | Roll | Name | Course | Sem");
        for (Student s : list) {
            System.out.println(s);
        }
    }

    private void searchStudents() {
        System.out.print("Name contains: ");
        String q = sc.nextLine().trim();
        List<Student> list = studentDAO.findByName(q);
        if (list.isEmpty()) {
            System.out.println("No match.");
            return;
        }
        for (Student s : list) {
            System.out.println(s);
        }
    }

    private void updateStudent() {
        System.out.print("Student ID to update: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        Student existing = studentDAO.findById(id);
        if (existing == null) {
            System.out.println("Not found.");
            return;
        }
        System.out.print("New name [" + existing.getFullName() + "]: ");
        String name = sc.nextLine().trim();
        if (!name.isEmpty()) {
            existing.setFullName(name);
        }
        System.out.print("New phone [" + existing.getPhone() + "]: ");
        String phone = sc.nextLine().trim();
        if (!phone.isEmpty()) {
            existing.setPhone(phone);
        }
        System.out.print("New email [" + existing.getEmail() + "]: ");
        String email = sc.nextLine().trim();
        if (!email.isEmpty()) {
            existing.setEmail(email);
        }
        System.out.println(studentDAO.update(existing) ? "Updated." : "Update failed.");
    }

    private void deleteStudent() {
        System.out.print("Student ID to delete: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        System.out.println(studentDAO.delete(id) ? "Deleted." : "Delete failed.");
    }

    private void marksMenu() {
        System.out.println("\n-- Marks --");
        System.out.println("1. Add  2. List all  3. By student  4. Delete  0. Back");
        System.out.print("Choice: ");
        String c = sc.nextLine().trim();
        switch (c) {
            case "1":
                addMarks();
                break;
            case "2":
                for (Marks m : marksDAO.getAll()) {
                    System.out.println(m.getMarkId() + " | " + m.getStudentName()
                            + " | " + m.getSubjectName() + " | " + m.getMarksObtained()
                            + "/" + m.getMaxMarks());
                }
                break;
            case "3":
                System.out.print("Student ID: ");
                int sid = Integer.parseInt(sc.nextLine().trim());
                for (Marks m : marksDAO.getByStudent(sid)) {
                    System.out.println(m);
                }
                break;
            case "4":
                System.out.print("Mark ID: ");
                int mid = Integer.parseInt(sc.nextLine().trim());
                System.out.println(marksDAO.delete(mid) ? "Deleted." : "Failed.");
                break;
            default:
                break;
        }
    }

    private void addMarks() {
        System.out.print("Student ID: ");
        int sid = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Subject: ");
        String subject = sc.nextLine().trim();
        System.out.print("Marks obtained: ");
        double obtained = Double.parseDouble(sc.nextLine().trim());
        System.out.print("Max marks [100]: ");
        String maxStr = sc.nextLine().trim();
        double max = maxStr.isEmpty() ? 100 : Double.parseDouble(maxStr);
        System.out.print("Exam type [Internal]: ");
        String exam = sc.nextLine().trim();
        if (exam.isEmpty()) {
            exam = "Internal";
        }
        Marks m = new Marks(sid, subject, obtained, max, exam);
        System.out.println(marksDAO.add(m) ? "Marks added." : "Failed.");
    }

    private void attendanceMenu() {
        System.out.println("\n-- Attendance --");
        System.out.println("1. Mark  2. List all  3. By student  4. Delete  0. Back");
        System.out.print("Choice: ");
        String c = sc.nextLine().trim();
        switch (c) {
            case "1":
                markAttendance();
                break;
            case "2":
                for (Attendance a : attendanceDAO.getAll()) {
                    System.out.println(a.getAttendanceId() + " | " + a.getStudentName()
                            + " | " + a.getAttendDate() + " | " + a.getStatus()
                            + " | " + a.getSubjectName());
                }
                break;
            case "3":
                System.out.print("Student ID: ");
                int sid = Integer.parseInt(sc.nextLine().trim());
                for (Attendance a : attendanceDAO.getByStudent(sid)) {
                    System.out.println(a);
                }
                break;
            case "4":
                System.out.print("Attendance ID: ");
                int aid = Integer.parseInt(sc.nextLine().trim());
                System.out.println(attendanceDAO.delete(aid) ? "Deleted." : "Failed.");
                break;
            default:
                break;
        }
    }

    private void markAttendance() {
        System.out.print("Student ID: ");
        int sid = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Date (YYYY-MM-DD) [today]: ");
        String dateStr = sc.nextLine().trim();
        LocalDate date = dateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dateStr);
        System.out.print("Status (Present/Absent/Late): ");
        String status = sc.nextLine().trim();
        System.out.print("Subject: ");
        String subject = sc.nextLine().trim();
        Attendance a = new Attendance(sid, date, status, subject);
        System.out.println(attendanceDAO.add(a) ? "Attendance saved." : "Failed.");
    }
}
