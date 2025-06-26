package com.example.semproject;

/**
 * Model class representing a maintenance report submitted by a tenant.
 */
public class MaintenanceReport {
    private int id;
    private String tenantName;
    private String roomNumber;
    private String description;
    private String status;
    private String dateReported;

    // Constructor
    public MaintenanceReport(int id, String tenantName, String roomNumber,
                             String description, String status, String dateReported) {
        this.id = id;
        this.tenantName = tenantName;
        this.roomNumber = roomNumber;
        this.description = description;
        this.status = status;
        this.dateReported = dateReported;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTenantName() {
        return tenantName;
    }

    public String getRoomNumber() {
        return roomNumber;
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

    // Setters (optional, if needed)
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
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
}
