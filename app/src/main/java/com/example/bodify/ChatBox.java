package com.example.bodify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bodify.Adapters.FavouriteAdapter;
import com.example.bodify.Adapters.MessageAdapter;
import com.example.bodify.Models.CR;
import com.example.bodify.Models.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

public class ChatBox extends AppCompatActivity {
    private String theme;
    private final ArrayList<Message> messages = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private RecyclerView recyclerView;
    private EditText editText;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbox);
        Intent intent = getIntent();
        theme = intent.getExtras().getString("theme");
        Objects.requireNonNull(getSupportActionBar()).setTitle("Chat Room: " + theme);
        recyclerView = findViewById(R.id.recyclerView2);
        Button floatingActionButton = findViewById(R.id.button2);
        editText = findViewById(R.id.editTextTextPersonName);
        showAllMessages();
        floatingActionButton.setOnClickListener(v -> {
            if (editText.getText().toString().isEmpty()) {
                Toast.makeText(ChatBox.this, "Message is empty!", Toast.LENGTH_SHORT).show();
            } else {
                createMessage(editText.getText().toString());
            }
        });
    }

    public void showAllMessages() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChatRoom").child(theme);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CR cr = snapshot.getValue(CR.class);
                if (cr != null) {
                    Log.i("Messages","" + cr.getMessages().toString());
                    messages.addAll(cr.getMessages());
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ChatBox.this));
                    messageAdapter = new MessageAdapter(messages);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatBox.this,"Error occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createMessage(String strMessage) {
        Date date = new Date();
        Message message = new Message(strMessage,"",mAuth.getUid(),dateFormat.format(date));
        DatabaseReference chatRoomReference = FirebaseDatabase.getInstance().getReference("ChatRoom").child(theme);
        chatRoomReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CR cr = snapshot.getValue(CR.class);
                if (cr != null) {
                    cr.getMessages().add(message);
                    chatRoomReference.child("messages").setValue(cr.getMessages()).addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Toast.makeText(ChatBox.this, "Error occurred: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            editText.setText("");
                            messageAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatBox.this, "Error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}