package com.example.smarttuitionmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AssignedTeacherAdapter extends RecyclerView.Adapter<AssignedTeacherAdapter.AssignedTeacherViewHolder> {

    private List<String> assignedTeachers;
    private OnAssignedTeacherClickListener listener;

    public interface OnAssignedTeacherClickListener {
        void onRemoveClick(String teacherName);
    }

    public AssignedTeacherAdapter(List<String> assignedTeachers, OnAssignedTeacherClickListener listener) {
        this.assignedTeachers = assignedTeachers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AssignedTeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_assigned_teacher, parent, false);
        return new AssignedTeacherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignedTeacherViewHolder holder, int position) {
        String assignedTeacher = assignedTeachers.get(position);
        holder.bind(assignedTeacher);
    }

    @Override
    public int getItemCount() {
        return assignedTeachers.size();
    }

    class AssignedTeacherViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTeacherInfo;
        private ImageButton btnRemove;

        public AssignedTeacherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTeacherInfo = itemView.findViewById(R.id.tv_teacher_info);
            btnRemove = itemView.findViewById(R.id.btn_remove);

            btnRemove.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    // Extract teacher name from the full info string
                    String fullInfo = assignedTeachers.get(position);
                    String teacherName = extractTeacherName(fullInfo);
                    listener.onRemoveClick(teacherName);
                }
            });
        }

        public void bind(String teacherInfo) {
            tvTeacherInfo.setText(teacherInfo);
        }

        private String extractTeacherName(String fullInfo) {
            // Extract teacher name from "TeacherName - Subject (Time)" format
            return fullInfo.split(" - ")[0];
        }
    }
} 