package com.example.tutionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminHomeActivity extends AppCompatActivity {


        private Button btnManageUsers, btnAssignCourses, btnViewReports;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin_home);

            btnManageUsers   = findViewById(R.id.btnManageUsers);
            btnAssignCourses = findViewById(R.id.btnAssignCourses);
            btnViewReports   = findViewById(R.id.btnViewReports);

            btnManageUsers.setOnClickListener(v ->
                    startActivity(new Intent(this, ManageUsersActivity.class))
            );
            btnAssignCourses.setOnClickListener(v ->
                    startActivity(new Intent(this, AssignCoursesActivity.class))
            );
            btnViewReports.setOnClickListener(v ->
                    startActivity(new Intent(this, ReportsActivity.class))
            );
        }
    }


