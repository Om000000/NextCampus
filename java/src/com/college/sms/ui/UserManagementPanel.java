package com.college.sms.ui;

import com.college.sms.dao.UserDAO;
import com.college.sms.model.User;
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
import java.util.List;

/**
 * User Accounts & Roles Management Panel for HOD (Admin).
 */
public class UserManagementPanel extends JPanel {

    private final UserDAO userDAO = new UserDAO();
    private final DefaultTableModel model;
    private final JTable table;

    private final JTextField txtUserId = new JTextField(5);
    private final JTextField txtUsername = new JTextField(12);
    private final JTextField txtPassword = new JTextField(12);
    private final JComboBox<String> cmbRole = new JComboBox<>(new String[]{"HOD", "TEACHER", "LIBRARIAN", "STUDENT"});
    private final JTextField txtFullName = new JTextField(16);
    private final JTextField txtEmail = new JTextField(16);
    private final JTextField txtStudentId = new JTextField(5);

    public UserManagementPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        model = new DefaultTableModel(
                new String[]{"User ID", "Username", "Role", "Full Name", "Email", "Linked Student ID"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        Theme.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                populateForm(table.getSelectedRow());
            }
        });

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form for Add/Edit
        JPanel form = new JPanel(new GridLayout(4, 4, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("User Account & Role Details"));

        txtUserId.setEditable(false);
        form.add(new JLabel("User ID:")); form.add(txtUserId);
        form.add(new JLabel("Username:")); form.add(txtUsername);
        form.add(new JLabel("Password:")); form.add(txtPassword);
        form.add(new JLabel("System Role:")); form.add(cmbRole);
        form.add(new JLabel("Full Name:")); form.add(txtFullName);
        form.add(new JLabel("Email Address:")); form.add(txtEmail);
        form.add(new JLabel("Student ID (If Student):")); form.add(txtStudentId);
        form.add(new JLabel("")); form.add(new JLabel(""));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        JButton btnAdd = new JButton("➕ Create User");
        JButton btnUpdate = new JButton("✏ Update User");
        JButton btnDelete = new JButton("🗑 Delete User");
        JButton btnClear = new JButton("🧹 Clear");
        JButton btnRefresh = new JButton("🔄 Refresh");

        buttons.add(btnAdd);
        buttons.add(btnUpdate);
        buttons.add(btnDelete);
        buttons.add(btnClear);
        buttons.add(btnRefresh);

        JPanel pnlSouth = new JPanel(new BorderLayout());
        pnlSouth.add(form, BorderLayout.CENTER);
        pnlSouth.add(buttons, BorderLayout.SOUTH);
        add(pnlSouth, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> onAddUser());
        btnUpdate.addActionListener(e -> onUpdateUser());
        btnDelete.addActionListener(e -> onDeleteUser());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> refreshTable());

        refreshTable();
    }

    private void refreshTable() {
        model.setRowCount(0);
        List<User> list = userDAO.getAllUsers();
        for (User u : list) {
            model.addRow(new Object[]{
                    u.getUserId(),
                    u.getUsername(),
                    u.getRole(),
                    u.getFullName(),
                    u.getEmail() != null ? u.getEmail() : "-",
                    u.getStudentId() != null ? u.getStudentId() : "-"
            });
        }
    }

    private void populateForm(int row) {
        txtUserId.setText(String.valueOf(model.getValueAt(row, 0)));
        txtUsername.setText(String.valueOf(model.getValueAt(row, 1)));
        cmbRole.setSelectedItem(String.valueOf(model.getValueAt(row, 2)));
        txtFullName.setText(String.valueOf(model.getValueAt(row, 3)));
        Object email = model.getValueAt(row, 4);
        txtEmail.setText("-".equals(email) ? "" : String.valueOf(email));
        Object sid = model.getValueAt(row, 5);
        txtStudentId.setText("-".equals(sid) ? "" : String.valueOf(sid));
    }

    private void clearForm() {
        txtUserId.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        cmbRole.setSelectedIndex(0);
        txtFullName.setText("");
        txtEmail.setText("");
        txtStudentId.setText("");
        table.clearSelection();
    }

    private void onAddUser() {
        String u = txtUsername.getText().trim();
        String p = txtPassword.getText().trim();
        String r = (String) cmbRole.getSelectedItem();
        String name = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String sidStr = txtStudentId.getText().trim();

        if (u.isEmpty() || p.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username, Password, and Full Name are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer sid = null;
        if (!sidStr.isEmpty()) {
            try {
                sid = Integer.parseInt(sidStr);
            } catch (Exception ignored) {}
        }

        User user = new User(0, u, p, r, name, email, sid);
        if (userDAO.addUser(user)) {
            JOptionPane.showMessageDialog(this, "User " + u + " (" + r + ") created successfully!");
            clearForm();
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create user. Username may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdateUser() {
        if (txtUserId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a user to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(txtUserId.getText().trim());
        String u = txtUsername.getText().trim();
        String p = txtPassword.getText().trim();
        String r = (String) cmbRole.getSelectedItem();
        String name = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String sidStr = txtStudentId.getText().trim();

        if (p.isEmpty()) {
            User existing = userDAO.getUserByUsername(u);
            if (existing != null) p = existing.getPassword();
        }

        Integer sid = null;
        if (!sidStr.isEmpty()) {
            try {
                sid = Integer.parseInt(sidStr);
            } catch (Exception ignored) {}
        }

        User user = new User(id, u, p, r, name, email, sid);
        if (userDAO.updateUser(user)) {
            JOptionPane.showMessageDialog(this, "User updated successfully!");
            clearForm();
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update user.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDeleteUser() {
        if (txtUserId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a user to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(txtUserId.getText().trim());
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (userDAO.deleteUser(id)) {
                JOptionPane.showMessageDialog(this, "User deleted.");
                clearForm();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete user.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
