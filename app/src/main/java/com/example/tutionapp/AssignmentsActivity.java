package com.example.tutionapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class AssignmentsActivity extends AppCompatActivity {
    private static final int REQ_PICK_FILE = 300;
    private Spinner spinnerClass;
    private Button btnUpload;
    private ListView lvAssignments, lvTeacherAssignments;

    private DBHelper db;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> assignmentAdapter, teacherAssignmentAdapter;

    private ArrayList<Integer> classIds = new ArrayList<>();
    private ArrayList<String> submittedAssignments = new ArrayList<>();
    private ArrayList<String> teacherAssignments = new ArrayList<>();

    private String studentEmail;

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_assignments);

        spinnerClass = findViewById(R.id.spinnerClass);
        btnUpload = findViewById(R.id.btnUploadAssignment);
        lvAssignments = findViewById(R.id.lvAssignments);
        lvTeacherAssignments = findViewById(R.id.lvTeacherAssignments);

        db = new DBHelper(this);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(adapter);

        assignmentAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, submittedAssignments);
        lvAssignments.setAdapter(assignmentAdapter);

        teacherAssignmentAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, teacherAssignments);
        lvTeacherAssignments.setAdapter(teacherAssignmentAdapter);

        studentEmail = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getString("logged_email", "unknown@student.com");

        loadClasses();
        loadSubmittedAssignments();
        loadTeacherAssignments();

        btnUpload.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("*/*");
            startActivityForResult(i, REQ_PICK_FILE);
        });
    }

    private void loadClasses() {
        classIds.clear();
        adapter.clear();

        Cursor c = db.getAllAssignedClasses();
        while (c.moveToNext()) {
            String email = c.getString(c.getColumnIndexOrThrow("email"));
            String name = c.getString(c.getColumnIndexOrThrow("class_name"));
            if (email.equalsIgnoreCase(studentEmail)) {
                adapter.add(name);
                classIds.add(adapter.getCount() - 1); // index-based reference
            }
        }
        c.close();

        if (adapter.isEmpty()) {
            adapter.add("No classes assigned");
            Toast.makeText(this, "No classes found for your account", Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged();
    }

    private void loadSubmittedAssignments() {
        submittedAssignments.clear();
        Cursor c = db.getStudentAssignments(studentEmail);
        while (c.moveToNext()) {
            String fileName = c.getString(c.getColumnIndexOrThrow("file_name"));
            long time = c.getLong(c.getColumnIndexOrThrow("uploaded_at"));
            submittedAssignments.add("📎 " + fileName + "\n🕒 " + new Date(time).toString());
        }
        c.close();

        if (submittedAssignments.isEmpty()) {
            submittedAssignments.add("No assignments submitted yet.");
        }
        assignmentAdapter.notifyDataSetChanged();
    }

    private void loadTeacherAssignments() {
        teacherAssignments.clear();
        Cursor c = db.getAllTeacherAssignments();
        while (c.moveToNext()) {
            String fileName = c.getString(c.getColumnIndexOrThrow("file_name"));
            long time = c.getLong(c.getColumnIndexOrThrow("uploaded_at"));
            teacherAssignments.add("📝 " + fileName + "\n🕒 " + new Date(time).toString());
        }
        c.close();

        if (teacherAssignments.isEmpty()) {
            teacherAssignments.add("No assignments uploaded by teacher.");
        }
        teacherAssignmentAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req, res, data);
        if (req == REQ_PICK_FILE && res == RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            String fileName = getFileName(fileUri);
            int pos = spinnerClass.getSelectedItemPosition();

            if (!classIds.isEmpty() && pos < classIds.size()) {
                int classId = pos + 1;
                boolean inserted = db.insertStudentAssignment(
                        studentEmail, classId, fileName, fileUri.toString()
                );
                Toast.makeText(this, inserted ?
                        "Assignment submitted successfully" :
                        "Submission failed", Toast.LENGTH_SHORT).show();
                loadSubmittedAssignments();
            } else {
                Toast.makeText(this, "No valid class selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = "submission";
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            int idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (idx >= 0) result = c.getString(idx);
            c.close();
        }
        return result;
    }
}
