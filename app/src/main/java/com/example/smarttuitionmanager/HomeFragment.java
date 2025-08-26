package com.example.smarttuitionmanager;

import android.content.Context;
import android.content.Intent;
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
        userId = (int) prefs.getLong("user_id", -1);

        // Get user role from arguments (default to "student")
        if (getArguments() != null) {
            userRole = getArguments().getString("role", "teacher");
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


        // Real stats - using correct table names
        int subjectCount = getCount("SELECT COUNT(*) FROM student_subject WHERE student_id = ?", new String[]{String.valueOf(userId)});
        int assignmentCount = getCount("SELECT COUNT(*) FROM ASSIGNMENTS WHERE Subject_id IN (SELECT subject_id FROM student_subject WHERE student_id = ?)", new String[]{String.valueOf(userId)});
        // Note: ATTENDANCE table doesn't exist in current schema, using placeholder values
        int presentCount = 0; // Placeholder since ATTENDANCE table doesn't exist
        int totalAttendance = 0; // Placeholder since ATTENDANCE table doesn't exist
        String attendanceRate = "0%"; // Placeholder

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
        setWelcomeWithLogout("Welcome, Admin");

        layoutStats.removeAllViews();
        layoutActions.removeAllViews();
        layoutExtra.removeAllViews();

        // Using correct table names from the database schema
        int studentCount = getCount("SELECT COUNT(*) FROM student", null);
        int teacherCount = getCount("SELECT COUNT(*) FROM teacher", null);
        int courseCount = getCount("SELECT COUNT(*) FROM subject", null);

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

        layoutWelcome.removeAllViews();
        TextView welcomeText = new TextView(getContext());
        welcomeText.setText(title);
        welcomeText.setTextSize(20);
        welcomeText.setTypeface(null, Typeface.BOLD);
        layoutWelcome.addView(welcomeText);
    }

    private void setWelcomeWithLogout(String title) {
        layoutWelcome.removeAllViews();
        
        // Create horizontal layout for welcome text and logout button
        LinearLayout welcomeLayout = new LinearLayout(getContext());
        welcomeLayout.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        welcomeLayout.setOrientation(LinearLayout.HORIZONTAL);
        welcomeLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
        
        // Welcome text
        TextView welcomeText = new TextView(getContext());
        welcomeText.setText(title);
        welcomeText.setTextSize(20);
        welcomeText.setTypeface(null, Typeface.BOLD);
        welcomeText.setTextColor(android.graphics.Color.WHITE);
        welcomeText.setLayoutParams(new LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1.0f
        ));
        
        // Logout button
        ImageView logoutButton = new ImageView(getContext());
        logoutButton.setImageResource(R.drawable.ic_logout);
        logoutButton.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        logoutButton.setPadding(16, 16, 16, 16);
        logoutButton.setBackgroundResource(R.drawable.rounded_card_background);
        logoutButton.setColorFilter(android.graphics.Color.WHITE);
        
        // Add click listener for logout
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogout();
            }
        });
        
        welcomeLayout.addView(welcomeText);
        welcomeLayout.addView(logoutButton);
        layoutWelcome.addView(welcomeLayout);
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
        ImageView iconView = card.findViewById(R.id.action_icon);
        if (iconView != null) {
            iconView.setImageResource(iconRes);
        }

        
        // Add click listener for navigation
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleActionCardClick(label);
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
        try {

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(query, args);
        int count = 0;
            if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
            if (cursor != null) {
        cursor.close();
            }
        return count;
        } catch (Exception e) {
            // Log the error and return 0 to prevent crashes
            android.util.Log.e("HomeFragment", "Database query error: " + e.getMessage());
            return 0;
        }
    }

    private void handleLogout() {
        // Clear SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("LoginPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // Navigate to Login activity
        Intent intent = new Intent(getActivity(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void handleActionCardClick(String actionLabel) {
        Fragment selectedFragment = null;
        Bundle bundle = new Bundle();
        bundle.putString("role", userRole);
        
        switch (actionLabel) {
            case "Users":
                selectedFragment = new UsersFragment();
                break;
            case "Reports":
                selectedFragment = new ReportsFragment();
                break;
            case "Notify":
                // For now, show a toast message
                android.widget.Toast.makeText(getContext(), "Notifications feature coming soon!", android.widget.Toast.LENGTH_SHORT).show();
                return;
            case "Approvals":
                // For now, show a toast message
                android.widget.Toast.makeText(getContext(), "Approvals feature coming soon!", android.widget.Toast.LENGTH_SHORT).show();
                return;
            case "Fees Report":
                // For now, show a toast message
                android.widget.Toast.makeText(getContext(), "Fees Report feature coming soon!", android.widget.Toast.LENGTH_SHORT).show();
                return;
        }
        
        if (selectedFragment != null) {
            try {
                selectedFragment.setArguments(bundle);
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .addToBackStack(null)
                        .commit();
            } catch (Exception e) {
                android.util.Log.e("HomeFragment", "Error loading fragment: " + e.getMessage());
                android.widget.Toast.makeText(getContext(), "Error loading " + actionLabel, android.widget.Toast.LENGTH_SHORT).show();
            }
        }

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