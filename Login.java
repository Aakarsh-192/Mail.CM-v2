import java.awt.*;
import java.awt.event.*;
import java.util.Optional;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.Timer;

public class Login extends JPanel {
    private final EmailClient client;
    private final IDataManager dataManager;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    private String enteredEmail;
    private Optional<User> targetUser = Optional.empty();
    private String userFirstName;

    private Step1_Email step1;
    private Step2_Password step2;

    
    private static final Color GRADIENT_START = new Color(255, 94, 77); 
    private static final Color GRADIENT_MID = new Color(233, 72, 13); 
    private static final Color GRADIENT_END = new Color(255, 138, 48); 
    private static final Color CARD_BG = new Color(255, 255, 255, 245); 
    private static final Color TEXT_PRIMARY = new Color(30, 30, 30);
    private static final Color TEXT_SECONDARY = new Color(100, 100, 100);
    private static final Color ACCENT_COLOR = new Color(233, 72, 13); 
    private static final Color ACCENT_HOVER = new Color(203, 62, 11); 
    private static final Color SUCCESS_COLOR = new Color(52, 168, 83);
    private static final Color ERROR_COLOR = new Color(234, 67, 53);
    private static final Color INPUT_BORDER = new Color(220, 220, 220);
    private static final Color INPUT_FOCUS = new Color(233, 72, 13); 

    private float gradientOffset = 0.0f;
    private Timer animationTimer;

    public Login(EmailClient client) {
        this.client = client;
        this.dataManager = client.getDataManager();
        this.cardLayout = new CardLayout();

        setLayout(new BorderLayout());
        setOpaque(false);

        this.cardPanel = new JPanel(cardLayout);
        this.cardPanel.setOpaque(false);

        step1 = new Step1_Email();
        step2 = new Step2_Password();

        cardPanel.add(step1, "Email");
        cardPanel.add(step2, "Password");

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

        GradientPaint gradient = new GradientPaint(
                0, 0, color1,
                w, h, color2);

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
        enteredEmail = null;
        targetUser = Optional.empty();
        userFirstName = null;
        step1.emailField.setText("username@mail.cm");
        step1.emailField.setForeground(TEXT_SECONDARY);

        step2.passwordField.setText("");
        step2.passwordField.setEchoChar((char) UIManager.get("PasswordField.echoChar"));
        step2.titleLabel.setText("Welcome");

        showStep("Email");
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

                
                g2.setColor(getBackground());
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

    private JButton createLinkButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(ACCENT_COLOR);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(ACCENT_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(ACCENT_COLOR);
            }
        });

        return button;
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
                            0, getHeight(), isHovered ? new Color(98, 75, 200) : new Color(110, 80, 220));
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

    class Step1_Email extends JPanel {
        private final JTextField emailField;
        private final String PLACEHOLDER = "username@mail.cm";

        public Step1_Email() {
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
            JLabel title = new JLabel("Sign in");
            title.setFont(new Font("Segoe UI", Font.BOLD, 32));
            title.setForeground(TEXT_PRIMARY);
            title.setHorizontalAlignment(JLabel.CENTER);
            card.add(title, gbc);

            gbc.gridy = 2;
            gbc.insets = new Insets(0, 0, 25, 0);
            JLabel subtitle = new JLabel("to continue to " + EmailClient.APP_NAME);
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            subtitle.setForeground(TEXT_SECONDARY);
            subtitle.setHorizontalAlignment(JLabel.CENTER);
            card.add(subtitle, gbc);

            gbc.gridy = 3;
            gbc.insets = new Insets(15, 0, 10, 0);
            emailField = createModernTextField();
            emailField.setText(PLACEHOLDER);
            emailField.setForeground(TEXT_SECONDARY);

            emailField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (emailField.getText().equals(PLACEHOLDER)) {
                        emailField.setText("");
                        emailField.setForeground(TEXT_PRIMARY);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (emailField.getText().isEmpty()) {
                        emailField.setForeground(TEXT_SECONDARY);
                        emailField.setText(PLACEHOLDER);
                    }
                }
            });

            card.add(emailField, gbc);

            gbc.gridy = 4;
            gbc.insets = new Insets(10, 0, 0, 0);
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.NONE;

            JButton createAccountButton = createLinkButton("Create account");
            card.add(createAccountButton, gbc);

            gbc.gridy = 5;
            gbc.insets = new Insets(25, 0, 0, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            buttonPanel.setOpaque(false);

            JButton nextButton = createModernButton("Next", true);
            buttonPanel.add(nextButton);

            card.add(buttonPanel, gbc);
            add(card);

            nextButton.addActionListener(e -> attemptStep1());
            createAccountButton.addActionListener(e -> client.showRegister());
        }

        private void attemptStep1() {
            String email = emailField.getText().trim();
            if (email.equals(PLACEHOLDER) || email.isEmpty() || !email.endsWith(EmailClient.DOMAIN)) {
                JOptionPane.showMessageDialog(this, "Please enter a valid " + EmailClient.DOMAIN + " email address.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            targetUser = dataManager.getUsers().stream()
                    .filter(u -> u.getEmailId().equalsIgnoreCase(email))
                    .findFirst();

            if (targetUser.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Could not find your " + EmailClient.APP_NAME + " Account.",
                        "User Not Found", JOptionPane.ERROR_MESSAGE);
                return;
            }
            enteredEmail = email;
            userFirstName = targetUser.get().getName().split(" ")[0];
            showStep("Password");
        }
    }

    class Step2_Password extends JPanel {
        private final JPasswordField passwordField;
        private final JLabel emailDisplayLabel;
        private final JLabel titleLabel;

        public Step2_Password() {
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
            titleLabel = new JLabel("Welcome");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
            titleLabel.setForeground(TEXT_PRIMARY);
            titleLabel.setHorizontalAlignment(JLabel.CENTER);
            card.add(titleLabel, gbc);

            gbc.gridy = 2;
            gbc.insets = new Insets(0, 0, 25, 0);

            emailDisplayLabel = new JLabel("");
            emailDisplayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            emailDisplayLabel.setForeground(ACCENT_COLOR);
            emailDisplayLabel.setHorizontalAlignment(JLabel.CENTER);
            emailDisplayLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            emailDisplayLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showStep("Email");
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    emailDisplayLabel.setForeground(ACCENT_HOVER);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    emailDisplayLabel.setForeground(ACCENT_COLOR);
                }
            });
            card.add(emailDisplayLabel, gbc);

            gbc.gridy = 3;
            gbc.insets = new Insets(15, 0, 10, 0);

            passwordField = new JPasswordField(25) {
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

            passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            passwordField.setBackground(Color.WHITE);
            passwordField.setForeground(TEXT_PRIMARY);
            passwordField.setBorder(new EmptyBorder(12, 16, 12, 16));
            passwordField.setOpaque(false);
            card.add(passwordField, gbc);

            gbc.gridy = 4;
            gbc.insets = new Insets(10, 0, 0, 0);
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.NONE;

            JButton forgotPasswordButton = createLinkButton("Forgot password?");
            forgotPasswordButton.addActionListener(e -> client.showForgetPass());
            card.add(forgotPasswordButton, gbc);

            gbc.gridy = 5;
            gbc.insets = new Insets(25, 0, 0, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JPanel buttonPanel = new JPanel(new BorderLayout(10, 0));
            buttonPanel.setOpaque(false);

            JButton backButton = createModernButton("Back", false);
            backButton.addActionListener(e -> showStep("Email"));

            JButton nextButton = createModernButton("Next", true);

            buttonPanel.add(backButton, BorderLayout.WEST);
            buttonPanel.add(nextButton, BorderLayout.EAST);
            card.add(buttonPanel, gbc);

            add(card);

            nextButton.addActionListener(e -> attemptStep2());
        }

        @Override
        public void addNotify() {
            super.addNotify();
            if (enteredEmail != null)
                emailDisplayLabel.setText(enteredEmail);
            if (userFirstName != null && !userFirstName.isEmpty())
                titleLabel.setText("Welcome, " + userFirstName);
            else
                titleLabel.setText("Welcome");
        }

        private void attemptStep2() {
            String password = new String(passwordField.getPassword());
            if (targetUser.isPresent() && targetUser.get().getPasswordHash().equals(password)) {
                client.showMailbox(targetUser.get());
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect password. Please try again.", "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        }
    }
}