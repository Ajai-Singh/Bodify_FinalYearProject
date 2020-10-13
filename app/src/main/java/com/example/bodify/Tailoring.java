package com.example.bodify;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bodify.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.bodify.SignUp.MESSAGE_KEY;

public class Tailoring extends AppCompatActivity {

    private EditText weight, height;
    private RadioGroup gender, fitnessGoal, activityLevel;
    private Button submit;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tailoring);
        getSupportActionBar().setTitle("Personalize your profile");
        weight = findViewById(R.id.weightTextField);
        height = findViewById(R.id.heightTextField);
        gender = findViewById(R.id.genderRadioGroup);
        fitnessGoal = findViewById(R.id.fitnessGoalRadioGroup);
        activityLevel = findViewById(R.id.activityLevelRadioGroup);
        submit = findViewById(R.id.submitButton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double dblWeight = Double.parseDouble(weight.getText().toString());
                Double dblHeight = Double.parseDouble(height.getText().toString());
//                if(TextUtils.isEmpty(strWeight) && TextUtils.isEmpty(strHeight) && gender.getCheckedRadioButtonId() == -1 && fitnessGoal.getCheckedRadioButtonId() == -1
//                        && activityLevel.getCheckedRadioButtonId() == -1) {
//
//                }else if(TextUtils.isEmpty(strWeight)) {
//                    weight.setError("Weight is required.");
//                    weight.requestFocus();
//                }else if(TextUtils.isEmpty(strHeight)) {
//                    height.setError("Height is required.");
//                    height.requestFocus();
//                }else if(gender.getCheckedRadioButtonId() == -1) {
//
//                }else if(fitnessGoal.getCheckedRadioButtonId() == -1) {
//
//                }else if(activityLevel.getCheckedRadioButtonId() == -1) {
//
//                }
                //getting the selected radio button from a radio group
                int selectedGenderID = gender.getCheckedRadioButtonId();
                //find the radio button from the id returned
                RadioButton genderRadioButton = findViewById(selectedGenderID);
                String strGender = genderRadioButton.getText().toString();

                int selectedActivityLevelID = activityLevel.getCheckedRadioButtonId();
                RadioButton activityLevelRadioButton = findViewById(selectedActivityLevelID);
                String strActivityLevel = activityLevelRadioButton.getText().toString();

                int selectedFitnessGoalID = fitnessGoal.getCheckedRadioButtonId();
                RadioButton fitnessGoalRadioButton = findViewById(selectedFitnessGoalID);
                String strFitnessGoal = fitnessGoalRadioButton.getText().toString();

//                Intent intent = getIntent();
//                String strUserName = intent.getStringExtra(MESSAGE_KEY);
//                FirebaseUser firebaseUser = mAuth.getCurrentUser();
//                String userID = firebaseUser.getUid();
//                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//                //question for Thoa how to do I create a User Object with attributes that are in the Authentication table, Also I am passing one Attribute over with an Intent.
//                User user = new User(strUserName,userID.getPassword)

            }
        });

    }
}