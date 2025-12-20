import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileDataManager implements IDataManager {
    private List<User> users = new ArrayList<>();
    private List<Email> emails = new ArrayList<>();

    public FileDataManager() {
        reloadData();
    }

    @Override
    public void reloadData() {
    }

    @Override
    public void saveAll() {
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    @Override
    public void addUser(User user) {
        users.removeIf(u -> u.getEmailId().equalsIgnoreCase(user.getEmailId()));
        users.add(user);
        saveAll();
    }

    @Override
    public void updateUser(User user) {
        addUser(user);
    }

    @Override
    public void deleteUser(User user) {
        users.removeIf(u -> u.getEmailId().equalsIgnoreCase(user.getEmailId()));
        emails.removeIf(e -> e.getFrom().equalsIgnoreCase(user.getEmailId()));
        saveAll();
    }

    @Override
    public List<Email> getEmails() {
        return emails;
    }

    @Override
    public void addEmail(Email email) {
        emails.add(email);
        saveAll();
    }

    @Override
    public void updateEmail(Email email) {
        saveAll();
    }
    @Override
    public void sendEmailAtomic(Email senderCopy, Email recipientCopy) {
        addEmail(senderCopy);
        addEmail(recipientCopy);
    }

    @Override
    public String saveAttachment(File file) {
        return null;
    }

    @Override
    public File getAttachment(String uniqueFilename) {
        return null;
    }
}