package com.example.smarttuitionmanager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
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
    private MyDatabaseHelper.Teacher teacher;

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
        TextView tvGrade = view.findViewById(R.id.tv_profile_grade);
        TextView tvPhone = view.findViewById(R.id.tv_profile_phone);
        TextView tvGuardianPhone = view.findViewById(R.id.tv_profile_guardian_phone);
        TextView tvEmail = view.findViewById(R.id.tv_profile_email);
        TextView tvPassword = view.findViewById(R.id.tv_profile_password);
        Button btnUpdate = view.findViewById(R.id.btn_profile_update);
        Button btnLogout = view.findViewById(R.id.btn_profile_logout);
        
        // Edit form views
        CardView cardEdit = view.findViewById(R.id.card_profile_edit);
        EditText etFirstName = view.findViewById(R.id.et_profile_first_name);
        EditText etLastName = view.findViewById(R.id.et_profile_last_name);
        EditText etGrade = view.findViewById(R.id.et_profile_grade);
        EditText etPhone = view.findViewById(R.id.et_profile_phone);
        EditText etGuardianPhone = view.findViewById(R.id.et_profile_guardian_phone);
        EditText etEmail = view.findViewById(R.id.et_profile_email);
        EditText etPassword = view.findViewById(R.id.et_profile_password);
        Button btnSave = view.findViewById(R.id.btn_profile_save);
        Button btnCancel = view.findViewById(R.id.btn_profile_cancel);

        // Get user info from SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        String userRole = prefs.getString("user_role", "");
        long userId = prefs.getLong("user_id", -1);
        String userEmail = prefs.getString("user_email", "");

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(requireContext());

        // Load user data based on role
        if ("student".equals(userRole) && userId != -1) {
            try {
                // Load student data
                for (MyDatabaseHelper.StudentWithSubjects s : dbHelper.getAllStudentsWithSubjects()) {
                    if (s != null && s.sId == userId) {
                        student = s;
                        break;
                    }
                }
                
                if (student != null) {
                    // Set read-only views for student
                    tvFirstName.setText("First Name: " + (student.firstName != null ? student.firstName : ""));
                    tvLastName.setText("Last Name: " + (student.lastName != null ? student.lastName : ""));
                    tvGrade.setText("Grade: " + (student.grade != null ? student.grade : ""));
                    tvPhone.setText("Phone: " + (student.phoneNumber != null ? student.phoneNumber : ""));
                    tvGuardianPhone.setText("Guardian Phone: " + (student.guardianTP != null ? student.guardianTP : ""));
                    tvEmail.setText("Email: " + (student.email != null ? student.email : ""));
                    tvPassword.setText("Password: " + (student.password != null ? student.password : ""));
                    
                    // Set edit form fields for student
                    etFirstName.setText(student.firstName != null ? student.firstName : "");
                    etLastName.setText(student.lastName != null ? student.lastName : "");
                    etGrade.setText(student.grade != null ? student.grade : "");
                    etPhone.setText(student.phoneNumber != null ? student.phoneNumber : "");
                    etGuardianPhone.setText(student.guardianTP != null ? student.guardianTP : "");
                    etEmail.setText(student.email != null ? student.email : "");
                    etPassword.setText(student.password != null ? student.password : "");
                    
                    // Show student-specific fields
                    tvGrade.setVisibility(View.VISIBLE);
                    tvGuardianPhone.setVisibility(View.VISIBLE);
                    etGrade.setVisibility(View.VISIBLE);
                    etGuardianPhone.setVisibility(View.VISIBLE);
                } else {
                    // Handle case where student not found
                    Toast.makeText(requireContext(), "Student profile not found", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error loading student profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if ("teacher".equals(userRole) && userId != -1) {
            try {
                // Load teacher data
                for (MyDatabaseHelper.Teacher t : dbHelper.getAllTeachers()) {
                    if (t != null && t.tId == userId) {
                        teacher = t;
                        break;
                    }
                }
                
                if (teacher != null) {
                    // Set read-only views for teacher
                    tvFirstName.setText("First Name: " + (teacher.firstName != null ? teacher.firstName : ""));
                    tvLastName.setText("Last Name: " + (teacher.lastName != null ? teacher.lastName : ""));
                    tvGrade.setText("Class: " + (teacher.className != null ? teacher.className : ""));
                    tvPhone.setText("Phone: " + (teacher.phoneNumber != null ? teacher.phoneNumber : ""));
                    tvGuardianPhone.setText("ID Number: " + (teacher.idNumber != null ? teacher.idNumber : ""));
                    tvEmail.setText("Email: " + (teacher.email != null ? teacher.email : ""));
                    tvPassword.setText("Password: " + (teacher.password != null ? teacher.password : ""));
                    
                    // Set edit form fields for teacher
                    etFirstName.setText(teacher.firstName != null ? teacher.firstName : "");
                    etLastName.setText(teacher.lastName != null ? teacher.lastName : "");
                    etGrade.setText(teacher.className != null ? teacher.className : "");
                    etPhone.setText(teacher.phoneNumber != null ? teacher.phoneNumber : "");
                    etGuardianPhone.setText(teacher.idNumber != null ? teacher.idNumber : "");
                    etEmail.setText(teacher.email != null ? teacher.email : "");
                    etPassword.setText(teacher.password != null ? teacher.password : "");
                    
                    // Show teacher-specific fields
                    tvGrade.setVisibility(View.VISIBLE);
                    tvGuardianPhone.setVisibility(View.VISIBLE);
                    etGrade.setVisibility(View.VISIBLE);
                    etGuardianPhone.setVisibility(View.VISIBLE);
                } else {
                    // Handle case where teacher not found
                    Toast.makeText(requireContext(), "Teacher profile not found", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error loading teacher profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            // Handle case where user role is not recognized or user ID is invalid
            Toast.makeText(requireContext(), "User profile not found or invalid user type", Toast.LENGTH_SHORT).show();
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
                etFirstName.setText(student.firstName != null ? student.firstName : "");
                etLastName.setText(student.lastName != null ? student.lastName : "");
                etGrade.setText(student.grade != null ? student.grade : "");
                etPhone.setText(student.phoneNumber != null ? student.phoneNumber : "");
                etGuardianPhone.setText(student.guardianTP != null ? student.guardianTP : "");
                etEmail.setText(student.email != null ? student.email : "");
                etPassword.setText(student.password != null ? student.password : "");
            } else if (teacher != null) {
                etFirstName.setText(teacher.firstName != null ? teacher.firstName : "");
                etLastName.setText(teacher.lastName != null ? teacher.lastName : "");
                etGrade.setText(teacher.className != null ? teacher.className : "");
                etPhone.setText(teacher.phoneNumber != null ? teacher.phoneNumber : "");
                etGuardianPhone.setText(teacher.idNumber != null ? teacher.idNumber : "");
                etEmail.setText(teacher.email != null ? teacher.email : "");
                etPassword.setText(teacher.password != null ? teacher.password : "");
            }
            cardEdit.setVisibility(View.GONE);
            cardReport.setVisibility(View.VISIBLE);
        });
        
        btnSave.setOnClickListener(v -> {
            String newFirstName = etFirstName.getText().toString().trim();
            String newLastName = etLastName.getText().toString().trim();
            String newGrade = etGrade.getText().toString().trim();
            String newPhone = etPhone.getText().toString().trim();
            String newGuardianPhone = etGuardianPhone.getText().toString().trim();
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
            
            // Validate fields based on user role
            if ("student".equals(userRole)) {
                if (newGrade.isEmpty()) {
                    etGrade.setError("Grade is required");
                    valid = false;
                } else {
                    etGrade.setError(null);
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
                if (newGuardianPhone.isEmpty()) {
                    etGuardianPhone.setError("Guardian phone number is required");
                    valid = false;
                } else if (!newGuardianPhone.matches("\\d{10,15}")) {
                    etGuardianPhone.setError("Enter a valid phone number");
                    valid = false;
                } else {
                    etGuardianPhone.setError(null);
                }
            } else if ("teacher".equals(userRole)) {
                if (newGrade.isEmpty()) {
                    etGrade.setError("Class is required");
                    valid = false;
                } else {
                    etGrade.setError(null);
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
                if (newGuardianPhone.isEmpty()) {
                    etGuardianPhone.setError("ID number is required");
                    valid = false;
                } else {
                    etGuardianPhone.setError(null);
                }
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
            
            // Update based on user role
            if (student != null) {
                try {
                    dbHelper.updateStudent(student.sId, newFirstName, newLastName, newGrade, newPhone, newGuardianPhone, newEmail, newPassword);
                    Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                    
                    // Update stored email if changed
                    prefs.edit().putString("user_email", newEmail).apply();
                    
                    // Update local student object
                    student.firstName = newFirstName;
                    student.lastName = newLastName;
                    student.grade = newGrade;
                    student.phoneNumber = newPhone;
                    student.guardianTP = newGuardianPhone;
                    student.email = newEmail;
                    student.password = newPassword;
                    
                    // Update read-only views
                    tvFirstName.setText("First Name: " + newFirstName);
                    tvLastName.setText("Last Name: " + newLastName);
                    tvGrade.setText("Grade: " + newGrade);
                    tvPhone.setText("Phone: " + newPhone);
                    tvGuardianPhone.setText("Guardian Phone: " + newGuardianPhone);
                    tvEmail.setText("Email: " + newEmail);
                    tvPassword.setText("Password: " + newPassword);
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else if (teacher != null) {
                try {
                    dbHelper.updateTeacher(teacher.tId, newFirstName, newLastName, teacher.subject, newPhone, newGrade, newGuardianPhone, newEmail, newPassword);
                    Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show();
                    
                    // Update stored email if changed
                    prefs.edit().putString("user_email", newEmail).apply();
                    
                    // Update local teacher object
                    teacher.firstName = newFirstName;
                    teacher.lastName = newLastName;
                    teacher.className = newGrade;
                    teacher.phoneNumber = newPhone;
                    teacher.idNumber = newGuardianPhone;
                    teacher.email = newEmail;
                    teacher.password = newPassword;
                    
                    // Update read-only views
                    tvFirstName.setText("First Name: " + newFirstName);
                    tvLastName.setText("Last Name: " + newLastName);
                    tvGrade.setText("Class: " + newGrade);
                    tvPhone.setText("Phone: " + newPhone);
                    tvGuardianPhone.setText("ID Number: " + newGuardianPhone);
                    tvEmail.setText("Email: " + newEmail);
                    tvPassword.setText("Password: " + newPassword);
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            
            // Switch back to report card
            cardEdit.setVisibility(View.GONE);
            cardReport.setVisibility(View.VISIBLE);
        });
        
        btnLogout.setOnClickListener(v -> {
            // Clear SharedPreferences and navigate to login
            prefs.edit().clear().apply();
            Toast.makeText(requireContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show();
            requireActivity().finish(); // Close the current activity
            requireActivity().startActivity(new Intent(requireContext(), Login.class)); // Navigate to login
        });
        
        return view;
    }
}