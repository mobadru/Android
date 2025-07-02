package com.example.semproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class LeaseAgreementActivity extends AppCompatActivity {

    private static final String TAG = "LeaseAgreementActivity";

    private LinearLayout containerLeasedRooms;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lease_agreement);

        containerLeasedRooms = findViewById(R.id.containerLeasedRooms);
        db = new DatabaseHelper(this);

        loadLeasedRooms();
    }

    /**
     * Load all lease agreements from DB and display them in the container.
     */
    private void loadLeasedRooms() {
        containerLeasedRooms.removeAllViews();

        List<LeaseDetails> leases = db.getAllLeases();
        Log.d(TAG, "Number of leases retrieved from DB: " + leases.size());

        if (leases.isEmpty()) {
            Toast.makeText(this, "No leased rooms to display.", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Leases list is empty, showing toast.");
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);

        for (LeaseDetails lease : leases) {
            Log.d(TAG, "Inflating view for Lease: Room " + lease.getRoomNumber() + ", User " + lease.getUserName());

            View leaseView = inflater.inflate(R.layout.owner_item_lease, containerLeasedRooms, false);

            TextView tvRoomName = leaseView.findViewById(R.id.tvRoomName);
            TextView tvPrice = leaseView.findViewById(R.id.tvPrice);
            TextView tvStartDate = leaseView.findViewById(R.id.tvStartDate);
            TextView tvEndDate = leaseView.findViewById(R.id.tvEndDate);
            TextView tvUser = leaseView.findViewById(R.id.tvUser);
            TextView tvLeaseStatus = leaseView.findViewById(R.id.tvLeaseStatus);
            Button btnUpdate = leaseView.findViewById(R.id.btnUpdateLease);

            if (tvRoomName != null) tvRoomName.setText("Room: " + lease.getRoomNumber());
            if (tvUser != null) tvUser.setText("User: " + lease.getUserName());
            if (tvPrice != null) tvPrice.setText("TZS " + String.format("%.0f", lease.getRentAmount()));
            if (tvStartDate != null) tvStartDate.setText("Start: " + lease.getLeaseStart());
            if (tvEndDate != null) tvEndDate.setText("End: " + lease.getLeaseEnd());
            if (tvLeaseStatus != null) tvLeaseStatus.setText("Status: " + lease.getStatus());

            if (btnUpdate != null) {
                btnUpdate.setOnClickListener(v -> showUpdateDialog(lease));
            }

            containerLeasedRooms.addView(leaseView);
        }

        Log.d(TAG, "Finished loading leased rooms and added to container.");
    }

    /**
     * Show dialog to update lease status.
     */
    private void showUpdateDialog(LeaseDetails lease) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.owner_dialog_update_lease, null);
        builder.setView(view);

        Spinner spinnerStatus = view.findViewById(R.id.spinnerLeaseStatus);

        String[] statusOptions = {"Pending", "Active", "Terminated"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);

        // Pre-select current status
        int currentIndex = 0;
        for (int i = 0; i < statusOptions.length; i++) {
            if (statusOptions[i].equalsIgnoreCase(lease.getStatus())) {
                currentIndex = i;
                break;
            }
        }
        spinnerStatus.setSelection(currentIndex);

        builder.setTitle("Update Lease Status");
        builder.setPositiveButton("Update", (dialog, which) -> {
            String newStatus = spinnerStatus.getSelectedItem().toString();
            boolean updated = db.updateLeaseStatus(lease.getLeaseId(), newStatus);
            if (updated) {
                Toast.makeText(this, "Status updated!", Toast.LENGTH_SHORT).show();
                loadLeasedRooms();  // Refresh UI
            } else {
                Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }
}
