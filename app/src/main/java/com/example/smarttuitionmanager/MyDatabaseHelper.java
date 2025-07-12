package com.example.smarttuitionmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.content.ContentValues;
import android.database.Cursor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TuitionDB";
    private static final int DATABASE_VERSION = 3;  // <-- increment this

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Leave empty if you don't want to create anything for now

        // Student table
        db.execSQL("CREATE TABLE student (" +
                "s_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "first_name TEXT NOT NULL, " +
                "last_name TEXT NOT NULL, " +
                "grade TEXT NOT NULL, " +
                "phone_number TEXT, " +
                "guardian_tp TEXT, " +
                "qr_img TEXT, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL)");

        // Subject table
        // Subject table (updated with t_id as FK)
        db.execSQL("CREATE TABLE subject (" +
                "subject_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "t_id INTEGER, " +
                "FOREIGN KEY(t_id) REFERENCES teacher(t_id))");


        // Student-Subject mapping table (many-to-many)
        db.execSQL("CREATE TABLE student_subject (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id INTEGER NOT NULL, " +
                "subject_id INTEGER NOT NULL, " +
                "FOREIGN KEY(student_id) REFERENCES student(s_id), " +
                "FOREIGN KEY(subject_id) REFERENCES subject(subject_id))");

        db.execSQL("CREATE TABLE RESULTS (" +
                "result_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id INTEGER, " +
                "Subject_id INTEGER, " +
                "marks INTEGER, " +
                "remark TEXT, " +
                "FOREIGN KEY(student_id) REFERENCES student(s_id), " +
                "FOREIGN KEY(Subject_id) REFERENCES subject(subject_id))");

        // Subject_MATERIALS table
        db.execSQL("CREATE TABLE Subject_MATERIALS (" +
                "material_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Subject_id INTEGER, " +
                "title TEXT, " +
                "file_path TEXT, " +
                "FOREIGN KEY(Subject_id) REFERENCES subject(subject_id))");

        // ASSIGNMENTS table
        db.execSQL("CREATE TABLE ASSIGNMENTS (" +
                "assignment_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Title TEXT, " +
                "Description TEXT, " +
                "Subject_id INTEGER, " +
                "Deadline TEXT, " +
                "FOREIGN KEY(Subject_id) REFERENCES subject(subject_id))");

        //Teacher  table
        db.execSQL("CREATE TABLE TEACHER (" +
                "teacher_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "first_name TEXT, " +
                "last_name TEXT, " +
                "email INTEGER, " +
                "password TEXT)");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


        // Don't recreate anything now
    }

    // Insert a student and return the new student ID
    public long insertStudent(String firstName, String lastName, String grade, String phoneNumber, String guardianTP, String qrImg, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("grade", grade);
        values.put("phone_number", phoneNumber);
        values.put("guardian_tp", guardianTP);
        values.put("qr_img", qrImg);
        values.put("email", email);
        values.put("password", password);
        return db.insert("student", null, values);
    }

    // Insert a subject if not exists, return subject_id
    public long insertSubject(String subjectName) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Check if subject exists
        Cursor cursor = db.query("subject", new String[]{"subject_id"}, "name=?", new String[]{subjectName}, null, null, null);
        if (cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow("subject_id"));
            cursor.close();
            return id;
        } else {
            cursor.close();
            ContentValues values = new ContentValues();
            values.put("name", subjectName);
            return db.insert("subject", null, values);
        }
    }

    // Link a student and a subject
    public long insertStudentSubject(long studentId, long subjectId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("student_id", studentId);
        values.put("subject_id", subjectId);
        return db.insert("student_subject", null, values);
    }

    // Delete a student and their subject mappings
    public void deleteStudent(long studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("student_subject", "student_id=?", new String[]{String.valueOf(studentId)});
        db.delete("student", "s_id=?", new String[]{String.valueOf(studentId)});
    }

    // Update a student by s_id
    public void updateStudent(long studentId, String firstName, String lastName, String grade, String phoneNumber, String guardianTP, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("grade", grade);
        values.put("phone_number", phoneNumber);
        values.put("guardian_tp", guardianTP);
        values.put("email", email);
        values.put("password", password);
        db.update("student", values, "s_id=?", new String[]{String.valueOf(studentId)});
    }

    // Remove all subject mappings for a student
    public void removeAllSubjectsForStudent(long studentId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("student_subject", "student_id=?", new String[]{String.valueOf(studentId)});
    }
    public Cursor getAllStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM student", null);
    }

    public Cursor getAllSubjects() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM subject", null);
    }
    // ✅ Insert a result
    public boolean insertResult(int studentId, int subjectId, int marks, String remark) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("student_id", studentId);
        values.put("Subject_id", subjectId);
        values.put("marks", marks);
        values.put("remark", remark);
        long result = db.insert("RESULTS", null, values);
        return result != -1;
    }
    // ✅ Get subjects assigned to a specific teacher
    public Cursor getSubjectsByTeacherId(long teacherId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM subject WHERE t_id = ?";
        return db.rawQuery(query, new String[]{String.valueOf(teacherId)});
    }


    // ✅ Get all results
    public Cursor getAllResults() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT s.first_name || ' ' || s.last_name AS Name, " +
                "sub.name AS Subject_name, r.marks, r.remark " +
                "FROM RESULTS r " +
                "JOIN student s ON r.student_id = s.s_id " +
                "JOIN subject sub ON r.Subject_id = sub.subject_id";
        return db.rawQuery(query, null);
    }

    // Model class for student with subjects
    public static class StudentWithSubjects {
        public long sId;
        public String firstName;
        public String lastName;
        public String grade;
        public String phoneNumber;
        public String guardianTP;
        public String qrImg;
        public String email;
        public String password;
        public List<String> subjects;
    }

    // Fetch all students with their subjects
    public List<StudentWithSubjects> getAllStudentsWithSubjects() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<StudentWithSubjects> students = new ArrayList<>();
        HashMap<Long, StudentWithSubjects> studentMap = new HashMap<>();
        // Query students
        Cursor c = db.rawQuery("SELECT * FROM student", null);
        if (c.moveToFirst()) {
            do {
                StudentWithSubjects s = new StudentWithSubjects();
                s.sId = c.getLong(c.getColumnIndexOrThrow("s_id"));
                s.firstName = c.getString(c.getColumnIndexOrThrow("first_name"));
                s.lastName = c.getString(c.getColumnIndexOrThrow("last_name"));
                s.grade = c.getString(c.getColumnIndexOrThrow("grade"));
                s.phoneNumber = c.getString(c.getColumnIndexOrThrow("phone_number"));
                s.guardianTP = c.getString(c.getColumnIndexOrThrow("guardian_tp"));
                s.qrImg = c.getString(c.getColumnIndexOrThrow("qr_img"));
                s.email = c.getString(c.getColumnIndexOrThrow("email"));
                s.password = c.getString(c.getColumnIndexOrThrow("password"));
                s.subjects = new ArrayList<>();
                students.add(s);
                studentMap.put(s.sId, s);
            } while (c.moveToNext());
        }
        c.close();
        // Query subjects for all students
        Cursor cs = db.rawQuery("SELECT ss.student_id, sub.name FROM student_subject ss JOIN subject sub ON ss.subject_id = sub.subject_id", null);
        if (cs.moveToFirst()) {
            do {
                long studentId = cs.getLong(cs.getColumnIndexOrThrow("student_id"));
                String subjectName = cs.getString(cs.getColumnIndexOrThrow("name"));
                StudentWithSubjects s = studentMap.get(studentId);
                if (s != null) {
                    s.subjects.add(subjectName);
                }
            } while (cs.moveToNext());
        }
        cs.close();
        return students;
    }

}

