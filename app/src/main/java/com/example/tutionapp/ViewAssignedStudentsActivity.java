package com.example.tutionapp;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ViewAssignedStudentsActivity extends AppCompatActivity {

    private ListView lvAssignedStudents;
    private DBHelper db;
    private ArrayList<String> assignedList;
    private ArrayAdapter<String> assignedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_assigned_students);

        lvAssignedStudents = findViewById(R.id.lvAssignedStudents);
        db = new DBHelper(this);

        assignedList = new ArrayList<>();
        assignedAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, assignedList);
        lvAssignedStudents.setAdapter(assignedAdapter);

        loadAssignedStudentsSorted();
    }

    private void loadAssignedStudentsSorted() {
        assignedList.clear();

        Cursor c = db.getAllAssignedClasses();
        HashMap<String, ArrayList<String>> classMap = new HashMap<>();

        while (c.moveToNext()) {
            String email = c.getString(c.getColumnIndexOrThrow("email"));
            String className = c.getString(c.getColumnIndexOrThrow("class_name"));

            if (!classMap.containsKey(className)) {
                classMap.put(className, new ArrayList<>());
            }
            classMap.get(className).add(email);
        }
        c.close();

        List<String> sortedClassNames = new ArrayList<>(classMap.keySet());
        Collections.sort(sortedClassNames);

        for (String className : sortedClassNames) {
            assignedList.add("📘 " + className);
            for (String email : classMap.get(className)) {
                assignedList.add("👨‍🎓 " + email);
            }
            assignedList.add(""); // empty line for visual separation
        }

        assignedAdapter.notifyDataSetChanged();
    }
}
