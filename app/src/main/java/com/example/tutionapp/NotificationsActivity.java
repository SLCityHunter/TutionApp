package com.example.tutionapp;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class NotificationsActivity extends AppCompatActivity {

    private ListView lvNotifications;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> notifications = new ArrayList<>();
    private DBHelper db;
    private String studentEmail;

    private static final String PREFS = "app_prefs";
    private static final String KEY   = "logged_email";

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_notifications);

        lvNotifications = findViewById(R.id.lvNotifications);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, notifications);
        lvNotifications.setAdapter(adapter);

        db = new DBHelper(this);
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        studentEmail = prefs.getString(KEY, "");

        loadNotifications();
    }

    private void loadNotifications() {
        notifications.clear();
        Cursor c = db.getNotificationsForStudent(studentEmail);

        while (c.moveToNext()) {
            String title = c.getString(c.getColumnIndexOrThrow("title"));
            String body  = c.getString(c.getColumnIndexOrThrow("message"));
            long time    = c.getLong(c.getColumnIndexOrThrow("timestamp"));

            String formattedTime = new Date(time).toString();
            notifications.add("📣 " + title + "\n" + body + "\n🕒 " + formattedTime);
        }
        c.close();

        if (notifications.isEmpty()) notifications.add("No notifications found.");
        adapter.notifyDataSetChanged();
    }
}
