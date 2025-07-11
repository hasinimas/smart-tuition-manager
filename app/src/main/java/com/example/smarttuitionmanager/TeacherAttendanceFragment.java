package com.example.smarttuitionmanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import java.time.LocalTime;
import java.util.ArrayList;

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

        // Bind UI elements
        etStudentId = view.findViewById(R.id.et_student_id);
        btnScanQr = view.findViewById(R.id.btn_scan_qr);
        btnMarkAttendance = view.findViewById(R.id.btn_mark_attendance);
        lvAttendanceList = view.findViewById(R.id.lv_attendance_list);

        // Set up list adapter
        attendanceList = new ArrayList<>();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, attendanceList);
        lvAttendanceList.setAdapter(adapter);

        // Listeners
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
        integrator.setCameraId(0); // back camera
        integrator.setBeepEnabled(true);
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    private void markAttendance() {
        String studentId = etStudentId.getText().toString().trim();
        if (!studentId.isEmpty()) {
            String record = "✔ Marked: " + studentId + " - " + getCurrentTime();
            attendanceList.add(record);
            adapter.notifyDataSetChanged();

            // Auto-scroll down
            lvAttendanceList.post(() -> lvAttendanceList.setSelection(adapter.getCount() - 1));

            etStudentId.setText("");
            btnMarkAttendance.setEnabled(false);

            //  Show confirmation Snackbar
            showSuccessSnackbar("Attendance marked for " + studentId);

        } else {
            Toast.makeText(getContext(), "Please enter or scan Student ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSuccessSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG);

        View snackbarView = snackbar.getView();
       // snackbarView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.snackbar_success)); // background clr as green
        snackbarView.setBackgroundColor(Color.parseColor("#4CAF50")); // Green

        TextView textView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(16);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check, 0, 0, 0); // optional icon
        textView.setCompoundDrawablePadding(20); // spacing

        // Move Snackbar to center vertically
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = Gravity.CENTER;  // move to middle
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
}
