package com.example.smarttuitionmanager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.TextView;
import androidx.cardview.widget.CardView;


public class ProfileFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private MyDatabaseHelper.StudentWithSubjects student;

    public ProfileFragment() {
        // Required empty public constructor
    }


    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        // Read-only views
        CardView cardReport = view.findViewById(R.id.card_profile_report);
        TextView tvFirstName = view.findViewById(R.id.tv_profile_first_name);
        TextView tvLastName = view.findViewById(R.id.tv_profile_last_name);
        TextView tvPhone = view.findViewById(R.id.tv_profile_phone);
        TextView tvEmail = view.findViewById(R.id.tv_profile_email);
        TextView tvPassword = view.findViewById(R.id.tv_profile_password);
        Button btnUpdate = view.findViewById(R.id.btn_profile_update);
        // Edit form views
        CardView cardEdit = view.findViewById(R.id.card_profile_edit);
        EditText etFirstName = view.findViewById(R.id.et_profile_first_name);
        EditText etLastName = view.findViewById(R.id.et_profile_last_name);
        EditText etPhone = view.findViewById(R.id.et_profile_phone);
        EditText etEmail = view.findViewById(R.id.et_profile_email);
        EditText etPassword = view.findViewById(R.id.et_profile_password);
        Button btnSave = view.findViewById(R.id.btn_profile_save);
        Button btnCancel = view.findViewById(R.id.btn_profile_cancel);

        SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String email = prefs.getString("student_email", null);
        if (email != null) {
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(requireContext());
            for (MyDatabaseHelper.StudentWithSubjects s : dbHelper.getAllStudentsWithSubjects()) {
                if (email.equals(s.email)) {
                    student = s;
                    break;
                }
            }
        }
        if (student != null) {
            // Set read-only views
            tvFirstName.setText("First Name: " + student.firstName);
            tvLastName.setText("Last Name: " + student.lastName);
            tvPhone.setText("Phone: " + student.phoneNumber);
            tvEmail.setText("Email: " + student.email);
            tvPassword.setText("Password: " + student.password);
            // Set edit form fields
            etFirstName.setText(student.firstName);
            etLastName.setText(student.lastName);
            etPhone.setText(student.phoneNumber);
            etEmail.setText(student.email);
            etPassword.setText(student.password);
        }
        // Show report card, hide edit form
        cardReport.setVisibility(View.VISIBLE);
        cardEdit.setVisibility(View.GONE);

        btnUpdate.setOnClickListener(v -> {
            // Show edit form, hide report card
            cardReport.setVisibility(View.GONE);
            cardEdit.setVisibility(View.VISIBLE);
        });
        btnCancel.setOnClickListener(v -> {
            // Hide edit form, show report card, reset edit fields to original values
            if (student != null) {
                etFirstName.setText(student.firstName);
                etLastName.setText(student.lastName);
                etPhone.setText(student.phoneNumber);
                etEmail.setText(student.email);
                etPassword.setText(student.password);
            }
            cardEdit.setVisibility(View.GONE);
            cardReport.setVisibility(View.VISIBLE);
        });
        btnSave.setOnClickListener(v -> {
            String newFirstName = etFirstName.getText().toString().trim();
            String newLastName = etLastName.getText().toString().trim();
            String newPhone = etPhone.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();
            String newPassword = etPassword.getText().toString().trim();
            boolean valid = true;
            if (newFirstName.isEmpty()) {
                etFirstName.setError("First name is required");
                valid = false;
            } else {
                etFirstName.setError(null);
            }
            if (newLastName.isEmpty()) {
                etLastName.setError("Last name is required");
                valid = false;
            } else {
                etLastName.setError(null);
            }
            if (newPhone.isEmpty()) {
                etPhone.setError("Phone number is required");
                valid = false;
            } else if (!newPhone.matches("\\d{10,15}")) {
                etPhone.setError("Enter a valid phone number");
                valid = false;
            } else {
                etPhone.setError(null);
            }
            if (newEmail.isEmpty()) {
                etEmail.setError("Email is required");
                valid = false;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                etEmail.setError("Enter a valid email address");
                valid = false;
            } else {
                etEmail.setError(null);
            }
            if (newPassword.isEmpty()) {
                etPassword.setError("Password is required");
                valid = false;
            } else if (newPassword.length() < 6) {
                etPassword.setError("Password must be at least 6 characters");
                valid = false;
            } else {
                etPassword.setError(null);
            }
            if (!valid) return;
            if (student != null) {
                MyDatabaseHelper dbHelper = new MyDatabaseHelper(requireContext());
                dbHelper.updateStudent(student.sId, newFirstName, newLastName, student.grade, newPhone, student.guardianTP, newEmail, newPassword);
                Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                // Update stored email if changed
                prefs.edit().putString("student_email", newEmail).apply();
                // Update local student object
                student.firstName = newFirstName;
                student.lastName = newLastName;
                student.phoneNumber = newPhone;
                student.email = newEmail;
                student.password = newPassword;
                // Update read-only views
                tvFirstName.setText("First Name: " + newFirstName);
                tvLastName.setText("Last Name: " + newLastName);
                tvPhone.setText("Phone: " + newPhone);
                tvEmail.setText("Email: " + newEmail);
                tvPassword.setText("Password: " + newPassword);
                // Switch back to report card
                cardEdit.setVisibility(View.GONE);
                cardReport.setVisibility(View.VISIBLE);
            }
        });
        return view;
    }
}