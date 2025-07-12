package com.example.smarttuitionmanager;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportsFragment extends Fragment {

    private MyDatabaseHelper mydb;
    private LinearLayout cardTotalStudents, cardTotalTeachers, cardMostPopularSubject;
    private ProgressBar progressBar;
    private PieChart pieChartTeachers;
    private HorizontalBarChart barChartStudents;
    private BarChart barChartClasses;

    private List<MyDatabaseHelper.StudentWithSubjects> cachedStudents;
    private List<MyDatabaseHelper.Teacher> cachedTeachers;
    private ArrayList<String> cachedBarLabels;
    private ArrayList<String> cachedGradeLabels;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);
        mydb = new MyDatabaseHelper(getContext());
        initializeViews(view);
        loadUserFriendlyReport();
        return view;
    }

    private void initializeViews(View view) {
        cardTotalStudents = view.findViewById(R.id.card_total_students);
        cardTotalTeachers = view.findViewById(R.id.card_total_teachers);
        cardMostPopularSubject = view.findViewById(R.id.card_most_popular_subject);
        pieChartTeachers = view.findViewById(R.id.pie_chart_teachers);
        barChartStudents = view.findViewById(R.id.bar_chart_students);
        barChartClasses = view.findViewById(R.id.bar_chart_classes);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    private void loadUserFriendlyReport() {
        showLoading(true);
        barChartStudents.postDelayed(() -> {
            List<MyDatabaseHelper.StudentWithSubjects> students = mydb.getAllStudentsWithSubjects();
            List<MyDatabaseHelper.Teacher> teachers = mydb.getAllTeachers();
            cachedStudents = students;
            cachedTeachers = teachers;
            if (students.isEmpty() && teachers.isEmpty()) {
                showEmptyState(true);
                showLoading(false);
                return;
            }
            showEmptyState(false);
            updateSummaryCards(students, teachers);
            updateCharts(students, teachers);
            showLoading(false);
            setupDrillDownListeners();
        }, 400);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showEmptyState(boolean show) {
        // tvEmptyState.setVisibility(show ? View.VISIBLE : View.GONE); // This line was removed
    }

    private void updateSummaryCards(List<MyDatabaseHelper.StudentWithSubjects> students, List<MyDatabaseHelper.Teacher> teachers) {
        int totalStudents = students.size();
        int totalTeachers = teachers.size();
        // Find most popular subject
        Map<String, Integer> subjectCount = new HashMap<>();
        for (MyDatabaseHelper.StudentWithSubjects student : students) {
            for (String subject : student.subjects) {
                subjectCount.put(subject, subjectCount.getOrDefault(subject, 0) + 1);
            }
        }
        String mostPopularSubject = "-";
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : subjectCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostPopularSubject = entry.getKey();
            }
        }
        updateStatCard(cardTotalStudents, String.valueOf(totalStudents), "Total Students", Color.parseColor("#1976D2"));
        updateStatCard(cardTotalTeachers, String.valueOf(totalTeachers), "Total Teachers", Color.parseColor("#388E3C"));
        updateStatCard(cardMostPopularSubject, mostPopularSubject, "Most Popular Subject", Color.parseColor("#FBC02D"));

        cardTotalStudents.setOnClickListener(v -> {
            ArrayList<String> studentNames = new ArrayList<>();
            for (MyDatabaseHelper.StudentWithSubjects s : students) {
                studentNames.add(s.firstName + " " + s.lastName + " (Grade " + s.grade + ")");
            }
            DrillDownListDialogFragment.newInstance("All Students", studentNames).show(getParentFragmentManager(), "students_dialog");
        });
        cardTotalTeachers.setOnClickListener(v -> {
            ArrayList<String> teacherNames = new ArrayList<>();
            for (MyDatabaseHelper.Teacher t : teachers) {
                teacherNames.add(t.firstName + " " + t.lastName + " (" + t.subject + ")");
            }
            DrillDownListDialogFragment.newInstance("All Teachers", teacherNames).show(getParentFragmentManager(), "teachers_dialog");
        });
        final String mostPopularSubjectFinal = mostPopularSubject;
        cardMostPopularSubject.setOnClickListener(v -> {
            ArrayList<String> subjectStudents = new ArrayList<>();
            for (MyDatabaseHelper.StudentWithSubjects s : students) {
                if (s.subjects.contains(mostPopularSubjectFinal)) {
                    subjectStudents.add(s.firstName + " " + s.lastName + " (Grade " + s.grade + ")");
                }
            }
            DrillDownListDialogFragment.newInstance("Students in " + mostPopularSubjectFinal, subjectStudents).show(getParentFragmentManager(), "subject_students_dialog");
        });
    }

    private void updateStatCard(LinearLayout card, String count, String title, int color) {
        TextView countView = card.findViewById(R.id.stat_count);
        TextView titleView = card.findViewById(R.id.stat_title);
        countView.setText(count);
        titleView.setText(title);
        countView.setTextColor(color);
        // Optionally, set an icon here if you add ImageView to card_stat.xml
    }

    private void updateCharts(List<MyDatabaseHelper.StudentWithSubjects> students, List<MyDatabaseHelper.Teacher> teachers) {
        // Teacher Pie Chart
        Map<String, Integer> teacherSubjectCount = new HashMap<>();
        for (MyDatabaseHelper.Teacher teacher : teachers) {
            teacherSubjectCount.put(teacher.subject, teacherSubjectCount.getOrDefault(teacher.subject, 0) + 1);
        }
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : teacherSubjectCount.entrySet()) {
            pieEntries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Teachers by Subject");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextSize(14f);
        pieDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        PieData pieData = new PieData(pieDataSet);
        pieChartTeachers.setData(pieData);
        pieChartTeachers.getDescription().setEnabled(false);
        pieChartTeachers.setCenterText("Teachers");
        pieChartTeachers.setEntryLabelColor(Color.BLACK);
        pieChartTeachers.animateY(800);
        pieChartTeachers.invalidate();

        // Student Horizontal Bar Chart
        Map<String, Integer> studentSubjectCount = new HashMap<>();
        for (MyDatabaseHelper.StudentWithSubjects student : students) {
            for (String subject : student.subjects) {
                studentSubjectCount.put(subject, studentSubjectCount.getOrDefault(subject, 0) + 1);
            }
        }
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<String> barLabels = new ArrayList<>();
        int idx = 0;
        for (Map.Entry<String, Integer> entry : studentSubjectCount.entrySet()) {
            barEntries.add(new BarEntry(idx, entry.getValue()));
            barLabels.add(entry.getKey());
            idx++;
        }
        BarDataSet barDataSet = new BarDataSet(barEntries, "Students by Subject");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextSize(14f);
        barDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        BarData barData = new BarData(barDataSet);
        barChartStudents.setData(barData);
        barChartStudents.getDescription().setEnabled(false);
        barChartStudents.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(barLabels));
        barChartStudents.getXAxis().setLabelRotationAngle(45);
        barChartStudents.animateY(800);
        barChartStudents.invalidate();

        // Grade Distribution Vertical Bar Chart
        // Always show grades 6-11
        String[] allGrades = {"6", "7", "8", "9", "10", "11"};
        Map<String, Integer> gradeCount = new HashMap<>();
        for (String g : allGrades) gradeCount.put(g, 0);
        for (MyDatabaseHelper.StudentWithSubjects student : students) {
            String grade = student.grade != null ? student.grade.trim() : "";
            if (gradeCount.containsKey(grade)) {
                gradeCount.put(grade, gradeCount.get(grade) + 1);
            }
        }
        idx = 0;
        ArrayList<BarEntry> gradeEntries = new ArrayList<>();
        ArrayList<String> gradeLabels = new ArrayList<>();
        for (String g : allGrades) {
            gradeEntries.add(new BarEntry(idx, gradeCount.get(g)));
            gradeLabels.add("Grade " + g);
            idx++;
        }
        BarDataSet gradeDataSet = new BarDataSet(gradeEntries, "Students by Grade");
        gradeDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        gradeDataSet.setValueTextSize(14f);
        gradeDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        BarData gradeBarData = new BarData(gradeDataSet);
        gradeBarData.setBarWidth(0.7f); // Wider bars for better alignment
        barChartClasses.setData(gradeBarData);
        barChartClasses.getDescription().setEnabled(false);
        barChartClasses.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(gradeLabels));
        barChartClasses.getXAxis().setPosition(com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM);
        barChartClasses.getXAxis().setGranularity(1f);
        barChartClasses.getXAxis().setGranularityEnabled(true);
        barChartClasses.getXAxis().setCenterAxisLabels(false);
        barChartClasses.getXAxis().setDrawGridLines(false);
        barChartClasses.getXAxis().setLabelCount(gradeLabels.size());
        barChartClasses.getXAxis().setAvoidFirstLastClipping(true);
        barChartClasses.animateY(800);
        barChartClasses.invalidate();

        // Save barLabels and gradeLabels for drill-down
        cachedBarLabels = barLabels;
        cachedGradeLabels = gradeLabels;

        // Add chart drill-down listeners
        pieChartTeachers.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry pe = (PieEntry) e;
                String subject = pe.getLabel();
                ArrayList<String> teacherNames = new ArrayList<>();
                for (MyDatabaseHelper.Teacher t : teachers) {
                    if (t.subject.equals(subject)) {
                        teacherNames.add(t.firstName + " " + t.lastName);
                    }
                }
                DrillDownListDialogFragment.newInstance("Teachers: " + subject, teacherNames).show(getParentFragmentManager(), "pie_teachers_dialog");
            }
            @Override
            public void onNothingSelected() {}
        });
        barChartStudents.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int index = (int) e.getX();
                String subject = cachedBarLabels.get(index);
                ArrayList<String> studentNames = new ArrayList<>();
                for (MyDatabaseHelper.StudentWithSubjects s : cachedStudents) {
                    if (s.subjects.contains(subject)) {
                        studentNames.add(s.firstName + " " + s.lastName + " (Grade " + s.grade + ")");
                    }
                }
                DrillDownListDialogFragment.newInstance("Students: " + subject, studentNames).show(getParentFragmentManager(), "bar_students_dialog");
            }
            @Override
            public void onNothingSelected() {}
        });
        barChartClasses.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int index = (int) e.getX();
                String grade = cachedGradeLabels.get(index).replace("Grade ", "");
                ArrayList<String> studentNames = new ArrayList<>();
                for (MyDatabaseHelper.StudentWithSubjects s : cachedStudents) {
                    if (s.grade.equals(grade)) {
                        studentNames.add(s.firstName + " " + s.lastName + " (" + s.email + ")");
                    }
                }
                DrillDownListDialogFragment.newInstance("Students: Grade " + grade, studentNames).show(getParentFragmentManager(), "bar_grade_dialog");
            }
    @Override
            public void onNothingSelected() {}
        });
    }

    private void setupDrillDownListeners() {
        // No specific listeners to set up here as they are set in updateCharts
    }
}