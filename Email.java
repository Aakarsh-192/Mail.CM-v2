import java.io.Serializable;
import java.util.List;

public class Email implements Serializable {
    private static final long serialVersionUID = 1L;

    private int dbId = -1; 

    private String messageId;
    private String from;
    private List<String> to;
    private String subject;
    private String body;
    private List<String> attachmentPaths;
    private long timestamp;
    private boolean isRead;
    private EmailStatus status;
    private long lastUpdated;

    public Email(String messageId, String from, List<String> to, String subject, String body, List<String> attachmentPaths, EmailStatus status) {
        this.messageId = messageId;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.attachmentPaths = attachmentPaths;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
        this.isRead = false;
        this.lastUpdated = System.currentTimeMillis();
    }

    public boolean isRecipient(String emailAddress) {
        if (this.to == null || emailAddress == null) return false;
        for (String recipient : this.to) {
            if (recipient.trim().equalsIgnoreCase(emailAddress.trim())) {
                return true;
            }
        }
        return false;
    }

    public int getDbId() { return dbId; }
    public void setDbId(int dbId) { this.dbId = dbId; }

    public String getMessageId() { return messageId; }
    public String getFrom() { return from; }
    
    public List<String> getTo() { return to; }
    public void setTo(List<String> to) { this.to = to; }
    
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public List<String> getAttachmentPaths() { return attachmentPaths; }
    public long getTimestamp() { return timestamp; }
    
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    
    public EmailStatus getStatus() { return status; }
    public void setStatus(EmailStatus status) { this.status = status; }
    
    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
    
    @Override
    public String toString() {
        return subject; 
    }
}