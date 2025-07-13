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

        return view;
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

        if (!studentIdStr.isEmpty()) {
            int studentId = Integer.parseInt(studentIdStr);
            int teacherId = getLoggedInTeacherId(); // Dummy value for now
            int subjectId = getSubjectIdForTeacher(teacherId);

            if (subjectId == -1) {
                Toast.makeText(getContext(), "Subject not found for teacher", Toast.LENGTH_SHORT).show();
                return;
            }

            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String status = "Present";

            MyDatabaseHelper dbHelper = new MyDatabaseHelper(requireContext());

            boolean inserted = dbHelper.markAttendance(studentId, subjectId, date, status);

            if (inserted) {
                String record = "✔ Marked: " + studentIdStr + " - " + getCurrentTime();
                attendanceList.add(record);
                adapter.notifyDataSetChanged();

                lvAttendanceList.post(() -> lvAttendanceList.setSelection(adapter.getCount() - 1));
                etStudentId.setText("");
                btnMarkAttendance.setEnabled(false);

                showSuccessSnackbar("Attendance marked for " + studentIdStr);
            } else {
                showErrorSnackbar("Already marked for today!");
            }
        } else {
            Toast.makeText(getContext(), "Please scan Student ID", Toast.LENGTH_SHORT).show();
        }
    }

    private int getSubjectIdForTeacher(int teacherId) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(requireContext());
        Cursor cursor = dbHelper.getSubjectsByTeacherId(teacherId);
        if (cursor != null && cursor.moveToFirst()) {
            int subjectId = cursor.getInt(cursor.getColumnIndexOrThrow("subject_id"));
            cursor.close();
            return subjectId;
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
            if (parts.length >= 2) {
                String studentId = parts[1];
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
        return 1; // Replace with real teacher login ID
    }
}
