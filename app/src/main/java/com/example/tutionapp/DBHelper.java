package com.example.tutionapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME    = "tuition.db";
    private static final int    DB_VERSION = 2;  // bumped from 1 to 2

    public static final String TABLE_USER        = "users";
    private static final String COL_ID            = "id";
    private static final String COL_NAME          = "name";
    private static final String COL_EMAIL         = "email";
    private static final String COL_PASSWORD      = "password";
    private static final String COL_ROLE          = "role";
    private static final String COL_BIRTH_YEAR    = "birth_year";
    private static final String COL_PHONE         = "phone";
    private static final String COL_EMERGENCY_NAME  = "emergency_name";
    private static final String COL_EMERGENCY_PHONE = "emergency_phone";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the users table with all eight fields
        String createUsers =
                "CREATE TABLE " + TABLE_USER + " (" +
                        COL_ID              + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_NAME            + " TEXT, " +
                        COL_EMAIL           + " TEXT UNIQUE, " +
                        COL_PASSWORD        + " TEXT, " +
                        COL_ROLE            + " TEXT, " +
                        COL_BIRTH_YEAR      + " INTEGER, " +
                        COL_PHONE           + " TEXT, " +
                        COL_EMERGENCY_NAME  + " TEXT, " +
                        COL_EMERGENCY_PHONE + " TEXT" +
                        ")";
        db.execSQL(createUsers);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop & recreate the table on schema change
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    /**
     * Inserts a new user with all sign-up fields.
     * Returns true on success, false if email already exists or error.
     */
    public boolean insertUser(
            String name,
            String email,
            String pwd,
            String role,
            int birthYear,
            String phone,
            String emergencyName,
            String emergencyPhone
    ) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME,           name);
        cv.put(COL_EMAIL,          email);
        cv.put(COL_PASSWORD,       pwd);
        cv.put(COL_ROLE,           role);
        cv.put(COL_BIRTH_YEAR,     birthYear);
        cv.put(COL_PHONE,          phone);
        cv.put(COL_EMERGENCY_NAME, emergencyName);
        cv.put(COL_EMERGENCY_PHONE,emergencyPhone);

        long id = db.insert(TABLE_USER, null, cv);
        return id != -1;
    }

    /**
     * Retrieves a user row (all eight fields) by email.
     * Caller must close the returned Cursor.
     */
    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(
                TABLE_USER,
                new String[]{
                        COL_ID,
                        COL_NAME,
                        COL_EMAIL,
                        COL_PASSWORD,
                        COL_ROLE,
                        COL_BIRTH_YEAR,
                        COL_PHONE,
                        COL_EMERGENCY_NAME,
                        COL_EMERGENCY_PHONE
                },
                COL_EMAIL + "=?",
                new String[]{ email },
                null, null, null
        );
    }
    public Cursor getAllUsers() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_USER, null, null, null, null, null, "name ASC");
    }

}
