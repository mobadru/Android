package com.example.semproject;

public class LeaseDetails {
    private int leaseId;
    private String userName;
    private String roomNumber;
    private double rentAmount;
    private String leaseStart;
    private String leaseEnd;
    private int roomId;
    private String status;

    // Constructor with all fields
    public LeaseDetails(int leaseId, String userName, String roomNumber, double rentAmount,
                        String leaseStart, String leaseEnd, int roomId, String status) {
        this.leaseId = leaseId;
        this.userName = userName;
        this.roomNumber = roomNumber;
        this.rentAmount = rentAmount;
        this.leaseStart = leaseStart;
        this.leaseEnd = leaseEnd;
        this.roomId = roomId;
        this.status = status;
    }

    // Constructor without status
    public LeaseDetails(int leaseId, String userName, String roomNumber, double rentAmount,
                        String leaseStart, String leaseEnd, int roomId) {
        this.leaseId = leaseId;
        this.userName = userName;
        this.roomNumber = roomNumber;
        this.rentAmount = rentAmount;
        this.leaseStart = leaseStart;
        this.leaseEnd = leaseEnd;
        this.roomId = roomId;
        this.status = "Pending"; // or null, or any default
    }

    // Getters
    public int getLeaseId() {
        return leaseId;
    }

    public String getUserName() {
        return userName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public double getRentAmount() {
        return rentAmount;
    }

    public String getLeaseStart() {
        return leaseStart;
    }

    public String getLeaseEnd() {
        return leaseEnd;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getStatus() {
        return status;
    }

    // Setters
    public void setLeaseId(int leaseId) {
        this.leaseId = leaseId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setRentAmount(double rentAmount) {
        this.rentAmount = rentAmount;
    }

    public void setLeaseStart(String leaseStart) {
        this.leaseStart = leaseStart;
    }

    public void setLeaseEnd(String leaseEnd) {
        this.leaseEnd = leaseEnd;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
