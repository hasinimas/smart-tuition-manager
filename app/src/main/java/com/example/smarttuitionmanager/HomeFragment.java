package com.example.smarttuitionmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private LinearLayout layoutWelcome, layoutStats, layoutExtra;
    private GridLayout layoutActions;
    private String userRole;
    private MyDatabaseHelper dbHelper;
    private int userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Bind views
        layoutWelcome = view.findViewById(R.id.layout_welcome);
        layoutStats = view.findViewById(R.id.layout_stats);
        layoutActions = view.findViewById(R.id.layout_actions);
        layoutExtra = view.findViewById(R.id.layout_extra);

        dbHelper = new MyDatabaseHelper(getContext());

        // Get user ID from SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        // Get user role from arguments (default to "student")
        if (getArguments() != null) {
            userRole = getArguments().getString("role", "student");
        } else {
            userRole = "student";
        }

        // Load content based on role
        switch (userRole) {
            case "teacher":
                loadTeacherContent();
                break;
            case "admin":
                loadAdminContent();
                break;
            case "student":
            default:
                loadStudentContent();
                break;
        }

        return view;
    }

    // --------------------- STUDENT ---------------------
    private void loadStudentContent() {
        setWelcome("Welcome, Student, Track your progress");

        int subjectCount = getCount("SELECT COUNT(*) FROM STUDENT_COURSES WHERE student_id = ?", new String[]{String.valueOf(userId)});
        int assignmentCount = getCount("SELECT COUNT(*) FROM ASSIGNMENTS WHERE Subject_id IN (SELECT subject_id FROM STUDENT_COURSES WHERE student_id = ?)", new String[]{String.valueOf(userId)});
        int presentCount = getCount("SELECT COUNT(*) FROM ATTENDANCE WHERE student_id = ? AND status = 'Present'", new String[]{String.valueOf(userId)});
        int totalAttendance = getCount("SELECT COUNT(*) FROM ATTENDANCE WHERE student_id = ?", new String[]{String.valueOf(userId)});
        String attendanceRate = totalAttendance > 0 ? (presentCount * 100 / totalAttendance) + "%" : "0%";

        layoutStats.removeAllViews();
        addStatCard(attendanceRate, "Attendance");
        addStatCard(String.valueOf(subjectCount), "Courses");
        addStatCard(String.valueOf(assignmentCount), "Assignments");

        layoutActions.removeAllViews();
        addActionCard(R.drawable.ic_materials, "Course Materials");
        addActionCard(R.drawable.ic_assignments, "Assignments");
        addActionCard(R.drawable.ic_attendance, "Attendance");
        addActionCard(R.drawable.ic_results, "Results");

        layoutExtra.removeAllViews();
        addClassCard("Mathematics", "Grade 10", "9:00 AM", "Now");
        addClassCard("Physics", "Grade 11", "11:00 AM", "Upcoming");
    }

    // --------------------- ADMIN ---------------------
    private void loadAdminContent() {
        setWelcome("Welcome, Admin");

        layoutStats.removeAllViews();
        layoutActions.removeAllViews();
        layoutExtra.removeAllViews();

        int studentCount = getCount("SELECT COUNT(*) FROM USERS WHERE Role = 'Student'", null);
        int teacherCount = getCount("SELECT COUNT(*) FROM USERS WHERE Role = 'Teacher'", null);
        int courseCount = getCount("SELECT COUNT(*) FROM Subject", null);

        addStatCard(String.valueOf(studentCount), "Students");
        addStatCard(String.valueOf(teacherCount), "Teachers");
        addStatCard(String.valueOf(courseCount), "Courses");
        addStatCard("₹1.2L", "This Month");
        addStatCard("₹8,000", "Pending Fees");

        addActionCard(R.drawable.ic_manage_users, "Users");
        addActionCard(R.drawable.ic_report, "Reports");
        addActionCard(R.drawable.ic_notification, "Notify");
        addActionCard(R.drawable.ic_approval, "Approvals");
        addActionCard(R.drawable.ic_fees, "Fees Report");

        layoutExtra.removeAllViews();
        addClassCard("System Health", "All Modules Active", "24x7", "Now");
        addClassCard("Backup Scheduled", "Weekly Backup", "Sun 2 AM", "Upcoming");
        addClassCard("Today’s Tasks", "Review Fee Logs", "3 Pending", "Now");
    }

    // --------------------- TEACHER (Demo Only) ---------------------
    private void loadTeacherContent() {
        setWelcome("Welcome, Teacher");

        ArrayList<Stat> stats = new ArrayList<>();
        stats.add(new Stat("45", "Students"));
        stats.add(new Stat("5", "Classes"));
        stats.add(new Stat("12", "Assignments"));
        for (Stat stat : stats) {
            addStatCard(stat.count, stat.title);
        }

        ArrayList<Action> actions = new ArrayList<>();
        actions.add(new Action(R.drawable.ic_notification, "Notifications"));
        actions.add(new Action(R.drawable.ic_subjects, "Attendance"));
        actions.add(new Action(R.drawable.ic_home, "Materials"));
        actions.add(new Action(R.drawable.ic_report, "Alerts"));
        for (Action action : actions) {
            addActionCard(action.iconRes, action.label);
        }

        ArrayList<ClassItem> classes = new ArrayList<>();
        classes.add(new ClassItem("Maths", "Grade 10", "9:00 AM", "Now"));
        classes.add(new ClassItem("Physics", "Grade 11", "11:00 AM", "Upcoming"));
        for (ClassItem classItem : classes) {
            addClassCard(classItem.subject, classItem.grade, classItem.time, classItem.status);
        }
    }

    // --------------------- UI Methods ---------------------
    private void setWelcome(String title) {
        if (layoutWelcome != null && getContext() != null) {
            layoutWelcome.removeAllViews();
            TextView welcomeText = new TextView(getContext());
            welcomeText.setText(title);
            welcomeText.setTextSize(20);
            welcomeText.setTypeface(Typeface.DEFAULT_BOLD);
            welcomeText.setPadding(16, 16, 16, 16);
            layoutWelcome.addView(welcomeText);
        }
    }

    private void addActionCard(int iconRes, String label) {
        View card = LayoutInflater.from(getContext()).inflate(R.layout.card_action, layoutActions, false);
        ((TextView) card.findViewById(R.id.action_label)).setText(label);
        ImageView iconView = card.findViewById(R.id.action_icon);
        if (iconView != null) {
            iconView.setImageResource(iconRes);
        }

        card.setOnClickListener(v -> {
            Fragment targetFragment = null;
            switch (label) {
                case "Course Materials":
                    targetFragment = new StudentCourseGuide();
                    break;
                case "Assignments":
                    targetFragment = new TeacherAssignment();
                    break;
                case "Attendance":
                    targetFragment = new TeacherAttendanceFragment();
                    break;
                case "Results":
                    targetFragment = new TeacherResults();
                    break;
                case "Users":
                    targetFragment = new UsersFragment();
                    break;
                case "Reports":
                    targetFragment = new ReportsFragment();
                    break;
                case "Notify":
                    targetFragment = new TeacherResults(); // Change this if you have a NotificationFragment
                    break;
                case "Approvals":
                    targetFragment = new TeacherResults(); // Replace with real ApprovalFragment if available
                    break;
                case "Fees Report":
                    targetFragment = new ReportsFragment(); // Replace if you have a separate FeesFragment
                    break;
            }

            if (targetFragment != null) {
                getParentFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, targetFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        layoutActions.addView(card);
    }

    private void addStatCard(String count, String title) {
        View card = LayoutInflater.from(getContext()).inflate(R.layout.card_stat, layoutStats, false);
        ((TextView) card.findViewById(R.id.stat_count)).setText(count);
        ((TextView) card.findViewById(R.id.stat_title)).setText(title);
        layoutStats.addView(card);
    }

    private void addClassCard(String subject, String grade, String time, String status) {
        View card = LayoutInflater.from(getContext()).inflate(R.layout.card_class, layoutExtra, false);
        ((TextView) card.findViewById(R.id.class_subject)).setText(subject);
        ((TextView) card.findViewById(R.id.class_grade)).setText(grade);
        ((TextView) card.findViewById(R.id.class_time)).setText(time);
        ((TextView) card.findViewById(R.id.class_status)).setText(status);
        layoutExtra.addView(card);
    }

    private int getCount(String query, String[] args) {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(query, args);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // --------------------- Data Models ---------------------
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
