package com.college.sms.ui;

import com.college.sms.dao.AcademicStatsDAO;
import com.college.sms.dao.AttendanceDAO;
import com.college.sms.dao.ExamDAO;
import com.college.sms.dao.LibraryDAO;
import com.college.sms.dao.MarksDAO;
import com.college.sms.dao.StudentDAO;
import com.college.sms.model.AcademicStats;
import com.college.sms.model.BookIssue;
import com.college.sms.model.ExamEnrollment;
import com.college.sms.model.Marks;
import com.college.sms.model.Student;
import com.college.sms.model.User;
import com.college.sms.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

/**
 * Modern student profile dashboard showing overall academic overview,
 * attendance, allocated exam seat numbers, and library status in Dark Theme.
 */
public class StudentDashboardPanel extends JPanel {

    private final StudentDAO studentDAO = new StudentDAO();
    private final MarksDAO marksDAO = new MarksDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final AcademicStatsDAO academicStatsDAO = new AcademicStatsDAO();
    private final ExamDAO examDAO = new ExamDAO();
    private final LibraryDAO libraryDAO = new LibraryDAO();

    private final Student student;
    private final DefaultTableModel marksModel;

    public StudentDashboardPanel(User user) {
        Student s = null;
        if (user.getStudentId() != null) {
            s = studentDAO.getStudentById(user.getStudentId());
        }
        if (s == null) {
            s = studentDAO.getStudentByRollNo(user.getUsername());
        }
        if (s == null) {
            List<Student> all = studentDAO.getAllStudents();
            s = all.isEmpty() ? new Student(1, "BBA001", "Student", "BBA CA", 5, "", "") : all.get(0);
        }
        this.student = s;

        setBackground(Theme.BG);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // 1. TOP: Student Profile Header Card
        JPanel pnlProfile = new JPanel(new BorderLayout(10, 10));
        pnlProfile.setBackground(Theme.CARD_BG);
        pnlProfile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, Theme.isDarkMode() ? Theme.ACCENT_EMERALD : Theme.ACCENT_EMERALD_DARK),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.CARD_BORDER, 1),
                        BorderFactory.createEmptyBorder(14, 16, 14, 16)
                )
        ));

        JPanel pnlInfo = new JPanel();
        pnlInfo.setOpaque(false);
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));

        JLabel lblName = new JLabel(student.getFullName());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblName.setForeground(Theme.TEXT_MAIN);

        JLabel lblRoll = new JLabel("Roll No: " + student.getRollNo() + "  |  Course: " + student.getCourse() + " – Sem " + student.getSemester());
        lblRoll.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRoll.setForeground(Theme.isDarkMode() ? Theme.ACCENT_CYAN : Theme.ACCENT_BLUE);

        JLabel lblContact = new JLabel("Email: " + (student.getEmail() != null ? student.getEmail() : "N/A") + "  |  Phone: " + (student.getPhone() != null ? student.getPhone() : "N/A"));
        lblContact.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblContact.setForeground(Theme.TEXT_MUTED);

        pnlInfo.add(lblName);
        pnlInfo.add(Box.createVerticalStrut(4));
        pnlInfo.add(lblRoll);
        pnlInfo.add(Box.createVerticalStrut(2));
        pnlInfo.add(lblContact);

        pnlProfile.add(pnlInfo, BorderLayout.CENTER);
        add(pnlProfile, BorderLayout.NORTH);

        // 2. CENTER: KPI Cards + Marks Table
        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.setBackground(Theme.BG);

        // 4 KPI Summary Cards
        JPanel pnlCards = new JPanel(new GridLayout(1, 4, 10, 10));
        pnlCards.setBackground(Theme.BG);

        // Card 1: Exam Seat Number & Form Status
        String seatNo = "PENDING";
        String formStatus = "Not Enrolled";
        List<ExamEnrollment> enrollments = examDAO.getStudentEnrollments(student.getStudentId());
        for (ExamEnrollment ee : enrollments) {
            formStatus = ee.getFormStatus();
            if (ee.getSeatNumber() != null) {
                seatNo = ee.getSeatNumber();
                break;
            }
        }
        pnlCards.add(createCard("🎓 Internal Exam Seat", seatNo, "Form: " + formStatus, Theme.ACCENT_CYAN));

        // Card 2: Academic Progress (Assignments / Practicals)
        AcademicStats stats = academicStatsDAO.getStatsByStudentId(student.getStudentId());
        String aText = stats != null ? (stats.getAssignmentsCompleted() + "/" + stats.getTotalAssignments() + " Done") : "0/5 Done";
        String pText = stats != null ? (stats.getPracticalsCompleted() + "/" + stats.getTotalPracticals() + " Practicals") : "0/10 Practicals";
        pnlCards.add(createCard("📈 Assignment & Lab", aText, pText, Theme.ACCENT_EMERALD));

        // Card 3: Library Status
        List<BookIssue> issues = libraryDAO.getStudentIssues(student.getStudentId());
        long activeIssues = issues.stream().filter(i -> !"RETURNED".equalsIgnoreCase(i.getStatus())).count();
        pnlCards.add(createCard("📚 Library Borrowed", activeIssues + " Books", activeIssues > 0 ? "Check Due Dates" : "No Overdue Books", Theme.ACCENT_GOLD));

        // Card 4: Classroom Notice
        String notice = academicStatsDAO.getActiveNotice();
        if (notice.length() > 30) notice = notice.substring(0, 27) + "...";
        pnlCards.add(createCard("📢 Teacher Notice", "Live Class Update", notice, new Color(192, 132, 252)));

        pnlCenter.add(pnlCards, BorderLayout.NORTH);

        // Subject Marks Overview Table
        JPanel pnlMarks = new JPanel(new BorderLayout(6, 6));
        pnlMarks.setBackground(Theme.CARD_BG);
        pnlMarks.setBorder(BorderFactory.createTitledBorder("📊 Internal Subject Marks Overview"));

        marksModel = new DefaultTableModel(new String[]{"Subject Name", "Marks Obtained", "Maximum Marks", "Exam Type", "Percentage"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable marksTable = new JTable(marksModel);
        Theme.styleTable(marksTable);
        pnlMarks.add(new JScrollPane(marksTable), BorderLayout.CENTER);

        List<Marks> marksList = marksDAO.getMarksByStudentId(student.getStudentId());
        for (Marks m : marksList) {
            double pct = (m.getMaxMarks() > 0) ? Math.round((m.getMarksObtained() / m.getMaxMarks()) * 1000.0) / 10.0 : 0.0;
            marksModel.addRow(new Object[]{
                    m.getSubjectName(),
                    m.getMarksObtained(),
                    m.getMaxMarks(),
                    m.getExamType(),
                    pct + " %"
            });
        }

        pnlCenter.add(pnlMarks, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);
    }

    private JPanel createCard(String title, String value, String sub, Color accent) {
        JPanel c = new JPanel();
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBackground(Theme.CARD_BG);
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JLabel lblT = new JLabel(title);
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblT.setForeground(Theme.TEXT_MUTED);

        JLabel lblV = new JLabel(value);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblV.setForeground(accent);

        JLabel lblS = new JLabel(sub);
        lblS.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblS.setForeground(Theme.TEXT_MAIN);

        c.add(lblT);
        c.add(Box.createVerticalStrut(4));
        c.add(lblV);
        c.add(Box.createVerticalStrut(4));
        c.add(lblS);

        return c;
    }
}
