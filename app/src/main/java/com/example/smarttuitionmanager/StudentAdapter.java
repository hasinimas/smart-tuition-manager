package com.example.smarttuitionmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import android.widget.LinearLayout;
import java.util.Collections;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private List<MyDatabaseHelper.StudentWithSubjects> students;
    private List<MyDatabaseHelper.StudentWithSubjects> studentsFull;

    public StudentAdapter(List<MyDatabaseHelper.StudentWithSubjects> students) {
        this.students = students;
        this.studentsFull = new ArrayList<>(students);
    }

    public void setFilter(String query) {
        if (query == null || query.trim().isEmpty()) {
            students = new ArrayList<>(studentsFull);
        } else {
            String lower = query.toLowerCase();
            List<MyDatabaseHelper.StudentWithSubjects> filtered = new ArrayList<>();
            for (MyDatabaseHelper.StudentWithSubjects s : studentsFull) {
                if (String.valueOf(s.sId).contains(lower)
                    || (s.firstName != null && s.firstName.toLowerCase().contains(lower))) {
                    filtered.add(s);
                }
            }
            students = filtered;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        MyDatabaseHelper.StudentWithSubjects student = students.get(position);
        holder.tvSId.setText(String.valueOf(student.sId));
        holder.tvFirstName.setText(student.firstName);
        holder.tvLastName.setText(student.lastName);
        holder.tvClass.setText(student.grade);
        holder.tvSubjects.setText(android.text.TextUtils.join(", ", student.subjects));
        holder.tvPhone.setText(student.phoneNumber);
        holder.tvGuardianTP.setText(student.guardianTP);
        holder.tvEmail.setText(student.email);
        holder.tvPassword.setText(student.password);

        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_student_report, null);
            ((TextView) dialogView.findViewById(R.id.tv_report_s_id)).setText("S_ID: " + student.sId);
            ((TextView) dialogView.findViewById(R.id.tv_report_first_name)).setText("First Name: " + student.firstName);
            ((TextView) dialogView.findViewById(R.id.tv_report_last_name)).setText("Last Name: " + student.lastName);
            ((TextView) dialogView.findViewById(R.id.tv_report_class)).setText("Class: " + student.grade);
            ((TextView) dialogView.findViewById(R.id.tv_report_subjects)).setText("Subjects: " + android.text.TextUtils.join(", ", student.subjects));
            ((TextView) dialogView.findViewById(R.id.tv_report_phone)).setText("Phone Number: " + student.phoneNumber);
            ((TextView) dialogView.findViewById(R.id.tv_report_guardian_tp)).setText("Guardian TP: " + student.guardianTP);
            ((TextView) dialogView.findViewById(R.id.tv_report_email)).setText("Email: " + student.email);
            ((TextView) dialogView.findViewById(R.id.tv_report_password)).setText("Password: " + student.password);

            AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setCancelable(true)
                .create();
            Button btnClose = dialogView.findViewById(R.id.btn_close_report);
            btnClose.setOnClickListener(view -> dialog.dismiss());

            Button btnDelete = dialogView.findViewById(R.id.btn_delete_report);
            btnDelete.setOnClickListener(view -> {
                new AlertDialog.Builder(context)
                    .setMessage("Are you sure you want to delete this record?")
                    .setPositiveButton("Yes", (d, which) -> {
                        // Delete from database
                        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
                        dbHelper.deleteStudent(student.sId);
                        // Remove from UI list
                        int pos = holder.getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            students.remove(pos);
                            notifyItemRemoved(pos);
                        }
                        d.dismiss();
                        dialog.dismiss();
                    })
                    .setNegativeButton("No", (d, which) -> d.dismiss())
                    .show();
            });

            Button btnUpdate = dialogView.findViewById(R.id.btn_update_report);
            btnUpdate.setOnClickListener(view -> {
                dialog.dismiss();
                View updateView = LayoutInflater.from(context).inflate(R.layout.dialog_add_student, null);
                AlertDialog updateDialog = new AlertDialog.Builder(context)
                    .setView(updateView)
                    .setCancelable(true)
                    .create();
                // Pre-fill fields
                ((TextView) updateView.findViewById(R.id.et_first_name)).setText(student.firstName);
                ((TextView) updateView.findViewById(R.id.et_last_name)).setText(student.lastName);
                ((TextView) updateView.findViewById(R.id.et_class)).setText(student.grade);
                ((TextView) updateView.findViewById(R.id.et_phone)).setText(student.phoneNumber);
                ((TextView) updateView.findViewById(R.id.et_guardian_tp)).setText(student.guardianTP);
                ((TextView) updateView.findViewById(R.id.et_email)).setText(student.email);
                ((TextView) updateView.findViewById(R.id.et_password)).setText(student.password);

                // Subjects logic
                Spinner spinnerSubject = updateView.findViewById(R.id.spinner_subject);
                Button btnAddSubject = updateView.findViewById(R.id.btn_add_subject);
                TextView tvSelectedSubjects = updateView.findViewById(R.id.tv_selected_subjects);
                LinearLayout subjectChipContainer = new LinearLayout(context);
                subjectChipContainer.setOrientation(LinearLayout.HORIZONTAL);
                ((LinearLayout) tvSelectedSubjects.getParent()).addView(subjectChipContainer);
                String[] allSubjects = {"Mathematics", "Science", "English", "Sinhala", "History", "Buddhism"};
                android.widget.ArrayAdapter<String> subjectAdapter = new android.widget.ArrayAdapter<>(context, android.R.layout.simple_spinner_item, allSubjects);
                subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSubject.setAdapter(subjectAdapter);
                ArrayList<String> selectedSubjects = new ArrayList<>(student.subjects);
                Set<String> selectedSubjectsSet = new HashSet<>(student.subjects);
                final Runnable[] updateChips = new Runnable[1];
                updateChips[0] = () -> {
                    subjectChipContainer.removeAllViews();
                    for (String subj : selectedSubjects) {
                        android.widget.TextView chip = new android.widget.TextView(context);
                        chip.setText(subj + "  ✕");
                        chip.setBackgroundResource(android.R.drawable.btn_default_small);
                        chip.setPadding(16, 8, 16, 8);
                        chip.setOnClickListener(v2 -> {
                            selectedSubjectsSet.remove(subj);
                            selectedSubjects.remove(subj);
                            updateChips[0].run();
                        });
                        subjectChipContainer.addView(chip);
                    }
                    tvSelectedSubjects.setText("Selected Subjects: " + android.text.TextUtils.join(", ", selectedSubjects));
                };
                updateChips[0].run();
                btnAddSubject.setOnClickListener(v1 -> {
                    String selected = spinnerSubject.getSelectedItem().toString();
                    if (!selectedSubjectsSet.contains(selected)) {
                        selectedSubjects.add(selected);
                        selectedSubjectsSet.add(selected);
                        updateChips[0].run();
                    }
                });

                // Update button
                Button btnSubmit = updateView.findViewById(R.id.btn_submit_student);
                btnSubmit.setText("Update");
                btnSubmit.setOnClickListener(submitView -> {
                    // Get updated values
                    String firstName = ((TextView) updateView.findViewById(R.id.et_first_name)).getText().toString().trim();
                    String lastName = ((TextView) updateView.findViewById(R.id.et_last_name)).getText().toString().trim();
                    String grade = ((TextView) updateView.findViewById(R.id.et_class)).getText().toString().trim();
                    String phone = ((TextView) updateView.findViewById(R.id.et_phone)).getText().toString().trim();
                    String guardianTP = ((TextView) updateView.findViewById(R.id.et_guardian_tp)).getText().toString().trim();
                    String email = ((TextView) updateView.findViewById(R.id.et_email)).getText().toString().trim();
                    String password = ((TextView) updateView.findViewById(R.id.et_password)).getText().toString().trim();
                    // Update DB
                    MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
                    dbHelper.updateStudent(student.sId, firstName, lastName, grade, phone, guardianTP, email, password);
                    dbHelper.removeAllSubjectsForStudent(student.sId);
                    for (String subject : selectedSubjects) {
                        long subjectId = dbHelper.insertSubject(subject);
                        dbHelper.insertStudentSubject(student.sId, subjectId);
                    }
                    // Update UI
                    student.firstName = firstName;
                    student.lastName = lastName;
                    student.grade = grade;
                    student.phoneNumber = phone;
                    student.guardianTP = guardianTP;
                    student.email = email;
                    student.password = password;
                    student.subjects = new ArrayList<>(selectedSubjects);
                    notifyItemChanged(holder.getAdapterPosition());
                    updateDialog.dismiss();
                });
                updateDialog.show();
            });

            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvSId, tvFirstName, tvLastName, tvClass, tvSubjects, tvPhone, tvGuardianTP, tvEmail, tvPassword;
        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSId = itemView.findViewById(R.id.tv_s_id);
            tvFirstName = itemView.findViewById(R.id.tv_first_name);
            tvLastName = itemView.findViewById(R.id.tv_last_name);
            tvClass = itemView.findViewById(R.id.tv_class);
            tvSubjects = itemView.findViewById(R.id.tv_subjects);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvGuardianTP = itemView.findViewById(R.id.tv_guardian_tp);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvPassword = itemView.findViewById(R.id.tv_password);
        }
    }
} 