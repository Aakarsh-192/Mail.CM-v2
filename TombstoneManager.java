import java.io.*;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

public class TombstoneManager {
    private Set<String> deletedUserEmails;
    private Set<String> deletedEmailIds;
    
    private static final String TOMBSTONE_DIR = "database/sync_meta";
    private static final String USERS_TOMBSTONE_FILE = TOMBSTONE_DIR + "/deleted_users.log";
    private static final String EMAILS_TOMBSTONE_FILE = TOMBSTONE_DIR + "/deleted_emails.log";

    public TombstoneManager() {
        deletedUserEmails = new HashSet<>();
        deletedEmailIds = new HashSet<>();
        
        try {
            Files.createDirectories(Paths.get(TOMBSTONE_DIR));
            loadTombstones();
        } catch (IOException e) {
            System.err.println("Error initializing TombstoneManager: " + e.getMessage());
        }
    }

    private void loadTombstones() {
        loadSetFromFile(USERS_TOMBSTONE_FILE, deletedUserEmails);
        loadSetFromFile(EMAILS_TOMBSTONE_FILE, deletedEmailIds);
    }

    private void loadSetFromFile(String path, Set<String> set) {
        File file = new File(path);
        if (!file.exists()) return;
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    set.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading tombstone file " + path + ": " + e.getMessage());
        }
    }

    private void appendToFile(String path, String id) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, true))) {
            bw.write(id);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to tombstone file " + path + ": " + e.getMessage());
        }
    }

    public void markUserDeleted(String email) {
        if (!deletedUserEmails.contains(email)) {
            deletedUserEmails.add(email);
            appendToFile(USERS_TOMBSTONE_FILE, email);
        }
    }

    public void markEmailDeleted(String messageId) {
        if (!deletedEmailIds.contains(messageId)) {
            deletedEmailIds.add(messageId);
            appendToFile(EMAILS_TOMBSTONE_FILE, messageId);
        }
    }

    public boolean isUserDeleted(String email) {
        return deletedUserEmails.contains(email);
    }

    public boolean isEmailDeleted(String messageId) {
        return deletedEmailIds.contains(messageId);
    }
}