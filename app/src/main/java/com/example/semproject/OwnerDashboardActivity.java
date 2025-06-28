package com.example.semproject;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class OwnerDashboardActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_dashboard);

        findViewById(R.id.cardManageTenants).setOnClickListener(v -> {
            // TODO: Replace with actual activity when ready
            startActivity(new Intent(this, TenantManagementActivity.class));
        });

        findViewById(R.id.cardViewLeaseAgreements).setOnClickListener(v -> {
            // TODO: Replace with actual activity when ready
            startActivity(new Intent(this, LeaseAgreementActivity.class));
        });

        findViewById(R.id.cardViewPayments).setOnClickListener(v -> {
            // TODO: Replace with actual activity when ready
            startActivity(new Intent(this, Owner_Payments.class));
        });

        findViewById(R.id.cardManageRooms).setOnClickListener(v -> {
            // TODO: Replace with actual activity when ready
            startActivity(new Intent(this, RoomManagementActivity.class));
        });

        findViewById(R.id.cardManageMaintenance).setOnClickListener(v -> {
            // TODO: Replace with actual activity when ready
            startActivity(new Intent(this, OwnerMaintenance.class));
        });

        findViewById(R.id.cardManageMessages).setOnClickListener(v -> {
            // TODO: Replace with actual activity when ready
            startActivity(new Intent(this, MessageManagementActivity.class));
        });

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clear backstack
            startActivity(intent);
            finish();
        });
    }
}