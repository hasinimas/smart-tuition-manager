package com.example.smarttuitionmanager;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.util.ArrayList;

public class TeacherAttendanceFragment extends Fragment {

    private EditText etStudentId;
    private Button btnScanQr, btnMarkAttendance;
    private ListView lvAttendanceList;
    private ArrayList<String> attendanceList;
    private ArrayAdapter<String> adapter;
    private Button btnGenerateQR;
    private ImageView qrImageView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacherattendance, container, false);

        etStudentId = view.findViewById(R.id.et_student_id);
        btnScanQr = view.findViewById(R.id.btn_scan_qr);
        btnMarkAttendance = view.findViewById(R.id.btn_mark_attendance);
        lvAttendanceList = view.findViewById(R.id.lv_attendance_list);

        btnGenerateQR = view.findViewById(R.id.btn_generate_qr);  // gen QR button
        qrImageView = view.findViewById(R.id.qr_image_view);      // QR image view

        btnGenerateQR.setOnClickListener(v -> handleQRGeneration());


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

    private void generateQRCode(String text) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 300, 300);
            Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);

            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    bitmap.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            qrImageView.setImageBitmap(bitmap);
            qrImageView.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "QR Code Generated", Toast.LENGTH_SHORT).show();

        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error generating QR", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleQRGeneration() {

        // Later you can get subjectId dynamically from Spinner or DB
        String subjectId = "101"; // Temporary placeholder
        String studentId = "STU123"; // Could be scanned by student
        String date = java.time.LocalDate.now().toString();

        String qrContent = "attendance|" + studentId + "|" + subjectId + "|" + date;

        generateQRCode(qrContent); // call method to actually generate and display the QR
    }


}