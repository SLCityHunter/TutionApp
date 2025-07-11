package com.example.tutionapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ManageMaterialsActivity extends AppCompatActivity {
    private static final int REQ_PICK_MATERIAL  = 200;
    private static final int REQ_PICK_ASSIGNMENT = 201;

    private Spinner spinner;
    private ListView lv;
    private Button btnAdd, btnAddAssignment;
    private DBHelper db;
    private ArrayAdapter<String> classAdapter, matAdapter;
    private ArrayList<Integer> classIds = new ArrayList<>();
    private ArrayList<Material> materials = new ArrayList<>();

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_manage_materials);

        spinner          = findViewById(R.id.spinnerClasses);
        lv               = findViewById(R.id.lvMaterials);
        btnAdd           = findViewById(R.id.btnAddMaterial);
        btnAddAssignment = findViewById(R.id.btnAddAssignment);  // 🆕 Get the new button
        db               = new DBHelper(this);

        classAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(classAdapter);

        matAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1);
        lv.setAdapter(matAdapter);

        loadClasses();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id){
                if (!classIds.isEmpty() && pos < classIds.size()) {
                    loadMaterials(classIds.get(pos));
                } else {
                    Toast.makeText(ManageMaterialsActivity.this,
                            "No class selected or no class data available", Toast.LENGTH_SHORT).show();
                }
            }
            public void onNothingSelected(AdapterView<?> p){}
        });

        btnAdd.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("*/*");
            startActivityForResult(i, REQ_PICK_MATERIAL);
        });

        btnAddAssignment.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("*/*");
            startActivityForResult(i, REQ_PICK_ASSIGNMENT);
        });

        lv.setOnItemLongClickListener((p, v, pos, id) -> {
            Material m = materials.get(pos);
            db.deleteMaterial(m.id);
            loadMaterials(m.classId);
            Toast.makeText(this, "Material deleted", Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private void loadClasses() {
        classIds.clear();
        classAdapter.clear();

        Cursor c = db.getReadableDatabase().query(
                DBHelper.TABLE_TUITION_CLASSES,
                new String[]{"id", "class_name"},
                null, null, null, null,
                "class_name ASC"
        );
        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndexOrThrow("id"));
            String name = c.getString(c.getColumnIndexOrThrow("class_name"));
            classIds.add(id);
            classAdapter.add(name);
        }
        c.close();

        if (classIds.isEmpty()) {
            classAdapter.add("No classes found");
            Toast.makeText(this, "No registered classes in database!", Toast.LENGTH_SHORT).show();
        }
        classAdapter.notifyDataSetChanged();
    }

    private void loadMaterials(int classId) {
        materials.clear();
        matAdapter.clear();

        Cursor c = db.getMaterialsByClass(classId);
        while (c.moveToNext()) {
            int id     = c.getInt(c.getColumnIndexOrThrow("id"));
            String fn  = c.getString(c.getColumnIndexOrThrow("file_name"));
            String uri = c.getString(c.getColumnIndexOrThrow("file_uri"));
            materials.add(new Material(id, classId, fn, uri));
            matAdapter.add("📘 " + fn);
        }
        c.close();

        if (materials.isEmpty()) matAdapter.add("No materials uploaded yet.");
        matAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req, res, data);
        if (res == RESULT_OK && data != null) {
            Uri uri = data.getData();
            int classPos = spinner.getSelectedItemPosition();

            if (!classIds.isEmpty() && classPos < classIds.size()) {
                int classId = classIds.get(classPos);
                String fn = getFileNameFromUri(uri);

                boolean success = false;

                if (req == REQ_PICK_MATERIAL) {
                    success = db.insertMaterial(classId, fn, uri.toString());
                    Toast.makeText(this, success ? "Material uploaded" : "Material upload failed", Toast.LENGTH_SHORT).show();
                    if (success) loadMaterials(classId);
                } else if (req == REQ_PICK_ASSIGNMENT) {
                    success = db.insertAssignment(classId, fn, uri.toString());
                    Toast.makeText(this, success ? "Assignment uploaded" : "Assignment upload failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Invalid class selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String result = "file";
        Cursor c = getContentResolver().query(uri, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            int idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (idx >= 0) result = c.getString(idx);
            c.close();
        }
        return result;
    }

    private static class Material {
        int id, classId;
        String fileName, uri;
        Material(int i, int c, String f, String u){
            id=i; classId=c; fileName=f; uri=u;
        }
    }
}
