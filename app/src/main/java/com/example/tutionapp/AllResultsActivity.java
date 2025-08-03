package com.example.tutionapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class AllResultsActivity extends AppCompatActivity {

    private DBHelper db;
    private ListView listResults;
    private TextView txtEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_results);

        // 1) bind views
        listResults = findViewById(R.id.listResults);
        txtEmpty    = findViewById(R.id.txtEmpty);

        // 2) init DB helper
        db = new DBHelper(this);

        // 3) load all marks
        List<String> rows = db.getAllMarks();

        if (rows.isEmpty()) {
            // no data
            txtEmpty.setVisibility(View.VISIBLE);
        } else {
            // bind to ListView
            txtEmpty.setVisibility(View.GONE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_list_item_1,
                    rows
            );
            listResults.setAdapter(adapter);
        }
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
