package com.college.sms.util;

import com.college.sms.db.SchemaInitializer;
import com.college.sms.model.User;
import com.college.sms.ui.LibraryPanel;
import com.college.sms.ui.LoginDialog;
import com.college.sms.ui.StudentExamPanel;
import com.college.sms.ui.TimetablePresentyPanel;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class CaptureRealSwingScreens {

    public static void main(String[] args) {
        // Initialize DB
        try {
            SchemaInitializer.createTablesAndSampleData(com.college.sms.db.DBConnection.getConnection());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Capture LoginDialog
                JFrame dummy = new JFrame();
                LoginDialog login = new LoginDialog(dummy);
                login.pack();
                login.setSize(920, 530);
                saveComponent(login.getContentPane(), "../presentation_assets/screen1_login.png", 920, 530);
                login.dispose();

                // 2. Capture StudentExamPanel
                User studentUser = new User(2, "02", "pass", "STUDENT", "OMKAR SANDIP HADAWALE", "omkar@nextcampus.edu", 2);
                StudentExamPanel examPanel = new StudentExamPanel(studentUser);
                wrapAndSave(examPanel, "../presentation_assets/screen2_student_exam.png", 920, 530);

                // 3. Capture LibraryPanel
                LibraryPanel libPanel = new LibraryPanel(false);
                wrapAndSave(libPanel, "../presentation_assets/screen3_library.png", 920, 530);

                // 4. Capture TimetablePresentyPanel
                TimetablePresentyPanel ttPanel = new TimetablePresentyPanel();
                wrapAndSave(ttPanel, "../presentation_assets/screen4_timetable.png", 920, 530);

                dummy.dispose();
                System.out.println("ALL REAL SWING SCREENS CAPTURED SUCCESSFULLY!");
                System.exit(0);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        });
    }

    private static void wrapAndSave(JPanel panel, String path, int w, int h) {
        JFrame f = new JFrame();
        f.setUndecorated(true);
        f.setSize(w, h);
        f.getContentPane().add(panel);
        f.pack();
        f.setSize(w, h);
        f.validate();
        saveComponent(panel, path, w, h);
        f.dispose();
    }

    private static void saveComponent(Component comp, String path, int w, int h) {
        try {
            comp.setSize(w, h);
            comp.validate();
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = img.createGraphics();
            comp.paint(g2d);
            g2d.dispose();

            File out = new File(path);
            out.getParentFile().mkdirs();
            ImageIO.write(img, "png", out);
            System.out.println("Saved screen: " + out.getAbsolutePath() + " (" + w + "x" + h + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
