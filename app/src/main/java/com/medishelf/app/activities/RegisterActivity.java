package com.medishelf.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.medishelf.app.R;
import com.medishelf.app.firebase.FirebaseHelper;
import com.medishelf.app.firebase.UserSession;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput, confirmPasswordInput;
    private Button registerButton;
    private TextView loginLink;
    private ProgressBar progressBar;
    private FirebaseHelper firebaseHelper;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseHelper = FirebaseHelper.getInstance();
        userSession = new UserSession(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        registerButton.setOnClickListener(v -> registerUser());
        loginLink.setOnClickListener(v -> {
            finish();
        });
    }

    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters");
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInput.setError("Please confirm password");
            return;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordInput.setError("Passwords do not match");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        registerButton.setEnabled(false);

        firebaseHelper.getAuth().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseHelper.getAuth().getCurrentUser();
                        if (user != null) {
                            saveUserToDatabase(user.getUid(), email);
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        registerButton.setEnabled(true);
                        String error = task.getException() != null ?
                                task.getException().getMessage() : "Registration failed";
                        Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserToDatabase(String userId, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("usertype", "customer");

        firebaseHelper.getUsersRef().child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    registerButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this,
                                "Registration successful!", Toast.LENGTH_SHORT).show();

                        userSession.createLoginSession(email, "customer");

                        Intent intent = new Intent(RegisterActivity.this, ProductsActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                "Failed to save user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}