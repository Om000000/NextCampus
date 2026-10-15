package com.college.sms;

import com.college.sms.ui.MainFrame;
import com.college.sms.util.Theme;

import javax.swing.SwingUtilities;
import java.util.Scanner;

/**
 * Entry point for NextCampus – choose Swing GUI or Console mode.
 */
public class Main {

    public static void main(String[] args) {
        String choice = null;
        if (args != null && args.length > 0) {
            String arg = args[0].trim().toLowerCase();
            if (arg.equals("console") || arg.equals("--console") || arg.equals("-c") || arg.equals("2")) {
                choice = "2";
            } else if (arg.equals("gui") || arg.equals("--gui") || arg.equals("-g") || arg.equals("1")) {
                choice = "1";
            }
        }

        Scanner sc = new Scanner(System.in);
        if (choice == null) {
            System.out.println("========================================");
            System.out.println("  NextCampus – College Management System");
            System.out.println("  TY BBA CA – Semester 5");
            System.out.println("========================================");
            System.out.println("1. Swing GUI (NextCampus Desktop)");
            System.out.println("2. Console Menu");
            System.out.print("Choose mode (1/2): ");
            choice = sc.nextLine().trim();
        }

        if ("2".equals(choice)) {
            new ConsoleApp(sc).start();
        } else {
            // Apply NextCampus Dark Theme
            Theme.applyDarkTheme();
            SwingUtilities.invokeLater(() -> {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            });
        }
    }
}
