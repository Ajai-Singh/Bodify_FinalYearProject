package com.example.bodify.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.Models.Post;
import com.example.bodify.Models.User;
import com.example.bodify.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class UsersPostAdapter extends RecyclerView.Adapter<com.example.bodify.Adapters.UsersPostAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Post> posts;
    private final Context context;

    public UsersPostAdapter(ArrayList<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public com.example.bodify.Adapters.UsersPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_own_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.bodify.Adapters.UsersPostAdapter.ViewHolder holder, final int position) {
        holder.buttonViewOption.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.buttonViewOption);
            popupMenu.inflate(R.menu.tweet_options);
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.deleteTweet) {
                    Log.i("D","deleting is called");
                    builder.setMessage("Are you sure you want to delete this Tweet").setNegativeButton(
                            "No", (dialog, which) -> dialog.cancel()
                    ).setPositiveButton("Yes", (dialog, which) -> {
                        Post post = new Post();
                        for(int i = 0; i < posts.size(); i++) {
                            if(i == holder.getAdapterPosition()) {
                                post = posts.get(i);
                                break;
                            }
                        }
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Post");
                        databaseReference.child(post.getId()).removeValue();
                        Toast.makeText(context,"Tweet Removed!",Toast.LENGTH_SHORT).show();
                        posts.clear();
                        notifyDataSetChanged();
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setTitle("Attention required!");
                    alertDialog.show();
                }
                return false;
            });
            popupMenu.show();
        });
        String a = posts.get(position).getPostID();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    assert user != null;
                    user.setUserID(userSnapshot.getKey());
                    if(user.getUserID().equals(a)) {
                        holder.setName(user.getUserName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("TAG",error.getMessage());
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
        public TextView buttonViewOption;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.usersUserName);
            text = itemView.findViewById(R.id.usersTweet);
            dateTime = itemView.findViewById(R.id.usersTimeStamp);
            buttonViewOption = itemView.findViewById(R.id.textViewOptions);
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
