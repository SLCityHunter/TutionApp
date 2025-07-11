package com.example.tutionapp;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class AssignCoursesActivity extends AppCompatActivity {

    private Spinner spinnerStudent, spinnerClass;
    private EditText etStudentEmail, etClassName;
    private Button btnAssign;
    private TextView tvAssignedClasses;
    private DBHelper dbHelper;
    private ArrayList<String> studentEmails = new ArrayList<>();
    private ArrayList<String> classNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_courses);

        spinnerStudent     = findViewById(R.id.spinnerStudent);
        spinnerClass       = findViewById(R.id.spinnerClass);
        etStudentEmail     = findViewById(R.id.etStudentEmail);
        etClassName        = findViewById(R.id.etClassName);
        btnAssign          = findViewById(R.id.btnAssign);
        tvAssignedClasses  = findViewById(R.id.tvAssignedClasses);
        dbHelper           = new DBHelper(this);

        loadStudentEmails();
        loadClassNames();
        loadAssignedClasses();

        btnAssign.setOnClickListener(v -> {
            String emailInput = etStudentEmail.getText().toString().trim();
            String classInput = etClassName.getText().toString().trim();

            String email = !emailInput.isEmpty() ? emailInput : spinnerStudent.getSelectedItem().toString();
            String className = !classInput.isEmpty() ? classInput : spinnerClass.getSelectedItem().toString();

            boolean ok = dbHelper.assignClass(email, className);
            Toast.makeText(this, ok ? "Class assigned" : "Failed to assign", Toast.LENGTH_SHORT).show();
            loadAssignedClasses();
        });
    }

    private void loadStudentEmails() {
        studentEmails.clear();
        Cursor c = dbHelper.getAllUsers();
        while (c.moveToNext()) {
            String role = c.getString(c.getColumnIndexOrThrow("role"));
            if ("student".equalsIgnoreCase(role)) {
                studentEmails.add(c.getString(c.getColumnIndexOrThrow("email")));
            }
        }
        c.close();
        if (studentEmails.isEmpty()) studentEmails.add("No students found");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, studentEmails);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStudent.setAdapter(adapter);
    }

    private void loadClassNames() {
        classNames.clear();
        Cursor c = dbHelper.getAllTuitionClassNames();

        while (c.moveToNext()) {
            String className = c.getString(c.getColumnIndexOrThrow("class_name"));
            if (!classNames.contains(className)) {
                classNames.add(className);
            }
        }
        c.close();
        if (classNames.isEmpty()) {
            classNames.add("Math");
            classNames.add("Science");
            classNames.add("English");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(adapter);
    }

    private void loadAssignedClasses() {
        Cursor c = dbHelper.getAllAssignedClasses();
        StringBuilder sb = new StringBuilder();
        while (c.moveToNext()) {
            sb.append("Student: ").append(c.getString(c.getColumnIndexOrThrow("email"))).append("\n");
            sb.append("Class: ").append(c.getString(c.getColumnIndexOrThrow("class_name"))).append("\n\n");
        }
        c.close();
        tvAssignedClasses.setText(sb.length() > 0 ? sb.toString() : "No classes assigned yet.");
    }
}
