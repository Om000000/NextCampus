package com.college.sms;

import com.college.sms.dao.AttendanceDAO;
import com.college.sms.dao.ExamDAO;
import com.college.sms.dao.LibraryDAO;
import com.college.sms.dao.MarksDAO;
import com.college.sms.dao.StudentDAO;
import com.college.sms.dao.UserDAO;
import com.college.sms.model.Attendance;
import com.college.sms.model.Book;
import com.college.sms.model.BookIssue;
import com.college.sms.model.ExamEnrollment;
import com.college.sms.model.InternalExam;
import com.college.sms.model.Marks;
import com.college.sms.model.Student;
import com.college.sms.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/**
 * Console-based menu with Role-Based Authentication (HOD, Teacher, Librarian, Student),
 * Library Management, and Internal Exam Form / Seat Allocation.
 */
public class ConsoleApp {

    private final Scanner sc;
    private final StudentDAO studentDAO = new StudentDAO();
    private final MarksDAO marksDAO = new MarksDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final UserDAO userDAO = new UserDAO();
    private final LibraryDAO libraryDAO = new LibraryDAO();
    private final ExamDAO examDAO = new ExamDAO();

    private User currentUser;

    public ConsoleApp(Scanner sc) {
        this.sc = sc;
    }

    public void start() {
        loginMenu();
        if (currentUser == null) {
            System.out.println("Goodbye!");
            return;
        }

        boolean running = true;
        while (running) {
            System.out.println("\n=======================================================");
            System.out.println("  COLLEGE SMS – Logged in as: " + currentUser.getFullName() + " [" + currentUser.getRole() + "]");
            System.out.println("=======================================================");

            if (currentUser.isHOD()) {
                System.out.println("1. Students Management");
                System.out.println("2. Marks Management");
                System.out.println("3. Attendance");
                System.out.println("4. Library Management (Catalog, Issues, Returns)");
                System.out.println("5. Internal Exams & Seat Allocation");
                System.out.println("6. User Accounts & Roles");
                System.out.println("9. Switch User / Logout");
                System.out.println("0. Exit");
                System.out.print("Choice: ");
                String c = sc.nextLine().trim();
                switch (c) {
                    case "1": studentMenu(); break;
                    case "2": marksMenu(); break;
                    case "3": attendanceMenu(); break;
                    case "4": libraryMenu(false); break;
                    case "5": examMenu(true); break;
                    case "6": userMenu(); break;
                    case "9": loginMenu(); break;
                    case "0": running = false; break;
                    default: System.out.println("Invalid option."); break;
                }
            } else if (currentUser.isTeacher()) {
                System.out.println("1. Students Directory");
                System.out.println("2. Marks Entry & Review");
                System.out.println("3. Attendance Recording");
                System.out.println("4. Internal Exam Schedules & Enrolled Students");
                System.out.println("5. Library Catalog Search");
                System.out.println("9. Switch User / Logout");
                System.out.println("0. Exit");
                System.out.print("Choice: ");
                String c = sc.nextLine().trim();
                switch (c) {
                    case "1": studentMenu(); break;
                    case "2": marksMenu(); break;
                    case "3": attendanceMenu(); break;
                    case "4": examMenu(false); break;
                    case "5": libraryMenu(true); break;
                    case "9": loginMenu(); break;
                    case "0": running = false; break;
                    default: System.out.println("Invalid option."); break;
                }
            } else if (currentUser.isLibrarian()) {
                System.out.println("1. Library Catalog (Add / Search / List Books)");
                System.out.println("2. Issue Book to Student");
                System.out.println("3. Return Book & Calculate Late Fines");
                System.out.println("4. View Active & Overdue Issues");
                System.out.println("5. Student Directory");
                System.out.println("9. Switch User / Logout");
                System.out.println("0. Exit");
                System.out.print("Choice: ");
                String c = sc.nextLine().trim();
                switch (c) {
                    case "1": listAndSearchBooks(); break;
                    case "2": issueBookConsole(); break;
                    case "3": returnBookConsole(); break;
                    case "4": listIssuesConsole(); break;
                    case "5": studentMenu(); break;
                    case "9": loginMenu(); break;
                    case "0": running = false; break;
                    default: System.out.println("Invalid option."); break;
                }
            } else {
                // Student Role
                System.out.println("1. My Profile & Academic Summary");
                System.out.println("2. Internal Exam Form Status & Allocated Seat Number");
                System.out.println("3. Enroll / Apply for Upcoming Internal Exam");
                System.out.println("4. View Digital Hall Ticket");
                System.out.println("5. My Borrowed Library Books & Due Dates");
                System.out.println("6. Search Library Catalog");
                System.out.println("7. My Marks");
                System.out.println("9. Switch User / Logout");
                System.out.println("0. Exit");
                System.out.print("Choice: ");
                String c = sc.nextLine().trim();
                switch (c) {
                    case "1": studentProfileConsole(); break;
                    case "2": studentExamStatusConsole(); break;
                    case "3": studentEnrollExamConsole(); break;
                    case "4": studentHallTicketConsole(); break;
                    case "5": studentMyBooksConsole(); break;
                    case "6": listAndSearchBooks(); break;
                    case "7": studentMarksConsole(); break;
                    case "9": loginMenu(); break;
                    case "0": running = false; break;
                    default: System.out.println("Invalid option."); break;
                }
            }
        }
        System.out.println("Thank you for using College SMS!");
    }

    private void loginMenu() {
        System.out.println("\n-------------------------------------------------------");
        System.out.println("  SYSTEM LOGIN – Select Role or Enter Credentials");
        System.out.println("-------------------------------------------------------");
        System.out.println("1. Quick Login: HOD / Admin (admin / admin123)");
        System.out.println("2. Quick Login: Teacher (teacher / teacher123)");
        System.out.println("3. Quick Login: Librarian (librarian / library123)");
        System.out.println("4. Quick Login: Student Aarav Sharma (BBA001 / bba001)");
        System.out.println("5. Enter Custom Username & Password");
        System.out.println("0. Exit");
        System.out.print("Choice (1-5): ");
        String c = sc.nextLine().trim();

        switch (c) {
            case "1":
                currentUser = userDAO.authenticate("admin", "admin123");
                break;
            case "2":
                currentUser = userDAO.authenticate("teacher", "teacher123");
                break;
            case "3":
                currentUser = userDAO.authenticate("librarian", "library123");
                break;
            case "4":
                currentUser = userDAO.authenticate("BBA001", "bba001");
                break;
            case "5":
                System.out.print("Username / Roll No: ");
                String u = sc.nextLine().trim();
                System.out.print("Password: ");
                String p = sc.nextLine().trim();
                currentUser = userDAO.authenticate(u, p);
                if (currentUser == null) {
                    System.out.println("❌ Invalid credentials! Try again.");
                }
                break;
            case "0":
                currentUser = null;
                return;
            default:
                System.out.println("Invalid selection. Defaulting to HOD.");
                currentUser = userDAO.authenticate("admin", "admin123");
                break;
        }

        if (currentUser != null) {
            System.out.println("✔ Welcome, " + currentUser.getFullName() + " (" + currentUser.getRole() + ")!");
        }
    }

    // --- STUDENT ROLE CONSOLE ACTIONS ---
    private Student resolveStudent() {
        Student s = null;
        if (currentUser.getStudentId() != null) {
            s = studentDAO.getStudentById(currentUser.getStudentId());
        }
        if (s == null) {
            s = studentDAO.getStudentByRollNo(currentUser.getUsername());
        }
        if (s == null) {
            List<Student> all = studentDAO.getAllStudents();
            s = all.isEmpty() ? new Student(1, "BBA001", "Student", "BBA CA", 5, "", "") : all.get(0);
        }
        return s;
    }

    private void studentProfileConsole() {
        Student s = resolveStudent();
        System.out.println("\n-- STUDENT PROFILE --");
        System.out.println("ID        : " + s.getStudentId());
        System.out.println("Roll No   : " + s.getRollNo());
        System.out.println("Full Name : " + s.getFullName());
        System.out.println("Course    : " + s.getCourse());
        System.out.println("Semester  : " + s.getSemester());
        System.out.println("Email     : " + s.getEmail());
        System.out.println("Phone     : " + s.getPhone());
    }

    private void studentExamStatusConsole() {
        Student s = resolveStudent();
        System.out.println("\n-- INTERNAL EXAM FORM STATUS & ALLOCATED SEAT NUMBERS --");
        List<ExamEnrollment> list = examDAO.getStudentEnrollments(s.getStudentId());
        if (list.isEmpty()) {
            System.out.println("You have not applied for any internal exams yet.");
            return;
        }
        for (ExamEnrollment ee : list) {
            System.out.println("Subject       : " + ee.getSubjectName());
            System.out.println("Exam Name     : " + ee.getExamName());
            System.out.println("Date & Time   : " + ee.getExamDate() + " (" + ee.getStartTime() + " - " + ee.getEndTime() + ")");
            System.out.println("Exam Hall     : " + ee.getRoomNo());
            System.out.println("Form Status   : " + ee.getFormStatus().toUpperCase());
            System.out.println("ALLOCATED SEAT: " + (ee.getSeatNumber() != null ? ee.getSeatNumber() : "PENDING ALLOCATION"));
            System.out.println("Hall Ticket   : " + ee.getHallTicketStatus());
            System.out.println("Remarks       : " + (ee.getRemarks() != null ? ee.getRemarks() : ""));
            System.out.println("-------------------------------------------------------");
        }
    }

    private void studentEnrollExamConsole() {
        Student s = resolveStudent();
        System.out.println("\n-- UPCOMING INTERNAL EXAMS AVAILABLE FOR ENROLLMENT --");
        List<InternalExam> exams = examDAO.getExamsBySemester(s.getSemester());
        for (InternalExam ex : exams) {
            System.out.println(ex.getExamId() + " | " + ex.getSubjectName() + " | " + ex.getExamDate() + " | " + ex.getStartTime() + " | " + ex.getRoomNo());
        }
        System.out.print("Enter Exam ID to enroll (or 0 to cancel): ");
        int eid = Integer.parseInt(sc.nextLine().trim());
        if (eid > 0) {
            boolean ok = examDAO.enrollStudent(eid, s.getStudentId(), s.getRollNo(), "Student enrolled via console");
            if (ok) {
                System.out.println("✔ Exam Form submitted! Status: PENDING VERIFICATION.");
                System.out.println("Your allocated seat number will appear once approved by HOD.");
            } else {
                System.out.println("Enrollment failed or already registered.");
            }
        }
    }

    private void studentHallTicketConsole() {
        Student s = resolveStudent();
        List<ExamEnrollment> list = examDAO.getStudentEnrollments(s.getStudentId());
        ExamEnrollment approved = null;
        for (ExamEnrollment ee : list) {
            if ("Approved".equalsIgnoreCase(ee.getFormStatus()) && ee.getSeatNumber() != null) {
                approved = ee;
                break;
            }
        }
        if (approved == null) {
            System.out.println("No approved internal exam enrollment with allocated seat number found.");
            return;
        }

        System.out.println("\n=======================================================");
        System.out.println("       INTERNAL EXAMINATION ADMIT CARD / HALL TICKET   ");
        System.out.println("=======================================================");
        System.out.println("  Student Name : " + s.getFullName().toUpperCase());
        System.out.println("  Roll No      : " + s.getRollNo());
        System.out.println("  Course & Sem : " + s.getCourse() + " Sem " + s.getSemester());
        System.out.println("  ALLOCATED SEAT: " + approved.getSeatNumber());
        System.out.println("  Subject      : " + approved.getSubjectName());
        System.out.println("  Exam Date    : " + approved.getExamDate() + " (" + approved.getStartTime() + " - " + approved.getEndTime() + ")");
        System.out.println("  Hall / Room  : " + approved.getRoomNo());
        System.out.println("  Form Status  : " + approved.getFormStatus().toUpperCase());
        System.out.println("=======================================================");
    }

    private void studentMyBooksConsole() {
        Student s = resolveStudent();
        System.out.println("\n-- MY BORROWED BOOKS --");
        List<BookIssue> list = libraryDAO.getStudentIssues(s.getStudentId());
        if (list.isEmpty()) {
            System.out.println("No books currently borrowed.");
            return;
        }
        String today = LocalDate.now().toString();
        for (BookIssue bi : list) {
            double fine = LibraryDAO.computeLateFine(bi.getDueDate(), today, 5.0);
            System.out.println("Book: " + bi.getBookTitle() + " | Issued: " + bi.getIssueDate() + " | Due: " + bi.getDueDate()
                    + " | Status: " + bi.getStatus() + " | Fine: ₹" + fine);
        }
    }

    private void studentMarksConsole() {
        Student s = resolveStudent();
        System.out.println("\n-- MY MARKS --");
        for (Marks m : marksDAO.getMarksByStudentId(s.getStudentId())) {
            System.out.println(m.getSubjectName() + " : " + m.getMarksObtained() + " / " + m.getMaxMarks() + " (" + m.getExamType() + ")");
        }
    }

    // --- LIBRARY CONSOLE ACTIONS ---
    private void libraryMenu(boolean readOnly) {
        System.out.println("\n-- LIBRARY SYSTEM --");
        System.out.println("1. List & Search Books");
        if (!readOnly) {
            System.out.println("2. Add New Book");
            System.out.println("3. Issue Book to Student");
            System.out.println("4. Return Book & Calculate Fines");
            System.out.println("5. View All Issued Records");
        }
        System.out.println("0. Back");
        System.out.print("Choice: ");
        String c = sc.nextLine().trim();
        switch (c) {
            case "1": listAndSearchBooks(); break;
            case "2": if (!readOnly) addBookConsole(); break;
            case "3": if (!readOnly) issueBookConsole(); break;
            case "4": if (!readOnly) returnBookConsole(); break;
            case "5": if (!readOnly) listIssuesConsole(); break;
            default: break;
        }
    }

    private void listAndSearchBooks() {
        System.out.print("Search keyword (or press Enter for all): ");
        String q = sc.nextLine().trim();
        List<Book> books = q.isEmpty() ? libraryDAO.getAllBooks() : libraryDAO.searchBooks(q);
        System.out.println("\nID | Title | Author | Category | Avail/Total | Shelf");
        System.out.println("----------------------------------------------------------");
        for (Book b : books) {
            System.out.println(b.getBookId() + " | " + b.getTitle() + " | " + b.getAuthor() + " | "
                    + b.getCategory() + " | " + b.getAvailableCopies() + "/" + b.getTotalCopies() + " | " + b.getShelfLocation());
        }
    }

    private void addBookConsole() {
        System.out.print("Book Title: ");
        String t = sc.nextLine().trim();
        System.out.print("Author: ");
        String a = sc.nextLine().trim();
        System.out.print("Category: ");
        String cat = sc.nextLine().trim();
        System.out.print("Total Copies: ");
        int copies = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Shelf Location: ");
        String shelf = sc.nextLine().trim();

        Book b = new Book(0, null, t, a, cat, copies, copies, shelf);
        System.out.println(libraryDAO.addBook(b) ? "✔ Book added." : "Failed to add book.");
    }

    private void issueBookConsole() {
        System.out.print("Enter Book ID: ");
        int bid = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Enter Student ID: ");
        int sid = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Enter Student Roll No: ");
        String roll = sc.nextLine().trim();
        String today = LocalDate.now().toString();
        String due = LocalDate.now().plusDays(14).toString();

        boolean ok = libraryDAO.issueBook(bid, sid, roll, today, due, "Issued via console");
        System.out.println(ok ? "✔ Book successfully issued! Due date: " + due : "Failed. Book may be out of stock.");
    }

    private void returnBookConsole() {
        System.out.print("Enter Issue ID: ");
        int iid = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Enter Fine Amount [0.0]: ");
        String fStr = sc.nextLine().trim();
        double fine = fStr.isEmpty() ? 0.0 : Double.parseDouble(fStr);
        String today = LocalDate.now().toString();

        boolean ok = libraryDAO.returnBook(iid, today, fine, "Returned via console");
        System.out.println(ok ? "✔ Book marked as RETURNED and copies restored." : "Failed to return.");
    }

    private void listIssuesConsole() {
        System.out.println("\nIssue ID | Book Title | Roll No | Issue Date | Due Date | Status | Fine");
        System.out.println("-----------------------------------------------------------------------");
        for (BookIssue bi : libraryDAO.getActiveIssues()) {
            System.out.println(bi.getIssueId() + " | " + bi.getBookTitle() + " | " + bi.getRollNo() + " | "
                    + bi.getIssueDate() + " | " + bi.getDueDate() + " | " + bi.getStatus() + " | ₹" + bi.getFineAmount());
        }
    }

    // --- EXAMS & SEAT ALLOCATION CONSOLE ACTIONS ---
    private void examMenu(boolean isHOD) {
        System.out.println("\n-- INTERNAL EXAMS & SEAT ALLOCATION --");
        System.out.println("1. List All Internal Exams");
        System.out.println("2. View Student Enrollments & Seat Numbers");
        if (isHOD) {
            System.out.println("3. Schedule New Internal Exam");
            System.out.println("4. Approve Student Exam Form");
            System.out.println("5. Auto-Allocate Seat Numbers for Exam");
        }
        System.out.println("0. Back");
        System.out.print("Choice: ");
        String c = sc.nextLine().trim();
        switch (c) {
            case "1":
                for (InternalExam ex : examDAO.getAllExams()) {
                    System.out.println(ex.getExamId() + " | " + ex.getSubjectName() + " | " + ex.getExamDate()
                            + " | " + ex.getStartTime() + " - " + ex.getEndTime() + " | " + ex.getRoomNo() + " | " + ex.getStatus());
                }
                break;
            case "2":
                System.out.println("\nRoll No | Name | Subject | Form Status | Allocated Seat No | Remarks");
                System.out.println("-----------------------------------------------------------------");
                for (ExamEnrollment ee : examDAO.getAllEnrollments()) {
                    System.out.println(ee.getRollNo() + " | " + ee.getStudentName() + " | " + ee.getSubjectName()
                            + " | " + ee.getFormStatus() + " | " + (ee.getSeatNumber() != null ? ee.getSeatNumber() : "NOT ALLOCATED")
                            + " | " + ee.getRemarks());
                }
                break;
            case "3":
                if (isHOD) {
                    System.out.print("Subject Name: ");
                    String sub = sc.nextLine().trim();
                    System.out.print("Exam Date (YYYY-MM-DD): ");
                    String dt = sc.nextLine().trim();
                    System.out.print("Start Time (e.g. 10:00 AM): ");
                    String st = sc.nextLine().trim();
                    System.out.print("End Time (e.g. 11:30 AM): ");
                    String et = sc.nextLine().trim();
                    System.out.print("Room / Hall: ");
                    String rm = sc.nextLine().trim();
                    InternalExam ex = new InternalExam(0, "Internal Exam 1", "BBA CA", 5, sub, dt, st, et, rm, 40, "Upcoming");
                    System.out.println(examDAO.addExam(ex) ? "✔ Exam scheduled." : "Failed.");
                }
                break;
            case "4":
                if (isHOD) {
                    System.out.print("Enter Enrollment ID to approve: ");
                    int eid = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Enter Allocated Seat Number (e.g. BBA5-S101): ");
                    String seat = sc.nextLine().trim();
                    boolean ok = examDAO.updateEnrollmentStatus(eid, "Approved", seat, "Approved by HOD");
                    System.out.println(ok ? "✔ Form approved with Seat No " + seat : "Failed.");
                }
                break;
            case "5":
                if (isHOD) {
                    System.out.print("Enter Exam ID for Auto Seat Allocation: ");
                    int eid = Integer.parseInt(sc.nextLine().trim());
                    System.out.print("Seat Prefix [BBA5-S]: ");
                    String pre = sc.nextLine().trim();
                    int count = examDAO.autoAllocateSeatNumbers(eid, pre.isEmpty() ? "BBA5-S" : pre);
                    System.out.println("✔ Allocated sequential seat numbers to " + count + " students!");
                }
                break;
            default: break;
        }
    }

    // --- USER MANAGEMENT CONSOLE ACTIONS ---
    private void userMenu() {
        System.out.println("\n-- USER ACCOUNTS & ROLES --");
        System.out.println("1. List All Users");
        System.out.println("2. Create New User");
        System.out.println("0. Back");
        System.out.print("Choice: ");
        String c = sc.nextLine().trim();
        switch (c) {
            case "1":
                System.out.println("ID | Username | Role | Full Name | Email");
                System.out.println("----------------------------------------");
                for (User u : userDAO.getAllUsers()) {
                    System.out.println(u.getUserId() + " | " + u.getUsername() + " | " + u.getRole() + " | " + u.getFullName() + " | " + u.getEmail());
                }
                break;
            case "2":
                System.out.print("Username: ");
                String un = sc.nextLine().trim();
                System.out.print("Password: ");
                String pw = sc.nextLine().trim();
                System.out.print("Role (HOD/TEACHER/LIBRARIAN/STUDENT): ");
                String rl = sc.nextLine().trim().toUpperCase();
                System.out.print("Full Name: ");
                String fn = sc.nextLine().trim();
                System.out.print("Email: ");
                String em = sc.nextLine().trim();
                User nu = new User(0, un, pw, rl, fn, em, null);
                System.out.println(userDAO.addUser(nu) ? "✔ User created." : "Failed to create user.");
                break;
            default: break;
        }
    }

    // --- STUDENTS MENU ---
    private void studentMenu() {
        System.out.println("\n-- Students --");
        System.out.println("1. List all  2. Add  3. Search  0. Back");
        System.out.print("Choice: ");
        String c = sc.nextLine().trim();
        switch (c) {
            case "1":
                for (Student s : studentDAO.getAllStudents()) {
                    System.out.println(s.getStudentId() + " | " + s.getRollNo() + " | " + s.getFullName()
                            + " | " + s.getCourse() + " | Sem " + s.getSemester());
                }
                break;
            case "2":
                addStudentConsole();
                break;
            case "3":
                System.out.print("Search name: ");
                String name = sc.nextLine().trim();
                for (Student s : studentDAO.searchByName(name)) {
                    System.out.println(s);
                }
                break;
            default:
                break;
        }
    }

    private void addStudentConsole() {
        System.out.print("Roll No: ");
        String roll = sc.nextLine().trim();
        System.out.print("Full Name: ");
        String name = sc.nextLine().trim();
        System.out.print("Course [BBA CA]: ");
        String course = sc.nextLine().trim();
        if (course.isEmpty()) course = "BBA CA";
        System.out.print("Semester [5]: ");
        String semStr = sc.nextLine().trim();
        int sem = semStr.isEmpty() ? 5 : Integer.parseInt(semStr);
        System.out.print("Email: ");
        String email = sc.nextLine().trim();
        System.out.print("Phone: ");
        String phone = sc.nextLine().trim();

        Student s = new Student(0, roll, name, course, sem, email, phone);
        System.out.println(studentDAO.addStudent(s) ? "Student added." : "Failed.");
    }

    // --- MARKS MENU ---
    private void marksMenu() {
        System.out.println("\n-- Marks --");
        System.out.println("1. Add marks  2. View by student ID  0. Back");
        System.out.print("Choice: ");
        String c = sc.nextLine().trim();
        switch (c) {
            case "1":
                System.out.print("Student ID: ");
                int sid = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Subject: ");
                String sub = sc.nextLine().trim();
                System.out.print("Marks obtained: ");
                double obt = Double.parseDouble(sc.nextLine().trim());
                System.out.print("Max marks [100]: ");
                String mxStr = sc.nextLine().trim();
                double mx = mxStr.isEmpty() ? 100 : Double.parseDouble(mxStr);
                Marks m = new Marks(sid, sub, obt, mx, "Internal");
                System.out.println(marksDAO.add(m) ? "Marks added." : "Failed.");
                break;
            case "2":
                System.out.print("Student ID: ");
                int stid = Integer.parseInt(sc.nextLine().trim());
                for (Marks mk : marksDAO.getMarksByStudentId(stid)) {
                    System.out.println(mk);
                }
                break;
            default:
                break;
        }
    }

    // --- ATTENDANCE MENU ---
    private void attendanceMenu() {
        System.out.println("\n-- Attendance --");
        System.out.println("1. Mark Attendance  2. List Recent  0. Back");
        System.out.print("Choice: ");
        String c = sc.nextLine().trim();
        switch (c) {
            case "1":
                System.out.print("Student ID: ");
                int sid = Integer.parseInt(sc.nextLine().trim());
                System.out.print("Date (YYYY-MM-DD) [today]: ");
                String ds = sc.nextLine().trim();
                LocalDate dt = ds.isEmpty() ? LocalDate.now() : LocalDate.parse(ds);
                System.out.print("Status (Present/Absent/Late): ");
                String st = sc.nextLine().trim();
                System.out.print("Subject: ");
                String sb = sc.nextLine().trim();
                Attendance a = new Attendance(sid, dt, st.isEmpty() ? "Present" : st, sb);
                System.out.println(attendanceDAO.add(a) ? "Attendance saved." : "Failed.");
                break;
            case "2":
                for (Attendance att : attendanceDAO.getAll()) {
                    System.out.println(att.getAttendanceId() + " | " + att.getStudentName()
                            + " | " + att.getAttendDate() + " | " + att.getStatus() + " | " + att.getSubjectName());
                }
                break;
            default:
                break;
        }
    }
}
