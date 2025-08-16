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

    private static final int DATABASE_VERSION = 6;  // <-- increment this


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


        // Teacher table
        db.execSQL("CREATE TABLE teacher (" +
                "t_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "first_name TEXT NOT NULL, " +
                "last_name TEXT NOT NULL, " +
                "subject TEXT NOT NULL, " +
                "phone_number TEXT NOT NULL, " +
                "class TEXT NOT NULL, " +
                "id_number TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL)");

        // Admin table
        db.execSQL("CREATE TABLE admin (" +
                "admin_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "first_name TEXT NOT NULL, " +
                "last_name TEXT NOT NULL, " +
                "email TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL)");
      
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

        // Teacher Student Assign table
        db.execSQL("CREATE TABLE teacher_student_assign (" +
                "assign_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "teacher_name TEXT NOT NULL, " +
                "student_name TEXT NOT NULL, " +
                "subject TEXT NOT NULL, " +
                "time TEXT NOT NULL, " +
                "grade TEXT NOT NULL, " +
                "day TEXT NOT NULL, " +
                "status TEXT DEFAULT 'active')");

        //  Attendance table
        db.execSQL("CREATE TABLE attendance (" +
                "attendance_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id INTEGER NOT NULL, " +
                "subject_id INTEGER NOT NULL, " +
                "date TEXT NOT NULL, " +
                "status TEXT CHECK(status IN ('Present','Absent')) NOT NULL, " +
                "FOREIGN KEY(student_id) REFERENCES student(s_id), " +
                "FOREIGN KEY(subject_id) REFERENCES subject(subject_id))");

        // Notifications table
        db.execSQL("CREATE TABLE NOTIFICATIONS (" +
                "notification_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id INTEGER NOT NULL, " +
                "message TEXT NOT NULL, " +
                "timestamp TEXT NOT NULL, " +
                "FOREIGN KEY(student_id) REFERENCES student(s_id))");

        // Insert default admin if not exists
        db.execSQL("INSERT OR IGNORE INTO admin (first_name, last_name, email, password) VALUES ('new', 'admin', 'admin@gmail.com', 'admin123')");
        
        // Insert all 6 main subjects
        String[] subjects = {"Mathematics", "Science", "English", "Sinhala", "History", "Buddhism"};
        HashMap<String, Long> subjectIds = new HashMap<>();
        
        for (String subject : subjects) {
            ContentValues subjectValues = new ContentValues();
            subjectValues.put("name", subject);
            long subjectId = db.insert("subject", null, subjectValues);
            subjectIds.put(subject, subjectId);
        }
        
        // Insert 9 students
        String[][] students = {
                {"Senesh", "jayamaha", "10", "0774585658", "0112563258", "senesh@gmail.com", "senesh123", "Sinhala"},
                {"Amara", "perera", "9", "0771234567", "0112345678", "amara@gmail.com", "amara123", "Mathematics"},
                {"Dilshan", "fernando", "11", "0772345678", "0113456789", "dilshan@gmail.com", "dilshan123", "Science"},
                {"Nethmi", "silva", "8", "0773456789", "0114567890", "nethmi@gmail.com", "nethmi123", "English"},
                {"Kavindu", "rathnayake", "7", "0774567890", "0115678901", "kavindu@gmail.com", "kavindu123", "History"},
                {"Sachini", "wijesinghe", "10", "0775678901", "0116789012", "sachini@gmail.com", "sachini123", "Buddhism"},
                {"Ravindu", "bandara", "9", "0776789012", "0117890123", "ravindu@gmail.com", "ravindu123", "Mathematics"},
                {"Tharushi", "gunasekara", "11", "0777890123", "0118901234", "tharushi@gmail.com", "tharushi123", "Science"},
                {"Dhanushka", "jayawardena", "8", "0778901234", "0119012345", "dhanushka@gmail.com", "dhanushka123", "English"},
                {"Ayesha", "Fernando", "8", "0771000001", "0111036701", "ayesha1@gmail.com", "ayesha123", "Mathematics"},
                {"Kasun", "Perera", "9", "0771589002", "0111076502", "kasun2@gmail.com", "kasun123", "Science"},
                {"Nimali", "Silva", "10", "0771025693", "0111465003", "nimali3@gmail.com", "nimali123", "English"},
                {"Ruwan", "Jayalath", "11", "0771124504", "0111312004", "ruwan4@gmail.com", "ruwan123", "Sinhala"},
                {"Ishara", "Bandara", "7", "0771145895", "0111894005", "ishara5@gmail.com", "ishara123", "History"},
                {"Sajith", "Gunasekara", "8", "0771589886", "0111017406", "sajith6@gmail.com", "sajith123", "Buddhism"},
                {"Thilini", "Wijesinghe", "11", "0771125697", "0111069307", "thilini7@gmail.com", "thilini123", "Mathematics"},
                {"Chathura", "Rathnayake", "8", "0771147858", "0111045808", "chathura8@gmail.com", "chathura123", "Science"},
                {"Nadeesha", "Senanayake", "11", "0776589529", "01110379109", "nadeesha9@gmail.com", "nadeesha123", "English"},
                {"Sahan", "Dissanayake", "7", "0771558810", "0111984010", "sahan10@gmail.com", "sahan123", "Sinhala"},
                {"Rashmi", "Herath", "8", "0771736411", "0111111011", "rashmi11@gmail.com", "rashmi123", "History"},
                {"Dineth", "Abeysekara", "6", "0771136912", "0111476012", "dineth12@gmail.com", "dineth123", "Buddhism"},
                {"Malsha", "Karunaratne", "10", "0771287913", "0111986013", "malsha13@gmail.com", "malsha123", "Mathematics"},
                {"Kavindi", "Ekanayake", "11", "0771446614", "0111012414", "kavindi14@gmail.com", "kavindi123", "Science"},
                {"Sewwandi", "Jayasinghe", "7", "0771112215", "0114590015", "sewwandi15@gmail.com", "sewwandi123", "English"},
                {"Ravindu", "Wickramasinghe", "8", "0771778616", "0111857016", "ravindu16@gmail.com", "ravindu123", "Sinhala"},
                {"Tharuka", "Peris", "11", "0771000017", "0111458017", "tharuka17@gmail.com", "tharuka123", "History"},
                {"Sanduni", "Dias", "10", "0771789018", "0111857018", "sanduni18@gmail.com", "sanduni123", "Buddhism"},
                {"Isuru", "Hettiarachchi", "11", "0771890019", "0111459019", "isuru19@gmail.com", "isuru123", "Mathematics"},
                {"Nipun", "Jayawardena", "7", "0771008420", "0111045820", "nipun20@gmail.com", "nipun123", "Science"}


        };
        
        for (String[] student : students) {
            ContentValues studentValues = new ContentValues();
            studentValues.put("first_name", student[0]);
            studentValues.put("last_name", student[1]);
            studentValues.put("grade", student[2]);
            studentValues.put("phone_number", student[3]);
            studentValues.put("guardian_tp", student[4]);
            studentValues.put("email", student[5]);
            studentValues.put("password", student[6]);
            long studentId = db.insert("student", null, studentValues);
            
            // Link student to their subject
            if (studentId != -1) {
                String subjectName = student[7];
                Long subjectId = subjectIds.get(subjectName);
                if (subjectId != null) {
                    ContentValues linkValues = new ContentValues();
                    linkValues.put("student_id", studentId);
                    linkValues.put("subject_id", subjectId);
                    db.insert("student_subject", null, linkValues);
                }
            }
        }




        // Insert 8 dummy teachers
        String[][] teachers = {
            {"Nimal", "Perera", "Mathematics", "0771111111", "10", "198523652545", "nimal.teacher@gmail.com", "nimal123"},
            {"Sunil", "Fernando", "Science", "0772222222", "11", "199626587452", "sunil.teacher@gmail.com", "sunil123"},
            {"Kumari", "Silva", "English", "0773333333", "8", "198855847741", "kumari.teacher@gmail.com", "kumari123"},
            {"Ruwan", "Jayasinghe", "Sinhala", "0774444444", "9", "200059632332", "ruwan.teacher@gmail.com", "ruwan123"},
            {"Chathura", "Bandara", "History", "0775555555", "11", "200117203018", "chathura.teacher@gmail.com", "chathura123"},
            {"Harsha", "Weerasinghe", "Buddhism", "0776666666", "10", "199358877412", "harsha.teacher@gmail.com", "harsha123"},
            {"Ishara", "Mendis", "Mathematics", "0777777777", "7", "199744665891", "ishara.teacher@gmail.com", "ishara123"},
            {"Saman", "Abeysekara", "Science", "0778888888", "6", "198952974682", "saman.teacher@gmail.com", "saman123"}
        };
        for (String[] teacher : teachers) {
            ContentValues teacherValues = new ContentValues();
            teacherValues.put("first_name", teacher[0]);
            teacherValues.put("last_name", teacher[1]);
            teacherValues.put("subject", teacher[2]);
            teacherValues.put("phone_number", teacher[3]);
            teacherValues.put("class", teacher[4]);
            teacherValues.put("id_number", teacher[5]);
            teacherValues.put("email", teacher[6]);
            teacherValues.put("password", teacher[7]);
            db.insert("teacher", null, teacherValues);
        }


        


    }


        @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("CREATE TABLE IF NOT EXISTS admin (admin_id INTEGER PRIMARY KEY AUTOINCREMENT, first_name TEXT NOT NULL, last_name TEXT NOT NULL, email TEXT UNIQUE NOT NULL, password TEXT NOT NULL)");
            db.execSQL("INSERT OR IGNORE INTO admin (first_name, last_name, email, password) VALUES ('new', 'admin', 'admin@gmail.com', 'admin123')");
        }
        if (oldVersion < 5) {
            // Create teacher student assign table
            db.execSQL("CREATE TABLE IF NOT EXISTS teacher_student_assign (" +
                    "assign_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "teacher_name TEXT NOT NULL, " +
                    "student_name TEXT NOT NULL, " +
                "subject TEXT NOT NULL, " +
                "time TEXT NOT NULL, " +
                "grade TEXT NOT NULL, " +
                "assigned_date TEXT NOT NULL, " +
                "status TEXT DEFAULT 'active')");
        }
        if (oldVersion < 6) {
            // Update teacher_student_assign table to change assigned_date to day
            db.execSQL("ALTER TABLE teacher_student_assign ADD COLUMN day TEXT");
            db.execSQL("UPDATE teacher_student_assign SET day = 'Sunday' WHERE assigned_date IS NOT NULL");
            db.execSQL("ALTER TABLE teacher_student_assign DROP COLUMN assigned_date");
            // Create attendance table
            db.execSQL("CREATE TABLE IF NOT EXISTS attendance (" +
                    "attendance_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "student_id INTEGER NOT NULL, " +
                    "subject_id INTEGER NOT NULL, " +
                    "date TEXT NOT NULL, " +
                    "status TEXT CHECK(status IN ('Present','Absent')) NOT NULL, " +
                    "FOREIGN KEY(student_id) REFERENCES student(s_id), " +
                    "FOREIGN KEY(subject_id) REFERENCES subject(subject_id))");
        }
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

    // Get subjects by teacher name (alternative method)
    public Cursor getSubjectsByTeacherName(String teacherName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT s.* FROM subject s " +
                      "JOIN teacher t ON s.name = t.subject " +
                      "WHERE t.first_name || ' ' || t.last_name = ?";
        return db.rawQuery(query, new String[]{teacherName});
    }

    // Get subject ID by subject name
    public int getSubjectIdByName(String subjectName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT subject_id FROM subject WHERE name = ?", 
                                   new String[]{subjectName});
        if (cursor != null && cursor.moveToFirst()) {
            int subjectId = cursor.getInt(0);
            cursor.close();
            return subjectId;
        }
        if (cursor != null) cursor.close();
        return -1;
    }

    // Get attendance records for a student
    public Cursor getAttendanceForStudent(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT a.*, s.name as subject_name, a.date, a.status " +
                      "FROM attendance a " +
                      "JOIN subject s ON a.subject_id = s.subject_id " +
                      "WHERE a.student_id = ? " +
                      "ORDER BY a.date DESC";
        return db.rawQuery(query, new String[]{String.valueOf(studentId)});
    }

    // Get attendance records for a subject on a specific date
    public Cursor getAttendanceForSubjectOnDate(int subjectId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT a.*, s.first_name || ' ' || s.last_name as student_name " +
                      "FROM attendance a " +
                      "JOIN student s ON a.student_id = s.s_id " +
                      "WHERE a.subject_id = ? AND a.date = ? " +
                      "ORDER BY s.first_name, s.last_name";
        return db.rawQuery(query, new String[]{String.valueOf(subjectId), date});
    }

    // Delete attendance records for a subject on a specific date
    public int deleteAttendanceForSubjectOnDate(int subjectId, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("attendance", 
                        "subject_id = ? AND date = ?", 
                        new String[]{String.valueOf(subjectId), date});
    }

    // Update attendance status for a specific student, subject, and date
    public boolean updateAttendanceStatus(int studentId, int subjectId, String date, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);
        
        int updatedRows = db.update("attendance", values,
            "student_id = ? AND subject_id = ? AND date = ?",
            new String[]{String.valueOf(studentId), String.valueOf(subjectId), date});
        
        return updatedRows > 0;
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

   // Check if a student exists with given email and password
    public boolean checkStudentLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT s_id FROM student WHERE email=? AND password=?", new String[]{email, password});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Get student ID for successful login
    public long getStudentId(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT s_id FROM student WHERE email=? AND password=?", new String[]{email, password});
        long studentId = -1;
        if (cursor.moveToFirst()) {
            studentId = cursor.getLong(0);
        }
        cursor.close();
        return studentId;
    }

    // Check if a teacher exists with given email and password
    public boolean checkTeacherLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT t_id FROM teacher WHERE email=? AND password=?", new String[]{email, password});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Get teacher ID for successful login
    public long getTeacherId(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT t_id FROM teacher WHERE email=? AND password=?", new String[]{email, password});
        long teacherId = -1;
        if (cursor.moveToFirst()) {
            teacherId = cursor.getLong(0);
        }
        cursor.close();
        return teacherId;
    }

    // Check if an admin exists with given email and password
    public boolean checkAdminLogin(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT admin_id FROM admin WHERE email=? AND password=?", new String[]{email, password});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Get admin ID for successful login
    public long getAdminId(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT admin_id FROM admin WHERE email=? AND password=?", new String[]{email, password});
        long adminId = -1;
        if (cursor.moveToFirst()) {
            adminId = cursor.getLong(0);
        }
        cursor.close();
        return adminId;
    }

    // Insert a teacher and return the new teacher ID
    public long insertTeacher(String firstName, String lastName, String subject, String phoneNumber, String className, String idNumber, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("subject", subject);
        values.put("phone_number", phoneNumber);
        values.put("class", className);
        values.put("id_number", idNumber);
        values.put("email", email);
        values.put("password", password);
        return db.insert("teacher", null, values);
    }

    // Delete a teacher by t_id
    public void deleteTeacher(long teacherId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("teacher", "t_id=?", new String[]{String.valueOf(teacherId)});
    }

    // Update a teacher by t_id
    public void updateTeacher(long teacherId, String firstName, String lastName, String subject, String phoneNumber, String className, String idNumber, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("subject", subject);
        values.put("phone_number", phoneNumber);
        values.put("class", className);
        values.put("id_number", idNumber);
        values.put("email", email);
        values.put("password", password);
        db.update("teacher", values, "t_id=?", new String[]{String.valueOf(teacherId)});
    }

    // Insert an admin and return the new admin ID
    public long insertAdmin(String firstName, String lastName, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("first_name", firstName);
        values.put("last_name", lastName);
        values.put("email", email);
        values.put("password", password);
        return db.insert("admin", null, values);
    }

    // Assign default subject to students missing one
    public void assignDefaultSubjectToStudentsMissingOne() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Ensure 'Mathematics' subject exists
        long subjectId = insertSubject("Mathematics");
        // Find students with no subject
        Cursor cursor = db.rawQuery("SELECT s_id FROM student WHERE s_id NOT IN (SELECT student_id FROM student_subject)", null);
        if (cursor.moveToFirst()) {
            do {
                long studentId = cursor.getLong(0);
                ContentValues values = new ContentValues();
                values.put("student_id", studentId);
                values.put("subject_id", subjectId);
                db.insert("student_subject", null, values);
            } while (cursor.moveToNext());
        }
        cursor.close();
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

    // Fetch all students with their subjects (show all students, even those with no subject)
    public List<StudentWithSubjects> getAllStudentsWithSubjects() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<StudentWithSubjects> students = new ArrayList<>();
        HashMap<Long, StudentWithSubjects> studentMap = new HashMap<>();
        // Query students with LEFT JOIN to include those with no subject
        Cursor c = db.rawQuery(
            "SELECT s.*, sub.name as subject_name " +
            "FROM student s " +
            "LEFT JOIN student_subject ss ON s.s_id = ss.student_id " +
            "LEFT JOIN subject sub ON ss.subject_id = sub.subject_id " +
            "ORDER BY s.s_id",
            null
        );
        if (c.moveToFirst()) {
            do {
                long sId = c.getLong(c.getColumnIndexOrThrow("s_id"));
                StudentWithSubjects s = studentMap.get(sId);
                if (s == null) {
                    s = new StudentWithSubjects();
                    s.sId = sId;
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
                    studentMap.put(sId, s);
                }
                String subjectName = c.getString(c.getColumnIndexOrThrow("subject_name"));
                if (subjectName != null && !subjectName.isEmpty() && !s.subjects.contains(subjectName)) {
                    s.subjects.add(subjectName);
                }
            } while (c.moveToNext());
        }
        c.close();
        return students;
    }

    // Model class for teacher
    public static class Teacher {
        public long tId;
        public String firstName;
        public String lastName;
        public String subject;
        public String phoneNumber;
        public String className;
        public String idNumber;
        public String email;
        public String password;
    }

    // Fetch all teachers
    public List<Teacher> getAllTeachers() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Teacher> teachers = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM teacher", null);
        if (c.moveToFirst()) {
            do {
                Teacher t = new Teacher();
                t.tId = c.getLong(c.getColumnIndexOrThrow("t_id"));
                t.firstName = c.getString(c.getColumnIndexOrThrow("first_name"));
                t.lastName = c.getString(c.getColumnIndexOrThrow("last_name"));
                t.subject = c.getString(c.getColumnIndexOrThrow("subject"));
                t.phoneNumber = c.getString(c.getColumnIndexOrThrow("phone_number"));
                t.className = c.getString(c.getColumnIndexOrThrow("class"));
                t.idNumber = c.getString(c.getColumnIndexOrThrow("id_number"));
                t.email = c.getString(c.getColumnIndexOrThrow("email"));
                t.password = c.getString(c.getColumnIndexOrThrow("password"));
                teachers.add(t);
            } while (c.moveToNext());
        }
        c.close();
        return teachers;
    }

    // Get all teachers as Cursor
    public Cursor getAllTeachersCursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM teacher ORDER BY first_name, last_name", null);
    }

    // Get teachers by subject and grade
    public Cursor getTeachersBySubjectAndGrade(String subject, String grade) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
            "SELECT * FROM teacher WHERE subject = ? AND class = ? ORDER BY first_name, last_name",
            new String[]{subject, grade});
    }

    // Get teachers by subject only (fallback when no exact grade match)
    public Cursor getTeachersBySubject(String subject) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
            "SELECT * FROM teacher WHERE subject = ? ORDER BY first_name, last_name",
            new String[]{subject});
    }

    // Model class for admin
    public static class Admin {
        public long adminId;
        public String firstName;
        public String lastName;
        public String email;
        public String password;
    }

    // Fetch all admins
    public List<Admin> getAllAdmins() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Admin> admins = new ArrayList<>();
        Cursor c = db.rawQuery("SELECT * FROM admin", null);
        if (c.moveToFirst()) {
            do {
                Admin a = new Admin();
                a.adminId = c.getLong(c.getColumnIndexOrThrow("admin_id"));
                a.firstName = c.getString(c.getColumnIndexOrThrow("first_name"));
                a.lastName = c.getString(c.getColumnIndexOrThrow("last_name"));
                a.email = c.getString(c.getColumnIndexOrThrow("email"));
                a.password = c.getString(c.getColumnIndexOrThrow("password"));
                admins.add(a);
            } while (c.moveToNext());
        }
        c.close();
        return admins;
    }


    // -------------------------------------------- Attendance ---------------------------------------------------------------
    public void updateStudentQR(long studentId, String base64QR) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("qr_img", base64QR);
        db.update("student", values, "s_id = ?", new String[]{String.valueOf(studentId)});
    }

    public String getStudentQR(long studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT qr_img FROM student WHERE s_id = ?", new String[]{String.valueOf(studentId)});
        if (cursor.moveToFirst()) {
            String qrBase64 = cursor.getString(0);
            cursor.close();
            return qrBase64;
        } else {
            cursor.close();
            return null;
        }
    }

    // -------------------------------------------- Teacher Student Assign Methods ---------------------------------------------------------------

    // Assign a teacher to a student with details
    public long assignTeacherToStudent(String teacherName, String studentName, String subject, String time, String grade, String day) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("teacher_name", teacherName);
        values.put("student_name", studentName);
        values.put("subject", subject);
        values.put("time", time);
        values.put("grade", grade);
        values.put("day", day);
        values.put("status", "active");
        
        return db.insert("teacher_student_assign", null, values);
    }

    // Remove a teacher assignment from a student
    public boolean removeTeacherFromStudent(String teacherName, String studentName) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete("teacher_student_assign", 
            "teacher_name = ? AND student_name = ?", 
            new String[]{teacherName, studentName});
        return deletedRows > 0;
    }

    // Get all assignments for a specific student
    public Cursor getAssignmentsForStudent(String studentName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
            "SELECT * FROM teacher_student_assign " +
            "WHERE student_name = ? AND status = 'active' " +
            "ORDER BY day ASC",
            new String[]{studentName});
    }

    // Get all assignments for a specific teacher
    public Cursor getAssignmentsForTeacher(String teacherName) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
            "SELECT * FROM teacher_student_assign " +
            "WHERE teacher_name = ? AND status = 'active' " +
            "ORDER BY day ASC",
            new String[]{teacherName});
    }

    // Get all assignments
    public Cursor getAllAssignments() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
            "SELECT * FROM teacher_student_assign " +
            "WHERE status = 'active' " +
            "ORDER BY day ASC",
            null);
    }

    // Get assignments by subject
    public Cursor getAssignmentsBySubject(String subject) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
            "SELECT * FROM teacher_student_assign " +
            "WHERE subject = ? AND status = 'active' " +
            "ORDER BY day ASC",
            new String[]{subject});
    }

    // Get assignments by grade
    public Cursor getAssignmentsByGrade(String grade) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
            "SELECT * FROM teacher_student_assign " +
            "WHERE grade = ? AND status = 'active' " +
            "ORDER BY day ASC",
            new String[]{grade});
    }

    // Check if a teacher is already assigned to a student
    public boolean isTeacherAssignedToStudent(String teacherName, String studentName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
            "SELECT COUNT(*) FROM teacher_student_assign " +
            "WHERE teacher_name = ? AND student_name = ? AND status = 'active'",
            new String[]{teacherName, studentName});
        
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            cursor.close();
            return count > 0;
        }
        cursor.close();
        return false;
    }

    // Check if there's a time conflict for a teacher on a specific day and time
    public boolean hasTimeConflict(String teacherName, String day, String time) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
            "SELECT COUNT(*) FROM teacher_student_assign " +
            "WHERE teacher_name = ? AND day = ? AND time = ? AND status = 'active'",
            new String[]{teacherName, day, time});
        
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            cursor.close();
            return count > 0;
        }
        cursor.close();
        return false;
    }

    // Check if there's a time conflict for a student on a specific day and time
    public boolean hasStudentTimeConflict(String studentName, String day, String time) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
            "SELECT COUNT(*) FROM teacher_student_assign " +
            "WHERE student_name = ? AND day = ? AND time = ? AND status = 'active'",
            new String[]{studentName, day, time});
        
        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            cursor.close();
            return count > 0;
        }
        cursor.close();
        return false;
    }

    // Get assignment details by ID
    public Cursor getAssignmentById(long assignId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
            "SELECT * FROM teacher_student_assign " +
            "WHERE assign_id = ?",
            new String[]{String.valueOf(assignId)});
    }

    // Update assignment details
    public boolean updateAssignment(long assignId, String teacherName, String studentName, String subject, String time, String grade, String day) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("teacher_name", teacherName);
        values.put("student_name", studentName);
        values.put("subject", subject);
        values.put("time", time);
        values.put("grade", grade);
        values.put("day", day);
        
        int updatedRows = db.update("teacher_student_assign", values,
            "assign_id = ?",
            new String[]{String.valueOf(assignId)});
        
        return updatedRows > 0;
    }

    // Update assignment status (active/inactive)
    public boolean updateAssignmentStatus(long assignId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        
        int updatedRows = db.update("teacher_student_assign", values,
            "assign_id = ?",
            new String[]{String.valueOf(assignId)});
        
        return updatedRows > 0;
    }

    // Delete assignment by ID
    public boolean deleteAssignment(long assignId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete("teacher_student_assign", 
            "assign_id = ?", 
            new String[]{String.valueOf(assignId)});
        return deletedRows > 0;
    }

    // Get unique teachers from assignments
    public Cursor getUniqueTeachers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
            "SELECT DISTINCT teacher_name FROM teacher_student_assign " +
            "WHERE status = 'active' " +
            "ORDER BY teacher_name",
            null);
    }

    // Get unique students from assignments
    public Cursor getUniqueStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
            "SELECT DISTINCT student_name FROM teacher_student_assign " +
            "WHERE status = 'active' " +
            "ORDER BY student_name",
            null);
    }

    // Get unique subjects from assignments
    public Cursor getUniqueSubjects() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
            "SELECT DISTINCT subject FROM teacher_student_assign " +
            "WHERE status = 'active' " +
            "ORDER BY subject",
            null);
    }

    // Get unique grades from assignments
    public Cursor getUniqueGrades() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery(
            "SELECT DISTINCT grade FROM teacher_student_assign " +
            "WHERE status = 'active' " +
            "ORDER BY grade",
            null);
    }

    public boolean markAttendance(int studentId, int subjectId, String date, String status) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if already marked
        Cursor cursor = db.rawQuery(
                "SELECT * FROM attendance WHERE student_id = ? AND subject_id = ? AND date = ?",
                new String[]{String.valueOf(studentId), String.valueOf(subjectId), date});

        boolean alreadyMarked = cursor.moveToFirst();
        cursor.close();

        if (alreadyMarked) {
            return false; // Prevent duplicate entry
        }

        // Insert new attendance
        ContentValues values = new ContentValues();
        values.put("student_id", studentId);
        values.put("subject_id", subjectId);
        values.put("date", date);
        values.put("status", status);
        long result = db.insert("attendance", null, values);

        return result != -1;
    }

}
