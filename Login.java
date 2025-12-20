import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

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

    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color CARD_BG_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(30, 30, 30);
    private static final Color SECONDARY_TEXT_COLOR = new Color(100, 100, 100);
    private static final Color LINK_COLOR = new Color(0, 102, 204);
    private static final Color LOGO_COLOR = new Color(219, 68, 55);
    private static final Color BUTTON_COLOR = new Color(26, 115, 232);
    private static final Color SECONDARY_BUTTON_BG = new Color(230, 230, 230);

    public Login(EmailClient client) {
        this.client = client;
        this.dataManager = client.getDataManager();
        this.cardLayout = new CardLayout();
        
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        
        this.cardPanel = new JPanel(cardLayout);
        this.cardPanel.setOpaque(false);

        step1 = new Step1_Email();
        step2 = new Step2_Password();

        cardPanel.add(step1, "Email");
        cardPanel.add(step2, "Password");

        add(cardPanel, BorderLayout.CENTER);
    }

    public void clearFields() {
        enteredEmail = null;
        targetUser = Optional.empty();
        userFirstName = null;
        step1.emailField.setText("username@mail.cm");
        step1.emailField.setForeground(Color.GRAY);
        
        step2.passwordField.setText("");
        step2.passwordField.setEchoChar((char) UIManager.get("PasswordField.echoChar"));
        step2.titleLabel.setText("Welcome"); 
        
        showStep("Email");
    }

    public void showStep(String stepName) {
        cardLayout.show(cardPanel, stepName);
    }

    class RoundedPanel extends JPanel {
        private int cornerRadius = 30;

        public RoundedPanel() {
            super();
            setOpaque(false); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(CARD_BG_COLOR);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            
            g2.setColor(new Color(220, 220, 220));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius);
            
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        JLabel logoLabel = new JLabel("Mail.CM");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 32));
        logoLabel.setForeground(LOGO_COLOR);
        logoPanel.add(logoLabel);
        return logoPanel;
    }

    private JTextField createFormField() {
        JTextField field = new JTextField(25);
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(200, 200, 200)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        return field;
    }

    private JButton createLinkButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(LINK_COLOR);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        return button;
    }

    private JButton createPillButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight()); 
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    class Step1_Email extends JPanel {
        private final JTextField emailField;
        private final String PLACEHOLDER = "username@mail.cm";

        public Step1_Email() {
            setOpaque(false);
            setLayout(new GridBagLayout()); 
            
            RoundedPanel card = new RoundedPanel();
            card.setLayout(new GridBagLayout());
            card.setBorder(new EmptyBorder(40, 50, 40, 50));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 0, 10, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;

            gbc.gridy = 0;
            card.add(createLogoPanel(), gbc);

            gbc.gridy = 1;
            gbc.insets = new Insets(15, 0, 5, 0);
            JLabel title = new JLabel("Sign in");
            title.setFont(new Font("Arial", Font.BOLD, 28));
            title.setForeground(TEXT_COLOR);
            title.setHorizontalAlignment(JLabel.CENTER);
            card.add(title, gbc);

            gbc.gridy = 2;
            gbc.insets = new Insets(0, 0, 15, 0);
            JLabel subtitle = new JLabel("to continue to " + EmailClient.APP_NAME);
            subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
            subtitle.setForeground(SECONDARY_TEXT_COLOR);
            subtitle.setHorizontalAlignment(JLabel.CENTER);
            card.add(subtitle, gbc);

            gbc.gridy = 3;
            gbc.insets = new Insets(15, 0, 10, 0);
            emailField = createFormField();
            emailField.setText(PLACEHOLDER);
            emailField.setForeground(Color.GRAY);
            
            emailField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (emailField.getText().equals(PLACEHOLDER)) {
                        emailField.setText("");
                        emailField.setForeground(TEXT_COLOR);
                    }
                }
                @Override
                public void focusLost(FocusEvent e) {
                    if (emailField.getText().isEmpty()) {
                        emailField.setForeground(Color.GRAY);
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
            gbc.insets = new Insets(20, 0, 0, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            JPanel buttonPanel = new JPanel(new BorderLayout());
            buttonPanel.setOpaque(false);

            JButton nextButton = createPillButton("Next", BUTTON_COLOR, Color.WHITE);
            nextButton.setPreferredSize(new Dimension(100, 40));
            
            JPanel rightBtnContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            rightBtnContainer.setOpaque(false);
            rightBtnContainer.add(nextButton);
            
            buttonPanel.add(rightBtnContainer, BorderLayout.CENTER);
            card.add(buttonPanel, gbc);
            
            add(card);

            nextButton.addActionListener(e -> attemptStep1());
            createAccountButton.addActionListener(e -> client.showRegister());
        }

        private void attemptStep1() {
            String email = emailField.getText().trim();
            if (email.equals(PLACEHOLDER) || email.isEmpty() || !email.endsWith(EmailClient.DOMAIN)) {
                JOptionPane.showMessageDialog(this, "Please enter a valid " + EmailClient.DOMAIN + " email address.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            targetUser = dataManager.getUsers().stream()
                .filter(u -> u.getEmailId().equalsIgnoreCase(email))
                .findFirst();
            
            if (targetUser.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Could not find your " + EmailClient.APP_NAME + " Account.", "User Not Found", JOptionPane.ERROR_MESSAGE);
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

            RoundedPanel card = new RoundedPanel();
            card.setLayout(new GridBagLayout());
            card.setBorder(new EmptyBorder(40, 50, 40, 50));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 0, 10, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.gridx = 0;

            gbc.gridy = 0;
            card.add(createLogoPanel(), gbc);

            gbc.gridy = 1;
            gbc.insets = new Insets(15, 0, 5, 0);
            titleLabel = new JLabel("Welcome"); 
            titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
            titleLabel.setForeground(TEXT_COLOR);
            titleLabel.setHorizontalAlignment(JLabel.CENTER);
            card.add(titleLabel, gbc);

            gbc.gridy = 2;
            gbc.insets = new Insets(0, 0, 15, 0);
            
            emailDisplayLabel = new JLabel(""); 
            emailDisplayLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            emailDisplayLabel.setForeground(SECONDARY_TEXT_COLOR);
            emailDisplayLabel.setHorizontalAlignment(JLabel.CENTER);
            emailDisplayLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            emailDisplayLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) { showStep("Email"); }
            });
            card.add(emailDisplayLabel, gbc);

            gbc.gridy = 3;
            gbc.insets = new Insets(15, 0, 10, 0);
            passwordField = new JPasswordField(25);
            passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
            passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(200, 200, 200)),
                new EmptyBorder(10, 10, 10, 10)
            ));
            card.add(passwordField, gbc);

            gbc.gridy = 4;
            gbc.insets = new Insets(10, 0, 0, 0);
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.NONE;
            
            JButton forgotPasswordButton = createLinkButton("Forgot password?");
            forgotPasswordButton.addActionListener(e -> client.showForgetPass());
            card.add(forgotPasswordButton, gbc);

            gbc.gridy = 5;
            gbc.insets = new Insets(20, 0, 0, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            JPanel buttonPanel = new JPanel(new BorderLayout());
            buttonPanel.setOpaque(false);

            JButton backButton = createPillButton("Back", SECONDARY_BUTTON_BG, Color.BLACK);
            backButton.setPreferredSize(new Dimension(80, 40));
            backButton.addActionListener(e -> showStep("Email")); 

            JButton nextButton = createPillButton("Next", BUTTON_COLOR, Color.WHITE);
            nextButton.setPreferredSize(new Dimension(100, 40));

            buttonPanel.add(backButton, BorderLayout.WEST);
            buttonPanel.add(nextButton, BorderLayout.EAST);
            card.add(buttonPanel, gbc);
            
            add(card); 

            nextButton.addActionListener(e -> attemptStep2());
        }

        @Override
        public void addNotify() {
            super.addNotify();
            if (enteredEmail != null) emailDisplayLabel.setText(enteredEmail);
            if (userFirstName != null && !userFirstName.isEmpty()) titleLabel.setText("Welcome, " + userFirstName);
            else titleLabel.setText("Welcome"); 
        }

        private void attemptStep2() {
            String password = new String(passwordField.getPassword());
            if (targetUser.isPresent() && targetUser.get().getPasswordHash().equals(password)) {
                client.showMailbox(targetUser.get());
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect password. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        }
    }
}