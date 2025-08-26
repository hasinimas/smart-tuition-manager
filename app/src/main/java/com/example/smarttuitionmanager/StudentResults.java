package com.example.smarttuitionmanager;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

public class StudentResults extends Fragment {

    private Spinner spinnerStudent, spinnerSubject;
    private Button btnViewResults;
    private LinearLayout resultsContainer;
    private MyDatabaseHelper dbHelper;

    private ArrayList<Integer> studentIdsList = new ArrayList<>();
    private ArrayList<Integer> subjectIdsList = new ArrayList<>();

    private static final String TAG = "StudentResults";

    public StudentResults() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_results, container, false);

        dbHelper = new MyDatabaseHelper(requireContext());

        // ✅ Spinner & View Bindings
        spinnerStudent = view.findViewById(R.id.spinner_students);
        spinnerSubject = view.findViewById(R.id.spinner_course); // ✅ You missed this before!
        btnViewResults = view.findViewById(R.id.btn_view);
        resultsContainer = view.findViewById(R.id.results_list_layout);

        loadStudentsIntoSpinner();
        loadSubjectsIntoSpinner();

        btnViewResults.setOnClickListener(v -> showResults());

        // ✅ Top Navigation Button Handling
        Button btnassignments = view.findViewById(R.id.assignments);
        Button btncourseMaterials = view.findViewById(R.id.Cmaterial);
        Button btnResults = view.findViewById(R.id.results);

        btnassignments.setOnClickListener(v -> {
            Fragment StudentAssignmentFragment = new StudentAssignment(); // or StudentAssignment
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, StudentAssignmentFragment );
            transaction.addToBackStack(null);
            transaction.commit();
        });

        btncourseMaterials.setOnClickListener(v -> {
            Fragment StudentCourseGuideFragment = new StudentCourseGuide(); // or StudentCourseGuide
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, StudentCourseGuideFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        btnResults.setOnClickListener(v -> {
            Toast.makeText(getContext(), "You are already on the Results page", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void loadStudentsIntoSpinner() {
        Cursor cursor = dbHelper.getAllStudents();
        ArrayList<String> studentNames = new ArrayList<>();
        studentIdsList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("s_id"));
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
                studentNames.add(firstName + " " + lastName);
                studentIdsList.add(id);
            } while (cursor.moveToNext());
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, studentNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStudent.setAdapter(adapter);
    }

    private void loadSubjectsIntoSpinner() {
        Cursor cursor = dbHelper.getAllSubjects();
        ArrayList<String> subjectNames = new ArrayList<>();
        subjectIdsList.clear();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("subject_id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                subjectNames.add(name);
                subjectIdsList.add(id);
            } while (cursor.moveToNext());
            cursor.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, subjectNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(adapter);
    }

    private void showResults() {
        resultsContainer.removeAllViews();

        if (studentIdsList.isEmpty() || subjectIdsList.isEmpty()) {
            Toast.makeText(getContext(), "Please select both student and subject.", Toast.LENGTH_SHORT).show();
            return;
        }

        int studentId = studentIdsList.get(spinnerStudent.getSelectedItemPosition());
        int subjectId = subjectIdsList.get(spinnerSubject.getSelectedItemPosition());

        Cursor cursor = null;
        try {
            cursor = dbHelper.getResultsByStudentAndSubject(studentId, subjectId);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String studentName = cursor.getString(cursor.getColumnIndexOrThrow("Name"));
                    String subjectName = cursor.getString(cursor.getColumnIndexOrThrow("Subject_name"));
                    int marks = cursor.getInt(cursor.getColumnIndexOrThrow("marks"));
                    String remark = cursor.getString(cursor.getColumnIndexOrThrow("remark"));

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
                } while (cursor.moveToNext());
            } else {
                TextView noResult = new TextView(requireContext());
                noResult.setText("No results found for the selected student and subject.");
                noResult.setPadding(10, 10, 10, 10);
                resultsContainer.addView(noResult);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing results: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
