package com.example.tutionapp;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ReportsActivity extends AppCompatActivity {

    private TextView tvReport;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        tvReport = findViewById(R.id.tvReport);
        dbHelper = new DBHelper(this);

        loadClassSummaryReport();
    }

    private void loadClassSummaryReport() {
        Cursor c = dbHelper.getClassSummaryReport();
        StringBuilder sb = new StringBuilder();

        while (c.moveToNext()) {
            String className = c.getString(c.getColumnIndexOrThrow("class_name"));
            int count = c.getInt(c.getColumnIndexOrThrow("student_count"));
            sb.append("Class: ").append(className).append("\n");
            sb.append("Students Assigned: ").append(count).append("\n\n");
        }

        c.close();
        tvReport.setText(sb.length() > 0 ? sb.toString() : "No class assignments found.");
    }
}
