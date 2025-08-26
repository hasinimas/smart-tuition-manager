package com.example.smarttuitionmanager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Intent;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.*;
import android.widget.*;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.*;
import java.util.ArrayList;


    private EditText editTextTitle;
    private TextView textSelectedFile;
    private TextView textTeacherSubject;
    private String teacherSubject = "";
    private MyDatabaseHelper dbHelper;

    private Uri selectedPdfUri;

public class TeacherAssignment extends Fragment {
    private Spinner spinnerCourse;
    private EditText editTextTitle;
    private TextView textSelectedFile;
    private Uri selectedPdfUri;
    private int selectedSubjectId = -1;
    private MyDatabaseHelper dbHelper;
    private long loggedInTeacherId = -1;

    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_TEACHER_ID = "teacherId";
    private static final int PICK_PDF_REQUEST = 100;
    private static final String TAG = "TeacherAssignment";

    private ArrayList<String> subjectNames;
    private ArrayList<Integer> subjectIdsList;

    public TeacherAssignment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new MyDatabaseHelper(requireContext());

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loggedInTeacherId = sharedPreferences.getLong(KEY_TEACHER_ID, -1);
        Log.d(TAG, "Retrieved teacher ID: " + loggedInTeacherId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_assignment, container, false);


        // Initialize views
        textTeacherSubject = view.findViewById(R.id.text_teacher_subject);

        editTextTitle = view.findViewById(R.id.editText_title);
        textSelectedFile = view.findViewById(R.id.text_selected_file);
        Button btnSelectPdf = view.findViewById(R.id.btn_select_pdf);
        Button btnUpload = view.findViewById(R.id.btn_upload);
        Button CmaterialBtn = view.findViewById(R.id.Cmaterial);
        Button resultsBtn = view.findViewById(R.id.results);

        dbHelper = new MyDatabaseHelper(getContext());

        // Show logged-in teacher's subject
        SharedPreferences prefs = requireContext().getSharedPreferences("LoginPrefs", 0);
        long teacherId = prefs.getLong("teacherId", -1);
        if (teacherId != -1) {
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT subject FROM teacher WHERE t_id = ?", new String[]{String.valueOf(teacherId)});
            if (cursor != null && cursor.moveToFirst()) {
                teacherSubject = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
                textTeacherSubject.setText(teacherSubject);
            } else {
                textTeacherSubject.setText("No subject assigned");
            }
            if (cursor != null) cursor.close();
            db.close();
        } else {
            textTeacherSubject.setText("Error: Teacher not found");
        }

        // Add this to handle PDF selection result
        requireActivity().getSupportFragmentManager().setFragmentResultListener("select_pdf_result", this, (requestKey, result) -> {
            if (result.containsKey("pdf_uri")) {
                selectedPdfUri = result.getParcelable("pdf_uri");
                if (selectedPdfUri != null) {
                    String fileName = getFileName(selectedPdfUri);
                    textSelectedFile.setText(fileName);
                    Log.d("TeacherAssignment", "Selected PDF: " + fileName + ", URI: " + selectedPdfUri);
                }
            }

        });

        btnUpload.setOnClickListener(v -> uploadMaterial());


        // Navigate to TeacherCourseGuide fragment on course button click
        courseBtn.setOnClickListener(v -> {
            Fragment teacherCourseGuide = new TeacherCourseGuide();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, teacherCourseGuide);
            transaction.addToBackStack(null);
            transaction.commit();

        });

        resultsBtn.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new TeacherResults()); // ✅ use correct container ID
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == android.app.Activity.RESULT_OK && data != null) {
            selectedPdfUri = data.getData();
            String fileName = getFileName(selectedPdfUri);
            textSelectedFile.setText(fileName);
            Log.d("TeacherAssignment", "Selected PDF: " + fileName + ", URI: " + selectedPdfUri);
        }
    }

    // Define these methods according to your app logic
    private void selectPdfFromDevice() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 100);
    }

    private String getFileName(Uri uri) {
        String result = null;
        Cursor cursor = null;
        try {
            cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    result = cursor.getString(nameIndex);
                }
            }
        } catch (Exception e) {
            Log.e("TeacherAssignment", "Error getting file name: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();

        }
        return result;
    }

    private void uploadMaterial() {

        String title = editTextTitle.getText().toString().trim();

        if (teacherSubject.isEmpty()) {
            Toast.makeText(getContext(), "No subject assigned to teacher.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a title for the assignment.", Toast.LENGTH_SHORT).show();
            return;
        }

        String filePath = null;
        if (selectedPdfUri != null) {
            filePath = savePdfToInternalStorage(selectedPdfUri, title);
            if (filePath == null) {
                Toast.makeText(getContext(), "File saving failed", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(getContext(), "Please select a PDF file to upload.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the subject ID for the teacher's subject
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT subject_id FROM subject WHERE name = ?", new String[]{teacherSubject});
        int subjectId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            subjectId = cursor.getInt(cursor.getColumnIndexOrThrow("subject_id"));
        }
        if (cursor != null) cursor.close();

        if (subjectId == -1) {
            Toast.makeText(getContext(), "Subject not found in database.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Insert into ASSIGNMENTS table (plural, with correct columns)
        android.content.ContentValues values = new android.content.ContentValues();
        values.put("Title", title);
        values.put("Description", ""); // Empty for now
        values.put("Subject_id", subjectId);
        values.put("Deadline", ""); // Empty for now
        // Optionally, you could store the file path in Description or add a new column if needed

        try {
            long result = db.insert("ASSIGNMENTS", null, values);
            if (result == -1) {
                Log.e("TeacherAssignment", "Insert failed: " + values.toString());
                Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Assignment uploaded successfully", Toast.LENGTH_SHORT).show();
                editTextTitle.setText("");
                textSelectedFile.setText("No file selected");
                selectedPdfUri = null;
            }
        } catch (Exception e) {
            Log.e("TeacherAssignment", "DB error: " + e.getMessage(), e);
            Toast.makeText(getContext(), "DB error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String savePdfToInternalStorage(Uri uri, String title) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            File dir = new File(getContext().getFilesDir(), "assignments");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, title.replaceAll("[^a-zA-Z0-9.-]", "_") + ".pdf");
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e("TeacherAssignment", "Error saving PDF to internal storage: " + e.getMessage(), e);
            return null;
        }

    }
}
