package com.example.smarttuitionmanager;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

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
            userRole = getArguments().getString("role", "teacher");
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

    private void loadTeacherContent() {
        setWelcome("Welcome, Teacher");

        // Simulate dynamic stats data
        ArrayList<Stat> stats = new ArrayList<>();
        stats.add(new Stat("45", "Students"));
        stats.add(new Stat("5", "Classes"));
        stats.add(new Stat("12", "Assignments"));
        for (Stat stat : stats) {
            addStatCard(stat.count, stat.title);
        }

        // Simulate dynamic actions data
        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new Action(R.drawable.ic_notification, "Notifications"));
        actions.add(new Action(R.drawable.ic_subjects, "Attendance"));
        actions.add(new Action(R.drawable.ic_home, "Materials"));
        actions.add(new Action(R.drawable.ic_report, "Alerts"));
        for (Action action : actions) {
            addActionCard(action.iconRes, action.label);
        }

        // Simulate dynamic class data
        ArrayList<ClassItem> classes = new ArrayList<>();
        classes.add(new ClassItem("Maths", "Grade 10", "9:00 AM", "Now"));
        classes.add(new ClassItem("Physics", "Grade 11", "11:00 AM", "Upcoming"));
        for (ClassItem classItem : classes) {
            addClassCard(classItem.subject, classItem.grade, classItem.time, classItem.status);
        }
    }

    private void addStatCard(String count, String title) {
        View card = LayoutInflater.from(getContext()).inflate(R.layout.card_stat, layoutStats, false);
        ((TextView) card.findViewById(R.id.stat_count)).setText(count);
        ((TextView) card.findViewById(R.id.stat_title)).setText(title);
        layoutStats.addView(card);
    }

    private void addActionCard(int iconRes, String label) {
        View card = LayoutInflater.from(getContext()).inflate(R.layout.card_action, layoutActions, false);
        ((TextView) card.findViewById(R.id.action_label)).setText(label);
        // Ensure the layout has an ImageView with id action_icon
        ImageView iconView = card.findViewById(R.id.action_icon);
        if (iconView != null) {
            iconView.setImageResource(iconRes);
        }
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

    // Data models
    private static class Stat {
        String count, title;
        Stat(String count, String title) {
            this.count = count;
            this.title = title;
        }
    }

    private static class Action {
        int iconRes;
        String label;
        Action(int iconRes, String label) {
            this.iconRes = iconRes;
            this.label = label;
        }
    }

    private static class ClassItem {
        String subject, grade, time, status;
        ClassItem(String subject, String grade, String time, String status) {
            this.subject = subject;
            this.grade = grade;
            this.time = time;
            this.status = status;
        }
    }
}