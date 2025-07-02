package com.example.semproject;

public class Payment {
    private int paymentId;
    private int userId;
    private int leaseId;
    private double amount;
    private String paymentDate;
    private String status;

    // Updated constructor with status parameter
    public Payment(int paymentId, int userId, int leaseId, double amount, String paymentDate, String status) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.leaseId = leaseId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.status = status;  // Properly assigned here
    }

    public int getPaymentId() { return paymentId; }
    public int getUserId() { return userId; }
    public int getLeaseId() { return leaseId; }
    public double getAmount() { return amount; }
    public String getPaymentDate() { return paymentDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
