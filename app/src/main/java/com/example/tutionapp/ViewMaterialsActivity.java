package com.example.tutionapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ViewMaterialsActivity extends AppCompatActivity {

    private Spinner spinnerClass;
    private ListView lvMaterials;
    private DBHelper db;
    private ArrayAdapter<String> classAdapter, fileAdapter;
    private ArrayList<String> classNames = new ArrayList<>();
    private ArrayList<FileItem> files = new ArrayList<>();

    private static final String PREFS = "app_prefs";
    private static final String KEY = "logged_email";

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_view_materials);

        spinnerClass = findViewById(R.id.spinnerClass);
        lvMaterials  = findViewById(R.id.lvMaterials);
        db           = new DBHelper(this);

        classAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classNames);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(classAdapter);

        fileAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        lvMaterials.setAdapter(fileAdapter);

        loadAssignedClasses();

        spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id){
                String className = classNames.get(pos);
                loadMaterials(className);
            }
            public void onNothingSelected(AdapterView<?> p){}
        });

        lvMaterials.setOnItemClickListener((p, v, pos, id) -> {
            Uri uri = Uri.parse(files.get(pos).uri);
            startActivity(new Intent(Intent.ACTION_VIEW).setData(uri));
        });
    }

    private void loadAssignedClasses() {
        classNames.clear();
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String email = prefs.getString(KEY, "");

        Cursor c = db.getAllAssignedClasses();
        while (c.moveToNext()) {
            String assignedEmail = c.getString(c.getColumnIndexOrThrow("email"));
            String className = c.getString(c.getColumnIndexOrThrow("class_name"));

            if (assignedEmail.equalsIgnoreCase(email) && !classNames.contains(className)) {
                classNames.add(className);
            }
        }
        c.close();
        if (classNames.isEmpty()) classNames.add("No classes found");
        classAdapter.notifyDataSetChanged();
    }

    private void loadMaterials(String className) {
        fileAdapter.clear();
        files.clear();
        int classId = db.getClassIdByName(className);
        Cursor c = db.getMaterialsByClass(classId);
        while (c.moveToNext()) {
            int id       = c.getInt(c.getColumnIndexOrThrow("id"));
            String name  = c.getString(c.getColumnIndexOrThrow("file_name"));
            String uri   = c.getString(c.getColumnIndexOrThrow("file_uri"));
            files.add(new FileItem(id, classId, name, uri));
            fileAdapter.add("📘 " + name);
        }
        c.close();
        if (files.isEmpty()) fileAdapter.add("No materials uploaded.");
    }

    private static class FileItem {
        int id, classId;
        String fileName, uri;
        FileItem(int i, int c, String f, String u){
            id=i; classId=c; fileName=f; uri=u;
        }
    }
}
