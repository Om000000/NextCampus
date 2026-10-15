package com.college.sms.ui;

import com.college.sms.dao.ExamDAO;
import com.college.sms.dao.StudentDAO;
import com.college.sms.model.ExamEnrollment;
import com.college.sms.model.InternalExam;
import com.college.sms.model.Student;
import com.college.sms.model.User;
import com.college.sms.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

/**
 * Student-specific view for Internal Exam Form Status, Enrollment, and Allocated Seat Numbers.
 */
public class StudentExamPanel extends JPanel {

    private final ExamDAO examDAO = new ExamDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final User currentUser;
    private final Student student;

    private final DefaultTableModel examModel;
    private final JTable examTable;
    private final DefaultTableModel myEnrollModel;
    private final JTable myEnrollTable;

    private final JLabel lblStudentBanner = new JLabel();
    private final JLabel lblSeatBanner = new JLabel();

    public StudentExamPanel(User user) {
        this.currentUser = user;
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
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. TOP HEADER: Student Profile & Seat Number Summary
        JPanel pnlHeader = new JPanel();
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.Y_AXIS));
        pnlHeader.setBackground(Theme.CARD_BG);
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        lblStudentBanner.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStudentBanner.setForeground(Theme.isDarkMode() ? Theme.ACCENT_EMERALD : Theme.ACCENT_EMERALD_DARK);
        lblStudentBanner.setText("👤 Student: " + student.getFullName() + " | Roll No: " + student.getRollNo() + " | Course: " + student.getCourse() + " (Sem " + student.getSemester() + ")");

        lblSeatBanner.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSeatBanner.setForeground(Theme.isDarkMode() ? Theme.ACCENT_CYAN : Theme.ACCENT_BLUE);
        updateSeatBanner();

        pnlHeader.add(lblStudentBanner);
        pnlHeader.add(Box.createVerticalStrut(4));
        pnlHeader.add(lblSeatBanner);
        add(pnlHeader, BorderLayout.NORTH);

        // 2. CENTER: Split into 2 sections (Upcoming Exams + My Enrollment Status)
        JPanel pnlCenter = new JPanel(new GridLayout(2, 1, 8, 8));
        pnlCenter.setBackground(Theme.BG);

        // SECTION A: Available / Upcoming Internal Exams for Sem 5
        JPanel pnlUpcoming = new JPanel(new BorderLayout(6, 6));
        pnlUpcoming.setBackground(Theme.CARD_BG);
        pnlUpcoming.setBorder(BorderFactory.createTitledBorder("1. Upcoming & Current Internal Exams (Semester " + student.getSemester() + ")"));

        examModel = new DefaultTableModel(
                new String[]{"Exam ID", "Exam Name", "Subject", "Exam Date", "Time Slot", "Exam Hall", "Max Marks", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        examTable = new JTable(examModel);
        Theme.styleTable(examTable);
        pnlUpcoming.add(new JScrollPane(examTable), BorderLayout.CENTER);

        JPanel pnlUpcomingBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlUpcomingBtns.setBackground(Theme.CARD_BG);
        JButton btnEnroll = new JButton("📝 Submit Exam Enrollment Form for Selected");
        Theme.stylePrimaryButton(btnEnroll);
        btnEnroll.addActionListener(e -> onEnrollSelected());
        pnlUpcomingBtns.add(btnEnroll);
        pnlUpcoming.add(pnlUpcomingBtns, BorderLayout.SOUTH);

        pnlCenter.add(pnlUpcoming);

        // SECTION B: My Exam Form Status & Allocated Seat Numbers
        JPanel pnlMyForms = new JPanel(new BorderLayout(6, 6));
        pnlMyForms.setBackground(Theme.CARD_BG);
        pnlMyForms.setBorder(BorderFactory.createTitledBorder("2. My Exam Form Status & Allocated Seat Numbers"));

        myEnrollModel = new DefaultTableModel(
                new String[]{"Enroll ID", "Subject", "Exam Date", "Time", "Room", "Form Status", "Allocated Seat No", "Hall Ticket", "Remarks"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        myEnrollTable = new JTable(myEnrollModel);
        Theme.styleTable(myEnrollTable);
        pnlMyForms.add(new JScrollPane(myEnrollTable), BorderLayout.CENTER);

        JPanel pnlMyBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlMyBtns.setBackground(Theme.CARD_BG);
        JButton btnViewHallTicket = new JButton("🎫 View & Print Digital Hall Ticket");
        Theme.styleAccentButton(btnViewHallTicket, new Color(25, 90, 160));
        btnViewHallTicket.addActionListener(e -> onViewHallTicket());

        JButton btnRefreshAll = new JButton("🔄 Refresh Status");
        btnRefreshAll.addActionListener(e -> refreshAllData());

        pnlMyBtns.add(btnViewHallTicket);
        pnlMyBtns.add(btnRefreshAll);
        pnlMyForms.add(pnlMyBtns, BorderLayout.SOUTH);

        pnlCenter.add(pnlMyForms);

        add(pnlCenter, BorderLayout.CENTER);

        // Initial Load
        refreshAllData();
    }

    private void updateSeatBanner() {
        List<ExamEnrollment> list = examDAO.getStudentEnrollments(student.getStudentId());
        StringBuilder sb = new StringBuilder();
        int approvedCount = 0;
        String latestSeat = null;
        for (ExamEnrollment ee : list) {
            if ("Approved".equalsIgnoreCase(ee.getFormStatus()) && ee.getSeatNumber() != null) {
                approvedCount++;
                latestSeat = ee.getSeatNumber();
            }
        }
        if (latestSeat != null) {
            sb.append("⚡ ALLOCATED INTERNAL EXAM SEAT NO: ").append(latestSeat)
              .append(" (Approved in ").append(approvedCount).append(" subject exams)");
        } else if (!list.isEmpty()) {
            sb.append("⏳ Exam form is under review by HOD/Exam Cell. Seat number will be allocated once approved.");
        } else {
            sb.append("👉 You have not enrolled for upcoming internal exams. Select an exam below and click 'Submit Exam Enrollment Form'.");
        }
        lblSeatBanner.setText(sb.toString());
    }

    private void refreshAllData() {
        // 1. Upcoming exams
        examModel.setRowCount(0);
        List<InternalExam> exams = examDAO.getExamsBySemester(student.getSemester());
        for (InternalExam ex : exams) {
            examModel.addRow(new Object[]{
                    ex.getExamId(),
                    ex.getExamName(),
                    ex.getSubjectName(),
                    ex.getExamDate(),
                    ex.getStartTime() + " - " + ex.getEndTime(),
                    ex.getRoomNo(),
                    ex.getMaxMarks(),
                    ex.getStatus()
            });
        }

        // 2. My enrollments
        myEnrollModel.setRowCount(0);
        List<ExamEnrollment> myEnrollments = examDAO.getStudentEnrollments(student.getStudentId());
        for (ExamEnrollment ee : myEnrollments) {
            myEnrollModel.addRow(new Object[]{
                    ee.getEnrollmentId(),
                    ee.getSubjectName(),
                    ee.getExamDate(),
                    ee.getStartTime() + " - " + ee.getEndTime(),
                    ee.getRoomNo(),
                    ee.getFormStatus(),
                    ee.getSeatNumber() != null ? ee.getSeatNumber() : "PENDING ALLOCATION",
                    ee.getHallTicketStatus(),
                    ee.getRemarks() != null ? ee.getRemarks() : ""
            });
        }

        updateSeatBanner();
    }

    private void onEnrollSelected() {
        int r = examTable.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Please select an exam from the Upcoming Exams table to enroll.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int examId = Integer.parseInt(examModel.getValueAt(r, 0).toString());
        String sub = examModel.getValueAt(r, 2).toString();

        int confirm = JOptionPane.showConfirmDialog(this,
                "Submit Internal Exam Form for:\nSubject: " + sub + "\nStudent: " + student.getFullName() + " (" + student.getRollNo() + ")?",
                "Confirm Exam Enrollment", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = examDAO.enrollStudent(examId, student.getStudentId(), student.getRollNo(), "Student self-enrolled via portal");
            if (ok) {
                JOptionPane.showMessageDialog(this, "Exam Form successfully submitted!\nStatus: PENDING VERIFICATION\nYour allocated seat number will be displayed upon approval.");
                refreshAllData();
            } else {
                JOptionPane.showMessageDialog(this, "Enrollment failed. You may already be registered.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void onViewHallTicket() {
        int r = myEnrollTable.getSelectedRow();
        if (r < 0) {
            // If none selected, see if there is any approved enrollment
            if (myEnrollModel.getRowCount() > 0) {
                r = 0;
            } else {
                JOptionPane.showMessageDialog(this, "No exam enrollment found.", "Enrollment Required", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String sub = myEnrollModel.getValueAt(r, 1).toString();
        String date = myEnrollModel.getValueAt(r, 2).toString();
        String time = myEnrollModel.getValueAt(r, 3).toString();
        String room = myEnrollModel.getValueAt(r, 4).toString();
        String status = myEnrollModel.getValueAt(r, 5).toString();
        String seat = myEnrollModel.getValueAt(r, 6).toString();

        if (!"Approved".equalsIgnoreCase(status) || "PENDING ALLOCATION".equals(seat)) {
            JOptionPane.showMessageDialog(this,
                    "Exam Form Status: " + status + "\nSeat Number is not allocated yet. Hall ticket will be unlocked once approved by HOD.",
                    "Verification Pending", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dlg = new JDialog((java.awt.Frame) null, "My Hall Ticket – " + student.getRollNo(), true);
        dlg.setSize(520, 440);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(8, 8));

        JTextArea txtTicket = new JTextArea();
        txtTicket.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtTicket.setEditable(false);
        txtTicket.setText(
                "===============================================================\n"
                + "               COLLEGE OF COMPUTER APPLICATIONS\n"
                + "             INTERNAL EXAMINATION ADMIT CARD / HALL TICKET\n"
                + "                    TY BBA (CA) – SEMESTER 5\n"
                + "===============================================================\n\n"
                + "  STUDENT NAME     : " + student.getFullName().toUpperCase() + "\n"
                + "  ROLL NUMBER      : " + student.getRollNo() + "\n"
                + "  COURSE & SEM     : " + student.getCourse() + " – Semester " + student.getSemester() + "\n"
                + "  ALLOCATED SEAT NO: " + seat + "\n"
                + "  EXAMINATION ROOM : " + room + "\n"
                + "  SUBJECT          : " + sub + "\n"
                + "  EXAM DATE & TIME : " + date + " (" + time + ")\n"
                + "  FORM STATUS      : APPROVED & ELIGIBILITY VERIFIED\n\n"
                + "---------------------------------------------------------------\n"
                + "  INSTRUCTIONS FOR CANDIDATES:\n"
                + "  1. Present this Admit Slip along with your College ID Card.\n"
                + "  2. Occupy the seat allotted (" + seat + ") strictly.\n"
                + "  3. No electronic devices, smartwatches, or bags permitted.\n"
                + "===============================================================\n"
                + "  AUTHORIZED CONTROLLER OF EXAMINATIONS / HOD SIGNATURE\n"
                + "===============================================================\n"
        );

        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dlg.dispose());
        JPanel pnlClose = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlClose.add(btnClose);

        dlg.add(new JScrollPane(txtTicket), BorderLayout.CENTER);
        dlg.add(pnlClose, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
}
