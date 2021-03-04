package com.example.bodify.FirebaseAuthentication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bodify.Models.User;
import com.example.bodify.R;
import com.example.bodify.Tailoring;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Objects;

public class SignUp extends AppCompatActivity {
    private EditText emailAddress, userName, password, verifyPassword;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private ImageView profilePlaceHolder;
    public static final String MESSAGE_KEY = "MESSAGE1";
    public static final String MESSAGE_KEY1 = "MESSAGE2";
    private Uri mImageUri;
    private String imageDownloadUrl;
    private final ArrayList<String> userNames = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
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
        if (mImageUri != null) {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Uploading Image....");
            pd.show();
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));
            fileReference.putFile(mImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), "Upload Successful", Toast.LENGTH_SHORT).show();
                        imageDownloadUrl = fileReference.getPath();
                    }).addOnFailureListener(e -> {
                pd.dismiss();
                Toast.makeText(getApplicationContext(), "Error Occurred!" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }).addOnProgressListener(snapshot -> {
                double progressPercent = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Percentage " + (int) progressPercent + " " + "%");
            });
        } else {
            Toast.makeText(SignUp.this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sign Up Form");
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
        uploadProfilePicture.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1000);
        });
        signInInstead.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LogIn.class));
        });
        registerButton.setOnClickListener(v -> {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        User user = userSnapshot.getValue(User.class);
                        assert user != null;
                        userNames.add(user.getUserName());
                    }
                    createUser(userNames);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SignUp.this, "Error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    public void createUser(ArrayList<String> userNames) {
        if (TextUtils.isEmpty(userName.getText().toString().trim())) {
            userName.setError("User Name is required.");
            userName.requestFocus();
        } else if (TextUtils.isEmpty(emailAddress.getText().toString().trim())) {
            emailAddress.setError("Email Address is required.");
            emailAddress.requestFocus();
        } else if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Password is required.");
            password.requestFocus();
        } else if (TextUtils.isEmpty(verifyPassword.getText().toString())) {
            verifyPassword.setError("Password confirmation is required.");
            verifyPassword.requestFocus();
        } else if (password.getText().toString().length() < 6) {
            password.setError("Password length must be at least 6 characters.");
            password.requestFocus();
        } else if (verifyPassword.getText().toString().length() < 6) {
            verifyPassword.setError("Password length must be at least 6 characters.");
            verifyPassword.requestFocus();
        } else if (!verifyPassword.getText().toString().equals(password.getText().toString())) {
            password.setError("Passwords do not match");
            password.requestFocus();
            verifyPassword.setError("Passwords do not match");
            verifyPassword.requestFocus();
        } else if (userNames.contains(userName.getText().toString().trim())) {
            userName.setError("Error User name already exists!");
            userName.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(emailAddress.getText().toString().trim(), password.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(SignUp.this, Tailoring.class);
                    intent.putExtra(MESSAGE_KEY, userName.getText().toString().trim());
                    intent.putExtra(MESSAGE_KEY1, imageDownloadUrl);
                    Toast.makeText(getApplicationContext(), "User Created Successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    userName.setText("");
                    emailAddress.setText("");
                    password.setText("");
                    verifyPassword.setText("");
                } else {
                    Toast.makeText(getApplicationContext(), "Error Occurred!" + Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}


