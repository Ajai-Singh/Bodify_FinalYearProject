package com.example.bodify;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignUp extends AppCompatActivity {
    private EditText emailAddress, userName, password, verifyPassword;
    private Button registerButton;
    private TextView signInInstead;
    private FirebaseAuth mAuth;
    public static final String MESSAGE_KEY = "MESSAGE1";

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
                String strEmailAddress = emailAddress.getText().toString().trim();
                String strPassword = password.getText().toString();
                String strVerifyPassword = verifyPassword.getText().toString();
                 if(TextUtils.isEmpty(strUserName)) {
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
                        mAuth.createUserWithEmailAndPassword(strEmailAddress, strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Intent intent = new Intent(SignUp.this,Tailoring.class);
                                    intent.putExtra(MESSAGE_KEY,strUserName);
                                    Toast.makeText(getApplicationContext(),"User Created Successfully!",Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                    userName.setText("");
                                    emailAddress.setText("");
                                    password.setText("");
                                    verifyPassword.setText("");
                                }else {
                                    Toast.makeText(getApplicationContext(),"Error Occurred!" + task.getException().toString(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }
            }
        });
    }
}


