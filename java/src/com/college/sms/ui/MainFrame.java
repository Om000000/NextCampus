package com.college.sms.ui;

import com.college.sms.model.User;
import com.college.sms.util.AppIcon;
import com.college.sms.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;

/**
 * NextCampus Main Application Window.
 * Features dark theme, NextCampus app icon, role-based modules, and quick user switching.
 */
public class MainFrame extends JFrame {

    private User currentUser;
    private final JPanel pnlTopBar = new JPanel(new BorderLayout(8, 8));
    private final JLabel lblUserBanner = new JLabel();
    private final JLabel lblRoleBadge = new JLabel();
    private JTabbedPane tabbedPane;

    public MainFrame() {
        this(null);
    }

    public MainFrame(User user) {
        setTitle("NextCampus – College Student Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);

        // Apply NextCampus Icon to Window
        AppIcon.applyTo(this);

        this.currentUser = user;

        // If no user provided, prompt login dialog
        if (this.currentUser == null) {
            LoginDialog login = new LoginDialog(this);
            login.setVisible(true);
            this.currentUser = login.getAuthenticatedUser();
            if (this.currentUser == null) {
                System.exit(0);
            }
        }

        buildUI();
    }

    private void buildUI() {
        getContentPane().removeAll();
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        // 1. TOP HEADER & NAVIGATION BAR (Adapts to Active Theme)
        pnlTopBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.CARD_BORDER),
                BorderFactory.createEmptyBorder(10, 16, 10, 16)
        ));
        pnlTopBar.setBackground(Theme.CARD_BG);

        JPanel pnlLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlLeft.setOpaque(false);

        // NextCampus Logo Icon in Header (Transparent PNG)
        try {
            Image iconImg = AppIcon.getAppIcon().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            pnlLeft.add(new JLabel(new ImageIcon(iconImg)));
        } catch (Exception ignored) {}

        JLabel lblAppTitle = new JLabel("NextCampus");
        lblAppTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblAppTitle.setForeground(Theme.isDarkMode() ? Theme.ACCENT_EMERALD : Theme.ACCENT_EMERALD_DARK);

        lblUserBanner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUserBanner.setForeground(Theme.TEXT_MAIN);
        lblUserBanner.setText("Logged in: " + currentUser.getFullName() + " (" + currentUser.getUsername() + ")");

        lblRoleBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblRoleBadge.setOpaque(true);
        lblRoleBadge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        setRoleBadgeStyle(currentUser.getRole());

        JLabel lblSep = new JLabel("|");
        lblSep.setForeground(Theme.TEXT_MUTED);

        pnlLeft.add(lblAppTitle);
        pnlLeft.add(lblSep);
        pnlLeft.add(lblUserBanner);
        pnlLeft.add(lblRoleBadge);

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlRight.setOpaque(false);

        // One-Click Professional Theme Switcher
        JButton btnTheme = new JButton(Theme.isDarkMode() ? "☀️ Light Mode" : "🌙 Dark Mode");
        btnTheme.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTheme.setBackground(Theme.isDarkMode() ? new Color(45, 55, 72) : new Color(226, 232, 240));
        btnTheme.setForeground(Theme.TEXT_MAIN);
        btnTheme.setOpaque(true);
        btnTheme.setFocusPainted(false);
        btnTheme.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.CARD_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btnTheme.addActionListener(e -> {
            Theme.setDarkMode(!Theme.isDarkMode());
            buildUI();
        });

        JButton btnSwitchUser = new JButton("🔄 Switch Role / Logout");
        btnSwitchUser.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSwitchUser.setBackground(Theme.isDarkMode() ? new Color(30, 58, 82) : new Color(241, 245, 249));
        btnSwitchUser.setForeground(Theme.TEXT_MAIN);
        btnSwitchUser.setOpaque(true);
        btnSwitchUser.setFocusPainted(false);
        btnSwitchUser.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.isDarkMode() ? Theme.ACCENT_CYAN : new Color(148, 163, 184), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btnSwitchUser.addActionListener(e -> onSwitchUser());

        pnlRight.add(btnTheme);
        pnlRight.add(btnSwitchUser);

        pnlTopBar.add(pnlLeft, BorderLayout.WEST);
        pnlTopBar.add(pnlRight, BorderLayout.EAST);
        add(pnlTopBar, BorderLayout.NORTH);

        // 2. DYNAMIC ROLE-BASED TABS
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(Theme.BG);
        tabbedPane.setForeground(Theme.TEXT_MAIN);

        setupTabsForRole(currentUser.getRole());

        add(tabbedPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void setRoleBadgeStyle(String role) {
        lblRoleBadge.setText(role);
        if ("HOD".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
            lblRoleBadge.setBackground(new Color(230, 240, 255));
            lblRoleBadge.setForeground(new Color(20, 70, 160));
        } else if ("TEACHER".equalsIgnoreCase(role)) {
            lblRoleBadge.setBackground(new Color(240, 255, 240));
            lblRoleBadge.setForeground(new Color(20, 120, 50));
        } else if ("LIBRARIAN".equalsIgnoreCase(role)) {
            lblRoleBadge.setBackground(new Color(255, 248, 230));
            lblRoleBadge.setForeground(new Color(150, 90, 0));
        } else {
            lblRoleBadge.setBackground(new Color(250, 235, 255));
            lblRoleBadge.setForeground(new Color(120, 20, 140));
        }
    }

    private void setupTabsForRole(String role) {
        tabbedPane.removeAll();

        if ("HOD".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
            // HOD / Admin: Full Power
            tabbedPane.addTab("👥 Students", new StudentPanel());
            tabbedPane.addTab("📝 Marks", new MarksPanel());
            tabbedPane.addTab("📅 Attendance", new AttendancePanel());
            tabbedPane.addTab("📈 Academic Progress", new AcademicProgressPanel());
            tabbedPane.addTab("📱 Phone Presenty", new BluetoothPresentyPanel());
            tabbedPane.addTab("🕒 Timetable & Presenty", new TimetablePresentyPanel());
            tabbedPane.addTab("📚 Library Management", new LibraryPanel(false));
            tabbedPane.addTab("🎓 Exam & Seat Allocation", new ExamManagementPanel(true));
            tabbedPane.addTab("👤 User Accounts", new UserManagementPanel());

        } else if ("TEACHER".equalsIgnoreCase(role)) {
            // Teacher: Attendance, Marks, Academic Progress, Timetable, Exams & Library Catalog
            tabbedPane.addTab("👥 Students", new StudentPanel());
            tabbedPane.addTab("📝 Marks", new MarksPanel());
            tabbedPane.addTab("📅 Attendance", new AttendancePanel());
            tabbedPane.addTab("📈 Academic Progress", new AcademicProgressPanel());
            tabbedPane.addTab("📱 Phone Presenty", new BluetoothPresentyPanel());
            tabbedPane.addTab("🕒 Timetable & Presenty", new TimetablePresentyPanel());
            tabbedPane.addTab("🎓 Internal Exams", new ExamManagementPanel(false));
            tabbedPane.addTab("📚 Library Catalog", new LibraryPanel(true));

        } else if ("LIBRARIAN".equalsIgnoreCase(role)) {
            // Librarian: Full Library Management + Student Reference Directory
            tabbedPane.addTab("📚 Library Management", new LibraryPanel(false));
            tabbedPane.addTab("👥 Student Directory", new StudentPanel());

        } else {
            // Student: Student Profile, Exam Form & Seat Number, Library, Marks, Attendance
            tabbedPane.addTab("👤 My Profile & Dashboard", new StudentDashboardPanel(currentUser));
            tabbedPane.addTab("🎓 Exam Form & Seat Number", new StudentExamPanel(currentUser));
            tabbedPane.addTab("📚 My Library & Catalog", new StudentLibraryPanel(currentUser));
            tabbedPane.addTab("📅 My Attendance", new AttendancePanel());
            tabbedPane.addTab("📊 My Marks", new MarksPanel());
        }
    }

    private void onSwitchUser() {
        LoginDialog login = new LoginDialog(this);
        login.setVisible(true);
        User newUser = login.getAuthenticatedUser();
        if (newUser != null) {
            this.currentUser = newUser;
            buildUI();
        }
    }
}
