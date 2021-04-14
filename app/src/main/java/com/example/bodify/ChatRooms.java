package com.example.bodify;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bodify.Adapters.ChatRoomAdapter;
import com.example.bodify.Models.Room;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class ChatRooms extends AppCompatActivity {
    private RecyclerView recyclerView;
    private String spinnerSelection;
    private ChatRoomAdapter chatRoomAdapter;
    private final ArrayList<Room> rooms = new ArrayList<>();
    private ConstraintLayout constraintLayout;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatrooms);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ImageView imageView = findViewById(R.id.createChatRoom);
        recyclerView = findViewById(R.id.chatRoomRCV);
        constraintLayout = findViewById(R.id.test);
        showAllRooms();
        imageView.setOnClickListener(v -> {
            DatabaseReference roomReference = FirebaseDatabase.getInstance().getReference("Rooms");
            roomReference.addValueEventListener(new ValueEventListener() {
                final ArrayList<String> roomNames = new ArrayList<>();

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                        Room room = roomSnapshot.getValue(Room.class);
                        if (room != null) {
                            roomNames.add(room.getTheme());
                        }
                    }
                    createChatRoom(roomNames);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    errorOccurred(error.getMessage());
                }
            });
        });
    }

    public void showAllRooms() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Rooms");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                    Room room = roomSnapshot.getValue(Room.class);
                    rooms.add(room);
                }
                if (!rooms.isEmpty()) {
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ChatRooms.this));
                    chatRoomAdapter = new ChatRoomAdapter(rooms, ChatRooms.this, constraintLayout);
                    recyclerView.setAdapter(chatRoomAdapter);
                } else {
                    Snackbar snackbar = Snackbar.make(constraintLayout, "Sorry no Chats! ", Snackbar.LENGTH_LONG)
                            .setAction("Create Chat?", view -> {
                                DatabaseReference roomReference = FirebaseDatabase.getInstance().getReference("Rooms");
                                roomReference.addValueEventListener(new ValueEventListener() {
                                    final ArrayList<String> roomNames = new ArrayList<>();

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                        for (DataSnapshot roomSnapshot : snapshot1.getChildren()) {
                                            Room room = roomSnapshot.getValue(Room.class);
                                            if (room != null) {
                                                roomNames.add(room.getTheme());
                                            }
                                        }
                                        createChatRoom(roomNames);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        errorOccurred(error.getMessage());
                                    }
                                });
                            });
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorOccurred(error.getMessage());
            }
        });
    }

    public void createChatRoom(ArrayList<String> roomNames) {
        ArrayList<String> themes = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatRooms.this);
        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.chatroomdialogue, null);
        Spinner spinner = view.findViewById(R.id.chatRoomTheme);
        themes.add("Select theme");
        themes.add("Lose weight");
        themes.add("Maintain weight");
        themes.add("Gain weight");
        themes.add("Macro-nutrients");
        themes.add("Other");
        ArrayAdapter servingAdapter = new ArrayAdapter(ChatRooms.this, android.R.layout.simple_spinner_dropdown_item, themes) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textview = (TextView) view;
                if (position == 0) {
                    textview.setTextColor(Color.GRAY);
                } else {
                    textview.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinner.setAdapter(servingAdapter);
        servingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerSelection = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        builder.setPositiveButton("Create", (dialog, which) -> {
        });
        builder.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
            if (spinner.getSelectedItemPosition() == 0) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ChatRooms.this);
                dlgAlert.setMessage("Select chat theme!");
                dlgAlert.setTitle("Error...");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            } else if (roomNames.contains(spinner.getSelectedItem().toString())) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ChatRooms.this);
                dlgAlert.setMessage("Error similar room exists!");
                dlgAlert.setTitle("Error...");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            } else {
                dialog.dismiss();
                Room newRoom = new Room(spinnerSelection, mAuth.getUid());
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Rooms");
                databaseReference.push().setValue(newRoom).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference("Rooms");
                        roomRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                rooms.clear();
                                for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                                    Room room = roomSnapshot.getValue(Room.class);
                                    rooms.add(room);
                                }
                                if (!rooms.isEmpty()) {
                                    recyclerView.setHasFixedSize(true);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(ChatRooms.this));
                                    chatRoomAdapter = new ChatRoomAdapter(rooms, ChatRooms.this, constraintLayout);
                                    recyclerView.setAdapter(chatRoomAdapter);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                errorOccurred(error.getMessage());
                            }
                        });
                        Snackbar snackbar = Snackbar.make(constraintLayout, "Chat room created!", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    } else {
                        errorOccurred(Objects.requireNonNull(task.getException()).getMessage());
                    }
                });
            }
        });
    }

    public void errorOccurred(String errorMessage) {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Error occurred: " + errorMessage, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(ChatRooms.this, Management.class));
    }
}