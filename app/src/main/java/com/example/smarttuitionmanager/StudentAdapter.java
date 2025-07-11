package com.example.smarttuitionmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {
    private List<MyDatabaseHelper.StudentWithSubjects> students;

    public StudentAdapter(List<MyDatabaseHelper.StudentWithSubjects> students) {
        this.students = students;
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