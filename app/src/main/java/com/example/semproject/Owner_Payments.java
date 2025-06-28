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
        View view = LayoutInflater.from(this).inflate(R.layout.owner_dialog_update_payment, null);
        builder.setView(view);

        EditText edtAmount = view.findViewById(R.id.edtUpdateAmount);
        EditText edtDate = view.findViewById(R.id.edtUpdateDate);

        edtAmount.setText(String.valueOf(payment.getAmount()));
        edtDate.setText(payment.getPaymentDate());

        builder.setTitle("Update Payment");
        builder.setPositiveButton("Update", (dialog, which) -> {
            double newAmount = Double.parseDouble(edtAmount.getText().toString());
            String newDate = edtDate.getText().toString();

            boolean updated = dbHelper.updatePayment(payment.getPaymentId(), newAmount, newDate);
            if (updated) {
                Toast.makeText(this, "Payment updated!", Toast.LENGTH_SHORT).show();
                loadPayments();
            } else {
                Toast.makeText(this, "Failed to update.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
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
