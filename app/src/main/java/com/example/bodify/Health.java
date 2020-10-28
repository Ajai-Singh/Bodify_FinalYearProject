package com.example.bodify;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bodify.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class Health extends AppCompatActivity {
    private TextView bmi, weight, height, fitnessGoal;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);
        bmi = findViewById(R.id.bmiTextField);
        weight = findViewById(R.id.currentWeight);
        height = findViewById(R.id.currentHeight);
        fitnessGoal = findViewById(R.id.currentFitnessGoal);
        updateFields();
    }

    public void updateFields() {
        mAuth = FirebaseAuth.getInstance();
        final String userID = mAuth.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user != null) {
                    weight.setText(String.valueOf(user.getWeight()));
                    height.setText(String.valueOf(user.getHeight()));
                    fitnessGoal.setText(user.getFitnessGoal());
                    Double heightInMetres = user.getHeight()/100.00;
                    Double bodyMassIndex = user.getWeight() /Math.pow(heightInMetres,2.0);
                    DecimalFormat decimalFormat = new DecimalFormat("##.00");
                    Double formattedBodyMassIndex = Double.parseDouble(decimalFormat.format(bodyMassIndex));
                    bmi.setText(String.valueOf(formattedBodyMassIndex));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}