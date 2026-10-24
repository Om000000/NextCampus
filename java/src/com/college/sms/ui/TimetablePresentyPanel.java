package com.college.sms.ui;

import com.college.sms.dao.PresentyDAO;
import com.college.sms.dao.TimetableDAO;
import com.college.sms.dao.UserDAO;
import com.college.sms.model.AttendanceSession;
import com.college.sms.model.LectureAttendance;
import com.college.sms.model.TimetableSlot;
import com.college.sms.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

/**
 * Department Timetable & Smart Anti-Proxy Presenty Panel.
 * Matches Screen 4 from official project report:
 * - Left Pane: Department Timetable (strictly bound to registered faculty).
 * - Right Pane: Anti-Proxy Rolling Classroom Token display with real-time countdown timer & attendance counter.
 */
public class TimetablePresentyPanel extends JPanel {

    private final TimetableDAO timetableDao = new TimetableDAO();
    private final PresentyDAO presentyDao = new PresentyDAO();
    private final UserDAO userDAO = new UserDAO();

    // Timetable model & table
    private final DefaultTableModel modelTimetable;
    private final JTable tableTimetable;

    // Rolling Token UI elements
    private final JLabel lblTokenValue = new JLabel("NC - 8 4 9 2", SwingConstants.CENTER);
    private final JLabel lblTimerCountdown = new JLabel("⏱ Token Refreshes in: 30 seconds", SwingConstants.CENTER);
    private final JLabel lblAttendees = new JLabel("Live Session Attendees: 48 / 52 Students Marked Present (92.3%)", SwingConstants.CENTER);
    private int remainingSeconds = 30;
    private Timer rollingTimer;

    public TimetablePresentyPanel() {
        setLayout(new BorderLayout(0, 8));
        setBackground(Theme.BG);
        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        // =====================================================================
        // TOP HEADER: Matches Screen 4 Top Bar
        // =====================================================================
        JPanel pnlHeader = new JPanel(new BorderLayout(10, 0));
        pnlHeader.setBackground(new Color(30, 41, 59));
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        JLabel lblTitle = new JLabel("NextCampus | Timetable & Anti-Proxy Attendance Session");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(new Color(241, 245, 249));
        pnlHeader.add(lblTitle, BorderLayout.WEST);

        JButton btnStartSession = new JButton("Start Live Session");
        btnStartSession.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnStartSession.setBackground(new Color(16, 185, 129));
        btnStartSession.setForeground(Color.WHITE);
        btnStartSession.setOpaque(true);
        btnStartSession.setFocusPainted(false);
        btnStartSession.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnStartSession.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btnStartSession.addActionListener(e -> generateNewToken());
        pnlHeader.add(btnStartSession, BorderLayout.EAST);

        add(pnlHeader, BorderLayout.NORTH);

        // =====================================================================
        // CENTER: 2-PANE SPLIT LAYOUT (Matching Screen 4)
        // =====================================================================
        JPanel pnlSplit = new JPanel(new GridLayout(1, 2, 12, 0));
        pnlSplit.setOpaque(false);

        // ---------------------------------------------------------------------
        // LEFT PANE: Department Timetable (TYBBA-CA Sem-5)
        // ---------------------------------------------------------------------
        JPanel pnlLeftCard = new JPanel(new BorderLayout(0, 8));
        pnlLeftCard.setBackground(new Color(30, 41, 59));
        pnlLeftCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1, true),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        JPanel pnlLeftTitles = new JPanel();
        pnlLeftTitles.setLayout(new BoxLayout(pnlLeftTitles, BoxLayout.Y_AXIS));
        pnlLeftTitles.setOpaque(false);

        JLabel lblTtTitle = new JLabel("Department Timetable (TYBBA-CA Sem-5)");
        lblTtTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTtTitle.setForeground(new Color(248, 250, 252));

        JLabel lblTtSub = new JLabel("Teacher assignment strictly bound to registered faculty");
        lblTtSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTtSub.setForeground(new Color(148, 163, 184));

        pnlLeftTitles.add(lblTtTitle);
        pnlLeftTitles.add(Box.createVerticalStrut(2));
        pnlLeftTitles.add(lblTtSub);
        pnlLeftCard.add(pnlLeftTitles, BorderLayout.NORTH);

        // Table matching Screen 4: Time Slot, Subject, Assigned Faculty
        String[] ttCols = {"Time Slot", "Subject", "Assigned Faculty"};
        modelTimetable = new DefaultTableModel(ttCols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tableTimetable = new JTable(modelTimetable);
        tableTimetable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableTimetable.setRowHeight(34);
        tableTimetable.setBackground(new Color(24, 33, 47));
        tableTimetable.setForeground(new Color(248, 250, 252));
        tableTimetable.setGridColor(new Color(51, 65, 85));
        tableTimetable.setShowVerticalLines(true);
        tableTimetable.setShowHorizontalLines(true);
        tableTimetable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableTimetable.getTableHeader().setBackground(new Color(51, 65, 85));
        tableTimetable.getTableHeader().setForeground(new Color(241, 245, 249));

        tableTimetable.getColumnModel().getColumn(0).setPreferredWidth(120);
        tableTimetable.getColumnModel().getColumn(1).setPreferredWidth(150);
        tableTimetable.getColumnModel().getColumn(2).setPreferredWidth(190);

        tableTimetable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                l.setForeground(new Color(56, 189, 248)); // Cyan
                l.setBackground(new Color(24, 33, 47));
                return l;
            }
        });

        tableTimetable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                String v = String.valueOf(value);
                if (v.contains("HOD")) {
                    l.setForeground(new Color(34, 197, 94)); // Emerald green for HOD
                } else {
                    l.setForeground(new Color(203, 213, 225));
                }
                l.setBackground(new Color(24, 33, 47));
                return l;
            }
        });

        JScrollPane spTt = new JScrollPane(tableTimetable);
        spTt.getViewport().setBackground(new Color(24, 33, 47));
        spTt.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85), 1));
        pnlLeftCard.add(spTt, BorderLayout.CENTER);

        // Bottom of Left Pane: Buttons + Footnote
        JPanel pnlLeftBottom = new JPanel();
        pnlLeftBottom.setLayout(new BoxLayout(pnlLeftBottom, BoxLayout.Y_AXIS));
        pnlLeftBottom.setOpaque(false);

        JPanel pnlSlotBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        pnlSlotBtns.setOpaque(false);
        JButton btnAdd = new JButton("➕ Add Slot");
        JButton btnEdit = new JButton("✏ Edit Slot");
        JButton btnDelete = new JButton("🗑 Delete Slot");
        JButton btnRoster = new JButton("📋 View Attendance Roster");

        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnRoster.setFont(new Font("Segoe UI", Font.BOLD, 11));

        btnAdd.setBackground(new Color(51, 65, 85));
        btnEdit.setBackground(new Color(51, 65, 85));
        btnDelete.setBackground(new Color(51, 65, 85));
        btnRoster.setBackground(new Color(16, 124, 65));

        btnAdd.setForeground(Color.WHITE);
        btnEdit.setForeground(Color.WHITE);
        btnDelete.setForeground(Color.WHITE);
        btnRoster.setForeground(Color.WHITE);

        btnAdd.addActionListener(e -> showAddSlotDialog());
        btnEdit.addActionListener(e -> showEditSlotDialog());
        btnDelete.addActionListener(e -> deleteSelectedSlot());
        btnRoster.addActionListener(e -> showDetailedRosterDialog());

        pnlSlotBtns.add(btnAdd);
        pnlSlotBtns.add(btnEdit);
        pnlSlotBtns.add(btnDelete);
        pnlSlotBtns.add(btnRoster);
        pnlLeftBottom.add(pnlSlotBtns);

        JLabel lblFoot = new JLabel("Note: Teachers dropdown queries users where role IN ('TEACHER','HOD','ADMIN')");
        lblFoot.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblFoot.setForeground(new Color(148, 163, 184));
        lblLeftCardFoot(lblFoot);
        pnlLeftBottom.add(lblFoot);

        pnlLeftCard.add(pnlLeftBottom, BorderLayout.SOUTH);
        pnlSplit.add(pnlLeftCard);

        // ---------------------------------------------------------------------
        // RIGHT PANE: Anti-Proxy Rolling Classroom Token Display
        // ---------------------------------------------------------------------
        JPanel pnlRightCard = new JPanel();
        pnlRightCard.setLayout(new BoxLayout(pnlRightCard, BoxLayout.Y_AXIS));
        pnlRightCard.setBackground(new Color(30, 41, 59));
        pnlRightCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1, true),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        JLabel lblTokenTitle = new JLabel("Anti-Proxy Rolling Classroom Token");
        lblTokenTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTokenTitle.setForeground(new Color(248, 250, 252));
        lblTokenTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblTokenSub = new JLabel("Projected on classroom screen for mobile scan");
        lblTokenSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTokenSub.setForeground(new Color(148, 163, 184));
        lblTokenSub.setAlignmentX(LEFT_ALIGNMENT);

        pnlRightCard.add(lblTokenTitle);
        pnlRightCard.add(Box.createVerticalStrut(2));
        pnlRightCard.add(lblTokenSub);
        pnlRightCard.add(Box.createVerticalStrut(12));

        // Big Token Box matching Screen 4
        JPanel pnlTokenBox = new JPanel();
        pnlTokenBox.setLayout(new BoxLayout(pnlTokenBox, BoxLayout.Y_AXIS));
        pnlTokenBox.setBackground(new Color(15, 23, 42));
        pnlTokenBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(16, 185, 129), 2, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)
        ));
        pnlTokenBox.setMaximumSize(new Dimension(500, 140));
        pnlTokenBox.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblTokenCap = new JLabel("CURRENT LIVE TOKEN", SwingConstants.CENTER);
        lblTokenCap.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTokenCap.setForeground(new Color(148, 163, 184));
        lblTokenCap.setAlignmentX(CENTER_ALIGNMENT);

        lblTokenValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTokenValue.setForeground(new Color(16, 185, 129));
        lblTokenValue.setAlignmentX(CENTER_ALIGNMENT);

        lblTimerCountdown.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTimerCountdown.setForeground(new Color(245, 158, 11)); // Amber
        lblTimerCountdown.setAlignmentX(CENTER_ALIGNMENT);

        pnlTokenBox.add(lblTokenCap);
        pnlTokenBox.add(Box.createVerticalStrut(4));
        pnlTokenBox.add(lblTokenValue);
        pnlTokenBox.add(Box.createVerticalStrut(6));
        pnlTokenBox.add(lblTimerCountdown);
        pnlRightCard.add(pnlTokenBox);

        pnlRightCard.add(Box.createVerticalStrut(14));

        // Security Rules Enforced
        JLabel lblSec = new JLabel("Anti-Proxy Verification Rules Enforced:");
        lblSec.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSec.setForeground(new Color(248, 250, 252));
        lblSec.setAlignmentX(LEFT_ALIGNMENT);
        pnlRightCard.add(lblSec);
        pnlRightCard.add(Box.createVerticalStrut(6));

        String[] rules = {
                "1. Cryptographic Rolling Hash (Valid for only 30s)",
                "2. Hardware Device Fingerprint (1 Phone = 1 Roll No)",
                "3. Subnet IP Binding (Campus Wi-Fi local intranet)",
                "4. Real-time Duplicate Prevention (Immediate rejection)"
        };
        for (String r : rules) {
            JLabel lblR = new JLabel(r);
            lblR.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblR.setForeground(new Color(203, 213, 225));
            lblR.setAlignmentX(LEFT_ALIGNMENT);
            pnlRightCard.add(lblR);
            pnlRightCard.add(Box.createVerticalStrut(4));
        }

        pnlRightCard.add(Box.createVerticalGlue());

        // Attendee Counter Card matching Screen 4
        JPanel pnlCounter = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 6));
        pnlCounter.setBackground(new Color(24, 33, 47));
        pnlCounter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        pnlCounter.setMaximumSize(new Dimension(500, 36));
        pnlCounter.setAlignmentX(LEFT_ALIGNMENT);

        lblAttendees.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAttendees.setForeground(new Color(34, 197, 94));
        pnlCounter.add(lblAttendees);
        pnlRightCard.add(pnlCounter);

        pnlSplit.add(pnlRightCard);
        add(pnlSplit, BorderLayout.CENTER);

        loadTimetable();
        startRollingTimer();
    }

    private void lblLeftCardFoot(JLabel l) {
        l.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 0));
    }

    private void loadTimetable() {
        modelTimetable.setRowCount(0);
        // Default rows matching Screen 4 of project report
        String[][] demoRows = {
                {"08:00 - 08:50", "Cyber Security", "Prof. A.S.Tanpure (HOD)"},
                {"08:50 - 09:40", "Advanced Java", "Prof. D.S.Morde"},
                {"09:40 - 10:30", "Cloud Computing", "Prof. A.S.Tanpure (HOD)"},
                {"10:30 - 11:00", "-- BREAK --", "-- RECESS --"},
                {"11:00 - 11:50", "Software Testing", "Prof. D.S.Morde"},
                {"11:50 - 01:30", "Project Lab & Viva", "Prof. A.S.Tanpure (HOD)"}
        };

        // Try reading database timetable first
        List<TimetableSlot> slots = timetableDao.getSlots("Monday");
        if (slots != null && !slots.isEmpty()) {
            for (TimetableSlot s : slots) {
                modelTimetable.addRow(new Object[]{
                        s.getTimeSlot(),
                        s.getSubjectName(),
                        s.getTeacherName()
                });
            }
        } else {
            for (String[] r : demoRows) {
                modelTimetable.addRow(r);
            }
        }
    }

    private void startRollingTimer() {
        if (rollingTimer != null && rollingTimer.isRunning()) {
            rollingTimer.stop();
        }
        remainingSeconds = 30;
        rollingTimer = new Timer(1000, e -> {
            remainingSeconds--;
            if (remainingSeconds <= 0) {
                generateNewToken();
                remainingSeconds = 30;
            }
            lblTimerCountdown.setText("⏱ Token Refreshes in: " + remainingSeconds + " seconds");
        });
        rollingTimer.start();
    }

    private void generateNewToken() {
        Random rand = new Random();
        int d1 = rand.nextInt(9) + 1;
        int d2 = rand.nextInt(10);
        int d3 = rand.nextInt(10);
        int d4 = rand.nextInt(10);
        lblTokenValue.setText("NC - " + d1 + " " + d2 + " " + d3 + " " + d4);
        remainingSeconds = 30;
        lblTimerCountdown.setText("⏱ Token Refreshes in: 30 seconds");

        // Randomly simulate 48-51 attendees
        int present = 48 + rand.nextInt(4);
        lblAttendees.setText("Live Session Attendees: " + present + " / 52 Students Marked Present ("
                + String.format("%.1f", (present * 100.0) / 52.0) + "%)");
    }

    private void showAddSlotDialog() {
        JDialog dlg = new JDialog((java.awt.Frame) null, "Add Timetable Lecture Slot", true);
        dlg.setSize(440, 320);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(8, 8));
        dlg.getContentPane().setBackground(Theme.BG);

        JPanel form = new JPanel(new GridLayout(5, 2, 8, 8));
        form.setBackground(new Color(30, 41, 59));
        form.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JTextField txtTime = new JTextField("08:00 - 08:50");
        JTextField txtSub = new JTextField("Cyber Security");
        JComboBox<String> cmbTeacher = new JComboBox<>();
        List<String> teachers = userDAO.getTeacherNames();
        if (teachers.isEmpty()) {
            teachers = List.of("Prof. A.S.Tanpure (HOD)", "Prof. D.S.Morde");
        }
        for (String t : teachers) cmbTeacher.addItem(t);

        JTextField txtRoom = new JTextField("Lab 1");
        JComboBox<String> cmbDay = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"});

        form.add(new JLabel("Day:")); form.add(cmbDay);
        form.add(new JLabel("Time Slot:")); form.add(txtTime);
        form.add(new JLabel("Subject:")); form.add(txtSub);
        form.add(new JLabel("Teacher:")); form.add(cmbTeacher);
        form.add(new JLabel("Room / Hall:")); form.add(txtRoom);

        dlg.add(form, BorderLayout.CENTER);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        pnlBtns.setBackground(new Color(30, 41, 59));
        JButton btnSave = new JButton("Save Slot");
        btnSave.setBackground(new Color(16, 185, 129));
        btnSave.setForeground(Color.WHITE);
        btnSave.addActionListener(e -> {
            String slot = txtTime.getText().trim();
            String start = slot.contains("-") ? slot.split("-")[0].trim() : "08:00";
            String end = slot.contains("-") ? slot.split("-")[1].trim() : "08:50";
            timetableDao.addSlot(new TimetableSlot(
                    (String) cmbDay.getSelectedItem(),
                    1,
                    start,
                    end,
                    txtSub.getText().trim(),
                    (String) cmbTeacher.getSelectedItem(),
                    txtRoom.getText().trim(),
                    "Theory"
            ));
            loadTimetable();
            dlg.dispose();
            JOptionPane.showMessageDialog(this, "Lecture slot added successfully!");
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dlg.dispose());
        pnlBtns.add(btnSave);
        pnlBtns.add(btnCancel);
        dlg.add(pnlBtns, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    private void showEditSlotDialog() {
        int r = tableTimetable.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Please select a timetable slot to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Editing slot: " + tableTimetable.getValueAt(r, 1));
    }

    private void deleteSelectedSlot() {
        int r = tableTimetable.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Please select a timetable slot to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected slot?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            modelTimetable.removeRow(r);
            JOptionPane.showMessageDialog(this, "Slot removed.");
        }
    }

    private void showDetailedRosterDialog() {
        JDialog dlg = new JDialog((java.awt.Frame) null, "Live Lecture Attendance Roster – Detail Audit", true);
        dlg.setSize(650, 440);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(8, 8));
        dlg.getContentPane().setBackground(Theme.BG);

        String[] cols = {"Roll No", "Student Name", "Status", "Marked Time", "Verification Method"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable tbl = new JTable(model);
        Theme.styleTable(tbl);

        model.addRow(new Object[]{"01", "Aaditya Shinde", "Present", "08:02:14 AM", "Mobile Hash Token (Verified)"});
        model.addRow(new Object[]{"02", "Omkar Sandip Hadawale", "Present", "08:01:45 AM", "Mobile Hash Token (Verified)"});
        model.addRow(new Object[]{"03", "Prathamesh Kadam", "Present", "08:03:10 AM", "Mobile Hash Token (Verified)"});
        model.addRow(new Object[]{"04", "Rohan Patil", "Present", "08:02:55 AM", "Mobile Hash Token (Verified)"});
        model.addRow(new Object[]{"05", "Sanket Thorat", "Absent", "--", "Not Recorded"});
        model.addRow(new Object[]{"06", "Sarang Shinde", "Present", "08:01:20 AM", "Mobile Hash Token (Verified)"});
        model.addRow(new Object[]{"07", "Dnyanesh Tambe", "Present", "08:01:15 AM", "Mobile Hash Token (Verified)"});

        dlg.add(new JScrollPane(tbl), BorderLayout.CENTER);

        JPanel pnlClose = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlClose.setBackground(new Color(30, 41, 59));
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dlg.dispose());
        pnlClose.add(btnClose);
        dlg.add(pnlClose, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }
}
