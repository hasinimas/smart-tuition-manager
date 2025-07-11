package com.example.smarttuitionmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TuitionDB";
    private static final int DATABASE_VERSION = 2;  // <-- increment this

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Leave empty if you don't want to create anything for now
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS USERS");
        db.execSQL("DROP TABLE IF EXISTS Subject");
        db.execSQL("DROP TABLE IF EXISTS STUDENT_COURSES");
        db.execSQL("DROP TABLE IF EXISTS ASSIGNMENTS");
        db.execSQL("DROP TABLE IF EXISTS ATTENDANCE");
        db.execSQL("DROP TABLE IF EXISTS RESULTS");
        db.execSQL("DROP TABLE IF EXISTS Subject_MATERIALS");
        db.execSQL("DROP TABLE IF EXISTS SUBMISSIONS");

        // Don't recreate anything now
    }
}
