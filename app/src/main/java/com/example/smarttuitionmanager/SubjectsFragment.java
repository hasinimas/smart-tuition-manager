package com.example.smarttuitionmanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class SubjectsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    // 🔽 New variables added below
    private String role = "Student";

    private Button btnAssignments, btnResults;

    public SubjectsFragment() {
        // Required empty public constructor
    }

    public static SubjectsFragment newInstance(String param1, String param2) {
        SubjectsFragment fragment = new SubjectsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1); // you can pass role here
        args.putString(ARG_PARAM2, param2); // and userId here if encoded as string
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            // Optional: Extract role and userId from param1/param2 if passed
            role = mParam1 != null ? mParam1 : "Student";

        }
    }


}
