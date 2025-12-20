import java.io.Serializable;
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    String name;
    String emailId;
    String passwordHash;
    long lastModified;

    public User(String name, String emailId, String passwordHash) {
        this.name = name;
        this.emailId = emailId;
        this.passwordHash = passwordHash;
        this.lastModified = System.currentTimeMillis();
    }

    public User(String name, String emailId, String passwordHash, long lastModified) {
        this.name = name;
        this.emailId = emailId;
        this.passwordHash = passwordHash;
        this.lastModified = lastModified;
    }

    public String getEmailId() { return emailId; }
    public String getPasswordHash() { return passwordHash; }
    public String getName() { return name; }
    public long getLastModified() { return lastModified; }

    public void setName(String name) { 
        this.name = name; 
        this.lastModified = System.currentTimeMillis();
    }
    
    public void setPasswordHash(String passwordHash) { 
        this.passwordHash = passwordHash; 
        this.lastModified = System.currentTimeMillis();
    }
    
    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
}