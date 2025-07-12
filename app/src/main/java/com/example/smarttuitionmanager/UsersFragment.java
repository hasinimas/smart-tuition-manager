package com.example.smarttuitionmanager;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.Toast;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.ViewPropertyAnimator;
import android.text.InputFilter;
import android.text.InputType;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Button;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import android.widget.TextView;
import android.text.TextUtils;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import java.util.List;
import android.widget.HorizontalScrollView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerTeachers;
    private TeacherAdapter teacherAdapter;

    public UsersFragment() {
        // Required empty public constructor
    }

    public static UsersFragment newInstance(String param1, String param2) {
        UsersFragment fragment = new UsersFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        // Find views
        final TextView tabStudents = view.findViewById(R.id.tab_students);
        final TextView tabTeachers = view.findViewById(R.id.tab_teachers);
        final android.widget.Button btnAddStudent = view.findViewById(R.id.btn_add_student);
        final HorizontalScrollView scrollTeachers = view.findViewById(R.id.scroll_teachers);
        final HorizontalScrollView headerTeachers = view.findViewById(R.id.header_teachers);
        final HorizontalScrollView headerStudents = view.findViewById(R.id.header_students);
        final HorizontalScrollView scrollStudents = view.findViewById(R.id.scroll_students);
        final RecyclerView recyclerStudents = view.findViewById(R.id.recycler_students);
        recyclerStudents.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        final EditText etSearch = view.findViewById(R.id.et_search);
        // Show students by default
        StudentAdapter[] studentAdapterRef = new StudentAdapter[1];
        studentAdapterRef[0] = showStudentList(recyclerStudents);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (headerStudents.getVisibility() == View.VISIBLE) {
                    if (studentAdapterRef[0] != null) {
                        studentAdapterRef[0].setFilter(s.toString());
                    }
                } else if (headerTeachers.getVisibility() == View.VISIBLE) {
                    if (teacherAdapter != null) {
                        teacherAdapter.setFilter(s.toString());
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        recyclerTeachers = view.findViewById(R.id.recycler_teachers); // Make sure this ID exists in your layout
        recyclerTeachers.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        // Synchronize horizontal scrolling between header and list
        headerTeachers.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                scrollTeachers.scrollTo(scrollX, 0);
            }
        });
        scrollTeachers.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                headerTeachers.scrollTo(scrollX, 0);
            }
        });

        // Synchronize horizontal scrolling between student header and list
        headerStudents.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                scrollStudents.scrollTo(scrollX, 0);
            }
        });
        scrollStudents.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                headerStudents.scrollTo(scrollX, 0);
            }
        });

        // Set click listeners
        tabTeachers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddStudent.setText("+ Add Teacher");
                headerStudents.setVisibility(View.GONE);
                scrollStudents.setVisibility(View.GONE); // Ensure student list is hidden
                headerTeachers.setVisibility(View.VISIBLE);
                scrollTeachers.setVisibility(View.VISIBLE);

                tabTeachers.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tab_selected_bg));
                tabTeachers.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                tabStudents.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_white));
                tabStudents.setTextColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
                showTeacherList();
            }
        });
        tabStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddStudent.setText("+ Add Student");
                headerStudents.setVisibility(View.VISIBLE);
                scrollStudents.setVisibility(View.VISIBLE);
                headerTeachers.setVisibility(View.GONE);
                scrollTeachers.setVisibility(View.GONE); // Ensure teacher list is hidden

                tabStudents.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tab_selected_bg));
                tabStudents.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                tabTeachers.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_white));
                tabTeachers.setTextColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
                showStudentList(recyclerStudents);
            }
        });

        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnAddStudent.getText().toString().equals("+ Add Teacher")) {
                    // Show add teacher dialog
                    LayoutInflater dialogInflater = LayoutInflater.from(getContext());
                    View dialogView = dialogInflater.inflate(R.layout.dialog_add_teacher, null);

                    // Restrict phone field to numbers only
                    EditText etPhone = dialogView.findViewById(R.id.et_phone);
                    etPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
                    // Optionally, you can also set a max length if needed
                    // etPhone.setFilters(new InputFilter[] { new InputFilter.LengthFilter(15) });

                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setView(dialogView)
                            .setCancelable(true)
                            .create();

                    dialogView.findViewById(R.id.btn_submit_teacher).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Collect data
                            EditText etFirstName = dialogView.findViewById(R.id.et_first_name);
                            EditText etLastName = dialogView.findViewById(R.id.et_last_name);
                            EditText etSubject = dialogView.findViewById(R.id.et_subject);
                            EditText etPhone = dialogView.findViewById(R.id.et_phone);
                            EditText etClass = dialogView.findViewById(R.id.et_class);
                            EditText etIdNumber = dialogView.findViewById(R.id.et_id_number);
                            EditText etEmail = dialogView.findViewById(R.id.et_email);
                            EditText etPassword = dialogView.findViewById(R.id.et_password);

                            boolean valid = true;
                            if (etFirstName.getText().toString().trim().isEmpty()) {
                                etFirstName.setError("Required"); valid = false;
                            }
                            if (etLastName.getText().toString().trim().isEmpty()) {
                                etLastName.setError("Required"); valid = false;
                            }
                            if (etSubject != null && etSubject.getText().toString().trim().isEmpty()) {
                                etSubject.setError("Required"); valid = false;
                            }
                            if (etPhone.getText().toString().trim().isEmpty()) {
                                etPhone.setError("Required"); valid = false;
                            } else if (!etPhone.getText().toString().matches("\\d+")) {
                                etPhone.setError("Only numbers allowed"); valid = false;
                            }
                            if (etClass.getText().toString().trim().isEmpty()) {
                                etClass.setError("Required"); valid = false;
                            }
                            if (etIdNumber.getText().toString().trim().isEmpty()) {
                                etIdNumber.setError("Required"); valid = false;
                            }
                            if (etEmail.getText().toString().trim().isEmpty()) {
                                etEmail.setError("Required"); valid = false;
                            } else if (!etEmail.getText().toString().endsWith("@gmail.com")) {
                                etEmail.setError("Email must end with @gmail.com"); valid = false;
                            }
                            if (etPassword.getText().toString().trim().isEmpty()) {
                                etPassword.setError("Required"); valid = false;
                            }

                            if (!valid) return;

                            // Insert into database
                            MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext());
                            long result = dbHelper.insertTeacher(
                                etFirstName.getText().toString().trim(),
                                etLastName.getText().toString().trim(),
                                etSubject.getText().toString().trim(),
                                etPhone.getText().toString().trim(),
                                etClass.getText().toString().trim(),
                                etIdNumber.getText().toString().trim(),
                                etEmail.getText().toString().trim(),
                                etPassword.getText().toString().trim()
                            );

                            if (result != -1) {
                                // Animate success (scale and fade)
                                v.setEnabled(false);
                                v.animate()
                                    .scaleX(1.2f).scaleY(1.2f).alpha(0.7f)
                                    .setDuration(250)
                                    .withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            v.animate()
                                                .scaleX(1f).scaleY(1f).alpha(1f)
                                                .setDuration(250)
                                                .withEndAction(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getContext(), "Teacher submitted!", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                        // Show teacher tab and list after adding a teacher
                                                        headerTeachers.setVisibility(View.VISIBLE);
                                                        headerStudents.setVisibility(View.GONE);
                                                        recyclerTeachers.setVisibility(View.VISIBLE);
                                                        recyclerStudents.setVisibility(View.GONE);
                                                        tabTeachers.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.tab_selected_bg));
                                                        tabTeachers.setTextColor(ContextCompat.getColor(getContext(), R.color.primary));
                                                        tabStudents.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background_white));
                                                        tabStudents.setTextColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
                                                        showTeacherList();
                                                    }
                                                }).start();
                                        }
                                    }).start();
                            } else {
                                Toast.makeText(getContext(), "Failed to add teacher (maybe duplicate email)", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    dialog.show();
                } else if (btnAddStudent.getText().toString().equals("+ Add Student")) {
                    // Show add student dialog
                    LayoutInflater dialogInflater = LayoutInflater.from(getContext());
                    View dialogView = dialogInflater.inflate(R.layout.dialog_add_student, null);

                    // Set up multi-select subjects
                    Spinner spinnerSubject = dialogView.findViewById(R.id.spinner_subject);
                    Button btnAddSubject = dialogView.findViewById(R.id.btn_add_subject);
                    TextView tvSelectedSubjects = dialogView.findViewById(R.id.tv_selected_subjects);
                    String[] subjects = {"Mathematics", "Science", "English", "Sinhala", "History", "Buddhism"};
                    ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, subjects);
                    subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSubject.setAdapter(subjectAdapter);
                    ArrayList<String> selectedSubjects = new ArrayList<>();
                    Set<String> selectedSubjectsSet = new HashSet<>();
                    btnAddSubject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String selected = spinnerSubject.getSelectedItem().toString();
                            if (!selectedSubjectsSet.contains(selected)) {
                                selectedSubjects.add(selected);
                                selectedSubjectsSet.add(selected);
                                tvSelectedSubjects.setText("Selected Subjects: " + TextUtils.join(", ", selectedSubjects));
                            } else {
                                Toast.makeText(getContext(), selected + " already added", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    // Restrict phone and guardian tp fields to numbers only
                    EditText etPhone = dialogView.findViewById(R.id.et_phone);
                    EditText etGuardianTP = dialogView.findViewById(R.id.et_guardian_tp);
                    etPhone.setInputType(InputType.TYPE_CLASS_NUMBER);
                    etGuardianTP.setInputType(InputType.TYPE_CLASS_NUMBER);

                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setView(dialogView)
                            .setCancelable(true)
                            .create();

                    dialogView.findViewById(R.id.btn_submit_student).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Collect data
                            EditText etFirstName = dialogView.findViewById(R.id.et_first_name);
                            EditText etLastName = dialogView.findViewById(R.id.et_last_name);
                            EditText etPhone = dialogView.findViewById(R.id.et_phone);
                            EditText etClass = dialogView.findViewById(R.id.et_class);
                            EditText etGuardianTP = dialogView.findViewById(R.id.et_guardian_tp);
                            EditText etEmail = dialogView.findViewById(R.id.et_email);
                            EditText etPassword = dialogView.findViewById(R.id.et_password);

                            boolean valid = true;
                            if (etFirstName.getText().toString().trim().isEmpty()) {
                                etFirstName.setError("Required"); valid = false;
                            }
                            if (etLastName.getText().toString().trim().isEmpty()) {
                                etLastName.setError("Required"); valid = false;
                            }
                            if (etPhone.getText().toString().trim().isEmpty()) {
                                etPhone.setError("Required"); valid = false;
                            } else if (!etPhone.getText().toString().matches("\\d+")) {
                                etPhone.setError("Only numbers allowed"); valid = false;
                            }
                            if (etClass.getText().toString().trim().isEmpty()) {
                                etClass.setError("Required"); valid = false;
                            }
                            if (etGuardianTP.getText().toString().trim().isEmpty()) {
                                etGuardianTP.setError("Required"); valid = false;
                            } else if (!etGuardianTP.getText().toString().matches("\\d+")) {
                                etGuardianTP.setError("Only numbers allowed"); valid = false;
                            }
                            if (etEmail.getText().toString().trim().isEmpty()) {
                                etEmail.setError("Required"); valid = false;
                            } else if (!etEmail.getText().toString().endsWith("@gmail.com")) {
                                etEmail.setError("Email must end with @gmail.com"); valid = false;
                            }
                            if (etPassword.getText().toString().trim().isEmpty()) {
                                etPassword.setError("Required"); valid = false;
                            }

                            // Validate at least one subject selected
                            if (selectedSubjects.isEmpty()) {
                                tvSelectedSubjects.setError("Select at least one subject"); valid = false;
                            } else {
                                tvSelectedSubjects.setError(null);
                            }

                            if (!valid) return;

                            // Insert student and subjects into DB
                            MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext());
                            long studentId = dbHelper.insertStudent(
                                etFirstName.getText().toString().trim(),
                                etLastName.getText().toString().trim(),
                                etClass.getText().toString().trim(),
                                etPhone.getText().toString().trim(),
                                etGuardianTP.getText().toString().trim(),
                                null, // qr_img, set to null or handle as needed
                                etEmail.getText().toString().trim(),
                                etPassword.getText().toString().trim()
                            );
                            for (String subject : selectedSubjects) {
                                long subjectId = dbHelper.insertSubject(subject);
                                dbHelper.insertStudentSubject(studentId, subjectId);
                            }
                            // Refresh student list after adding
                            showStudentList(recyclerStudents);

                            // When collecting data, get selected subjects as a comma-separated string
                            String selectedSubjectsString = TextUtils.join(",", selectedSubjects);

                            // Animate success (scale and fade)
                            v.setEnabled(false);
                            v.animate()
                                .scaleX(1.2f).scaleY(1.2f).alpha(0.7f)
                                .setDuration(250)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        v.animate()
                                            .scaleX(1f).scaleY(1f).alpha(1f)
                                            .setDuration(250)
                                            .withEndAction(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getContext(), "Student submitted!", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                    // Show QR dialog after student submit
                                                    LayoutInflater qrInflater = LayoutInflater.from(getContext());
                                                    View qrDialogView = qrInflater.inflate(R.layout.dialog_student_qr, null);
                                                    AlertDialog qrDialog = new AlertDialog.Builder(getContext())
                                                            .setView(qrDialogView)
                                                            .setCancelable(true)
                                                            .create();
                                                    qrDialog.show();
                                                }
                                            }).start();
                                    }
                                }).start();
                        }
                    });

                    dialog.show();
                }
            }
        });

        return view;
    }

    private StudentAdapter showStudentList(RecyclerView recyclerStudents) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext());
        java.util.List<MyDatabaseHelper.StudentWithSubjects> students = dbHelper.getAllStudentsWithSubjects();
        StudentAdapter adapter = new StudentAdapter(students);
        recyclerStudents.setAdapter(adapter);
        return adapter;
    }

    private void showTeacherList() {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext());
        List<MyDatabaseHelper.Teacher> teachers = dbHelper.getAllTeachers();
        if (teacherAdapter == null) {
            teacherAdapter = new TeacherAdapter(teachers, teacher -> showTeacherReportDialog(teacher));
            recyclerTeachers.setAdapter(teacherAdapter);
        } else {
            teacherAdapter.setTeachers(teachers);
        }
    }

    private void showTeacherReportDialog(MyDatabaseHelper.Teacher teacher) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_teacher_report, null);

        ((TextView) dialogView.findViewById(R.id.tv_report_t_id)).setText("ID: " + teacher.tId);
        ((TextView) dialogView.findViewById(R.id.tv_report_name)).setText("Name: " + teacher.firstName + " " + teacher.lastName);
        ((TextView) dialogView.findViewById(R.id.tv_report_class)).setText("Class: " + teacher.className);
        ((TextView) dialogView.findViewById(R.id.tv_report_subject)).setText("Subject: " + teacher.subject);
        ((TextView) dialogView.findViewById(R.id.tv_report_phone)).setText("Phone: " + teacher.phoneNumber);
        ((TextView) dialogView.findViewById(R.id.tv_report_id_number)).setText("ID Number: " + teacher.idNumber);
        ((TextView) dialogView.findViewById(R.id.tv_report_email)).setText("Email: " + teacher.email);
        ((TextView) dialogView.findViewById(R.id.tv_report_password)).setText("Password: " + teacher.password);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialogView.findViewById(R.id.btn_close_teacher_report).setOnClickListener(v -> dialog.dismiss());

        // Delete logic
        dialogView.findViewById(R.id.btn_delete_teacher_report).setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                .setTitle("Delete Teacher")
                .setMessage("Are you sure you want to delete this teacher?")
                .setPositiveButton("Delete", (d, which) -> {
                    MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext());
                    dbHelper.deleteTeacher(teacher.tId);
                    dialog.dismiss();
                    showTeacherList();
                    Toast.makeText(getContext(), "Teacher deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
        });

        // Update logic
        dialogView.findViewById(R.id.btn_update_teacher_report).setOnClickListener(v -> {
            dialog.dismiss();
            showUpdateTeacherDialog(teacher);
        });

        dialog.show();
    }

    private void showUpdateTeacherDialog(MyDatabaseHelper.Teacher teacher) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_teacher, null);

        EditText etFirstName = dialogView.findViewById(R.id.et_first_name);
        EditText etLastName = dialogView.findViewById(R.id.et_last_name);
        EditText etSubject = dialogView.findViewById(R.id.et_subject);
        EditText etPhone = dialogView.findViewById(R.id.et_phone);
        EditText etClass = dialogView.findViewById(R.id.et_class);
        EditText etIdNumber = dialogView.findViewById(R.id.et_id_number);
        EditText etEmail = dialogView.findViewById(R.id.et_email);
        EditText etPassword = dialogView.findViewById(R.id.et_password);

        // Pre-fill fields
        etFirstName.setText(teacher.firstName);
        etLastName.setText(teacher.lastName);
        etSubject.setText(teacher.subject);
        etPhone.setText(teacher.phoneNumber);
        etClass.setText(teacher.className);
        etIdNumber.setText(teacher.idNumber);
        etEmail.setText(teacher.email);
        etPassword.setText(teacher.password);

        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialogView.findViewById(R.id.btn_submit_teacher).setOnClickListener(v -> {
            boolean valid = true;
            if (etFirstName.getText().toString().trim().isEmpty()) {
                etFirstName.setError("Required"); valid = false;
            }
            if (etLastName.getText().toString().trim().isEmpty()) {
                etLastName.setError("Required"); valid = false;
            }
            if (etSubject != null && etSubject.getText().toString().trim().isEmpty()) {
                etSubject.setError("Required"); valid = false;
            }
            if (etPhone.getText().toString().trim().isEmpty()) {
                etPhone.setError("Required"); valid = false;
            } else if (!etPhone.getText().toString().matches("\\d+")) {
                etPhone.setError("Only numbers allowed"); valid = false;
            }
            if (etClass.getText().toString().trim().isEmpty()) {
                etClass.setError("Required"); valid = false;
            }
            if (etIdNumber.getText().toString().trim().isEmpty()) {
                etIdNumber.setError("Required"); valid = false;
            }
            if (etEmail.getText().toString().trim().isEmpty()) {
                etEmail.setError("Required"); valid = false;
            } else if (!etEmail.getText().toString().endsWith("@gmail.com")) {
                etEmail.setError("Email must end with @gmail.com"); valid = false;
            }
            if (etPassword.getText().toString().trim().isEmpty()) {
                etPassword.setError("Required"); valid = false;
            }
            if (!valid) return;

            MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext());
            dbHelper.updateTeacher(
                teacher.tId,
                etFirstName.getText().toString().trim(),
                etLastName.getText().toString().trim(),
                etSubject.getText().toString().trim(),
                etPhone.getText().toString().trim(),
                etClass.getText().toString().trim(),
                etIdNumber.getText().toString().trim(),
                etEmail.getText().toString().trim(),
                etPassword.getText().toString().trim()
            );
            dialog.dismiss();
            showTeacherList();
            Toast.makeText(getContext(), "Teacher updated", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }
}