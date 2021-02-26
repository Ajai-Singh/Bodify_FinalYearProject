package com.example.bodify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.example.bodify.FirebaseAuthentication.LogIn;
import com.example.bodify.Models.Macro;
import com.example.bodify.Models.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import de.hdodenhof.circleimageview.CircleImageView;

public class Management extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private StorageReference storageReference;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();

    @SuppressLint({"WrongConstant", "NonConstantResourceId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_management);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.a);
        NavigationView navigationView = findViewById(R.id.nav_view);
        DatabaseReference macroRef = FirebaseDatabase.getInstance().getReference("Macros").child(userID);
        macroRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Macro macro = snapshot.getValue(Macro.class);
                if(macro == null) {
                    startService(new Intent(Management.this, HealthService.class));
                } else {
                    stopService(new Intent(Management.this,HealthService.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.personalProfile:
                    startActivity(new Intent(Management.this, PersonalProfile.class));
                    break;
                case R.id.gymFinder:
                    startActivity(new Intent(Management.this, GymsNearMe.class));
                    break;
                case R.id.dataAnalysis:
                    startActivity(new Intent(Management.this, BreakdownAnalysis.class));
                    break;
                case R.id.healthPage:
                    startActivity(new Intent(Management.this, Health.class));
                    break;
                case R.id.chat:
                    startActivity(new Intent(Management.this, ChatRoom.class));
                    break;
                case R.id.communityWall:
                    startActivity(new Intent(Management.this, CommunityWall.class));
                    break;
                case R.id.users:
                    startActivity(new Intent(Management.this, ViewAllUsers.class));
                    break;
                case R.id.foodFinder:
                    startActivity(new Intent(Management.this, FoodFinder.class));
                    break;
                case R.id.settings:
                    startActivity(new Intent(Management.this, Settings.class));
                    break;
                case R.id.logOut:
                    finish();
                    startActivity(new Intent(Management.this, LogIn.class));
                    break;
            }
            drawerLayout.closeDrawer(Gravity.START);
            return true;
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        BottomNavigationView bottomNavigationView = findViewById(R.id.topNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragLayout,new Meals()).commit();
        final String userID = mAuth.getUid();
        assert userID != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    NavigationView navigationView = findViewById(R.id.nav_view);
                    View headerView = navigationView.getHeaderView(0);
                    TextView navUsername = headerView.findViewById(R.id.navigationDrawerName);
                    navUsername.setText(user.getUserName());
                    final CircleImageView navProfilePicture = headerView.findViewById(R.id.navPicture);
                    storageReference.child(user.getmImageUrl()).getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        navProfilePicture.setImageBitmap(bitmap);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Management.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item -> {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.home:
                fragment = new Meals();
                break;
            case R.id.fav:
                fragment = new Favourites();
                break;
            case R.id.breakdown:
                fragment = new Breakdown();
                break;
            case R.id.foodRecommended:
                fragment = new FoodSuggester();
                break;
        }
        assert fragment != null;
        getSupportFragmentManager().beginTransaction().replace(R.id.fragLayout, fragment).commit();
        return true;
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}