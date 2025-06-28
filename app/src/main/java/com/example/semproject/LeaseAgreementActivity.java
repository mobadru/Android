package com.example.semproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class LeaseAgreementActivity extends AppCompatActivity {

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

    private void loadLeasedRooms() {
        containerLeasedRooms.removeAllViews();
        List<LeaseDetails> leases = db.getAllLeases();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (LeaseDetails lease : leases) {
            View leaseView = inflater.inflate(R.layout.owner_item_lease, containerLeasedRooms, false);

            TextView tvRoomName = leaseView.findViewById(R.id.tvRoomName);
            TextView tvPrice = leaseView.findViewById(R.id.tvPrice);
            TextView tvStartDate = leaseView.findViewById(R.id.tvStartDate);
            TextView tvEndDate = leaseView.findViewById(R.id.tvEndDate);
            TextView tvUser = leaseView.findViewById(R.id.tvUser);
            Button btnUpdate = leaseView.findViewById(R.id.btnUpdateLease);
            Button btnDelete = leaseView.findViewById(R.id.btnDeleteLease);

            tvRoomName.setText("Room: " + lease.getRoomNumber());
            tvUser.setText("User: " + lease.getUserName());
            tvPrice.setText("TZS " + String.format("%.0f", lease.getRentAmount()));
            tvStartDate.setText("Start: " + lease.getLeaseStart());
            tvEndDate.setText("End: " + lease.getLeaseEnd());

            btnUpdate.setOnClickListener(v -> showUpdateDialog(lease));
            btnDelete.setOnClickListener(v -> deleteLease(lease));

            containerLeasedRooms.addView(leaseView);
        }
    }

    private void showUpdateDialog(LeaseDetails lease) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.owner_dialog_update_lease, null);
        builder.setView(view);

        EditText etRent = view.findViewById(R.id.etRentAmount);
        EditText etStart = view.findViewById(R.id.etLeaseStart);
        EditText etEnd = view.findViewById(R.id.etLeaseEnd);

        etRent.setText(String.valueOf(lease.getRentAmount()));
        etStart.setText(lease.getLeaseStart());
        etEnd.setText(lease.getLeaseEnd());

        builder.setTitle("Update Lease");
        builder.setPositiveButton("Update", (dialog, which) -> {
            lease.setRentAmount(Double.parseDouble(etRent.getText().toString()));
            lease.setLeaseStart(etStart.getText().toString());
            lease.setLeaseEnd(etEnd.getText().toString());

            boolean updated = db.updateLease(lease);
            if (updated) {
                Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
                loadLeasedRooms();
            } else {
                Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void deleteLease(LeaseDetails lease) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Lease")
                .setMessage("Delete lease for room " + lease.getRoomNumber() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean deleted = db.deleteLease(lease.getLeaseId());
                    if (deleted) {
                        Toast.makeText(this, "Lease deleted", Toast.LENGTH_SHORT).show();
                        loadLeasedRooms();
                    } else {
                        Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
