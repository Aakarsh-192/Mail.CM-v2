import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.Timer;

public class SignUp extends JPanel {
    private final EmailClient client;
    private final IDataManager dataManager;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    private String tempFirstName;
    private String tempLastName;
    private String tempEmailID;
    private String tempPassword;

    private Step1_Name step1;
    private Step2_Email step2;
    private Step3_Password step3;

    
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

    public SignUp(EmailClient client) {
        this.client = client;
        this.dataManager = client.getDataManager();
        this.cardLayout = new CardLayout();

        setLayout(new BorderLayout());
        setOpaque(false);

        this.cardPanel = new JPanel(cardLayout);
        this.cardPanel.setOpaque(false);

        step1 = new Step1_Name();
        step2 = new Step2_Email();
        step3 = new Step3_Password();

        cardPanel.add(step1, "Name");
        cardPanel.add(step2, "Email");
        cardPanel.add(step3, "Password");

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
        tempFirstName = null;
        tempLastName = null;
        tempEmailID = null;
        tempPassword = null;

        step1.firstNameField.setText("");
        step1.lastNameField.setText("");
        step2.emailIdField.setText("");
        step2.suggestionArea.setText("");
        step3.passwordField.setText("");
        step3.confirmPasswordField.setText("");
        step3.showPasswordCheckbox.setSelected(false);
        char defaultEchoChar = (char) UIManager.get("PasswordField.echoChar");
        step3.passwordField.setEchoChar(defaultEchoChar);
        step3.confirmPasswordField.setEchoChar(defaultEchoChar);

        showStep("Name");
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

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_SECONDARY);
        return label;
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

    private JPanel createStepIndicator(int currentStep) {
        JPanel indicator = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        indicator.setOpaque(false);

        for (int i = 1; i <= 3; i++) {
            JLabel dot = new JLabel("●");
            dot.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            if (i == currentStep) {
                dot.setForeground(ACCENT_COLOR);
            } else if (i < currentStep) {
                dot.setForeground(SUCCESS_COLOR);
            } else {
                dot.setForeground(new Color(200, 200, 200));
            }
            indicator.add(dot);
        }

        return indicator;
    }

    abstract class BaseStepPanel extends JPanel {
        protected int stepNumber;

        public BaseStepPanel(int stepNumber) {
            this.stepNumber = stepNumber;
            setOpaque(false);
            setLayout(new GridBagLayout());

            GlassCardPanel cardPanel = new GlassCardPanel();
            cardPanel.setLayout(new GridBagLayout());
            cardPanel.setBorder(new EmptyBorder(50, 60, 50, 60));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 0, 10, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridy = 0;
            cardPanel.add(createLogoPanel(), gbc);

            gbc.gridy = 1;
            gbc.insets = new Insets(15, 0, 15, 0);
            cardPanel.add(createStepIndicator(stepNumber), gbc);

            gbc.gridy = 2;
            gbc.insets = new Insets(10, 0, 10, 0);
            cardPanel.add(createStepContent(), gbc);

            add(cardPanel);
        }

        abstract JPanel createStepContent();
    }

    class Step1_Name extends BaseStepPanel {
        private JTextField firstNameField;
        private JTextField lastNameField;

        public Step1_Name() {
            super(1);
        }

        @Override
        JPanel createStepContent() {
            JPanel content = new JPanel(new GridBagLayout());
            content.setOpaque(false);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;

            gbc.gridy = 0;
            gbc.gridwidth = 2;
            JLabel title = new JLabel("Create your Account");
            title.setFont(new Font("Segoe UI", Font.BOLD, 28));
            title.setForeground(TEXT_PRIMARY);
            title.setHorizontalAlignment(JLabel.CENTER);
            content.add(title, gbc);

            gbc.gridy = 1;
            gbc.insets = new Insets(0, 0, 20, 0);
            JLabel subtitle = new JLabel("Enter your name");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            subtitle.setForeground(TEXT_SECONDARY);
            subtitle.setHorizontalAlignment(JLabel.CENTER);
            content.add(subtitle, gbc);

            gbc.gridy = 2;
            gbc.gridwidth = 1;
            gbc.insets = new Insets(10, 0, 5, 5);
            content.add(createFieldLabel("First name"), gbc);

            gbc.gridx = 1;
            gbc.insets = new Insets(10, 5, 5, 0);
            content.add(createFieldLabel("Last name (optional)"), gbc);

            gbc.gridy = 3;
            gbc.gridx = 0;
            gbc.insets = new Insets(0, 0, 10, 5);
            firstNameField = createModernTextField();
            content.add(firstNameField, gbc);

            gbc.gridx = 1;
            gbc.insets = new Insets(0, 5, 10, 0);
            lastNameField = createModernTextField();
            content.add(lastNameField, gbc);

            gbc.gridy = 4;
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(10, 0, 0, 0);
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.NONE;

            JButton signInButton = createLinkButton("Sign in instead");
            content.add(signInButton, gbc);

            gbc.gridy = 5;
            gbc.insets = new Insets(25, 0, 0, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            buttonPanel.setOpaque(false);

            JButton nextButton = createModernButton("Next", true);
            buttonPanel.add(nextButton);

            content.add(buttonPanel, gbc);

            nextButton.addActionListener(e -> attemptStep1());
            signInButton.addActionListener(e -> client.showLogin());

            return content;
        }

        private void attemptStep1() {
            String first = firstNameField.getText().trim();
            String last = lastNameField.getText().trim();
            if (first.isEmpty()) {
                JOptionPane.showMessageDialog(this, "First name is required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            tempFirstName = first;
            tempLastName = last;
            showStep("Email");
        }
    }

    class Step2_Email extends BaseStepPanel {
        private JTextField emailIdField;
        private JTextArea suggestionArea;

        public Step2_Email() {
            super(2);
        }

        @Override
        JPanel createStepContent() {
            JPanel content = new JPanel(new GridBagLayout());
            content.setOpaque(false);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridwidth = 2;

            gbc.gridy = 0;
            JLabel title = new JLabel("How you'll sign in");
            title.setFont(new Font("Segoe UI", Font.BOLD, 28));
            title.setForeground(TEXT_PRIMARY);
            title.setHorizontalAlignment(JLabel.CENTER);
            content.add(title, gbc);

            gbc.gridy = 1;
            gbc.insets = new Insets(0, 0, 20, 0);
            JLabel subtitle = new JLabel("Create a " + EmailClient.APP_NAME + " address");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            subtitle.setForeground(TEXT_SECONDARY);
            subtitle.setHorizontalAlignment(JLabel.CENTER);
            content.add(subtitle, gbc);

            gbc.gridy = 2;
            gbc.insets = new Insets(10, 0, 5, 0);
            content.add(createFieldLabel("Username"), gbc);

            gbc.gridy = 3;
            gbc.insets = new Insets(0, 0, 5, 0);

            JPanel emailContainer = new JPanel(new BorderLayout(5, 0));
            emailContainer.setOpaque(false);
            emailIdField = createModernTextField();
            emailContainer.add(emailIdField, BorderLayout.CENTER);

            JLabel domainLabel = new JLabel(EmailClient.DOMAIN);
            domainLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            domainLabel.setForeground(TEXT_SECONDARY);
            emailContainer.add(domainLabel, BorderLayout.EAST);
            content.add(emailContainer, gbc);

            gbc.gridy = 4;
            gbc.insets = new Insets(0, 0, 10, 0);
            JLabel hintLabel = new JLabel("You can use letters, numbers & periods");
            hintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            hintLabel.setForeground(TEXT_SECONDARY);
            content.add(hintLabel, gbc);

            gbc.gridy = 5;
            suggestionArea = new JTextArea(3, 20);
            suggestionArea.setEditable(false);
            suggestionArea.setOpaque(false);
            suggestionArea.setForeground(ERROR_COLOR);
            suggestionArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            suggestionArea.setBorder(BorderFactory.createEmptyBorder());
            content.add(suggestionArea, gbc);

            gbc.gridy = 6;
            gbc.insets = new Insets(10, 0, 0, 0);
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            JButton signInButton = createLinkButton("Sign in instead");
            signInButton.addActionListener(e -> client.showLogin());
            content.add(signInButton, gbc);

            gbc.gridy = 7;
            gbc.insets = new Insets(20, 0, 0, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JPanel buttonPanel = new JPanel(new BorderLayout(10, 0));
            buttonPanel.setOpaque(false);

            JButton backButton = createModernButton("Back", false);
            backButton.addActionListener(e -> showStep("Name"));

            JButton nextButton = createModernButton("Next", true);

            buttonPanel.add(backButton, BorderLayout.WEST);
            buttonPanel.add(nextButton, BorderLayout.EAST);
            content.add(buttonPanel, gbc);

            nextButton.addActionListener(e -> attemptStep2());
            emailIdField.addCaretListener(e -> checkEmailAvailability());

            return content;
        }

        private void checkEmailAvailability() {
            String desiredId = emailIdField.getText().trim();
            String fullEmail = desiredId + EmailClient.DOMAIN;
            suggestionArea.setText("");
            if (desiredId.isEmpty())
                return;

            if (isEmailTaken(fullEmail)) {
                suggestionArea.setForeground(ERROR_COLOR);
                suggestionArea.setText("That username is taken. Try another.\n");
                List<String> suggestions = suggestEmailIDs(desiredId);
                suggestions.forEach(
                        s -> suggestionArea.append("  • " + s.substring(0, s.indexOf(EmailClient.DOMAIN)) + "\n"));
            } else {
                suggestionArea.setForeground(SUCCESS_COLOR);
                suggestionArea.setText("✓ Username is available");
            }
        }

        private boolean isEmailTaken(String fullEmail) {
            return dataManager.getUsers().stream().anyMatch(u -> u.getEmailId().equalsIgnoreCase(fullEmail));
        }

        private List<String> suggestEmailIDs(String baseId) {
            Set<String> suggestions = new HashSet<>();
            Random rand = new Random();
            if (baseId.isEmpty())
                baseId = "user";
            while (suggestions.size() < 3) {
                String randomPart = String.format("%04d", rand.nextInt(10000));
                String s = baseId + randomPart + EmailClient.DOMAIN;
                if (!isEmailTaken(s))
                    suggestions.add(s);
            }
            return new ArrayList<>(suggestions);
        }

        private void attemptStep2() {
            String desiredId = emailIdField.getText().trim();
            String fullEmail = desiredId + EmailClient.DOMAIN;
            if (desiredId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username is required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (isEmailTaken(fullEmail)) {
                JOptionPane.showMessageDialog(this, "That username is taken. Please choose an available ID.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            tempEmailID = desiredId;
            showStep("Password");
        }
    }

    class Step3_Password extends BaseStepPanel {
        private JPasswordField passwordField;
        private JPasswordField confirmPasswordField;
        private JCheckBox showPasswordCheckbox;

        public Step3_Password() {
            super(3);
        }

        @Override
        JPanel createStepContent() {
            JPanel content = new JPanel(new GridBagLayout());
            content.setOpaque(false);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;
            gbc.gridwidth = 2;

            gbc.gridy = 0;
            JLabel title = new JLabel("Create a strong password");
            title.setFont(new Font("Segoe UI", Font.BOLD, 28));
            title.setForeground(TEXT_PRIMARY);
            title.setHorizontalAlignment(JLabel.CENTER);
            content.add(title, gbc);

            gbc.gridy = 1;
            gbc.insets = new Insets(0, 0, 20, 0);
            JLabel subtitle = new JLabel("Use a mix of letters, numbers & symbols");
            subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            subtitle.setForeground(TEXT_SECONDARY);
            subtitle.setHorizontalAlignment(JLabel.CENTER);
            content.add(subtitle, gbc);

            gbc.gridy = 2;
            gbc.gridwidth = 1;
            gbc.insets = new Insets(10, 0, 5, 5);
            content.add(createFieldLabel("Password"), gbc);

            gbc.gridx = 1;
            gbc.insets = new Insets(10, 5, 5, 0);
            content.add(createFieldLabel("Confirm"), gbc);

            gbc.gridy = 3;
            gbc.gridx = 0;
            gbc.insets = new Insets(0, 0, 10, 5);
            passwordField = createModernPasswordField();
            content.add(passwordField, gbc);

            gbc.gridx = 1;
            gbc.insets = new Insets(0, 5, 10, 0);
            confirmPasswordField = createModernPasswordField();
            content.add(confirmPasswordField, gbc);

            gbc.gridy = 4;
            gbc.gridx = 0;
            gbc.gridwidth = 2;
            gbc.insets = new Insets(5, 0, 10, 0);
            showPasswordCheckbox = new JCheckBox("Show password");
            showPasswordCheckbox.setOpaque(false);
            showPasswordCheckbox.setForeground(TEXT_PRIMARY);
            showPasswordCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            showPasswordCheckbox.setFocusPainted(false);
            content.add(showPasswordCheckbox, gbc);

            gbc.gridy = 5;
            gbc.insets = new Insets(10, 0, 0, 0);
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            JButton signInButton = createLinkButton("Sign in instead");
            signInButton.addActionListener(e -> client.showLogin());
            content.add(signInButton, gbc);

            gbc.gridy = 6;
            gbc.insets = new Insets(20, 0, 0, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JPanel buttonPanel = new JPanel(new BorderLayout(10, 0));
            buttonPanel.setOpaque(false);

            JButton backButton = createModernButton("Back", false);
            backButton.addActionListener(e -> showStep("Email"));

            JButton nextButton = createModernButton("Next", true);

            buttonPanel.add(backButton, BorderLayout.WEST);
            buttonPanel.add(nextButton, BorderLayout.EAST);
            content.add(buttonPanel, gbc);

            nextButton.addActionListener(e -> attemptStep3());
            showPasswordCheckbox.addActionListener(e -> {
                char echoChar = showPasswordCheckbox.isSelected() ? 0 : (char) UIManager.get("PasswordField.echoChar");
                passwordField.setEchoChar(echoChar);
                confirmPasswordField.setEchoChar(echoChar);
            });

            return content;
        }

        private void attemptStep3() {
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmPasswordField.getPassword());
            if (password.length() < 6) {
                JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!password.equals(confirm)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String fullEmail = tempEmailID + EmailClient.DOMAIN;
            String fullName = tempFirstName + (tempLastName.isEmpty() ? "" : " " + tempLastName);

            User newUser = new User(fullName, fullEmail, password);
            dataManager.addUser(newUser);

            JOptionPane.showMessageDialog(this, "Account created successfully for " + fullEmail + "!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            client.showLogin();
        }
    }
}