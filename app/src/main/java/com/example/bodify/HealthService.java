package com.example.bodify;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.example.bodify.Models.Macro;
import com.example.bodify.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DecimalFormat;

public class HealthService extends Service {
    private static final String TAG = "BOOMBOOMTESTGPS";
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();
    private double formattedCalorieIntake,fatAmount,proteinAmount,proteinCalories,fatCalories,carbohydrateCalories,carbohydrateAmount;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public HealthService() {
        getValues();
    }

    public void getValues() {
        assert userID != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                double weightInPounds = user.getWeight() * 2.20462;
                double requiredCalories = 0.0;
                switch (user.getActivityLevel()) {
                    case "1":
                        requiredCalories = weightInPounds * 14;
                        break;
                    case "2":
                        requiredCalories = weightInPounds * 15;
                        break;
                    case "3":
                        requiredCalories = weightInPounds * 16;
                        break;
                }
                if (user.getFitnessGoal().equals("Lose Weight")) {
                    requiredCalories = requiredCalories - 500;
                } else if (user.getFitnessGoal().equals("Gain Weight")) {
                    requiredCalories = requiredCalories + 500;
                }
                DecimalFormat decimalFormat = new DecimalFormat("##.00");
                formattedCalorieIntake = Double.parseDouble(decimalFormat.format(requiredCalories));
                if (user.getBodyType().equalsIgnoreCase("Excess body fat")) {
                    proteinAmount = (user.getWeight() * 2.20462) * 0.75;
                } else if (user.getBodyType().equalsIgnoreCase("Average Shape")) {
                    proteinAmount = (user.getWeight() * 2.20462) * 1;
                } else if (user.getBodyType().equalsIgnoreCase("Good Shape")) {
                    proteinAmount = (user.getWeight() * 2.20462) * 1.25;
                }
                if (user.getPreferredMacroNutrient().equalsIgnoreCase("Carbohydrates")) {
                    fatAmount = (user.getWeight() * 2.20462) * 0.3;
                } else if (user.getPreferredMacroNutrient().equalsIgnoreCase("Don't have a preference")) {
                    fatAmount = (user.getWeight() * 2.20462) * 0.35;
                } else if (user.getPreferredMacroNutrient().equalsIgnoreCase("Fats")) {
                    fatAmount = (user.getWeight() * 2.20462) * 0.4;
                }
                proteinCalories = proteinAmount * 4;
                fatCalories = fatAmount * 9;
                carbohydrateCalories = formattedCalorieIntake - (proteinCalories + fatCalories);
                carbohydrateAmount = carbohydrateCalories / 4;
                setUserMacros(formattedCalorieIntake, fatAmount, carbohydrateAmount, proteinAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HealthService.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setUserMacros(Double calories,Double fat,Double carbs,Double protein) {
        Macro macro = new Macro(Math.round(calories),Math.round(fat),Math.round(carbs),Math.round(protein),userID);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        assert userID != null;
        databaseReference.child("Macros").child(userID).setValue(macro).addOnFailureListener(e -> Toast.makeText(HealthService.this,"Error Occurred: " + e.getMessage(),Toast.LENGTH_SHORT).show());
    }
}

