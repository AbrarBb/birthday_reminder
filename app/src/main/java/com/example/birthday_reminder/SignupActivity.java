package com.example.birthday_reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SignupActivity extends AppCompatActivity {
    private Button btnExit, btnSignup, btnToggle;
    private EditText etUserId, etPass, etRePass;
    private TextView tvTitle, tvRepassLabel;
    private SharedPreferences pref;
    private boolean isLoginMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        tvTitle = findViewById(R.id.tvTitle);
        tvRepassLabel = findViewById(R.id.tvRepassLabel);
        btnExit = findViewById(R.id.btnExit);
        btnSignup = findViewById(R.id.btnSignup);
        btnToggle = findViewById(R.id.btnToggle);
        etUserId = findViewById(R.id.etUserId);
        etPass = findViewById(R.id.etPass);
        etRePass = findViewById(R.id.etRePass);

        pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        // Check if any user was previously logged in
        String lastUser = pref.getString("LOGGED_IN_USER", "");
        if (!lastUser.isEmpty()) {
            setLoginMode(true);
            etUserId.setText(lastUser);
        }

        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoginMode(!isLoginMode);
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = etUserId.getText().toString().trim();
                String pass = etPass.getText().toString().trim();
                String rePass = etRePass.getText().toString().trim();

                if (user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isLoginMode) {
                    // Login Logic: Retrieve password for this specific user
                    String savedPass = pref.getString("PASS_" + user, "");
                    if (!savedPass.isEmpty() && pass.equals(savedPass)) {
                        saveSession(user);
                        goToMain();
                    } else {
                        Toast.makeText(SignupActivity.this, "Invalid User ID or Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Signup Logic
                    if (user.length() < 4) {
                        Toast.makeText(SignupActivity.this, "User ID must be at least 4 letters", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (pass.length() < 4) {
                        Toast.makeText(SignupActivity.this, "Password must be 4 digits", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!pass.equals(rePass)) {
                        Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Store password with a user-specific key to support multiple accounts
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("PASS_" + user, pass);
                    editor.apply();

                    Toast.makeText(SignupActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    setLoginMode(true);
                }
            }
        });
    }

    private void saveSession(String user) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("LOGGED_IN_USER", user);
        editor.apply();
    }

    private void setLoginMode(boolean loginMode) {
        this.isLoginMode = loginMode;
        if (loginMode) {
            tvTitle.setText("Login");
            btnSignup.setText("Login");
            btnToggle.setText("Don't have an account");
            etRePass.setVisibility(View.GONE);
            tvRepassLabel.setVisibility(View.GONE);
        } else {
            tvTitle.setText("Signup");
            btnSignup.setText("Signup");
            btnToggle.setText("Already have an account");
            etRePass.setVisibility(View.VISIBLE);
            tvRepassLabel.setVisibility(View.VISIBLE);
        }
    }

    private void goToMain() {
        Intent i = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
