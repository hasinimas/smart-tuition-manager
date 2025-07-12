package com.example.smarttuitionmanager;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List; // Import List for clarity

public class TeacherResults extends Fragment {

    private static final String TAG = "TeacherResults";

    private Spinner spinnerStudent, spinnerSubject;
    private EditText editTextMarks, editTextRemark;
    private Button btnAddResult, btnViewResults;
    private LinearLayout resultsContainer;

    private MyDatabaseHelper dbHelper;

    // Lists to hold IDs for spinners, to ensure correct ID retrieval
    private ArrayList<Integer> studentIdsList;
    private ArrayList<Integer> subjectIdsList;

    // *** FIX: Declare studentNames and subjectNames as class-level fields ***
    private ArrayList<String> studentNames;
    private ArrayList<String> subjectNames;


    public TeacherResults() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_results, container, false);

        dbHelper = new MyDatabaseHelper(requireContext());

        spinnerStudent = view.findViewById(R.id.spinner_students);
        spinnerSubject = view.findViewById(R.id.spinner_course);
        editTextMarks = view.findViewById(R.id.editText_marks);
        editTextRemark = view.findViewById(R.id.editText_remark); // Corrected ID here
        btnAddResult = view.findViewById(R.id.btn_addresults);
        btnViewResults = view.findViewById(R.id.btn_view);
        resultsContainer = view.findViewById(R.id.results_list_layout);

        loadStudentsIntoSpinner();
        loadSubjectsIntoSpinner();

        btnAddResult.setOnClickListener(v -> addResult());
        btnViewResults.setOnClickListener(v -> showResults());

        Button btnAssignments = view.findViewById(R.id.assignments);
        Button btnCourseMaterials = view.findViewById(R.id.Cmaterial);
        Button btnResults = view.findViewById(R.id.results);

        btnAssignments.setOnClickListener(v -> {
            Fragment teacherAssignmentFragment = new TeacherAssignment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, teacherAssignmentFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        btnCourseMaterials.setOnClickListener(v -> {
            Fragment teacherCourseGuideFragment = new TeacherCourseGuide();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, teacherCourseGuideFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        btnResults.setOnClickListener(v -> {
            Toast.makeText(getContext(), "You are already on the Results page", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void loadStudentsIntoSpinner() {
        studentNames = new ArrayList<>(); // Initialize
        studentIdsList = new ArrayList<>(); // Initialize

        Cursor cursor = null;
        try {
            cursor = dbHelper.getAllStudents();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("s_id"));
                    String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
                    String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
                    String name = firstName + " " + lastName;
                    studentIdsList.add(id);
                    studentNames.add(name);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading students into spinner: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Error loading students: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (studentNames.isEmpty()) {
            studentNames.add("No students available");
            spinnerStudent.setEnabled(false);
        } else {
            spinnerStudent.setEnabled(true);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, studentNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStudent.setAdapter(adapter);
    }

    private void loadSubjectsIntoSpinner() {
        subjectNames = new ArrayList<>(); // Initialize
        subjectIdsList = new ArrayList<>(); // Initialize

        Cursor cursor = null;
        try {
            // IMPORTANT: If you want subjects assigned to the logged-in teacher,
            // you'll need the teacher ID here and use dbHelper.getSubjectsByTeacherId(loggedInTeacherId)
            // as you did in TeacherCourseGuide.
            // For now, it uses getAllSubjects() which gets all subjects from the DB.
            cursor = dbHelper.getAllSubjects(); // Or dbHelper.getSubjectsByTeacherId(loggedInTeacherId); if you get the teacher ID

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("subject_id"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    subjectIdsList.add(id);
                    subjectNames.add(name);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading subjects into spinner: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Error loading subjects: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (subjectNames.isEmpty()) {
            subjectNames.add("No subjects available");
            spinnerSubject.setEnabled(false);
        } else {
            spinnerSubject.setEnabled(true);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, subjectNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(adapter);
    }

    private void addResult() {
        // Check if lists are not empty and selection is valid
        if (studentIdsList.isEmpty() || spinnerStudent.getSelectedItemPosition() == AdapterView.INVALID_POSITION) {
            Toast.makeText(getContext(), "No students available or selected.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (subjectIdsList.isEmpty() || spinnerSubject.getSelectedItemPosition() == AdapterView.INVALID_POSITION) {
            Toast.makeText(getContext(), "No subjects available or selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        int studentId = studentIdsList.get(spinnerStudent.getSelectedItemPosition());
        int subjectId = subjectIdsList.get(spinnerSubject.getSelectedItemPosition());

        String marksStr = editTextMarks.getText().toString().trim();
        String remark = editTextRemark.getText().toString().trim();

        if (TextUtils.isEmpty(marksStr)) {
            Toast.makeText(getContext(), "Please enter marks", Toast.LENGTH_SHORT).show();
            return;
        }

        int marks;
        try {
            marks = Integer.parseInt(marksStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid marks. Please enter a number.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "NumberFormatException for marks: " + marksStr, e);
            return;
        }

        boolean success = dbHelper.insertResult(studentId, subjectId, marks, remark);
        if (success) {
            Toast.makeText(getContext(), "Result added successfully!", Toast.LENGTH_SHORT).show();
            editTextMarks.setText("");
            editTextRemark.setText("");
            showResults();
        } else {
            Toast.makeText(getContext(), "Failed to add result.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Failed to insert result into database.");
        }
    }

    private void showResults() {
        resultsContainer.removeAllViews();

        Cursor cursor = null;
        try {
            cursor = dbHelper.getAllResults();

            if (cursor != null && cursor.moveToFirst()) {
                Log.d(TAG, "Results found: " + cursor.getCount());
                do {
                    int studentNameIndex = cursor.getColumnIndex("Name");
                    int subjectNameIndex = cursor.getColumnIndex("Subject_name");
                    int marksIndex = cursor.getColumnIndex("marks");
                    int remarkIndex = cursor.getColumnIndex("remark");

                    String studentName = (studentNameIndex != -1) ? cursor.getString(studentNameIndex) : "N/A";
                    String subjectName = (subjectNameIndex != -1) ? cursor.getString(subjectNameIndex) : "N/A";
                    int marks = (marksIndex != -1) ? cursor.getInt(marksIndex) : 0;
                    String remark = (remarkIndex != -1) ? cursor.getString(remarkIndex) : "";

                    TextView resultView = new TextView(requireContext());
                    resultView.setText(
                            "Student: " + studentName + "\n" +
                                    "Subject: " + subjectName + "\n" +
                                    "Marks: " + marks + "\n" +
                                    "Remark: " + (TextUtils.isEmpty(remark) ? "-" : remark) + "\n" +
                                    "------------------------"
                    );
                    resultView.setPadding(10, 10, 10, 10);
                    resultsContainer.addView(resultView);
                    Log.d(TAG, "Displayed result for: " + studentName + " - " + subjectName);

                } while (cursor.moveToNext());
            } else {
                TextView noResult = new TextView(requireContext());
                noResult.setText("No results found.");
                noResult.setPadding(10, 10, 10, 10);
                resultsContainer.addView(noResult);
                Log.d(TAG, "No results found in database.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error displaying results: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Error displaying results: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}