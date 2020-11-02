package com.example.bodify;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bodify.Models.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Health extends AppCompatActivity {
    private TextView bmi, weight, height, fitnessGoal, fitnessLevel, calorieIntake;
    private FirebaseAuth mAuth;
    private BarChart barChart;
    private BarData barData;
    private BarDataSet barDataSet;
    private ArrayList<BarEntry> barEntries = new ArrayList<>();
    private Double formattedCalorieIntake;
    private Double proteinAmount;
    private Double carbohydrateAmount;
    private Double fatAmount;
    private Double proteinCalories;
    private Double carbohydrateCalories;
    private Double fatCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);
        bmi = findViewById(R.id.bmiTextField);
        weight = findViewById(R.id.currentWeight);
        height = findViewById(R.id.currentHeight);
        fitnessGoal = findViewById(R.id.currentFitnessGoal);
        fitnessLevel = findViewById(R.id.fitnessLevelTextField);
        calorieIntake = findViewById(R.id.calorieIntakeTextField);
        barChart = findViewById(R.id.barChart);
        mAuth = FirebaseAuth.getInstance();
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
                ArrayList<BarEntry> macros = new ArrayList<>();
                if (user != null) {
                    weight.setText(String.valueOf(user.getWeight()));
                    height.setText(String.valueOf(user.getHeight()));
                    fitnessGoal.setText(user.getFitnessGoal());
                    Double heightInMetres = user.getHeight() / 100.00;
                    Double bodyMassIndex = user.getWeight() / Math.pow(heightInMetres, 2.0);
                    DecimalFormat decimalFormat = new DecimalFormat("##.00");
                    Double formattedBodyMassIndex = Double.parseDouble(decimalFormat.format(bodyMassIndex));
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
                    Double weightInPounds = user.getWeight() * 2.20462;
                    Double requiredCalories = 0.0;
                    if (user.getActivityLevel().equals("1")) {
                        requiredCalories = weightInPounds * 14;
                    } else if (user.getActivityLevel().equals("2")) {
                        requiredCalories = weightInPounds * 15;
                    } else if (user.getActivityLevel().equals("3")) {
                        requiredCalories = weightInPounds * 16;
                    }
                    if (user.getFitnessGoal().equals("Lose Weight")) {
                        requiredCalories = requiredCalories - 500;
                    } else if (user.getFitnessGoal().equals("Gain Weight")) {
                        requiredCalories = requiredCalories + 500;
                    }
                    formattedCalorieIntake = Double.parseDouble(decimalFormat.format(requiredCalories));
                    calorieIntake.setText(String.valueOf(formattedCalorieIntake));
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
                    macros.add(new BarEntry(1,fatAmount.floatValue()));
                    macros.add(new BarEntry(2,carbohydrateAmount.floatValue()));
                    macros.add(new BarEntry(3,proteinAmount.floatValue()));
                }
                showChart(macros);
                if(user == null) {
                    barChart.clear();
                    barChart.invalidate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Health.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showChart(ArrayList<BarEntry> macros) {
        barEntries.add(macros.get(0));
        barEntries.add(macros.get(1));
        barEntries.add(macros.get(2));
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getXaxisValues()));
        barDataSet = new BarDataSet(barEntries, "Data Set");
        barData = new BarData(barDataSet);
        barChart.clear();
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart.invalidate();
    }

    public ArrayList<String> getXaxisValues() {
        ArrayList<String> xLabels = new ArrayList<>();
        xLabels.add("0");
        xLabels.add("Fats");
        xLabels.add("Carbohydrates");
        xLabels.add("Proteins");
        ArrayList<String> label = new ArrayList<>();
        for (int i = 0; i < xLabels.size(); i++)
            label.add(xLabels.get(i));
        return label;
    }
}
