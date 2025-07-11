package com.example.smarttuitionmanager;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
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

public class TeacherCourseGuide extends Fragment {

    private Spinner spinnerCourse;
    private EditText editTextTitle;
    private TextView textSelectedFile;
    private Uri selectedPdfUri;
    private int selectedSubjectId = -1;
    private MyDatabaseHelper dbHelper;

    public TeacherCourseGuide() {
        // Required empty public constructor
    }

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
        Button assignmentsBtn = view.findViewById(R.id.assignments); // Assignment button
        Button resultsBtn = view.findViewById(R.id.results);


        loadSubjectsIntoSpinner();

        btnSelectPdf.setOnClickListener(v -> selectPdfFromDevice());
        btnUpload.setOnClickListener(v -> uploadMaterial());

        // ✅ Navigate to TeacherAssignment fragment on button click
        assignmentsBtn.setOnClickListener(v -> {
            Fragment teacherAssignmentFragment = new TeacherAssignment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, teacherAssignmentFragment); // Make sure this ID matches your activity layout
            transaction.addToBackStack(null);
            transaction.commit();
        });
        // ✅ Navigate to TeacherResults fragment on button click
        resultsBtn.setOnClickListener(v -> {
            Fragment teacherResultsFragment = new TeacherResults();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, teacherResultsFragment); // Make sure this ID matches your activity layout
            transaction.addToBackStack(null);
            transaction.commit();
        });
        return view;
    }

    private void loadSubjectsIntoSpinner() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> subjectNames = new ArrayList<>();
        List<Integer> subjectIds = new ArrayList<>();

        // Replace 1 with actual teacher_id from login/session
        Cursor cursor = db.rawQuery("SELECT Subject_id, Subject_name FROM Subject WHERE teacher_id = ?", new String[]{"1"});
        while (cursor.moveToNext()) {
            subjectIds.add(cursor.getInt(0));
            subjectNames.add(cursor.getString(1));
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, subjectNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(adapter);

        spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSubjectId = subjectIds.get(position);
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
        }
    }

    private String getFileName(Uri uri) {
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        String fileName = cursor.getString(nameIndex);
        cursor.close();
        return fileName;
    }

    private void uploadMaterial() {
        String title = editTextTitle.getText().toString().trim();

        // If PDF not selected, skip saving file and leave filePath null
        String filePath = null;
        if (selectedPdfUri != null) {
            filePath = savePdfToInternalStorage(selectedPdfUri, title);
            if (filePath == null) {
                Toast.makeText(getContext(), "File saving failed", Toast.LENGTH_SHORT).show();
                return;
            }
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
            File file = new File(dir, title + ".pdf");
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
            e.printStackTrace();
            return null;
        }
    }
}
