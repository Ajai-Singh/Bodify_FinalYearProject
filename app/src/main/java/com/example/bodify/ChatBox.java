package com.example.bodify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bodify.Models.ChatRoom;
import com.example.bodify.Models.Message;
import com.example.bodify.Models.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatBox extends AppCompatActivity {
    private String theme;
    private EditText editText;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private FirebaseListAdapter<Message> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbox);
        Intent intent = getIntent();
        theme = intent.getExtras().getString("theme");
        Objects.requireNonNull(getSupportActionBar()).setTitle("Chat Room: " + theme);
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
        Query query = FirebaseDatabase.getInstance().getReference().child("ChatRoom").child(theme).child("messages");
        ListView listOfMessages;
        listOfMessages = findViewById(R.id.recyclerView2);
        FirebaseListOptions<Message> options =
                new FirebaseListOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .setLayout(R.layout.message)
                        .build();
        adapter = new FirebaseListAdapter<Message>(options) {
            @Override
            protected void populateView(@org.jetbrains.annotations.NotNull @NotNull View v, @org.jetbrains.annotations.NotNull @NotNull Message model, int position) {
                TextView messageText = v.findViewById(R.id.groupChatMessage);
                TextView messageTime = v.findViewById(R.id.messageStamp);
                CircleImageView circleImageView = v.findViewById(R.id.cardViewProfilePicture);
                TextView messageUser = v.findViewById(R.id.userNameCardView);
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User");
                    userReference.addValueEventListener(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                User user = userSnapshot.getValue(User.class);
                                assert user != null;
                                user.setUserID(userSnapshot.getKey());
                                if (user.getUserID().equals(mAuth.getUid())) {
                                    messageUser.setText("User: " + user.getUserName());
                                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                    storageReference.child(user.getmImageUrl()).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        circleImageView.setImageBitmap(bitmap);
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(ChatBox.this, "Error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                messageText.setText(model.getMessageText());
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

    public void createMessage(String strMessage) {
        Date date = new Date();
        Message message = new Message(strMessage, mAuth.getUid(), dateFormat.format(date));
        DatabaseReference chatRoomReference = FirebaseDatabase.getInstance().getReference("ChatRoom").child(theme);
        chatRoomReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ChatRoom chatRoom = snapshot.getValue(ChatRoom.class);
                if (chatRoom != null) {
                    chatRoom.getMessages().add(message);
                    chatRoomReference.child("messages").setValue(chatRoom.getMessages()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            editText.setText("");
                        } else {
                            Toast.makeText(ChatBox.this, "Error occurred: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
       finish();
       startActivity(new Intent(ChatBox.this, ChatRooms.class));
    }
}