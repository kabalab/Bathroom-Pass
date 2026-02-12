import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import javax.swing.*;
import javax.swing.Timer;

public class Main {

    // UI components
    private static JLabel titleLabel;
    private static JLabel periodLabel;
    private static JLabel infoLabel;
    private static JLabel nameLabel;
    private static JLabel timerLabel;
    private static JButton actionButton;
    private static JPanel card;
    private static Timer passTimer;

    // Pass state
    private static boolean passActive = false;
    private static long passStartMillis = 0;

    // Colors (same style as original)
    private static final Color APP_BG = new Color(30, 30, 40);
    private static final Color CARD_BG = new Color(45, 45, 60);
    private static final Color PASS_ACTIVE_BG = new Color(35, 120, 70);
    private static final Color PRIMARY_BTN = new Color(72, 99, 255);
    private static final Color STOP_BTN = new Color(160, 60, 60);
    private static final Color TEXT_PRIMARY = Color.WHITE;
    private static final Color TEXT_SECONDARY = new Color(200, 200, 200);
    private static final Color TEXT_MUTED = new Color(170, 170, 170);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowUi);
    }

    private static void createAndShowUi() {
        JFrame frame = new JFrame("Bathroom Pass");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(APP_BG);

        titleLabel = new JLabel("Bathroom Pass", JLabel.CENTER);
        titleLabel.setForeground(TEXT_PRIMARY);

        periodLabel = new JLabel(getPeriodDisplay(), JLabel.CENTER);
        periodLabel.setForeground(TEXT_SECONDARY);

        infoLabel = new JLabel("Ready to use the pass", JLabel.CENTER);
        infoLabel.setForeground(TEXT_MUTED);

        nameLabel = new JLabel("", JLabel.CENTER);
        nameLabel.setForeground(TEXT_PRIMARY);

        timerLabel = new JLabel("00:00", JLabel.CENTER);
        timerLabel.setForeground(TEXT_PRIMARY);

        actionButton = makeTxtButton("Start Pass", PRIMARY_BTN);
        actionButton.addActionListener(e -> handlePassButton());

        card = new JPanel();
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        periodLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        timerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(periodLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(infoLabel);
        card.add(Box.createVerticalStrut(12));
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(timerLabel);
        card.add(Box.createVerticalStrut(20));
        card.add(actionButton);

        frame.add(card);
        frame.setSize(460, 400);
        frame.setLocationRelativeTo(null);

        updateFonts(frame.getWidth());
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateFonts(frame.getWidth());
            }
        });

        startPeriodUpdater();

        frame.setVisible(true);
    }

    private static void handlePassButton() {
        if (!passActive) {
            String name = JOptionPane.showInputDialog(card, "Enter student name:");
            if (name == null) {
                return;
            }
            name = name.trim();
            if (name.isEmpty()) {
                return;
            }
            startPass(name);
        } else {
            endPass();
        }
    }

    private static void startPass(String name) {
        passActive = true;
        passStartMillis = System.currentTimeMillis();

        card.setBackground(PASS_ACTIVE_BG);
        infoLabel.setText("Pass active");
        nameLabel.setText("Out: " + name);
        timerLabel.setText("00:00");

        actionButton.setText("End Pass");
        actionButton.setBackground(STOP_BTN);

        if (passTimer != null) {
            passTimer.stop();
        }

        passTimer = new Timer(1000, e -> updatePassTimer());
        passTimer.setRepeats(true);
        passTimer.start();
    }

    private static void endPass() {
        passActive = false;
        passStartMillis = 0;

        if (passTimer != null) {
            passTimer.stop();
        }

        card.setBackground(CARD_BG);
        infoLabel.setText("Ready to use the pass");
        nameLabel.setText("");
        timerLabel.setText("00:00");

        actionButton.setText("Start Pass");
        actionButton.setBackground(PRIMARY_BTN);
    }

    private static void updatePassTimer() {
        long elapsed = System.currentTimeMillis() - passStartMillis;
        long totalSeconds = Math.max(0, elapsed / 1000);
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;

        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private static void startPeriodUpdater() {
        Timer timer = new Timer(30_000, e -> periodLabel.setText(getPeriodDisplay()));
        timer.setRepeats(true);
        timer.start();
    }

    /* ================= BUTTON CREATION ================= */
    public static JButton makeTxtButton(String txt, Color clr) {
        return makeTxtButton(txt, clr, new Dimension(160, 50), 16);
    }

    public static JButton makeTxtButton(String txt, Color clr, Dimension size, int fontSize) {
        JButton btn = new JButton(txt);
        btn.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        btn.setBackground(clr);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        if (size != null) {
            btn.setPreferredSize(size);
        }
        return btn;
    }

    /* ================= FONT SCALING ================= */
    private static void updateFonts(int width) {
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, Math.max(20, width / 14)));
        periodLabel.setFont(new Font("SansSerif", Font.BOLD, Math.max(16, width / 22)));
        infoLabel.setFont(new Font("SansSerif", Font.PLAIN, Math.max(14, width / 26)));
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, Math.max(16, width / 20)));
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, Math.max(20, width / 16)));
        actionButton.setFont(new Font("SansSerif", Font.BOLD, Math.max(14, width / 22)));
    }

    /* ================= PERIOD LOGIC (PASSING = NEXT PERIOD) ================= */
    private static String getPeriodDisplay() {
        String periodSuffix = getAttendancePeriodSuffix();
        if (periodSuffix.isEmpty()) {
            return "Outside of class periods";
        }
        return "Current Period: P" + periodSuffix;
    }

    private static String getAttendancePeriodSuffix() {
        LocalDate date = LocalDate.now();
        LocalTime now = LocalTime.now();
        DayOfWeek day = date.getDayOfWeek();

        if (day == DayOfWeek.MONDAY) {
            return getMondayPeriodIncludingPassing(now);
        }

        if (day == DayOfWeek.TUESDAY || day == DayOfWeek.WEDNESDAY
                || day == DayOfWeek.THURSDAY || day == DayOfWeek.FRIDAY) {
            return getDailyPeriodIncludingPassing(now);
        }

        return "";
    }

    private static String getMondayPeriodIncludingPassing(LocalTime now) {
        if (isBetween(now, LocalTime.of(9, 20), LocalTime.of(10, 16))) return "1";
        if (isBetween(now, LocalTime.of(10, 16), LocalTime.of(11, 12))) return "2";
        if (isBetween(now, LocalTime.of(11, 12), LocalTime.of(12, 8))) return "3";
        if (isBetween(now, LocalTime.of(12, 8), LocalTime.of(13, 40))) return "4";
        if (isBetween(now, LocalTime.of(13, 40), LocalTime.of(14, 45))) return "5";
        if (isBetween(now, LocalTime.of(14, 45), LocalTime.of(15, 35))) return "6";
        return "";
    }

    private static String getDailyPeriodIncludingPassing(LocalTime now) {
        if (isBetween(now, LocalTime.of(8, 45), LocalTime.of(9, 48))) return "1";
        if (isBetween(now, LocalTime.of(9, 48), LocalTime.of(10, 51))) return "2";
        if (isBetween(now, LocalTime.of(10, 51), LocalTime.of(11, 54))) return "3";
        if (isBetween(now, LocalTime.of(11, 54), LocalTime.of(13, 33))) return "4";
        if (isBetween(now, LocalTime.of(13, 33), LocalTime.of(14, 36))) return "5";
        if (isBetween(now, LocalTime.of(14, 36), LocalTime.of(15, 33))) return "6";
        return "";
    }

    private static boolean isBetween(LocalTime now, LocalTime start, LocalTime nextStart) {
        return !now.isBefore(start) && now.isBefore(nextStart);
    }
}