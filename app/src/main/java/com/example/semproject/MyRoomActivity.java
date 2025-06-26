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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_room);

        Log.d(TAG, "onCreate: started");

        // Adjust padding for system bars (Edge-to-edge)
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
            Log.d(TAG, "Checking room: " + room.getRoomNumber() + ", status: " + room.getStatus());
            if ("Available".equalsIgnoreCase(room.getStatus())) {
                roomList.add(room);
                Log.d(TAG, "Added available room: " + room.getRoomNumber());
            }
        }

        if (roomList.isEmpty()) {
            Log.d(TAG, "No available rooms found");
            Toast.makeText(this, "No available rooms at the moment", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "Available rooms to display: " + roomList.size());
        }

        // Pass the list and the lease click callback
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

        int rentPerMonth = room.getType().equalsIgnoreCase("master") ? 100000 : 70000;
        tvPrice.setText(rentPerMonth + " TZS");
        Log.d(TAG, "Rent per month set to: " + rentPerMonth);

        final String[] startDate = {""};
        final String[] endDate = {""};

        tvStartDate.setOnClickListener(v -> {
            Log.d(TAG, "Start date picker opened");
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                startDate[0] = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                tvStartDate.setText(startDate[0]);
                Log.d(TAG, "Start date selected: " + startDate[0]);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        tvEndDate.setOnClickListener(v -> {
            Log.d(TAG, "End date picker opened");
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                endDate[0] = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                tvEndDate.setText(endDate[0]);
                Log.d(TAG, "End date selected: " + endDate[0]);

                // Calculate duration in months and total cost
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    Date start = sdf.parse(startDate[0]);
                    Date end = sdf.parse(endDate[0]);
                    long diffInMillis = end.getTime() - start.getTime();

                    int months = (int) (diffInMillis / (1000L * 60 * 60 * 24 * 30));
                    months = Math.max(months, 1); // minimum 1 month

                    tvDuration.setText(String.valueOf(months));
                    tvTotalCost.setText((months * rentPerMonth) + " TZS");

                    Log.d(TAG, "Calculated duration: " + months + " months, total cost: " + (months * rentPerMonth) + " TZS");
                } catch (Exception e) {
                    Log.e(TAG, "Error calculating duration and cost", e);
                }
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        new AlertDialog.Builder(this)
                .setTitle("Lease Room " + room.getRoomNumber())
                .setView(dialogView)
                .setPositiveButton("Submit", (dialog, which) -> {
                    Log.d(TAG, "Lease form submit clicked");
                    if (startDate[0].isEmpty() || endDate[0].isEmpty()) {
                        Toast.makeText(this, "Please select both dates", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Submit failed: Dates not selected");
                        return;
                    }

                    User currentUser = SessionManager.getCurrentUser(this);
                    if (currentUser == null) {
                        Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Submit failed: User not logged in");
                        return;
                    }

                    Log.d(TAG, "Attempting to insert lease agreement for userId=" + currentUser.getId()
                            + ", roomId=" + room.getId()
                            + ", startDate=" + startDate[0]
                            + ", endDate=" + endDate[0]);

                    boolean success = dbHelper.insertLeaseAgreement(currentUser.getId(), room.getId(), startDate[0], endDate[0]);
                    if (success) {
                        Toast.makeText(this, "Lease created successfully", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Lease agreement inserted successfully");
                        loadAvailableRooms(); // Refresh the list after lease
                    } else {
                        Toast.makeText(this, "Failed to save lease", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Failed to insert lease agreement");
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Log.d(TAG, "Lease form cancelled");
                })
                .show();
    }
}
