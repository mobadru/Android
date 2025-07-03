package com.example.semproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TenantProfile extends AppCompatActivity {

    private static final String TAG = "TenantProfile";

    private TextView tvName, tvEmail, tvPhone, tvPassword;
    private Button btnEditProfile; // The edit button

    private DatabaseHelper dbHelper;
    private int tenantId;
    private User currentUser; // Store the currently loaded user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_profile); // Your existing layout

        // Initialize TextViews
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvPassword = findViewById(R.id.tvPassword);
        btnEditProfile = findViewById(R.id.btnEditProfile); // Initialize the edit button

        dbHelper = new DatabaseHelper(this); // Initialize DatabaseHelper

        tenantId = getIntent().getIntExtra("tenantId", -1); // Get tenantId from intent
        if (tenantId == -1) {
            Toast.makeText(this, "Error: Tenant ID not found.", Toast.LENGTH_LONG).show();
            finish(); // Close activity if no valid ID
            return;
        }

        loadUserProfile(); // Load and display user data

        // Set click listener for the Edit Profile button
        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfile(); // Refresh profile when activity resumes (e.g., after dialog closes)
    }

    private void loadUserProfile() {
        currentUser = dbHelper.getUserById(tenantId); // Fetch the user data

        if (currentUser == null) {
            Log.w(TAG, "User not found for ID: " + tenantId + ", loading default user.");
            // Fallback for debugging, if user not in DB
            currentUser = new User(1, "Default User", "default@example.com", "0000000000", "password", "TENANT");
        }

        // Set text for TextViews
        tvName.setText(safeString(currentUser.getName(), "N/A"));
        tvEmail.setText(safeString(currentUser.getEmail(), "N/A"));
        tvPhone.setText(safeString(currentUser.getPhone(), "N/A"));
        tvPassword.setText("Password: " + maskPassword(currentUser.getPassword()));
    }

    private void showEditProfileDialog() {
        // Inflate the custom layout for the dialog
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_edit_profile, null); // Create this XML

        // Get references to EditTexts in the dialog layout
        EditText etName = dialogView.findViewById(R.id.etName);
        EditText etEmail = dialogView.findViewById(R.id.etEmail);
        EditText etPhone = dialogView.findViewById(R.id.etPhone);
        EditText etPassword = dialogView.findViewById(R.id.etPassword);

        // Pre-fill EditTexts with current user data
        if (currentUser != null) {
            etName.setText(currentUser.getName());
            etEmail.setText(currentUser.getEmail());
            etPhone.setText(currentUser.getPhone());
            etPassword.setText(currentUser.getPassword()); // Be cautious with displaying plain passwords
        }

        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Edit Profile")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle Save button click
                        String name = etName.getText().toString().trim();
                        String email = etEmail.getText().toString().trim();
                        String phone = etPhone.getText().toString().trim();
                        String password = etPassword.getText().toString(); // Get password

                        // Basic validation
                        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                            Toast.makeText(TenantProfile.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                            // Do not dismiss dialog here, or dismiss and show error
                            return; // Return to prevent saving if fields are empty
                        }

                        // Update the currentUser object with new data
                        currentUser.setName(name);
                        currentUser.setEmail(email);
                        currentUser.setPhone(phone);
                        currentUser.setPassword(password); // Update password

                        // Save updated user data to the database
                        boolean success = dbHelper.updateUser(currentUser); // Ensure updateUser method exists in DatabaseHelper
                        if (success) {
                            Toast.makeText(TenantProfile.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                            loadUserProfile(); // Refresh the displayed profile
                        } else {
                            Toast.makeText(TenantProfile.this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Dismiss the dialog on Cancel
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show(); // Show the dialog
    }

    // Helper method to safely get string or default value
    private String safeString(String s, String defaultVal) {
        return (s != null && !s.isEmpty()) ? s : defaultVal;
    }

    // Helper method to mask password for display
    private String maskPassword(String password) {
        if (password == null || password.isEmpty()) return "";
        // Consider not showing password in edit dialog or mask it even there for security
        return "*".repeat(password.length());
    }
}
