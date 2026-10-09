package com.college.sms.ui;

import com.college.sms.dao.MarksDAO;
import com.college.sms.model.Marks;

import javax.swing.BorderFactory;
import javax.swing.JButton;
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

public class MarksPanel extends JPanel {

    private final MarksDAO dao = new MarksDAO();
    private final DefaultTableModel model;

    private final JTextField txtStudentId = new JTextField(6);
    private final JTextField txtSubject = new JTextField(12);
    private final JTextField txtObtained = new JTextField(6);
    private final JTextField txtMax = new JTextField(6);
    private final JTextField txtExam = new JTextField(8);
    private final JTextField txtMarkId = new JTextField(6);

    public MarksPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        model = new DefaultTableModel(
                new String[]{"Mark ID", "Student ID", "Student", "Subject", "Obtained", "Max", "Exam", "%"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);

        txtMax.setText("100");
        txtExam.setText("Internal");

        JPanel form = new JPanel(new GridLayout(3, 4, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Add / Delete Marks"));
        form.add(new JLabel("Student ID"));
        form.add(txtStudentId);
        form.add(new JLabel("Subject"));
        form.add(txtSubject);
        form.add(new JLabel("Marks Obtained"));
        form.add(txtObtained);
        form.add(new JLabel("Max Marks"));
        form.add(txtMax);
        form.add(new JLabel("Exam Type"));
        form.add(txtExam);
        form.add(new JLabel("Mark ID (delete)"));
        form.add(txtMarkId);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Add Marks");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");
        buttons.add(btnAdd);
        buttons.add(btnDelete);
        buttons.add(btnRefresh);

        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addMarks());
        btnDelete.addActionListener(e -> deleteMarks());
        btnRefresh.addActionListener(e -> loadAll());

        loadAll();
    }

    private void loadAll() {
        model.setRowCount(0);
        for (Marks m : dao.getAll()) {
            model.addRow(new Object[]{
                    m.getMarkId(), m.getStudentId(), m.getStudentName(),
                    m.getSubjectName(), m.getMarksObtained(), m.getMaxMarks(),
                    m.getExamType(), String.format("%.1f", m.getPercentage())
            });
        }
    }

    private void addMarks() {
        try {
            Marks m = new Marks(
                    Integer.parseInt(txtStudentId.getText().trim()),
                    txtSubject.getText().trim(),
                    Double.parseDouble(txtObtained.getText().trim()),
                    Double.parseDouble(txtMax.getText().trim()),
                    txtExam.getText().trim()
            );
            if (dao.add(m)) {
                JOptionPane.showMessageDialog(this, "Marks added.");
                loadAll();
            } else {
                JOptionPane.showMessageDialog(this, "Failed. Check Student ID exists.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteMarks() {
        try {
            int id = Integer.parseInt(txtMarkId.getText().trim());
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
