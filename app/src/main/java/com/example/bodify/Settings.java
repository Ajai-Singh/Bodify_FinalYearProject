package com.example.bodify;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.bodify.Models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class Settings extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView email;
    private EditText userName, height, weight;
    private Spinner activityLevelSpinner, fitnessGoalSpinner, bodyType, preferredMacroNutrient;
    private final ArrayList<String> activityLevels = new ArrayList<>();
    private final ArrayList<String> fitnessGoals = new ArrayList<>();
    private final ArrayList<String> bodyTypes = new ArrayList<>();
    private final ArrayList<String> preferredMacroNutrients = new ArrayList<>();
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Objects.requireNonNull(getSupportActionBar()).setTitle("User Settings");
        userName = findViewById(R.id.userNameTextField);
        email = findViewById(R.id.emailAddressTextField);
        height = findViewById(R.id.heightTextFieldPP);
        weight = findViewById(R.id.weightTextFieldPP);
        Button updateProfile = findViewById(R.id.updateInformationButton);
        activityLevelSpinner = findViewById(R.id.activityLevelSpinner);
        fitnessGoalSpinner = findViewById(R.id.fitnessGoalSpinner);
        bodyType = findViewById(R.id.bodyCompositionSpinner);
        preferredMacroNutrient = findViewById(R.id.preferredMacroNutrientSpinner);
        constraintLayout = findViewById(R.id.scl);
        updateSpinners();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(Objects.requireNonNull(mAuth.getUid()));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    userName.setText(user.getUserName());
                    email.setText(user.getEmail());
                    height.setText(String.valueOf(user.getHeight()));
                    weight.setText(String.valueOf(user.getWeight()));
                    for (int i = 0; i < activityLevels.size(); i++) {
                        if (activityLevels.get(i).equalsIgnoreCase(user.getActivityLevel())) {
                            activityLevelSpinner.setSelection(i);
                            break;
                        }
                    }
                    for (int i = 0; i < fitnessGoals.size(); i++) {
                        if (fitnessGoals.get(i).equalsIgnoreCase(user.getFitnessGoal())) {
                            fitnessGoalSpinner.setSelection(i);
                            break;
                        }
                    }
                    for (int i = 0; i < bodyTypes.size(); i++) {
                        if (bodyTypes.get(i).equalsIgnoreCase(user.getBodyType())) {
                            bodyType.setSelection(i);
                            break;
                        }
                    }
                    for (int i = 0; i < preferredMacroNutrients.size(); i++) {
                        if (preferredMacroNutrients.get(i).equalsIgnoreCase(user.getPreferredMacroNutrient())) {
                            preferredMacroNutrient.setSelection(i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar snackbar = Snackbar.make(constraintLayout, "Database Access Error!" + error.getMessage(), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });

        updateProfile.setOnClickListener(v -> {
            if (weight.getText().toString().length() < 2) {
                weight.setError("Invalid weight!");
                weight.requestFocus();
            } else if (height.getText().toString().length() < 2) {
                height.setError("Invalid height!");
                height.requestFocus();
            } else if (Double.parseDouble(weight.getText().toString()) > 442) {
                weight.setError("Error max weight is 442KG!");
            } else if (Integer.parseInt(height.getText().toString()) > 232) {
                height.setError("Error max height is 232CM!");
                height.requestFocus();
            } else {
                weight.requestFocus();
                double dblHeight = Double.parseDouble(height.getText().toString().trim());
                double dblWeight = Double.parseDouble(weight.getText().toString().trim());
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("User").child(mAuth.getUid());
                double heightInMetres = dblHeight / 100.00;
                double bodyMassIndex = dblWeight / Math.pow(heightInMetres, 2.0);
                DecimalFormat decimalFormat = new DecimalFormat("##.00");
                double formattedBodyMassIndex = Double.parseDouble(decimalFormat.format(bodyMassIndex));
                databaseReference1.child("userName").setValue(userName.getText().toString().trim());
                databaseReference1.child("weight").setValue(dblWeight);
                databaseReference1.child("height").setValue(dblHeight);
                databaseReference1.child("activityLevel").setValue(activityLevelSpinner.getSelectedItem().toString());
                databaseReference1.child("fitnessGoal").setValue(fitnessGoalSpinner.getSelectedItem().toString());
                databaseReference1.child("preferredMacroNutrient").setValue(preferredMacroNutrient.getSelectedItem().toString());
                databaseReference1.child("bodyType").setValue(bodyType.getSelectedItem().toString());
                databaseReference1.child("bodyMassIndicator").setValue(formattedBodyMassIndex);
                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            height.setText(String.valueOf(user.getHeight()));
                            weight.setText(String.valueOf(user.getWeight()));
                            for (int i = 0; i < activityLevels.size(); i++) {
                                if (activityLevels.get(i).equalsIgnoreCase(user.getActivityLevel())) {
                                    activityLevelSpinner.setSelection(i);
                                    break;
                                }
                            }
                            for (int i = 0; i < fitnessGoals.size(); i++) {
                                if (fitnessGoals.get(i).equalsIgnoreCase(user.getFitnessGoal())) {
                                    fitnessGoalSpinner.setSelection(i);
                                    break;
                                }
                            }
                            for (int i = 0; i < bodyTypes.size(); i++) {
                                if (bodyTypes.get(i).equalsIgnoreCase(user.getBodyType())) {
                                    bodyType.setSelection(i);
                                    break;
                                }
                            }
                            for (int i = 0; i < preferredMacroNutrients.size(); i++) {
                                if (preferredMacroNutrients.get(i).equalsIgnoreCase(user.getPreferredMacroNutrient())) {
                                    preferredMacroNutrient.setSelection(i);
                                    break;
                                }
                            }
                            update();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Snackbar snackbar = Snackbar.make(constraintLayout, "Error occurred: " + error.getMessage(), Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }
                });
                startService(new Intent(Settings.this, HealthService.class));
                stopService(new Intent(Settings.this, HealthService.class));
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

        ArrayAdapter<String> adapterActivityLevels = new ArrayAdapter<>(Settings.this, android.R.layout.simple_spinner_dropdown_item, activityLevels);
        adapterActivityLevels.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activityLevelSpinner.setAdapter(adapterActivityLevels);
        activityLevelSpinner.setOnItemSelectedListener(Settings.this);

        ArrayAdapter<String> adapterFitnessGoal = new ArrayAdapter<>(Settings.this, android.R.layout.simple_spinner_dropdown_item, fitnessGoals);
        adapterFitnessGoal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fitnessGoalSpinner.setAdapter(adapterFitnessGoal);
        fitnessGoalSpinner.setOnItemSelectedListener(Settings.this);

        ArrayAdapter<String> adapterBodyTypes = new ArrayAdapter<>(Settings.this, android.R.layout.simple_spinner_dropdown_item, bodyTypes);
        adapterFitnessGoal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bodyType.setAdapter(adapterBodyTypes);
        bodyType.setOnItemSelectedListener(Settings.this);

        ArrayAdapter<String> adapterPreferredMacroNutrient = new ArrayAdapter<>(Settings.this, android.R.layout.simple_spinner_dropdown_item, preferredMacroNutrients);
        adapterPreferredMacroNutrient.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        preferredMacroNutrient.setAdapter(adapterPreferredMacroNutrient);
        preferredMacroNutrient.setOnItemSelectedListener(Settings.this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(Settings.this, Management.class));
    }

    public void update() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Update successful", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}