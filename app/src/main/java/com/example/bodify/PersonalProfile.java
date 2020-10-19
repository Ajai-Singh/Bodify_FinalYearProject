package com.example.bodify;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bodify.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PersonalProfile extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener{
    private FirebaseAuth mAuth;
    private EditText userName,email,height,weight;
    private Spinner activityLevelSpinner,fitnessGoalSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        userName = findViewById(R.id.userNameTextField);
        email = findViewById(R.id.emailAddressTextField);
        height = findViewById(R.id.heightTextFieldPP);
        weight = findViewById(R.id.weightTextFieldPP);
        activityLevelSpinner = findViewById(R.id.activityLevelSpinner);
        fitnessGoalSpinner = findViewById(R.id.fitnessGoalSpinner);
        mAuth = FirebaseAuth.getInstance();
        final String userID = mAuth.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 User user = snapshot.getValue(User.class);
                userName.setText(user.getUserName());
                email.setText(user.getEmail());
                int intHeight = user.getHeight();
                String strHeight = String.valueOf(intHeight);
                height.setText(strHeight);
                Double dblWeight = user.getWeight();
                String strWeight = String.valueOf(dblWeight);
                weight.setText(strWeight);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(PersonalProfile.this,R.array.activityLevels,android.R.layout.simple_spinner_dropdown_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                activityLevelSpinner.setAdapter(adapter);
                activityLevelSpinner.setOnItemSelectedListener(PersonalProfile.this);
                activityLevelSpinner.setPrompt(user.getActivityLevel());

                ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(PersonalProfile.this,R.array.fitnessGoals,android.R.layout.simple_spinner_dropdown_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                fitnessGoalSpinner.setAdapter(adapter1);
                fitnessGoalSpinner.setOnItemSelectedListener(PersonalProfile.this);
                fitnessGoalSpinner.setPrompt(user.getFitnessGoal());

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Database Access Error!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String choice = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}