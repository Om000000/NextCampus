package com.college.sms.util;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utility helper to load and apply NextCampus project icon across Swing windows.
 */
public class AppIcon {

    private static Image cachedIcon = null;

    public static synchronized Image getAppIcon() {
        if (cachedIcon != null) {
            return cachedIcon;
        }

        // 1. Try loading external icon file
        String[] paths = {
                "nextcampus_icon.png",
                "java/resources/icon.png",
                "resources/icon.png",
                "python/static/icon.png",
                "../nextcampus_icon.png"
        };
        for (String p : paths) {
            try {
                File f = new File(p);
                if (f.exists()) {
                    cachedIcon = ImageIO.read(f);
                    if (cachedIcon != null) {
                        return cachedIcon;
                    }
                }
            } catch (Exception ignored) {}
        }

        // 2. High-res vector fallback icon (NextCampus Logo)
        cachedIcon = createFallbackIcon();
        return cachedIcon;
    }

    public static void applyTo(Window window) {
        try {
            window.setIconImage(getAppIcon());
        } catch (Exception ignored) {}
    }

    private static Image createFallbackIcon() {
        int size = 128;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Rounded green gradient background
        GradientPaint gp = new GradientPaint(0, 0, new Color(13, 75, 45), size, size, new Color(20, 140, 75));
        g.setPaint(gp);
        g.fill(new RoundRectangle2D.Float(4, 4, size - 8, size - 8, 32, 32));

        // Gold border
        g.setColor(new Color(230, 190, 80));
        g.draw(new RoundRectangle2D.Float(4, 4, size - 8, size - 8, 32, 32));

        // White 'N' + Graduation motif
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 54));
        g.drawString("N", 24, 82);

        // Cap symbol
        g.setColor(new Color(245, 215, 100));
        int[] xPoints = {64, 98, 64, 30};
        int[] yPoints = {32, 44, 56, 44};
        g.fillPolygon(xPoints, yPoints, 4);

        g.dispose();
        return img;
    }
}
