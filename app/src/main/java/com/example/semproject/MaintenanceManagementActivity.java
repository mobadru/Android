package com.example.semproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView; // Added for ListView title
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MaintenanceManagementActivity extends AppCompatActivity {

    private Spinner spinnerRooms;
    private EditText editTextDescription;
    private Button btnSubmit;
    private ListView listViewRequests;

    private int editingRequestId = -1; // -1 indicates new request, otherwise ID of request being edited
    private int tenantId = -1; // ID of the currently logged-in tenant

    private List<LeaseDetails> leaseList; // List of leases for the current tenant
    private List<String> roomLabels;      // Labels for the room spinner (e.g., "Room 101")
    private List<Integer> roomIds;        // Corresponding Room IDs for spinner selection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // For edge-to-edge display
        setContentView(R.layout.activity_maintenance_management);

        // Apply window insets for system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        spinnerRooms = findViewById(R.id.spinnerRooms);
        editTextDescription = findViewById(R.id.editTextDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        listViewRequests = findViewById(R.id.listViewRequests);

        // Get tenantId from the intent (passed from previous activity, e.g., login or tenant dashboard)
        tenantId = getIntent().getIntExtra("tenantId", -1);
        if (tenantId == -1) {
            Toast.makeText(this, "Error: Tenant ID not provided. Cannot load maintenance.", Toast.LENGTH_LONG).show();
            Log.e("MaintenanceActivity", "Tenant ID is -1. Finishing activity.");
            finish(); // Close activity if no valid tenant ID
            return;
        }

        // Load rooms associated with the tenant's leases into the spinner
        loadRoomSpinner();

        // Load all maintenance requests submitted by this tenant
        loadTenantRequests();

        // Set up listener for the Submit/Update button
        btnSubmit.setOnClickListener(v -> submitMaintenanceRequest());
    }

    /**
     * Loads the rooms that the current tenant has leased into the spinner.
     * Fetches lease details for the tenant from the database.
     */
    private void loadRoomSpinner() {
        DatabaseHelper db = new DatabaseHelper(this);
        leaseList = db.getLeaseDetailsByUserId(tenantId); // Get all leases for this tenant

        roomLabels = new ArrayList<>(); // To store display names for rooms (e.g., "Room 101")
        roomIds = new ArrayList<>();    // To store actual room IDs

        if (leaseList == null || leaseList.isEmpty()) {
            Toast.makeText(this, "No leased rooms found for this tenant.", Toast.LENGTH_LONG).show();
            roomLabels.add("No Rooms Available"); // Display a message if no rooms
            roomIds.add(-1); // Indicate no valid room ID
        } else {
            for (LeaseDetails lease : leaseList) {
                String roomNumber = lease.getRoomNumber();
                // Handle cases where roomNumber might be null or empty from DB
                if (roomNumber == null || roomNumber.trim().isEmpty()) {
                    roomLabels.add("Room (Unknown Number) - ID:" + lease.getRoomId());
                } else {
                    roomLabels.add("Room " + roomNumber);
                }
                roomIds.add(lease.getRoomId());
            }
        }

        // Create and set an ArrayAdapter for the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roomLabels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRooms.setAdapter(adapter);
    }

    /**
     * Handles the submission or update of a maintenance request.
     * Checks if it's a new request or an update based on editingRequestId.
     */
    private void submitMaintenanceRequest() {
        String description = editTextDescription.getText().toString().trim();
        int selectedPosition = spinnerRooms.getSelectedItemPosition();

        // Basic input validation
        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter a description for the issue.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedPosition < 0 || selectedPosition >= roomIds.size() || roomIds.get(selectedPosition) == -1) {
            Toast.makeText(this, "Please select a valid room.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper db = new DatabaseHelper(this);

        if (editingRequestId == -1) { // It's a new maintenance request
            int roomId = roomIds.get(selectedPosition);
            String dateReported = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().getTime());

            // Insert the new request into the database. Initial status is "Pending".
            boolean success = db.insertMaintenanceRequest(tenantId, roomId, description, dateReported, "Pending");

            if (success) {
                Toast.makeText(this, "Maintenance request submitted successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to submit request. Please try again.", Toast.LENGTH_SHORT).show();
                Log.e("MaintenanceActivity", "Failed to insert new maintenance request.");
                return;
            }
        } else { // It's an update to an existing request
            // Note: Currently, only description is updatable by tenant. Status (like "Pending") might be managed by admin.
            // If the tenant is updating, it usually means they are adding more details or correcting an old report.
            // The status is typically changed by the admin. We can set it to "Pending" if an edit is made.
            boolean success = db.updateMaintenanceRequest(editingRequestId, description, "Pending");

            if (success) {
                Toast.makeText(this, "Maintenance request updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update request. Please try again.", Toast.LENGTH_SHORT).show();
                Log.e("MaintenanceActivity", "Failed to update maintenance request ID: " + editingRequestId);
                return;
            }
            editingRequestId = -1; // Reset editing state
            btnSubmit.setText("Submit Request"); // Change button text back
        }

        // Clear input fields and refresh the list after submission/update
        editTextDescription.setText("");
        spinnerRooms.setSelection(0); // Select the first item
        loadTenantRequests(); // Refresh the list of requests
    }

    /**
     * Loads all maintenance requests for the current tenant from the database
     * and displays them in the ListView.
     */
    private void loadTenantRequests() {
        DatabaseHelper db = new DatabaseHelper(this);
        // Fetches all maintenance reports for the specific tenant ID
        List<MaintenanceReport> reports = db.getMaintenanceReportsByUserId(tenantId);

        if (reports == null || reports.isEmpty()) {
            Toast.makeText(this, "You have no maintenance requests yet.", Toast.LENGTH_SHORT).show();
            // Optionally clear the list view if there are no reports
            listViewRequests.setAdapter(null);
            return;
        }

        // Create and set the custom adapter for the ListView
        MaintenanceAdapter adapter = new MaintenanceAdapter(this, reports);
        listViewRequests.setAdapter(adapter);

        // Set up item click listener for the ListView to allow editing
        listViewRequests.setOnItemClickListener((parent, view, position, id) -> {
            MaintenanceReport selectedReport = reports.get(position);

            // Populate input fields with selected report's data for editing
            editTextDescription.setText(selectedReport.getDescription());
            editingRequestId = selectedReport.getId(); // Set ID of the request being edited
            btnSubmit.setText("Update Request"); // Change button text to "Update"

            // Select the corresponding room in the spinner
            int roomId = selectedReport.getRoomId();
            int index = roomIds.indexOf(roomId); // Find the index of the room in our list
            if (index >= 0) {
                spinnerRooms.setSelection(index);
            } else {
                // Handle case where room is not found in the current tenant's leased rooms (e.g., lease expired)
                Toast.makeText(this, "Associated room not found in your current leases.", Toast.LENGTH_LONG).show();
                spinnerRooms.setSelection(0); // Default to first item
            }
        });
    }
}
