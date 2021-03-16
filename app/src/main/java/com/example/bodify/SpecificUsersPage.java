package com.example.bodify;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.Adapters.PostAdapter;
import com.example.bodify.Models.Post;
import com.example.bodify.Models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;

public class SpecificUsersPage extends AppCompatActivity {
    private final ArrayList<Post> posts = new ArrayList<>();
    private TextView date, tweets, currentUser;
    private RecyclerView rcv;
    private CircleImageView circleImageView;
    private PostAdapter adapter;
    private String position;
    private StorageReference storageReference;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Welcome");
        circleImageView = findViewById(R.id.ppp);
        date = findViewById(R.id.dateJoinedPP);
        tweets = findViewById(R.id.noOfTweetsPP);
        rcv = findViewById(R.id.pprcv);
        currentUser = findViewById(R.id.usersPagePP);
        constraintLayout = findViewById(R.id.supCL);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        setViews();
    }

    public void setViews() {
        position = getIntent().getStringExtra("position");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            String userID;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    assert user != null;
                    user.setId(userSnapshot.getKey());
                    if(user.getEmail().equals(position)) {
                        storageReference.child(user.getmImageUrl()).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            circleImageView.setImageBitmap(bitmap);
                        });
                        currentUser.setText(user.getUserName());
                        date.setText(user.getsignUpDate());
                        userID = user.getId();
                        break;
                    }
                }
                getTweetCount(userID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SpecificUsersPage.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getTweetCount(String userID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int tweetCount = 0;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Post post = userSnapshot.getValue(Post.class);
                    assert post != null;
                    if (post.getPostID().equals(userID)) {
                        Log.i("num", "equals");
                        tweetCount += 1;
                    }
                    tweets.setText(String.valueOf(tweetCount));
                }
                getAllUserPosts(userID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("TAG",error.getMessage());
            }
        });
    }

    public void getAllUserPosts(String userID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Post post = userSnapshot.getValue(Post.class);
                    assert post != null;
                    if (post.getPostID().equals(userID)) {
                        posts.add(post);
                    }
                }
                if(posts.isEmpty()) {
                    tweets.setText("0");
                }
                rcv.setHasFixedSize(true);
                rcv.setLayoutManager(new LinearLayoutManager(SpecificUsersPage.this));
                adapter = new PostAdapter(posts,SpecificUsersPage.this);
                rcv.setAdapter(adapter);
                if(posts.isEmpty()) {
                    showSnackBar();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error Occurred!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showSnackBar() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Sorry this user has no posts!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}

