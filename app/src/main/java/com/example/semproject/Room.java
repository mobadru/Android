package com.example.semproject;

public class Room {
    private int id;
    private String roomNumber;
    private String description;
    private String type;
    private double rentAmount;
    private String status;

    public Room(int id, String roomNumber, String description, String type, double rentAmount, String status) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.description = description;
        this.type = type;
        this.rentAmount = rentAmount;
        this.status = status;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getRoomNumber() { return roomNumber; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public double getRentAmount() { return rentAmount; }
    public String getStatus() { return status; }

    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setDescription(String description) { this.description = description; }
    public void setType(String type) { this.type = type; }
    public void setRentAmount(double rentAmount) { this.rentAmount = rentAmount; }
    public void setStatus(String status) { this.status = status; }
}
