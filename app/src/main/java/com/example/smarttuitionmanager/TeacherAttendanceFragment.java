package com.example.smarttuitionmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class TeacherAttendanceFragment extends Fragment {

    private EditText etStudentId;
    private Button btnScanQr, btnMarkAttendance;
    private ListView lvAttendanceList;
    private ArrayList<String> attendanceList;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacherattendance, container, false);

        etStudentId = view.findViewById(R.id.et_student_id);
        btnScanQr = view.findViewById(R.id.btn_scan_qr);
        btnMarkAttendance = view.findViewById(R.id.btn_mark_attendance);
        lvAttendanceList = view.findViewById(R.id.lv_attendance_list);

        attendanceList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, attendanceList);
        lvAttendanceList.setAdapter(adapter);

        btnScanQr.setOnClickListener(v -> {
            String studentId = "STU123"; // Placeholder for QR scan
            etStudentId.setText(studentId);
            btnMarkAttendance.setEnabled(true);
        });

        btnMarkAttendance.setOnClickListener(v -> {
            String studentId = etStudentId.getText().toString().trim();
            if (!studentId.isEmpty()) {
                attendanceList.add("Marked: " + studentId + " - " + getCurrentTime());
                adapter.notifyDataSetChanged();
                etStudentId.setText("");
                btnMarkAttendance.setEnabled(false);
            }
        });

        return view;
    }

    private String getCurrentTime() {
        return java.time.LocalTime.now().toString();
    }
}