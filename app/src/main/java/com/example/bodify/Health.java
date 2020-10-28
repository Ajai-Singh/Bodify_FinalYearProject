package com.example.bodify;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bodify.Models.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
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
    private ArrayList barEntries;

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
        getBarChartEntries();
        barDataSet = new BarDataSet(barEntries, "Data Set");
        barData = new BarData(barDataSet);
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
    }

    public void updateFields() {
        mAuth = FirebaseAuth.getInstance();
        final String userID = mAuth.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
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
                    //calculate calories
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
                    Double formattedCalorieIntake = Double.parseDouble(decimalFormat.format(requiredCalories));
                    calorieIntake.setText(String.valueOf(formattedCalorieIntake));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Health.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getBarChartEntries() {
        //calculate fats carbs and protein and  then pass them into the Y-axis
        barEntries = new ArrayList();
        barEntries.add(new BarEntry(1, 50));
        barEntries.add(new BarEntry(2, 100));
        barEntries.add(new BarEntry(3, 150));

        final ArrayList<String> xLabels = new ArrayList<>();
        xLabels.add("Fats");
        xLabels.add("Carbohydrates");
        xLabels.add("Proteins");


        final XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(value >= 0) {
                    if(value<=xLabels.size() - 1){
                        return xLabels.get((int) value);
                    }
                    return "";
                }
                return "";
            }
        });

    }
}
