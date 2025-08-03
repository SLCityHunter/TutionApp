package com.example.tutionapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class AssignMarksActivity extends AppCompatActivity {

    private Spinner spinnerStudentEmail;
    private EditText editClassName;
    private EditText editMark;
    private Button btnAssign;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_marks);

        // 1) bind views
        spinnerStudentEmail = findViewById(R.id.spinnerStudentEmail);
        editClassName       = findViewById(R.id.editClassName);
        editMark            = findViewById(R.id.editMark);
        btnAssign           = findViewById(R.id.btnAssignMark);

        // 2) init DB helper
        db = new DBHelper(this);

        // 3) populate student-email spinner
        loadStudentEmails();

        // 4) button click → assign mark
        btnAssign.setOnClickListener(v -> assignMark());
    }

    private void loadStudentEmails() {
        List<String> emails = db.getAllStudentEmails();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                emails
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStudentEmail.setAdapter(adapter);
    }

    private void assignMark() {
        String email     = spinnerStudentEmail.getSelectedItem() != null
                ? spinnerStudentEmail.getSelectedItem().toString()
                : "";
        String className = editClassName.getText().toString().trim();
        String markStr   = editMark.getText().toString().trim();

        // validate inputs
        if (email.isEmpty() || className.isEmpty() || markStr.isEmpty()) {
            Toast.makeText(this,
                    "Select student, enter class name, and enter a mark",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        int mark;
        try {
            mark = Integer.parseInt(markStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this,
                    "Please enter a valid numeric mark",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // insert into DB
        boolean success = db.insertMark(email, className, mark);
        Toast.makeText(this,
                success ? "Mark assigned successfully!" : "Failed to assign mark",
                Toast.LENGTH_SHORT
        ).show();

        // clear on success
        if (success) {
            editClassName.setText("");
            editMark.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
