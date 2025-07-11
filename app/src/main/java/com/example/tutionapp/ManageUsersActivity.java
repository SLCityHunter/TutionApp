package com.example.tutionapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class ManageUsersActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private TextView tvUsers;
    private EditText etEmailToEdit, etName, etPassword, etRole, etBirthYear, etPhone, etEmergencyName, etEmergencyPhone;
    private Button btnAddUser, btnLoadUser, btnUpdate, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        dbHelper = new DBHelper(this);

        tvUsers         = findViewById(R.id.tvUsers);
        etEmailToEdit   = findViewById(R.id.etEmailToEdit);
        etName          = findViewById(R.id.etName);
        etPassword      = findViewById(R.id.etPassword);
        etRole          = findViewById(R.id.etRole);
        etBirthYear     = findViewById(R.id.etBirthYear);
        etPhone         = findViewById(R.id.etPhone);
        etEmergencyName = findViewById(R.id.etEmergencyName);
        etEmergencyPhone= findViewById(R.id.etEmergencyPhone);

        btnAddUser  = findViewById(R.id.btnAddUser);
        btnLoadUser = findViewById(R.id.btnLoadUser);
        btnUpdate   = findViewById(R.id.btnUpdate);
        btnDelete   = findViewById(R.id.btnDelete);

        loadUsers();

        btnAddUser.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });

        btnLoadUser.setOnClickListener(v -> {
            String email = etEmailToEdit.getText().toString().trim();
            Cursor c = dbHelper.getUserByEmail(email);
            if (c.moveToFirst()) {
                etName.setText(c.getString(c.getColumnIndexOrThrow("name")));
                etPassword.setText(c.getString(c.getColumnIndexOrThrow("password")));
                etRole.setText(c.getString(c.getColumnIndexOrThrow("role")));
                etBirthYear.setText(String.valueOf(c.getInt(c.getColumnIndexOrThrow("birth_year"))));
                etPhone.setText(c.getString(c.getColumnIndexOrThrow("phone")));
                etEmergencyName.setText(c.getString(c.getColumnIndexOrThrow("emergency_name")));
                etEmergencyPhone.setText(c.getString(c.getColumnIndexOrThrow("emergency_phone")));
            } else {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            }
            c.close();
        });

        btnUpdate.setOnClickListener(v -> {
            String email = etEmailToEdit.getText().toString().trim();
            boolean ok = dbHelper.updateUser(
                    email,
                    etName.getText().toString().trim(),
                    etPassword.getText().toString().trim(),
                    etRole.getText().toString().trim(),
                    Integer.parseInt(etBirthYear.getText().toString().trim()),
                    etPhone.getText().toString().trim(),
                    etEmergencyName.getText().toString().trim(),
                    etEmergencyPhone.getText().toString().trim()
            );
            Toast.makeText(this, ok ? "User updated" : "Update failed", Toast.LENGTH_SHORT).show();
            loadUsers();
        });

        btnDelete.setOnClickListener(v -> {
            String email = etEmailToEdit.getText().toString().trim();
            boolean deleted = dbHelper.deleteUserByEmail(email);
            Toast.makeText(this, deleted ? "User deleted" : "User not found", Toast.LENGTH_SHORT).show();
            loadUsers();
        });
    }

    private void loadUsers() {
        Cursor c = dbHelper.getAllUsers();
        StringBuilder sb = new StringBuilder();
        while (c.moveToNext()) {
            sb.append("Name: ").append(c.getString(c.getColumnIndexOrThrow("name"))).append("\n");
            sb.append("Email: ").append(c.getString(c.getColumnIndexOrThrow("email"))).append("\n");
            sb.append("Role: ").append(c.getString(c.getColumnIndexOrThrow("role"))).append("\n\n");
        }
        c.close();
        tvUsers.setText(sb.length() > 0 ? sb.toString() : "No users found.");
    }
}
