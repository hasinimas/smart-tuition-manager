package com.example.smarttuitionmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class TeacherAssignment extends Fragment {

    private Spinner spinnerCourse;
    private EditText editTextTitle;
    private TextView textSelectedFile;

    public TeacherAssignment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the correct layout file for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_assignment, container, false);

        // Initialize views
        spinnerCourse = view.findViewById(R.id.spinner_course);
        editTextTitle = view.findViewById(R.id.editText_title);
        textSelectedFile = view.findViewById(R.id.text_selected_file);
        Button btnSelectPdf = view.findViewById(R.id.btn_select_pdf);
        Button btnUpload = view.findViewById(R.id.btn_upload);
        Button courseBtn = view.findViewById(R.id.Cmaterial);
        Button resultsBtn = view.findViewById(R.id.results);

        // Load subjects into spinner - implement this method in your class
        loadSubjectsIntoSpinner();

        btnSelectPdf.setOnClickListener(v -> selectPdfFromDevice());
        btnUpload.setOnClickListener(v -> uploadMaterial());

        // Navigate to TeacherAssignment fragment on assignments button click
        courseBtn.setOnClickListener(v -> {
            Fragment teacherCourseGuide = new TeacherCourseGuide();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, teacherCourseGuide);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Navigate to TeacherResults fragment on results button click
        resultsBtn.setOnClickListener(v -> {
            Fragment teacherResultsFragment = new TeacherResults();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, teacherResultsFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    // Define these methods according to your app logic
    private void loadSubjectsIntoSpinner() {
        // Your code to load subjects
    }

    private void selectPdfFromDevice() {
        // Your code to select PDF
    }

    private void uploadMaterial() {
        // Your code to upload material
    }
}
