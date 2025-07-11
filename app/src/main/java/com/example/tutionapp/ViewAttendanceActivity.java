package com.example.tutionapp;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.*;

public class ViewAttendanceActivity extends AppCompatActivity {

    private ListView lvAttendance;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> attendanceList = new ArrayList<>();
    private DBHelper db;
    private SharedPreferences prefs;
    private static final String PREFS = "app_prefs";
    private static final String KEY = "logged_email";

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_view_attendance);

        lvAttendance = findViewById(R.id.lvAttendance);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, attendanceList);
        lvAttendance.setAdapter(adapter);

        db = new DBHelper(this);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        loadAttendance();
    }

    private void loadAttendance() {
        attendanceList.clear();
        String email = prefs.getString(KEY, "");

        Cursor c = db.getAllAttendance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

        while (c.moveToNext()) {
            String scannedEmail = c.getString(c.getColumnIndexOrThrow("email"));
            long timestamp = c.getLong(c.getColumnIndexOrThrow("timestamp"));

            if (scannedEmail.equalsIgnoreCase(email)) {
                String formatted = sdf.format(new Date(timestamp));
                attendanceList.add("🗓️ " + formatted);
            }
        }
        c.close();

        if (attendanceList.isEmpty()) attendanceList.add("No attendance records found.");
        adapter.notifyDataSetChanged();
    }
}
