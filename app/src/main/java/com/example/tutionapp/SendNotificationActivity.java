package com.example.tutionapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class SendNotificationActivity extends AppCompatActivity {

    private EditText etTitle, etMessage, etRecipient;
    private Button btnSend;
    private DBHelper db;
    private String teacherEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);

        etTitle     = findViewById(R.id.etNotifyTitle);
        etMessage   = findViewById(R.id.etNotifyMessage);
        etRecipient = findViewById(R.id.etRecipientEmail);
        btnSend     = findViewById(R.id.btnSendNotification);
        db          = new DBHelper(this);

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        teacherEmail = prefs.getString("logged_email", "no_email@teacher.com");

        btnSend.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String msg   = etMessage.getText().toString().trim();
            String to    = etRecipient.getText().toString().trim();

            if (title.isEmpty() || msg.isEmpty() || to.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean sent = db.insertNotification(title, msg, to);
            if (sent) {
                Toast.makeText(this, "Notification sent to " + to, Toast.LENGTH_SHORT).show();
                etTitle.setText("");
                etMessage.setText("");
                etRecipient.setText("");
            } else {
                Toast.makeText(this, "Failed to send notification", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
