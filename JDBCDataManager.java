import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.List;
import java.util.UUID;

public class JDBCDataManager implements IDataManager {

    private static final String DB_URL = "jdbc:sqlite:database/mail_sql_v2.db";
    private static final String ATTACHMENTS_DIR_PATH = "database/attachments/";

    private UserDAO userDAO;
    private EmailDAO emailDAO;
    private Connection connection;

    public JDBCDataManager() {
        try {
            Files.createDirectories(Paths.get("database"));
            Files.createDirectories(Paths.get(ATTACHMENTS_DIR_PATH));
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection(DB_URL);
            createTables();

            userDAO = new UserDAOImpl(connection);
            emailDAO = new EmailDAOImpl(connection);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        String sqlUsers = "CREATE TABLE IF NOT EXISTS users ("
                + " name text NOT NULL,"
                + " email text PRIMARY KEY,"
                + " password text NOT NULL,"
                + " lastModified integer DEFAULT 0"
                + ");";

        String sqlEmails = "CREATE TABLE IF NOT EXISTS emails ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " messageId text,"
                + " sender text NOT NULL,"
                + " recipients text NOT NULL,"
                + " subject text,"
                + " body text,"
                + " attachments text,"
                + " timestamp integer,"
                + " lastUpdated integer DEFAULT 0,"
                + " isRead integer,"
                + " status text"
                + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlUsers);
            stmt.execute(sqlEmails);
        } catch (SQLException e) {
            System.err.println("Table Creation Error: " + e.getMessage());
        }
    }

    @Override
    public void reloadData() {
    }

    @Override
    public void saveAll() {
    }

    @Override
    public List<User> getUsers() {
        return userDAO.findAll();
    }

    @Override
    public List<Email> getEmails() {
        return emailDAO.findAll();
    }

    @Override
    public void addUser(User user) {
        userDAO.insert(user);
    }

    @Override
    public void updateUser(User user) {
        userDAO.update(user);
    }

    @Override
    public void deleteUser(User user) {
        String sqlEmails = "DELETE FROM emails WHERE sender = ?";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement pstEmail = connection.prepareStatement(sqlEmails)) {
                pstEmail.setString(1, user.getEmailId());
                pstEmail.executeUpdate();

                userDAO.delete(user);

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addEmail(Email email) {
        emailDAO.insert(email);
    }

    @Override
    public void updateEmail(Email email) {
        emailDAO.update(email);
    }

    @Override
    public void sendEmailAtomic(Email senderCopy, Email recipientCopy) {
        emailDAO.insertAtomic(senderCopy, recipientCopy);
    }

    @Override
    public String saveAttachment(File file) {
        if (file == null || !file.exists())
            return null;
        try {
            String uniqueFilename = UUID.randomUUID().toString() + "-" + file.getName();
            File destFile = new File(ATTACHMENTS_DIR_PATH + uniqueFilename);
            Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return uniqueFilename;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public File getAttachment(String uniqueFilename) {
        File file = new File(ATTACHMENTS_DIR_PATH + uniqueFilename);
        return (file.exists() && file.isFile()) ? file : null;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}