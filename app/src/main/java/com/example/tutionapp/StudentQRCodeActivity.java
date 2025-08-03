package com.example.tutionapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class StudentQRCodeActivity extends AppCompatActivity {

    private ImageView ivQr;
    private ListView lvHistory;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> attendanceList = new ArrayList<>();
    private DBHelper db;
    private String email;
    private static final String PREFS = "app_prefs";
    private static final String KEY   = "logged_email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_qr);

        ivQr   = findViewById(R.id.ivQr);
        lvHistory = findViewById(R.id.lvAttendanceHistory);
//        Button btnSend = findViewById(R.id.btnSendToScanner);
        Button btnSave = findViewById(R.id.btnSaveToGallery);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        email = prefs.getString(KEY, "no_email@example.com");

        db = new DBHelper(this);
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, attendanceList);
        lvHistory.setAdapter(adapter);

        generateQrCode();
        loadAttendanceHistory();

//        btnSend.setOnClickListener(v -> {
//            Intent i = new Intent(this, TeacherScanAttendanceActivity.class);
//            i.putExtra("email_to_scan", email);
//            startActivity(i);
//        });

        btnSave.setOnClickListener(v -> saveImageToGallery());
    }

    private void generateQrCode() {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap qrBmp = encoder.encodeBitmap(
                    email, BarcodeFormat.QR_CODE, 400, 400
            );
            ivQr.setImageBitmap(qrBmp);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "QR generation failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadAttendanceHistory() {
        attendanceList.clear();
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

        if (attendanceList.isEmpty()) {
            attendanceList.add("No attendance records found.");
        }
        adapter.notifyDataSetChanged();
    }

    private void saveImageToGallery() {
        BitmapDrawable drawable = (BitmapDrawable) ivQr.getDrawable();
        if (drawable == null) {
            Toast.makeText(this, "No QR to save", Toast.LENGTH_SHORT).show();
            return;
        }
        Bitmap bitmap = drawable.getBitmap();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME,
                "QR_" + System.currentTimeMillis() + ".png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/QRcodes");

        Uri uri = getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        try (OutputStream out = getContentResolver().openOutputStream(uri)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Toast.makeText(this, "Saved to Gallery", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Save failed", Toast.LENGTH_SHORT).show();
        }
    }
}
