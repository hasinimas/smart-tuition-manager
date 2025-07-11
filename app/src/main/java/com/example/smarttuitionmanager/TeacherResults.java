package com.example.smarttuitionmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class TeacherResults extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    // Declare UI components
    private Spinner spinnerCourse;
    private EditText editTextTitle;
    private TextView textSelectedFile;

    public TeacherResults() {
        // Required empty public constructor
    }

    public static TeacherResults newInstance(String param1, String param2) {
        TeacherResults fragment = new TeacherResults();
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
        // Inflate the correct layout for this fragment
        View view = inflater.inflate(R.layout.fragment_teacher_results, container, false);

        // Initialize your views
        spinnerCourse = view.findViewById(R.id.spinner_course);
        editTextTitle = view.findViewById(R.id.editText_title);
        textSelectedFile = view.findViewById(R.id.text_selected_file);

        Button btnSelectPdf = view.findViewById(R.id.btn_select_pdf);
        Button btnUpload = view.findViewById(R.id.btn_upload);
        Button assignmentsBtn = view.findViewById(R.id.assignments);
        Button courseBtn = view.findViewById(R.id.Cmaterial);

        // Call your methods to initialize spinner and other UI
        loadSubjectsIntoSpinner();

        btnSelectPdf.setOnClickListener(v -> selectPdfFromDevice());
        btnUpload.setOnClickListener(v -> uploadMaterial());

        // Navigate to TeacherAssignment fragment
        assignmentsBtn.setOnClickListener(v -> {
            Fragment teacherAssignmentFragment = new TeacherAssignment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, teacherAssignmentFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Navigate to TeacherResults fragment (could be redundant since you are here)
        courseBtn.setOnClickListener(v -> {
            Fragment teacherCmaterialFragment = new TeacherCourseGuide();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, teacherCmaterialFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    // Stub methods - implement your logic here
    private void loadSubjectsIntoSpinner() {
        // TODO: your code here
    }

    private void selectPdfFromDevice() {
        // TODO: your code here
    }

    private void uploadMaterial() {
        // TODO: your code here
    }
}
