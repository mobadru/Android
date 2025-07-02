package com.example.semproject;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class PaymentsActivity extends AppCompatActivity {

    private LinearLayout containerPayments;
    private int tenantId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payments);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        containerPayments = findViewById(R.id.containerPayments);
        tenantId = getIntent().getIntExtra("tenantId", -1);

        if (tenantId == -1) {
            Toast.makeText(this, "Invalid tenant ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadPayments();
    }

    private void loadPayments() {
        DatabaseHelper db = new DatabaseHelper(this);
        List<Payment> paymentList = db.getPaymentsByUserId(tenantId);

        if (paymentList.isEmpty()) {
            Toast.makeText(this, "No payments found", Toast.LENGTH_LONG).show();
            return;
        }

        containerPayments.removeAllViews();

        for (Payment payment : paymentList) {
            View paymentCard = getLayoutInflater().inflate(R.layout.item_payment_card, containerPayments, false);

            TextView tvPaymentId = paymentCard.findViewById(R.id.tvPaymentId);
            TextView tvPaymentLeaseId = paymentCard.findViewById(R.id.tvPaymentLeaseId);
            TextView tvPaymentAmount = paymentCard.findViewById(R.id.tvPaymentAmount);
            TextView tvPaymentDate = paymentCard.findViewById(R.id.tvPaymentDate);

            tvPaymentId.setText("Payment ID: " + payment.getPaymentId());
            tvPaymentLeaseId.setText("Lease ID: " + payment.getLeaseId());
            tvPaymentAmount.setText(String.format("Amount: TZS %.2f", payment.getAmount()));
            tvPaymentDate.setText("Date: " + payment.getPaymentDate());
            TextView tvPaymentStatus = paymentCard.findViewById(R.id.tvPaymentStatus);
            tvPaymentStatus.setText("Status: " + payment.getStatus());



            containerPayments.addView(paymentCard);
        }
    }
}
