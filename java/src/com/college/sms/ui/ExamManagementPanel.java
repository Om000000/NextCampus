package com.college.sms.ui;

import com.college.sms.dao.ExamDAO;
import com.college.sms.dao.StudentDAO;
import com.college.sms.model.ExamEnrollment;
import com.college.sms.model.InternalExam;
import com.college.sms.model.Student;
import com.college.sms.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

/**
 * Interface for HOD (Admin) and Teachers to schedule internal exams,
 * review exam form applications, and allocate seat numbers.
 */
public class ExamManagementPanel extends JPanel {

    private final ExamDAO examDAO = new ExamDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final boolean isHOD;

    // --- Tab 1: Exam Schedules ---
    private final DefaultTableModel examModel;
    private final JTable examTable;
    private final JTextField txtExamId = new JTextField(4);
    private final JTextField txtExamName = new JTextField("Internal Exam 1 (Winter 2026)", 16);
    private final JTextField txtSubject = new JTextField(12);
    private final JTextField txtDate = new JTextField("2026-10-28", 10);
    private final JTextField txtStartTime = new JTextField("10:00 AM", 8);
    private final JTextField txtEndTime = new JTextField("11:30 AM", 8);
    private final JTextField txtRoom = new JTextField("Exam Hall 101", 10);
    private final JTextField txtMaxMarks = new JTextField("40", 5);
    private final JComboBox<String> cmbExamStatus = new JComboBox<>(new String[]{"Upcoming", "Current", "Completed"});

    // --- Tab 2: Enrollments & Seat Allocation ---
    private final JComboBox<String> cmbFilterExam = new JComboBox<>();
    private final DefaultTableModel enrollModel;
    private final JTable enrollTable;
    private final JTextField txtSeatPrefix = new JTextField("BBA5-S", 8);
    private final JTextField txtCustomSeat = new JTextField(10);
    private final JTextField txtEnrollRemarks = new JTextField(15);

    public ExamManagementPanel(boolean isHOD) {
        this.isHOD = isHOD;
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();

        // ==========================================
        // TAB 1: EXAM SCHEDULES
        // ==========================================
        JPanel pnlSchedules = new JPanel(new BorderLayout(8, 8));
        pnlSchedules.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        examModel = new DefaultTableModel(
                new String[]{"ID", "Exam Name", "Course", "Sem", "Subject", "Date", "Start Time", "End Time", "Room", "Max", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        examTable = new JTable(examModel);
        Theme.styleTable(examTable);
        examTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && examTable.getSelectedRow() >= 0) {
                populateExamForm(examTable.getSelectedRow());
            }
        });

        pnlSchedules.add(new JScrollPane(examTable), BorderLayout.CENTER);

        // Form for Add/Edit Exam
        JPanel formExam = new JPanel(new BorderLayout(6, 6));
        JPanel gridExam = new JPanel(new GridLayout(3, 6, 6, 6));
        gridExam.setBorder(BorderFactory.createTitledBorder("Internal Exam Details"));

        txtExamId.setEditable(false);
        gridExam.add(new JLabel("ID:")); gridExam.add(txtExamId);
        gridExam.add(new JLabel("Exam Name:")); gridExam.add(txtExamName);
        gridExam.add(new JLabel("Subject:")); gridExam.add(txtSubject);
        gridExam.add(new JLabel("Date (YYYY-MM-DD):")); gridExam.add(txtDate);
        gridExam.add(new JLabel("Start Time:")); gridExam.add(txtStartTime);
        gridExam.add(new JLabel("End Time:")); gridExam.add(txtEndTime);
        gridExam.add(new JLabel("Room / Hall:")); gridExam.add(txtRoom);
        gridExam.add(new JLabel("Max Marks:")); gridExam.add(txtMaxMarks);
        gridExam.add(new JLabel("Status:")); gridExam.add(cmbExamStatus);

        JPanel pnlExamBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnAddExam = new JButton("➕ Schedule Exam");
        JButton btnUpdateExam = new JButton("✏ Update Schedule");
        JButton btnDeleteExam = new JButton("🗑 Delete Exam");
        JButton btnRefreshExams = new JButton("🔄 Refresh");

        pnlExamBtns.add(btnAddExam);
        pnlExamBtns.add(btnUpdateExam);
        pnlExamBtns.add(btnDeleteExam);
        pnlExamBtns.add(btnRefreshExams);

        formExam.add(gridExam, BorderLayout.CENTER);
        formExam.add(pnlExamBtns, BorderLayout.SOUTH);
        pnlSchedules.add(formExam, BorderLayout.SOUTH);

        btnAddExam.addActionListener(e -> onAddExam());
        btnUpdateExam.addActionListener(e -> onUpdateExam());
        btnDeleteExam.addActionListener(e -> onDeleteExam());
        btnRefreshExams.addActionListener(e -> refreshExamList());

        tabs.addTab("📅 Internal Exam Schedules", pnlSchedules);

        // ==========================================
        // TAB 2: EXAM FORM STATUS & SEAT ALLOCATION
        // ==========================================
        JPanel pnlEnroll = new JPanel(new BorderLayout(8, 8));
        pnlEnroll.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Top Filter Bar
        JPanel pnlTopFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        pnlTopFilter.add(new JLabel("Filter by Exam:"));
        cmbFilterExam.setPreferredSize(new Dimension(320, 26));
        cmbFilterExam.addActionListener(e -> refreshEnrollments());
        pnlTopFilter.add(cmbFilterExam);
        JButton btnRefreshEnroll = new JButton("🔄 Reload");
        btnRefreshEnroll.addActionListener(e -> refreshEnrollments());
        pnlTopFilter.add(btnRefreshEnroll);
        pnlEnroll.add(pnlTopFilter, BorderLayout.NORTH);

        enrollModel = new DefaultTableModel(
                new String[]{"Enroll ID", "Roll No", "Student Name", "Exam Name", "Subject", "Enroll Date", "Form Status", "Allocated Seat No", "Hall Ticket", "Remarks"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        enrollTable = new JTable(enrollModel);
        Theme.styleTable(enrollTable);
        enrollTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && enrollTable.getSelectedRow() >= 0) {
                int r = enrollTable.getSelectedRow();
                Object seat = enrollModel.getValueAt(r, 7);
                txtCustomSeat.setText(seat != null ? seat.toString() : "");
                Object rem = enrollModel.getValueAt(r, 9);
                txtEnrollRemarks.setText(rem != null ? rem.toString() : "");
            }
        });

        pnlEnroll.add(new JScrollPane(enrollTable), BorderLayout.CENTER);

        // Bottom Controls: Form Approval & Auto Seat Allocation
        JPanel pnlActions = new JPanel(new BorderLayout(6, 6));
        pnlActions.setBorder(BorderFactory.createTitledBorder("Exam Form Approval & Seat Number Allocation"));

        JPanel pnlRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        JButton btnApprove = new JButton("✔ Approve Form");
        btnApprove.setBackground(new Color(16, 124, 65));
        btnApprove.setForeground(Color.WHITE);
        btnApprove.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnApprove.setOpaque(true);

        JButton btnReject = new JButton("❌ Reject Form");
        btnReject.setBackground(new Color(180, 40, 40));
        btnReject.setForeground(Color.WHITE);
        btnReject.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnReject.setOpaque(true);

        JButton btnHallTicket = new JButton("🎫 View / Print Hall Ticket");
        btnHallTicket.setBackground(new Color(25, 90, 160));
        btnHallTicket.setForeground(Color.WHITE);
        btnHallTicket.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnHallTicket.setOpaque(true);

        pnlRow1.add(btnApprove);
        pnlRow1.add(btnReject);
        pnlRow1.add(btnHallTicket);

        // Auto Seat Allocation controls
        pnlRow1.add(new JLabel("  |  Seat Prefix:"));
        pnlRow1.add(txtSeatPrefix);
        JButton btnAutoAllocate = new JButton("⚡ Auto-Allocate Seat Numbers");
        btnAutoAllocate.setBackground(new Color(180, 100, 0));
        btnAutoAllocate.setForeground(Color.WHITE);
        btnAutoAllocate.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAutoAllocate.setOpaque(true);
        pnlRow1.add(btnAutoAllocate);

        JPanel pnlRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        pnlRow2.add(new JLabel("Manual Seat No:"));
        pnlRow2.add(txtCustomSeat);
        pnlRow2.add(new JLabel("Remarks:"));
        pnlRow2.add(txtEnrollRemarks);
        JButton btnSaveManualSeat = new JButton("💾 Save Seat & Remarks");
        pnlRow2.add(btnSaveManualSeat);

        pnlActions.add(pnlRow1, BorderLayout.NORTH);
        pnlActions.add(pnlRow2, BorderLayout.SOUTH);
        pnlEnroll.add(pnlActions, BorderLayout.SOUTH);

        btnApprove.addActionListener(e -> onApproveForm());
        btnReject.addActionListener(e -> onRejectForm());
        btnAutoAllocate.addActionListener(e -> onAutoAllocateSeats());
        btnSaveManualSeat.addActionListener(e -> onSaveManualSeat());
        btnHallTicket.addActionListener(e -> onPreviewHallTicket());

        tabs.addTab("🎓 Student Exam Forms & Seat Allocation", pnlEnroll);

        add(tabs, BorderLayout.CENTER);

        // Initial Data Load
        refreshExamList();
        refreshExamDropdown();
        refreshEnrollments();
    }

    private void refreshExamList() {
        examModel.setRowCount(0);
        List<InternalExam> list = examDAO.getAllExams();
        for (InternalExam ex : list) {
            examModel.addRow(new Object[]{
                    ex.getExamId(),
                    ex.getExamName(),
                    ex.getCourse(),
                    ex.getSemester(),
                    ex.getSubjectName(),
                    ex.getExamDate(),
                    ex.getStartTime(),
                    ex.getEndTime(),
                    ex.getRoomNo(),
                    ex.getMaxMarks(),
                    ex.getStatus()
            });
        }
    }

    private void refreshExamDropdown() {
        cmbFilterExam.removeAllItems();
        cmbFilterExam.addItem("0: -- All Internal Exams --");
        List<InternalExam> list = examDAO.getAllExams();
        for (InternalExam ex : list) {
            cmbFilterExam.addItem(ex.getExamId() + ": " + ex.getSubjectName() + " (" + ex.getExamDate() + ")");
        }
    }

    private void refreshEnrollments() {
        enrollModel.setRowCount(0);
        String selected = (String) cmbFilterExam.getSelectedItem();
        int examId = 0;
        if (selected != null && !selected.startsWith("0:")) {
            try {
                examId = Integer.parseInt(selected.split(":")[0].trim());
            } catch (Exception ignored) {}
        }

        List<ExamEnrollment> list = (examId > 0) ? examDAO.getEnrollmentsByExam(examId) : examDAO.getAllEnrollments();
        for (ExamEnrollment ee : list) {
            enrollModel.addRow(new Object[]{
                    ee.getEnrollmentId(),
                    ee.getRollNo(),
                    ee.getStudentName(),
                    ee.getExamName(),
                    ee.getSubjectName(),
                    ee.getEnrollmentDate(),
                    ee.getFormStatus(),
                    ee.getSeatNumber() != null ? ee.getSeatNumber() : "NOT ALLOCATED",
                    ee.getHallTicketStatus(),
                    ee.getRemarks() != null ? ee.getRemarks() : ""
            });
        }
    }

    private void populateExamForm(int row) {
        txtExamId.setText(String.valueOf(examModel.getValueAt(row, 0)));
        txtExamName.setText(String.valueOf(examModel.getValueAt(row, 1)));
        txtSubject.setText(String.valueOf(examModel.getValueAt(row, 4)));
        txtDate.setText(String.valueOf(examModel.getValueAt(row, 5)));
        txtStartTime.setText(String.valueOf(examModel.getValueAt(row, 6)));
        txtEndTime.setText(String.valueOf(examModel.getValueAt(row, 7)));
        txtRoom.setText(String.valueOf(examModel.getValueAt(row, 8)));
        txtMaxMarks.setText(String.valueOf(examModel.getValueAt(row, 9)));
        cmbExamStatus.setSelectedItem(String.valueOf(examModel.getValueAt(row, 10)));
    }

    private void onAddExam() {
        String name = txtExamName.getText().trim();
        String sub = txtSubject.getText().trim();
        String date = txtDate.getText().trim();
        String start = txtStartTime.getText().trim();
        String end = txtEndTime.getText().trim();
        String room = txtRoom.getText().trim();
        String maxStr = txtMaxMarks.getText().trim();
        String status = (String) cmbExamStatus.getSelectedItem();

        if (sub.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Subject name and exam date are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double max = 40.0;
        try {
            max = Double.parseDouble(maxStr);
        } catch (Exception ignored) {}

        InternalExam ex = new InternalExam(0, name, "BBA CA", 5, sub, date, start, end, room, max, status);
        if (examDAO.addExam(ex)) {
            JOptionPane.showMessageDialog(this, "Internal Exam scheduled successfully!");
            refreshExamList();
            refreshExamDropdown();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to schedule exam.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdateExam() {
        if (txtExamId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select an exam from the table to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(txtExamId.getText().trim());
        String name = txtExamName.getText().trim();
        String sub = txtSubject.getText().trim();
        String date = txtDate.getText().trim();
        String start = txtStartTime.getText().trim();
        String end = txtEndTime.getText().trim();
        String room = txtRoom.getText().trim();
        double max = Double.parseDouble(txtMaxMarks.getText().trim());
        String status = (String) cmbExamStatus.getSelectedItem();

        InternalExam ex = new InternalExam(id, name, "BBA CA", 5, sub, date, start, end, room, max, status);
        if (examDAO.updateExam(ex)) {
            JOptionPane.showMessageDialog(this, "Exam schedule updated successfully!");
            refreshExamList();
            refreshExamDropdown();
            refreshEnrollments();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update exam.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDeleteExam() {
        if (txtExamId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select an exam from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(txtExamId.getText().trim());
        int confirm = JOptionPane.showConfirmDialog(this, "Delete this exam and all student enrollments?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (examDAO.deleteExam(id)) {
                JOptionPane.showMessageDialog(this, "Exam deleted.");
                txtExamId.setText("");
                refreshExamList();
                refreshExamDropdown();
                refreshEnrollments();
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete exam.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onApproveForm() {
        int r = enrollTable.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a student enrollment row to approve.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int enrollId = Integer.parseInt(enrollModel.getValueAt(r, 0).toString());
        String roll = enrollModel.getValueAt(r, 1).toString();
        String currentSeat = (String) enrollModel.getValueAt(r, 7);
        if (currentSeat == null || currentSeat.equals("NOT ALLOCATED")) {
            currentSeat = "BBA5-S" + (100 + r + 1);
        }

        examDAO.updateEnrollmentStatus(enrollId, "Approved", currentSeat, "Form approved by Exam Cell");
        JOptionPane.showMessageDialog(this, "Exam Form Approved for Roll No " + roll + "!\nAllocated Seat No: " + currentSeat);
        refreshEnrollments();
    }

    private void onRejectForm() {
        int r = enrollTable.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a student enrollment row to reject.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int enrollId = Integer.parseInt(enrollModel.getValueAt(r, 0).toString());
        String roll = enrollModel.getValueAt(r, 1).toString();
        String reason = JOptionPane.showInputDialog(this, "Enter rejection remarks (e.g. low attendance / pending fees):", "Attendance Shortage");
        if (reason == null) return;

        examDAO.updateEnrollmentStatus(enrollId, "Rejected", null, reason.isEmpty() ? "Application rejected" : reason);
        JOptionPane.showMessageDialog(this, "Exam Form Rejected for Roll No " + roll + ".");
        refreshEnrollments();
    }

    private void onAutoAllocateSeats() {
        String selected = (String) cmbFilterExam.getSelectedItem();
        if (selected == null || selected.startsWith("0:")) {
            JOptionPane.showMessageDialog(this, "Please select a specific exam from the 'Filter by Exam' dropdown above first.", "Exam Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int examId = Integer.parseInt(selected.split(":")[0].trim());
        String prefix = txtSeatPrefix.getText().trim();
        if (prefix.isEmpty()) prefix = "BBA5-S";

        int count = examDAO.autoAllocateSeatNumbers(examId, prefix);
        JOptionPane.showMessageDialog(this, "⚡ Successfully allocated sequential seat numbers to " + count + " students!\nPrefix: " + prefix + " (e.g. " + prefix + "101, " + prefix + "102...)");
        refreshEnrollments();
    }

    private void onSaveManualSeat() {
        int r = enrollTable.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a student enrollment row to update seat.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int enrollId = Integer.parseInt(enrollModel.getValueAt(r, 0).toString());
        String seat = txtCustomSeat.getText().trim();
        String rem = txtEnrollRemarks.getText().trim();

        examDAO.updateEnrollmentStatus(enrollId, "Approved", seat.isEmpty() ? null : seat, rem);
        JOptionPane.showMessageDialog(this, "Seat allocation updated!");
        refreshEnrollments();
    }

    private void onPreviewHallTicket() {
        int r = enrollTable.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Select a student enrollment row to view Hall Ticket.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String roll = enrollModel.getValueAt(r, 1).toString();
        String name = enrollModel.getValueAt(r, 2).toString();
        String examName = enrollModel.getValueAt(r, 3).toString();
        String sub = enrollModel.getValueAt(r, 4).toString();
        String status = enrollModel.getValueAt(r, 6).toString();
        String seat = enrollModel.getValueAt(r, 7).toString();

        if (!"Approved".equalsIgnoreCase(status) || "NOT ALLOCATED".equals(seat)) {
            JOptionPane.showMessageDialog(this, "Hall Ticket can only be generated for APPROVED forms with an allocated seat number.", "Status Pending", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dlg = new JDialog((java.awt.Frame) null, "Official Digital Hall Ticket – " + roll, true);
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
                + "  STUDENT NAME     : " + name.toUpperCase() + "\n"
                + "  ROLL NUMBER      : " + roll + "\n"
                + "  COURSE & SEM     : BBA (CA) – Semester 5\n"
                + "  ALLOCATED SEAT NO: " + seat + "\n"
                + "  EXAM NAME        : " + examName + "\n"
                + "  SUBJECT          : " + sub + "\n"
                + "  STATUS           : FORM APPROVED & ELIGIBILITY VERIFIED\n\n"
                + "---------------------------------------------------------------\n"
                + "  IMPORTANT INSTRUCTIONS FOR STUDENTS:\n"
                + "  1. Candidate must occupy the designated seat number 15 minutes\n"
                + "     before the commencement of the exam.\n"
                + "  2. Bring your College Identity Card and this Admit Slip.\n"
                + "  3. Mobile phones, smartwatches, and unauthorized notes are strictly\n"
                + "     prohibited inside the examination hall.\n"
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
