package com.example.bodify;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.bodify.Models.Macro;
import com.example.bodify.Models.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
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

public class Health extends AppCompatActivity {
    private TextView bmi, weight, height, fitnessGoal, fitnessLevel, calorieIntake;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private BarChart barChart;
    private final ArrayList<BarEntry> barEntries = new ArrayList<>();
    private Double formattedCalorieIntake, proteinAmount, carbohydrateAmount, fatAmount, proteinCalories, carbohydrateCalories, fatCalories;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Health");
        bmi = findViewById(R.id.bmiTextField);
        weight = findViewById(R.id.currentWeight);
        height = findViewById(R.id.currentHeight);
        fitnessGoal = findViewById(R.id.currentFitnessGoal);
        fitnessLevel = findViewById(R.id.fitnessLevelTextField);
        calorieIntake = findViewById(R.id.calorieIntakeTextField);
        constraintLayout = findViewById(R.id.hcl);
        barChart = findViewById(R.id.barChart);
        barChart.setTouchEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        updateFields();
    }

    public void updateFields() {
        final String userID = mAuth.getUid();
        assert userID != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                ArrayList<BarEntry> macros = new ArrayList<>();
                if (user != null) {
                    weight.setText(String.valueOf(user.getWeight()));
                    height.setText(String.valueOf(user.getHeight()));
                    fitnessGoal.setText(user.getFitnessGoal());
                    double heightInMetres = user.getHeight() / 100.00;
                    Double bodyMassIndex = user.getWeight() / Math.pow(heightInMetres, 2.0);
                    DecimalFormat decimalFormat = new DecimalFormat("##.00");
                    double formattedBodyMassIndex = Double.parseDouble(decimalFormat.format(bodyMassIndex));
                    bmi.setText(String.valueOf(formattedBodyMassIndex));
                    if (formattedBodyMassIndex < 18.5) {
                        fitnessLevel.setText("Underweight");
                    } else if (formattedBodyMassIndex >= 18.5 && formattedBodyMassIndex <= 24.9) {
                        fitnessLevel.setText("Healthy Weight");
                    } else if (formattedBodyMassIndex >= 25.0 && formattedBodyMassIndex <= 29.9) {
                        fitnessLevel.setText("Overweight");
                    } else if (formattedBodyMassIndex > 30.0) {
                        fitnessLevel.setText("Obese");
                    }
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
                    formattedCalorieIntake = Double.parseDouble(decimalFormat.format(requiredCalories));
                    calorieIntake.setText(String.valueOf(Math.round(formattedCalorieIntake)));
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
                    macros.add(new BarEntry(1, fatAmount.floatValue()));
                    macros.add(new BarEntry(2, carbohydrateAmount.floatValue()));
                    macros.add(new BarEntry(3, proteinAmount.floatValue()));
                }
                setUserMacros(formattedCalorieIntake, fatAmount, carbohydrateAmount, proteinAmount);
                showChart(macros);
                if (user == null) {
                    barChart.clear();
                    barChart.invalidate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar snackbar = Snackbar.make(constraintLayout, "Error occurred: " + error.getMessage(), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
    }

    public void setUserMacros(Double calories, Double fat, Double carbs, Double protein) {
        Macro macro = new Macro(Math.round(calories), Math.round(fat), Math.round(carbs), Math.round(protein));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Macros").child(Objects.requireNonNull(mAuth.getUid())).setValue(macro).addOnFailureListener(e -> {
            Snackbar snackbar = Snackbar.make(constraintLayout, "Error occurred: " + e.getMessage(), Snackbar.LENGTH_SHORT);
            snackbar.show();
        });
    }

    public void showChart(ArrayList<BarEntry> macros) {
        barEntries.addAll(macros);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getXAxisValues()));
        BarDataSet barDataSet = new BarDataSet(barEntries, "Data Set");
        BarData barData = new BarData(barDataSet);
        barChart.clear();
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart.invalidate();
    }

    public ArrayList<String> getXAxisValues() {
        ArrayList<String> xLabels = new ArrayList<>();
        xLabels.add("0");
        xLabels.add("Fats");
        xLabels.add("Carbohydrates");
        xLabels.add("Proteins");
        return new ArrayList<>(xLabels);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(Health.this, Management.class));
    }
}
