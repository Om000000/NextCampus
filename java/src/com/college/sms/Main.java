package com.college.sms;

import com.college.sms.ui.MainFrame;

import javax.swing.SwingUtilities;
import java.util.Scanner;

/**
 * Entry point – choose Swing GUI or Console mode.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  College Student Management System");
        System.out.println("  TY BBA CA – Semester 5");
        System.out.println("========================================");
        System.out.println("1. Swing GUI");
        System.out.println("2. Console Menu");
        System.out.print("Choose mode (1/2): ");

        Scanner sc = new Scanner(System.in);
        String choice = sc.nextLine().trim();

        if ("2".equals(choice)) {
            new ConsoleApp(sc).start();
        } else {
            SwingUtilities.invokeLater(() -> {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
            });
        }
    }
}
