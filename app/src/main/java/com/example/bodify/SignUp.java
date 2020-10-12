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
import com.example.bodify.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    private EditText emailAddress, userName, password, verifyPassword;
    private Button registerButton;
    private ProgressBar progressBar;
    private TextView signInInstead;
    private FirebaseAuth mAuth;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;
    public static final String sendUserName = "SEND1";
    public static final String sendUserEmailAddress = "SEND2";
    public static final String sendUserPassword = "SEND3";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle("Sign Up Form");
        mAuth = FirebaseAuth.getInstance();
        userName = findViewById(R.id.userNameSignUp);
        emailAddress = findViewById(R.id.emailAddressSignUp);
        password = findViewById(R.id.passwordTextField);
        verifyPassword = findViewById(R.id.confirmPasswordTextField);
        progressBar = findViewById(R.id.signUpProgressBar);
        signInInstead = findViewById(R.id.existing_account);
        registerButton = findViewById(R.id.signUpButton);
        signInInstead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LogIn.class));
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String strUserName = userName.getText().toString().trim();
                final String strEmailAddress = emailAddress.getText().toString().trim();
                final String strPassword = password.getText().toString();
                String strVerifyPassword = verifyPassword.getText().toString();
                if(TextUtils.isEmpty(strUserName) && TextUtils.isEmpty(strEmailAddress) && TextUtils.isEmpty(strPassword) &&
                        TextUtils.isEmpty(strVerifyPassword)) {
                    userName.setError("User Name is required.");
                    userName.requestFocus();
                    emailAddress.setError("Email Address is required.");
                    emailAddress.requestFocus();
                    password.setError("Password is required.");
                    password.requestFocus();
                    verifyPassword.setError("Password confirmation is required.");
                    verifyPassword.requestFocus();
                }else if(TextUtils.isEmpty(strUserName)) {
                    userName.setError("User Name is required.");
                    userName.requestFocus();
                }else if(TextUtils.isEmpty(strEmailAddress)) {
                    emailAddress.setError("Email Address is required.");
                    emailAddress.requestFocus();
                }else if(TextUtils.isEmpty(strPassword)) {
                    password.setError("Password is required.");
                    password.requestFocus();
                }else if(TextUtils.isEmpty(strVerifyPassword)) {
                    verifyPassword.setError("Password confirmation is required.");
                    verifyPassword.requestFocus();
                }else if(strPassword.length() < 6) {
                    password.setError("Password length must be at least 6 characters.");
                    password.requestFocus();
                }else if(strVerifyPassword.length() < 6) {
                    verifyPassword.setError("Password length must be at least 6 characters.");
                    verifyPassword.requestFocus();
                }else if(!strVerifyPassword.equals(strPassword)) {
                    password.setError("Passwords do not match");
                    password.requestFocus();
                    verifyPassword.setError("Passwords do not match");
                    verifyPassword.requestFocus();
                }else {
                    //reference = rootNode.getReference("Users");
                    //User user = new User("Ajai_Singh","helloworld","ajai@live.ie","Dublin",88.0,6.0,26.0);
                    //reference.setValue(user);
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(strEmailAddress, strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SignUp.this, "User created", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUp.this, Tailoring.class);
                                intent.putExtra(sendUserName, strUserName);
                                intent.putExtra(sendUserEmailAddress, strEmailAddress);
                                intent.putExtra(sendUserPassword, strPassword);
                                startActivity(intent);
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignUp.this, "Error occurred! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}


