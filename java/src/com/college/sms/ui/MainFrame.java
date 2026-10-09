package com.college.sms.ui;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

/**
 * Main Swing window with tabs for Students, Marks, Attendance.
 */
public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("College Student Management System – TY BBA CA Sem 5");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 620);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Students", new StudentPanel());
        tabs.addTab("Marks", new MarksPanel());
        tabs.addTab("Attendance", new AttendancePanel());
        tabs.addTab("Academic Progress", new AcademicProgressPanel());
        tabs.addTab("Phone Presenty", new BluetoothPresentyPanel());
        tabs.addTab("📅 Timetable & Presenty", new TimetablePresentyPanel());

        add(tabs, BorderLayout.CENTER);
    }
}
