package com.example.tutionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class TeacherHomeActivity extends AppCompatActivity {

    private Button btnManageCourses, btnViewStudents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        btnManageCourses = findViewById(R.id.btnManageCourses);
        btnViewStudents  = findViewById(R.id.btnViewStudents);

        // TODO: implement screens for these actions
        btnManageCourses.setOnClickListener(v ->
                startActivity(new Intent(this, AssignCoursesActivity.class))
        );

        btnViewStudents.setOnClickListener(v ->
                startActivity(new Intent(this, ManageUsersActivity.class))
        );
    }
}
