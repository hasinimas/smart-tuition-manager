package com.example.smarttuitionmanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smarttuitionmanager.R;
import com.example.smarttuitionmanager.RegisterActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.content.SharedPreferences;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.app.AlertDialog;

public class Login extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private TextInputLayout tilEmail, tilPassword;
    private MaterialButton btnLogin, btnGoogleSignin;
    private View tvForgotPassword, tvSignUp;
    private RadioGroup rgUserType;
    private RadioButton rbStudent, rbTeacher, rbAdmin;
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize views
        initViews();

        // Set up click listeners
        setupClickListeners();

        // Handle edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        btnLogin = findViewById(R.id.btn_login);
        btnGoogleSignin = findViewById(R.id.btn_google_signin);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvSignUp = findViewById(R.id.tv_sign_up);
        rgUserType = findViewById(R.id.rg_user_type);
        rbStudent = findViewById(R.id.rb_student);
        rbTeacher = findViewById(R.id.rb_teacher);
        rbAdmin = findViewById(R.id.rb_admin);
        dbHelper = new MyDatabaseHelper(this);
        
        // Set up custom password toggle
        setupPasswordToggle();
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());
        btnGoogleSignin.setOnClickListener(v -> handleGoogleSignIn());
        tvForgotPassword.setOnClickListener(v -> handleForgotPassword());
        tvSignUp.setOnClickListener(v -> handleSignUp());
    }

    private void handleLogin() {
        // Clear previous errors on the input fields
        tilEmail.setError(null);
        tilPassword.setError(null);

        // Get user input from EditTexts
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password is required");
            return;
        }

        // Get selected user type
        final String userType;
        if (rbStudent.isChecked()) {
            userType = "student";
        } else if (rbTeacher.isChecked()) {
            userType = "teacher";
        } else if (rbAdmin.isChecked()) {
            userType = "admin";
        } else {
            userType = "student"; // Default fallback
        }

        // Show loading state on the login button
        btnLogin.setEnabled(false);
        btnLogin.setText("Logging In...");

        // Perform login validation
        btnLogin.postDelayed(() -> {
            try {
                String userRole = "";
                long userId = -1;

                // Check login based on selected user type
                switch (userType) {
                    case "student":
                        if (dbHelper.checkStudentLogin(email, password)) {
                            userRole = "student";
                            userId = dbHelper.getStudentId(email, password);
                        }
                        break;
                    case "teacher":
                        if (dbHelper.checkTeacherLogin(email, password)) {
                            userRole = "teacher";
                            userId = dbHelper.getTeacherId(email, password);
                        }
                        break;
                    case "admin":
                        if (dbHelper.checkAdminLogin(email, password)) {
                            userRole = "admin";
                            userId = dbHelper.getAdminId(email, password);
                        }
                        break;
                }

                if (userId != -1) {
                    // Login successful
                    Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    
                    // Save user info to SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putLong("user_id", userId);
                    editor.putString("user_role", userRole);
                    editor.putString("user_email", email);
                    if ("teacher".equals(userRole)) {
                        editor.putLong("teacherId", userId); // Save teacherId for fragments
                    }
                    editor.apply();

                    // Start MainActivity
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    // Login failed
                    Toast.makeText(Login.this, "Invalid " + userType + " credentials", Toast.LENGTH_SHORT).show();
                    tilEmail.setError("Invalid " + userType + " credentials");
                    tilPassword.setError("Invalid " + userType + " credentials");
                    
                    // Re-enable the login button
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Login");
                }
            } catch (Exception e) {
                android.util.Log.e("Login", "Error during login: " + e.getMessage());
                Toast.makeText(Login.this, "Error during login", Toast.LENGTH_SHORT).show();
                // Re-enable the login button if there's an error
                btnLogin.setEnabled(true);
                btnLogin.setText("Login");
            }
        }, 500);
    }

    private void handleGoogleSignIn() {
        Toast.makeText(this, "Google Log In clicked", Toast.LENGTH_SHORT).show();

    }

    private void handleForgotPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password")
               .setMessage("Contact Smart Tuition Manager")
               .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
               .setCancelable(true);
        
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupPasswordToggle() {
        // Set custom end icon drawable and click listener
        tilPassword.setEndIconDrawable(android.R.drawable.ic_menu_view);
        tilPassword.setEndIconOnClickListener(v -> {
            if (etPassword.getInputType() == (android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                // Password is hidden, show it
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                tilPassword.setEndIconDrawable(android.R.drawable.ic_menu_close_clear_cancel);
            } else {
                // Password is visible, hide it
                etPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                tilPassword.setEndIconDrawable(android.R.drawable.ic_menu_view);
            }
        });
    }

    private void handleSignUp() {
        Intent intent = new Intent(Login.this, RegisterActivity.class);
        startActivity(intent);
    }
}
