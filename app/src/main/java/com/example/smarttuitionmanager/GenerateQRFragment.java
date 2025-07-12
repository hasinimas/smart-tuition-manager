package com.example.smarttuitionmanager;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class GenerateQRFragment extends Fragment {

    private TextView tvStudentId;
    private Button btnGenerateQR, btnDownload, btnSaveToDB;
    private ImageView qrImageView;
    private Bitmap generatedQRBitmap;
    private long studentId = -1;
    private String lastQRBase64 = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generate_qr, container, false);

        tvStudentId = view.findViewById(R.id.et_student_id);  // Non-editable now
        btnGenerateQR = view.findViewById(R.id.btn_generate_qr);
        qrImageView = view.findViewById(R.id.qr_image_view);
        btnDownload = view.findViewById(R.id.btn_qr_download);
        btnSaveToDB = view.findViewById(R.id.btn_qr_save);

        // Get studentId passed from UsersFragment
        if (getArguments() != null && getArguments().containsKey("studentId")) {
            studentId = getArguments().getLong("studentId", -1);
            tvStudentId.setText(String.valueOf(studentId));
        }

        btnGenerateQR.setOnClickListener(v -> handleQRGeneration());

        btnDownload.setOnClickListener(v -> {
            if (generatedQRBitmap != null) {
                checkStoragePermissionAndSave(generatedQRBitmap);
            } else {
                Toast.makeText(getContext(), "QR not generated yet", Toast.LENGTH_SHORT).show();
            }
        });

        btnSaveToDB.setOnClickListener(v -> {
            if (studentId > 0 && lastQRBase64 != null) {
                MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext());
                dbHelper.updateStudentQR(studentId, lastQRBase64);
                Toast.makeText(getContext(), "QR saved to database", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Generate QR first", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void handleQRGeneration() {
        if (studentId <= 0) {
            Toast.makeText(getContext(), "Invalid Student ID", Toast.LENGTH_SHORT).show();
            return;
        }

        String subjectId = "101"; // Placeholder
        String date = java.time.LocalDate.now().toString();
        String qrContent = "attendance|" + studentId + "|" + subjectId + "|" + date;
        generateQRCode(qrContent);
    }

    private void generateQRCode(String content) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, 300, 300);
            Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.RGB_565);
            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    bitmap.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            qrImageView.setImageBitmap(bitmap);
            qrImageView.setVisibility(View.VISIBLE);
            btnDownload.setVisibility(View.VISIBLE);
            btnSaveToDB.setVisibility(View.VISIBLE);
            generatedQRBitmap = bitmap;

            lastQRBase64 = bitmapToBase64(bitmap);

            Toast.makeText(getContext(), "QR Generated", Toast.LENGTH_SHORT).show();
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error generating QR", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkStoragePermissionAndSave(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            } else {
                saveQRToDownloads(bitmap);
            }
        } else {
            saveQRToDownloads(bitmap); // No permission needed on Android 10+
        }
    }

    private void saveQRToDownloads(Bitmap bitmap) {
        String filename = "qr_code_" + System.currentTimeMillis() + ".png";
        OutputStream outputStream = null;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QR_Codes");

                Uri imageUri = requireActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                if (imageUri != null) {
                    outputStream = requireActivity().getContentResolver().openOutputStream(imageUri);
                }
            } else {
                File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "QR_Codes");
                if (!directory.exists()) directory.mkdirs();
                File imageFile = new File(directory, filename);
                outputStream = new FileOutputStream(imageFile);

                // Notify system gallery
                requireActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
            }

            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                Toast.makeText(getContext(), "QR saved to Pictures/QR_Codes", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to access storage", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to save QR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}



