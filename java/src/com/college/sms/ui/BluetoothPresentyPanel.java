package com.college.sms.ui;

import com.college.sms.dao.AcademicStatsDAO;
import com.college.sms.dao.PresentyDAO;
import com.college.sms.model.AttendanceSession;
import com.college.sms.model.PhonePresenty;
import com.college.sms.util.BluetoothHash;
import com.college.sms.util.Theme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;

/**
 * Teacher screen: start a Bluetooth hash session so students can mark presenty from phones.
 */
public class BluetoothPresentyPanel extends JPanel {

    private final PresentyDAO dao = new PresentyDAO();
    private final JTextField txtSubject = new JTextField("Java Programming", 14);
    private final JTextField txtBeacon = new JTextField("SMS-CLASS", 10);
    private final JSpinner spnMinutes = new JSpinner(new SpinnerNumberModel(15, 1, 180, 1));
    private final JLabel lblSession = new JLabel("No active session", SwingConstants.CENTER);
    private final JLabel lblHash = new JLabel("--------", SwingConstants.CENTER);
    private final JLabel lblPhoneUrl = new JLabel("", SwingConstants.CENTER);
    private final JLabel lblHint = new JLabel("Start a session, then students open the phone URL on the same Wi-Fi.", SwingConstants.CENTER);
    private final DefaultTableModel model;
    private AttendanceSession current;
    private final Timer timer;
    private final String localIp;

    private final AcademicStatsDAO statsDao = new AcademicStatsDAO();
    private final JTextField txtLiveNotice = new JTextField(30);
    private final JButton btnUpdateNotice = new JButton("📢 Update Notice on Phone");

    public BluetoothPresentyPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        localIp = detectLocalIp();

        JPanel top = new JPanel(new GridLayout(5, 1, 4, 4));
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        form.add(new JLabel("Subject:"));
        form.add(txtSubject);
        form.add(new JLabel("Beacon:"));
        form.add(txtBeacon);
        form.add(new JLabel("Duration (mins):"));
        form.add(spnMinutes);

        JButton btnStart = new JButton("▶ Start Session");
        JButton btnStop = new JButton("⏹ Stop Session");
        JButton btnLaunch = new JButton("🚀 Launch Phone Server");
        JButton btnOpen = new JButton("🌐 Open Phone Page");
        JButton btnResetDev = new JButton("📱 Reset Device Lock");

        btnStart.setBackground(new Color(30, 130, 60));
        btnStart.setForeground(Color.WHITE);
        btnStart.setOpaque(true);

        form.add(btnStart);
        form.add(btnStop);
        form.add(btnLaunch);
        form.add(btnOpen);
        form.add(btnResetDev);

        JPanel formNotice = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        formNotice.add(new JLabel("📢 Live Notice for Phone:"));
        txtLiveNotice.setText(statsDao.getActiveNotice());
        formNotice.add(txtLiveNotice);
        formNotice.add(btnUpdateNotice);

        lblHash.setFont(new Font("Monospaced", Font.BOLD, 44));
        lblHash.setForeground(Theme.ACCENT_EMERALD);
        lblSession.setFont(new Font("SansSerif", Font.BOLD, 15));

        lblPhoneUrl.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblPhoneUrl.setForeground(Theme.ACCENT_CYAN);
        lblPhoneUrl.setText("📱 Phone URL: http://" + localIp + ":5000  (Connect phone to same Wi-Fi)");

        top.add(form);
        top.add(formNotice);
        top.add(lblSession);
        top.add(lblHash);
        top.add(lblPhoneUrl);
        add(top, BorderLayout.NORTH);
        add(lblHint, BorderLayout.SOUTH);

        btnUpdateNotice.addActionListener(e -> {
            String notice = txtLiveNotice.getText().trim();
            if (!notice.isEmpty()) {
                if (statsDao.setNotice(notice, "Teacher")) {
                    JOptionPane.showMessageDialog(this, "Notice updated! Students will see this live on their phones.");
                }
            }
        });

        model = new DefaultTableModel(
                new String[]{"Time", "Roll", "Name", "BT device", "Hash", "Verified by"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        Theme.styleTable(table);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnStart.addActionListener(e -> startSession());
        btnStop.addActionListener(e -> stopSession());
        btnLaunch.addActionListener(e -> launchPythonServer());
        btnOpen.addActionListener(e -> openPhonePage());
        btnResetDev.addActionListener(e -> {
            String roll = JOptionPane.showInputDialog(this, "Enter Student Roll No to unlock device (leave empty to unlock all devices):");
            if (roll != null) {
                if (roll.trim().isEmpty()) {
                    int c = JOptionPane.showConfirmDialog(this, "Are you sure you want to clear device locks for ALL students?", "Confirm Unlock All", JOptionPane.YES_NO_OPTION);
                    if (c == JOptionPane.YES_OPTION) {
                        dao.resetAllDeviceBindings();
                        JOptionPane.showMessageDialog(this, "All device locks cleared successfully.");
                    }
                } else {
                    if (dao.resetDeviceBinding(roll.trim())) {
                        JOptionPane.showMessageDialog(this, "Device lock reset for roll number: " + roll.trim());
                    } else {
                        JOptionPane.showMessageDialog(this, "No registered device lock found for: " + roll.trim());
                    }
                }
            }
        });

        timer = new Timer(1000, e -> refreshLive());
        timer.start();
        refreshLive();
    }

    private void startSession() {
        String subject = txtSubject.getText().trim();
        String beacon = txtBeacon.getText().trim();
        if (subject.isEmpty() || beacon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Subject and Bluetooth beacon name are required.");
            return;
        }
        int minutes = (Integer) spnMinutes.getValue();
        current = dao.startSession(subject, beacon, minutes);
        if (current == null) {
            JOptionPane.showMessageDialog(this, "Could not start session. Please try again.");
            return;
        }
        JOptionPane.showMessageDialog(this,
                "Attendance Session Started!\n\n"
                        + "1. URL for phones: http://" + localIp + ":5000\n"
                        + "2. Show the live 8-character hash on this screen to students.\n"
                        + "   (It refreshes automatically every 30 seconds).\n"
                        + "3. Ensure python Phone Presenty server is running (click 'Launch Phone Server').\n"
                        + "4. Students on the same Wi-Fi enter Roll No & live hash to mark present.");
        refreshLive();
    }

    private void stopSession() {
        dao.closeAllActive();
        current = null;
        lblHash.setText("--------");
        lblSession.setText("Session stopped");
        model.setRowCount(0);
    }

    private void refreshLive() {
        current = dao.getActive();
        if (current == null) {
            lblSession.setText("No active Bluetooth session. Click 'Start Session' above.");
            lblHash.setText("--------");
            return;
        }
        long unix = System.currentTimeMillis() / 1000L;
        String hash = BluetoothHash.broadcastHash(current.getSecret(), current.getSessionId(), unix);
        long remain = BluetoothHash.SLOT_SECONDS - (unix % BluetoothHash.SLOT_SECONDS);
        lblHash.setText(hash);
        lblSession.setText("Session: " + current.getSessionId()
                + "  |  Subject: " + current.getSubjectName()
                + "  |  Beacon: " + current.getBeaconName()
                + "  |  Hash updates in: " + remain + "s"
                + "  |  Ends: " + current.getEndsAt());

        model.setRowCount(0);
        for (PhonePresenty p : dao.listForSession(current.getSessionId())) {
            model.addRow(new Object[]{
                    p.getMarkedAt(), p.getRollNo(), p.getStudentName(),
                    p.getBtDeviceName() == null ? "-" : p.getBtDeviceName(),
                    p.getBluetoothHash(),
                    p.getVerifyMethod()
            });
        }
    }

    private void launchPythonServer() {
        try {
            Path[] candidates = {
                    Paths.get("..", "python", "run_presenty.bat"),
                    Paths.get("python", "run_presenty.bat"),
                    Paths.get("run_presenty.bat")
            };
            Path found = null;
            for (Path p : candidates) {
                if (Files.exists(p)) {
                    found = p.toAbsolutePath().normalize();
                    break;
                }
            }
            if (found == null) {
                JOptionPane.showMessageDialog(this, "Could not find python/run_presenty.bat.\nPlease run it from the python/ folder.");
                return;
            }
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "start", found.toString());
            pb.directory(found.getParent().toFile());
            pb.start();
            JOptionPane.showMessageDialog(this, "Phone Presenty server is starting in a new window!\nURL: http://" + localIp + ":5000");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error starting server: " + ex.getMessage()
                    + "\nPlease open a terminal and run python/run_presenty.bat manually.");
        }
    }

    private void openPhonePage() {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI("http://localhost:5000"));
            } else {
                JOptionPane.showMessageDialog(this, "Please open: http://" + localIp + ":5000 in your browser.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please open: http://" + localIp + ":5000 in your browser.");
        }
    }

    private String detectLocalIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp() || iface.isVirtual()) continue;
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress()) {
                        String ip = addr.getHostAddress();
                        if (!ip.startsWith("169.254.")) {
                            return ip;
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return "127.0.0.1";
    }
}
