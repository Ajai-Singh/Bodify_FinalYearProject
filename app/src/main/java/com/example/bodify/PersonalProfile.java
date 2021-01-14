package com.example.bodify;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.Adapters.UsersPostAdapter;
import com.example.bodify.Models.Post;
import com.example.bodify.Models.User;
import com.google.firebase.auth.FirebaseAuth;
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

public class PersonalProfile extends AppCompatActivity {
    private RecyclerView rcv;
    private TextView dateJoined, tweetCount,myName;
    private final ArrayList<Post> posts = new ArrayList<>();
    private StorageReference storageReference;
    private CircleImageView circleImageView;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Personal Profile");
        rcv = findViewById(R.id.pprcv);
        dateJoined = findViewById(R.id.dateJoinedPP);
        tweetCount = findViewById(R.id.noOfTweetsPP);
        circleImageView = findViewById(R.id.ppp);
        myName = findViewById(R.id.usersPagePP);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        populateViews();
    }

    public void populateViews() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                dateJoined.setText(user.getDate());
                storageReference.child(user.getmImageUrl()).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    circleImageView.setImageBitmap(bitmap);

                });
                myName.setText(user.getUserName());
                getTweetCount();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PersonalProfile.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getTweetCount() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for(DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Post post = userSnapshot.getValue(Post.class);
                    assert post != null;
                    post.setId(userSnapshot.getKey());
                    if(post.getPostID().equals(userID)) {
                        count = count + 1;
                        posts.add(post);
                    }
                    tweetCount.setText(String.valueOf(count));
                }
                populateTweets(posts);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PersonalProfile.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void populateTweets(ArrayList<Post> tweets) {
        rcv.setHasFixedSize(true);
        rcv.setLayoutManager(new LinearLayoutManager(PersonalProfile.this));
        RecyclerView.Adapter adapter = new UsersPostAdapter(tweets,PersonalProfile.this);
        rcv.setAdapter(adapter);
    }
}