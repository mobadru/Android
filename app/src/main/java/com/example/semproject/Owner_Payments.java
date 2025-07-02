package com.example.semproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class Owner_Payments extends AppCompatActivity {

    private LinearLayout containerPayments;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_payments);

        containerPayments = findViewById(R.id.containerPayments);
        dbHelper = new DatabaseHelper(this);

        loadPayments();
    }

    private void loadPayments() {
        containerPayments.removeAllViews();
        List<Payment> payments = dbHelper.getAllPayments();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Payment payment : payments) {
            View paymentView = inflater.inflate(R.layout.owner_payments_item, containerPayments, false);

            TextView tvPaymentId = paymentView.findViewById(R.id.tvPaymentId);
            TextView tvPaymentUser = paymentView.findViewById(R.id.tvPaymentUser);
            TextView tvPaymentAmount = paymentView.findViewById(R.id.tvPaymentAmount);
            TextView tvPaymentDate = paymentView.findViewById(R.id.tvPaymentDate);
            Button btnUpdate = paymentView.findViewById(R.id.btnUpdatePayment);
            Button btnDelete = paymentView.findViewById(R.id.btnDeletePayment);

            tvPaymentId.setText("Payment ID: #" + payment.getPaymentId());
            tvPaymentUser.setText("User ID: " + payment.getUserId()); // Or fetch username if needed
            tvPaymentAmount.setText("Amount: TZS " + payment.getAmount());
            tvPaymentDate.setText("Date: " + payment.getPaymentDate());

            btnUpdate.setOnClickListener(v -> showUpdateDialog(payment));
            btnDelete.setOnClickListener(v -> confirmDelete(payment));

            containerPayments.addView(paymentView);
        }
    }

    private void showUpdateDialog(Payment payment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Payment Status");

        // Create a Spinner programmatically
        Spinner spinner = new Spinner(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Pending", "Paid"}
        );
        spinner.setAdapter(adapter);

        // Set current status if available (assuming status is part of the Payment model)
        // If you want to store status, modify your Payment model and DB
        // For now, just default to "Pending"
        spinner.setSelection(0); // Default to Pending

        builder.setView(spinner);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newStatus = spinner.getSelectedItem().toString();

            // Call your update status method (you need to store this status in DB)
            boolean updated = dbHelper.updatePaymentStatus(payment.getPaymentId(), newStatus);
            if (updated) {
                Toast.makeText(this, "Status updated!", Toast.LENGTH_SHORT).show();
                loadPayments();
            } else {
                Toast.makeText(this, "Update failed.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void confirmDelete(Payment payment) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Payment")
                .setMessage("Are you sure you want to delete this payment?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean deleted = dbHelper.deletePayment(payment.getPaymentId());
                    if (deleted) {
                        Toast.makeText(this, "Payment deleted", Toast.LENGTH_SHORT).show();
                        loadPayments();
                    } else {
                        Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
