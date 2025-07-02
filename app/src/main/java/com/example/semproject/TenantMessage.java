package com.example.semproject;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TenantMessage extends AppCompatActivity {

    EditText edtMessage;
    Button btnSend;
    DatabaseHelper db;
    int tenantId;
    int ownerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_message);

        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSendMessage);

        db = new DatabaseHelper(this);
        tenantId = getIntent().getIntExtra("tenantId", -1);

        btnSend.setOnClickListener(v -> {
            String message = edtMessage.getText().toString().trim();
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            if (!message.isEmpty()) {
                boolean success = db.insertMessage(tenantId, ownerId, message, timestamp);
                if (success) {
                    Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show();
                    edtMessage.setText("");
                } else {
                    Toast.makeText(this, "Failed to send.", Toast.LENGTH_SHORT).show();
                }
            } else {
                edtMessage.setError("Enter a message");
            }
        });
    }
}
