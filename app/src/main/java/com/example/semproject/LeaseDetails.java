package com.example.semproject;

public class LeaseDetails {
    private int leaseId;
    private String userName;
    private String roomNumber;
    private double rentAmount;
    private String leaseStart;
    private String leaseEnd;

    public LeaseDetails(int leaseId, String userName, String roomNumber, double rentAmount, String leaseStart, String leaseEnd) {
        this.leaseId = leaseId;
        this.userName = userName;
        this.roomNumber = roomNumber;
        this.rentAmount = rentAmount;
        this.leaseStart = leaseStart;
        this.leaseEnd = leaseEnd;
    }

    // Getters
    public int getLeaseId() { return leaseId; }
    public String getUserName() { return userName; }
    public String getRoomNumber() { return roomNumber; }
    public double getRentAmount() { return rentAmount; }
    public String getLeaseStart() { return leaseStart; }
    public String getLeaseEnd() { return leaseEnd; }

    // Optional setters (uncomment if needed)
    // public void setLeaseId(int leaseId) { this.leaseId = leaseId; }
    // public void setUserName(String userName) { this.userName = userName; }
    // public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    // public void setRentAmount(double rentAmount) { this.rentAmount = rentAmount; }
    // public void setLeaseStart(String leaseStart) { this.leaseStart = leaseStart; }
    // public void setLeaseEnd(String leaseEnd) { this.leaseEnd = leaseEnd; }

    @Override
    public String toString() {
        return "LeaseDetails{" +
                "leaseId=" + leaseId +
                ", userName='" + userName + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                ", rentAmount=" + rentAmount +
                ", leaseStart='" + leaseStart + '\'' +
                ", leaseEnd='" + leaseEnd + '\'' +
                '}';
    }
}
