package com.example.smarttuitionmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class AssignSubjectFragment extends Fragment {

    private MyDatabaseHelper databaseHelper;
    private AutoCompleteTextView spinnerStudent;
    private RecyclerView recyclerTeachers;
    private RecyclerView recyclerAssignedTeachers;
    private TextView tvAvailableCount;
    private TextView tvAssignedCount;
    private LinearLayout emptyAssignedState;
    private LinearLayout progressContainer;
    private CircularProgressIndicator progressIndicator;
    private TextView progressText;

    private List<String> availableTeachers;
    private List<String> assignedTeachers;
    private String selectedStudent;
    private String selectedStudentSubject;
    private TeacherAssignmentAdapter teacherAdapter;
    private AssignedTeacherAdapter assignedTeacherAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_asign_subject, container, false);
        
        databaseHelper = new MyDatabaseHelper(requireContext());
        initializeViews(view);
        setupListeners(view);
        loadData();
        
        return view;
    }

    private void initializeViews(View view) {
        spinnerStudent = view.findViewById(R.id.spinner_student);
        recyclerTeachers = view.findViewById(R.id.recycler_teachers);
        recyclerAssignedTeachers = view.findViewById(R.id.recycler_assigned_teachers);
        tvAvailableCount = view.findViewById(R.id.tv_available_count);
        tvAssignedCount = view.findViewById(R.id.tv_assigned_count);
        emptyAssignedState = view.findViewById(R.id.empty_assigned_state);
        progressContainer = view.findViewById(R.id.progress_container);
        progressIndicator = view.findViewById(R.id.progress_indicator);
        progressText = view.findViewById(R.id.progress_text);

        // Removed btnAddTeacher, btnCancel and btnSave

        // Setup RecyclerViews
        recyclerTeachers.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerAssignedTeachers.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize lists
        availableTeachers = new ArrayList<>();
        assignedTeachers = new ArrayList<>();

        // Setup adapters
        teacherAdapter = new TeacherAssignmentAdapter(availableTeachers, new TeacherAssignmentAdapter.OnTeacherClickListener() {
            @Override
            public void onTeacherClick(String teacherName) {
                assignTeacherToStudent(teacherName);
            }
        });

        assignedTeacherAdapter = new AssignedTeacherAdapter(assignedTeachers, new AssignedTeacherAdapter.OnAssignedTeacherClickListener() {
            @Override
            public void onRemoveClick(String teacherName) {
                removeTeacherFromStudent(teacherName);
            }
        });

        recyclerTeachers.setAdapter(teacherAdapter);
        recyclerAssignedTeachers.setAdapter(assignedTeacherAdapter);
    }

    private void setupListeners(View view) {
        // Student selection listener
        spinnerStudent.setOnItemClickListener((parent, view1, position, id) -> {
            try {
                // Check if position is valid
                if (position >= 0 && position < parent.getCount()) {
                    String selectedItem = (String) parent.getItemAtPosition(position);
                    
                    // Don't process if it's the placeholder text
                    if ("No students available".equals(selectedItem)) {
                        return;
                    }
                    
                    selectedStudent = selectedItem;
                    selectedStudentSubject = extractSubjectFromStudentString(selectedStudent);
                    loadTeachersForStudentSubject();
                    loadAssignedTeachers();
                }
            } catch (Exception e) {
                Log.e("AssignSubjectFragment", "Error selecting student: " + e.getMessage());
                Toast.makeText(requireContext(), "Error selecting student. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        // Add search functionality to student dropdown
        spinnerStudent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Only filter if we have at least 2 characters or if clearing
                if (s.length() >= 2 || s.length() == 0) {
                    filterStudents(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Removed all button listeners
    }

    private void loadData() {
        loadStudents();
        // Don't load teachers initially - wait for student selection
        availableTeachers.clear();
        tvAvailableCount.setText("0 available");
        teacherAdapter.notifyDataSetChanged();
    }

    private void loadStudents() {
        List<String> students = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllStudents();
        
        if (cursor.moveToFirst()) {
            do {
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
                String grade = cursor.getString(cursor.getColumnIndexOrThrow("grade"));
                String subject = getStudentSubject(firstName, lastName);
                students.add(firstName + " " + lastName + " (Grade " + grade + " - " + subject + ")");
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Always ensure we have at least an empty adapter
        if (students.isEmpty()) {
            students.add("No students available");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_dropdown_item_1line, students);
        spinnerStudent.setAdapter(adapter);
    }

    private void filterStudents(String searchText) {
        List<String> allStudents = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllStudents();
        
        if (cursor.moveToFirst()) {
            do {
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
                String grade = cursor.getString(cursor.getColumnIndexOrThrow("grade"));
                String subject = getStudentSubject(firstName, lastName);
                String fullName = firstName + " " + lastName;
                
                // Filter by search text
                if (searchText.isEmpty() || 
                    fullName.toLowerCase().contains(searchText.toLowerCase()) ||
                    subject.toLowerCase().contains(searchText.toLowerCase())) {
                    allStudents.add(fullName + " (Grade " + grade + " - " + subject + ")");
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        // Only update adapter if we have results or if search is empty
        if (!allStudents.isEmpty() || searchText.isEmpty()) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_dropdown_item_1line, allStudents);
            spinnerStudent.setAdapter(adapter);
        }
    }

    private String getStudentSubject(String firstName, String lastName) {
        // Get student's subject from student_subject table
        Cursor cursor = databaseHelper.getAllStudents();
        if (cursor.moveToFirst()) {
            do {
                String dbFirstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
                String dbLastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
                
                if (dbFirstName.equals(firstName) && dbLastName.equals(lastName)) {
                    long studentId = cursor.getLong(cursor.getColumnIndexOrThrow("s_id"));
                    cursor.close();
                    
                    // Get subject for this student
                    Cursor subjectCursor = databaseHelper.getAllSubjects();
                    if (subjectCursor.moveToFirst()) {
                        do {
                            long subjectId = subjectCursor.getLong(subjectCursor.getColumnIndexOrThrow("subject_id"));
                            // Check if this student has this subject
                            Cursor linkCursor = databaseHelper.getAllStudents();
                            // For now, return a default subject - you can implement proper linking
                            subjectCursor.close();
                            return "Mathematics"; // Default subject
                        } while (subjectCursor.moveToNext());
                    }
                    subjectCursor.close();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return "Mathematics"; // Default subject
    }

    private void loadTeachersForStudentSubject() {
        if (selectedStudent == null || selectedStudentSubject == null) return;

        availableTeachers.clear();
        String studentGrade = extractGradeFromStudentString(selectedStudent);
        
        // First priority: Get teachers who teach the same subject AND grade as the student
        Cursor cursor = databaseHelper.getTeachersBySubjectAndGrade(selectedStudentSubject, studentGrade);
        boolean foundExactMatch = false;
        
        if (cursor.moveToFirst()) {
            do {
                String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
                String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
                String teacherSubject = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
                String teacherGrade = cursor.getString(cursor.getColumnIndexOrThrow("class"));
                
                String teacherName = firstName + " " + lastName + " (" + teacherSubject + " - Grade " + teacherGrade + ")";
                if (!assignedTeachers.contains(teacherName)) {
                    availableTeachers.add(teacherName);
                    foundExactMatch = true;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        // If no exact matches found, get teachers who teach the same subject (regardless of grade)
        if (!foundExactMatch) {
            cursor = databaseHelper.getTeachersBySubject(selectedStudentSubject);
            if (cursor.moveToFirst()) {
                do {
                    String firstName = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
                    String lastName = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));
                    String teacherSubject = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
                    String teacherGrade = cursor.getString(cursor.getColumnIndexOrThrow("class"));
                    
                    String teacherName = firstName + " " + lastName + " (" + teacherSubject + " - Grade " + teacherGrade + ")";
                    if (!assignedTeachers.contains(teacherName)) {
                        availableTeachers.add(teacherName);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        updateTeacherCountDisplay();
        teacherAdapter.notifyDataSetChanged();
        
        if (availableTeachers.isEmpty()) {
            Toast.makeText(requireContext(), 
                "No " + selectedStudentSubject + " teachers available for Grade " + studentGrade, 
                Toast.LENGTH_SHORT).show();
        } else if (!foundExactMatch) {
            Toast.makeText(requireContext(), 
                "Showing " + selectedStudentSubject + " teachers from other grades (no exact grade match found)", 
                Toast.LENGTH_LONG).show();
        }
    }



    // Update the teacher count display
    private void updateTeacherCountDisplay() {
        tvAvailableCount.setText(availableTeachers.size() + " available");
    }

    private void loadAssignedTeachers() {
        if (selectedStudent == null) return;

        assignedTeachers.clear();
        String studentName = extractNameFromStudentString(selectedStudent);
        
        Cursor cursor = databaseHelper.getAssignmentsForStudent(studentName);
        if (cursor.moveToFirst()) {
            do {
                String teacherName = cursor.getString(cursor.getColumnIndexOrThrow("teacher_name"));
                String subject = cursor.getString(cursor.getColumnIndexOrThrow("subject"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("time"));
                String day = cursor.getString(cursor.getColumnIndexOrThrow("day"));
                assignedTeachers.add(teacherName + " - " + subject + " (" + day + " " + time + ")");
            } while (cursor.moveToNext());
        }
        cursor.close();

        tvAssignedCount.setText(assignedTeachers.size() + " assigned");
        
        if (assignedTeachers.isEmpty()) {
            emptyAssignedState.setVisibility(View.VISIBLE);
            recyclerAssignedTeachers.setVisibility(View.GONE);
        } else {
            emptyAssignedState.setVisibility(View.GONE);
            recyclerAssignedTeachers.setVisibility(View.VISIBLE);
        }
        
        assignedTeacherAdapter.notifyDataSetChanged();
    }

    private void assignTeacherToStudent(String teacherName) {
        if (selectedStudent == null) {
            Toast.makeText(requireContext(), "Please select a student first", Toast.LENGTH_SHORT).show();
            return;
        }

        showAssignmentDialog(teacherName);
    }

    private void showAssignmentDialog(String teacherName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Assign Teacher");

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_assign_teacher, null);
        builder.setView(dialogView);

        AutoCompleteTextView daySpinner = dialogView.findViewById(R.id.spinner_day);
        AutoCompleteTextView timeSpinner = dialogView.findViewById(R.id.spinner_time);

        // Setup day spinner with 7 days of the week
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_dropdown_item_1line, days);
        daySpinner.setAdapter(dayAdapter);

        // Setup time spinner
        String[] times = {"8:00 AM - 9:00 AM", "9:00 AM - 10:00 AM", "10:00 AM - 11:00 AM", 
                         "11:00 AM - 12:00 PM", "2:00 PM - 3:00 PM", "3:00 PM - 4:00 PM", 
                         "4:00 PM - 5:00 PM", "5:00 PM - 6:00 PM"};
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(requireContext(), 
            android.R.layout.simple_dropdown_item_1line, times);
        timeSpinner.setAdapter(timeAdapter);

        builder.setPositiveButton("Assign", (dialog, which) -> {
            String day = daySpinner.getText().toString();
            String time = timeSpinner.getText().toString();
            
            if (day.isEmpty() || time.isEmpty()) {
                Toast.makeText(requireContext(), "Please select day and time", Toast.LENGTH_SHORT).show();
                return;
            }

            saveAssignment(teacherName, selectedStudentSubject, time, day);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void saveAssignment(String teacherName, String subject, String time, String day) {
        if (selectedStudent == null) return;

        String studentName = extractNameFromStudentString(selectedStudent);
        String grade = extractGradeFromStudentString(selectedStudent);

        // Check for time conflicts
        if (databaseHelper.hasTimeConflict(teacherName, day, time)) {
            Toast.makeText(requireContext(), 
                "Teacher " + teacherName + " is already assigned on " + day + " at " + time, 
                Toast.LENGTH_LONG).show();
            return;
        }

        if (databaseHelper.hasStudentTimeConflict(studentName, day, time)) {
            Toast.makeText(requireContext(), 
                "Student " + studentName + " already has an assignment on " + day + " at " + time, 
                Toast.LENGTH_LONG).show();
            return;
        }

        showProgress("Saving assignment...");

        // Save to database
        long result = databaseHelper.assignTeacherToStudent(teacherName, studentName, subject, time, grade, day);
        
        hideProgress();
        
        if (result != -1) {
            Toast.makeText(requireContext(), "Teacher assigned successfully", Toast.LENGTH_SHORT).show();
            loadTeachersForStudentSubject();
            loadAssignedTeachers();
        } else {
            Toast.makeText(requireContext(), "Failed to assign teacher", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeTeacherFromStudent(String teacherName) {
        if (selectedStudent == null) return;

        String studentName = extractNameFromStudentString(selectedStudent);
        
        new AlertDialog.Builder(requireContext())
            .setTitle("Remove Assignment")
            .setMessage("Are you sure you want to remove this teacher assignment?")
            .setPositiveButton("Remove", (dialog, which) -> {
                boolean success = databaseHelper.removeTeacherFromStudent(teacherName, studentName);
                if (success) {
                    Toast.makeText(requireContext(), "Assignment removed", Toast.LENGTH_SHORT).show();
                    loadTeachersForStudentSubject();
                    loadAssignedTeachers();
                } else {
                    Toast.makeText(requireContext(), "Failed to remove assignment", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }



    private void clearSelection() {
        spinnerStudent.setText("");
        selectedStudent = null;
        selectedStudentSubject = null;
        availableTeachers.clear();
        assignedTeachers.clear();
        tvAvailableCount.setText("0 available");
        tvAssignedCount.setText("0 assigned");
        teacherAdapter.notifyDataSetChanged();
        assignedTeacherAdapter.notifyDataSetChanged();
        emptyAssignedState.setVisibility(View.VISIBLE);
        recyclerAssignedTeachers.setVisibility(View.GONE);
    }

    private void saveAssignments() {
        if (selectedStudent == null) {
            Toast.makeText(requireContext(), "Please select a student first", Toast.LENGTH_SHORT).show();
            return;
        }

        if (assignedTeachers.isEmpty()) {
            Toast.makeText(requireContext(), "No teachers assigned", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(requireContext(), "All assignments saved successfully", Toast.LENGTH_SHORT).show();
    }

    private void showProgress(String message) {
        progressText.setText(message);
        progressContainer.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressContainer.setVisibility(View.GONE);
    }

    // Helper methods
    private String extractNameFromStudentString(String studentString) {
        // Extract name from "FirstName LastName (Grade X - Subject)" format
        return studentString.split(" \\(")[0];
    }

    private String extractGradeFromStudentString(String studentString) {
        // Extract grade from "FirstName LastName (Grade X - Subject)" format
        String gradePart = studentString.split("Grade ")[1];
        return gradePart.split(" - ")[0];
    }

    private String extractSubjectFromStudentString(String studentString) {
        // Extract subject from "FirstName LastName (Grade X - Subject)" format
        String[] parts = studentString.split(" - ");
        if (parts.length > 1) {
            return parts[1].replace(")", "");
        }
        return "Mathematics"; // Default subject
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
} 