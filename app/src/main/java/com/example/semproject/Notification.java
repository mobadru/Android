package com.example.semproject;

public class Notification {
    private int id;
    private String type;
    private String message;
    private String timestamp;
    private boolean isRead;
    private int targetUserId;

    public Notification() {}

    public Notification(int id, String type, String message, String timestamp, boolean isRead, int targetUserId) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.timestamp = timestamp;
        this.isRead = isRead;
        this.targetUserId = targetUserId;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }
    public int getTargetUserId() { return targetUserId; }

    public void setId(int id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setMessage(String message) { this.message = message; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setRead(boolean read) { isRead = read; }
    public void setTargetUserId(int targetUserId) { this.targetUserId = targetUserId; }
}
