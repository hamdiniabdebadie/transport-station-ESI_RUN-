package transport.core;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ActivityLog implements Serializable {
    private LocalDateTime timestamp;
    private String category;
    private String action;
    private String details;
    
    public ActivityLog(String category, String action, String details) {
        this.timestamp = LocalDateTime.now();
        this.category = category;
        this.action = action;
        this.details = details;
    }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getCategory() { return category; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
}
