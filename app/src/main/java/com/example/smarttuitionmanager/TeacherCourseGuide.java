package com.example.smarttuitionmanager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context; // Import Context for SharedPreferences
import android.content.Intent;
import android.content.SharedPreferences; // Import SharedPreferences
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log; // Import Log for debugging
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


// ... (all imports remain unchanged)

public class TeacherCourseGuide extends Fragment {

    private Spinner spinnerCourse;
    private EditText editTextTitle;
    private TextView textSelectedFile;
    private Uri selectedPdfUri;
    private int selectedSubjectId = -1;
    private MyDatabaseHelper dbHelper;
    private long loggedInTeacherId = -1;

    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_TEACHER_ID = "teacherId"; // this is t_id

    public TeacherCourseGuide() {}

    public static TeacherCourseGuide newInstance(String param1, String param2) {
        TeacherCourseGuide fragment = new TeacherCourseGuide();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new MyDatabaseHelper(getContext());

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loggedInTeacherId = sharedPreferences.getLong(KEY_TEACHER_ID, -1L);
        Log.d("TeacherCourseGuide", "Retrieved t_id: " + loggedInTeacherId);

        if (loggedInTeacherId == -1) {
            Toast.makeText(getContext(), "Error: Teacher ID not found. Please log in.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_course_guide, container, false);

        spinnerCourse = view.findViewById(R.id.spinner_course);
        editTextTitle = view.findViewById(R.id.editText_title);
        textSelectedFile = view.findViewById(R.id.text_selected_file);
        Button btnSelectPdf = view.findViewById(R.id.btn_select_pdf);
        Button btnUpload = view.findViewById(R.id.btn_upload);
        Button assignmentsBtn = view.findViewById(R.id.assignments);
        Button resultsBtn = view.findViewById(R.id.results);

        if (loggedInTeacherId != -1) {
            loadSubjectsIntoSpinner();
        } else {
            Toast.makeText(getContext(), "Cannot load subjects: Teacher not identified.", Toast.LENGTH_LONG).show();
            spinnerCourse.setEnabled(false);
        }

        btnSelectPdf.setOnClickListener(v -> selectPdfFromDevice());
        btnUpload.setOnClickListener(v -> uploadMaterial());

        assignmentsBtn.setOnClickListener(v -> {
            Fragment teacherAssignmentFragment = new TeacherAssignment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, teacherAssignmentFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        resultsBtn.setOnClickListener(v -> {
            Fragment teacherResultsFragment = new TeacherResults();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, teacherResultsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    private void loadSubjectsIntoSpinner() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> subjectNames = new ArrayList<>();
        List<Integer> subjectIds = new ArrayList<>();

        Cursor cursor = null;
        try {
            // ✅ Fixed: Use actual method from DB helper
            cursor = dbHelper.getSubjectsByTeacherId(loggedInTeacherId);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    subjectIds.add(cursor.getInt(cursor.getColumnIndexOrThrow("subject_id")));
                    subjectNames.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                }
            }
        } catch (Exception e) {
            Log.e("TeacherCourseGuide", "Error loading subjects: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Error loading subjects: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) cursor.close();
        }

        if (subjectNames.isEmpty()) {
            subjectNames.add("No subjects available");
            spinnerCourse.setEnabled(false);
            Toast.makeText(getContext(), "No subjects assigned to this teacher.", Toast.LENGTH_SHORT).show();
        } else {
            spinnerCourse.setEnabled(true);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, subjectNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(adapter);

        spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!subjectIds.isEmpty() && position < subjectIds.size()) {
                    selectedSubjectId = subjectIds.get(position);
                } else {
                    selectedSubjectId = -1;
                }
                Log.d("TeacherCourseGuide", "Selected Subject ID: " + selectedSubjectId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSubjectId = -1;
            }
        });
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
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPdfUri = data.getData();
            String fileName = getFileName(selectedPdfUri);
            textSelectedFile.setText(fileName);
            Log.d("TeacherCourseGuide", "Selected PDF: " + fileName + ", URI: " + selectedPdfUri);
        }
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
            Log.e("TeacherCourseGuide", "Error getting file name: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return result;
    }

    private void uploadMaterial() {
        String title = editTextTitle.getText().toString().trim();

        if (selectedSubjectId == -1) {
            Toast.makeText(getContext(), "Please select a subject.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a title for the material.", Toast.LENGTH_SHORT).show();
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

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Subject_id", selectedSubjectId);
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

    private String savePdfToInternalStorage(Uri uri, String title) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            File dir = new File(getContext().getFilesDir(), "materials");
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
            Log.e("TeacherCourseGuide", "Error saving PDF to internal storage: " + e.getMessage(), e);
            return null;
        }
    }
}
