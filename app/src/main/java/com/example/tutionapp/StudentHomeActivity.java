package com.example.tutionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StudentHomeActivity extends AppCompatActivity {

    private Button btnAttendance, btnAssignments, btnMaterials, btnNotifications, btnMap, btnViewResults;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_student_home);

            btnAttendance    = findViewById(R.id.btnAttendance);
            btnAssignments   = findViewById(R.id.btnAssignments);
            btnMaterials     = findViewById(R.id.btnMaterials);
            btnNotifications = findViewById(R.id.btnNotifications);
            btnMap           = findViewById(R.id.btnStudentMap);
            btnViewResults = findViewById(R.id.btnViewResults);
            btnViewResults.setOnClickListener(v -> {
                // Launch the AllResultsActivity
                Intent intent = new Intent(this, AllResultsActivity.class);
                startActivity(intent);
            });

            btnMap.setOnClickListener(v ->
                startActivity(new Intent(this, StudentMapActivity.class))

            );
            btnAttendance.setOnClickListener(v ->
                    startActivity(new Intent(this, StudentQRCodeActivity.class))
            );
            btnAssignments.setOnClickListener(v ->
                    startActivity(new Intent(this, AssignmentsActivity.class))
            );
            btnMaterials.setOnClickListener(v ->
                    startActivity(new Intent(this, MaterialsActivity.class))
            );
            btnNotifications.setOnClickListener(v ->
                    startActivity(new Intent(this, NotificationsActivity.class))
            );
        }
    }