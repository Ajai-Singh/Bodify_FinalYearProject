package com.example.bodify.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.ChatBox;
import com.example.bodify.Models.ChatRoom;
import com.example.bodify.Models.Message;
import com.example.bodify.Models.Room;
import com.example.bodify.Models.User;
import com.example.bodify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Room> rooms;
    private final Context context;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public ChatRoomAdapter(ArrayList<Room> rooms, Context context) {
        this.rooms = rooms;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatRoomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatroom, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(@NonNull ChatRoomAdapter.ViewHolder holder, final int position) {
        DatabaseReference chatRoomReference = FirebaseDatabase.getInstance().getReference("ChatRoom").child(rooms.get(position).getTheme());
        chatRoomReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ChatRoom chatRoom = snapshot.getValue(ChatRoom.class);
                if (chatRoom != null) {
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User").child(chatRoom.getMessages()
                            .get(chatRoom.getMessages().size() - 1).getUserId());
                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                holder.setLatestMessage(user.getUserName() + ": " + chatRoom.getMessages().get(chatRoom.getMessages().size() - 1).getMessageText());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(context, "Error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    holder.setTime(chatRoom.getMessages().get(chatRoom.getMessages().size() - 1).getDateTime());
                    if(chatRoom.getUserIds().size() == 1 && chatRoom.getUserIds().get(0).equals("No users")) {
                            holder.setCount("0");
                    } else {
                        holder.setCount(String.valueOf(chatRoom.getUserIds().size()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.setTheme(rooms.get(position).getTheme());
        holder.options.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.options);
            popup.inflate(R.menu.chatroomoptions);
            popup.setOnMenuItemClickListener(item -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                switch (item.getItemId()) {
                    case R.id.enterRoom:
                        builder.setMessage("Would you like to enter chat room?")
                                .setNegativeButton("No", (dialog1, which) -> dialog1.cancel())
                                .setPositiveButton("Yes", (dialog1, which) -> {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChatRoom").child(rooms.get(position).getTheme());
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        final ArrayList<Message> messages = new ArrayList<>();
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            ChatRoom chatRoom = snapshot.getValue(ChatRoom.class);
                                            if (chatRoom == null) {
                                                ArrayList<String> userIds = new ArrayList<>();
                                                userIds.add(mAuth.getUid());
                                                Date date = new Date();
                                                String currentDateTime = dateFormat.format(date);
                                                Message message = new Message("Created group chat", mAuth.getUid(), currentDateTime);
                                                messages.add(message);
                                                ChatRoom newChatRoom = new ChatRoom(messages, rooms.get(position).getTheme(), userIds);
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ChatRoom");
                                                databaseReference.child(rooms.get(position).getTheme()).setValue(newChatRoom).addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(context, "Group Chat Created", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(context, "Error occurred: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            } else {
                                                if (!chatRoom.getUserIds().contains(mAuth.getUid())) {
                                                    DatabaseReference chatRoomReference = FirebaseDatabase.getInstance().getReference("ChatRoom").child(rooms.get(position).getTheme());
                                                    chatRoomReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            ChatRoom dbChatRoom = snapshot.getValue(ChatRoom.class);
                                                            assert dbChatRoom != null;
                                                            if(dbChatRoom.getUserIds().contains("No users"))
                                                                for(int i = 0; i < dbChatRoom.getUserIds().size(); i++) {
                                                                    if(dbChatRoom.getUserIds().get(i).equals("No users")) {
                                                                        dbChatRoom.getUserIds().remove(i);
                                                                        dbChatRoom.getUserIds().add(mAuth.getUid());
                                                                        break;
                                                                    }
                                                                }
                                                            chatRoomReference.child("userIds").setValue(dbChatRoom.getUserIds());
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                            Toast.makeText(context, "Error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(context, "Error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Intent intent = new Intent(context, ChatBox.class);
                                    intent.putExtra("theme", rooms.get(position).getTheme());
                                    context.startActivity(intent);
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.setTitle("Attention required!");
                        alertDialog.show();
                        break;
                    case R.id.leaveChat:
                        DatabaseReference userChatCheck = FirebaseDatabase.getInstance().getReference("ChatRoom").child(rooms.get(position).getTheme());
                        userChatCheck.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ChatRoom chatRoom = snapshot.getValue(ChatRoom.class);
                                if(chatRoom != null) {
                                    if (chatRoom.getUserIds().contains(mAuth.getUid())) {
                                        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(context);
                                        deleteBuilder.setMessage("Are you sure you want to leave this room")
                                                .setNegativeButton("No", (dialog1, which) -> dialog1.cancel())
                                                .setPositiveButton("Yes", (dialog1, which) -> {
                                                    for (int i = 0; i < chatRoom.getUserIds().size(); i++) {
                                                        if (chatRoom.getUserIds().get(i).equals(mAuth.getUid())) {
                                                            chatRoom.getUserIds().remove(i);
                                                            break;
                                                        }
                                                    }
                                                    if(chatRoom.getUserIds().isEmpty()) {
                                                        chatRoom.getUserIds().add("No users");
                                                    }
                                                    userChatCheck.child("userIds").setValue(chatRoom.getUserIds());
                                                    notifyDataSetChanged();
                                                });
                                        AlertDialog deleteDialog = deleteBuilder.create();
                                        deleteDialog.setTitle("Attention required!");
                                        deleteDialog.show();
                                    } else {
                                        Toast.makeText(context,"You're not a member of this chat",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(context,"Error occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    @Override
    public void onClick(View v) {
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView theme, options, count, latestMessage, time;

        public ViewHolder(@NonNull View v) {
            super(v);
            theme = v.findViewById(R.id.theme);
            options = v.findViewById(R.id.chatRoomOptions);
            count = v.findViewById(R.id.memberCount);
            latestMessage = v.findViewById(R.id.lastMessage);
            time = v.findViewById(R.id.textView52);
        }

        @SuppressLint("SetTextI18n")
        public void setTheme(String t) {
            theme.setText("Chat Theme: " + t);
        }

        @SuppressLint("SetTextI18n")
        public void setCount(String c) {
            count.setText(c + " Members");
        }

        public void setLatestMessage(String l) {
            latestMessage.setText(l);
        }

        public void setTime(String td) {
            time.setText(td);
        }
    }
}
