package com.example.bodify;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.Adapters.PostAdapter;
import com.example.bodify.Models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class CommunityWall extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private final ArrayList<Post> posts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_wall);
        Toolbar toolbar = findViewById(R.id.toolbarCommunityWall);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Home");
        getAllPosts();
        ImageView createPost = findViewById(R.id.post);
        final String userID = mAuth.getUid();
        assert userID != null;
        createPost.setOnClickListener(v -> {
            createPost();
        });
    }

    private void createPost() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CommunityWall.this);
        View view = getLayoutInflater().inflate(R.layout.postdialogue, null);
        EditText text = view.findViewById(R.id.postText);
        Button postText = view.findViewById(R.id.postButton);
        TextView counter = view.findViewById(R.id.postCounter);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int character = text.length();
                String length = String.valueOf(character);
                counter.setText(length + "/100");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        postText.setOnClickListener(v -> {
            if (text.getText().toString().isEmpty()) {
                text.setError("Post cannot be empty!");
                text.requestFocus();
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(CommunityWall.this);
                alert.setTitle("Attention").setNegativeButton("Close", (dialog, which) -> dialog.cancel()).setMessage("Would you like to post this message?").setPositiveButton("Ok", (dialog, which1) -> {
                    @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    Date date = new Date();
                    String currentDateTime = dateFormat.format(date);
                    String dialogueText = text.getText().toString();
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    assert firebaseUser != null;
                    String userID = firebaseUser.getUid();
                    Post post = new Post(currentDateTime, dialogueText, userID);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("Post").push().setValue(post).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Post Saved Successfully!", Toast.LENGTH_SHORT).show();
                            text.setText("");
                        } else {
                            Toast.makeText(getApplicationContext(), "Error Occurred!" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                });
                alert.create().show();
            }
        });
        builder.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void getAllPosts() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                posts.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Post post = userSnapshot.getValue(Post.class);
                    posts.add(post);
                }
                recyclerView = findViewById(R.id.postRecyclerView);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(CommunityWall.this));
                adapter = new PostAdapter(posts);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error Occurred!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}