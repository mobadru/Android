package com.example.semproject;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.semproject.MainActivity;

public class Register extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        dbHelper = new DatabaseHelper(this);

        EditText nameInput = findViewById(R.id.name);
        EditText emailInput = findViewById(R.id.email);
        EditText phoneInput = findViewById(R.id.phonenumber);           // Add this line
        EditText passwordInput = findViewById(R.id.password);
        Button registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String phone = phoneInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean inserted = dbHelper.insertUser(name, email, phone, password, DatabaseHelper.ROLE_TENANT); // Use constant if available
            if (inserted) {
                Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Registration failed! Email may already be used.", Toast.LENGTH_SHORT).show();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView backToLogin = findViewById(R.id.backToLogin);
        backToLogin.setOnClickListener(v -> {
            startActivity(new Intent(Register.this, MainActivity.class));
            finish();
        });
    }
}
