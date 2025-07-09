package com.example.smarttuitionmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME ="TuitionDB";
    private static final int DATABASE_VERSION = 1;


    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
SQLiteDatabase db  = this.getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE USERS (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Name TEXT, " +
                "Email TEXT UNIQUE, " +
                "Password TEXT, " +
                "Role TEXT CHECK(Role IN ('Admin', 'Teacher', 'Student')))");

        db.execSQL("CREATE TABLE Subject (" +
                "Subject_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Subject_name TEXT, " +
                "teacher_id INTEGER, " +
                "FOREIGN KEY(teacher_id) REFERENCES USERS(user_id))");

        db.execSQL("CREATE TABLE STUDENT_COURSES (" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id INTEGER, " +
                "subject_id INTEGER, " +
                "FOREIGN KEY(student_id) REFERENCES USERS(user_id), " +
                "FOREIGN KEY(subject_id) REFERENCES Subject(Subject_id))");

        db.execSQL("CREATE TABLE ASSIGNMENTS (" +
                "assignment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Title TEXT, " +
                "Description TEXT, " +
                "Subject_id INTEGER, " +
                "Deadline TEXT, " +
                "FOREIGN KEY(Subject_id) REFERENCES Subject(Subject_id))");

        db.execSQL("CREATE TABLE ATTENDANCE (" +
                "attendance_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id INTEGER, " +
                "Subject_id INTEGER, " +
                "date TEXT, " +
                "status TEXT CHECK(status IN ('Present', 'Absent')), " +
                "FOREIGN KEY(student_id) REFERENCES USERS(user_id), " +
                "FOREIGN KEY(Subject_id) REFERENCES Subject(Subject_id))");

        db.execSQL("CREATE TABLE RESULTS (" +
                "result_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id INTEGER, " +
                "Subject_id INTEGER, " +
                "marks INTEGER, " +
                "remark TEXT, " +
                "FOREIGN KEY(student_id) REFERENCES USERS(user_id), " +
                "FOREIGN KEY(Subject_id) REFERENCES Subject(Subject_id))");

        db.execSQL("CREATE TABLE Subject_MATERIALS (" +
                "material_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Subject_id INTEGER, " +
                "title TEXT, " +
                "file_path TEXT, " +
                "FOREIGN KEY(Subject_id) REFERENCES Subject(Subject_id))");

        db.execSQL("CREATE TABLE SUBMISSIONS (" +
                "submission_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "assignment_id INTEGER, " +
                "student_id INTEGER, " +
                "file_path TEXT, " +
                "submitted_at TEXT, " +
                "FOREIGN KEY(assignment_id) REFERENCES ASSIGNMENTS(assignment_id), " +
                "FOREIGN KEY(student_id) REFERENCES USERS(user_id))");


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
        onCreate(db);

    }
}
