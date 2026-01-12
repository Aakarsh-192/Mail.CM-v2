import java.awt.*;
import java.awt.event.*;
import java.util.Optional;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.Timer;

public class ForgetPass extends JPanel {
    private final EmailClient client;
    private final IDataManager dataManager;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    private User targetUser;

    private Step1_FindAccount step1;
    private Step2_ResetPassword step2;

    
    private static final Color GRADIENT_START = new Color(255, 94, 77);
    private static final Color GRADIENT_MID = new Color(233, 72, 13);
    private static final Color GRADIENT_END = new Color(255, 138, 48);
    private static final Color CARD_BG = new Color(255, 255, 255, 245);
    private static final Color TEXT_PRIMARY = new Color(30, 30, 30);
    private static final Color TEXT_SECONDARY = new Color(100, 100, 100);
    private static final Color ACCENT_COLOR = new Color(233, 72, 13);
    private static final Color ACCENT_HOVER = new Color(203, 62, 11);
    private static final Color INPUT_BORDER = new Color(220, 220, 220);
    private static final Color INPUT_FOCUS = new Color(233, 72, 13);

    private float gradientOffset = 0.0f;
    private Timer animationTimer;

    public ForgetPass(EmailClient client) {
        this.client = client;
        this.dataManager = client.getDataManager();
        this.cardLayout = new CardLayout();

        setLayout(new BorderLayout());
        setOpaque(false);
        this.cardPanel = new JPanel(cardLayout);
        this.cardPanel.setOpaque(false);

        step1 = new Step1_FindAccount();
        step2 = new Step2_ResetPassword();

        cardPanel.add(step1, "FindAccount");
        cardPanel.add(step2, "ResetPassword");

        add(cardPanel, BorderLayout.CENTER);

        startBackgroundAnimation();
    }

    private void startBackgroundAnimation() {
        animationTimer = new Timer(50, e -> {
            gradientOffset += 0.002f;
            if (gradientOffset > 1.0f)
                gradientOffset = 0.0f;
            repaint();
        });
        animationTimer.start();
    }

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

    private Color interpolateColors(Color c1, Color c2, float fraction) {
        float frac = Math.abs((fraction * 2) % 1.0f);
        int red = (int) (c1.getRed() + (c2.getRed() - c1.getRed()) * frac);
        int green = (int) (c1.getGreen() + (c2.getGreen() - c1.getGreen()) * frac);
        int blue = (int) (c1.getBlue() + (c2.getBlue() - c1.getBlue()) * frac);
        return new Color(red, green, blue);
    }

    public void clearFields() {
        targetUser = null;
        step1.emailField.setText("");
        step2.newPasswordField.setText("");
        step2.confirmPasswordField.setText("");
        showStep("FindAccount");
    }

    public void showStep(String stepName) {
        cardLayout.show(cardPanel, stepName);
    }

    class GlassCardPanel extends JPanel {
        private int cornerRadius = 20;

        public GlassCardPanel() {
            super();
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            
            for (int i = 10; i > 0; i--) {
                float alpha = (10 - i) / 100f;
                g2.setColor(new Color(0, 0, 0, (int) (alpha * 60)));
                g2.fillRoundRect(i, i, getWidth() - i * 2, getHeight() - i * 2, cornerRadius, cornerRadius);
            }

            
            g2.setColor(CARD_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

            
            g2.setColor(new Color(255, 255, 255, 180));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, cornerRadius, cornerRadius);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);

        JLabel logoLabel = new JLabel("Mail.CM") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, ACCENT_COLOR,
                        getWidth(), 0, GRADIENT_END);
                g2.setPaint(gradient);
                g2.setFont(getFont());

                
                g2.setColor(new Color(0, 0, 0, 30));
                g2.drawString(getText(), 3, getHeight() - 8);

                
                g2.setPaint(gradient);
                g2.drawString(getText(), 2, getHeight() - 9);

                g2.dispose();
            }
        };

        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        logoLabel.setForeground(ACCENT_COLOR);
        logoPanel.add(logoLabel);
        return logoPanel;
    }

    private JTextField createModernTextField() {
        JTextField field = new JTextField(25) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                if (hasFocus()) {
                    g2.setColor(INPUT_FOCUS);
                    g2.setStroke(new BasicStroke(2));
                } else {
                    g2.setColor(INPUT_BORDER);
                    g2.setStroke(new BasicStroke(1));
                }
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
            }
        };

        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(new EmptyBorder(12, 16, 12, 16));
        field.setOpaque(false);

        return field;
    }

    private JPasswordField createModernPasswordField() {
        JPasswordField field = new JPasswordField(25) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                if (hasFocus()) {
                    g2.setColor(INPUT_FOCUS);
                    g2.setStroke(new BasicStroke(2));
                } else {
                    g2.setColor(INPUT_BORDER);
                    g2.setStroke(new BasicStroke(1));
                }
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
            }
        };

        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(new EmptyBorder(12, 16, 12, 16));
        field.setOpaque(false);

        return field;
    }

    private JButton createModernButton(String text, boolean primary) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (primary) {
                    GradientPaint gradient = new GradientPaint(
                            0, 0, isHovered ? ACCENT_HOVER : ACCENT_COLOR,
                            0, getHeight(), isHovered ? new Color(176, 54, 9) : new Color(200, 65, 11));
                    g2.setPaint(gradient);
                } else {
                    g2.setColor(isHovered ? new Color(240, 240, 240) : new Color(250, 250, 250));
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(2, 3, getWidth() - 4, getHeight() - 3, 25, 25);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setForeground(primary ? Color.WHITE : TEXT_PRIMARY);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(new EmptyBorder(12, 32, 12, 32));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.putClientProperty("isHovered", true);
                button.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.putClientProperty("isHovered", false);
                button.repaint();
            }
        });

        return button;
    }

    class Step1_FindAccount extends JPanel {
        private final JTextField emailField;

        public Step1_FindAccount() {
            setOpaque(false);
            setLayout(new GridBagLayout());

            GlassCardPanel card = new GlassCardPanel();
            card.setLayout(new GridBagLayout());
            card.setBorder(new EmptyBorder(50, 60, 50, 60));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 0, 10, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;

            gbc.gridy = 0;
            card.add(createLogoPanel(), gbc);

            gbc.gridy = 1;
            gbc.insets = new Insets(20, 0, 5, 0);
            JLabel title = new JLabel("Account Recovery");
            title.setFont(new Font("Segoe UI", Font.BOLD, 28));
            title.setForeground(TEXT_PRIMARY);
            title.setHorizontalAlignment(JLabel.CENTER);
            card.add(title, gbc);

            gbc.gridy = 2;
            gbc.insets = new Insets(0, 0, 25, 0);
            JLabel subtitle = new JLabel("Enter your email to reset password");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            subtitle.setForeground(TEXT_SECONDARY);
            subtitle.setHorizontalAlignment(JLabel.CENTER);
            card.add(subtitle, gbc);

            gbc.gridy = 3;
            gbc.insets = new Insets(15, 0, 10, 0);
            emailField = createModernTextField();
            card.add(emailField, gbc);

            gbc.gridy = 4;
            gbc.insets = new Insets(25, 0, 0, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JPanel buttonPanel = new JPanel(new BorderLayout(10, 0));
            buttonPanel.setOpaque(false);

            JButton backButton = createModernButton("Back", false);
            backButton.addActionListener(e -> client.showLogin());

            JButton nextButton = createModernButton("Next", true);
            nextButton.addActionListener(e -> attemptFindUser());

            buttonPanel.add(backButton, BorderLayout.WEST);
            buttonPanel.add(nextButton, BorderLayout.EAST);
            card.add(buttonPanel, gbc);

            add(card);
        }

        private void attemptFindUser() {
            String email = emailField.getText().trim();
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your email.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Optional<User> userOpt = dataManager.getUsers().stream()
                    .filter(u -> u.getEmailId().equalsIgnoreCase(email))
                    .findFirst();

            if (userOpt.isPresent()) {
                targetUser = userOpt.get();
                step2.userLabel.setText("Account: " + targetUser.getEmailId());
                showStep("ResetPassword");
            } else {
                JOptionPane.showMessageDialog(this, "No account found with that email.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class Step2_ResetPassword extends JPanel {
        private final JPasswordField newPasswordField;
        private final JPasswordField confirmPasswordField;
        private final JLabel userLabel;

        public Step2_ResetPassword() {
            setOpaque(false);
            setLayout(new GridBagLayout());

            GlassCardPanel card = new GlassCardPanel();
            card.setLayout(new GridBagLayout());
            card.setBorder(new EmptyBorder(50, 60, 50, 60));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 0, 10, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;

            gbc.gridy = 0;
            card.add(createLogoPanel(), gbc);

            gbc.gridy = 1;
            gbc.insets = new Insets(20, 0, 5, 0);
            JLabel title = new JLabel("Reset Password");
            title.setFont(new Font("Segoe UI", Font.BOLD, 28));
            title.setForeground(TEXT_PRIMARY);
            title.setHorizontalAlignment(JLabel.CENTER);
            card.add(title, gbc);

            gbc.gridy = 2;
            gbc.insets = new Insets(0, 0, 20, 0);
            userLabel = new JLabel("Account: ");
            userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            userLabel.setForeground(ACCENT_COLOR);
            userLabel.setHorizontalAlignment(JLabel.CENTER);
            card.add(userLabel, gbc);

            gbc.gridy = 3;
            gbc.insets = new Insets(10, 0, 5, 0);
            JLabel l1 = new JLabel("New Password");
            l1.setFont(new Font("Segoe UI", Font.BOLD, 14));
            l1.setForeground(TEXT_SECONDARY);
            card.add(l1, gbc);

            gbc.gridy = 4;
            gbc.insets = new Insets(0, 0, 10, 0);
            newPasswordField = createModernPasswordField();
            card.add(newPasswordField, gbc);

            gbc.gridy = 5;
            gbc.insets = new Insets(10, 0, 5, 0);
            JLabel l2 = new JLabel("Confirm Password");
            l2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            l2.setForeground(TEXT_SECONDARY);
            card.add(l2, gbc);

            gbc.gridy = 6;
            gbc.insets = new Insets(0, 0, 10, 0);
            confirmPasswordField = createModernPasswordField();
            card.add(confirmPasswordField, gbc);

            gbc.gridy = 7;
            gbc.insets = new Insets(25, 0, 0, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JPanel buttonPanel = new JPanel(new BorderLayout(10, 0));
            buttonPanel.setOpaque(false);

            JButton backButton = createModernButton("Back", false);
            backButton.addActionListener(e -> showStep("FindAccount"));

            JButton saveButton = createModernButton("Change Password", true);
            saveButton.addActionListener(e -> attemptReset());

            buttonPanel.add(backButton, BorderLayout.WEST);
            buttonPanel.add(saveButton, BorderLayout.EAST);
            card.add(buttonPanel, gbc);

            add(card);
        }

        private void attemptReset() {
            String p1 = new String(newPasswordField.getPassword());
            String p2 = new String(confirmPasswordField.getPassword());

            if (p1.length() < 6) {
                JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!p1.equals(p2)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            targetUser.setPasswordHash(p1);
            dataManager.updateUser(targetUser);

            JOptionPane.showMessageDialog(this, "Password successfully changed. Please login.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            client.showLogin();
        }
    }
}