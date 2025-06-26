package com.example.semproject;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class TenantManagementActivity extends AppCompatActivity {

    // UI Components
    Spinner spinnerRole;
    EditText editName, editEmail, editPassword;
    Button btnAddUser;
    RecyclerView recyclerView;

    // Helpers and data
    DatabaseHelper dbHelper;
    UserAdapter adapter;
    List<User> userList;

    /**
     * onCreate() - Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_management);  // Ensure this layout has the correct IDs

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Link UI elements to XML layout
        spinnerRole = findViewById(R.id.spinnerRole);  // Spinner for role selection
        editName = findViewById(R.id.name);                  // EditText for name input
        editEmail = findViewById(R.id.email);                // EditText for email input
        editPassword = findViewById(R.id.password);          // EditText for password input
        btnAddUser = findViewById(R.id.btnAddTenant);          // Button to trigger user addition
        recyclerView = findViewById(R.id.recyclerViewUsers); // RecyclerView to show users

        // Setup Spinner with user roles
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Tenant", "Admin"}  // Roles
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(spinnerAdapter);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadUsers();  // Load user data into the RecyclerView

        // Handle Add User button click
        btnAddUser.setOnClickListener(v -> {
            // Get values from form fields
            String name = editName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            String role = spinnerRole.getSelectedItem().toString();

            // Basic validation
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert user to database
            boolean inserted = dbHelper.insertUser(name, email, password, role);
            if (inserted) {
                Toast.makeText(this, "User added successfully!", Toast.LENGTH_SHORT).show();
                // Clear the input fields
                editName.setText("");
                editEmail.setText("");
                editPassword.setText("");
                // Reload users list
                loadUsers();
            } else {
                Toast.makeText(this, "Failed to add user (duplicate email?)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * loadUsers() - Loads all users from the database and displays them in RecyclerView.
     */
    private void loadUsers() {
        // Fetch all users from the database
        userList = dbHelper.getAllUsers();

        // Setup adapter and attach listeners for edit/delete
        adapter = new UserAdapter(userList, new UserAdapter.OnUserActionListener() {
            @Override
            public void onEdit(User user) {
                // TODO: Implement editing logic
                Toast.makeText(TenantManagementActivity.this, "Edit user: " + user.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDelete(User user) {
                // Delete user from database
                boolean deleted = dbHelper.deleteUser(user.getId());
                if (deleted) {
                    Toast.makeText(TenantManagementActivity.this, "User deleted", Toast.LENGTH_SHORT).show();
                    loadUsers(); // Reload list
                } else {
                    Toast.makeText(TenantManagementActivity.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Attach adapter to RecyclerView
        recyclerView.setAdapter(adapter);
    }
}
