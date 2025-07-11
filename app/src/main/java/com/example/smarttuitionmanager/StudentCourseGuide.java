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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.smarttuitionmanager.MyDatabaseHelper;
import com.example.smarttuitionmanager.R;

public class StudentCourseGuide extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private LinearLayout materialListLayout;
    private MyDatabaseHelper myDatabaseHelper;  // Your SQLiteOpenHelper subclass

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
        myDatabaseHelper = new MyDatabaseHelper(requireContext());  // Initialize DB helper here
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_course_guide, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        materialListLayout = view.findViewById(R.id.material_list);

        loadMaterialsFromDatabase();
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
