package com.example.bodify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.Models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewAllUsers extends AppCompatActivity{
    private RecyclerView rv;
    private FirebaseRecyclerOptions<User> userFirebaseRecyclerOptions;
    private FirebaseRecyclerAdapter<User,UserHolder> adapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = mAuth.getCurrentUser();
    private ArrayList<User> users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_users);
        getAllUsers();
        rv = findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(linearLayoutManager);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User");
        userFirebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<User>().setQuery(ref,User.class).build();
        adapter = new FirebaseRecyclerAdapter<User,UserHolder>(userFirebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull User model) {
                //no matter what if I add a statement in here to avoid current user it will end up coming as a blank view
                holder.setUserName(model.getUserName());
                holder.setEmailAddress(model.getEmail());
                holder.setImage(model.getmImageUrl());
            }
            @NonNull
            @Override
            public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
                return new UserHolder(view);
            }


        };
        adapter.startListening();
        rv.setAdapter(adapter);
    }

    //steps
    //step 1. read all User Objects
    //Step 2. manually filter them
    //get a arraylist of all the users and simply remove the ones you dont want
    public void getAllUsers() {
        users = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    users.add(user);
                    for (int i = 0; i < users.size(); i++) {
                        if (users.get(i).getEmail().equalsIgnoreCase(firebaseUser.getEmail())) {
                            users.remove(i);
                    }
                }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewAllUsers.this,"Error Occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }


            @Override
    protected void onStart() {
        super.onStart();
        if(adapter != null) {
            adapter.startListening();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(adapter != null) {
            adapter.stopListening();
            super.onStop();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null) {
            adapter.startListening();
        }
    }
}