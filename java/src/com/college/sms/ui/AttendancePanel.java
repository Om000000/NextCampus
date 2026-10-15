package com.college.sms.ui;

import com.college.sms.dao.AttendanceDAO;
import com.college.sms.model.Attendance;
import com.college.sms.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;

public class AttendancePanel extends JPanel {

    private final AttendanceDAO dao = new AttendanceDAO();
    private final DefaultTableModel model;

    private final JTextField txtStudentId = new JTextField(6);
    private final JTextField txtDate = new JTextField(10);
    private final JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Present", "Absent", "Late"});
    private final JTextField txtSubject = new JTextField(12);
    private final JTextField txtAttId = new JTextField(6);

    public AttendancePanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        model = new DefaultTableModel(
                new String[]{"Att ID", "Student ID", "Student", "Date", "Status", "Subject"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        Theme.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);

        txtDate.setText(LocalDate.now().toString());

        JPanel form = new JPanel(new GridLayout(3, 4, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Mark / Delete Attendance"));
        form.add(new JLabel("Student ID"));
        form.add(txtStudentId);
        form.add(new JLabel("Date (YYYY-MM-DD)"));
        form.add(txtDate);
        form.add(new JLabel("Status"));
        form.add(cmbStatus);
        form.add(new JLabel("Subject"));
        form.add(txtSubject);
        form.add(new JLabel("Attendance ID (delete)"));
        form.add(txtAttId);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Mark Attendance");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");
        buttons.add(btnAdd);
        buttons.add(btnDelete);
        buttons.add(btnRefresh);

        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addAttendance());
        btnDelete.addActionListener(e -> deleteAttendance());
        btnRefresh.addActionListener(e -> loadAll());

        loadAll();
    }

    private void loadAll() {
        model.setRowCount(0);
        for (Attendance a : dao.getAll()) {
            model.addRow(new Object[]{
                    a.getAttendanceId(), a.getStudentId(), a.getStudentName(),
                    a.getAttendDate(), a.getStatus(), a.getSubjectName()
            });
        }
    }

    private void addAttendance() {
        try {
            Attendance a = new Attendance(
                    Integer.parseInt(txtStudentId.getText().trim()),
                    LocalDate.parse(txtDate.getText().trim()),
                    String.valueOf(cmbStatus.getSelectedItem()),
                    txtSubject.getText().trim()
            );
            if (dao.add(a)) {
                JOptionPane.showMessageDialog(this, "Attendance saved.");
                loadAll();
            } else {
                JOptionPane.showMessageDialog(this, "Failed. Check Student ID exists.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteAttendance() {
        try {
            int id = Integer.parseInt(txtAttId.getText().trim());
            if (dao.delete(id)) {
                JOptionPane.showMessageDialog(this, "Deleted.");
                loadAll();
            } else {
                JOptionPane.showMessageDialog(this, "Delete failed.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
