package com.college.sms.util;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

/**
 * NextCampus Professional Dual-Theme Engine (Light & Dark Mode).
 * Designed for tolerable contrast, executive polish, and clean readability.
 */
public class Theme {

    private static boolean darkMode = true;

    // Dark Mode Palette (Midnight Slate)
    public static final Color BG_DARK_VAL        = new Color(15, 23, 42);   // #0f172a
    public static final Color CARD_DARK_VAL      = new Color(30, 41, 59);   // #1e293b
    public static final Color BORDER_DARK_VAL    = new Color(51, 65, 85);   // #334155
    public static final Color INPUT_DARK_VAL     = new Color(15, 23, 42);   // #0f172a
    public static final Color INPUT_BORDER_DARK  = new Color(71, 85, 105);  // #475569
    public static final Color TEXT_MAIN_DARK     = new Color(248, 250, 252);// #f8fafc
    public static final Color TEXT_MUTED_DARK    = new Color(148, 163, 184);// #94a3b8
    public static final Color SELECTION_DARK     = new Color(16, 75, 50);

    // Light Mode Palette (Executive Slate & Crisp Clean White)
    public static final Color BG_LIGHT_VAL       = new Color(241, 245, 249);// #f1f5f9 Soft Slate (avoids glare)
    public static final Color CARD_LIGHT_VAL     = new Color(255, 255, 255);// #ffffff Crisp White
    public static final Color BORDER_LIGHT_VAL   = new Color(203, 213, 225);// #cbd5e1 Subtle Slate
    public static final Color INPUT_LIGHT_VAL    = new Color(255, 255, 255);// #ffffff Pure Input
    public static final Color INPUT_BORDER_LIGHT = new Color(148, 163, 184);// #94a3b8
    public static final Color TEXT_MAIN_LIGHT    = new Color(15, 23, 42);   // #0f172a Sharp Slate Text
    public static final Color TEXT_MUTED_LIGHT   = new Color(100, 116, 139);// #64748b Professional Muted
    public static final Color SELECTION_LIGHT    = new Color(209, 250, 229);// #d1fae5 Soft Emerald Highlight

    // Accents
    public static final Color ACCENT_EMERALD     = new Color(16, 185, 129); // NextCampus Green
    public static final Color ACCENT_EMERALD_DARK= new Color(5, 150, 105);  // Deeper Green for Light Mode
    public static final Color ACCENT_CYAN        = new Color(56, 189, 248); // Tech Cyan
    public static final Color ACCENT_BLUE        = new Color(37, 99, 235);  // Professional Royal Blue
    public static final Color ACCENT_GOLD        = new Color(245, 158, 11); // Amber
    public static final Color ACCENT_RED         = new Color(239, 68, 68);  // Rose Red

    // Active Theme Dynamic references (kept backwards-compatible with direct field access)
    public static Color BG          = BG_DARK_VAL;
    public static Color BG_DARK     = BG_DARK_VAL;
    public static Color CARD_BG     = CARD_DARK_VAL;
    public static Color CARD_BORDER = BORDER_DARK_VAL;
    public static Color INPUT_BG    = INPUT_DARK_VAL;
    public static Color INPUT_BORDER= INPUT_BORDER_DARK;
    public static Color TEXT_MAIN   = TEXT_MAIN_DARK;
    public static Color TEXT_MUTED  = TEXT_MUTED_DARK;
    public static Color SELECTION_BG= SELECTION_DARK;

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void setDarkMode(boolean dark) {
        darkMode = dark;
        if (dark) {
            BG = BG_DARK_VAL;
            CARD_BG = CARD_DARK_VAL;
            CARD_BORDER = BORDER_DARK_VAL;
            INPUT_BG = INPUT_DARK_VAL;
            INPUT_BORDER = INPUT_BORDER_DARK;
            TEXT_MAIN = TEXT_MAIN_DARK;
            TEXT_MUTED = TEXT_MUTED_DARK;
            SELECTION_BG = SELECTION_DARK;
        } else {
            BG = BG_LIGHT_VAL;
            CARD_BG = CARD_LIGHT_VAL;
            CARD_BORDER = BORDER_LIGHT_VAL;
            INPUT_BG = INPUT_LIGHT_VAL;
            INPUT_BORDER = INPUT_BORDER_LIGHT;
            TEXT_MAIN = TEXT_MAIN_LIGHT;
            TEXT_MUTED = TEXT_MUTED_LIGHT;
            SELECTION_BG = SELECTION_LIGHT;
        }
        applyTheme(dark);
    }

    public static void applyDarkTheme() {
        setDarkMode(true);
    }

    public static void applyLightTheme() {
        setDarkMode(false);
    }

    public static void applyTheme(boolean dark) {
        try {
            Color bg = dark ? BG_DARK_VAL : BG_LIGHT_VAL;
            Color card = dark ? CARD_DARK_VAL : CARD_LIGHT_VAL;
            Color border = dark ? BORDER_DARK_VAL : BORDER_LIGHT_VAL;
            Color inBg = dark ? INPUT_DARK_VAL : INPUT_LIGHT_VAL;
            Color inBorder = dark ? INPUT_BORDER_DARK : INPUT_BORDER_LIGHT;
            Color text = dark ? TEXT_MAIN_DARK : TEXT_MAIN_LIGHT;
            Color selBg = dark ? SELECTION_DARK : SELECTION_LIGHT;
            Color selFg = dark ? TEXT_MAIN_DARK : new Color(6, 95, 70);

            UIManager.put("Panel.background", bg);
            UIManager.put("Panel.foreground", text);

            UIManager.put("Label.foreground", text);
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 12));

            UIManager.put("TextField.background", inBg);
            UIManager.put("TextField.foreground", text);
            UIManager.put("TextField.caretForeground", text);
            UIManager.put("TextField.border", BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(inBorder, 1),
                    BorderFactory.createEmptyBorder(4, 6, 4, 6)
            ));

            UIManager.put("PasswordField.background", inBg);
            UIManager.put("PasswordField.foreground", text);
            UIManager.put("PasswordField.caretForeground", text);
            UIManager.put("PasswordField.border", BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(inBorder, 1),
                    BorderFactory.createEmptyBorder(4, 6, 4, 6)
            ));

            UIManager.put("TextArea.background", inBg);
            UIManager.put("TextArea.foreground", text);
            UIManager.put("TextArea.caretForeground", text);

            UIManager.put("ComboBox.background", inBg);
            UIManager.put("ComboBox.foreground", text);

            UIManager.put("Table.background", card);
            UIManager.put("Table.foreground", text);
            UIManager.put("Table.gridColor", border);
            UIManager.put("Table.selectionBackground", selBg);
            UIManager.put("Table.selectionForeground", selFg);
            UIManager.put("Table.rowHeight", 28);

            UIManager.put("TableHeader.background", dark ? new Color(20, 29, 43) : new Color(226, 232, 240));
            UIManager.put("TableHeader.foreground", dark ? ACCENT_CYAN : new Color(15, 23, 42));
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 12));

            UIManager.put("TabbedPane.background", bg);
            UIManager.put("TabbedPane.foreground", text);
            UIManager.put("TabbedPane.selected", card);
            UIManager.put("TabbedPane.shadow", border);
            UIManager.put("TabbedPane.darkShadow", bg);

            UIManager.put("ScrollPane.background", bg);
            UIManager.put("ScrollPane.border", BorderFactory.createLineBorder(border, 1));
            UIManager.put("Viewport.background", card);

            UIManager.put("TitledBorder.titleColor", dark ? ACCENT_EMERALD : ACCENT_EMERALD_DARK);
            UIManager.put("TitledBorder.border", BorderFactory.createLineBorder(border, 1));

            UIManager.put("OptionPane.background", card);
            UIManager.put("OptionPane.messageForeground", text);

            UIManager.put("Button.background", dark ? new Color(30, 58, 82) : new Color(241, 245, 249));
            UIManager.put("Button.foreground", text);
            UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 12));
            UIManager.put("Button.border", BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(border, 1),
                    BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        } catch (Exception ignored) {}
    }

    public static void styleTable(JTable table) {
        boolean dark = isDarkMode();
        Color card = dark ? CARD_DARK_VAL : CARD_LIGHT_VAL;
        Color border = dark ? BORDER_DARK_VAL : BORDER_LIGHT_VAL;
        Color text = dark ? TEXT_MAIN_DARK : TEXT_MAIN_LIGHT;
        Color selBg = dark ? SELECTION_DARK : SELECTION_LIGHT;
        Color selFg = dark ? TEXT_MAIN_DARK : new Color(6, 95, 70);

        table.setBackground(card);
        table.setForeground(text);
        table.setGridColor(border);
        table.setSelectionBackground(selBg);
        table.setSelectionForeground(selFg);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setBackground(dark ? new Color(20, 29, 43) : new Color(226, 232, 240));
            header.setForeground(dark ? ACCENT_CYAN : new Color(15, 23, 42));
            header.setFont(new Font("Segoe UI", Font.BOLD, 12));
            header.setPreferredSize(new Dimension(header.getWidth(), 30));
        }
    }

    public static void stylePrimaryButton(JButton btn) {
        btn.setBackground(new Color(16, 124, 65));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(20, 150, 80), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
    }

    public static void styleAccentButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.brighter(), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
    }
}
