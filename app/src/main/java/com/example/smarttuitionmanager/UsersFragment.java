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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UsersFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        final View tabStudents = view.findViewById(R.id.tab_students);
        final View tabTeachers = view.findViewById(R.id.tab_teachers);
        final android.widget.Button btnAddStudent = view.findViewById(R.id.btn_add_student);

        // Set click listeners
        tabTeachers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddStudent.setText("+ Add Teacher");
            }
        });
        tabStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAddStudent.setText("+ Add Student");
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
                            EditText etAddress = dialogView.findViewById(R.id.et_address);
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
                            if (etAddress.getText().toString().trim().isEmpty()) {
                                etAddress.setError("Required"); valid = false;
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
                                                }
                                            }).start();
                                    }
                                }).start();
                        }
                    });

                    dialog.show();
                } else if (btnAddStudent.getText().toString().equals("+ Add Student")) {
                    // Show add student dialog
                    LayoutInflater dialogInflater = LayoutInflater.from(getContext());
                    View dialogView = dialogInflater.inflate(R.layout.dialog_add_student, null);

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
                            EditText etSubject = dialogView.findViewById(R.id.et_subject);
                            EditText etAddress = dialogView.findViewById(R.id.et_address);
                            EditText etPhone = dialogView.findViewById(R.id.et_phone);
                            EditText etClass = dialogView.findViewById(R.id.et_class);
                            EditText etGuardianTP = dialogView.findViewById(R.id.et_guardian_tp);
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
                            if (etAddress.getText().toString().trim().isEmpty()) {
                                etAddress.setError("Required"); valid = false;
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
}