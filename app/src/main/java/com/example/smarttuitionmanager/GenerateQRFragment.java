package com.example.smarttuitionmanager;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class GenerateQRFragment extends Fragment {

    private EditText etStudentId;
    private Button btnGenerateQR;
    private ImageView qrImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generate_qr, container, false);

        etStudentId = view.findViewById(R.id.et_student_id);
        btnGenerateQR = view.findViewById(R.id.btn_generate_qr);
        qrImageView = view.findViewById(R.id.qr_image_view);

        btnGenerateQR.setOnClickListener(v -> handleQRGeneration());

        return view;
    }

    private void handleQRGeneration() {
        String studentId = etStudentId.getText().toString().trim();

        if (studentId.isEmpty()) {
            Toast.makeText(getContext(), "Please enter Student ID", Toast.LENGTH_SHORT).show();
            return;
        }

        String subjectId = "101"; // Placeholder — later get dynamically
        String date = java.time.LocalDate.now().toString();

        String qrContent = "attendance|" + studentId + "|" + subjectId + "|" + date;
        generateQRCode(qrContent);
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
}
