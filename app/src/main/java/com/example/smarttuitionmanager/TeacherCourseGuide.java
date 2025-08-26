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
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;



public class TeacherCourseGuide extends Fragment {

    private TextView textTeacherSubject;
    private EditText editTextTitle;
    private TextView textSelectedFile;
    private Uri selectedPdfUri;
    private String teacherSubject = "";
    private MyDatabaseHelper dbHelper;
    private long loggedInTeacherId = -1;

    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_TEACHER_ID = "teacherId";

    private static final int PICK_PDF_REQUEST = 100;
    private static final String TAG = "TeacherCourseGuide";


    private ArrayList<String> subjectNames;
    private ArrayList<Integer> subjectIdsList;



    public TeacherCourseGuide() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new MyDatabaseHelper(requireContext());

        // Get teacher ID from shared preferences
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loggedInTeacherId = sharedPreferences.getLong(KEY_TEACHER_ID, -1);
        Log.d(TAG, "Retrieved teacher ID: " + loggedInTeacherId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_course_guide, container, false);


        textTeacherSubject = view.findViewById(R.id.text_teacher_subject);

        // Initialize views
     //   spinnerCourse = view.findViewById(R.id.spinner_course);

        editTextTitle = view.findViewById(R.id.editText_title);
        textSelectedFile = view.findViewById(R.id.text_selected_file);
        Button btnSelectPdf = view.findViewById(R.id.btn_select_pdf);
        Button btnUpload = view.findViewById(R.id.btn_upload);
        Button assignmentsBtn = view.findViewById(R.id.assignments);
        Button resultsBtn = view.findViewById(R.id.results);


        if (loggedInTeacherId != -1) {
            loadTeacherSubject();
        } else {
            Toast.makeText(getContext(), "Cannot load subject: Teacher not identified.", Toast.LENGTH_LONG).show();
            textTeacherSubject.setText("Error: Teacher not found");
        }


        // Load all available subjects into spinner
        loadAllSubjects();

        // Set up spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                subjectNames
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(spinnerAdapter);

        // Spinner item selection listener
        spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < subjectIdsList.size()) {
                    selectedSubjectId = subjectIdsList.get(position);
                    String selectedSubject = subjectNames.get(position);
                    Log.d(TAG, "Selected subject: " + selectedSubject + " (ID: " + selectedSubjectId + ")");
                    Toast.makeText(requireContext(), "Selected: " + selectedSubject, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSubjectId = -1;
            }
        });

        // PDF selection button
        btnSelectPdf.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, PICK_PDF_REQUEST);
        });

        // Upload button
        btnUpload.setOnClickListener(v -> {
            uploadMaterial();
        });

        // Navigation buttons
        assignmentsBtn.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout_teacher, new TeacherAssignment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        resultsBtn.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout_teacher, new TeacherResults());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }


    private void loadTeacherSubject() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;
        
        try {
            // Get teacher's subject directly from teacher table
            cursor = db.rawQuery("SELECT subject FROM teacher WHERE t_id = ?", 
                                new String[]{String.valueOf(loggedInTeacherId)});
            
            if (cursor != null && cursor.moveToFirst()) {
                teacherSubject = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
                textTeacherSubject.setText(teacherSubject);
                Log.d("TeacherCourseGuide", "Teacher Subject: " + teacherSubject);
            } else {
                textTeacherSubject.setText("No subject assigned");
                Toast.makeText(getContext(), "No subject assigned to this teacher.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("TeacherCourseGuide", "Error loading teacher subject: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Error loading teacher subject: " + e.getMessage(), Toast.LENGTH_LONG).show();
            textTeacherSubject.setText("Error loading subject");
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    private void selectPdfFromDevice() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 100);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK && data != null) {

            selectedPdfUri = data.getData();
            String fileName = getFileName(selectedPdfUri);
            textSelectedFile.setText(fileName);
            Log.d(TAG, "Selected PDF: " + fileName);
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = requireContext().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void uploadMaterial() {
        try {
            // Get the PDF file as bytes
            InputStream inputStream = requireContext().getContentResolver().openInputStream(selectedPdfUri);
            byte[] pdfBytes = new byte[inputStream.available()];
            inputStream.read(pdfBytes);
            inputStream.close();


        if (teacherSubject.isEmpty()) {
            Toast.makeText(getContext(), "No subject assigned to teacher.", Toast.LENGTH_SHORT).show();
            return;
        }


            // Save to database
            String title = editTextTitle.getText().toString().trim();
            String filePath = file.getAbsolutePath();

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("Subject_id", selectedSubjectId);
            values.put("title", title);
            values.put("file_path", filePath);

        // Get the subject ID for the teacher's subject
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT subject_id FROM subject WHERE name = ?", 
                                   new String[]{teacherSubject});
        
        int subjectId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            subjectId = cursor.getInt(cursor.getColumnIndexOrThrow("subject_id"));
        }
        cursor.close();

        if (subjectId == -1) {
            Toast.makeText(getContext(), "Subject not found in database.", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put("Subject_id", subjectId);
        values.put("title", title);
        values.put("file_path", filePath);
        long result = db.insert("Subject_MATERIALS", null, values);

        if (result != -1) {
            Toast.makeText(getContext(), "Material uploaded successfully", Toast.LENGTH_SHORT).show();
            editTextTitle.setText("");
            textSelectedFile.setText("No file selected");
            selectedPdfUri = null;
        } else {
            Toast.makeText(getContext(), "Upload failed", Toast.LENGTH_SHORT).show();
        }
    }


            if (result != -1) {
                Toast.makeText(requireContext(), "Material uploaded successfully", Toast.LENGTH_SHORT).show();
                resetForm();
            } else {
                Toast.makeText(requireContext(), "Failed to upload material", Toast.LENGTH_SHORT).show();

            }
        } catch (Exception e) {
            Log.e(TAG, "Error uploading material: " + e.getMessage());
            Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void resetForm() {
        editTextTitle.setText("");
        textSelectedFile.setText("No file selected");
        selectedPdfUri = null;
        spinnerCourse.setSelection(0);
    }
}

