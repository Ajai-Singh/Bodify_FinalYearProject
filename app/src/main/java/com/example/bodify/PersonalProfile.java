package com.example.bodify;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bodify.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class PersonalProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private FirebaseAuth mAuth;
    private TextView userName,email;
    private EditText height,weight;
    private Spinner activityLevelSpinner,fitnessGoalSpinner,bodyType,preferredMacroNutrient;
    private final ArrayList<String> activityLevels = new ArrayList<>();
    private final ArrayList<String> fitnessGoals = new ArrayList<>();
    private final ArrayList<String> bodyTypes = new ArrayList<>();
    private final ArrayList<String> preferredMacroNutrients = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);
        userName = findViewById(R.id.userNameTextField);
        email = findViewById(R.id.emailAddressTextField);
        height = findViewById(R.id.heightTextFieldPP);
        weight = findViewById(R.id.weightTextFieldPP);
        Button updateProfile = findViewById(R.id.updateInformationButton);
        activityLevelSpinner = findViewById(R.id.activityLevelSpinner);
        fitnessGoalSpinner = findViewById(R.id.fitnessGoalSpinner);
        bodyType = findViewById(R.id.bodyCompositionSpinner);
        preferredMacroNutrient = findViewById(R.id.preferredMacroNutrientSpinner);
        updateSpinners();
        mAuth = FirebaseAuth.getInstance();
        final String userID = mAuth.getUid();
        assert userID != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    int activityLevelIndex;
                    int fitnessGoalIndex;
                    int bodyTypeIndex;
                    int preferredMacroNutrientIndex;
                    userName.setText(user.getUserName());
                    email.setText(user.getEmail());
                    height.setText(String.valueOf(user.getHeight()));
                    weight.setText(String.valueOf(user.getWeight()));
                    for (int i = 0; i < activityLevels.size(); i++) {
                        if (activityLevels.get(i).equalsIgnoreCase(user.getActivityLevel())) {
                            activityLevelIndex = i;
                            activityLevelSpinner.setSelection(activityLevelIndex);
                        }
                    }
                    for (int i = 0; i < fitnessGoals.size(); i++) {
                        if (fitnessGoals.get(i).equalsIgnoreCase(user.getFitnessGoal())) {
                            fitnessGoalIndex = i;
                            fitnessGoalSpinner.setSelection(fitnessGoalIndex);
                        }
                    }
                    for(int i = 0; i < bodyTypes.size(); i++){
                        if(bodyTypes.get(i).equalsIgnoreCase(user.getBodyType())) {
                            bodyTypeIndex = i;
                            bodyType.setSelection(bodyTypeIndex);
                        }
                    }
                    for(int i = 0; i < preferredMacroNutrients.size(); i++){
                        if(preferredMacroNutrients.get(i).equalsIgnoreCase(user.getBodyType())) {
                            preferredMacroNutrientIndex = i;
                            preferredMacroNutrient.setSelection(preferredMacroNutrientIndex);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Database Access Error!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String strInputtedHeight = height.getText().toString().trim();
            String strInputtedWeight = weight.getText().toString().trim();
            String strFitnessGoalSpinner = fitnessGoalSpinner.getSelectedItem().toString();
            String strActivityGoalSpinner = activityLevelSpinner.getSelectedItem().toString();
            String strBodyType = bodyType.getSelectedItem().toString();
            String strPreferredMacroNutrient = preferredMacroNutrient.getSelectedItem().toString();
            if(TextUtils.isEmpty(strInputtedHeight)){
                height.setError("Height in CMs is required!");
                height.requestFocus();
            }else if(TextUtils.isEmpty(strInputtedWeight)){
                weight.setError("Weight in KGs is required!");
                weight.requestFocus();
            }else{
                Double dblHeight = Double.parseDouble(strInputtedHeight);
                Double dblWeight = Double.parseDouble(strInputtedWeight);
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                assert firebaseUser != null;
                String userID = firebaseUser.getUid();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userID);
                //Note update BMI TO DO LIST
                //databaseReference.child("bmi").setValue(bmi);
                databaseReference.child("weight").setValue(dblWeight);
                databaseReference.child("height").setValue(dblHeight);
                databaseReference.child("activityLevel").setValue(strActivityGoalSpinner);
                databaseReference.child("fitnessGoal").setValue(strFitnessGoalSpinner);
                databaseReference.child("preferredMacroNutrient").setValue(strPreferredMacroNutrient);
                databaseReference.child("bodyType").setValue(strBodyType);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int activityLevelIndex;
                        int fitnessGoalIndex;
                        int bodyTypeIndex;
                        int preferredMacroNutrientIndex;
                        User user = snapshot.getValue(User.class);
                        if(user != null) {
                            height.setText(String.valueOf(user.getHeight()));
                            weight.setText(String.valueOf(user.getWeight()));

                            for(int i = 0; i < activityLevels.size(); i++){
                                if(activityLevels.get(i).equalsIgnoreCase(user.getActivityLevel())) {
                                 activityLevelIndex = i;
                                 activityLevelSpinner.setSelection(activityLevelIndex);
                                }
                            }
                            for(int i = 0; i < fitnessGoals.size(); i++){
                                if(fitnessGoals.get(i).equalsIgnoreCase(user.getFitnessGoal())) {
                                    fitnessGoalIndex = i;
                                    fitnessGoalSpinner.setSelection(fitnessGoalIndex);
                                }
                            }
                            for(int i = 0; i < bodyTypes.size(); i++){
                                if(bodyTypes.get(i).equalsIgnoreCase(user.getBodyType())) {
                                    bodyTypeIndex = i;
                                    bodyType.setSelection(bodyTypeIndex);
                                }
                            }
                            for(int i = 0; i < preferredMacroNutrients.size(); i++){
                                if(preferredMacroNutrients.get(i).equalsIgnoreCase(user.getBodyType())) {
                                    preferredMacroNutrientIndex = i;
                                    preferredMacroNutrient.setSelection(preferredMacroNutrientIndex);
                                }
                            }
                            Toast.makeText(PersonalProfile.this,"Update Success",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PersonalProfile.this,"Error Occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    });
}

    public void updateSpinners() {
        activityLevels.add("1");
        activityLevels.add("2");
        activityLevels.add("3");
        fitnessGoals.add("Lose Weight");
        fitnessGoals.add("Maintain Weight");
        fitnessGoals.add("Gain Weight");
        bodyTypes.add("Excess body fat");
        bodyTypes.add("Average Shape");
        bodyTypes.add("Good Shape");
        preferredMacroNutrients.add("Fats");
        preferredMacroNutrients.add("Carbohydrates");
        preferredMacroNutrients.add("Don't have a preference");

        ArrayAdapter<String> adapterActivityLevels = new ArrayAdapter<>(PersonalProfile.this,android.R.layout.simple_spinner_dropdown_item,activityLevels);
        adapterActivityLevels.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityLevelSpinner.setAdapter(adapterActivityLevels);
        activityLevelSpinner.setOnItemSelectedListener(PersonalProfile.this);

        ArrayAdapter<String> adapterFitnessGoal = new ArrayAdapter<>(PersonalProfile.this,android.R.layout.simple_spinner_dropdown_item,fitnessGoals);
        adapterFitnessGoal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fitnessGoalSpinner.setAdapter(adapterFitnessGoal);
        fitnessGoalSpinner.setOnItemSelectedListener(PersonalProfile.this);

        ArrayAdapter<String> adapterBodyTypes = new ArrayAdapter<>(PersonalProfile.this,android.R.layout.simple_spinner_dropdown_item,bodyTypes);
        adapterFitnessGoal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bodyType.setAdapter(adapterBodyTypes);
        bodyType.setOnItemSelectedListener(PersonalProfile.this);

        ArrayAdapter<String> adapterPreferredMacroNutrient = new ArrayAdapter<>(PersonalProfile.this,android.R.layout.simple_spinner_dropdown_item,preferredMacroNutrients);
        adapterPreferredMacroNutrient.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        preferredMacroNutrient.setAdapter(adapterPreferredMacroNutrient);
        preferredMacroNutrient.setOnItemSelectedListener(PersonalProfile.this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}