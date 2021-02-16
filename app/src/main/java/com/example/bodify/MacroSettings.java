package com.example.bodify;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.bodify.Models.SuggestionMacros;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;

public class MacroSettings extends AppCompatActivity {

    private SeekBar fatsSB, proteinsSB, carbsSB, caloriesSB;
    private TextView fats, protein, carbs, calories;
    private final int minimumFat = 1;
    private final int minimumProtein = 10;
    private final int minimumCarbs = 10;
    private final int minimumCalories = 50;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();
    private ConstraintLayout constraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macro_settings);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Recipe suggestion settings");
        fatsSB = findViewById(R.id.fatsSeekBar);
        proteinsSB = findViewById(R.id.proteinsSeekBar);
        carbsSB = findViewById(R.id.carbohydratesSeekBar);
        caloriesSB = findViewById(R.id.caloriesSeekBar);
        Button update = findViewById(R.id.updateSuggestion);
        Button remove = findViewById(R.id.removeConstraints);
        fats = findViewById(R.id.fatCount);
        protein = findViewById(R.id.proteinCount);
        carbs = findViewById(R.id.carbCount);
        calories = findViewById(R.id.calorieCount);
        constraintLayout = findViewById(R.id.macroSettingsCL);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RecipeSuggestionMacro").child(userID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SuggestionMacros suggestionMacros = snapshot.getValue(SuggestionMacros.class);
                if(suggestionMacros != null) {
                    setProgressBarValues(suggestionMacros.getCalories(),suggestionMacros.getFats(),suggestionMacros.getProteins(),suggestionMacros.getCarbohydrates());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MacroSettings.this,"Error Occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        fatsSB.setMax(100);
        proteinsSB.setMax(100);
        carbsSB.setMax(100);
        caloriesSB.setMax(800);
        fatsSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getProgress() < minimumFat) {
                    seekBar.setProgress(minimumFat);
                } else {
                    fats.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        proteinsSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getProgress() < minimumProtein) {
                    seekBar.setProgress(minimumProtein);
                } else {
                    protein.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        carbsSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getProgress() < minimumCarbs) {
                    seekBar.setProgress(minimumCarbs);
                } else {
                    carbs.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        caloriesSB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getProgress() < minimumCalories) {
                    seekBar.setProgress(minimumCalories);
                } else {
                    calories.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        update.setOnClickListener(v -> {
            if (fatsSB.getProgress() == 0 || proteinsSB.getProgress() == 0 || carbsSB.getProgress() == 0 || caloriesSB.getProgress() == 0) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MacroSettings.this);
                dlgAlert.setMessage("Error no values can be left at zero!");
                dlgAlert.setTitle("Error...");
                dlgAlert.setCancelable(true);
                dlgAlert.setPositiveButton("Ok",
                        (dialog, which) -> {
                            dialog.cancel();
                        });
                dlgAlert.create().show();
            } else {
                SuggestionMacros suggestionMacros = new SuggestionMacros(caloriesSB.getProgress(), fatsSB.getProgress(), proteinsSB.getProgress(), carbsSB.getProgress(), userID);
                Log.i("Macro", "" + suggestionMacros.toString());
                DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("RecipeSuggestionMacro");
                databaseReference1.child(userID).setValue(suggestionMacros).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i("Saved", "Successfully saved");
                        updateSnackBar();
                    }
                }).addOnFailureListener(e -> Toast.makeText(MacroSettings.this, "Error Occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });

        remove.setOnClickListener(v -> {
            DatabaseReference databaseReference12 = FirebaseDatabase.getInstance().getReference("RecipeSuggestionMacro").child(userID);
            databaseReference12.removeValue();
            deleteSnackBar();
            fatsSB.setProgress(0);
            proteinsSB.setProgress(0);
            carbsSB.setProgress(0);
            caloriesSB.setProgress(0);
        });
    }

    public void setProgressBarValues(int calories,int fat,int proteins,int carbs) {
        caloriesSB.setProgress(calories);
        fatsSB.setProgress(fat);
        proteinsSB.setProgress(proteins);
        carbsSB.setProgress(carbs);
    }

    public void updateSnackBar() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Macro suggestion settings updated in the Database", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void deleteSnackBar() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Macro suggestion settings deleted in the Database", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}