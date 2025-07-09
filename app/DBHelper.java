import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "tuition_db.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Only necessary tables
        db.execSQL("CREATE TABLE attendance (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id TEXT, " +
                "course_id TEXT, " +
                "date TEXT, " +
                "status TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tuition");

        onCreate(db);
    }

    // Add attendance
    public boolean markAttendance(String studentId, String courseId, String date, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("student_id", studentId);
        values.put("subject_id", courseId);
        values.put("date", date);
        values.put("status", status);
        long result = db.insert("attendance", null, values);
        return result != -1;
    }

    // Get attendance for a course
    public Cursor getAttendanceByCourse(String courseId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM attendance WHERE student_id = ?", new String[]{courseId});
    }

    // Optional: Insert student
    public boolean addStudent(String studentId, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("student_id", studentId);
        values.put("name", name);
        long result = db.insert("students", null, values);
        return result != -1;
    }

    // Optional: Get all students
    public Cursor getAllStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM students", null);
    }
}
