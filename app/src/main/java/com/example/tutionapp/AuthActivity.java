package com.example.tutionapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button   btnLogin, btnGoToSignUp;
    private DBHelper dbHelper;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Init views
        etEmail       = findViewById(R.id.etEmail);
        etPassword    = findViewById(R.id.etPassword);
        btnLogin      = findViewById(R.id.btnLogin);
        btnGoToSignUp = findViewById(R.id.btnGoToSignUp);

        // Init DB helper and prefs
        dbHelper = new DBHelper(this);
        prefs    = getSharedPreferences("app_prefs", MODE_PRIVATE);

        // Launch SignUpActivity if user needs to register
        btnGoToSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        });

        // Login flow
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pwd   = etPassword.getText().toString();

            if (email.isEmpty() || pwd.isEmpty()) {
                Toast.makeText(this, "Email & password required", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor c = dbHelper.getUserByEmail(email);
            if (c.moveToFirst()) {
                String storedPwd = c.getString(c.getColumnIndexOrThrow("password"));
                String role      = c.getString(c.getColumnIndexOrThrow("role"));
                c.close();

                if (pwd.equals(storedPwd)) {
                    // 1) Save logged-in email for later use (e.g. QR code)
                    prefs.edit()
                            .putString("logged_email", email)
                            .apply();

                    // 2) Route to the correct home screen
                    Intent i;
                    if ("admin".equalsIgnoreCase(role)) {
                        i = new Intent(this, AdminHomeActivity.class);
                    }
                    else if ("teacher".equalsIgnoreCase(role)) {
                        i = new Intent(this, TeacherHomeActivity.class);
                    }
                    else {  // student
                        i = new Intent(this, StudentHomeActivity.class);
                    }

                    startActivity(i);
                    finish();
                } else {
                    Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show();
                }
            } else {
                c.close();
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
