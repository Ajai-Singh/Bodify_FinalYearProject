package com.example.bodify;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.bodify.FirebaseAuthentication.LogIn;
import com.example.bodify.Models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class DashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private FirebaseAuth mAuth;
    private ImageView profileImageView;
    private TextView welcome;
    private StorageReference storageReference;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        drawer = findViewById(R.id.drawer_layout);
//        NavigationView navigationView = findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //profileImageView = findViewById(R.id.personalProfile);
        mAuth = FirebaseAuth.getInstance();
        //showProfilePicture();
        displayMessage();
        Button profile = findViewById(R.id.buttonProfile);
        Button gymLocations = findViewById(R.id.gymFinderButton);
        Button health = findViewById(R.id.healthButton);
        Button pedometer = findViewById(R.id.buttonPedometer);
        Button users = findViewById(R.id.buttonUsers);
        //welcome = findViewById(R.id.welcomeUser);
        Button chat = findViewById(R.id.buttonChat);
        Button recipes = findViewById(R.id.buttonRecipes);
        Button diary = findViewById(R.id.buttonDiary);
       // Objects.requireNonNull(getSupportActionBar()).setTitle("Welcome To Bodify");
        final String userID = mAuth.getUid();
        assert userID != null;
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userID);
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @SuppressLint("SetTextI18n")
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User user = snapshot.getValue(User.class);
//                if (user != null) {
//                    welcome.setText("User Logged in: ");
//                    welcome.append(user.getUserName());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(DashBoard.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });


        gymLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GymsNearMe.class));
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PersonalProfile.class));
            }
        });
        health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoard.this, Health.class));
            }
        });
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashBoard.this,ChatRoom.class));
            }
        });
        pedometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoard.this,Pedometer.class));
            }
        });
        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoard.this,ViewAllUsers.class));
            }
        });
        recipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashBoard.this, GenerateRecipes.class));
            }
        });
//        diary.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(DashBoard.this,Management.class));
//            }
//        });
    }

    public void displayMessage() {
        ArrayList<String> motivatingMessages = new ArrayList<>();
        Random random = new Random();
        motivatingMessages.add("YOU’RE THE ONLY ONE WHO CAN MAKE THE DIFFERENCE. WHATEVER YOUR DREAM IS, GO FOR IT. – MAGIC JOHNSON.");
        motivatingMessages.add("EXERCISE SHOULD BE REGARDED AS TRIBUTE TO THE HEART. – GENE TUNNEY.");
        motivatingMessages.add("EXERCISE IS KING. NUTRITION IS QUEEN. PUT THEM TOGETHER AND YOU’VE GOT A KINGDOM. – JACK LALANNE.");
        motivatingMessages.add("ONE MAN PRACTICING SPORTSMANSHIP IS FAR BETTER THAN 50 PREACHING IT.- KNUTE ROCKNE.");
        motivatingMessages.add("THE PAIN YOU FEEL TODAY WILL BE THE STRENGTH YOU FEEL TOMORROW. – RITU GHATOUREY.");
        motivatingMessages.add("IF YOU DON’T PRACTISE YOU DON’T DESERVE TO WIN. – ANDRE AGASSI.");
        motivatingMessages.add("UNLESS YOU PUKE, FAINT, OR DIE, KEEP GOING. – JILLIAN MICHAELS.");
        motivatingMessages.add("I HATE EVERY MINUTE OF TRAINING. BUT I SAID, DON’T QUIT. SUFFER NOW AND LIVE THE REST OF YOUR LIFE AS A CHAMPION. – MOHAMMAD ALI.");
        motivatingMessages.add("IF YOU EVER LACK THE MOTIVATION TO TRAIN THEN THINK WHAT HAPPENS TO YOUR MIND & BODY WHEN YOU DON’T. – SHIFU YAN LEI.");
        motivatingMessages.add("THE BEST ABS EXERCISE IS 5 SETS OF STOP EATING SO MUCH CRAP. – LAZAR ANGELOV.");
        motivatingMessages.add("TODAY I WILL DO WHAT OTHERS WON’T, SO TOMORROW I CAN ACCOMPLISH WHAT OTHERS CAN’T. – JERRY RICE.");
        motivatingMessages.add("BLOOD, SWEAT AND RESPECT. FIRST TWO YOU GIVE. LAST ONE YOU EARN. – DWAYNE “THE ROCK” JOHNSON.");
        motivatingMessages.add("I’VE ALWAYS BELIEVED THAT IF YOU PUT IN THE WORK, THE RESULTS WILL COME. – MICHAEL JORDAN.");
        motivatingMessages.add("I THINK IN LIFE YOU SHOULD WORK ON YOURSELF UNTIL THE DAY YOU DIE. – SERENA WILLIAMS.");
        motivatingMessages.add("PRACTISE PUTS BRAINS IN YOUR MUSCLES. – SAM SNEAD.");
        motivatingMessages.add("EVERYTHING IS POSSIBLE AS LONG AS YOU PUT YOUR MIND TO IT AND YOU PUT THE WORK AND TIME INTO IT. – MICHAEL PHELPS.");
        motivatingMessages.add("IF YOU TURN UP WORRYING ABOUT HOW YOU’RE GOING TO PERFORM, YOU’VE ALREADY LOST. TRAIN HARD, TURN UP, RUN YOUR BEST AND THE REST WILL TAKE CARE OF ITSELF. – USAIN BOLT.");
        motivatingMessages.add("I STILL LOOK AT MYSELF AND WANT TO IMPROVE. – DAVID BECKHAM.");
        motivatingMessages.add("I TRAIN TO BE THE BEST IN THE WORLD ON MY WORST DAY. – RONDA ROUSEY.");
        motivatingMessages.add("I ALWAYS TRIED SO HARD TO FIT IN, AND THEN I FIGURED OUT THAT I DIDN’T WANT TO FIT. – ANTHONY DAVIS.");
        motivatingMessages.add("REAL PURPOSE OF RUNNING ISN’T TO WIN A RACE. IT’S TO TEST THE LIMITS OF THE HUMAN HEART.- BILL BOWERMAN");
        motivatingMessages.add("I AM ALWAYS IN THE GYM SPARRING. I LOOK AT EVERY FIGHT KIND OF LIKE A SPARRING MATCH. – CHRIS WEIDMAN.");
        motivatingMessages.add("MY JOB IS TO BE FIT AND I’M REALLY BLESSED THAT I GET TO GO AND WORK OUT AND LIVE A REALLY HEALTHY LIFE STYLE.- KERRI WALSH.");
        motivatingMessages.add("I DIDN’T HAVE THE SAME FITNESS OR ABILITY AS OTHER GIRLS, SO I HAD TO BEAT THEM WITH MY MIND. – MARTINA HINGIS.");
        motivatingMessages.add("EXERCISE IS DONE AGAINST ONE’S WISHES AND MAINTAINED ONLY BECAUSE THE ALTERNATIVE IS WORSE. – GEORGE A. SHEEHAN.");
        motivatingMessages.add("IT’S BEEN A LONG ROAD TO HEALTH AND FITNESS FOR ME. I AM JUST GLAD TO HAVE BEEN GIVEN THE OPPORTUNITY TO DO WHAT I LOVE MOST.- JONAH LOMU.");

        int index = random.nextInt(motivatingMessages.size());
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(DashBoard.this);
        dlgAlert.setMessage(motivatingMessages.get(index));
        dlgAlert.setTitle("Welcome To Bodify");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
    }

//    public void showProfilePicture() {
//        String userID = mAuth.getUid();
//        assert userID != null;
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userID);
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                User user = snapshot.getValue(User.class);
//                if (user != null) {
//                    storageReference.child(user.getmImageUrl()).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                        @Override
//                        public void onSuccess(byte[] bytes) {
//                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                            profileImageView.setImageBitmap(bitmap);
//                        }
//                    });
//                }
//            }

//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(DashBoard.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logOut) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getApplicationContext(),"Successfully Logged Out",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), LogIn.class));
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}