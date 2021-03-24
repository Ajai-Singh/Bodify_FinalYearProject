package com.example.bodify.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bodify.BreakdownAnalysis;
import com.example.bodify.ChatBox;
//import com.example.bodify.ChatRoom;
import com.example.bodify.FoodFinder;
import com.example.bodify.Models.CR;
import com.example.bodify.Models.Favourite;
import com.example.bodify.Models.Ingredient;
import com.example.bodify.Models.Meal;
import com.example.bodify.Models.Message;
import com.example.bodify.Models.Room;
import com.example.bodify.R;
import com.example.bodify.WeightProgression;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Room> rooms;
    private final Context context;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    public ChatRoomAdapter(ArrayList<Room> rooms,Context context) {
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
        holder.theme.setText(rooms.get(position).getTheme());
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
                                        ArrayList<Message> messages = new ArrayList<>();
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                CR cr = snapshot.getValue(CR.class);
                                                //increase the number count by 1 and add the user id to the arraylist in the constructor if the user is not in the chat already!
                                                if(cr == null) {
                                                    //if the chat room is empty create it!
                                                    ArrayList<String> userIds = new ArrayList<>();
                                                    userIds.add(mAuth.getUid());

                                                    Date date = new Date();
                                                    String currentDateTime = dateFormat.format(date);
                                                    Message message = new Message("Group chat created","",mAuth.getUid(),currentDateTime);
                                                    messages.add(message);
                                                    CR newChatRoom = new CR(messages,rooms.get(position).getTheme(),userIds);
                                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("ChatRoom");
                                                    databaseReference.child(rooms.get(position).getTheme()).setValue(newChatRoom).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()) {
                                                                Toast.makeText(context,"Group Chat Created", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                Toast.makeText(context,"Error occurred: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    //the chat room is not empty so we need to append to its current details.
                                                    //check if the current user logged in is a member or not
                                                    if(!cr.getUserIds().contains(mAuth.getUid())) {
                                                        DatabaseReference chatRoomReference = FirebaseDatabase.getInstance().getReference("ChatRoom").child(rooms.get(position).getTheme());
                                                        chatRoomReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                CR dbCR = snapshot.getValue(CR.class);
                                                                //adding the logged in user to the list of the userids
                                                                dbCR.getUserIds().add(mAuth.getUid());
                                                                //recreate the db object
                                                                //updating the chat makes it so I do not need to override the db everytime with  new object
                                                                chatRoomReference.child("userIds").setValue(dbCR.getUserIds());
                                                                //chatRoomReference.child("messages").setValue(dbCR.getMessages());
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                Toast.makeText(context,"Error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

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
                        //Removed delete functionality for now
//                    case R.id.deleteRoom:
//                        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(context);
//                        deleteBuilder.setMessage("Are you sure you want to delete this room")
//                                .setNegativeButton("No", (dialog1, which) -> dialog1.cancel())
//                                .setPositiveButton("Yes", (dialog1, which) -> {
//                                    if(rooms.get(position).getUserID().equals(mAuth.getUid())) {
//                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Rooms");
//                                        databaseReference.addValueEventListener(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                for(DataSnapshot roomSnapshot : snapshot.getChildren()) {
//                                                    Room room = roomSnapshot.getValue(Room.class);
//                                                    assert room != null;
//                                                    room.setId(roomSnapshot.getKey());
//                                                    if(room.getTheme().equals(rooms.get(position).getTheme()) && room.getUserID().equals(rooms.get(position).getUserID())) {
//                                                        databaseReference.child(room.getId()).removeValue();
//                                                        rooms.clear();
//                                                        notifyDataSetChanged();
//                                                        Toast.makeText(context,"Chat Room deleted!",Toast.LENGTH_SHORT).show();
//                                                        break;
//                                                    }
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError error) {
//
//                                            }
//                                        });
//                                    } else {
//                                        Toast.makeText(context,"You are not admin of this room",Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                        AlertDialog deleteDialog = deleteBuilder.create();
//                        deleteDialog.setTitle("Attention required!");
//                        deleteDialog.show();
//                        break;
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
        private final TextView theme,options;

        public ViewHolder(@NonNull View v) {
            super(v);
            theme = v.findViewById(R.id.theme);
            options = v.findViewById(R.id.chatRoomOptions);
        }

        public void setTheme(String t) {
            theme.setText(t);
        }

//        public void setAmount(double a) {
//            amount.setText(String.valueOf(a));
//        }
//
//        public void setUnit(String u) {
//            unit.setText(u);
//        }
    }
}
