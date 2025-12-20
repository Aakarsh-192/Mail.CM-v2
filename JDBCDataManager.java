import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class JDBCDataManager implements IDataManager {
    
    private static final String DB_URL = "jdbc:sqlite:database/mail_sql_v2.db";
    private static final String ATTACHMENTS_DIR_PATH = "database/attachments/"; 

    public JDBCDataManager() {
        try {
            Files.createDirectories(Paths.get("database"));
            Files.createDirectories(Paths.get(ATTACHMENTS_DIR_PATH));
            Class.forName("org.sqlite.JDBC");
            createTables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
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

        try (Connection conn = connect();
            Statement stmt = conn.createStatement()) {
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
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(new User(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getLong("lastModified")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public List<Email> getEmails() { 
        List<Email> emails = new ArrayList<>();
        String sql = "SELECT * FROM emails ORDER BY timestamp DESC";

        try (Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String toStr = rs.getString("recipients");
                List<String> toList = (toStr == null || toStr.isEmpty()) ? new ArrayList<>() : Arrays.asList(toStr.split(","));
                
                String attachStr = rs.getString("attachments");
                List<String> attachList = (attachStr == null || attachStr.isEmpty()) ? new ArrayList<>() : Arrays.asList(attachStr.split(","));

                Email email = new Email(
                    rs.getString("messageId"),
                    rs.getString("sender"),
                    toList,
                    rs.getString("subject"),
                    rs.getString("body"),
                    attachList,
                    EmailStatus.valueOf(rs.getString("status"))
                );
                email.setDbId(rs.getInt("id"));
                
                email.setRead(rs.getInt("isRead") == 1);
                email.setLastUpdated(rs.getLong("lastUpdated"));
                emails.add(email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return emails;
    }

    @Override
    public void addUser(User user) {
        String sql = "INSERT OR REPLACE INTO users(name, email, password, lastModified) VALUES(?,?,?,?)";
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmailId());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setLong(4, user.getLastModified());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateUser(User user) { 
        addUser(user); 
    }

    @Override
    public void deleteUser(User user) {
        String sqlUser = "DELETE FROM users WHERE email = ?";
        String sqlEmails = "DELETE FROM emails WHERE sender = ?"; 
        
        Connection conn = null;
        try {
            conn = connect();
            conn.setAutoCommit(false); 

            try (PreparedStatement pstUser = conn.prepareStatement(sqlUser);
                PreparedStatement pstEmail = conn.prepareStatement(sqlEmails)) {
                
                pstUser.setString(1, user.getEmailId());
                pstUser.executeUpdate();

                pstEmail.setString(1, user.getEmailId());
                pstEmail.executeUpdate();
                
                conn.commit(); 
            } catch (SQLException e) {
                conn.rollback(); 
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(conn);
        }
    }

    @Override
    public void addEmail(Email email) {
        insertEmailInternal(null, email);
    }

    @Override
    public void updateEmail(Email email) {
        if (email.getDbId() != -1) {
            String sql = "UPDATE emails SET isRead=?, status=?, lastUpdated=? WHERE id=?";
            try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, email.isRead() ? 1 : 0);
                pstmt.setString(2, email.getStatus().toString());
                pstmt.setLong(3, System.currentTimeMillis());
                pstmt.setInt(4, email.getDbId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            String sql = "UPDATE emails SET isRead=?, status=?, lastUpdated=? WHERE messageId=? AND sender=?";
            try (Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, email.isRead() ? 1 : 0);
                pstmt.setString(2, email.getStatus().toString());
                pstmt.setLong(3, System.currentTimeMillis());
                pstmt.setString(4, email.getMessageId());
                pstmt.setString(5, email.getFrom());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendEmailAtomic(Email senderCopy, Email recipientCopy) {
        Connection conn = null;
        try {
            conn = connect();
            conn.setAutoCommit(false); 

            insertEmailOnConnection(conn, senderCopy);
            insertEmailOnConnection(conn, recipientCopy);
            
            conn.commit(); 
            
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch(SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
        } finally {
            closeQuietly(conn);
        }
    }

    private void insertEmailOnConnection(Connection conn, Email email) throws SQLException {
        String sql = "INSERT INTO emails(messageId, sender, recipients, subject, body, attachments, timestamp, isRead, status, lastUpdated) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email.getMessageId());
            pstmt.setString(2, email.getFrom());
            pstmt.setString(3, String.join(",", email.getTo()));
            pstmt.setString(4, email.getSubject());
            pstmt.setString(5, email.getBody());
            pstmt.setString(6, (email.getAttachmentPaths() == null) ? "" : String.join(",", email.getAttachmentPaths()));
            pstmt.setLong(7, email.getTimestamp());
            pstmt.setInt(8, email.isRead() ? 1 : 0);
            pstmt.setString(9, email.getStatus().toString());
            pstmt.setLong(10, email.getLastUpdated());
            pstmt.executeUpdate();
        }
    }

    private void insertEmailInternal(Connection externalConn, Email email) {
        try {
            Connection conn = (externalConn != null) ? externalConn : connect();
            insertEmailOnConnection(conn, email);
            if (externalConn == null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String saveAttachment(File file) {
        if (file == null || !file.exists()) return null;
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
    
    private void closeQuietly(Connection conn) {
        try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}