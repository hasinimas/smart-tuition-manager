package com.example.smarttuitionmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class StudentResults extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Spinner spinnerCourse;
    private Spinner spinnerName;
    private Button btnUpload;

    public StudentResults() {
        // Required empty public constructor
    }

    public static StudentResults newInstance(String param1, String param2) {
        StudentResults fragment = new StudentResults();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_results, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerCourse = view.findViewById(R.id.spinner_course);
    //    spinnerName = view.findViewById(R.id.spinner_name);
        btnUpload = view.findViewById(R.id.btn_upload);

        Button cmaterialBtn = view.findViewById(R.id.Cmaterial);
        Button assignmentBtn = view.findViewById(R.id.assignments);
        Button resultsBtn = view.findViewById(R.id.results); // Already in this fragment

        // Upload button action
        btnUpload.setOnClickListener(v -> {
            // TODO: Save result to database or show a message
        });

        // Navigation: Course Materials
        cmaterialBtn.setOnClickListener(v -> {
            Fragment studentCourseGuideFragment = new StudentCourseGuide();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, studentCourseGuideFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Navigation: Assignments
        assignmentBtn.setOnClickListener(v -> {
            Fragment studentAssignmentFragment = new StudentAssignment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, studentAssignmentFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Clicking "Results" does nothing because you're already here
        resultsBtn.setOnClickListener(v -> {
            // Optional: Show a message or refresh the page
        });

        // Load spinner data
        loadSubjectsIntoSpinner();
    }

    private void loadSubjectsIntoSpinner() {
        // TODO: Populate both spinnerCourse and spinnerName
    }
}