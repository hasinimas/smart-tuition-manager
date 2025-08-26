package com.example.smarttuitionmanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.SharedPreferences;

public class StudentCourseGuide extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private LinearLayout materialListLayout;
    private MyDatabaseHelper myDatabaseHelper;

    public StudentCourseGuide() {
        // Required empty public constructor
    }

    public static StudentCourseGuide newInstance(String param1, String param2) {
        StudentCourseGuide fragment = new StudentCourseGuide();
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
        myDatabaseHelper = new MyDatabaseHelper(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_course_guide, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        materialListLayout = view.findViewById(R.id.material_list);
        loadMaterialsFromDatabase();

        // Show logged-in student's subject(s)
        TextView subjectTextView = view.findViewById(R.id.text_student_subject);
        SharedPreferences prefs = requireContext().getSharedPreferences("LoginPrefs", 0);
        long studentId = prefs.getLong("user_id", -1);
        if (studentId != -1) {
            SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(
                "SELECT sub.name FROM subject sub " +
                "JOIN student_subject ss ON sub.subject_id = ss.subject_id " +
                "WHERE ss.student_id = ?",
                new String[]{String.valueOf(studentId)}
            );
            StringBuilder subjects = new StringBuilder();
            while (cursor.moveToNext()) {
                if (subjects.length() > 0) subjects.append(", ");
                subjects.append(cursor.getString(0));
            }
            cursor.close();
            db.close();
            if (subjects.length() > 0) {
                subjectTextView.setText(subjects.toString());
            } else {
                subjectTextView.setText("No subjects assigned");
            }
        } else {
            subjectTextView.setText("Error: Student not found");
        }

        // Navigation Buttons
        Button cmaterialBtn = view.findViewById(R.id.Cmaterial);
        Button assignmentBtn = view.findViewById(R.id.assignments);
        Button resultsBtn = view.findViewById(R.id.results);

        // Navigate to Course Materials (Already in this fragment)
        cmaterialBtn.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Already viewing Course Materials", Toast.LENGTH_SHORT).show();
        });

        // Navigate to Assignments
        assignmentBtn.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragment_container, new StudentAssignment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Navigate to Results
        resultsBtn.setOnClickListener(v -> {
            FragmentTransaction transaction = requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(R.id.fragment_container, new StudentResults());
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    private void loadMaterialsFromDatabase() {
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT title, file_path FROM Subject_MATERIALS", null);

        if (cursor.moveToFirst()) {
            int titleIndex = cursor.getColumnIndexOrThrow("title");
            int filePathIndex = cursor.getColumnIndexOrThrow("file_path");

            do {
                String title = cursor.getString(titleIndex);
                String filePath = cursor.getString(filePathIndex);

                TextView materialItem = new TextView(requireContext());
                materialItem.setText("📄 " + title);
                materialItem.setTextColor(Color.BLACK);
                materialItem.setTextSize(16);
                materialItem.setPadding(8, 8, 8, 8);

                materialItem.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(filePath), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                    if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(requireContext(), "No app found to open PDF", Toast.LENGTH_SHORT).show();
                    }
                });

                materialListLayout.addView(materialItem);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }
}
