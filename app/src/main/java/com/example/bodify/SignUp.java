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
    private Button registerButton, selectProfilePictureButton;
    private ImageView profilePictureImageView;
    private ProgressBar progressBar;
    private TextView signInInstead;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    public static final String MESSAGE_KEY = "MESSAGE1";
    public static final String MESSAGE_KEY1 = "MESSAGE2";
    private String imageURL;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000) {
            if(resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                profilePictureImageView.setImageURI(imageUri);
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(final Uri imageUri) {
        StorageReference fileReference = storageReference.child("profile.jpg");
        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(),"Image Successfully Uploaded",Toast.LENGTH_SHORT).show();
                imageURL = taskSnapshot.getStorage().getDownloadUrl().toString();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Error Occurred!" + e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle("Sign Up Form");
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        selectProfilePictureButton = findViewById(R.id.profilePictureButton);
        profilePictureImageView = findViewById(R.id.profilePictureImageView);
        userName = findViewById(R.id.userNameSignUp);
        emailAddress = findViewById(R.id.emailAddressSignUp);
        password = findViewById(R.id.passwordTextField);
        verifyPassword = findViewById(R.id.confirmPasswordTextField);
        progressBar = findViewById(R.id.signUpProgressBar);
        signInInstead = findViewById(R.id.existing_account);
        registerButton = findViewById(R.id.signUpButton);
        selectProfilePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Gallery
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1000);
            }
        });
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
                        progressBar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(strEmailAddress, strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Intent intent = new Intent(SignUp.this,Tailoring.class);
                                    intent.putExtra(MESSAGE_KEY,strUserName);
                                    intent.putExtra(MESSAGE_KEY1,imageURL);
                                    Toast.makeText(getApplicationContext(),"User Created Successfully!",Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                    userName.setText("");
                                    emailAddress.setText("");
                                    password.setText("");
                                    verifyPassword.setText("");
                                }else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(),"Error Occurred!" + task.getException().toString(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }
            }
        });
    }
}


