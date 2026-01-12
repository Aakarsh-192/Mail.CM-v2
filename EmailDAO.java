import java.sql.Connection;
import java.util.List;

public interface EmailDAO {
    List<Email> findAll();

    Email findById(String emailId);

    List<Email> findByRecipient(String email);

    List<Email> findBySender(String email);

    void insert(Email email);

    void insertWithConnection(Connection conn, Email email);

    void update(Email email);

    void delete(Email email);

    void insertAtomic(Email senderCopy, Email recipientCopy);
}
