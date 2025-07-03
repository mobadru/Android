package com.example.semproject;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_management);

        dbHelper = new DatabaseHelper(this);

        spinnerRole = findViewById(R.id.spinnerRole);
        editName = findViewById(R.id.name);
        editEmail = findViewById(R.id.email);
        EditText editPhone = findViewById(R.id.editPhone);
        editPassword = findViewById(R.id.password);
        btnAddUser = findViewById(R.id.btnAddTenant);
        recyclerView = findViewById(R.id.recyclerViewUsers);

        // Setup Spinner with user roles
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Tenant", "Admin"}
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(spinnerAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadUsers();

        btnAddUser.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            String role = spinnerRole.getSelectedItem().toString();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean inserted = dbHelper.insertUser(name, phone, email, password, role);
            if (inserted) {
                Toast.makeText(this, "User added successfully!", Toast.LENGTH_SHORT).show();
                editName.setText("");
                editEmail.setText("");
                editPassword.setText("");
                loadUsers();
            } else {
                Toast.makeText(this, "Failed to add user (duplicate email?)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUsers() {
        userList = dbHelper.getAllUsers();

        adapter = new UserAdapter(userList, new UserAdapter.OnUserActionListener() {
            @Override
            public void onEdit(User user) {
                showEditUserDialog(user);
            }

            @Override
            public void onDelete(User user) {
                boolean deleted = dbHelper.deleteUser(user.getId());
                if (deleted) {
                    Toast.makeText(TenantManagementActivity.this, "User deleted", Toast.LENGTH_SHORT).show();
                    loadUsers();
                } else {
                    Toast.makeText(TenantManagementActivity.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void showEditUserDialog(User user) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_user, null);

        EditText editName = dialogView.findViewById(R.id.editUserName);
        EditText editPhone = dialogView.findViewById(R.id.editUserPhone);
        EditText editEmail = dialogView.findViewById(R.id.editUserEmail);
        EditText editPassword = dialogView.findViewById(R.id.editUserPassword);
        Spinner spinnerRole = dialogView.findViewById(R.id.spinnerUserRole);

        // Pre-fill fields
        editName.setText(user.getName());
        editPhone.setText(user.getPhone());
        editEmail.setText(user.getEmail());
        editPassword.setText(user.getPassword());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Tenant", "Admin"});
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(spinnerAdapter);
        spinnerRole.setSelection(user.getRole() != null && user.getRole().equalsIgnoreCase("Admin") ? 1 : 0);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Edit User")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = editName.getText().toString().trim();
                    String newPhone = editPhone.getText().toString().trim();
                    String newEmail = editEmail.getText().toString().trim();
                    String newPassword = editPassword.getText().toString().trim();
                    String newRole = spinnerRole.getSelectedItem().toString();

                    if (newName.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
                        Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean updated = dbHelper.updateUser(user.getId(), newName, newEmail, newPassword, newPhone, newRole);

                    if (updated) {
                        Toast.makeText(this, "User updated!", Toast.LENGTH_SHORT).show();
                        loadUsers();
                    } else {
                        Toast.makeText(this, "Failed to update user", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
