package com.example.tutionapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ManageUsersActivity extends AppCompatActivity {

    private Button btnAddUser;
    private TextView tvUsers;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        btnAddUser = findViewById(R.id.btnAddUser);
        tvUsers    = findViewById(R.id.tvUsers);
        dbHelper   = new DBHelper(this);

        loadUsers();

        btnAddUser.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });

    }

    private void loadUsers() {
        Cursor c = dbHelper.getAllUsers();
        StringBuilder sb = new StringBuilder();

        while (c.moveToNext()) {
            String name  = c.getString(c.getColumnIndexOrThrow("name"));
            String email = c.getString(c.getColumnIndexOrThrow("email"));
            String role  = c.getString(c.getColumnIndexOrThrow("role"));

            sb.append("Name: ").append(name).append("\n");
            sb.append("Email: ").append(email).append("\n");
            sb.append("Role: ").append(role).append("\n\n");
        }

        c.close();
        tvUsers.setText(sb.length() > 0 ? sb.toString() : "No users found.");
    }
}
