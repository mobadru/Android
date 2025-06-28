package com.example.semproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class OwnerMaintenance extends AppCompatActivity {

    LinearLayout containerReports;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_maintenance);

        containerReports = findViewById(R.id.containerReports);
        db = new DatabaseHelper(this);

        loadMaintenanceReports();
    }

    private void loadMaintenanceReports() {
        containerReports.removeAllViews();
        List<MaintenanceReport> reports = db.getAllMaintenance();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (MaintenanceReport report : reports) {
            View card = inflater.inflate(R.layout.owner_maintenance_item_admin, containerReports, false);

            TextView tvRoomNumber = card.findViewById(R.id.tvRoomNumber);
            TextView tvDescription = card.findViewById(R.id.tvDescription);
            TextView tvDate = card.findViewById(R.id.tvDate);
            TextView tvStatus = card.findViewById(R.id.tvStatus);
            Button btnUpdate = card.findViewById(R.id.btnUpdateStatus);
            Button btnDelete = card.findViewById(R.id.btnDelete);

            tvRoomNumber.setText("Room: " + report.getRoomNumber());
            tvDescription.setText(report.getDescription());
            tvDate.setText("Reported: " + report.getDateReported());
            tvStatus.setText("Status: " + report.getStatus());

            btnUpdate.setOnClickListener(v -> showUpdateStatusDialog(report));
            btnDelete.setOnClickListener(v -> deleteReport(report.getId()));

            containerReports.addView(card);
        }
    }

    private void showUpdateStatusDialog(MaintenanceReport report) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Status");

        // Inflate a custom layout for spinner
        View view = LayoutInflater.from(this).inflate(android.R.layout.simple_spinner_item, null);
        final Spinner statusSpinner = new Spinner(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Pending", "In Progress", "Complete"}
        );
        statusSpinner.setAdapter(adapter);

        // Set the spinner to current status
        int currentIndex = adapter.getPosition(report.getStatus());
        if (currentIndex != -1) {
            statusSpinner.setSelection(currentIndex);
        }

        builder.setView(statusSpinner);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newStatus = statusSpinner.getSelectedItem().toString();
            db.updateMaintenanceStatus(report.getId(), newStatus);
            Toast.makeText(this, "Status updated", Toast.LENGTH_SHORT).show();
            loadMaintenanceReports();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }


    private void deleteReport(int reportId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Report")
                .setMessage("Are you sure you want to delete this maintenance report?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.deleteMaintenance(reportId);
                    Toast.makeText(this, "Report deleted", Toast.LENGTH_SHORT).show();
                    loadMaintenanceReports();
                })
                .setNegativeButton("No", null)
                .show();
    }
}
