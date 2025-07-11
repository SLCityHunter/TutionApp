package com.example.tutionapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ManageTuitionClassesActivity extends AppCompatActivity {

    private ListView lvTuitionClasses;
    private Button btnAddTuitionClass;
    private DBHelper db;
    private SharedPreferences prefs;
    private ArrayList<ClassItem> classList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private static final String PREFS = "app_prefs";
    private static final String KEY = "logged_email";

    @Override
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_manage_tuition_classes);

        lvTuitionClasses   = findViewById(R.id.lvTuitionClasses);
        btnAddTuitionClass = findViewById(R.id.btnAddTuitionClass);
        db    = new DBHelper(this);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        lvTuitionClasses.setAdapter(adapter);

        btnAddTuitionClass.setOnClickListener(v -> showDialog(null));

        lvTuitionClasses.setOnItemLongClickListener((p, v, pos, id) -> {
            showDialog(classList.get(pos));
            return true;
        });

        loadClasses();
    }

    private void loadClasses() {
        adapter.clear();
        classList.clear();
        String email = prefs.getString(KEY, "");
        Cursor c = db.getTuitionClassesByTeacher(email);
        while (c.moveToNext()) {
            int id       = c.getInt(c.getColumnIndexOrThrow("id"));
            String name  = c.getString(c.getColumnIndexOrThrow("class_name"));
            String desc  = c.getString(c.getColumnIndexOrThrow("description"));
            classList.add(new ClassItem(id, name, desc));
            adapter.add("📘 " + name + "\n📝 " + desc);
        }
        c.close();
    }

    private void showDialog(ClassItem cls) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle(cls == null ? "Add Tuition Class" : "Edit Class");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        EditText etName = new EditText(this);
        etName.setHint("Class Name");
        layout.addView(etName);

        EditText etDesc = new EditText(this);
        etDesc.setHint("Description");
        etDesc.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        layout.addView(etDesc);

        if (cls != null) {
            etName.setText(cls.name);
            etDesc.setText(cls.desc);
        }

        b.setView(layout);

        b.setPositiveButton("Save", (d, w) -> {
            String n    = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String email = prefs.getString(KEY, "");
            if (!n.isEmpty()) {
                if (cls == null) {
                    db.insertTuitionClass(email, n, desc);
                } else {
                    db.updateTuitionClass(cls.id, n, desc);
                }
                loadClasses();
            } else {
                Toast.makeText(this, "Class name required", Toast.LENGTH_SHORT).show();
            }
        });

        if (cls != null) {
            b.setNegativeButton("Delete", (d, w) -> {
                db.deleteTuitionClass(cls.id);
                loadClasses();
            });
        }

        b.show();
    }

    // 🔁 Public helper to get teacher's class list if needed elsewhere
    public static ArrayList<String> getTeacherClassNames(Context ctx) {
        ArrayList<String> result = new ArrayList<>();
        SharedPreferences prefs = ctx.getSharedPreferences(PREFS, MODE_PRIVATE);
        String email = prefs.getString(KEY, "");
        DBHelper db = new DBHelper(ctx);
        Cursor c = db.getTuitionClassesByTeacher(email);
        while (c.moveToNext()) {
            result.add(c.getString(c.getColumnIndexOrThrow("class_name")));
        }
        c.close();
        return result;
    }

    private static class ClassItem {
        int id;
        String name, desc;
        ClassItem(int i, String n, String d) {
            id = i; name = n; desc = d;
        }
    }
}
