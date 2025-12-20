import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.*;

public class EmailClient extends JFrame {

    public static final String APP_NAME = "Mail.CM";
    public static final String DOMAIN = "@mail.cm";
    public static final int UNSEND_TIMEOUT_MS = 60000;
    public static final Color COLOR_PRIMARY = new Color(26, 115, 232); // Google Blue
    public static final Color COLOR_DANGER = new Color(219, 68, 55);   // Google Red
    public static final Color COLOR_SUCCESS = new Color(52, 168, 83);  // Google Green
    public static final Color COLOR_BG_MAIN = new Color(245, 245, 245); // Light Gray
    public static final Color COLOR_BG_CARD = Color.WHITE;
    public static final Color COLOR_TEXT_SECONDARY = new Color(95, 99, 104);

    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    
    public final IDataManager dataManager;
    
    private User loggedInUser;

    private Login loginPanel;
    private SignUp signUpPanel;
    private ForgetPass forgetPassPanel;
    private MailboxPanel mailboxPanel;

    public EmailClient() {
        IDataManager tempManager;
        try {
            tempManager = new JDBCDataManager();
        } catch (Exception e) {
            System.err.println("Failed to initialize JDBCDataManager: " + e.getMessage());
            tempManager = null; 
        }
        this.dataManager = tempManager;

        setTitle(APP_NAME);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        
        try {
            ImageIcon icon = new ImageIcon("CMlogo.png");
            setIconImage(icon.getImage());
        } catch (Exception e) {

        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        loginPanel = new Login(this);
        signUpPanel = new SignUp(this);
        forgetPassPanel = new ForgetPass(this); 

        mainPanel.add(new WelcomePanel(this), "Welcome");
        mainPanel.add(loginPanel, "Login");
        mainPanel.add(signUpPanel, "Register");
        mainPanel.add(forgetPassPanel, "ForgetPass"); 

        add(mainPanel);
    }

    public IDataManager getDataManager() { return dataManager; }

    public void showWelcome() { setTitle(APP_NAME); cardLayout.show(mainPanel, "Welcome"); }
    public void showLogin() { setTitle(APP_NAME + " - Sign In"); if(loginPanel != null) loginPanel.clearFields(); cardLayout.show(mainPanel, "Login"); }
    public void showRegister() { setTitle(APP_NAME + " - Create Account"); if(signUpPanel != null) signUpPanel.clearFields(); cardLayout.show(mainPanel, "Register"); }

    public void showForgetPass() {
        setTitle(APP_NAME + " - Reset Password");
        if(forgetPassPanel != null) forgetPassPanel.clearFields();
        cardLayout.show(mainPanel, "ForgetPass");
    }

    public void showMailbox(User user) {
        this.loggedInUser = user;
        setTitle(APP_NAME + " - " + user.getEmailId());
        
        if (mailboxPanel != null) {
            mainPanel.remove(mailboxPanel);
        }
        mailboxPanel = new MailboxPanel(this, dataManager, user);
        mainPanel.add(mailboxPanel, "Mailbox");
        cardLayout.show(mainPanel, "Mailbox");
    }

    public void logout() { this.loggedInUser = null; showWelcome(); }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> {
            Splash splash = new Splash();
            splash.setVisible(true);
            EmailClient emailClient = new EmailClient();
            splash.startLoading(emailClient);
        });
    }

    public JButton createStyledButton(String text, Color bg, Color fg) {
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
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        return button;
    }

    public static class RoundedPanel extends JPanel {
        private int radius = 30;
        public RoundedPanel() { super(); setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(new Color(220, 220, 220));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class AvatarIcon extends JComponent {
        private String letter;
        private Color bg;
        public AvatarIcon(String name) {
            this.letter = (name != null && !name.isEmpty()) ? name.substring(0, 1).toUpperCase() : "?";
            int hash = letter.hashCode();
            this.bg = new Color((hash * 123) % 200 + 50, (hash * 321) % 200 + 50, (hash * 213) % 200 + 50);
            setPreferredSize(new Dimension(42, 42));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillOval(0, 0, 40, 40);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 20));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(letter, (40 - fm.stringWidth(letter)) / 2, (40 + fm.getAscent()) / 2 - 3);
            g2.dispose();
        }
    }

    class WelcomePanel extends JPanel {
        public WelcomePanel(EmailClient client) {
            setLayout(new GridBagLayout());
            setBackground(COLOR_BG_MAIN);

            RoundedPanel card = new RoundedPanel();
            card.setBackground(Color.WHITE);
            card.setLayout(new GridBagLayout());
            card.setBorder(new EmptyBorder(40, 60, 40, 60));

            JLabel title = new JLabel(APP_NAME);
            title.setFont(new Font("Arial", Font.BOLD, 72));
            title.setForeground(COLOR_DANGER);

            JButton signInButton = createStyledButton("Sign In", COLOR_PRIMARY, Color.WHITE);
            signInButton.setPreferredSize(new Dimension(200, 50));

            JButton registerButton = createStyledButton("Create Account", COLOR_SUCCESS, Color.WHITE);
            registerButton.setPreferredSize(new Dimension(200, 50));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(20, 0, 20, 0);
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            card.add(title, gbc);

            gbc.gridy = 1; gbc.gridwidth = 1; gbc.insets = new Insets(40, 15, 20, 15);
            card.add(signInButton, gbc);

            gbc.gridx = 1; 
            card.add(registerButton, gbc);

            add(card);

            signInButton.addActionListener(e -> client.showLogin());
            registerButton.addActionListener(e -> client.showRegister());
        }
    }

    public class MailboxPanel extends JPanel {
        private final EmailClient client;
        private final IDataManager dataManager;
        private final User user;
        private final CardLayout contentCardLayout;
        private final JPanel contentCardPanel;

        private MailListPanel inboxListPanel, draftListPanel, sentListPanel, archiveListPanel, trashListPanel;
        private ComposePanel composePanel;
        private SettingsPanel settingsPanel;
        
        private JSplitPane mainSplitPane;
        private final int SIDEBAR_WIDTH = 220;
        private String currentView = "INBOX";

        public MailboxPanel(EmailClient client, IDataManager dataManager, User user) {
            this.client = client;
            this.dataManager = dataManager;
            this.user = user;
            setLayout(new BorderLayout());

            add(createHeaderPanel(), BorderLayout.NORTH);

            contentCardLayout = new CardLayout();
            contentCardPanel = new JPanel(contentCardLayout);

            inboxListPanel = new MailListPanel(this, client, ViewType.INBOX);
            draftListPanel = new MailListPanel(this, client, ViewType.DRAFTS);
            sentListPanel = new SentListPanel(this, client);
            archiveListPanel = new MailListPanel(this, client, ViewType.ARCHIVE);
            trashListPanel = new TrashPanel(this, client);
            composePanel = new ComposePanel(this, client);
            settingsPanel = new SettingsPanel(this, client);

            contentCardPanel.add(inboxListPanel, "INBOX");
            contentCardPanel.add(draftListPanel, "DRAFTS");
            contentCardPanel.add(sentListPanel, "SENT");
            contentCardPanel.add(archiveListPanel, "ARCHIVE");
            contentCardPanel.add(trashListPanel, "DELETED");
            contentCardPanel.add(composePanel, "COMPOSE");
            contentCardPanel.add(settingsPanel, "SETTINGS");

            mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createSidebar(), contentCardPanel);
            mainSplitPane.setDividerLocation(SIDEBAR_WIDTH);
            mainSplitPane.setDividerSize(1);
            mainSplitPane.setBorder(null);

            add(mainSplitPane, BorderLayout.CENTER);
            refreshAllViews();
            showView("INBOX");
        }

        private JPanel createHeaderPanel() {
            JPanel header = new JPanel(new BorderLayout());
            header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            header.setBackground(Color.WHITE);

            JLabel logo = new JLabel(APP_NAME);
            logo.setFont(new Font("Arial", Font.BOLD, 24));
            logo.setForeground(COLOR_DANGER);

            JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            right.setBackground(Color.WHITE);
            
            JLabel userLbl = new JLabel(user.getEmailId());
            userLbl.setFont(new Font("Arial", Font.PLAIN, 14));
            userLbl.setForeground(COLOR_TEXT_SECONDARY);
            
            JButton settingsBtn = client.createStyledButton("Settings", new Color(240, 240, 240), Color.BLACK);
            settingsBtn.addActionListener(e -> showView("SETTINGS"));
            
            JButton logoutBtn = client.createStyledButton("Sign Out", new Color(240, 240, 240), Color.BLACK);
            logoutBtn.addActionListener(e -> client.logout());

            right.add(userLbl);
            right.add(settingsBtn);
            right.add(logoutBtn);

            header.add(logo, BorderLayout.WEST);
            header.add(right, BorderLayout.EAST);
            return header;
        }

        private JPanel createSidebar() {
            JPanel sidebar = new JPanel();
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
            sidebar.setBackground(Color.WHITE);
            sidebar.setBorder(new EmptyBorder(10, 10, 10, 10));

            JButton composeBtn = client.createStyledButton("Compose", new Color(200, 225, 255), Color.BLACK);
            composeBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            composeBtn.setMaximumSize(new Dimension(200, 50));
            composeBtn.addActionListener(e -> showView("COMPOSE"));
            
            sidebar.add(composeBtn);
            sidebar.add(Box.createVerticalStrut(20));

            sidebar.add(createNavLink("Inbox", "INBOX"));
            sidebar.add(createNavLink("Drafts", "DRAFTS"));
            sidebar.add(createNavLink("Sent", "SENT"));
            sidebar.add(createNavLink("Archive", "ARCHIVE"));
            sidebar.add(createNavLink("Trash", "DELETED"));
            
            sidebar.add(Box.createVerticalGlue());
            return sidebar;
        }

        private JButton createNavLink(String text, String view) {
            JButton btn = new JButton(text);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(200, 40));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setBorder(new EmptyBorder(5, 15, 5, 15));
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setOpaque(true);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            if(currentView.equals(view)) {
                btn.setBackground(new Color(232, 240, 254));
                btn.setFont(new Font("Arial", Font.BOLD, 14));
                btn.setForeground(COLOR_PRIMARY);
            } else {
                btn.setBackground(Color.WHITE);
                btn.setFont(new Font("Arial", Font.PLAIN, 14));
                btn.setForeground(Color.BLACK);
            }
            
            btn.addActionListener(e -> showView(view));
            return btn;
        }

        public void showView(String view) {
            this.currentView = view;
            if("COMPOSE".equals(view)) {
                if(!composePanel.isLoading) composePanel.clearFields();
                composePanel.isLoading = false;
            }
            
            if("INBOX".equals(view)) inboxListPanel.refresh();
            else if("DRAFTS".equals(view)) draftListPanel.refresh();
            else if("SENT".equals(view)) sentListPanel.refresh();
            else if("ARCHIVE".equals(view)) archiveListPanel.refresh();
            else if("DELETED".equals(view)) trashListPanel.refresh();
            else if("SETTINGS".equals(view)) settingsPanel.loadUserSettings();

            contentCardLayout.show(contentCardPanel, view);
            mainSplitPane.setLeftComponent(createSidebar()); 
        }

        public void refreshAllViews() {
            inboxListPanel.refresh();
            draftListPanel.refresh();
            sentListPanel.refresh();
            archiveListPanel.refresh();
            trashListPanel.refresh();
        }

        public User getLoggedInUser() { return user; }
        public IDataManager getDataManager() { return dataManager; }
        public ComposePanel getComposePanel() { return composePanel; }
    }

    public class MailListPanel extends JPanel {
        protected final MailboxPanel parentPanel;
        protected final EmailClient client;
        protected final ViewType viewType;
        protected final CustomTableModel tableModel;
        protected JTable emailTable;
        protected JCheckBox masterCheckBox;
        protected JPanel readingPanel, emptyStatePanel;
        protected JLabel subjectLabel, fromLabel, dateLabel;
        protected JPanel avatarContainer, attachmentPanel;
        protected JTextArea bodyArea;
        protected JButton replyBtn, forwardBtn;
        protected Email currentSelectedEmail;
        protected JButton deleteButton, archiveButton, unarchiveButton, refreshButton;

        public static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("MMM d, HH:mm");

        public MailListPanel(MailboxPanel parent, EmailClient client, ViewType type) {
            this.parentPanel = parent; this.client = client; this.viewType = type;
            setLayout(new BorderLayout());

            add(createControlsPanel(), BorderLayout.NORTH);

            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            split.setDividerLocation(500);
            split.setDividerSize(3);
            split.setBorder(null);

            tableModel = new CustomTableModel(new String[]{"", "S", "Sender", "Subject", "Date"});
            emailTable = new JTable(tableModel);
            emailTable.setRowHeight(36);
            emailTable.setShowVerticalLines(false);
            emailTable.setIntercellSpacing(new Dimension(0,0));
            emailTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            
            TableColumnModel tcm = emailTable.getColumnModel();
            tcm.getColumn(0).setPreferredWidth(30); tcm.getColumn(0).setMaxWidth(30);
            tcm.getColumn(1).setPreferredWidth(0); tcm.getColumn(1).setMaxWidth(0); 
            tcm.getColumn(4).setPreferredWidth(90); tcm.getColumn(4).setMaxWidth(90); 
            tcm.removeColumn(tcm.getColumn(1)); 

            emailTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable t, Object v, boolean isSel, boolean hasF, int r, int c) {
                    super.getTableCellRendererComponent(t, v, isSel, hasF, r, c);
                    setBorder(new EmptyBorder(0, 5, 0, 5));
                    if(isSel) setBackground(new Color(232, 240, 254));
                    else setBackground(Color.WHITE);
                    Email e = (Email)tableModel.getValueAt(t.convertRowIndexToModel(r), 5);
                    if(e != null && !e.isRead()) setFont(getFont().deriveFont(Font.BOLD));
                    return this;
                }
            });

            tableModel.addTableModelListener(e -> { if (e.getColumn() == 0) updateControls(); });
            emailTable.getSelectionModel().addListSelectionListener(e -> { if(!e.getValueIsAdjusting()) displayEmail(); });

            split.setLeftComponent(new JScrollPane(emailTable));

            readingPanel = new JPanel(new BorderLayout());
            readingPanel.setBackground(Color.WHITE);
            emptyStatePanel = new JPanel(new GridBagLayout());
            emptyStatePanel.setBackground(Color.WHITE);
            JLabel emptyLbl = new JLabel("Select an email to read");
            emptyLbl.setForeground(Color.LIGHT_GRAY);
            emptyLbl.setFont(new Font("Arial", Font.BOLD, 16));
            emptyStatePanel.add(emptyLbl);
            
            readingPanel.add(emptyStatePanel, BorderLayout.CENTER);
            split.setRightComponent(readingPanel);

            add(split, BorderLayout.CENTER);
        }

        protected JPanel createControlsPanel() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p.setBackground(Color.WHITE);
            p.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.LIGHT_GRAY));

            masterCheckBox = new JCheckBox();
            masterCheckBox.setOpaque(false);
            masterCheckBox.addActionListener(e -> toggleAll(masterCheckBox.isSelected()));
            p.add(masterCheckBox);

            if(viewType == ViewType.INBOX) {
                refreshButton = client.createStyledButton("Refresh", new Color(240, 240, 240), Color.BLACK);
                refreshButton.addActionListener(e -> {
                    new SwingWorker<Void, Void>() {
                        @Override protected Void doInBackground() { parentPanel.getDataManager().reloadData(); return null; }
                        @Override protected void done() { parentPanel.refreshAllViews(); }
                    }.execute();
                });
                p.add(refreshButton);
                
                archiveButton = client.createStyledButton("Archive", new Color(240, 240, 240), Color.BLACK);
                archiveButton.addActionListener(e -> moveSelected(EmailStatus.ARCHIVED));
                p.add(archiveButton);
            }
            
            if(viewType == ViewType.ARCHIVE) {
                unarchiveButton = client.createStyledButton("Unarchive", COLOR_PRIMARY, Color.WHITE);
                unarchiveButton.addActionListener(e -> unarchiveSelected());
                p.add(unarchiveButton);
            }

            deleteButton = client.createStyledButton("Delete", COLOR_DANGER, Color.WHITE);
            deleteButton.addActionListener(e -> deleteSelected());
            p.add(deleteButton);

            return p;
        }

        private void initReadingUI() {
            readingPanel.removeAll();
            JPanel header = new JPanel(new GridBagLayout());
            header.setBackground(Color.WHITE);
            header.setBorder(new EmptyBorder(20, 30, 10, 30));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; gbc.gridx = 0;

            subjectLabel = new JLabel("Subject");
            subjectLabel.setFont(new Font("Arial", Font.BOLD, 22));
            gbc.gridy = 0; header.add(subjectLabel, gbc);

            JPanel senderRow = new JPanel(new BorderLayout(10, 0));
            senderRow.setOpaque(false);
            senderRow.setBorder(new EmptyBorder(15, 0, 0, 0));
            avatarContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            avatarContainer.setOpaque(false);
            senderRow.add(avatarContainer, BorderLayout.WEST);

            JPanel info = new JPanel(new GridLayout(2, 1));
            info.setOpaque(false);
            fromLabel = new JLabel("Sender Name"); fromLabel.setFont(new Font("Arial", Font.BOLD, 14));
            dateLabel = new JLabel("Date"); dateLabel.setForeground(Color.GRAY);
            info.add(fromLabel); info.add(dateLabel);
            senderRow.add(info, BorderLayout.CENTER);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            actions.setOpaque(false);
            replyBtn = client.createStyledButton("Reply", new Color(240, 240, 240), Color.BLACK);
            forwardBtn = client.createStyledButton("Forward", new Color(240, 240, 240), Color.BLACK);
            actions.add(replyBtn); actions.add(forwardBtn);
            senderRow.add(actions, BorderLayout.EAST);

            gbc.gridy = 1; header.add(senderRow, gbc);
            readingPanel.add(header, BorderLayout.NORTH);

            bodyArea = new JTextArea();
            bodyArea.setEditable(false);
            bodyArea.setLineWrap(true);
            bodyArea.setWrapStyleWord(true);
            bodyArea.setFont(new Font("Arial", Font.PLAIN, 14));
            bodyArea.setBorder(new EmptyBorder(20, 30, 20, 30));
            readingPanel.add(new JScrollPane(bodyArea), BorderLayout.CENTER);

            attachmentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            attachmentPanel.setBackground(new Color(245, 245, 245));
            attachmentPanel.setBorder(new EmptyBorder(10, 30, 10, 30));
            readingPanel.add(attachmentPanel, BorderLayout.SOUTH);
        }

        private void displayEmail() {
            int row = emailTable.getSelectedRow();
            if(row == -1) { readingPanel.removeAll(); readingPanel.add(emptyStatePanel); readingPanel.repaint(); currentSelectedEmail = null; return; }
            Email e = (Email) tableModel.getValueAt(emailTable.convertRowIndexToModel(row), 5);
            currentSelectedEmail = e;
            
            if(viewType == ViewType.DRAFTS) {
                initReadingUI();
                replyBtn.setVisible(false); forwardBtn.setVisible(false);
            } else {
                initReadingUI();
            }

            subjectLabel.setText(e.getSubject());
            fromLabel.setText(e.getFrom());
            dateLabel.setText(DATE_FMT.format(e.getTimestamp()));
            bodyArea.setText(e.getBody());
            bodyArea.setCaretPosition(0);

            avatarContainer.removeAll();
            String name = e.getFrom().split("@")[0];
            avatarContainer.add(new AvatarIcon(name));

            boolean allowReply = (viewType != ViewType.DRAFTS && viewType != ViewType.DELETED && viewType != ViewType.SENT);
            replyBtn.setVisible(allowReply); 
            forwardBtn.setVisible(viewType != ViewType.DELETED);
            
            replyBtn.addActionListener(evt -> parentPanel.getComposePanel().loadReply(e));
            forwardBtn.addActionListener(evt -> parentPanel.getComposePanel().loadForward(e));

            if(!e.isRead() && viewType != ViewType.DRAFTS) {
                e.setRead(true);
                parentPanel.getDataManager().updateEmail(e);
                parentPanel.refreshAllViews();
            }

            attachmentPanel.removeAll();
            if(e.getAttachmentPaths() != null && !e.getAttachmentPaths().isEmpty()) {
                attachmentPanel.setVisible(true);
                for(String path : e.getAttachmentPaths()) {
                    String nameOnly = path.contains("-") ? path.substring(path.indexOf("-")+1) : path;
                    JButton dlBtn = client.createStyledButton(nameOnly, Color.WHITE, Color.BLACK);
                    dlBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                    dlBtn.addActionListener(evt -> saveAttachment(path, nameOnly));
                    attachmentPanel.add(dlBtn);
                }
            } else {
                attachmentPanel.setVisible(false);
            }
            readingPanel.revalidate(); readingPanel.repaint();
        }

        public void refresh() {
            parentPanel.getDataManager().reloadData();
            tableModel.setRowCount(0);
            List<Email> filteredEmails = parentPanel.getDataManager().getEmails().stream().filter(e -> {
                String userEmail = parentPanel.getLoggedInUser().getEmailId();
                switch (viewType) {
                    case INBOX: return e.isRecipient(userEmail) && e.getStatus() == EmailStatus.INBOX;
                    case ARCHIVE: return (e.isRecipient(userEmail) || e.getFrom().equalsIgnoreCase(userEmail)) && e.getStatus() == EmailStatus.ARCHIVED;
                    case DELETED: return (e.isRecipient(userEmail) || e.getFrom().equalsIgnoreCase(userEmail)) && e.getStatus() == EmailStatus.DELETED;
                    case DRAFTS: return e.getFrom().equalsIgnoreCase(userEmail) && e.getStatus() == EmailStatus.DRAFT;
                    default: return false;
                }
            }).sorted((e1, e2) -> Long.compare(e2.getTimestamp(), e1.getTimestamp())).collect(Collectors.toList());

            for (Email email : filteredEmails) {
                String fromToText = email.getFrom();
                if (viewType == ViewType.SENT || viewType == ViewType.DRAFTS) fromToText = String.join(", ", email.getTo());
                tableModel.addRow(new Object[] { false, email.isRead() ? "" : "N", fromToText.split("@")[0], email.getSubject(), DATE_FMT.format(email.getTimestamp()), email });
            }
            updateControls();
            
            if(currentSelectedEmail == null) {
                readingPanel.removeAll(); readingPanel.add(emptyStatePanel); readingPanel.repaint();
            }
        }

        protected void updateControls() {
            boolean hasSel = getSelectedEmails().size() > 0;
            deleteButton.setVisible(hasSel);
            if(archiveButton != null) archiveButton.setVisible(hasSel);
            if(unarchiveButton != null) unarchiveButton.setVisible(hasSel);
        }
        
        protected void toggleAll(boolean sel) { for(int i=0; i<tableModel.getRowCount(); i++) tableModel.setValueAt(sel, i, 0); updateControls(); }
        
        protected List<Email> getSelectedEmails() {
            List<Email> list = new ArrayList<>();
            for(int i=0; i<tableModel.getRowCount(); i++) {
                if((Boolean)tableModel.getValueAt(i, 0)) list.add((Email)tableModel.getValueAt(i, 5));
            }
            return list;
        }

        protected void deleteSelected() {
            List<Email> list = getSelectedEmails();
            if(list.isEmpty()) return;
            boolean perm = viewType == ViewType.DELETED;
            String msg = perm ? "Delete forever?" : "Move to Trash?";
            if(JOptionPane.showConfirmDialog(this, msg, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                for(Email e : list) {
                    if(perm) { e.setStatus(EmailStatus.DELETED); parentPanel.getDataManager().updateEmail(e); /* Ideal: hard delete */ } 
                    else { e.setStatus(EmailStatus.DELETED); parentPanel.getDataManager().updateEmail(e); }
                }
                parentPanel.refreshAllViews();
            }
        }
        
        protected void moveSelected(EmailStatus status) {
            for(Email e : getSelectedEmails()) { e.setStatus(status); parentPanel.getDataManager().updateEmail(e); }
            parentPanel.refreshAllViews();
        }
        
        protected void unarchiveSelected() {
            for(Email e : getSelectedEmails()) {
                e.setStatus(e.getFrom().equals(parentPanel.getLoggedInUser().getEmailId()) ? EmailStatus.SENT : EmailStatus.INBOX);
                parentPanel.getDataManager().updateEmail(e);
            }
            parentPanel.refreshAllViews();
        }

        private void saveAttachment(String uniq, String orig) {
            File f = parentPanel.getDataManager().getAttachment(uniq);
            if(f==null) return;
            JFileChooser fc = new JFileChooser(); fc.setSelectedFile(new File(orig));
            if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try { Files.copy(f.toPath(), fc.getSelectedFile().toPath(), StandardCopyOption.REPLACE_EXISTING); JOptionPane.showMessageDialog(this, "Saved."); } 
                catch(Exception ex) { ex.printStackTrace(); }
            }
        }

        class CustomTableModel extends DefaultTableModel {
            public CustomTableModel(Object[] cols) { super(cols, 0); addColumn("Hidden"); }
            public Class<?> getColumnClass(int c) { return c == 0 ? Boolean.class : super.getColumnClass(c); }
            public boolean isCellEditable(int r, int c) { return c == 0; }
        }
    }

    public class SentListPanel extends MailListPanel {
        public SentListPanel(MailboxPanel p, EmailClient c) { super(p, c, ViewType.SENT); }
        @Override protected void deleteSelected() {
            List<Email> list = getSelectedEmails();
            long now = System.currentTimeMillis();
            boolean anyUnsend = list.stream().anyMatch(e -> now - e.getTimestamp() < UNSEND_TIMEOUT_MS);
            if(anyUnsend) {
                if(JOptionPane.showConfirmDialog(this, "Unsend recent emails?", "Unsend", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    for(Email e : list) { e.setStatus(EmailStatus.DELETED); parentPanel.getDataManager().updateEmail(e); }
                    parentPanel.refreshAllViews();
                    return;
                }
            }
            super.deleteSelected();
        }
        @Override public void refresh() {
            parentPanel.getDataManager().reloadData();
            tableModel.setRowCount(0);
            List<Email> list = parentPanel.getDataManager().getEmails().stream()
                .filter(e -> e.getFrom().equals(parentPanel.getLoggedInUser().getEmailId()) && e.getStatus() == EmailStatus.SENT)
                .collect(Collectors.toList());
            for(Email e : list) tableModel.addRow(new Object[]{false, "", "To: " + String.join(", ", e.getTo()), e.getSubject(), DATE_FMT.format(e.getTimestamp()), e});
        }
    }

    public class TrashPanel extends MailListPanel {
        private JButton recoverButton;
        public TrashPanel(MailboxPanel p, EmailClient c) { super(p, c, ViewType.DELETED); }
        @Override protected JPanel createControlsPanel() {
            JPanel p = super.createControlsPanel();
            recoverButton = client.createStyledButton("Recover", COLOR_PRIMARY, Color.WHITE);
            recoverButton.addActionListener(e -> recoverSelected());
            p.add(recoverButton, 1);
            return p;
        }
        @Override protected void updateControls() {
            super.updateControls();
            if(recoverButton != null) recoverButton.setVisible(getSelectedEmails().size() > 0);
        }
        private void recoverSelected() {
            for(Email e : getSelectedEmails()) {
                e.setStatus(e.getFrom().equals(parentPanel.getLoggedInUser().getEmailId()) ? EmailStatus.SENT : EmailStatus.INBOX);
                parentPanel.getDataManager().updateEmail(e);
            }
            parentPanel.refreshAllViews();
        }
    }

    public class ComposePanel extends JPanel {
        private JTextField subjectField; private JTextArea toField, bodyArea;
        private MailboxPanel parent; private EmailClient client;
        private Email currentDraft; public boolean isLoading = false;
        private JPanel attachmentList; private List<File> pendingAttachments = new ArrayList<>();

        public ComposePanel(MailboxPanel parent, EmailClient client) {
            this.parent = parent; this.client = client;
            setLayout(new GridBagLayout()); setBackground(COLOR_BG_MAIN);
            RoundedPanel card = new RoundedPanel(); card.setLayout(new BorderLayout()); card.setBorder(new EmptyBorder(30, 40, 30, 40));
            
            JPanel top = new JPanel(new GridBagLayout()); top.setOpaque(false);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; gbc.gridx = 0; gbc.insets = new Insets(0,0,15,0);

            JLabel l1 = new JLabel("To"); l1.setForeground(COLOR_TEXT_SECONDARY); gbc.gridy=0; top.add(l1, gbc);
            
            JPanel toContainer = new JPanel(new BorderLayout(5, 0)); toContainer.setOpaque(false);
            toField = new JTextArea(1, 40); toField.setLineWrap(true); toField.setWrapStyleWord(true); 
            toField.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.LIGHT_GRAY)); toField.setOpaque(false);
            JButton contactsBtn = new JButton("Contacts \u25BC"); contactsBtn.setBorderPainted(false); contactsBtn.setContentAreaFilled(false); contactsBtn.setForeground(COLOR_PRIMARY); contactsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            contactsBtn.addActionListener(e -> showContactsPopup(contactsBtn));
            toContainer.add(toField, BorderLayout.CENTER); toContainer.add(contactsBtn, BorderLayout.EAST);
            
            gbc.gridy=1; top.add(toContainer, gbc);

            JLabel l2 = new JLabel("Subject"); l2.setForeground(COLOR_TEXT_SECONDARY); gbc.gridy=2; top.add(l2, gbc);
            subjectField = new JTextField(); subjectField.setBorder(BorderFactory.createMatteBorder(0,0,1,0, Color.LIGHT_GRAY)); subjectField.setOpaque(false);
            gbc.gridy=3; top.add(subjectField, gbc);

            card.add(top, BorderLayout.NORTH);

            bodyArea = new JTextArea(); bodyArea.setFont(new Font("Arial", Font.PLAIN, 14));
            bodyArea.setLineWrap(true); bodyArea.setWrapStyleWord(true);
            JScrollPane scroll = new JScrollPane(bodyArea); scroll.setBorder(null); card.add(scroll, BorderLayout.CENTER);

            JPanel bottom = new JPanel(new BorderLayout()); bottom.setOpaque(false); bottom.setBorder(new EmptyBorder(15,0,0,0));
            attachmentList = new JPanel(new FlowLayout(FlowLayout.LEFT)); attachmentList.setOpaque(false); bottom.add(attachmentList, BorderLayout.NORTH);
            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT)); btns.setOpaque(false);
            JButton attachBtn = client.createStyledButton("Attach", new Color(240,240,240), Color.BLACK); attachBtn.addActionListener(e -> attachFile());
            JButton draftBtn = client.createStyledButton("Save Draft", new Color(240,240,240), Color.BLACK); draftBtn.addActionListener(e -> send(true));
            JButton discardBtn = client.createStyledButton("Discard", new Color(240,240,240), Color.BLACK); discardBtn.addActionListener(e -> { clearFields(); parent.showView("INBOX"); });
            JButton sendBtn = client.createStyledButton("Send", COLOR_PRIMARY, Color.WHITE); sendBtn.setPreferredSize(new Dimension(100, 40)); sendBtn.addActionListener(e -> send(false));
            btns.add(attachBtn); btns.add(draftBtn); btns.add(discardBtn); btns.add(sendBtn); bottom.add(btns, BorderLayout.SOUTH);
            card.add(bottom, BorderLayout.SOUTH);
            GridBagConstraints mainGbc = new GridBagConstraints(); mainGbc.fill = GridBagConstraints.BOTH; mainGbc.weightx = 1.0; mainGbc.weighty = 1.0; mainGbc.insets = new Insets(20, 20, 20, 20);
            add(card, mainGbc);
            
            toField.getDocument().addDocumentListener(new DocumentListener() { 
                public void insertUpdate(DocumentEvent e) {} public void removeUpdate(DocumentEvent e) {} public void changedUpdate(DocumentEvent e) {} 
            });
        }

        private void showContactsPopup(Component invoker) {
            JPopupMenu popup = new JPopupMenu(); popup.setPreferredSize(new Dimension(300, 200)); 
            JPanel contentPanel = new JPanel(); contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); contentPanel.setBackground(Color.WHITE);
            List<User> allUsers = parent.getDataManager().getUsers(); String currentUserEmail = parent.getLoggedInUser().getEmailId(); 
            Set<String> currentEmails = Arrays.stream(toField.getText().split(",")).map(String::trim).map(String::toLowerCase).collect(Collectors.toSet());
            
            for (User user : allUsers) { 
                if (user.getEmailId().equalsIgnoreCase(currentUserEmail)) continue; 
                String labelText = user.getName() + " <" + user.getEmailId() + ">"; 
                JCheckBox checkBox = new JCheckBox(labelText); checkBox.setBackground(Color.WHITE); checkBox.setFocusPainted(false); 
                if (currentEmails.contains(user.getEmailId().toLowerCase())) checkBox.setSelected(true); 
                
                checkBox.addActionListener(e -> { 
                    Set<String> emails = Arrays.stream(toField.getText().split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toSet());
                    if(checkBox.isSelected()) emails.add(user.getEmailId());
                    else emails.remove(user.getEmailId());
                    toField.setText(String.join(", ", emails));
                }); 
                contentPanel.add(checkBox); 
            }
            JScrollPane scrollPane = new JScrollPane(contentPanel); scrollPane.setBorder(null); popup.add(scrollPane); popup.show(invoker, 0, invoker.getHeight());
        }

        public void clearFields() { toField.setText(""); subjectField.setText(""); bodyArea.setText(""); pendingAttachments.clear(); refreshAttachments(); currentDraft = null; }
        public void loadReply(Email e) { isLoading=true; parent.showView("COMPOSE"); clearFields(); toField.setText(e.getFrom()); subjectField.setText("Re: "+e.getSubject()); bodyArea.setText("\n\nOn "+MailListPanel.DATE_FMT.format(e.getTimestamp())+" wrote:\n"+e.getBody()); }
        public void loadForward(Email e) { isLoading=true; parent.showView("COMPOSE"); clearFields(); subjectField.setText("Fwd: "+e.getSubject()); bodyArea.setText("\n\n--- Forwarded ---\n"+e.getBody()); }
        public void loadDraft(Email e) { isLoading=true; parent.showView("COMPOSE"); clearFields(); currentDraft = e; toField.setText(String.join(",", e.getTo())); subjectField.setText(e.getSubject()); bodyArea.setText(e.getBody()); }
        private void attachFile() { JFileChooser fc = new JFileChooser(); if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { pendingAttachments.add(fc.getSelectedFile()); refreshAttachments(); } }
        private void refreshAttachments() { attachmentList.removeAll(); for(File f : pendingAttachments) { JButton b = new JButton(f.getName() + " X"); b.addActionListener(e -> { pendingAttachments.remove(f); refreshAttachments(); }); attachmentList.add(b); } attachmentList.revalidate(); attachmentList.repaint(); }
        private void send(boolean isDraft) {
            String to = toField.getText(); List<String> rcpts = Arrays.asList(to.split(",")); String sub = subjectField.getText(); String body = bodyArea.getText();
            List<String> attPaths = new ArrayList<>(); for(File f : pendingAttachments) { String p = parent.getDataManager().saveAttachment(f); if(p!=null) attPaths.add(p); }
            if(currentDraft != null) { currentDraft.setStatus(EmailStatus.DELETED); parent.getDataManager().updateEmail(currentDraft); }
            String id = UUID.randomUUID().toString(); Email newEmail = new Email(id, parent.getLoggedInUser().getEmailId(), rcpts, sub, body, attPaths, isDraft ? EmailStatus.DRAFT : EmailStatus.SENT);
            if(isDraft) parent.getDataManager().addEmail(newEmail);
            else { Email inboxCopy = new Email(id, parent.getLoggedInUser().getEmailId(), rcpts, sub, body, attPaths, EmailStatus.INBOX); parent.getDataManager().sendEmailAtomic(newEmail, inboxCopy); }
            JOptionPane.showMessageDialog(this, isDraft ? "Draft Saved" : "Sent"); clearFields(); parent.showView(isDraft ? "DRAFTS" : "SENT");
        }
    }

    public class SettingsPanel extends JPanel {
        private final MailboxPanel parentPanel; private final EmailClient client; private final JTextField nameField, emailField; private final JPasswordField newPasswordField, confirmPasswordField;
        public SettingsPanel(MailboxPanel parentPanel, EmailClient client) {
            this.parentPanel = parentPanel; this.client = client; setLayout(new BorderLayout()); setBorder(new EmptyBorder(30, 50, 30, 50));
            JLabel title = new JLabel("Account Settings"); title.setFont(new Font("Arial", Font.BOLD, 30)); title.setForeground(COLOR_PRIMARY);
            JPanel formPanel = new JPanel(new GridBagLayout()); formPanel.setBackground(Color.WHITE); formPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)); formPanel.setPreferredSize(new Dimension(600, 550));
            GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(10, 20, 10, 20); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST;
            gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; formPanel.add(new JLabel("Display Name:"), gbc); gbc.gridx = 1; gbc.weightx = 1; nameField = new JTextField(25); formPanel.add(nameField, gbc);
            gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; formPanel.add(new JLabel("Email Address:"), gbc); gbc.gridx = 1; gbc.weightx = 1; emailField = new JTextField(25); emailField.setEditable(false); emailField.setBackground(new Color(240, 240, 240)); formPanel.add(emailField, gbc);
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.insets = new Insets(20, 0, 20, 0); formPanel.add(new JSeparator(), gbc); gbc.insets = new Insets(10, 20, 10, 20);
            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.weightx = 0; formPanel.add(new JLabel("New Password:"), gbc); gbc.gridx = 1; gbc.weightx = 1; newPasswordField = new JPasswordField(25); formPanel.add(newPasswordField, gbc);
            gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; formPanel.add(new JLabel("Confirm Password:"), gbc); gbc.gridx = 1; gbc.weightx = 1; confirmPasswordField = new JPasswordField(25); formPanel.add(confirmPasswordField, gbc);
            JButton saveButton = client.createStyledButton("Save Changes", COLOR_SUCCESS, Color.WHITE);
            gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST; gbc.insets = new Insets(30, 20, 10, 20); formPanel.add(saveButton, gbc);
            gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(20, 0, 20, 0); formPanel.add(new JSeparator(), gbc);
            JButton deleteAccountButton = client.createStyledButton("Delete Account", COLOR_DANGER, Color.WHITE);
            gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER; gbc.insets = new Insets(10, 20, 20, 20); formPanel.add(deleteAccountButton, gbc);
            JPanel centerContainer = new JPanel(new GridBagLayout()); centerContainer.add(formPanel);
            add(title, BorderLayout.NORTH); add(centerContainer, BorderLayout.CENTER);
            saveButton.addActionListener(e -> saveSettings()); deleteAccountButton.addActionListener(e -> deleteAccount());
        }
        public void loadUserSettings() { User user = parentPanel.getLoggedInUser(); nameField.setText(user.getName()); emailField.setText(user.getEmailId()); newPasswordField.setText(""); confirmPasswordField.setText(""); }
        private void saveSettings() {
            User user = parentPanel.getLoggedInUser(); String newName = nameField.getText().trim(); String newPass = new String(newPasswordField.getPassword()); String confirmPass = new String(confirmPasswordField.getPassword());
            boolean nameChanged = !user.getName().equals(newName); boolean passwordChanged = !newPass.isEmpty();
            if (newName.isEmpty()) { JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE); return; }
            if (passwordChanged) { if (!newPass.equals(confirmPass)) { JOptionPane.showMessageDialog(this, "Mismatch.", "Error", JOptionPane.ERROR_MESSAGE); return; } if (newPass.length() < 6) { JOptionPane.showMessageDialog(this, "Too short.", "Error", JOptionPane.ERROR_MESSAGE); return; } }
            if (nameChanged) user.setName(newName); if (passwordChanged) user.setPasswordHash(newPass);
            if (nameChanged || passwordChanged) { parentPanel.getDataManager().updateUser(user); JOptionPane.showMessageDialog(this, "Saved!", "Success", JOptionPane.INFORMATION_MESSAGE); parentPanel.showView("INBOX"); } else { JOptionPane.showMessageDialog(this, "No changes.", "Info", JOptionPane.INFORMATION_MESSAGE); }
        }
        private void deleteAccount() {
            JPasswordField pf = new JPasswordField(); int ok = JOptionPane.showConfirmDialog(this, new Object[]{"Enter password:", pf}, "Confirm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok == JOptionPane.OK_OPTION) {
                if (new String(pf.getPassword()).equals(parentPanel.getLoggedInUser().getPasswordHash())) {
                    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure? This deletes everything.", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                    if (confirm == JOptionPane.YES_OPTION) { parentPanel.getDataManager().deleteUser(parentPanel.getLoggedInUser()); JOptionPane.showMessageDialog(this, "Deleted.", "Bye", JOptionPane.INFORMATION_MESSAGE); client.logout(); }
                } else JOptionPane.showMessageDialog(this, "Incorrect.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}