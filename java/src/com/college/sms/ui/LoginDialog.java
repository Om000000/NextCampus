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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.io.File;

/**
 * Modern two-pane split login window matching the official report System Output & Validation screenshots.
 * Left Card: College emblem, NextCampus ERP branding, department info, and feature list.
 * Right Card: Role selection tabs, credentials inputs, demo quick sign-in chips, and login button.
 */
public class LoginDialog extends JDialog {

    private final UserDAO userDAO = new UserDAO();
    private final JTextField txtUsername = new JTextField(18);
    private final JPasswordField txtPassword = new JPasswordField(18);
    private final JLabel lblMsg = new JLabel(" ", SwingConstants.LEFT);
    private User authenticatedUser = null;

    // Role Tab buttons
    private final JButton btnTabHod = new JButton("HOD / Admin");
    private final JButton btnTabTeacher = new JButton("Teacher");
    private final JButton btnTabLib = new JButton("Librarian");
    private final JButton btnTabStudent = new JButton("Student");
    private String selectedRole = "HOD";

    public LoginDialog(JFrame parent) {
        super(parent, "NextCampus – Multi-Role Academic Authentication (HRM Pune)", true);
        setSize(920, 530);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(Theme.BG);

        // Apply NextCampus Transparent Icon to Window
        AppIcon.applyTo(this);

        // TOP WINDOW BAR (Title matching report mockup)
        JPanel pnlWindowBar = new JPanel(new BorderLayout(8, 0));
        pnlWindowBar.setBackground(new Color(30, 41, 59));
        pnlWindowBar.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        JLabel lblWinTitle = new JLabel("NextCampus – Multi-Role Academic Authentication (HRM Pune)");
        lblWinTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblWinTitle.setForeground(new Color(241, 245, 249));
        pnlWindowBar.add(lblWinTitle, BorderLayout.WEST);

        // Mini window indicator dots
        JPanel pnlDots = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 4));
        pnlDots.setOpaque(false);
        JLabel dotGreen = createCircleDot(new Color(34, 197, 94));
        JLabel dotYellow = createCircleDot(new Color(234, 179, 8));
        JLabel dotRed = createCircleDot(new Color(239, 68, 68));
        pnlDots.add(dotGreen);
        pnlDots.add(dotYellow);
        pnlDots.add(dotRed);
        pnlWindowBar.add(pnlDots, BorderLayout.EAST);
        add(pnlWindowBar, BorderLayout.NORTH);

        // MAIN CONTENT: 2-PANE SPLIT LAYOUT
        JPanel pnlMain = new JPanel(new GridLayout(1, 2, 16, 0));
        pnlMain.setBackground(Theme.BG);
        pnlMain.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        // =====================================================================
        // LEFT CARD: College Emblem & NextCampus ERP Branding
        // =====================================================================
        JPanel pnlLeftCard = new JPanel();
        pnlLeftCard.setLayout(new BoxLayout(pnlLeftCard, BoxLayout.Y_AXIS));
        pnlLeftCard.setBackground(new Color(30, 41, 59));
        pnlLeftCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 2, true),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)
        ));

        // College Logo Image
        try {
            File fLogo = new File("presentation_assets/college_logo.png");
            if (!fLogo.exists()) {
                fLogo = new File("../presentation_assets/college_logo.png");
            }
            if (fLogo.exists()) {
                ImageIcon orig = new ImageIcon(fLogo.getAbsolutePath());
                Image scaled = orig.getImage().getScaledInstance(220, 110, Image.SCALE_SMOOTH);
                JLabel lblLogo = new JLabel(new ImageIcon(scaled));
                lblLogo.setAlignmentX(LEFT_ALIGNMENT);
                pnlLeftCard.add(lblLogo);
                pnlLeftCard.add(Box.createVerticalStrut(12));
            }
        } catch (Exception ignored) {}

        JLabel lblBrandTitle = new JLabel("NextCampus ERP");
        lblBrandTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBrandTitle.setForeground(new Color(16, 185, 129));
        lblBrandTitle.setAlignmentX(LEFT_ALIGNMENT);
        pnlLeftCard.add(lblBrandTitle);

        pnlLeftCard.add(Box.createVerticalStrut(4));
        JLabel lblCollegeName = new JLabel("Hutatma Rajguru Mahavidyalaya");
        lblCollegeName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCollegeName.setForeground(new Color(248, 250, 252));
        lblCollegeName.setAlignmentX(LEFT_ALIGNMENT);
        pnlLeftCard.add(lblCollegeName);

        JLabel lblDept = new JLabel("Department of BBA(CA)");
        lblDept.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDept.setForeground(new Color(148, 163, 184));
        lblDept.setAlignmentX(LEFT_ALIGNMENT);
        pnlLeftCard.add(lblDept);

        pnlLeftCard.add(Box.createVerticalStrut(14));

        String[] features = {
                "• Role-Based Access Control (RBAC)",
                "• Dynamic Light/Dark Theme Engine",
                "• Anti-Proxy Token Security (30s)",
                "• Automated Exam Seat Allocation",
                "• Embedded SQLite Relational Database"
        };
        for (String feat : features) {
            JLabel lblF = new JLabel(feat);
            lblF.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblF.setForeground(new Color(203, 213, 225));
            lblF.setAlignmentX(LEFT_ALIGNMENT);
            pnlLeftCard.add(lblF);
            pnlLeftCard.add(Box.createVerticalStrut(4));
        }

        pnlLeftCard.add(Box.createVerticalGlue());
        JLabel lblVer = new JLabel("Version: 2.4.0 (TYBBA-CA Semester V)");
        lblVer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblVer.setForeground(new Color(100, 116, 139));
        lblVer.setAlignmentX(LEFT_ALIGNMENT);
        pnlLeftCard.add(lblVer);

        pnlMain.add(pnlLeftCard);

        // =====================================================================
        // RIGHT CARD: Role Tabs & Authentication Form
        // =====================================================================
        JPanel pnlRightCard = new JPanel();
        pnlRightCard.setLayout(new BoxLayout(pnlRightCard, BoxLayout.Y_AXIS));
        pnlRightCard.setBackground(new Color(30, 41, 59));
        pnlRightCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 2, true),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
        ));

        JLabel lblFormTitle = new JLabel("Sign In to Your Academic Portal");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblFormTitle.setForeground(new Color(248, 250, 252));
        lblFormTitle.setAlignmentX(LEFT_ALIGNMENT);
        pnlRightCard.add(lblFormTitle);

        JLabel lblFormSub = new JLabel("Select your role to access relevant department features");
        lblFormSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFormSub.setForeground(new Color(148, 163, 184));
        lblFormSub.setAlignmentX(LEFT_ALIGNMENT);
        pnlRightCard.add(lblFormSub);

        pnlRightCard.add(Box.createVerticalStrut(12));

        // 4 Role Selector Tabs
        JPanel pnlRoleTabs = new JPanel(new GridLayout(1, 4, 6, 0));
        pnlRoleTabs.setOpaque(false);
        pnlRoleTabs.setAlignmentX(LEFT_ALIGNMENT);
        pnlRoleTabs.setMaximumSize(new Dimension(500, 32));

        styleRoleTab(btnTabHod, true);
        styleRoleTab(btnTabTeacher, false);
        styleRoleTab(btnTabLib, false);
        styleRoleTab(btnTabStudent, false);

        btnTabHod.addActionListener(e -> selectRoleTab("HOD"));
        btnTabTeacher.addActionListener(e -> selectRoleTab("TEACHER"));
        btnTabLib.addActionListener(e -> selectRoleTab("LIBRARIAN"));
        btnTabStudent.addActionListener(e -> selectRoleTab("STUDENT"));

        pnlRoleTabs.add(btnTabHod);
        pnlRoleTabs.add(btnTabTeacher);
        pnlRoleTabs.add(btnTabLib);
        pnlRoleTabs.add(btnTabStudent);
        pnlRightCard.add(pnlRoleTabs);

        pnlRightCard.add(Box.createVerticalStrut(14));

        // Username
        JLabel lblU = new JLabel("Username / Registered Mobile:");
        lblU.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblU.setForeground(new Color(203, 213, 225));
        lblU.setAlignmentX(LEFT_ALIGNMENT);
        pnlRightCard.add(lblU);
        pnlRightCard.add(Box.createVerticalStrut(4));

        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtUsername.setBackground(new Color(15, 23, 42));
        txtUsername.setForeground(new Color(248, 250, 252));
        txtUsername.setCaretColor(Color.WHITE);
        txtUsername.setMaximumSize(new Dimension(500, 34));
        txtUsername.setAlignmentX(LEFT_ALIGNMENT);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        pnlRightCard.add(txtUsername);

        pnlRightCard.add(Box.createVerticalStrut(10));

        // Password
        JLabel lblP = new JLabel("Password / Security PIN:");
        lblP.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblP.setForeground(new Color(203, 213, 225));
        lblP.setAlignmentX(LEFT_ALIGNMENT);
        pnlRightCard.add(lblP);
        pnlRightCard.add(Box.createVerticalStrut(4));

        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtPassword.setBackground(new Color(15, 23, 42));
        txtPassword.setForeground(new Color(248, 250, 252));
        txtPassword.setCaretColor(Color.WHITE);
        txtPassword.setMaximumSize(new Dimension(500, 34));
        txtPassword.setAlignmentX(LEFT_ALIGNMENT);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        pnlRightCard.add(txtPassword);

        pnlRightCard.add(Box.createVerticalStrut(10));

        // Demo Quick Sign-In row matching screenshot
        JLabel lblQuick = new JLabel("Demo Quick Sign-In (Click to Auto-Fill):");
        lblQuick.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblQuick.setForeground(new Color(148, 163, 184));
        lblQuick.setAlignmentX(LEFT_ALIGNMENT);
        pnlRightCard.add(lblQuick);
        pnlRightCard.add(Box.createVerticalStrut(4));

        JPanel pnlChips = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlChips.setOpaque(false);
        pnlChips.setAlignmentX(LEFT_ALIGNMENT);
        pnlChips.setMaximumSize(new Dimension(500, 30));

        JButton chipHod = createChipButton("HOD");
        JButton chipTeacher = createChipButton("Teacher");
        JButton chipLib = createChipButton("Librarian");
        JButton chipStudent = createChipButton("Student (Omkar)");

        chipHod.addActionListener(e -> { selectRoleTab("HOD"); setCredentials("admin", "admin123"); });
        chipTeacher.addActionListener(e -> { selectRoleTab("TEACHER"); setCredentials("teacher", "teacher123"); });
        chipLib.addActionListener(e -> { selectRoleTab("LIBRARIAN"); setCredentials("librarian", "library123"); });
        chipStudent.addActionListener(e -> { selectRoleTab("STUDENT"); setCredentials("02", "student123"); });

        pnlChips.add(chipHod);
        pnlChips.add(chipTeacher);
        pnlChips.add(chipLib);
        pnlChips.add(chipStudent);
        pnlRightCard.add(pnlChips);

        pnlRightCard.add(Box.createVerticalStrut(8));

        // Feedback message
        lblMsg.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMsg.setForeground(new Color(239, 68, 68));
        lblMsg.setAlignmentX(LEFT_ALIGNMENT);
        pnlRightCard.add(lblMsg);

        pnlRightCard.add(Box.createVerticalGlue());

        // Buttons row (Big Emerald Login + Exit)
        JPanel pnlActions = new JPanel(new BorderLayout(8, 0));
        pnlActions.setOpaque(false);
        pnlActions.setAlignmentX(LEFT_ALIGNMENT);
        pnlActions.setMaximumSize(new Dimension(500, 38));

        JButton btnLogin = new JButton("SECURE LOGIN →");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setBackground(new Color(16, 185, 129));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setOpaque(true);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnLogin.addActionListener(e -> performLogin());

        JButton btnExit = new JButton("Exit");
        btnExit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnExit.setBackground(new Color(51, 65, 85));
        btnExit.setForeground(new Color(203, 213, 225));
        btnExit.setOpaque(true);
        btnExit.setFocusPainted(false);
        btnExit.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnExit.addActionListener(e -> {
            authenticatedUser = null;
            dispose();
            System.exit(0);
        });

        pnlActions.add(btnExit, BorderLayout.WEST);
        pnlActions.add(btnLogin, BorderLayout.CENTER);
        pnlRightCard.add(pnlActions);

        pnlMain.add(pnlRightCard);
        add(pnlMain, BorderLayout.CENTER);

        // Default login prefilled as HOD
        selectRoleTab("HOD");
        setCredentials("admin", "admin123");
        getRootPane().setDefaultButton(btnLogin);
    }

    private JLabel createCircleDot(Color c) {
        JLabel l = new JLabel("●");
        l.setFont(new Font("SansSerif", Font.BOLD, 14));
        l.setForeground(c);
        return l;
    }

    private void styleRoleTab(JButton btn, boolean active) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (active) {
            btn.setBackground(new Color(16, 185, 129));
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
        } else {
            btn.setBackground(new Color(51, 65, 85));
            btn.setForeground(new Color(203, 213, 225));
            btn.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
        }
    }

    private void selectRoleTab(String role) {
        this.selectedRole = role;
        styleRoleTab(btnTabHod, "HOD".equalsIgnoreCase(role));
        styleRoleTab(btnTabTeacher, "TEACHER".equalsIgnoreCase(role));
        styleRoleTab(btnTabLib, "LIBRARIAN".equalsIgnoreCase(role));
        styleRoleTab(btnTabStudent, "STUDENT".equalsIgnoreCase(role));

        if ("HOD".equalsIgnoreCase(role)) {
            setCredentials("admin", "admin123");
        } else if ("TEACHER".equalsIgnoreCase(role)) {
            setCredentials("teacher", "teacher123");
        } else if ("LIBRARIAN".equalsIgnoreCase(role)) {
            setCredentials("librarian", "library123");
        } else if ("STUDENT".equalsIgnoreCase(role)) {
            setCredentials("02", "student123");
        }
    }

    private JButton createChipButton(String title) {
        JButton btn = new JButton(title);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        btn.setBackground(new Color(51, 65, 85));
        btn.setForeground(new Color(56, 189, 248));
        btn.setOpaque(true);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1, true),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)
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
            lblMsg.setText("Invalid credentials. Try demo quick sign-in buttons.");
        }
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
}
