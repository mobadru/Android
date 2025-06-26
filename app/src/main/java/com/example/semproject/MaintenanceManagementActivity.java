package com.example.semproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

    private Spinner spinnerRooms;
    private EditText editTextDescription;
    private Button btnSubmit;
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

        spinnerRooms = findViewById(R.id.spinnerRooms);
        editTextDescription = findViewById(R.id.editTextDescription);
        btnSubmit = findViewById(R.id.btnSubmit);

        tenantId = getIntent().getIntExtra("tenantId", -1);
        if (tenantId == -1) {
            Toast.makeText(this, "Invalid tenant ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRoomSpinner();

        btnSubmit.setOnClickListener(v -> submitMaintenanceRequest());
    }

    private void loadRoomSpinner() {
        DatabaseHelper db = new DatabaseHelper(this);
        leaseList = db.getLeaseDetailsByUserId(tenantId);

        roomLabels = new ArrayList<>();
        roomIds = new ArrayList<>();

        for (LeaseDetails lease : leaseList) {
            roomLabels.add("Room " + lease.getRoomNumber());
            roomIds.add(lease.getRoomId()); // You must add getRoomId() to LeaseDetails
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

        int roomId = roomIds.get(selectedPosition);
        String dateReported = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().getTime());

        DatabaseHelper db = new DatabaseHelper(this);
        boolean success = db.insertMaintenanceRequest(tenantId, roomId, description, dateReported);

        if (success) {
            Toast.makeText(this, "Maintenance request submitted", Toast.LENGTH_SHORT).show();
            editTextDescription.setText("");
            spinnerRooms.setSelection(0);
        } else {
            Toast.makeText(this, "Failed to submit request", Toast.LENGTH_SHORT).show();
        }
    }
}
