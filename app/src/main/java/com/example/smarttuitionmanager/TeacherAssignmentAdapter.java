package com.example.smarttuitionmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TeacherAssignmentAdapter extends RecyclerView.Adapter<TeacherAssignmentAdapter.TeacherViewHolder> {

    private List<String> teachers;
    private OnTeacherClickListener listener;

    public interface OnTeacherClickListener {
        void onTeacherClick(String teacherName);
    }

    public TeacherAssignmentAdapter(List<String> teachers, OnTeacherClickListener listener) {
        this.teachers = teachers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_teacher_assignment, parent, false);
        return new TeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        String teacher = teachers.get(position);
        holder.bind(teacher);
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    class TeacherViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTeacherName;
        private TextView tvSubject;

        public TeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTeacherName = itemView.findViewById(R.id.tv_teacher_name);
            tvSubject = itemView.findViewById(R.id.tv_subject);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onTeacherClick(teachers.get(position));
                }
            });
        }

        public void bind(String teacherInfo) {
            // Parse teacher info: "FirstName LastName (Subject)"
            String[] parts = teacherInfo.split(" \\(");
            String teacherName = parts[0];
            String subject = parts[1].replace(")", "");

            tvTeacherName.setText(teacherName);
            tvSubject.setText(subject);
        }
    }
} 