package com.example.semproject;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class activity_admin_notifications extends AppCompatActivity {

    private LinearLayout containerNotifications;
    private DatabaseHelper dbHelper;
    private int adminUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_notifications);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        containerNotifications = findViewById(R.id.containerNotifications);
        dbHelper = new DatabaseHelper(this);
        adminUserId = dbHelper.getAdminUserId();

        loadNotifications();
    }

    private void loadNotifications() {
        List<Notification> notifications = dbHelper.getNotificationsForAdmin(adminUserId);

        containerNotifications.removeAllViews();

        if (notifications.isEmpty()) {
            TextView emptyView = new TextView(this);
            emptyView.setText("No Notifications");
            emptyView.setTextSize(18);
            emptyView.setTextColor(Color.GRAY);
            emptyView.setPadding(20, 40, 20, 40);
            emptyView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            containerNotifications.addView(emptyView);
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);

        for (Notification notification : notifications) {
            View itemView = inflater.inflate(R.layout.item_notification, containerNotifications, false);

            TextView tvType = itemView.findViewById(R.id.tvType);
            TextView tvMessage = itemView.findViewById(R.id.tvMessage);
            TextView tvTimestamp = itemView.findViewById(R.id.tvTimestamp);

            tvType.setText(notification.getType());
            tvMessage.setText(notification.getMessage());
            tvTimestamp.setText(notification.getTimestamp());

            // Visual indication for unread notifications
            if (!notification.isRead()) {
                tvType.setTextColor(Color.parseColor("#3F51B5")); // Blue for unread
                tvMessage.setAlpha(1f);
                tvTimestamp.setAlpha(1f);
            } else {
                tvType.setTextColor(Color.GRAY);
                tvMessage.setAlpha(0.5f);
                tvTimestamp.setAlpha(0.5f);
            }

            itemView.setOnClickListener(v -> {
                if (!notification.isRead()) {
                    boolean success = dbHelper.markNotificationAsRead(notification.getId());
                    if (success) {
                        notification.setRead(true);
                        // Update UI to indicate read
                        tvType.setTextColor(Color.GRAY);
                        tvMessage.setAlpha(0.5f);
                        tvTimestamp.setAlpha(0.5f);
                        Toast.makeText(this, "Notification marked as read", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to mark notification as read", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Notification already read", Toast.LENGTH_SHORT).show();
                }
            });

            containerNotifications.addView(itemView);
        }
    }
}
