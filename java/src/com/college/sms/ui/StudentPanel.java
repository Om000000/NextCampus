package com.college.sms.ui;

import com.college.sms.dao.StudentDAO;
import com.college.sms.model.Student;
import com.college.sms.util.Theme;

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
import java.util.List;

public class StudentPanel extends JPanel {

    private final StudentDAO dao = new StudentDAO();
    private final DefaultTableModel model;
    private final JTable table;

    private final JTextField txtId = new JTextField(5);
    private final JTextField txtRoll = new JTextField(10);
    private final JTextField txtName = new JTextField(15);
    private final JTextField txtCourse = new JTextField(10);
    private final JTextField txtSem = new JTextField(5);
    private final JTextField txtEmail = new JTextField(15);
    private final JTextField txtPhone = new JTextField(10);
    private final JTextField txtSearch = new JTextField(12);

    public StudentPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        model = new DefaultTableModel(
                new String[]{"ID", "Roll No", "Name", "Course", "Sem", "Email", "Phone"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        Theme.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(4, 4, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Student Form"));
        form.add(new JLabel("ID (for update/delete)"));
        form.add(txtId);
        form.add(new JLabel("Roll No"));
        form.add(txtRoll);
        form.add(new JLabel("Full Name"));
        form.add(txtName);
        form.add(new JLabel("Course"));
        form.add(txtCourse);
        form.add(new JLabel("Semester"));
        form.add(txtSem);
        form.add(new JLabel("Email"));
        form.add(txtEmail);
        form.add(new JLabel("Phone"));
        form.add(txtPhone);
        form.add(new JLabel("Search name"));
        form.add(txtSearch);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnSearch = new JButton("Search");
        JButton btnClear = new JButton("Clear");

        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnRefresh);
        buttons.add(btnSearch);
        buttons.add(btnClear);

        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnRefresh.addActionListener(e -> loadAll());
        btnSearch.addActionListener(e -> search());
        btnClear.addActionListener(e -> clearForm());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                fillFromSelectedRow();
            }
        });

        loadAll();
    }

    private void loadAll() {
        fillTable(dao.getAll());
    }

    private void search() {
        fillTable(dao.findByName(txtSearch.getText().trim()));
    }

    private void fillTable(List<Student> list) {
        model.setRowCount(0);
        for (Student s : list) {
            model.addRow(new Object[]{
                    s.getStudentId(), s.getRollNo(), s.getFullName(),
                    s.getCourse(), s.getSemester(), s.getEmail(), s.getPhone()
            });
        }
    }

    private void fillFromSelectedRow() {
        int r = table.getSelectedRow();
        txtId.setText(String.valueOf(model.getValueAt(r, 0)));
        txtRoll.setText(String.valueOf(model.getValueAt(r, 1)));
        txtName.setText(String.valueOf(model.getValueAt(r, 2)));
        txtCourse.setText(String.valueOf(model.getValueAt(r, 3)));
        txtSem.setText(String.valueOf(model.getValueAt(r, 4)));
        Object email = model.getValueAt(r, 5);
        Object phone = model.getValueAt(r, 6);
        txtEmail.setText(email == null ? "" : String.valueOf(email));
        txtPhone.setText(phone == null ? "" : String.valueOf(phone));
    }

    private void addStudent() {
        try {
            Student s = readForm(false);
            if (dao.add(s)) {
                JOptionPane.showMessageDialog(this, "Student added.");
                clearForm();
                loadAll();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add. Check roll no / DB.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void updateStudent() {
        try {
            Student s = readForm(true);
            if (dao.update(s)) {
                JOptionPane.showMessageDialog(this, "Student updated.");
                loadAll();
            } else {
                JOptionPane.showMessageDialog(this, "Update failed.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void deleteStudent() {
        try {
            int id = Integer.parseInt(txtId.getText().trim());
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete student ID " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION && dao.delete(id)) {
                JOptionPane.showMessageDialog(this, "Deleted.");
                clearForm();
                loadAll();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private Student readForm(boolean needId) {
        Student s = new Student();
        if (needId) {
            s.setStudentId(Integer.parseInt(txtId.getText().trim()));
        }
        s.setRollNo(txtRoll.getText().trim());
        s.setFullName(txtName.getText().trim());
        s.setCourse(txtCourse.getText().trim());
        s.setSemester(Integer.parseInt(txtSem.getText().trim()));
        s.setEmail(txtEmail.getText().trim());
        s.setPhone(txtPhone.getText().trim());
        return s;
    }

    private void clearForm() {
        txtId.setText("");
        txtRoll.setText("");
        txtName.setText("");
        txtCourse.setText("BBA CA");
        txtSem.setText("5");
        txtEmail.setText("");
        txtPhone.setText("");
        txtSearch.setText("");
    }
}
