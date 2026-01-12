import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import javax.swing.Timer;

public class Splash extends JWindow {

    private JProgressBar progressBar;
    private Timer timer;
    private int progressValue = 0;
    private static final int LOADING_TIME_MS = 3000;
    private static final int TIMER_DELAY = 30;

    
    private static final Color GRADIENT_START = new Color(255, 94, 77);
    private static final Color GRADIENT_MID = new Color(233, 72, 13);
    private static final Color GRADIENT_END = new Color(255, 138, 48);
    private static final Color ACCENT_COLOR = new Color(233, 72, 13);

    private float gradientOffset = 0.0f;

    public Splash() {
        setSize(550, 350);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                int w = getWidth();
                int h = getHeight();

                Color color1 = interpolateColors(GRADIENT_START, GRADIENT_MID, gradientOffset);
                Color color2 = interpolateColors(GRADIENT_MID, GRADIENT_END, gradientOffset);

                GradientPaint gradient = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, w, h);
                g2d.dispose();
            }
        };

        mainPanel.setOpaque(true);
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel);

        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 20, 0);

        JLabel logoLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText() != null && !getText().isEmpty()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    
                    g2.setColor(new Color(0, 0, 0, 50));
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(getText())) / 2;
                    int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(getText(), x + 3, y + 3);

                    
                    GradientPaint gradient = new GradientPaint(
                            0, 0, Color.WHITE,
                            getWidth(), 0, new Color(255, 255, 255, 230));
                    g2.setPaint(gradient);
                    g2.drawString(getText(), x, y);

                    g2.dispose();
                }
            }
        };

        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            File imgFile = new File("CMlogo.png");
            if (imgFile.exists()) {
                ImageIcon originalIcon = new ImageIcon("CMlogo.png");
                Image img = originalIcon.getImage();
                Image scaledImg = img.getScaledInstance(-1, 180, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(scaledImg));
            } else {
                logoLabel.setText("Mail.CM");
                logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 72));
                logoLabel.setForeground(Color.WHITE);
            }
        } catch (Exception e) {
            System.err.println("Error loading splash image: " + e.getMessage());
            logoLabel.setText("Mail.CM");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 72));
            logoLabel.setForeground(Color.WHITE);
        }

        centerPanel.add(logoLabel, gbc);

        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 30, 40));

        progressBar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                
                g2.setColor(new Color(255, 255, 255, 100));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                
                int progressWidth = (int) ((getWidth() - 4) * getValue() / 100.0);
                if (progressWidth > 0) {
                    GradientPaint gradient = new GradientPaint(
                            0, 0, Color.WHITE,
                            progressWidth, 0, new Color(255, 255, 255, 200));
                    g2.setPaint(gradient);
                    g2.fillRoundRect(2, 2, progressWidth, getHeight() - 4, 12, 12);
                }

                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                String text = getString();
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

                
                g2.setColor(new Color(0, 0, 0, 80));
                g2.drawString(text, x + 1, y + 1);

                
                g2.setColor(Color.WHITE);
                g2.drawString(text, x, y);

                g2.dispose();
            }
        };

        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setString("Loading...");
        progressBar.setPreferredSize(new Dimension(100, 35));
        progressBar.setBorderPainted(false);
        progressBar.setOpaque(false);

        bottomPanel.add(progressBar, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        
        Timer gradientTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gradientOffset += 0.005f;
                if (gradientOffset > 1.0f)
                    gradientOffset = 0.0f;
                mainPanel.repaint();
            }
        });
        gradientTimer.start();
    }

    private Color interpolateColors(Color c1, Color c2, float fraction) {
        float frac = Math.abs((fraction * 2) % 1.0f);
        int red = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * frac);
        int green = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * frac);
        int blue = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * frac);
        return new Color(red, green, blue);
    }

    public void startLoading(EmailClient mainApp) {
        timer = new Timer(TIMER_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressValue++;
                progressBar.setValue(progressValue);

                if (progressValue >= 100) {
                    timer.stop();
                    setVisible(false);
                    dispose();
                    mainApp.setVisible(true);
                }
            }
        });

        timer.start();
    }
}