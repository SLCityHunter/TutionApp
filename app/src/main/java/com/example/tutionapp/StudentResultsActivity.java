//package com.example.tutionapp;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//import java.util.List;
//
//public class StudentResultsActivity extends AppCompatActivity {
//
//    private ListView listResults;
//    private TextView lblNoResults;
//    private DBHelper db;
//    private String studentEmail;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_student_results);
//
//        // bind views
//        listResults  = findViewById(R.id.listResults);
//        lblNoResults = findViewById(R.id.lblNoResults);
//
//        // init DB helper
//        db = new DBHelper(this);
//
//        // get the email passed in
//        Intent i = getIntent();
//        studentEmail = i.getStringExtra("email");
//        if (studentEmail == null) {
//            finish();
//            return;
//        }
//
//        // fetch marks
//        List<MarkItem> marks = db.getMarksForStudent(studentEmail);
//        if (marks.isEmpty()) {
//            lblNoResults.setText("No results available.");
//            lblNoResults.setVisibility(View.VISIBLE);
//        } else {
//            lblNoResults.setVisibility(View.GONE);
//            ArrayAdapter<MarkItem> adapter = new ArrayAdapter<>(
//                    this,
//                    android.R.layout.simple_list_item_1,
//                    marks
//            );
//            listResults.setAdapter(adapter);
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        db.close();
//        super.onDestroy();
//    }
//}
