import java.awt.*;
import java.util.Optional;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;

public class ForgetPass extends JPanel {
    private final EmailClient client;
    private final IDataManager dataManager;
    private final CardLayout cardLayout;
    private final JPanel cardPanel;

    private User targetUser;

    private Step1_FindAccount step1;
    private Step2_ResetPassword step2;
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color CARD_BG_COLOR = Color.WHITE;
    private static final Color TEXT_COLOR = new Color(30, 30, 30);
    private static final Color SECONDARY_TEXT_COLOR = new Color(100, 100, 100);
    private static final Color LOGO_COLOR = new Color(219, 68, 55);
    private static final Color BUTTON_COLOR = new Color(26, 115, 232);
    private static final Color SECONDARY_BUTTON_BG = new Color(230, 230, 230);

    public ForgetPass(EmailClient client) {
        this.client = client;
        this.dataManager = client.getDataManager();
        this.cardLayout = new CardLayout();
        
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);
        this.cardPanel = new JPanel(cardLayout);
        this.cardPanel.setOpaque(false);

        step1 = new Step1_FindAccount();
        step2 = new Step2_ResetPassword();

        cardPanel.add(step1, "FindAccount");
        cardPanel.add(step2, "ResetPassword");

        add(cardPanel, BorderLayout.CENTER);
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
    
    class RoundedPanel extends JPanel {
        private int cornerRadius = 30;
        public RoundedPanel() { super(); setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
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
        JPanel logoPanel = new JPanel(); logoPanel.setOpaque(false);
        JLabel logoLabel = new JLabel("Mail.CM"); logoLabel.setFont(new Font("Arial", Font.BOLD, 32)); logoLabel.setForeground(LOGO_COLOR);
        logoPanel.add(logoLabel);
        return logoPanel;
    }

    private JTextField createFormField() {
        JTextField field = new JTextField(25); field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(200, 200, 200)), new EmptyBorder(10, 10, 10, 10)));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(25); field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(200, 200, 200)), new EmptyBorder(10, 10, 10, 10)));
        return field;
    }

    private JButton createPillButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        button.setUI(new BasicButtonUI());
        button.setBackground(bg); button.setForeground(fg);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false); button.setBorderPainted(false); button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    class Step1_FindAccount extends JPanel {
        private final JTextField emailField;

        public Step1_FindAccount() {
            setOpaque(false); setLayout(new GridBagLayout());
            RoundedPanel card = new RoundedPanel(); card.setLayout(new GridBagLayout()); card.setBorder(new EmptyBorder(40, 50, 40, 50));
            GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(10, 0, 10, 0); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridx = 0;

            gbc.gridy = 0; card.add(createLogoPanel(), gbc);
            
            gbc.gridy = 1; gbc.insets = new Insets(15, 0, 5, 0);
            JLabel title = new JLabel("Account Recovery"); title.setFont(new Font("Arial", Font.BOLD, 24)); title.setForeground(TEXT_COLOR); title.setHorizontalAlignment(JLabel.CENTER);
            card.add(title, gbc);

            gbc.gridy = 2; gbc.insets = new Insets(0, 0, 15, 0);
            JLabel subtitle = new JLabel("Enter your email to reset password"); subtitle.setFont(new Font("Arial", Font.PLAIN, 16)); subtitle.setForeground(SECONDARY_TEXT_COLOR); subtitle.setHorizontalAlignment(JLabel.CENTER);
            card.add(subtitle, gbc);

            gbc.gridy = 3; gbc.insets = new Insets(15, 0, 10, 0);
            emailField = createFormField();
            card.add(emailField, gbc);

            gbc.gridy = 4; gbc.insets = new Insets(20, 0, 0, 0); gbc.fill = GridBagConstraints.HORIZONTAL;
            JPanel buttonPanel = new JPanel(new BorderLayout()); buttonPanel.setOpaque(false);

            JButton backButton = createPillButton("Back", SECONDARY_BUTTON_BG, Color.BLACK);
            backButton.setPreferredSize(new Dimension(80, 40));
            backButton.addActionListener(e -> client.showLogin());

            JButton nextButton = createPillButton("Next", BUTTON_COLOR, Color.WHITE);
            nextButton.setPreferredSize(new Dimension(100, 40));
            nextButton.addActionListener(e -> attemptFindUser());

            buttonPanel.add(backButton, BorderLayout.WEST);
            buttonPanel.add(nextButton, BorderLayout.EAST);
            card.add(buttonPanel, gbc);

            add(card);
        }

        private void attemptFindUser() {
            String email = emailField.getText().trim();
            if(email.isEmpty()) { JOptionPane.showMessageDialog(this, "Please enter your email.", "Error", JOptionPane.ERROR_MESSAGE); return; }
            
            Optional<User> userOpt = dataManager.getUsers().stream().filter(u -> u.getEmailId().equalsIgnoreCase(email)).findFirst();
            
            if(userOpt.isPresent()) {
                targetUser = userOpt.get();
                step2.userLabel.setText("Account: " + targetUser.getEmailId());
                showStep("ResetPassword");
            } else {
                JOptionPane.showMessageDialog(this, "No account found with that email.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    class Step2_ResetPassword extends JPanel {
        private final JPasswordField newPasswordField;
        private final JPasswordField confirmPasswordField;
        private final JLabel userLabel;

        public Step2_ResetPassword() {
            setOpaque(false); setLayout(new GridBagLayout());
            RoundedPanel card = new RoundedPanel(); card.setLayout(new GridBagLayout()); card.setBorder(new EmptyBorder(40, 50, 40, 50));
            GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(10, 0, 10, 0); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.gridx = 0;

            gbc.gridy = 0; card.add(createLogoPanel(), gbc);

            gbc.gridy = 1; gbc.insets = new Insets(15, 0, 5, 0);
            JLabel title = new JLabel("Reset Password"); title.setFont(new Font("Arial", Font.BOLD, 24)); title.setForeground(TEXT_COLOR); title.setHorizontalAlignment(JLabel.CENTER);
            card.add(title, gbc);

            gbc.gridy = 2; gbc.insets = new Insets(0, 0, 15, 0);
            userLabel = new JLabel("Account: "); userLabel.setFont(new Font("Arial", Font.PLAIN, 14)); userLabel.setForeground(SECONDARY_TEXT_COLOR); userLabel.setHorizontalAlignment(JLabel.CENTER);
            card.add(userLabel, gbc);

            gbc.gridy = 3; gbc.insets = new Insets(5, 0, 0, 0);
            JLabel l1 = new JLabel("New Password"); l1.setForeground(SECONDARY_TEXT_COLOR);
            card.add(l1, gbc);
            
            gbc.gridy = 4; gbc.insets = new Insets(0, 0, 10, 0);
            newPasswordField = createPasswordField();
            card.add(newPasswordField, gbc);

            gbc.gridy = 5; gbc.insets = new Insets(5, 0, 0, 0);
            JLabel l2 = new JLabel("Confirm Password"); l2.setForeground(SECONDARY_TEXT_COLOR);
            card.add(l2, gbc);

            gbc.gridy = 6; gbc.insets = new Insets(0, 0, 10, 0);
            confirmPasswordField = createPasswordField();
            card.add(confirmPasswordField, gbc);

            gbc.gridy = 7; gbc.insets = new Insets(20, 0, 0, 0); gbc.fill = GridBagConstraints.HORIZONTAL;
            JPanel buttonPanel = new JPanel(new BorderLayout()); buttonPanel.setOpaque(false);

            JButton backButton = createPillButton("Back", SECONDARY_BUTTON_BG, Color.BLACK);
            backButton.setPreferredSize(new Dimension(80, 40));
            backButton.addActionListener(e -> showStep("FindAccount"));

            JButton saveButton = createPillButton("Change Password", BUTTON_COLOR, Color.WHITE);
            saveButton.addActionListener(e -> attemptReset());

            buttonPanel.add(backButton, BorderLayout.WEST);
            buttonPanel.add(saveButton, BorderLayout.EAST);
            card.add(buttonPanel, gbc);

            add(card);
        }

        private void attemptReset() {
            String p1 = new String(newPasswordField.getPassword());
            String p2 = new String(confirmPasswordField.getPassword());

            if(p1.length() < 6) { JOptionPane.showMessageDialog(this, "Password must be at least 6 characters.", "Error", JOptionPane.ERROR_MESSAGE); return; }
            if(!p1.equals(p2)) { JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE); return; }

            targetUser.setPasswordHash(p1);
            dataManager.updateUser(targetUser);

            JOptionPane.showMessageDialog(this, "Password successfully changed. Please login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            client.showLogin();
        }
    }
}