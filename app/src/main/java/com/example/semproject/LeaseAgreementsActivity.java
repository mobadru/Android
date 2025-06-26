package com.example.semproject;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LeaseAgreementsActivity extends AppCompatActivity {

    private LinearLayout containerLeasedRooms;
    private int tenantId = -1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lease_agreements);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        containerLeasedRooms = findViewById(R.id.containerLeasedRooms);
        tenantId = getIntent().getIntExtra("tenantId", -1);

        if (tenantId == -1) {
            Toast.makeText(this, "Invalid tenant ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadLeasedRooms();
    }

    private void loadLeasedRooms() {
        DatabaseHelper db = new DatabaseHelper(this);
        List<LeaseDetails> leaseList = db.getLeaseDetailsByUserId(tenantId);

        if (leaseList.isEmpty()) {
            Toast.makeText(this, "No leased rooms found", Toast.LENGTH_LONG).show();
            return;
        }

        containerLeasedRooms.removeAllViews();

        for (LeaseDetails lease : leaseList) {
            View roomCard = getLayoutInflater().inflate(R.layout.item_lease_card, containerLeasedRooms, false);

            TextView tvRoomName = roomCard.findViewById(R.id.tvRoomName);
            TextView tvPrice = roomCard.findViewById(R.id.tvPrice);
            TextView tvStartDate = roomCard.findViewById(R.id.tvStartDate);
            TextView tvEndDate = roomCard.findViewById(R.id.tvEndDate);
            TextView tvDuration = roomCard.findViewById(R.id.tvDuration);
            TextView tvTotalCost = roomCard.findViewById(R.id.tvTotalCost);
            Button btnUpdate = roomCard.findViewById(R.id.btnUpdateLease);
            Button btnMakePayment = roomCard.findViewById(R.id.btnMakePayment);

            // Set read-only text
            tvRoomName.setText(lease.getRoomNumber());
            tvPrice.setText(String.format(Locale.US, "$%.2f", lease.getRentAmount()));
            tvStartDate.setText(lease.getLeaseStart());
            tvEndDate.setText(lease.getLeaseEnd());
            tvStartDate.setFocusable(false);
            tvStartDate.setClickable(false);
            tvEndDate.setFocusable(false);
            tvEndDate.setClickable(false);

            // Duration and Total cost
            int months = calculateMonthsBetween(lease.getLeaseStart(), lease.getLeaseEnd());
            tvDuration.setText(String.valueOf(months));
            double totalCost = months * lease.getRentAmount();
            tvTotalCost.setText(String.format(Locale.US, "$%.2f", totalCost));

            btnUpdate.setOnClickListener(v -> {
                // Optional: You can re-enable date picking here if you want to allow changes during update
                tvStartDate.setClickable(true);
                tvEndDate.setClickable(true);

                tvStartDate.setOnClickListener(v2 -> showDatePicker(tvStartDate));
                tvEndDate.setOnClickListener(v2 -> showDatePicker(tvEndDate));

                String newStart = tvStartDate.getText().toString();
                String newEnd = tvEndDate.getText().toString();

                if (isDateValid(newStart, newEnd)) {
                    boolean success = db.updateLeaseDates(lease.getLeaseId(), newStart, newEnd);
                    if (success) {
                        Toast.makeText(this, "Lease updated!", Toast.LENGTH_SHORT).show();
                        int newMonths = calculateMonthsBetween(newStart, newEnd);
                        tvDuration.setText(String.valueOf(newMonths));
                        double newTotalCost = newMonths * lease.getRentAmount();
                        tvTotalCost.setText(String.format(Locale.US, "$%.2f", newTotalCost));
                    } else {
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Invalid dates", Toast.LENGTH_SHORT).show();
                }

                // Disable date pickers again after update
                tvStartDate.setClickable(false);
                tvEndDate.setClickable(false);
            });

            btnMakePayment.setOnClickListener(v -> {
                String totalCostStr = tvTotalCost.getText().toString();

                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Confirm Payment")
                        .setMessage("Pay " + totalCostStr + " for room " + lease.getRoomNumber() + "?")
                        .setPositiveButton("Pay Now", (dialog, which) -> {
                            boolean paymentSuccess = db.insertPayment(
                                    tenantId,
                                    lease.getLeaseId(),
                                    totalCostStr,
                                    dateFormat.format(Calendar.getInstance().getTime())
                            );

                            if (paymentSuccess) {
                                Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Payment failed. Try again.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });

            containerLeasedRooms.addView(roomCard);
        }
    }

    private void showDatePicker(TextView tvStartDate) {
    }


    private boolean isDateValid(String start, String end) {
        return false;
    }

    private int calculateMonthsBetween(String start, String end) {
        try {
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(dateFormat.parse(start));
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(dateFormat.parse(end));

            int yearDiff = endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR);
            int monthDiff = endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH);
            int totalMonths = yearDiff * 12 + monthDiff;

            if (endCal.get(Calendar.DAY_OF_MONTH) >= startCal.get(Calendar.DAY_OF_MONTH)) {
                totalMonths += 1;
            }

            return Math.max(totalMonths, 1);
        } catch (Exception e) {
            return 0;
        }
    }
}
