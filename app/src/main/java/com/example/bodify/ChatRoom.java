package com.example.bodify;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bodify.Models.Comment;
import com.example.bodify.Models.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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

public class ChatRoom extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText input;
    private FloatingActionButton fab;
    private FirebaseListAdapter<Comment> adapter;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        fab = findViewById(R.id.fab);
        input = findViewById(R.id.input);
        mAuth = FirebaseAuth.getInstance();
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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input.getText().toString().isEmpty()) {
                    Toast.makeText(ChatRoom.this, "Message is empty!", Toast.LENGTH_SHORT).show();
                } else {
                    Date date = new Date();
                    String currentDateTime = dateFormat.format(date);
                    mAuth = FirebaseAuth.getInstance();
                    final String userID = mAuth.getUid();
                    Comment comment = new Comment(input.getText().toString(), userName,userID,currentDateTime);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("Chat").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ChatRoom.this, "Message Saved", Toast.LENGTH_SHORT).show();
                                input.setText("");
                                displayMessages();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatRoom.this, "Error Occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    //I am able to write new messages to firebase realtime database
    //I need to fix this method and populate the list with the messages from the database
    public void displayMessages() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Chat");
        ListView listOfMessages = findViewById(R.id.list_of_messages);
        FirebaseListOptions<Comment> options =
                new FirebaseListOptions.Builder<Comment>()
                        .setQuery(query, Comment.class)
                        .setLayout(R.layout.message)
                        .build();
        adapter = new FirebaseListAdapter<Comment>(options){
            @Override
            protected void populateView(@NotNull View v, @NotNull Comment model, int position) {
                TextView messageText = v.findViewById(R.id.message_text);
                TextView messageUser = v.findViewById(R.id.message_user);
                TextView messageTime = v.findViewById(R.id.message_time);
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(model.getDateTime());
            }
        };
        listOfMessages.setAdapter(adapter);
    }
}