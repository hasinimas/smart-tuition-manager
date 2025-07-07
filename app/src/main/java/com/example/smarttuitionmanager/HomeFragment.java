package com.example.smarttuitionmanager;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeFragment extends Fragment {

    private LinearLayout layoutWelcome, layoutStats, layoutExtra;
    private GridLayout layoutActions;
    private String userRole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Bind views
        layoutWelcome = view.findViewById(R.id.layout_welcome);
        layoutStats = view.findViewById(R.id.layout_stats);
        layoutActions = view.findViewById(R.id.layout_actions);
        layoutExtra = view.findViewById(R.id.layout_extra);

        // Get user role from arguments (default to "student")
        if (getArguments() != null) {
            userRole = getArguments().getString("role", "student");
        } else {
            userRole = "student";
        }

        // Load content based on role
        if (userRole.equals("teacher")) {
            loadTeacherContent();
        } else if (userRole.equals("student")) {
            loadStudentContent();
        } else if (userRole.equals("admin")) {
            loadAdminContent();
        }

        return view;
    }

    // Role-specific methods

    private void loadTeacherContent() {
        setWelcome("Welcome, Teacher");

        setWelcome("Welcome, Teacher");

        // Add cards into layout_stats
        addStatCard("45", "Students");
        addStatCard("5", "Classes");
        addStatCard("12", "Assignments");

        // Add actions into layout_actions
        addActionCard("📷", "Attendance");
        addActionCard("📄", "Assignments");
        addActionCard("📘", "Materials");
        addActionCard("🔔", "Notifications");

        // Add today's classes into layout_extra
        addClassCard("Maths", "Grade 10", "9:00 AM", "Now");
        addClassCard("Physics", "Grade 11", "11:00 AM", "Upcoming");
    }

    private void addStatCard(String count, String title) {
        View card = LayoutInflater.from(getContext()).inflate(R.layout.card_stat, layoutStats, false);
        ((TextView) card.findViewById(R.id.stat_count)).setText(count);
        ((TextView) card.findViewById(R.id.stat_title)).setText(title);
        layoutStats.addView(card);
    }

    private void addActionCard(String icon, String label) {
        View card = LayoutInflater.from(getContext()).inflate(R.layout.card_action, layoutActions, false);
        ((TextView) card.findViewById(R.id.action_icon)).setText(icon);
        ((TextView) card.findViewById(R.id.action_label)).setText(label);
        layoutActions.addView(card);
    }

    private void addClassCard(String subject, String grade, String time, String status) {
        View card = LayoutInflater.from(getContext()).inflate(R.layout.card_class, layoutExtra, false);
        ((TextView) card.findViewById(R.id.class_subject)).setText(subject);
        ((TextView) card.findViewById(R.id.class_grade)).setText(grade);
        ((TextView) card.findViewById(R.id.class_time)).setText(time);
        ((TextView) card.findViewById(R.id.class_status)).setText(status);
        layoutExtra.addView(card);
    }


    private void loadStudentContent() {
        setWelcome("Welcome, Student");

        addSimpleText(layoutStats, "Stats: Subjects, Progress");
        addSimpleText(layoutActions, "Actions: View Materials, Submit Work");
        addSimpleText(layoutExtra, "Extra: Upcoming Assignments");
    }

    private void loadAdminContent() {
        setWelcome("Welcome, Admin");

        addSimpleText(layoutStats, "Stats: Total Students, Teachers, Revenue");
        addSimpleText(layoutActions, "Actions: Manage Users, Reports");
        addSimpleText(layoutExtra, "Extra: System Overview");
    }

    // Shared helper methods

    private void setWelcome(String title) {
        layoutWelcome.removeAllViews();
        TextView welcomeText = new TextView(getContext());
        welcomeText.setText(title);
        welcomeText.setTextSize(20);
        welcomeText.setTypeface(null, Typeface.BOLD);
        layoutWelcome.addView(welcomeText);
    }

    private void addSimpleText(ViewGroup parent, String text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTextSize(16);
        textView.setPadding(0, 12, 0, 12);
        parent.addView(textView);
    }
}
