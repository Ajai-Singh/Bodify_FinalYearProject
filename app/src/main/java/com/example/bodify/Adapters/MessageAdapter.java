package com.example.bodify.Adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.Models.Ingredient;
import com.example.bodify.Models.Message;
import com.example.bodify.Models.User;
import com.example.bodify.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Message> messages;

    public MessageAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, final int position) {
        holder.setDateTime(messages.get(position).getDateTime());
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User").child(messages.get(position).getUserId());
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                holder.setImage(user.getmImageUrl());
                holder.setName(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.setMessage(messages.get(position).getMessageText());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public void onClick(View v) { }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CircleImageView circleImageView;
        private final TextView message,dateTime,name;
        public ViewHolder(@NonNull View v) {
            super(v);
            name = v.findViewById(R.id.userNameCardView);
            circleImageView = v.findViewById(R.id.cardViewProfilePicture);
            message = v.findViewById(R.id.groupChatMessage);
            dateTime = v.findViewById(R.id.messageStamp);
        }

        public void setDateTime(String d) {
            dateTime.setText(d);
        }

        public void setMessage(String m) {
            message.setText(m);
        }

        public void setImage(String i) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            storageReference.child(i).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                circleImageView.setImageBitmap(bitmap);
            });
        }

        @SuppressLint("SetTextI18n")
        public void setName(String n) {
            name.setText("User: " + n);
        }
    }
}
