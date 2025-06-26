package com.example.semproject;

public class Payment {
    private int paymentId;
    private int userId;
    private int leaseId;
    private double amount;
    private String paymentDate;

    // Constructor name must match the class name: Payment
    public Payment(int paymentId, int userId, int leaseId, double amount, String paymentDate) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.leaseId = leaseId;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    public int getPaymentId() { return paymentId; }
    public int getUserId() { return userId; }
    public int getLeaseId() { return leaseId; }
    public double getAmount() { return amount; }
    public String getPaymentDate() { return paymentDate; }
}
