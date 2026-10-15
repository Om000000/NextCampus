package com.college.sms.ui;

import com.college.sms.dao.AcademicStatsDAO;
import com.college.sms.model.AcademicStats;
import com.college.sms.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

/**
 * Teacher interface to post classroom notices and manage assignment/practical completion stats.
 */
public class AcademicProgressPanel extends JPanel {

    private final AcademicStatsDAO dao = new AcademicStatsDAO();
    private final DefaultTableModel model;
    private final JTable table;

    // Notice controls
    private final JTextField txtNotice = new JTextField(35);
    private final JButton btnPostNotice = new JButton("📢 Post Notice to Phone Portal");

    // Student stats editor controls
    private final JTextField txtStudentId = new JTextField(5);
    private final JTextField txtStudentName = new JTextField(12);
    private final JSpinner spnAssignDone = new JSpinner(new SpinnerNumberModel(4, 0, 100, 1));
    private final JSpinner spnAssignTotal = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
    private final JSpinner spnPracDone = new JSpinner(new SpinnerNumberModel(8, 0, 100, 1));
    private final JSpinner spnPracTotal = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
    private final JTextField txtRemarks = new JTextField("Journal certified. In progress.", 20);

    public AcademicProgressPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // 1. TOP: Notice / Comment Section
        JPanel pnlNotice = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        pnlNotice.setBorder(BorderFactory.createTitledBorder("📢 Live Classroom Notice / Teacher Comment (Visible on Student Phone)"));
        txtNotice.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txtNotice.setText(dao.getActiveNotice());
        btnPostNotice.setBackground(new Color(20, 100, 160));
        btnPostNotice.setForeground(Color.WHITE);
        btnPostNotice.setOpaque(true);

        pnlNotice.add(new JLabel("Notice/Comment:"));
        pnlNotice.add(txtNotice);
        pnlNotice.add(btnPostNotice);
        add(pnlNotice, BorderLayout.NORTH);

        // 2. CENTER: Student Completion Table
        model = new DefaultTableModel(
                new String[]{"ID", "Roll", "Name", "Assignments", "Assign %", "Practicals", "Prac %", "Teacher Remarks", "Updated"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        Theme.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                loadSelectedRowToEditor();
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);

        // 3. SOUTH: Stats Form Editor
        JPanel pnlSouth = new JPanel(new BorderLayout(4, 4));
        JPanel form = new JPanel(new GridLayout(2, 6, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Update Student Assignment & Practical Completion"));

        txtStudentId.setEditable(false);
        txtStudentName.setEditable(false);

        form.add(new JLabel("Student ID:"));
        form.add(txtStudentId);
        form.add(new JLabel("Student Name:"));
        form.add(txtStudentName);
        form.add(new JLabel("Assignments Done / Total:"));
        JPanel pnlAssign = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        pnlAssign.add(spnAssignDone);
        pnlAssign.add(new JLabel("/"));
        pnlAssign.add(spnAssignTotal);
        form.add(pnlAssign);

        form.add(new JLabel("Practicals Done / Total:"));
        JPanel pnlPrac = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        pnlPrac.add(spnPracDone);
        pnlPrac.add(new JLabel("/"));
        pnlPrac.add(spnPracTotal);
        form.add(pnlPrac);

        form.add(new JLabel("Teacher Remarks:"));
        form.add(txtRemarks);
        form.add(new JLabel(""));
        form.add(new JLabel(""));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        JButton btnSave = new JButton("💾 Save Completion Stats");
        JButton btnRefresh = new JButton("🔄 Refresh Table");
        btnSave.setBackground(new Color(30, 130, 60));
        btnSave.setForeground(Color.WHITE);
        btnSave.setOpaque(true);

        buttons.add(btnSave);
        buttons.add(btnRefresh);

        pnlSouth.add(form, BorderLayout.CENTER);
        pnlSouth.add(buttons, BorderLayout.SOUTH);
        add(pnlSouth, BorderLayout.SOUTH);

        // Action listeners
        btnPostNotice.addActionListener(e -> postNotice());
        btnSave.addActionListener(e -> saveStats());
        btnRefresh.addActionListener(e -> loadAll());

        loadAll();
    }

    private void postNotice() {
        String notice = txtNotice.getText().trim();
        if (notice.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter notice text.");
            return;
        }
        if (dao.setNotice(notice, "Teacher")) {
            JOptionPane.showMessageDialog(this, "Notice published successfully!\nStudents opening the phone attendance page will see this immediately.");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update notice.");
        }
    }

    private void loadAll() {
        model.setRowCount(0);
        List<AcademicStats> list = dao.getAll();
        for (AcademicStats s : list) {
            model.addRow(new Object[]{
                    s.getStudentId(),
                    s.getRollNo(),
                    s.getStudentName(),
                    s.getAssignmentsCompleted() + " / " + s.getTotalAssignments(),
                    s.getAssignmentPercentage() + "%",
                    s.getPracticalsCompleted() + " / " + s.getTotalPracticals(),
                    s.getPracticalPercentage() + "%",
                    s.getRemarks(),
                    s.getUpdatedAt()
            });
        }
        if (list.size() > 0 && table.getSelectedRow() < 0) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    private void loadSelectedRowToEditor() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtStudentId.setText(String.valueOf(model.getValueAt(row, 0)));
        txtStudentName.setText(String.valueOf(model.getValueAt(row, 2)));

        String assignStr = String.valueOf(model.getValueAt(row, 3));
        String[] assignParts = assignStr.split("/");
        if (assignParts.length == 2) {
            try {
                spnAssignDone.setValue(Integer.parseInt(assignParts[0].trim()));
                spnAssignTotal.setValue(Integer.parseInt(assignParts[1].trim()));
            } catch (Exception ignored) {}
        }

        String pracStr = String.valueOf(model.getValueAt(row, 5));
        String[] pracParts = pracStr.split("/");
        if (pracParts.length == 2) {
            try {
                spnPracDone.setValue(Integer.parseInt(pracParts[0].trim()));
                spnPracTotal.setValue(Integer.parseInt(pracParts[1].trim()));
            } catch (Exception ignored) {}
        }

        txtRemarks.setText(String.valueOf(model.getValueAt(row, 7)));
    }

    private void saveStats() {
        if (txtStudentId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table first.");
            return;
        }
        try {
            int sid = Integer.parseInt(txtStudentId.getText().trim());
            int aDone = (Integer) spnAssignDone.getValue();
            int aTotal = (Integer) spnAssignTotal.getValue();
            int pDone = (Integer) spnPracDone.getValue();
            int pTotal = (Integer) spnPracTotal.getValue();
            String remarks = txtRemarks.getText().trim();

            AcademicStats stats = new AcademicStats(sid, aDone, aTotal, pDone, pTotal, remarks);
            if (dao.saveOrUpdate(stats)) {
                JOptionPane.showMessageDialog(this, "Academic completion stats saved successfully for " + txtStudentName.getText() + "!");
                loadAll();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save stats.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving stats: " + ex.getMessage());
        }
    }
}
