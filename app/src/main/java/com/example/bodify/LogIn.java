package com.example.bodify;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogIn extends AppCompatActivity {
    private TextView createAcc;
    private ProgressBar progressBar;
    private Button logIn;
    private EditText emailAddress, password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Log In Form");
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();
        logIn = findViewById(R.id.logInButton);
        emailAddress = findViewById(R.id.userNameSignUp);
        password = findViewById(R.id.passwordTextField);
        createAcc = findViewById(R.id.createNewAccount);
        progressBar = findViewById(R.id.progressBar);
        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUp.class));
            }
        });
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmailAddress = emailAddress.getText().toString().trim();
                String strPassword = password.getText().toString();
                if(TextUtils.isEmpty(strEmailAddress) && TextUtils.isEmpty(strPassword)) {
                    emailAddress.setError("Email Address is required.");
                    emailAddress.requestFocus();
                    password.setError("Password is required.");
                    password.requestFocus();
                }
                else if(TextUtils.isEmpty(strEmailAddress)) {
                    emailAddress.setError("Email Address is required.");
                    emailAddress.requestFocus();
                }else if(TextUtils.isEmpty(strPassword)) {
                    password.setError("Password is required.");
                    password.requestFocus();
                }else if(strPassword.length() < 6) {
                    password.setError("Password length must be >= len(6)");
                    password.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(strEmailAddress, strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(LogIn.this, "User verified", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), DashBoard.class));
                            }else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LogIn.this, "Error occurred! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}