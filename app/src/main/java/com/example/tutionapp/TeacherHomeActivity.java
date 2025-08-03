package com.example.tutionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class TeacherHomeActivity extends AppCompatActivity {

    private Button btnManageTuitionClasses, btnManageMaterials, btnViewStudents, btnScanAttendance, btnAssignStudents, btnNotify, btnAssignMarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        btnManageTuitionClasses = findViewById(R.id.btnManageTuitionClasses);
        btnManageMaterials      = findViewById(R.id.btnManageMaterials);
        btnViewStudents         = findViewById(R.id.btnViewStudents);
        btnScanAttendance       = findViewById(R.id.btnScanAttendance);
        btnAssignStudents = findViewById(R.id.btnAssignStudents);
        btnNotify = findViewById(R.id.btnSendNotification);
        btnAssignMarks = findViewById(R.id.btnAssignMarks);

        btnAssignMarks.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherHomeActivity.this, AssignMarksActivity.class);
            startActivity(intent);
        });


        btnNotify.setOnClickListener(v -> {
            Intent i = new Intent(this, SendNotificationActivity.class);
            startActivity(i);
        });


        btnAssignStudents.setOnClickListener(v ->
                startActivity(new Intent(this, AssignCoursesActivity.class))
        );


        btnManageTuitionClasses.setOnClickListener(v ->
                startActivity(new Intent(this, ManageTuitionClassesActivity.class))
        );

        btnManageMaterials.setOnClickListener(v ->
                startActivity(new Intent(this, ManageMaterialsActivity.class))
        );

        btnViewStudents.setOnClickListener(v ->
                startActivity(new Intent(this, ViewAssignedStudentsActivity.class))
        );

        btnScanAttendance.setOnClickListener(v ->
                startActivity(new Intent(this, TeacherScanAttendanceActivity.class))
        );
    }
}
