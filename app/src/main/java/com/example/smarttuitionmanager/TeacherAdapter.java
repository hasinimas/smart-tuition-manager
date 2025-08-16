package com.example.smarttuitionmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.ArrayList;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {
    private List<MyDatabaseHelper.Teacher> teachers;
    private List<MyDatabaseHelper.Teacher> teachersFull;
    private OnTeacherClickListener listener;

    public interface OnTeacherClickListener {
        void onTeacherClick(MyDatabaseHelper.Teacher teacher);
    }

    public TeacherAdapter(List<MyDatabaseHelper.Teacher> teachers, OnTeacherClickListener listener) {
        this.teachers = teachers;
        this.teachersFull = new ArrayList<>(teachers);
        this.listener = listener;
    }

    public void setTeachers(List<MyDatabaseHelper.Teacher> teachers) {
        this.teachers = teachers;
        notifyDataSetChanged();
    }

    public void setFilter(String query) {
        if (query == null || query.trim().isEmpty()) {
            teachers = new ArrayList<>(teachersFull);
        } else {
            String lower = query.toLowerCase();
            List<MyDatabaseHelper.Teacher> filtered = new ArrayList<>();
            for (MyDatabaseHelper.Teacher t : teachersFull) {
                if (String.valueOf(t.tId).contains(lower)
                    || (t.firstName != null && t.firstName.toLowerCase().contains(lower))
                    || (t.lastName != null && t.lastName.toLowerCase().contains(lower))) {
                    filtered.add(t);
                }
            }
            teachers = filtered;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        MyDatabaseHelper.Teacher teacher = teachers.get(position);
        holder.tvTId.setText(String.valueOf(teacher.tId));
        holder.tvFirstName.setText(teacher.firstName);
        holder.tvLastName.setText(teacher.lastName);
        holder.tvClass.setText(teacher.className);
        holder.tvSubject.setText(teacher.subject);
        holder.tvPhone.setText(teacher.phoneNumber);
        holder.tvIdNumber.setText(teacher.idNumber);
        holder.tvEmail.setText(teacher.email);
        holder.tvPassword.setText(teacher.password);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onTeacherClick(teacher);
        });
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    public static class TeacherViewHolder extends RecyclerView.ViewHolder {
        TextView tvTId, tvFirstName, tvLastName, tvClass, tvSubject, tvPhone, tvIdNumber, tvEmail, tvPassword;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTId = itemView.findViewById(R.id.tv_t_id);
            tvFirstName = itemView.findViewById(R.id.tv_first_name);
            tvLastName = itemView.findViewById(R.id.tv_last_name);
            tvClass = itemView.findViewById(R.id.tv_class);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvIdNumber = itemView.findViewById(R.id.tv_id_number);
            tvEmail = itemView.findViewById(R.id.tv_email);
            tvPassword = itemView.findViewById(R.id.tv_password);
        }
    }
} 