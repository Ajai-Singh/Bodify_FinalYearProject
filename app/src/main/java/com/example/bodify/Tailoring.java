package com.example.bodify;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bodify.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.DecimalFormat;
import static com.example.bodify.SignUp.MESSAGE_KEY;

public class Tailoring extends AppCompatActivity {

    private EditText weight, height;
    private RadioGroup gender, fitnessGoal, activityLevel;
    private Button submit;

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
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strWeight = weight.getText().toString();
                String strHeight = height.getText().toString();
                if(strWeight.matches("") || strHeight.matches("") || gender.getCheckedRadioButtonId() == -1 || fitnessGoal.getCheckedRadioButtonId() == -1
                        || activityLevel.getCheckedRadioButtonId() == -1) {
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(Tailoring.this);
                    dlgAlert.setMessage("Not All Fields are Filled!");
                    dlgAlert.setTitle("Error...");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                }
                else{
                    Double dblWeight = Double.parseDouble(strWeight);
                    int intHeight = Integer.parseInt(strHeight);
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

                Intent intent = getIntent();
                String strUserName = intent.getStringExtra(MESSAGE_KEY);
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                String strEmail = firebaseUser.getEmail();
                String userID = firebaseUser.getUid();

                Double bodyMassIndex;
                Double heightInMetres = intHeight/100.00;
                bodyMassIndex = dblWeight /Math.pow(heightInMetres,2.0);
                DecimalFormat decimalFormat = new DecimalFormat("##.00");
                Double formattedBodyMassIndex = Double.parseDouble(decimalFormat.format(bodyMassIndex));
                final User user = new User(strUserName,strEmail,strGender,strActivityLevel,strFitnessGoal,dblWeight,formattedBodyMassIndex,intHeight);
                databaseReference.child("User").child(userID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "User Saved Successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Error Occurred!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }}
        });
    }
}
