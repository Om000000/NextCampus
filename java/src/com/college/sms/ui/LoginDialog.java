package com.college.sms.ui;

import com.college.sms.dao.UserDAO;
import com.college.sms.model.User;
import com.college.sms.util.AppIcon;
import com.college.sms.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;

/**
 * Clean, modern role-based login dialog with quick-fill demo buttons and NextCampus Dark Theme.
 */
public class LoginDialog extends JDialog {

    private final UserDAO userDAO = new UserDAO();
    private final JTextField txtUsername = new JTextField(15);
    private final JPasswordField txtPassword = new JPasswordField(15);
    private final JLabel lblMsg = new JLabel(" ", SwingConstants.CENTER);
    private User authenticatedUser = null;

    public LoginDialog(JFrame parent) {
        super(parent, "NextCampus – Role-Based Login", true);
        setSize(500, 470);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(Theme.BG);

        // Apply NextCampus Icon to Window
        AppIcon.applyTo(this);

        // 1. HEADER (NextCampus Logo with Transparent Icon)
        JPanel pnlHeader = new JPanel();
        pnlHeader.setBackground(Theme.CARD_BG);
        pnlHeader.setLayout(new BorderLayout(12, 0));
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.CARD_BORDER),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        // Logo Image
        try {
            Image iconImg = AppIcon.getAppIcon().getScaledInstance(44, 44, Image.SCALE_SMOOTH);
            pnlHeader.add(new JLabel(new ImageIcon(iconImg)), BorderLayout.WEST);
        } catch (Exception ignored) {}

        JPanel pnlHeaderTexts = new JPanel();
        pnlHeaderTexts.setLayout(new BoxLayout(pnlHeaderTexts, BoxLayout.Y_AXIS));
        pnlHeaderTexts.setOpaque(false);

        JLabel lblTitle = new JLabel("NextCampus");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Theme.isDarkMode() ? Theme.ACCENT_EMERALD : Theme.ACCENT_EMERALD_DARK);

        JLabel lblSub = new JLabel("College Student Management System – Role-Based Access");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(Theme.TEXT_MUTED);

        pnlHeaderTexts.add(lblTitle);
        pnlHeaderTexts.add(Box.createVerticalStrut(2));
        pnlHeaderTexts.add(lblSub);
        pnlHeader.add(pnlHeaderTexts, BorderLayout.CENTER);

        add(pnlHeader, BorderLayout.NORTH);

        // 2. CENTER PANEL (Form)
        JPanel pnlCenter = new JPanel();
        pnlCenter.setBackground(Theme.BG);
        pnlCenter.setLayout(new BoxLayout(pnlCenter, BoxLayout.Y_AXIS));
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(16, 24, 12, 24));

        // Quick Role Selection Banner
        JLabel lblQuick = new JLabel("Quick Viva Demo Logins (Click to Auto-Fill):");
        lblQuick.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblQuick.setForeground(Theme.isDarkMode() ? Theme.ACCENT_CYAN : Theme.ACCENT_BLUE);
        lblQuick.setAlignmentX(CENTER_ALIGNMENT);
        pnlCenter.add(lblQuick);
        pnlCenter.add(Box.createVerticalStrut(8));

        JPanel pnlQuick = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        pnlQuick.setOpaque(false);

        JButton btnHod = createRoleButton("👑 HOD (Admin)", new Color(30, 58, 82));
        JButton btnTeacher = createRoleButton("👨‍🏫 Teacher", new Color(20, 70, 45));
        JButton btnLibrarian = createRoleButton("📚 Librarian", new Color(85, 60, 20));
        JButton btnStudent = createRoleButton("🎓 Student", new Color(60, 35, 85));

        btnHod.addActionListener(e -> setCredentials("admin", "admin123"));
        btnTeacher.addActionListener(e -> setCredentials("teacher", "teacher123"));
        btnLibrarian.addActionListener(e -> setCredentials("librarian", "library123"));
        btnStudent.addActionListener(e -> setCredentials("BBA001", "bba001"));

        pnlQuick.add(btnHod);
        pnlQuick.add(btnTeacher);
        pnlQuick.add(btnLibrarian);
        pnlQuick.add(btnStudent);
        pnlCenter.add(pnlQuick);
        pnlCenter.add(Box.createVerticalStrut(16));

        // Form fields card
        JPanel pnlFormCard = new JPanel(new GridBagLayout());
        pnlFormCard.setBackground(Theme.CARD_BG);
        pnlFormCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblU = new JLabel("Username / Roll No:");
        lblU.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblU.setForeground(Theme.TEXT_MAIN);
        gbc.gridx = 0; gbc.gridy = 0;
        pnlFormCard.add(lblU, gbc);

        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setBackground(Theme.INPUT_BG);
        txtUsername.setForeground(Theme.TEXT_MAIN);
        txtUsername.setCaretColor(Theme.TEXT_MAIN);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        gbc.gridx = 1; gbc.gridy = 0;
        pnlFormCard.add(txtUsername, gbc);

        JLabel lblP = new JLabel("Password:");
        lblP.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblP.setForeground(Theme.TEXT_MAIN);
        gbc.gridx = 0; gbc.gridy = 1;
        pnlFormCard.add(lblP, gbc);

        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setBackground(Theme.INPUT_BG);
        txtPassword.setForeground(Theme.TEXT_MAIN);
        txtPassword.setCaretColor(Theme.TEXT_MAIN);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        gbc.gridx = 1; gbc.gridy = 1;
        pnlFormCard.add(txtPassword, gbc);

        pnlCenter.add(pnlFormCard);
        pnlCenter.add(Box.createVerticalStrut(10));

        // Feedback label
        lblMsg.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMsg.setForeground(Theme.ACCENT_RED);
        lblMsg.setAlignmentX(CENTER_ALIGNMENT);
        pnlCenter.add(lblMsg);

        add(pnlCenter, BorderLayout.CENTER);

        // 3. BOTTOM BUTTONS
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 12));
        pnlBottom.setBackground(Theme.CARD_BG);
        pnlBottom.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.CARD_BORDER));

        JButton btnExit = new JButton("Exit");
        btnExit.setPreferredSize(new Dimension(85, 34));
        btnExit.setBackground(Theme.isDarkMode() ? new Color(45, 55, 72) : new Color(226, 232, 240));
        btnExit.setForeground(Theme.TEXT_MAIN);
        btnExit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnExit.setBorder(BorderFactory.createLineBorder(Theme.CARD_BORDER, 1));
        btnExit.setOpaque(true);
        btnExit.addActionListener(e -> {
            authenticatedUser = null;
            dispose();
            System.exit(0);
        });

        JButton btnLogin = new JButton("Sign In ➔");
        btnLogin.setPreferredSize(new Dimension(110, 34));
        btnLogin.setBackground(Theme.ACCENT_EMERALD);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setOpaque(true);
        btnLogin.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(25, 205, 145), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btnLogin.addActionListener(e -> performLogin());

        // Press Enter to login
        getRootPane().setDefaultButton(btnLogin);

        pnlBottom.add(btnExit);
        pnlBottom.add(btnLogin);
        add(pnlBottom, BorderLayout.SOUTH);

        // Default credentials
        setCredentials("admin", "admin123");
    }

    private JButton createRoleButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(bg);
        btn.setForeground(Theme.TEXT_MAIN);
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.brighter(), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return btn;
    }

    private void setCredentials(String username, String password) {
        txtUsername.setText(username);
        txtPassword.setText(password);
        lblMsg.setText(" ");
        txtPassword.requestFocusInWindow();
    }

    private void performLogin() {
        String u = txtUsername.getText().trim();
        String p = new String(txtPassword.getPassword()).trim();

        if (u.isEmpty() || p.isEmpty()) {
            lblMsg.setText("Please enter username and password.");
            return;
        }

        User user = userDAO.authenticate(u, p);
        if (user != null) {
            this.authenticatedUser = user;
            dispose();
        } else {
            lblMsg.setText("Invalid credentials. Try quick viva buttons above.");
        }
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
}
