package com.college.sms.ui;

import com.college.sms.dao.PresentyDAO;
import com.college.sms.dao.TimetableDAO;
import com.college.sms.model.AttendanceSession;
import com.college.sms.model.LectureAttendance;
import com.college.sms.model.TimetableSlot;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

/**
 * Teacher-only Timetable & Everyday Lecture Presenty Viewer.
 */
public class TimetablePresentyPanel extends JPanel {

    private final TimetableDAO timetableDao = new TimetableDAO();
    private final PresentyDAO presentyDao = new PresentyDAO();

    // Date & Day controls
    private LocalDate selectedDate = LocalDate.now();
    private final JTextField txtDate = new JTextField(9);
    private final JLabel lblDay = new JLabel("", SwingConstants.LEFT);
    private final JComboBox<String> cmbDayFilter = new JComboBox<>(new String[]{
            "Follow Date", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "All Days"
    });

    // Timetable table & model
    private final DefaultTableModel modelTimetable;
    private final JTable tableTimetable;

    // Lecture attendance roster table & model
    private final DefaultTableModel modelAttendance;
    private final JTable tableAttendance;

    // Header & summary labels
    private final JLabel lblSelectedLecture = new JLabel("Select a lecture from the timetable to view presenty.", SwingConstants.LEFT);
    private final JLabel lblSummaryStats = new JLabel("Attendance: -", SwingConstants.RIGHT);

    private TimetableSlot selectedSlot = null;

    public TimetablePresentyPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // --- 1. TOP BAR: Date Chooser & Filter ---
        JPanel pnlTop = new JPanel(new BorderLayout(8, 8));
        JPanel pnlDate = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));

        txtDate.setText(selectedDate.toString());
        txtDate.setFont(new Font("Monospaced", Font.BOLD, 13));

        JButton btnPrev = new JButton("◀ Prev Day");
        JButton btnToday = new JButton("Today");
        JButton btnNext = new JButton("Next Day ▶");
        JButton btnGoDate = new JButton("Go to Date");

        pnlDate.add(new JLabel("Date (YYYY-MM-DD):"));
        pnlDate.add(txtDate);
        pnlDate.add(btnGoDate);
        pnlDate.add(btnPrev);
        pnlDate.add(btnToday);
        pnlDate.add(btnNext);

        lblDay.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblDay.setForeground(new Color(15, 80, 150));
        pnlDate.add(lblDay);

        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        pnlFilter.add(new JLabel("Day Filter:"));
        pnlFilter.add(cmbDayFilter);

        pnlTop.add(pnlDate, BorderLayout.WEST);
        pnlTop.add(pnlFilter, BorderLayout.EAST);
        add(pnlTop, BorderLayout.NORTH);

        // --- 2. LEFT: Timetable Table ---
        modelTimetable = new DefaultTableModel(
                new String[]{"ID", "Day", "Lec #", "Time Slot", "Subject", "Type", "Teacher", "Room", "Presenty Count"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableTimetable = new JTable(modelTimetable);
        tableTimetable.setRowHeight(24);
        tableTimetable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tableTimetable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tableTimetable.getSelectedRow() >= 0) {
                onLectureSelected();
            }
        });

        JPanel pnlTimetable = new JPanel(new BorderLayout(4, 4));
        pnlTimetable.setBorder(BorderFactory.createTitledBorder("📅 Everyday Lecture Timetable"));
        pnlTimetable.add(new JScrollPane(tableTimetable), BorderLayout.CENTER);

        JPanel pnlTtButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        JButton btnAddSlot = new JButton("➕ Add Lecture");
        JButton btnEditSlot = new JButton("✏ Edit Slot");
        JButton btnDeleteSlot = new JButton("🗑 Delete Slot");
        JButton btnStartPresenty = new JButton("🚀 Start Phone Presenty for Lecture");
        btnStartPresenty.setBackground(new Color(30, 130, 60));
        btnStartPresenty.setForeground(Color.WHITE);
        btnStartPresenty.setOpaque(true);

        pnlTtButtons.add(btnAddSlot);
        pnlTtButtons.add(btnEditSlot);
        pnlTtButtons.add(btnDeleteSlot);
        pnlTtButtons.add(btnStartPresenty);
        pnlTimetable.add(pnlTtButtons, BorderLayout.SOUTH);

        // --- 3. RIGHT: Lecture Attendance Roster ---
        modelAttendance = new DefaultTableModel(
                new String[]{"Student ID", "Roll No", "Student Name", "Status", "Marked Time", "Verification Method"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableAttendance = new JTable(modelAttendance);
        tableAttendance.setRowHeight(24);
        tableAttendance.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        tableAttendance.setDefaultRenderer(Object.class, new StatusCellRenderer());

        JPanel pnlRoster = new JPanel(new BorderLayout(4, 4));
        pnlRoster.setBorder(BorderFactory.createTitledBorder("📋 Lecture Presenty Roster (Everyday & Lecture-wise)"));

        JPanel pnlRosterHeader = new JPanel(new BorderLayout(4, 4));
        lblSelectedLecture.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblSelectedLecture.setForeground(new Color(20, 90, 50));
        lblSummaryStats.setFont(new Font("SansSerif", Font.BOLD, 13));
        pnlRosterHeader.add(lblSelectedLecture, BorderLayout.WEST);
        pnlRosterHeader.add(lblSummaryStats, BorderLayout.EAST);
        pnlRoster.add(pnlRosterHeader, BorderLayout.NORTH);
        pnlRoster.add(new JScrollPane(tableAttendance), BorderLayout.CENTER);

        JPanel pnlRosterButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        JButton btnMarkPresent = new JButton("✔ Mark Present");
        JButton btnMarkAbsent = new JButton("❌ Mark Absent");
        JButton btnMarkAll = new JButton("🌟 Mark All Present");
        JButton btnRefreshRoster = new JButton("🔄 Refresh Presenty");

        btnMarkPresent.setBackground(new Color(25, 120, 55));
        btnMarkPresent.setForeground(Color.WHITE);
        btnMarkPresent.setOpaque(true);

        btnMarkAbsent.setBackground(new Color(170, 30, 30));
        btnMarkAbsent.setForeground(Color.WHITE);
        btnMarkAbsent.setOpaque(true);

        pnlRosterButtons.add(btnMarkPresent);
        pnlRosterButtons.add(btnMarkAbsent);
        pnlRosterButtons.add(btnMarkAll);
        pnlRosterButtons.add(btnRefreshRoster);
        pnlRoster.add(pnlRosterButtons, BorderLayout.SOUTH);

        // --- 4. Split Pane combining Timetable (top/left) and Lecture Presenty (bottom/right) ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnlTimetable, pnlRoster);
        splitPane.setDividerLocation(260);
        splitPane.setResizeWeight(0.45);
        add(splitPane, BorderLayout.CENTER);

        // --- Action Listeners ---
        btnPrev.addActionListener(e -> {
            selectedDate = selectedDate.minusDays(1);
            updateDateUI();
        });
        btnToday.addActionListener(e -> {
            selectedDate = LocalDate.now();
            updateDateUI();
        });
        btnNext.addActionListener(e -> {
            selectedDate = selectedDate.plusDays(1);
            updateDateUI();
        });
        btnGoDate.addActionListener(e -> {
            try {
                selectedDate = LocalDate.parse(txtDate.getText().trim());
                updateDateUI();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
            }
        });
        cmbDayFilter.addActionListener(e -> refreshTimetable());

        btnAddSlot.addActionListener(e -> showAddSlotDialog());
        btnEditSlot.addActionListener(e -> showEditSlotDialog());
        btnDeleteSlot.addActionListener(e -> deleteSelectedSlot());
        btnStartPresenty.addActionListener(e -> startPhonePresentyForSelectedLecture());

        btnMarkPresent.addActionListener(e -> toggleStudentStatus("Present"));
        btnMarkAbsent.addActionListener(e -> toggleStudentStatus("Absent"));
        btnMarkAll.addActionListener(e -> markAllStudentsPresent());
        btnRefreshRoster.addActionListener(e -> loadLectureAttendance());

        updateDateUI();
    }

    private void updateDateUI() {
        txtDate.setText(selectedDate.toString());
        String dayName = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        lblDay.setText("📅 " + dayName);
        refreshTimetable();
    }

    private void refreshTimetable() {
        modelTimetable.setRowCount(0);
        String filter = String.valueOf(cmbDayFilter.getSelectedItem());
        String effectiveDay;

        if ("Follow Date".equalsIgnoreCase(filter)) {
            effectiveDay = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        } else {
            effectiveDay = filter;
        }

        String curDateStr = selectedDate.toString();
        List<TimetableSlot> slots = timetableDao.getSlots(effectiveDay);

        for (TimetableSlot s : slots) {
            int[] summary = timetableDao.getLectureAttendanceSummary(curDateStr, s.getSubjectName());
            String statsStr = summary[0] > 0 ? (summary[1] + " / " + summary[0] + " Present (" + Math.round((summary[1] * 100.0) / summary[0]) + "%)") : "0 Present";

            modelTimetable.addRow(new Object[]{
                    s.getTimetableId(),
                    s.getDayOfWeek(),
                    s.getLectureNumber(),
                    s.getTimeSlot(),
                    s.getSubjectName(),
                    s.getLectureType(),
                    s.getTeacherName(),
                    s.getRoomNo(),
                    statsStr
            });
        }

        if (slots.size() > 0) {
            tableTimetable.setRowSelectionInterval(0, 0);
            onLectureSelected();
        } else {
            selectedSlot = null;
            modelAttendance.setRowCount(0);
            lblSelectedLecture.setText("No lectures scheduled for " + effectiveDay + ".");
            lblSummaryStats.setText("Attendance: -");
        }
    }

    private void onLectureSelected() {
        int row = tableTimetable.getSelectedRow();
        if (row < 0) return;

        int tid = (Integer) modelTimetable.getValueAt(row, 0);
        String day = String.valueOf(modelTimetable.getValueAt(row, 1));
        int lecNum = (Integer) modelTimetable.getValueAt(row, 2);
        String time = String.valueOf(modelTimetable.getValueAt(row, 3));
        String subject = String.valueOf(modelTimetable.getValueAt(row, 4));
        String type = String.valueOf(modelTimetable.getValueAt(row, 5));
        String teacher = String.valueOf(modelTimetable.getValueAt(row, 6));
        String room = String.valueOf(modelTimetable.getValueAt(row, 7));

        String[] times = time.split("-");
        String start = times.length > 0 ? times[0].trim() : "09:00 AM";
        String end = times.length > 1 ? times[1].trim() : "10:00 AM";

        selectedSlot = new TimetableSlot(day, lecNum, start, end, subject, teacher, room, type);
        selectedSlot.setTimetableId(tid);

        lblSelectedLecture.setText("Lecture " + lecNum + ": " + subject + " (" + type + ") | " + time + " | " + room + " | " + selectedDate);
        loadLectureAttendance();
    }

    private void loadLectureAttendance() {
        if (selectedSlot == null) return;
        modelAttendance.setRowCount(0);
        String curDateStr = selectedDate.toString();
        List<LectureAttendance> list = timetableDao.getLectureAttendance(curDateStr, selectedSlot.getSubjectName());

        int total = list.size();
        int present = 0;

        for (LectureAttendance la : list) {
            boolean isPres = "Present".equalsIgnoreCase(la.getStatus()) || "Late".equalsIgnoreCase(la.getStatus());
            if (isPres) present++;

            modelAttendance.addRow(new Object[]{
                    la.getStudentId(),
                    la.getRollNo(),
                    la.getStudentName(),
                    la.getStatus(),
                    la.getMarkedAt().isEmpty() ? "-" : la.getMarkedAt(),
                    la.getVerifyMethod()
            });
        }

        int pct = total > 0 ? (int) Math.round((present * 100.0) / total) : 0;
        lblSummaryStats.setText("Total: " + total + "  |  Present: " + present + "  |  Absent: " + (total - present) + "  |  Rate: " + pct + "%");
    }

    private void toggleStudentStatus(String targetStatus) {
        if (selectedSlot == null) {
            JOptionPane.showMessageDialog(this, "Please select a lecture first.");
            return;
        }
        int row = tableAttendance.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student from the attendance table.");
            return;
        }
        int studentId = (Integer) modelAttendance.getValueAt(row, 0);
        String studentName = String.valueOf(modelAttendance.getValueAt(row, 2));

        if (timetableDao.setStudentAttendance(studentId, selectedDate.toString(), selectedSlot.getSubjectName(), targetStatus)) {
            loadLectureAttendance();
            // Refresh counts in timetable table too
            updateSelectedRowTimetableCount();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update attendance.");
        }
    }

    private void markAllStudentsPresent() {
        if (selectedSlot == null) {
            JOptionPane.showMessageDialog(this, "Please select a lecture first.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Mark ALL students Present for " + selectedSlot.getSubjectName() + " on " + selectedDate + "?",
                "Mark All Present", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            timetableDao.markAllPresent(selectedDate.toString(), selectedSlot.getSubjectName());
            loadLectureAttendance();
            updateSelectedRowTimetableCount();
        }
    }

    private void updateSelectedRowTimetableCount() {
        int row = tableTimetable.getSelectedRow();
        if (row >= 0 && selectedSlot != null) {
            int[] summary = timetableDao.getLectureAttendanceSummary(selectedDate.toString(), selectedSlot.getSubjectName());
            String statsStr = summary[0] > 0 ? (summary[1] + " / " + summary[0] + " Present (" + Math.round((summary[1] * 100.0) / summary[0]) + "%)") : "0 Present";
            modelTimetable.setValueAt(statsStr, row, 8);
        }
    }

    private void startPhonePresentyForSelectedLecture() {
        if (selectedSlot == null) {
            JOptionPane.showMessageDialog(this, "Please select a lecture from the timetable first.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Start Live Phone Presenty Session for:\n"
                        + "Subject: " + selectedSlot.getSubjectName() + "\n"
                        + "Lecture: " + selectedSlot.getLectureNumber() + " (" + selectedSlot.getTimeSlot() + ")?\n\n"
                        + "This will create a live rolling Bluetooth hash session for students to mark attendance from their phones.",
                "Start Phone Presenty", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            AttendanceSession session = presentyDao.startSession(selectedSlot.getSubjectName(), "SMS-CLASS", 15);
            if (session != null) {
                JOptionPane.showMessageDialog(this,
                        "Phone Presenty Session Started for " + selectedSlot.getSubjectName() + "!\n\n"
                                + "Session ID: " + session.getSessionId() + "\n"
                                + "Duration: 15 minutes\n\n"
                                + "Students can now mark attendance on their phones at:\n"
                                + "http://" + detectLocalIp() + ":5000\n\n"
                                + "Switch to the 'Phone Presenty' tab to view the live hash!");
            }
        }
    }

    private void showAddSlotDialog() {
        JTextField txtDay = new JTextField(String.valueOf(cmbDayFilter.getSelectedItem()).equals("All Days") ? "Monday" : String.valueOf(cmbDayFilter.getSelectedItem()));
        if (txtDay.getText().equals("Follow Date")) {
            txtDay.setText(selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        }
        JTextField txtLecNum = new JTextField("1");
        JTextField txtStart = new JTextField("09:00 AM");
        JTextField txtEnd = new JTextField("10:00 AM");
        JTextField txtSubject = new JTextField("Java Programming");
        JTextField txtTeacher = new JTextField("Prof. Sharma");
        JTextField txtRoom = new JTextField("Room 302");
        JComboBox<String> cmbType = new JComboBox<>(new String[]{"Theory", "Practical"});

        JPanel form = new JPanel(new GridLayout(8, 2, 6, 6));
        form.add(new JLabel("Day of Week:")); form.add(txtDay);
        form.add(new JLabel("Lecture Number:")); form.add(txtLecNum);
        form.add(new JLabel("Start Time:")); form.add(txtStart);
        form.add(new JLabel("End Time:")); form.add(txtEnd);
        form.add(new JLabel("Subject:")); form.add(txtSubject);
        form.add(new JLabel("Teacher:")); form.add(txtTeacher);
        form.add(new JLabel("Room / Lab:")); form.add(txtRoom);
        form.add(new JLabel("Type:")); form.add(cmbType);

        int res = JOptionPane.showConfirmDialog(this, form, "Add Timetable Lecture Slot", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                TimetableSlot s = new TimetableSlot(
                        txtDay.getText().trim(),
                        Integer.parseInt(txtLecNum.getText().trim()),
                        txtStart.getText().trim(),
                        txtEnd.getText().trim(),
                        txtSubject.getText().trim(),
                        txtTeacher.getText().trim(),
                        txtRoom.getText().trim(),
                        String.valueOf(cmbType.getSelectedItem())
                );
                if (timetableDao.addSlot(s)) {
                    refreshTimetable();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add slot.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void showEditSlotDialog() {
        if (selectedSlot == null) {
            JOptionPane.showMessageDialog(this, "Please select a slot to edit.");
            return;
        }
        JTextField txtDay = new JTextField(selectedSlot.getDayOfWeek());
        JTextField txtLecNum = new JTextField(String.valueOf(selectedSlot.getLectureNumber()));
        JTextField txtStart = new JTextField(selectedSlot.getStartTime());
        JTextField txtEnd = new JTextField(selectedSlot.getEndTime());
        JTextField txtSubject = new JTextField(selectedSlot.getSubjectName());
        JTextField txtTeacher = new JTextField(selectedSlot.getTeacherName());
        JTextField txtRoom = new JTextField(selectedSlot.getRoomNo());
        JComboBox<String> cmbType = new JComboBox<>(new String[]{"Theory", "Practical"});
        cmbType.setSelectedItem(selectedSlot.getLectureType());

        JPanel form = new JPanel(new GridLayout(8, 2, 6, 6));
        form.add(new JLabel("Day of Week:")); form.add(txtDay);
        form.add(new JLabel("Lecture Number:")); form.add(txtLecNum);
        form.add(new JLabel("Start Time:")); form.add(txtStart);
        form.add(new JLabel("End Time:")); form.add(txtEnd);
        form.add(new JLabel("Subject:")); form.add(txtSubject);
        form.add(new JLabel("Teacher:")); form.add(txtTeacher);
        form.add(new JLabel("Room / Lab:")); form.add(txtRoom);
        form.add(new JLabel("Type:")); form.add(cmbType);

        int res = JOptionPane.showConfirmDialog(this, form, "Edit Timetable Lecture Slot", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                selectedSlot.setDayOfWeek(txtDay.getText().trim());
                selectedSlot.setLectureNumber(Integer.parseInt(txtLecNum.getText().trim()));
                selectedSlot.setStartTime(txtStart.getText().trim());
                selectedSlot.setEndTime(txtEnd.getText().trim());
                selectedSlot.setSubjectName(txtSubject.getText().trim());
                selectedSlot.setTeacherName(txtTeacher.getText().trim());
                selectedSlot.setRoomNo(txtRoom.getText().trim());
                selectedSlot.setLectureType(String.valueOf(cmbType.getSelectedItem()));

                if (timetableDao.updateSlot(selectedSlot)) {
                    refreshTimetable();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update slot.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }

    private void deleteSelectedSlot() {
        if (selectedSlot == null) {
            JOptionPane.showMessageDialog(this, "Please select a slot to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete Lecture Slot: " + selectedSlot.getSubjectName() + " (" + selectedSlot.getDayOfWeek() + " " + selectedSlot.getTimeSlot() + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (timetableDao.deleteSlot(selectedSlot.getTimetableId())) {
                refreshTimetable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete slot.");
            }
        }
    }

    private String detectLocalIp() {
        try {
            java.util.Enumeration<java.net.NetworkInterface> interfaces = java.net.NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                java.net.NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp() || iface.isVirtual()) continue;
                java.util.Enumeration<java.net.InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    java.net.InetAddress addr = addresses.nextElement();
                    if (addr instanceof java.net.Inet4Address && !addr.isLoopbackAddress()) {
                        String ip = addr.getHostAddress();
                        if (!ip.startsWith("169.254.")) return ip;
                    }
                }
            }
        } catch (Exception ignored) {}
        return "127.0.0.1";
    }

    /**
     * Custom cell renderer to color status column: Present = Green, Absent = Red, Late = Orange
     */
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == 3 && value != null) {
                String status = value.toString();
                if ("Present".equalsIgnoreCase(status)) {
                    c.setForeground(new Color(15, 120, 45));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if ("Absent".equalsIgnoreCase(status)) {
                    c.setForeground(new Color(185, 30, 30));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else if ("Late".equalsIgnoreCase(status)) {
                    c.setForeground(new Color(190, 100, 10));
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    c.setForeground(Color.BLACK);
                }
            } else {
                c.setForeground(Color.BLACK);
            }
            return c;
        }
    }
}
