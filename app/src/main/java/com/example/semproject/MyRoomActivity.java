package com.example.semproject;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyRoomActivity extends AppCompatActivity {

    private static final String TAG = "MyRoomActivity";
    private RecyclerView recyclerView;
    private RoomAdapterTenant roomAdapter;
    private List<Room> roomList;
    private DatabaseHelper dbHelper;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_room);

        Log.d(TAG, "onCreate: started");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_room_management), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerViewLease);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dbHelper = new DatabaseHelper(this);
        roomList = new ArrayList<>();

        loadAvailableRooms();
    }

    private void loadAvailableRooms() {
        Log.d(TAG, "Loading available rooms from database");
        roomList.clear();
        List<Room> allRooms = dbHelper.getAllRooms();
        Log.d(TAG, "Total rooms found in DB: " + allRooms.size());

        for (Room room : allRooms) {
            if ("Available".equalsIgnoreCase(room.getStatus())) {
                roomList.add(room);
            }
        }

        if (roomList.isEmpty()) {
            Toast.makeText(this, "No available rooms at the moment", Toast.LENGTH_SHORT).show();
        }

        // Initialize adapter with lease click callback
        roomAdapter = new RoomAdapterTenant(roomList, this::showLeaseFormDialog);
        recyclerView.setAdapter(roomAdapter);
    }

    private void showLeaseFormDialog(Room room) {
        Log.d(TAG, "Showing lease form dialog for room: " + room.getRoomNumber());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_lease_form, null);

        TextView tvRoomName = dialogView.findViewById(R.id.tvRoomNumber);
        TextView tvPrice = dialogView.findViewById(R.id.tvPrice);
        TextView tvStartDate = dialogView.findViewById(R.id.tvStartDate);
        TextView tvEndDate = dialogView.findViewById(R.id.tvEndDate);
        TextView tvDuration = dialogView.findViewById(R.id.tvDuration);
        TextView tvTotalCost = dialogView.findViewById(R.id.tvTotalCost);

        tvRoomName.setText(room.getRoomNumber());

        // Calculate rent based on room type
        final double rentPerMonth = room.getType().equalsIgnoreCase("master") ? 100000 : 70000;
        tvPrice.setText(String.format(Locale.US, "%.2f TZS", rentPerMonth));

        final String[] startDate = {""};
        final String[] endDate = {""};

        // Date picker for start date
        tvStartDate.setOnClickListener(v -> showDatePicker((view, year, month, day) -> {
            startDate[0] = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
            tvStartDate.setText(startDate[0]);
            updateCostDisplay(startDate[0], endDate[0], rentPerMonth, tvDuration, tvTotalCost);
        }));

        // Date picker for end date
        tvEndDate.setOnClickListener(v -> showDatePicker((view, year, month, day) -> {
            endDate[0] = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
            tvEndDate.setText(endDate[0]);
            updateCostDisplay(startDate[0], endDate[0], rentPerMonth, tvDuration, tvTotalCost);
        }));

        new AlertDialog.Builder(this)
                .setTitle("Lease Room " + room.getRoomNumber())
                .setView(dialogView)
                .setPositiveButton("Submit", (dialog, which) -> {
                    if (startDate[0].isEmpty() || endDate[0].isEmpty()) {
                        Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    User currentUser = SessionManager.getCurrentUser(this);
                    if (currentUser == null) {
                        Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Calculate duration in months
                    int months = calculateMonthsBetween(startDate[0], endDate[0]);
                    if (months <= 0) {
                        Toast.makeText(this, "Invalid date range", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Insert lease agreement
                    boolean success = dbHelper.insertLeaseAgreement(
                            currentUser.getId(),
                            room.getId(),
                            rentPerMonth,
                            startDate[0],
                            endDate[0],
                            "Pending"  // Default status
                    );

                    if (success) {
                        Toast.makeText(this, "Lease created successfully", Toast.LENGTH_SHORT).show();
                        loadAvailableRooms(); // Refresh room list
                    } else {
                        Toast.makeText(this, "Failed to save lease", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDatePicker(DatePickerDialog.OnDateSetListener listener) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(
                this,
                listener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void updateCostDisplay(String start, String end, double rent, TextView tvDuration, TextView tvTotalCost) {
        if (start.isEmpty() || end.isEmpty()) return;

        try {
            int months = calculateMonthsBetween(start, end);
            tvDuration.setText(String.valueOf(months));
            tvTotalCost.setText(String.format(Locale.US, "%.2f TZS", months * rent));
        } catch (Exception e) {
            Log.e(TAG, "Error calculating cost", e);
        }
    }

    private int calculateMonthsBetween(String startStr, String endStr) {
        try {
            Date start = sdf.parse(startStr);
            Date end = sdf.parse(endStr);

            if (start == null || end == null || end.before(start)) return 0;

            Calendar startCal = Calendar.getInstance();
            startCal.setTime(start);
            Calendar endCal = Calendar.getInstance();
            endCal.setTime(end);

            int yearDiff = endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR);
            int monthDiff = endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH);
            int totalMonths = yearDiff * 12 + monthDiff;

            // Add partial month if end day >= start day
            if (endCal.get(Calendar.DAY_OF_MONTH) >= startCal.get(Calendar.DAY_OF_MONTH)) {
                totalMonths++;
            }

            return Math.max(totalMonths, 1);
        } catch (Exception e) {
            Log.e(TAG, "Date calculation error", e);
            return 0;
        }
    }
}
