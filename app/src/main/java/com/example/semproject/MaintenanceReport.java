package com.example.semproject;

public class MaintenanceReport {
    private int id;
    private int tenantId;
    private int roomId;
    private String description;
    private String status;
    private String dateReported;

    // Optional display fields (for admin UI)
    private String roomNumber;

    public MaintenanceReport(int id, int tenantId, int roomId,
                             String description, String status, String dateReported) {
        this.id = id;
        this.tenantId = tenantId;
        this.roomId = roomId;
        this.description = description;
        this.status = status;
        this.dateReported = dateReported;
    }

    // Overloaded constructor for admin display
    public MaintenanceReport(int id, String tenantName, String roomNumber, String description, String status, String date) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.dateReported = date;
        this.roomNumber = roomNumber;
        // tenantName is not stored here but could be added as another field if needed
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getTenantId() {
        return tenantId;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getDateReported() {
        return dateReported;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDateReported(String dateReported) {
        this.dateReported = dateReported;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
}
