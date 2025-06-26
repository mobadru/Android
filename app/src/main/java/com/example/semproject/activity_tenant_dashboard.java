package com.example.semproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class activity_tenant_dashboard extends AppCompatActivity {

    private int tenantId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tenant_dashboard);

        // Get tenantId passed from MainActivity
        tenantId = getIntent().getIntExtra("tenantId", -1);

        if (tenantId == -1) {
            Toast.makeText(this, "Error: tenant ID not found!", Toast.LENGTH_SHORT).show();
            finish(); // Prevent continuing without valid ID
            return;
        }

        // Apply padding for system bars (status, navigation)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(
                    systemBarsInsets.left,
                    systemBarsInsets.top,
                    systemBarsInsets.right,
                    systemBarsInsets.bottom
            );
            return insets;
        });

        // Setup click listeners for all cards
        findViewById(R.id.cardViewLeaseAgreements).setOnClickListener(v -> openLeaseAgreements());
        findViewById(R.id.cardMyRoom).setOnClickListener(v -> openMyRoom());
        findViewById(R.id.cardPayments).setOnClickListener(v -> openPayments());
        findViewById(R.id.cardMaintenance).setOnClickListener(v -> openMaintenance());
        findViewById(R.id.cardMessages).setOnClickListener(v -> openMessages());
        findViewById(R.id.cardProfile).setOnClickListener(v -> openProfile());

        // ✅ Logout button listener - fixed inside onCreate()
        Button logoutButton = findViewById(R.id.btnLogout);
        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> {
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clears back stack
                startActivity(intent);
                finish();
            });
        }
    }

    private void openLeaseAgreements() {
        Intent intent = new Intent(this, LeaseAgreementsActivity.class);
        intent.putExtra("tenantId", tenantId);
        startActivity(intent);
    }

    private void openMyRoom() {
        Toast.makeText(this, "Opening My Room...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, MyRoomActivity.class);
        intent.putExtra("tenantId", tenantId);
        startActivity(intent);
    }

    private void openPayments() {
        Toast.makeText(this, "Opening Payments...", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, PaymentsActivity.class);
        intent.putExtra("tenantId", tenantId);
        startActivity(intent);
    }

    private void openMaintenance() {
        Toast.makeText(this, "Opening Maintenance Requests...", Toast.LENGTH_SHORT).show();
        // Intent intent = new Intent(this, MaintenanceActivity.class);
        // intent.putExtra("tenantId", tenantId);
        // startActivity(intent);
    }

    private void openMessages() {
        Toast.makeText(this, "Opening Messages...", Toast.LENGTH_SHORT).show();
        // Intent intent = new Intent(this, MessagesActivity.class);
        // intent.putExtra("tenantId", tenantId);
        // startActivity(intent);
    }

    private void openProfile() {
        Toast.makeText(this, "Opening Profile...", Toast.LENGTH_SHORT).show();
        // Intent intent = new Intent(this, ProfileActivity.class);
        // intent.putExtra("tenantId", tenantId);
        // startActivity(intent);
    }
}
