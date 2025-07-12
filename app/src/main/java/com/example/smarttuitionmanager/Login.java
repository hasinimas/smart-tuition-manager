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

public class Login extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private TextInputLayout tilEmail, tilPassword;
    private MaterialButton btnLogin, btnGoogleSignin;
    private View tvForgotPassword, tvSignUp;

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

        // Show loading state on the login button
        btnLogin.setEnabled(false);
        btnLogin.setText("Logging In...");

        // Simulate a delay for login (e.g., network/database operation)
        btnLogin.postDelayed(() -> {
            // Always allow login, regardless of email or password
            Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }, 500);
    }

    private void handleGoogleSignIn() {
        Toast.makeText(this, "Google Log In clicked", Toast.LENGTH_SHORT).show();

    }

    private void handleForgotPassword() {
        Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show();

    }

    private void handleSignUp() {
        Intent intent = new Intent(Login.this, RegisterActivity.class);
        startActivity(intent);
    }
}
