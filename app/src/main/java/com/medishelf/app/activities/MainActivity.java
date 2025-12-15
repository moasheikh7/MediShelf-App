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
import com.medishelf.app.R;
import com.medishelf.app.firebase.FirebaseHelper;
import com.medishelf.app.firebase.UserSession;

public class MainActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView registerLink;
    private ProgressBar progressBar;
    private FirebaseHelper firebaseHelper;
    private UserSession userSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseHelper = FirebaseHelper.getInstance();
        userSession = new UserSession(this);

        // --- SAFETY CHECK: AUTO-LOGIN DISABLED FOR FIRST RUN ---
        // Once you confirm the app opens, you can uncomment these lines later.
        /*
        if (userSession.isLoggedIn()) {
            redirectUser();
            return;
        }
        */

        initViews();
        setupListeners();
    }

    private void initViews() {
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerLink = findViewById(R.id.registerLink);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> loginUser());
        registerLink.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (TextUtils.isEmpty(email)) { emailInput.setError("Required"); return; }
        if (TextUtils.isEmpty(password)) { passwordInput.setError("Required"); return; }

        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        firebaseHelper.getAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    loginButton.setEnabled(true);
                    if (task.isSuccessful()) {
                        checkUserType(email);
                    } else {
                        Toast.makeText(MainActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserType(String email) {
        if (firebaseHelper.getAuth().getCurrentUser() == null) return;
        String userId = firebaseHelper.getAuth().getCurrentUser().getUid();

        firebaseHelper.getUsersRef().child(userId).get()
                .addOnCompleteListener(task -> {
                    String userType = "customer";
                    if (task.isSuccessful() && task.getResult().exists()) {
                        String type = task.getResult().child("usertype").getValue(String.class);
                        if (type != null) userType = type;
                    }
                    userSession.createLoginSession(email, userType);
                    redirectUser();
                });
    }

    private void redirectUser() {
        Intent intent;
        if (userSession.isAdmin()) {
            intent = new Intent(MainActivity.this, AdminOrdersActivity.class);
        } else {
            intent = new Intent(MainActivity.this, ProductsActivity.class);
        }
        startActivity(intent);
        finish();
    }
}