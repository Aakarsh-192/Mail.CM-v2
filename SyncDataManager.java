import java.io.File;
import java.util.List;

public class SyncDataManager implements IDataManager {
    
    private IDataManager localManager;

    public SyncDataManager() {
        try {
            this.localManager = new JDBCDataManager();
        } catch (Exception e) {
            System.err.println("JDBC Init failed, using FileDataManager: " + e.getMessage());
            this.localManager = new FileDataManager();
        }
    }

    @Override
    public void reloadData() {
        localManager.reloadData();
    }

    @Override
    public void saveAll() {
        localManager.saveAll();
    }

    @Override
    public List<User> getUsers() {
        return localManager.getUsers();
    }

    @Override
    public void addUser(User user) {
        localManager.addUser(user);
    }

    @Override
    public void updateUser(User user) {
        localManager.updateUser(user);
    }

    @Override
    public void deleteUser(User user) {
        localManager.deleteUser(user);
    }

    @Override
    public List<Email> getEmails() {
        return localManager.getEmails();
    }

    @Override
    public void addEmail(Email email) {
        localManager.addEmail(email);
    }

    @Override
    public void updateEmail(Email email) {
        localManager.updateEmail(email);
    }

    @Override
    public void sendEmailAtomic(Email senderCopy, Email recipientCopy) {
        localManager.sendEmailAtomic(senderCopy, recipientCopy);
    }

    @Override
    public String saveAttachment(File file) {
        return localManager.saveAttachment(file);
    }

    @Override
    public File getAttachment(String uniqueFilename) {
        return localManager.getAttachment(uniqueFilename);
    }
}