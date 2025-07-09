package com.example.smarttuitionmanager;

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

        // Load HomeFragment with role = "teacher" / for the testing
        if (savedInstanceState == null) {
            HomeFragment homeFragment = new HomeFragment();
            Bundle bundle = new Bundle();
            bundle.putString("role", "teacher"); // Change this to "student" or "admin" to test
            homeFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationBar);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            Bundle bundle = new Bundle();
            bundle.putString("role", "teacher");

            if (item.getItemId() == R.id.navHome) {
                selectedFragment = new HomeFragment();
            } else if (item.getItemId() == R.id.navAttendance) {
                selectedFragment = new TeacherAttendanceFragment();
            } else if (item.getItemId() == R.id.navSubjects) {
                selectedFragment = new SubjectsFragment();
            } else if (item.getItemId() == R.id.navProfile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                selectedFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });

    }
}