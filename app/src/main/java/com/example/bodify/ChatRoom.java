package com.example.bodify;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bodify.Models.Message;
import com.example.bodify.Models.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ChatRoom extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private EditText input;
    private FloatingActionButton fab;
    private FirebaseListAdapter<Message> adapter;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Chat Room");
        fab = findViewById(R.id.fab);
        input = findViewById(R.id.input);
        final String userID = mAuth.getUid();
        assert userID != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                String userName = user.getUserName();
                createMessage(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatRoom.this, "Message is empty!", Toast.LENGTH_SHORT).show();
            }
        });
        displayMessages();
    }

    public void createMessage(final String userName) {
        fab.setOnClickListener(view -> {
            if (input.getText().toString().isEmpty()) {
                Toast.makeText(ChatRoom.this, "Message is empty!", Toast.LENGTH_SHORT).show();
            } else {
                Date date = new Date();
                String currentDateTime = dateFormat.format(date);
                mAuth = FirebaseAuth.getInstance();
                final String userID = mAuth.getUid();
                Message comment = new Message(input.getText().toString(), userName, userID, currentDateTime);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("Chat").push().setValue(comment).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        input.setText("");
                    } else {
                        Toast.makeText(ChatRoom.this, "Error occurred: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(ChatRoom.this, "Error Occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    public void displayMessages() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Chat");
        ListView listOfMessages = findViewById(R.id.list_of_messages);
        FirebaseListOptions<Message> options =
                new FirebaseListOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .setLayout(R.layout.message)
                        .build();
        adapter = new FirebaseListAdapter<Message>(options) {
            @Override
            protected void populateView(@NotNull View v, @NotNull Message model, int position) {
                TextView messageText = v.findViewById(R.id.message_text);
                TextView messageUser = v.findViewById(R.id.message_user);
                TextView messageTime = v.findViewById(R.id.message_time);
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(model.getDateTime());
            }
        };
        listOfMessages.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}