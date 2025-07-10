package com.example.tutionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private Spinner  spinnerRole;
    private EditText etBirthYear, etPhone, etEmergencyName, etEmergencyPhone;
    private Button   btnSignUp, btnGoToLogin;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etName           = findViewById(R.id.etName);
        etEmail          = findViewById(R.id.etEmail);
        etPassword       = findViewById(R.id.etPassword);
        spinnerRole      = findViewById(R.id.spinnerRole);
        etBirthYear      = findViewById(R.id.etBirthYear);
        etPhone          = findViewById(R.id.etPhone);
        etEmergencyName  = findViewById(R.id.etEmergencyName);
        etEmergencyPhone = findViewById(R.id.etEmergencyPhone);
        btnSignUp        = findViewById(R.id.btnSignUp);
        btnGoToLogin     = findViewById(R.id.btnGoToLogin);

        dbHelper = new DBHelper(this);

        btnSignUp.setOnClickListener(v -> {
            String name    = etName.getText().toString().trim();
            String email   = etEmail.getText().toString().trim();
            String pwd     = etPassword.getText().toString();
            String role    = spinnerRole.getSelectedItem().toString().toLowerCase();
            String byear   = etBirthYear.getText().toString().trim();
            String phone   = etPhone.getText().toString().trim();
            String eName   = etEmergencyName.getText().toString().trim();
            String ePhone  = etEmergencyPhone.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || pwd.isEmpty()
                    || byear.isEmpty() || phone.isEmpty()
                    || eName.isEmpty() || ePhone.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean ok = dbHelper.insertUser(
                    name, email, pwd, role,
                    Integer.parseInt(byear),
                    phone, eName, ePhone
            );

            if (ok) {
                Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, AuthActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Error: Email may already exist", Toast.LENGTH_SHORT).show();
            }
        });

        btnGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
        });
    }
}
