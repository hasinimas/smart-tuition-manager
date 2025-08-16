package com.example.smarttuitionmanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.view.Gravity;
import android.widget.FrameLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.time.LocalTime;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.app.AlertDialog;

public class TeacherAttendanceFragment extends Fragment {

    private static final int CAMERA_REQUEST_CODE = 101;

    private EditText etStudentId;
    private Button btnScanQr, btnMarkAttendance;
    private ListView lvAttendanceList;
    private ArrayList<String> attendanceList;
    private ArrayAdapter<String> adapter;

    public TeacherAttendanceFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_attendance, container, false);

        etStudentId = view.findViewById(R.id.et_student_id);
        btnScanQr = view.findViewById(R.id.btn_scan_qr);
        btnMarkAttendance = view.findViewById(R.id.btn_mark_attendance);
        lvAttendanceList = view.findViewById(R.id.lv_attendance_list);

        attendanceList = new ArrayList<>();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, attendanceList);
        lvAttendanceList.setAdapter(adapter);

        btnScanQr.setOnClickListener(v -> handleScanButton());
        btnMarkAttendance.setOnClickListener(v -> markAttendance());

        // Add text change listener for manual student ID entry
        etStudentId.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                String studentId = s.toString().trim();
                btnMarkAttendance.setEnabled(!studentId.isEmpty());
                
                if (!studentId.isEmpty()) {
                    validateAndShowStudentName(studentId);
                }
            }
        });

        // Add view history button
        Button btnViewHistory = view.findViewById(R.id.btn_view_history);
        if (btnViewHistory != null) {
            btnViewHistory.setOnClickListener(v -> showAttendanceHistory());
        }

        // Add clear button
        Button btnClearList = view.findViewById(R.id.btn_clear_list);
        if (btnClearList != null) {
            btnClearList.setOnClickListener(v -> clearTodayAttendance());
        }

        // Load today's attendance records
        loadTodayAttendance();

        return view;
    }

    private void loadTodayAttendance() {
        try {
            int teacherId = getLoggedInTeacherId();
            if (teacherId == -1) return;

            int subjectId = getSubjectIdForTeacher(teacherId);
            if (subjectId == -1) return;

            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(requireContext());
            
            Cursor cursor = dbHelper.getAttendanceForSubjectOnDate(subjectId, today);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String studentName = cursor.getString(cursor.getColumnIndexOrThrow("student_name"));
                    String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                    String record = "✔ " + studentName + " - " + status;
                    attendanceList.add(record);
                } while (cursor.moveToNext());
                adapter.notifyDataSetChanged();
            }
            if (cursor != null) cursor.close();
        } catch (Exception e) {
            Log.e("TeacherAttendance", "Error loading today's attendance: " + e.getMessage());
        }
    }

    private void handleScanButton() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            startQRScanner();
        }
    }

    private void startQRScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan Student QR Code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    private void markAttendance() {
        String studentIdStr = etStudentId.getText().toString().trim();

        if (studentIdStr.isEmpty()) {
            Toast.makeText(getContext(), "Please scan Student ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show attendance status dialog
        showAttendanceStatusDialog(studentIdStr);
    }

    private void showAttendanceStatusDialog(String studentIdStr) {
        String[] options = {"Present", "Absent"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Mark Attendance")
               .setItems(options, (dialog, which) -> {
                   String status = options[which];
                   markAttendanceWithStatus(studentIdStr, status);
               })
               .setNegativeButton("Cancel", null)
               .show();
    }

    private void markAttendanceWithStatus(String studentIdStr, String status) {
        try {
            int studentId = Integer.parseInt(studentIdStr);
            int teacherId = getLoggedInTeacherId();
            
            if (teacherId == -1) {
                Toast.makeText(getContext(), "Error: Teacher not authenticated. Please log in again.", Toast.LENGTH_LONG).show();
                return;
            }

            int subjectId = getSubjectIdForTeacher(teacherId);

            if (subjectId == -1) {
                Toast.makeText(getContext(), "Subject not found for teacher. Please contact administrator.", Toast.LENGTH_LONG).show();
                return;
            }

            // Validate that the student exists
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(requireContext());
            Cursor studentCursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT s_id, first_name, last_name FROM student WHERE s_id = ?", 
                new String[]{String.valueOf(studentId)});
            
            String studentName = "";
            if (studentCursor.moveToFirst()) {
                studentName = studentCursor.getString(studentCursor.getColumnIndexOrThrow("first_name")) + 
                             " " + studentCursor.getString(studentCursor.getColumnIndexOrThrow("last_name"));
            } else {
                studentCursor.close();
                Toast.makeText(getContext(), "Student ID not found in database", Toast.LENGTH_SHORT).show();
                return;
            }
            studentCursor.close();

            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            boolean inserted = dbHelper.markAttendance(studentId, subjectId, date, status);

            if (inserted) {
                String record = "✔ " + studentName + " - " + status + " (" + getCurrentTime() + ")";
                attendanceList.add(record);
                adapter.notifyDataSetChanged();

                lvAttendanceList.post(() -> lvAttendanceList.setSelection(adapter.getCount() - 1));
                etStudentId.setText("");
                btnMarkAttendance.setEnabled(false);

                showSuccessSnackbar("Attendance marked: " + studentName + " - " + status);
            } else {
                showErrorSnackbar("Already marked for today!");
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid Student ID format", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e("TeacherAttendance", "Error marking attendance: " + e.getMessage());
            Toast.makeText(getContext(), "Error marking attendance: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private int getSubjectIdForTeacher(int teacherId) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(requireContext());
        
        // First try to get subject by teacher ID
        Cursor cursor = dbHelper.getSubjectsByTeacherId(teacherId);
        if (cursor != null && cursor.moveToFirst()) {
            int subjectId = cursor.getInt(cursor.getColumnIndexOrThrow("subject_id"));
            cursor.close();
            return subjectId;
        }
        if (cursor != null) cursor.close();
        
        // If no subject found by teacher ID, try to get teacher's subject name and find subject ID
        try {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT subject FROM teacher WHERE t_id = ?", 
                               new String[]{String.valueOf(teacherId)});
            if (cursor != null && cursor.moveToFirst()) {
                String subjectName = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
                cursor.close();
                
                // Get subject ID by name
                return dbHelper.getSubjectIdByName(subjectName);
            }
            if (cursor != null) cursor.close();
        } catch (Exception e) {
            Log.e("TeacherAttendance", "Error getting teacher subject: " + e.getMessage());
        }
        
        return -1;
    }

    private void showSuccessSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#4CAF50")); // green

        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0);
        textView.setCompoundDrawablePadding(20);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = Gravity.CENTER;
        snackbarView.setLayoutParams(params);
        snackbar.show();
    }

    private void showErrorSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#D32F2F")); // red

        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error, 0, 0, 0);
        textView.setCompoundDrawablePadding(20);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = Gravity.CENTER;
        snackbarView.setLayoutParams(params);
        snackbar.show();
    }

    private void handleScannedResult(String qrData) {
        if (qrData != null && qrData.startsWith("attendance|")) {
            String[] parts = qrData.split("\\|");

            if (parts.length >= 2 && parts[1].matches("\\d+")) {
                String studentId = parts[1];  // Use second part
                etStudentId.setText(studentId);
                btnMarkAttendance.setEnabled(true);
                Toast.makeText(getContext(), "Scanned ID: " + studentId, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Toast.makeText(getContext(), "Invalid QR Code", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            handleScannedResult(result.getContents());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String getCurrentTime() {
        return LocalTime.now().withNano(0).toString(); // HH:mm:ss
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startQRScanner();
        } else {
            Toast.makeText(getContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private int getLoggedInTeacherId() {
        // Get teacher ID from SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        return (int) prefs.getLong("teacherId", -1);
    }

    private void showAttendanceHistory() {
        try {
            int teacherId = getLoggedInTeacherId();
            if (teacherId == -1) {
                Toast.makeText(getContext(), "Teacher not authenticated", Toast.LENGTH_SHORT).show();
                return;
            }

            int subjectId = getSubjectIdForTeacher(teacherId);
            if (subjectId == -1) {
                Toast.makeText(getContext(), "Subject not found", Toast.LENGTH_SHORT).show();
                return;
            }

            MyDatabaseHelper dbHelper = new MyDatabaseHelper(requireContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(
                "SELECT a.date, COUNT(*) as total_students, " +
                "SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END) as present_count " +
                "FROM attendance a " +
                "WHERE a.subject_id = ? " +
                "GROUP BY a.date " +
                "ORDER BY a.date DESC " +
                "LIMIT 30",
                new String[]{String.valueOf(subjectId)});

            if (cursor != null && cursor.moveToFirst()) {
                StringBuilder history = new StringBuilder("Attendance History (Last 30 days):\n\n");
                do {
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                    int totalStudents = cursor.getInt(cursor.getColumnIndexOrThrow("total_students"));
                    int presentCount = cursor.getInt(cursor.getColumnIndexOrThrow("present_count"));
                    int absentCount = totalStudents - presentCount;
                    double attendanceRate = totalStudents > 0 ? (double) presentCount / totalStudents * 100 : 0;
                    
                    history.append(date).append(":\n");
                    history.append("  Present: ").append(presentCount).append("\n");
                    history.append("  Absent: ").append(absentCount).append("\n");
                    history.append("  Rate: ").append(String.format("%.1f%%", attendanceRate)).append("\n\n");
                } while (cursor.moveToNext());

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Attendance History")
                       .setMessage(history.toString())
                       .setPositiveButton("OK", null)
                       .show();
            } else {
                Toast.makeText(getContext(), "No attendance history found", Toast.LENGTH_SHORT).show();
            }
            if (cursor != null) cursor.close();
        } catch (Exception e) {
            Log.e("TeacherAttendance", "Error showing attendance history: " + e.getMessage());
            Toast.makeText(getContext(), "Error loading attendance history", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearTodayAttendance() {
        try {
            int teacherId = getLoggedInTeacherId();
            if (teacherId == -1) {
                Toast.makeText(getContext(), "Teacher not authenticated", Toast.LENGTH_SHORT).show();
                return;
            }

            int subjectId = getSubjectIdForTeacher(teacherId);
            if (subjectId == -1) {
                Toast.makeText(getContext(), "Subject not found", Toast.LENGTH_SHORT).show();
                return;
            }

            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(requireContext());
            int rowsDeleted = dbHelper.deleteAttendanceForSubjectOnDate(subjectId, today);

            if (rowsDeleted > 0) {
                attendanceList.clear();
                adapter.notifyDataSetChanged();
                showSuccessSnackbar("Today's attendance list cleared.");
            } else {
                showErrorSnackbar("No attendance records found for today.");
            }
        } catch (Exception e) {
            Log.e("TeacherAttendance", "Error clearing attendance: " + e.getMessage());
            Toast.makeText(getContext(), "Error clearing attendance: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void validateAndShowStudentName(String studentIdStr) {
        try {
            int studentId = Integer.parseInt(studentIdStr);
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(requireContext());
            Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT first_name, last_name, grade FROM student WHERE s_id = ?", 
                new String[]{String.valueOf(studentId)});
            
            if (cursor != null && cursor.moveToFirst()) {
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
                String grade = cursor.getString(cursor.getColumnIndexOrThrow("grade"));
                String fullName = firstName + " " + lastName;
                
                // Show student info in a subtle way (you could add a TextView for this)
                Toast.makeText(getContext(), "Student: " + fullName + " (Grade " + grade + ")", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Student ID not found", Toast.LENGTH_SHORT).show();
            }
            if (cursor != null) cursor.close();
        } catch (NumberFormatException e) {
            // Invalid number format, ignore
        } catch (Exception e) {
            Log.e("TeacherAttendance", "Error validating student: " + e.getMessage());
        }
    }
}