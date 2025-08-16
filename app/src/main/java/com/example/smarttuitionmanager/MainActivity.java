package com.example.smarttuitionmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
 MyDatabaseHelper mydb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        mydb = new MyDatabaseHelper(this);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Load HomeFragment with user role from SharedPreferences
        if (savedInstanceState == null) {
            try {
                HomeFragment homeFragment = new HomeFragment();
                Bundle bundle = new Bundle();

                // Get user role from SharedPreferences
                SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                String userRole = prefs.getString("user_role", "student"); // Default to student if not found

                bundle.putString("role", userRole);

                homeFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
            } catch (Exception e) {
                android.util.Log.e("MainActivity", "Error loading HomeFragment: " + e.getMessage());
                // Show a simple error message instead of crashing
                android.widget.Toast.makeText(this, "Error loading home screen", android.widget.Toast.LENGTH_SHORT).show();
            }
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationBar);

        // Get user role from SharedPreferences (reuse the one from above)
        SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String userRole = prefs.getString("user_role", "student");

        // Set the correct menu based on role
        bottomNav.getMenu().clear();
        if ("admin".equals(userRole)) {
            bottomNav.inflateMenu(R.menu.navbar_admin);
        } else if ("teacher".equals(userRole)) {
            bottomNav.inflateMenu(R.menu.navbar_teacher);
        } else if ("student".equals(userRole)) {
            bottomNav.inflateMenu(R.menu.navbar_student);
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            Bundle bundle = new Bundle();
            
            // Use the userRole variable from above
            bundle.putString("role", userRole);
            
            // Log the navigation attempt
            android.util.Log.d("MainActivity", "Navigation clicked: " + item.getTitle() + " (ID: " + item.getItemId() + ") for role: " + userRole);

            // Role-based navigation logic
            if (item.getItemId() == R.id.navHome) {
                selectedFragment = new HomeFragment();
            } else {
                // Handle different navigation items based on user role
                switch (userRole) {
                    case "admin":
                        if (item.getItemId() == R.id.navUsers) {
                            selectedFragment = new UsersFragment();
                            android.util.Log.d("MainActivity", "Loading UsersFragment for admin");
                        } else if (item.getItemId() == R.id.navSubjects) {
                            selectedFragment = new AssignSubjectFragment();
                            android.util.Log.d("MainActivity", "Loading AssignSubjectFragment for admin");
                        } else if (item.getItemId() == R.id.navReports) {
                            selectedFragment = new ReportsFragment();
                            android.util.Log.d("MainActivity", "Loading ReportsFragment for admin");
                        }
                        break;
                        
                    case "student":
                        if (item.getItemId() == R.id.navSubjects) {
                            selectedFragment = new StudentCourseGuide();
                        } else if (item.getItemId() == R.id.navAttendance) {
                            selectedFragment = new TeacherCourseGuide();
                        } else if (item.getItemId() == R.id.navProfile) {
                            selectedFragment = new ProfileFragment();
                        }
                        break;
                        
                    case "teacher":
                        if (item.getItemId() == R.id.navAttendance) {
                            selectedFragment = new TeacherAttendanceFragment();
                        } else if (item.getItemId() == R.id.navSubjects) {
                            selectedFragment = new TeacherCourseGuide();
                        } else if (item.getItemId() == R.id.navProfile) {
                            selectedFragment = new ProfileFragment();
                        }
                        break;
                }
            }

            if (selectedFragment != null) {
                try {
                    selectedFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                } catch (Exception e) {
                    android.util.Log.e("MainActivity", "Error loading fragment: " + e.getMessage());
                    android.widget.Toast.makeText(this, "Error loading " + item.getTitle(), android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            return true;
        });

    }
}