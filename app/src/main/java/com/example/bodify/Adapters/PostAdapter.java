package com.example.bodify.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bodify.Models.Post;
import com.example.bodify.Models.User;
import com.example.bodify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class PostAdapter extends RecyclerView.Adapter<com.example.bodify.Adapters.PostAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Post> posts;


    public PostAdapter(ArrayList<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public com.example.bodify.Adapters.PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.bodify.Adapters.PostAdapter.ViewHolder holder, final int position) {
        String a = posts.get(position).getPostID();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    assert user != null;
                    user.setUserID(userSnapshot.getKey());
                    if (user.getUserID().equals(a)) {
                        holder.setName(user.getUserName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("TAG", error.getMessage());
            }
        });
        holder.setText(posts.get(position).getPostText());
        holder.setDateTime(posts.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public void onClick(View v) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, text, dateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.userName);
            text = itemView.findViewById(R.id.tweet);
            dateTime = itemView.findViewById(R.id.timeStamp);
        }

        public void setName(String n) {
            name.setText(n);
        }

        public void setText(String t) {
            text.setText(t);
        }

        public void setDateTime(String dt) {
            dateTime.setText(dt);
        }
    }
}
