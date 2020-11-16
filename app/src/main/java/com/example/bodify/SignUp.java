package com.example.bodify;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.util.Objects;

public class SignUp extends AppCompatActivity {
    private EditText emailAddress, userName, password, verifyPassword;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private ImageView profilePlaceHolder;
    public static final String MESSAGE_KEY = "MESSAGE1";
    public static final String MESSAGE_KEY1 = "MESSAGE2";
    private Uri  mImageUri;
    private String imageDownloadUrl;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                mImageUri = data.getData();
                Picasso.get().load(mImageUri).into(profilePlaceHolder);
                uploadImageToFirebase();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImageToFirebase() {
        if(mImageUri != null) {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Uploading Image....");
            pd.show();
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
            + "." + getFileExtension(mImageUri));
            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(),"Upload Successful",Toast.LENGTH_SHORT).show();
                            imageDownloadUrl = fileReference.getPath();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(),"Error Occurred!" + e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progressPercent = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    pd.setMessage("Percentage " + (int) progressPercent + " " + "%");
                }
            });
        } else {
            Toast.makeText(SignUp.this, "No file selected",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle("Sign Up Form");
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        mAuth = FirebaseAuth.getInstance();
        userName = findViewById(R.id.userNameSignUp);
        emailAddress = findViewById(R.id.emailAddressSignUp);
        password = findViewById(R.id.passwordTextField);
        verifyPassword = findViewById(R.id.confirmPasswordTextField);
        TextView signInInstead = findViewById(R.id.existing_account);
        Button registerButton = findViewById(R.id.signUpButton);
        Button uploadProfilePicture = findViewById(R.id.uploadPicture);
        profilePlaceHolder = findViewById(R.id.ProfilePicture);
        uploadProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),1000);
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
                        mAuth.createUserWithEmailAndPassword(strEmailAddress, strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Intent intent = new Intent(SignUp.this,Tailoring.class);
                                    intent.putExtra(MESSAGE_KEY,strUserName);
                                    intent.putExtra(MESSAGE_KEY1,imageDownloadUrl);
                                    Toast.makeText(getApplicationContext(),"User Created Successfully!",Toast.LENGTH_SHORT).show();
                                    startActivity(intent);
                                    userName.setText("");
                                    emailAddress.setText("");
                                    password.setText("");
                                    verifyPassword.setText("");
                                }else {
                                    Toast.makeText(getApplicationContext(),"Error Occurred!" + Objects.requireNonNull(task.getException()).toString(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }
            }
        });
    }
}


