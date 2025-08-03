package com.example.tutionapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME    = "tuition.db";
    private static final int    DB_VERSION = 9;

    // === USERS TABLE ===
    public static final String TABLE_USERS           = "users";
    private static final String COL_USER_ID          = "id";
    private static final String COL_USER_NAME        = "name";
    private static final String COL_USER_EMAIL       = "email";
    private static final String COL_USER_PASSWORD    = "password";
    private static final String COL_USER_ROLE        = "role";
    private static final String COL_USER_BIRTH_YEAR  = "birth_year";
    private static final String COL_USER_PHONE       = "phone";
    private static final String COL_USER_EMER_NAME   = "emergency_name";
    private static final String COL_USER_EMER_PHONE  = "emergency_phone";

    // === ASSIGNED CLASSES ===
    public static final String TABLE_ASSIGNED_CLASSES = "assigned_classes";

    // === ATTENDANCE ===
    public static final String TABLE_ATTENDANCE       = "attendance";

    // === TUITION CLASSES ===
    public static final String TABLE_TUITION_CLASSES  = "tuition_classes";

    // === MATERIALS ===
    public static final String TABLE_MATERIALS        = "materials";
    public static final String TABLE_ASSIGNMENTS = "assignments";

    public static final String TABLE_NOTIFICATIONS = "notifications";



    public static final String TABLE_MARKS      = "marks";
    private static final String COL_MARK_ID    = "id";
    private static final String COL_MARK_EMAIL = "student_email";
    private static final String COL_MARK_CLASS = "class_name";
    private static final String COL_MARK_VALUE = "mark";





    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1) users
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + " (" +
                        COL_USER_ID        + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_USER_NAME      + " TEXT, " +
                        COL_USER_EMAIL     + " TEXT UNIQUE, " +
                        COL_USER_PASSWORD  + " TEXT, " +
                        COL_USER_ROLE      + " TEXT, " +
                        COL_USER_BIRTH_YEAR+ " INTEGER, " +
                        COL_USER_PHONE     + " TEXT, " +
                        COL_USER_EMER_NAME + " TEXT, " +
                        COL_USER_EMER_PHONE+ " TEXT" +
                        ")"
        );

        // 2) assigned_classes
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_ASSIGNED_CLASSES + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "email TEXT, " +
                        "class_name TEXT" +
                        ")"
        );

        // 3) attendance
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_ATTENDANCE + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "email TEXT, " +
                        "timestamp INTEGER" +
                        ")"
        );

        // 4) tuition_classes
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_TUITION_CLASSES + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "teacher_email TEXT, " +
                        "class_name TEXT, " +
                        "description TEXT" +
                        ")"
        );

        // 5) materials
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_MATERIALS + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "class_id INTEGER, " +
                        "file_name TEXT, " +
                        "file_uri TEXT, " +
                        "uploaded_at INTEGER" +
                        ")"
        );
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ASSIGNMENTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "class_id INTEGER, " +
                "file_name TEXT, " +
                "file_uri TEXT, " +
                "uploaded_at INTEGER)");

        db.execSQL("CREATE TABLE IF NOT EXISTS student_assignments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_email TEXT, " +
                "class_id INTEGER, " +
                "file_name TEXT, " +
                "file_uri TEXT, " +
                "uploaded_at INTEGER)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATIONS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "message TEXT, " +
                "recipient_email TEXT, " +
                "timestamp INTEGER)");


        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_MARKS + " (" +
                        COL_MARK_ID    + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_MARK_EMAIL + " TEXT    NOT NULL, " +
                        COL_MARK_CLASS + " TEXT    NOT NULL, " +
                        COL_MARK_VALUE + " INTEGER NOT NULL" +
                        ")"
        );


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSIGNED_CLASSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTENDANCE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TUITION_CLASSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MATERIALS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSIGNMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MARKS);

        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + TABLE_MARKS + " (" +
                        COL_MARK_ID    + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_MARK_EMAIL + " TEXT    NOT NULL, " +
                        COL_MARK_CLASS + " TEXT    NOT NULL, " +
                        COL_MARK_VALUE + " INTEGER NOT NULL" +
                        ")"
        );
        onCreate(db);
    }

    // ==== USER CRUD ====

    public boolean insertUser(String name, String email, String pwd, String role,
                              int birthYear, String phone, String emerName, String emerPhone) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_NAME, name);
        cv.put(COL_USER_EMAIL, email);
        cv.put(COL_USER_PASSWORD, pwd);
        cv.put(COL_USER_ROLE, role);
        cv.put(COL_USER_BIRTH_YEAR, birthYear);
        cv.put(COL_USER_PHONE, phone);
        cv.put(COL_USER_EMER_NAME, emerName);
        cv.put(COL_USER_EMER_PHONE, emerPhone);
        return db.insert(TABLE_USERS, null, cv) != -1;
    }

    public Cursor getUserByEmail(String email) {
        return getReadableDatabase().query(
                TABLE_USERS, null,
                COL_USER_EMAIL + "=?",
                new String[]{ email },
                null, null, null
        );
    }

    public Cursor getAllUsers() {
        return getReadableDatabase().query(
                TABLE_USERS, null,
                null, null,
                null, null,
                COL_USER_NAME + " ASC"
        );
    }

    public boolean updateUser(String email, String name, String pwd, String role,
                              int birthYear, String phone, String emerName, String emerPhone) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_NAME, name);
        cv.put(COL_USER_PASSWORD, pwd);
        cv.put(COL_USER_ROLE, role);
        cv.put(COL_USER_BIRTH_YEAR, birthYear);
        cv.put(COL_USER_PHONE, phone);
        cv.put(COL_USER_EMER_NAME, emerName);
        cv.put(COL_USER_EMER_PHONE, emerPhone);
        return db.update(TABLE_USERS, cv, COL_USER_EMAIL + "=?",
                new String[]{ email }) > 0;
    }

    public boolean deleteUserByEmail(String email) {
        return getWritableDatabase().delete(
                TABLE_USERS, COL_USER_EMAIL + "=?",
                new String[]{ email }) > 0;
    }

    // ==== CLASS ASSIGNMENTS ====

    public boolean assignClass(String email, String className) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("email", email);
        cv.put("class_name", className);
        return db.insert(TABLE_ASSIGNED_CLASSES, null, cv) != -1;
    }

    public Cursor getAllAssignedClasses() {
        return getReadableDatabase().query(
                TABLE_ASSIGNED_CLASSES,
                null, null, null,
                null, null,
                "email ASC"
        );
    }

    public Cursor getClassSummaryReport() {
        return getReadableDatabase().rawQuery(
                "SELECT class_name, COUNT(email) AS student_count " +
                        "FROM " + TABLE_ASSIGNED_CLASSES + " " +
                        "GROUP BY class_name " +
                        "ORDER BY class_name ASC", null
        );
    }

    // ==== ATTENDANCE ====

    public boolean markAttendance(String email) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("email", email);
        cv.put("timestamp", System.currentTimeMillis());
        return db.insert(TABLE_ATTENDANCE, null, cv) != -1;
    }

    public Cursor getAllAttendance() {
        return getReadableDatabase().query(
                TABLE_ATTENDANCE,
                new String[]{ "email", "timestamp" },
                null, null, null, null,
                "timestamp DESC"
        );
    }

    // ==== TUITION CLASSES CRUD ====

    public boolean insertTuitionClass(String teacherEmail, String className, String desc) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("teacher_email", teacherEmail);
        cv.put("class_name", className);
        cv.put("description", desc);
        return db.insert(TABLE_TUITION_CLASSES, null, cv) != -1;
    }

    public Cursor getTuitionClassesByTeacher(String teacherEmail) {
        return getReadableDatabase().query(
                TABLE_TUITION_CLASSES,
                null,
                "teacher_email=?",
                new String[]{ teacherEmail },
                null, null,
                "class_name ASC"
        );
    }
    public Cursor getAllTuitionClasses() {
        return getReadableDatabase().query(
                TABLE_TUITION_CLASSES,
                null,
                null, null,
                null, null,
                "class_name ASC"
        );
    }


    public boolean updateTuitionClass(int id, String className, String desc) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("class_name", className);
        cv.put("description", desc);
        return db.update(TABLE_TUITION_CLASSES, cv,
                "id=?", new String[]{ String.valueOf(id) }) > 0;
    }

    public boolean deleteTuitionClass(int id) {
        return getWritableDatabase().delete(
                TABLE_TUITION_CLASSES,
                "id=?", new String[]{ String.valueOf(id) }) > 0;
    }

    public int getClassIdByName(String className) {
        Cursor c = getReadableDatabase().query(
                TABLE_TUITION_CLASSES,
                new String[]{ "id" },
                "class_name=?",
                new String[]{ className },
                null, null, null
        );
        int id = -1;
        if (c.moveToFirst()) {
            id = c.getInt(c.getColumnIndexOrThrow("id"));
        }
        c.close();
        return id;
    }

    // ==== MATERIALS CRUD ====

    public boolean insertMaterial(int classId, String fileName, String fileUri) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("class_id", classId);
        cv.put("file_name", fileName);
        cv.put("file_uri", fileUri);
        cv.put("uploaded_at", System.currentTimeMillis());
        return db.insert(TABLE_MATERIALS, null, cv) != -1;
    }


    public Cursor getMaterialsByClass(int classId) {
        return getReadableDatabase().query(
                TABLE_MATERIALS, null,
                "class_id=?", new String[]{ String.valueOf(classId) },
                null, null,
                "uploaded_at DESC"
        );
    }

    public boolean deleteMaterial(int id) {
        return getWritableDatabase().delete(
                TABLE_MATERIALS,
                "id=?", new String[]{ String.valueOf(id) }) > 0;
    }
    public Cursor getAllTuitionClassNames() {
        return getReadableDatabase().query(
                TABLE_TUITION_CLASSES,
                new String[]{"class_name"},
                null, null, null, null,
                "class_name ASC"
        );
    }
    public boolean insertAssignment(int classId, String fileName, String fileUri) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("class_id", classId);
        cv.put("file_name", fileName);
        cv.put("file_uri", fileUri);
        cv.put("uploaded_at", System.currentTimeMillis());
        return db.insert(TABLE_ASSIGNMENTS, null, cv) != -1;
    }

    public Cursor getAssignmentsByClass(int classId) {
        return getReadableDatabase().query(TABLE_ASSIGNMENTS,
                null, "class_id=?", new String[]{String.valueOf(classId)},
                null, null, "uploaded_at DESC");
    }

    public boolean deleteAssignment(int id) {
        return getWritableDatabase().delete(TABLE_ASSIGNMENTS,
                "id=?", new String[]{String.valueOf(id)}) > 0;
    }
    public boolean insertStudentAssignment(String email, int classId, String fileName, String uri) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("student_email", email);
        cv.put("class_id", classId);
        cv.put("file_name", fileName);
        cv.put("file_uri", uri);
        cv.put("uploaded_at", System.currentTimeMillis());
        return db.insert("student_assignments", null, cv) != -1;
    }
    public Cursor getSubmissionsByClass(int classId) {
        return getReadableDatabase().query(
                "student_assignments",
                null,
                "class_id = ?",
                new String[]{String.valueOf(classId)},
                null, null,
                "uploaded_at DESC"
        );
    }
    public boolean insertNotification(String title, String message, String recipientEmail) {
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("message", message);
        cv.put("recipient_email", recipientEmail);
        cv.put("timestamp", System.currentTimeMillis());
        return getWritableDatabase().insert("notifications", null, cv) != -1;
    }
    public Cursor getNotificationsForStudent(String email) {
        return getReadableDatabase().query("notifications",
                null, "recipient_email = ?", new String[]{email},
                null, null, "timestamp DESC");
    }
    public Cursor getStudentAssignments(String email) {
        return getReadableDatabase().query(
                "student_assignments",
                null,
                "student_email = ?",
                new String[]{email},
                null, null,
                "uploaded_at DESC"
        );
    }
    public Cursor getAllTeacherAssignments() {
        return getReadableDatabase().query(
                TABLE_ASSIGNMENTS,
                null,
                null, null,
                null, null,
                "uploaded_at DESC"
        );
    }
    public boolean insertMark(String email, String className, int mark) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MARK_EMAIL, email);
        cv.put(COL_MARK_CLASS, className);
        cv.put(COL_MARK_VALUE, mark);
        long row = db.insert(TABLE_MARKS, null, cv);
        return row != -1;
    }
    public List<String> getAllStudentEmails() {
        List<String> emails = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                TABLE_USERS,
                new String[]{ COL_USER_EMAIL },
                "role = ?", new String[]{ "student" },
                null, null,
                COL_USER_EMAIL + " ASC"
        );
        if (c.moveToFirst()) {
            do {
                emails.add(c.getString(
                        c.getColumnIndexOrThrow(COL_USER_EMAIL)
                ));
            } while (c.moveToNext());
        }
        c.close();
        return emails;
    }

    /**
     * Returns all class_name values for the given teacher.
     */
    /**
     * Returns a list of all class_name values.
     */
    public List<String> getAllClassNames() {
        List<String> classes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(
                TABLE_ASSIGNED_CLASSES,           // table name
                new String[]{ "class_name" },     // columns to return
                null, null,                       // no WHERE clause
                null, null,                       // no GROUP BY / HAVING
                "class_name ASC"                  // ORDER BY
        );

        if (c.moveToFirst()) {
            int idx = c.getColumnIndexOrThrow("class_name");
            do {
                classes.add(c.getString(idx));
            } while (c.moveToNext());
        }
        c.close();
        return classes;
    }
    public List<String> getAllMarks() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(
                TABLE_MARKS,
                new String[]{ COL_MARK_EMAIL, COL_MARK_CLASS, COL_MARK_VALUE },
                null, null,    // no WHERE
                null, null,    // no GROUP BY / HAVING
                COL_MARK_EMAIL + " ASC"  // order by student_email
        );

        if (c.moveToFirst()) {
            int idxEmail = c.getColumnIndexOrThrow(COL_MARK_EMAIL);
            int idxClass = c.getColumnIndexOrThrow(COL_MARK_CLASS);
            int idxMark  = c.getColumnIndexOrThrow(COL_MARK_VALUE);

            do {
                String email = c.getString(idxEmail);
                String cls   = c.getString(idxClass);
                int    m     = c.getInt(idxMark);
                list.add(email + " → [" + cls + "] = " + m);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }


}
