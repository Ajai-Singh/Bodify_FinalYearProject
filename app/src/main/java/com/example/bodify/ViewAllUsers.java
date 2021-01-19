package com.example.bodify;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.Adapters.ViewAllUsersAdapter;
import com.example.bodify.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ViewAllUsers extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ViewAllUsersAdapter viewAllUsers;
    private final ArrayList<User> users = new ArrayList<>();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser firebaseUser = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_users);
        Objects.requireNonNull(getSupportActionBar()).setTitle("App Users");
        EditText userName = findViewById(R.id.userNameSearch);
        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        getAllUsers();
    }

    private void filter(String text) {
        ArrayList<User> filteredList = new ArrayList<>();
        for(User user : users) {
            if (user.getUserName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(user);
            }
        }
        viewAllUsers.filteredList(filteredList);
    }

    public void getAllUsers() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    assert user != null;
                    if (!user.getEmail().equals(firebaseUser.getEmail())) {
                        users.add(user);
                    }
                }
                recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(ViewAllUsers.this));
                viewAllUsers = new ViewAllUsersAdapter(users);
                new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
                recyclerView.setAdapter(viewAllUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewAllUsers.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    ItemTouchHelper.SimpleCallback itemTouch = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN |
            ItemTouchHelper.START | ItemTouchHelper.END,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            Collections.swap(users, viewHolder.getAdapterPosition(), target.getAdapterPosition());
            viewAllUsers.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return false;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(ViewAllUsers.this, R.color.grey))
                    .addActionIcon(R.drawable.email)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            Intent intent = new Intent(getApplicationContext(), SpecificUsersPage.class);
            intent.putExtra("position", users.get(viewHolder.getAdapterPosition()).getEmail());
            startActivity(intent);
            finish();
        }
    };
}
