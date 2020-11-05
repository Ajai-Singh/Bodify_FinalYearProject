package com.example.bodify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ViewAllUsers extends AppCompatActivity{
    private RecyclerView rv;
    FirebaseRecyclerOptions<User> userFirebaseRecyclerOptions;
    FirebaseRecyclerAdapter<User,UserHolder> adapter;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_users);
        rv = findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(linearLayoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User");
        userFirebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<User>().setQuery(ref,User.class).build();
        adapter = new FirebaseRecyclerAdapter<User,UserHolder>(userFirebaseRecyclerOptions) {

            @Override
            protected void onBindViewHolder(@NonNull UserHolder holder, int position, @NonNull User model) {
                //I want to add a constraint somewhere here to remove a specific row
                final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                holder.setUserName(model.getUserName());
                holder.setEmailAddress(model.getEmail());
                holder.setImage(model.getmImageUrl());
//                for(int i = 0; i < rv.getChildCount();i++) {
//                    RecyclerView.ViewHolder test = rv.findContainingViewHolder(holder.email);
//                    if(test.toString().equals(firebaseUser.getEmail())) {
//                        int p = test.getAdapterPosition();
//                        rv.getLayoutManager().removeViewAt(p);
//                    }
//                }

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