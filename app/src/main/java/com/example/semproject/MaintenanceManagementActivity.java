package com.example.semproject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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

    private Spinner spinnerRooms, spinnerStatus;
    private EditText editTextDescription;
    private Button btnSubmit;
    private ListView listViewRequests;

    private int editingRequestId = -1;
    private int tenantId = -1;

    private List<LeaseDetails> leaseList;
    private List<String> roomLabels;
    private List<Integer> roomIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_maintenance_management);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        spinnerRooms = findViewById(R.id.spinnerRooms);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        editTextDescription = findViewById(R.id.editTextDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        listViewRequests = findViewById(R.id.listViewRequests);

        // Get tenantId from intent
        tenantId = getIntent().getIntExtra("tenantId", -1);
        if (tenantId == -1) {
            Toast.makeText(this, "Invalid tenant ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load rooms into spinnerRooms
        loadRoomSpinner();

        // Setup status spinner (filter)
        setupStatusSpinner();

        // Load tenant requests initially
        loadTenantRequests();

        // Submit button listener
        btnSubmit.setOnClickListener(v -> submitMaintenanceRequest());
    }

    private void setupStatusSpinner() {
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"All", "Pending", "Resolved"}
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadTenantRequests();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }

    private void loadRoomSpinner() {
        DatabaseHelper db = new DatabaseHelper(this);
        leaseList = db.getLeaseDetailsByUserId(tenantId);

        roomLabels = new ArrayList<>();
        roomIds = new ArrayList<>();

        for (LeaseDetails lease : leaseList) {
            roomLabels.add("Room " + lease.getRoomNumber());
            roomIds.add(lease.getRoomId());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roomLabels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRooms.setAdapter(adapter);
    }

    private void submitMaintenanceRequest() {
        String description = editTextDescription.getText().toString().trim();
        int selectedPosition = spinnerRooms.getSelectedItemPosition();

        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedPosition < 0 || selectedPosition >= roomIds.size()) {
            Toast.makeText(this, "Please select a room", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper db = new DatabaseHelper(this);

        if (editingRequestId == -1) {
            // New maintenance request
            int roomId = roomIds.get(selectedPosition);
            String dateReported = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().getTime());
            boolean success = db.insertMaintenanceRequest(tenantId, roomId, description, dateReported);

            if (success) {
                Toast.makeText(this, "Maintenance request submitted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to submit request", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            // Update existing request
            boolean success = db.updateMaintenanceRequest(editingRequestId, description, "Pending");

            if (success) {
                Toast.makeText(this, "Request updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show();
                return;
            }
            editingRequestId = -1;
            btnSubmit.setText("Submit Request");
        }

        // Clear inputs and reload list
        editTextDescription.setText("");
        spinnerRooms.setSelection(0);
        loadTenantRequests();
    }

    private void loadTenantRequests() {
        DatabaseHelper db = new DatabaseHelper(this);

        String selectedStatus = (spinnerStatus.getSelectedItem() != null)
                ? spinnerStatus.getSelectedItem().toString()
                : "All";

        List<MaintenanceReport> reports;

        if ("All".equals(selectedStatus)) {
            reports = db.getAllMaintenanceReports();
        } else {
            reports = db.getMaintenanceReportsByUserIdAndStatus(tenantId, selectedStatus);
        }

        MaintenanceAdapter adapter = new MaintenanceAdapter(this, reports);
        listViewRequests.setAdapter(adapter);

        // Allow editing existing requests by clicking on a list item
        listViewRequests.setOnItemClickListener((parent, view, position, id) -> {
            MaintenanceReport selectedReport = reports.get(position);
            editTextDescription.setText(selectedReport.getDescription());
            editingRequestId = selectedReport.getId();
            btnSubmit.setText("Update Request");

            // Select the room associated with the report
            int roomId = selectedReport.getRoomId();
            int index = roomIds.indexOf(roomId);
            if (index >= 0) {
                spinnerRooms.setSelection(index);
            }
        });
    }
}
