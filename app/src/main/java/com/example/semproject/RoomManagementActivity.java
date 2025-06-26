package com.example.semproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RoomManagementActivity extends AppCompatActivity {

    private EditText editRoomNumber, editDescription, editType, editRentAmount;
    private Spinner spinnerStatus;
    private Button btnAddRoom;
    private RecyclerView recyclerViewRooms;
    private RoomAdapter roomAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_management);

        editRoomNumber = findViewById(R.id.editRoomNumber);
        editDescription = findViewById(R.id.editDescription);
        editType = findViewById(R.id.editType);
        editRentAmount = findViewById(R.id.editRentAmount);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnAddRoom = findViewById(R.id.btnAddRoom);
        recyclerViewRooms = findViewById(R.id.recyclerViewRooms);

        // Setup Spinner with values
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Available", "Leased"}
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        dbHelper = new DatabaseHelper(this);
        recyclerViewRooms.setLayoutManager(new LinearLayoutManager(this));

        loadRooms();

        btnAddRoom.setOnClickListener(v -> addRoom());
    }

    private void loadRooms() {
        List<Room> roomList = dbHelper.getAllRooms();
        roomAdapter = new RoomAdapter(roomList, new RoomAdapter.OnRoomActionListener() {
            @Override
            public void onEdit(Room room) {
                showEditDialog(room);
            }

            @Override
            public void onDelete(Room room) {
                boolean deleted = dbHelper.deleteRoom(room.getId());
                if (deleted) {
                    Toast.makeText(RoomManagementActivity.this, "Room deleted", Toast.LENGTH_SHORT).show();
                    loadRooms();
                } else {
                    Toast.makeText(RoomManagementActivity.this, "Failed to delete room", Toast.LENGTH_SHORT).show();
                }
            }
        });
        recyclerViewRooms.setAdapter(roomAdapter);
    }

    private void addRoom() {
        String number = editRoomNumber.getText().toString().trim();
        String desc = editDescription.getText().toString().trim();
        String type = editType.getText().toString().trim();
        String rentStr = editRentAmount.getText().toString().trim();
        String status = spinnerStatus.getSelectedItem().toString();

        if (TextUtils.isEmpty(number) || TextUtils.isEmpty(type) || TextUtils.isEmpty(rentStr) || TextUtils.isEmpty(status)) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double rent;
        try {
            rent = Double.parseDouble(rentStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid rent amount", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean inserted = dbHelper.insertRoom(number, desc, type, rent, status);

        if (inserted) {
            Toast.makeText(this, "Room added", Toast.LENGTH_SHORT).show();
            clearInputs();
            loadRooms();
        } else {
            Toast.makeText(this, "Failed to add room", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputs() {
        editRoomNumber.setText("");
        editDescription.setText("");
        editType.setText("");
        editRentAmount.setText("");
        spinnerStatus.setSelection(0); // reset spinner to first item ("Available")
    }

    private void showEditDialog(Room room) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_room, null);
        EditText editRoomNumber = view.findViewById(R.id.editRoomNumber);
        EditText editDescription = view.findViewById(R.id.editDescription);
        EditText editType = view.findViewById(R.id.editType);
        EditText editRentAmount = view.findViewById(R.id.editRentAmount);
        Spinner spinnerStatus = view.findViewById(R.id.spinnerStatus);

        editRoomNumber.setText(room.getRoomNumber());
        editDescription.setText(room.getDescription());
        editType.setText(room.getType());
        editRentAmount.setText(String.valueOf(room.getRentAmount()));

        // Setup spinner in dialog
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Available", "Leased"}
        );
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);

        // Set spinner selection based on room status
        int spinnerPosition = statusAdapter.getPosition(room.getStatus());
        if (spinnerPosition >= 0) {
            spinnerStatus.setSelection(spinnerPosition);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Edit Room")
                .setView(view)
                .setPositiveButton("Update", (d, which) -> {
                    String number = editRoomNumber.getText().toString().trim();
                    String desc = editDescription.getText().toString().trim();
                    String type = editType.getText().toString().trim();

                    double rent;
                    try {
                        rent = Double.parseDouble(editRentAmount.getText().toString().trim());
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid rent amount", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String statusSelected = spinnerStatus.getSelectedItem().toString();

                    boolean updated = dbHelper.updateRoom(room.getId(), number, desc, type, rent, statusSelected);
                    if (updated) {
                        Toast.makeText(this, "Room updated", Toast.LENGTH_SHORT).show();
                        loadRooms();
                    } else {
                        Toast.makeText(this, "Failed to update room", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }
}
