package com.example.semproject;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
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

        // Adjust padding for system bars (Edge-to-edge)
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
            TextView tvLeaseStatus = roomCard.findViewById(R.id.tvLeaseStatus);
            Button btnUpdate = roomCard.findViewById(R.id.btnUpdateLease);
            Button btnMakePayment = roomCard.findViewById(R.id.btnMakePayment);

            tvRoomName.setText(lease.getRoomNumber());
            tvPrice.setText(String.format(Locale.US, "TZS : %.2f", lease.getRentAmount()));
            tvStartDate.setText(lease.getLeaseStart());
            tvEndDate.setText(lease.getLeaseEnd());

            int months = calculateMonthsBetween(lease.getLeaseStart(), lease.getLeaseEnd());
            tvDuration.setText(String.valueOf(months));

            double totalCost = months * lease.getRentAmount();
            tvTotalCost.setText(String.format(Locale.US, "TZS : %.2f", totalCost));

            String status = lease.getStatus();
            tvLeaseStatus.setText("Status: " + status);

            // Enable/disable buttons based on status
            switch (status.toLowerCase(Locale.ROOT)) {
                case "pending":
                    btnMakePayment.setEnabled(false);
                    btnUpdate.setEnabled(true);
                    break;
                case "active":
                    btnMakePayment.setEnabled(true);
                    btnUpdate.setEnabled(false);
                    break;
                case "terminated":// handle any spelling variants if needed
                    btnMakePayment.setEnabled(false);
                    btnUpdate.setEnabled(false);
                    break;
                default:
                    // Default behavior if status is unknown
                    btnMakePayment.setEnabled(false);
                    btnUpdate.setEnabled(false);
                    break;
            }

            btnUpdate.setOnClickListener(v -> showUpdateDialog(lease, db));

            btnMakePayment.setOnClickListener(v -> {
                String totalCostStr = tvTotalCost.getText().toString();
                String numericPart = totalCostStr.replaceAll("[^\\d.]+", "");
                double totalCostDouble;

                try {
                    totalCostDouble = Double.parseDouble(numericPart);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Invalid payment amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Confirm Payment")
                        .setMessage("Pay " + totalCostStr + " for room " + lease.getRoomNumber() + "?")
                        .setPositiveButton("Pay Now", (dialog, which) -> {
                            boolean paymentSuccess = db.insertPayment(
                                    tenantId,
                                    lease.getLeaseId(),
                                    totalCostDouble,
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


    private void showUpdateDialog(LeaseDetails lease, DatabaseHelper db) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_update_lease, null);
        TextView tvDialogStartDate = dialogView.findViewById(R.id.tvDialogStartDate);
        TextView tvDialogEndDate = dialogView.findViewById(R.id.tvDialogEndDate);
        TextView tvDialogTotalPrice = dialogView.findViewById(R.id.tvDialogTotalPrice);

        tvDialogStartDate.setText(lease.getLeaseStart());
        tvDialogEndDate.setText(lease.getLeaseEnd());

        final String[] startDate = {lease.getLeaseStart()};
        final String[] endDate = {lease.getLeaseEnd()};
        final String[] status = {lease.getStatus()};

        tvDialogStartDate.setOnClickListener(v -> showDatePickerDialog(date -> {
            startDate[0] = date;
            tvDialogStartDate.setText(date);
            updateTotal(tvDialogTotalPrice, startDate[0], endDate[0], lease.getRentAmount());
        }));

        tvDialogEndDate.setOnClickListener(v -> showDatePickerDialog(date -> {
            endDate[0] = date;
            tvDialogEndDate.setText(date);
            updateTotal(tvDialogTotalPrice, startDate[0], endDate[0], lease.getRentAmount());
        }));

        updateTotal(tvDialogTotalPrice, startDate[0], endDate[0], lease.getRentAmount());

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Update Lease Dates")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    if (!isDateValid(startDate[0], endDate[0])) {
                        Toast.makeText(this, "Invalid date range", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean updated = db.updateLeaseDates(lease.getLeaseId(), startDate[0], endDate[0], status[0]);

                    if (updated) {
                        Toast.makeText(this, "Lease updated successfully", Toast.LENGTH_SHORT).show();
                        loadLeasedRooms();
                    } else {
                        Toast.makeText(this, "Failed to update lease", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void updateTotal(TextView tvTotal, String start, String end, double rentAmount) {
        int months = calculateMonthsBetween(start, end);
        double total = months * rentAmount;
        tvTotal.setText(String.format(Locale.US, "TZS : %.2f", total));
    }

    private interface DateCallback {
        void onDatePicked(String date);
    }

    private void showDatePickerDialog(DateCallback callback) {
        final Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            String date = dateFormat.format(calendar.getTime());
            callback.onDatePicked(date);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private boolean isDateValid(String start, String end) {
        try {
            Calendar s = Calendar.getInstance();
            Calendar e = Calendar.getInstance();
            s.setTime(dateFormat.parse(start));
            e.setTime(dateFormat.parse(end));
            return !e.before(s);
        } catch (Exception e) {
            return false;
        }
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

            // Add 1 month if day of end >= day of start
            if (endCal.get(Calendar.DAY_OF_MONTH) >= startCal.get(Calendar.DAY_OF_MONTH)) {
                totalMonths += 1;
            }

            return Math.max(totalMonths, 1);
        } catch (Exception e) {
            return 0;
        }
    }
}
