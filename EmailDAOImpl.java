import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmailDAOImpl implements EmailDAO {
    private Connection connection;

    public EmailDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Email> findAll() {
        List<Email> emails = new ArrayList<>();
        String sql = "SELECT * FROM emails ORDER BY timestamp DESC";

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                emails.add(extractEmailFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return emails;
    }

    @Override
    public Email findById(String emailId) {
        String sql = "SELECT * FROM emails WHERE messageId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, emailId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractEmailFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Email> findByRecipient(String email) {
        List<Email> emails = new ArrayList<>();
        String sql = "SELECT * FROM emails WHERE recipients LIKE ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + email + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                emails.add(extractEmailFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return emails;
    }

    @Override
    public List<Email> findBySender(String email) {
        List<Email> emails = new ArrayList<>();
        String sql = "SELECT * FROM emails WHERE sender = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                emails.add(extractEmailFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return emails;
    }

    @Override
    public void insert(Email email) {
        insertWithConnection(connection, email);
    }

    @Override
    public void insertWithConnection(Connection conn, Email email) {
        String sql = "INSERT INTO emails(messageId, sender, recipients, subject, body, attachments, timestamp, isRead, status, lastUpdated) VALUES(?,?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email.getMessageId());
            pstmt.setString(2, email.getFrom());
            pstmt.setString(3, String.join(",", email.getTo()));
            pstmt.setString(4, email.getSubject());
            pstmt.setString(5, email.getBody());

            String attachmentPathsStr = (email.getAttachmentPaths() == null) ? ""
                    : String.join(",", email.getAttachmentPaths());
            pstmt.setString(6, attachmentPathsStr);

            pstmt.setLong(7, email.getTimestamp());
            pstmt.setInt(8, email.isRead() ? 1 : 0);
            pstmt.setString(9, email.getStatus().toString());
            pstmt.setLong(10, email.getLastUpdated());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Email email) {
        if (email.getDbId() != -1) {
            String sql = "UPDATE emails SET isRead=?, status=?, lastUpdated=? WHERE id=?";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
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
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
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
    public void delete(Email email) {
        String sql = "DELETE FROM emails WHERE messageId = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email.getMessageId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertAtomic(Email senderCopy, Email recipientCopy) {
        try {
            connection.setAutoCommit(false);

            insertWithConnection(connection, senderCopy);
            insertWithConnection(connection, recipientCopy);

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private Email extractEmailFromResultSet(ResultSet rs) throws SQLException {
        String toStr = rs.getString("recipients");
        List<String> toList = (toStr == null || toStr.isEmpty()) ? new ArrayList<>() : Arrays.asList(toStr.split(","));

        String attachStr = rs.getString("attachments");
        List<String> attachList = (attachStr == null || attachStr.isEmpty()) ? new ArrayList<>()
                : Arrays.asList(attachStr.split(","));

        Email email = new Email(
                rs.getString("messageId"),
                rs.getString("sender"),
                toList,
                rs.getString("subject"),
                rs.getString("body"),
                attachList,
                EmailStatus.valueOf(rs.getString("status")));
        email.setDbId(rs.getInt("id"));
        email.setRead(rs.getInt("isRead") == 1);
        email.setLastUpdated(rs.getLong("lastUpdated"));

        return email;
    }
}
