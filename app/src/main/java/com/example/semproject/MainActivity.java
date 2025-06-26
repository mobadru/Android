package com.example.semproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button btnLogin, registerButton;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Login layout

        // Initialize views
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);
        registerButton = findViewById(R.id.registerButton);

        dbHelper = new DatabaseHelper(this);

        // 🔐 Login Logic
        btnLogin.setOnClickListener(v -> {
            String emailInput = editTextEmail.getText().toString().trim();
            String passwordInput = editTextPassword.getText().toString().trim();

            if (emailInput.isEmpty() || passwordInput.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = dbHelper.checkUserCredentials(emailInput, passwordInput);

            if (user != null) {
                SessionManager.saveUser(MainActivity.this, user);

                String role = user.getRole().toLowerCase();
                switch (role) {
                    case "admin":
                        startActivity(new Intent(MainActivity.this, OwnerDashboardActivity.class));
                        finish();
                        break;

                    case "tenant":
                        // ➕ Optional: Go to tenant dashboard first
                        Intent tenantIntent = new Intent(MainActivity.this, activity_tenant_dashboard.class);
                        tenantIntent.putExtra("tenantId", user.getId());
                        startActivity(tenantIntent);
                        finish();

                        // ➕ Optionally, send them directly to lease payment
                        /*
                        Intent leaseIntent = new Intent(MainActivity.this, LeaseAgreementsActivity.class);
                        leaseIntent.putExtra("tenantId", user.getId());
                        startActivity(leaseIntent);
                        finish();
                        */
                        break;

                    default:
                        Toast.makeText(MainActivity.this, "Unauthorized role: " + role, Toast.LENGTH_SHORT).show();
                        break;
                }
            } else {
                Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        });

        // 📥 Navigate to Register Activity
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Register.class);
            startActivity(intent);
        });
    }
}
