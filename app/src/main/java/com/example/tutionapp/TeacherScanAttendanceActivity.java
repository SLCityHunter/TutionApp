package com.example.tutionapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.RGBLuminanceSource;
import java.io.InputStream;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import android.graphics.BitmapFactory;

public class TeacherScanAttendanceActivity extends AppCompatActivity {
    private static final int REQ_SCAN_CAMERA = 100;
    private static final int REQ_PICK_IMAGE  = 200;

    private TextView tvResult;
    private DBHelper  db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_scan);

        tvResult = findViewById(R.id.tvResult);
        db       = new DBHelper(this);

        Button btnScan   = findViewById(R.id.btnScan);
        Button btnUpload = findViewById(R.id.btnUploadQr);

        // 1) Live camera scan
        btnScan.setOnClickListener(v -> {
            new IntentIntegrator(this)
                    .setRequestCode(REQ_SCAN_CAMERA)
                    .setPrompt("Scan student QR")
                    .setBeepEnabled(true)
                    .initiateScan();
        });

        // 2) Pick a saved QR image
        btnUpload.setOnClickListener(v -> {
            Intent pick = new Intent(Intent.ACTION_GET_CONTENT);
            pick.setType("image/*");
            startActivityForResult(
                    Intent.createChooser(pick, "Select QR image"),
                    REQ_PICK_IMAGE
            );
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // A) Handle camera-based QR scan
        if (requestCode == REQ_SCAN_CAMERA) {
            IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
            if (result != null && result.getContents() != null) {
                handleScannedEmail(result.getContents());
            }
            return;
        }

        // B) Handle gallery image pick
        if (requestCode == REQ_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri == null) {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                Bitmap bmp = loadDownsampledBitmap(imageUri, 800);
                String email = decodeQRCodeWithHints(bmp);
                if (email != null) {
                    handleScannedEmail(email);
                } else {
                    Toast.makeText(this, "No QR code found in image", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to decode image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Load a downsampled Bitmap up to maxDim pixels
    private Bitmap loadDownsampledBitmap(Uri uri, int maxDim) throws Exception {
        // 1) Get image bounds
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        try (InputStream is = getContentResolver().openInputStream(uri)) {
            BitmapFactory.decodeStream(is, null, opts);
        }
        // 2) Calculate sample size
        int maxOriginal = Math.max(opts.outWidth, opts.outHeight);
        opts.inSampleSize = maxOriginal > maxDim
                ? Math.round((float) maxOriginal / maxDim)
                : 1;
        opts.inJustDecodeBounds = false;
        // 3) Decode actual bitmap
        try (InputStream is = getContentResolver().openInputStream(uri)) {
            return BitmapFactory.decodeStream(is, null, opts);
        }
    }

    // Decode QR with ZXing hints
    private String decodeQRCodeWithHints(Bitmap bmp) {
        int width  = bmp.getWidth();
        int height = bmp.getHeight();
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);

        LuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.POSSIBLE_FORMATS,
                Collections.singletonList(BarcodeFormat.QR_CODE));
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);

        try {
            Result result = new MultiFormatReader().decode(binaryBitmap, hints);
            return result.getText();
        } catch (Exception e) {
            return null;
        }
    }

    // Centralized attendance marking
    private void handleScannedEmail(String email) {
        tvResult.setText("Last scan: " + email);
        boolean ok = db.markAttendance(email);
        Toast.makeText(this,
                ok ? "Attendance marked" : "Marking failed",
                Toast.LENGTH_SHORT).show();
    }
}
