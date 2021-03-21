package com.example.bodify.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
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

import com.example.bodify.ChatRoom;
import com.example.bodify.Models.Favourite;
import com.example.bodify.Models.Ingredient;
import com.example.bodify.Models.Meal;
import com.example.bodify.Models.Room;
import com.example.bodify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Room> rooms;
    private final Context context;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

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

                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.setTitle("Attention required!");
                        alertDialog.show();
                        break;
                    case R.id.deleteRoom:
                        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(context);
                        deleteBuilder.setMessage("Are you sure you want to delete this room")
                                .setNegativeButton("No", (dialog1, which) -> dialog1.cancel())
                                .setPositiveButton("Yes", (dialog1, which) -> {
                                    if(rooms.get(position).getUserID().equals(mAuth.getUid())) {
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Rooms");
                                        databaseReference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for(DataSnapshot roomSnapshot : snapshot.getChildren()) {
                                                    Room room = new Room();
                                                    room.setId(roomSnapshot.getKey());
                                                    if(room.getTheme().equals(rooms.get(position).getTheme())) {
                                                        databaseReference.child(room.getId()).removeValue();
                                                        rooms.clear();
                                                        notifyDataSetChanged();
                                                        break;
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    } else {
                                        Toast.makeText(context,"You are not admin of this room",Toast.LENGTH_SHORT).show();
                                    }
                                });
                        AlertDialog deleteDialog = deleteBuilder.create();
                        deleteDialog.setTitle("Attention required!");
                        deleteDialog.show();

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
