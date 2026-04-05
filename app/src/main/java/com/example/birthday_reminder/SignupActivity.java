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
    private TextView tvTitle;
    private SharedPreferences pref;
    private String userId, prevPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate...SignupActivity");
        setContentView(R.layout.activity_signup);

        tvTitle = findViewById(R.id.tvTitle);
        btnExit = findViewById(R.id.btnExit);
        btnSignup = findViewById(R.id.btnSignup);
        btnToggle = findViewById(R.id.btnToggle);
        etUserId = findViewById(R.id.etUserId);
        etPass = findViewById(R.id.etPass);
        etRePass = findViewById(R.id.etRePass);

        pref = this.getPreferences(MODE_PRIVATE);
        userId = pref.getString("USER_ID", "NO-ACCOUNT");
        if(!userId.equals("NO-ACCOUNT")){
            prevPass = pref.getString("PASS", "");
            // convert to login page
            etRePass.setVisibility(View.GONE);
            findViewById(R.id.tvRepassLabel).setVisibility(View.GONE);
            tvTitle.setText("Login");
            btnSignup.setText("Login");
            btnToggle.setText("Don't have an account");
        }

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("btnExit...SignupActivity");
                finish();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("btnGo...SignupActivity");

                String user = etUserId.getText().toString();
                String pass = etPass.getText().toString();
                String rePass = etRePass.getText().toString();

                System.out.println(user);
                System.out.println(pass);
                System.out.println(rePass);

                if(user.length() < 4){
                    Toast.makeText(SignupActivity.this, "User id must be 4-8 letters", Toast.LENGTH_LONG).show();
                    return;
                }

                if(pass.length() < 4){
                    return;
                }

                if(prevPass == null) {
                    if (rePass.length() < 4) {
                        return;
                    }
                    if (!pass.equals(rePass)) {
                        System.out.println("Passwords didn't match");
                        return;
                    }
                } else {
                    if(!user.equals(userId)){
                        Toast.makeText(SignupActivity.this, "User Id didn't match", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(!pass.equals(prevPass)) {
                        Toast.makeText(SignupActivity.this, "Password didn't match", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                System.out.println(userId);

                if(!userId.equals("NO-ACCOUNT")){
                    SharedPreferences.Editor et = pref.edit();
                    et.putString("USER_ID", userId);
                    et.putString("PASS", pass);
                    et.apply();
                }
                Intent i = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
    @Override
    public void onStart(){
        super.onStart();
        System.out.println("onStart...SignupActivity");
    }
    @Override
    public void onPause(){
        super.onPause();
        System.out.println("onPause...SignupActivity");
    }
    @Override
    public void onResume(){
        super.onResume();
        System.out.println("onResume...SignupActivity");
    }
    @Override
    public void onStop(){
        super.onStop();
        System.out.println("onStop...SignupActivity");
    }
    @Override
    public void onRestart(){
        super.onRestart();
        System.out.println("onRestart...SignupActivity");
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        System.out.println("onDestroy...SignupActivity");
    }
}