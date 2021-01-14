package com.example.bodify.FirebaseAuthentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bodify.Management;
import com.example.bodify.R;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;

public class LogIn extends AppCompatActivity {
    private EditText emailAddress, password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Log In Form");
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();
        Button logIn = findViewById(R.id.logInButton);
        emailAddress = findViewById(R.id.userNameSignUp);
        password = findViewById(R.id.passwordTextField);
        TextView createAcc = findViewById(R.id.createNewAccount);
        createAcc.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SignUp.class)));
        logIn.setOnClickListener(v -> {
            String strEmailAddress = emailAddress.getText().toString().trim();
            String strPassword = password.getText().toString();
            if(TextUtils.isEmpty(strEmailAddress)) {
                emailAddress.setError("Email Address is required.");
                emailAddress.requestFocus();
            }else if(TextUtils.isEmpty(strPassword)) {
                password.setError("Password is required.");
                password.requestFocus();
            }else if(strPassword.length() < 6) {
                password.setError("Password length must be >= len(6)");
                password.requestFocus();
            } else {
                mAuth.signInWithEmailAndPassword(strEmailAddress, strPassword).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Toast.makeText(LogIn.this, "User verified", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Management.class));
                    }else {
                        Toast.makeText(LogIn.this, "Error occurred! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}