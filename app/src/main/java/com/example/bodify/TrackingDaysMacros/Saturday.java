package com.example.bodify.TrackingDaysMacros;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bodify.Models.Macro;
import com.example.bodify.Models.MacroCopy;
import com.example.bodify.Models.Meal;
import com.example.bodify.R;
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

import java.util.ArrayList;

public class Saturday extends Fragment {
    private TextView caloriesTV, fatsTV, proteinsTV, carbohydratesTV;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();
    private BarChart barChart;
    private BarChart barChart1;
    private final ArrayList<BarEntry> barEntries = new ArrayList<>();
    private final ArrayList<BarEntry> barEntries1 = new ArrayList<>();

    public Saturday() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_monday, container, false);
        caloriesTV = view.findViewById(R.id.dailyCaloriesLeftTextView);
        fatsTV = view.findViewById(R.id.dailyFatsTV);
        proteinsTV = view.findViewById(R.id.dailyProteinsTV);
        carbohydratesTV = view.findViewById(R.id.dailyCarbsTV);
        barChart = view.findViewById(R.id.breakdownBarChart);
        barChart1 = view.findViewById(R.id.pieChart);
        barEntries.clear();
        barEntries1.clear();
        barChart.setTouchEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        barChart1.setTouchEnabled(false);
        barChart1.setPinchZoom(false);
        barChart1.setDoubleTapToZoomEnabled(false);
        getValuesForMacroCopy();
        getMacroCopyValues();
        setUIComponents();
        setUpBarChart1();
        setUpBarChart();
        return view;
    }

    public void getValuesForMacroCopy() {
        assert userID != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Macros").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Macro macro = snapshot.getValue(Macro.class);
                if (macro != null) {
                    createMacroCopyInDatabase(macro.getCalorieConsumption(), macro.getCarbohydrates(), macro.getFats(), macro.getProteins());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createMacroCopyInDatabase(double a, double b, double c, double d) {
        assert userID != null;
        MacroCopy macroCopy = new MacroCopy(a, b, c, d, userID);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("TemporaryMacros");
        databaseReference.child("Saturday").child(userID).setValue(macroCopy).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i("Saved", "Successfully saved");
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error Occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void setUIComponents() {
        assert userID != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TemporaryMacros").child("Saturday").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MacroCopy macroCopy = snapshot.getValue(MacroCopy.class);
                if (macroCopy != null) {
                    if (macroCopy.getCarbohydrateConsumption() < 0) {
                        carbohydratesTV.setTextColor(Color.parseColor("#FF0000"));
                    } else {
                        carbohydratesTV.setTextColor(Color.BLACK);
                    }
                    if (macroCopy.getFatConsumption() < 0) {
                        fatsTV.setTextColor(Color.parseColor("#FF0000"));
                    } else {
                        fatsTV.setTextColor(Color.BLACK);
                    }
                    if (macroCopy.getProteinConsumption() < 0) {
                        proteinsTV.setTextColor(Color.parseColor("#FF0000"));
                    } else {
                        proteinsTV.setTextColor(Color.BLACK);
                    }
                    if (macroCopy.getCalorieConsumption() < 0) {
                        caloriesTV.setTextColor(Color.parseColor("#FF0000"));
                    } else {
                        caloriesTV.setTextColor(Color.BLACK);
                    }
                    caloriesTV.setText(String.valueOf(macroCopy.getCalorieConsumption()));
                    fatsTV.setText(String.valueOf(macroCopy.getFatConsumption()));
                    proteinsTV.setText(String.valueOf(macroCopy.getProteinConsumption()));
                    carbohydratesTV.setText(String.valueOf(macroCopy.getCarbohydrateConsumption()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setUpBarChart1() {
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("DayOfWeek").child("Saturday");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            double loggedCalories, loggedProteins, loggedFats, loggedCarbohydrates;
            final ArrayList<BarEntry> macros = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Meal meal = userSnapshot.getValue(Meal.class);
                    assert meal != null;
                    if (meal.getUserID().equals(userID)) {
                        loggedCalories += meal.getCalories() * meal.getNumberOfServings();
                        loggedProteins += meal.getItemProtein() * meal.getNumberOfServings();
                        loggedFats += meal.getItemTotalFat() * meal.getNumberOfServings();
                        loggedCarbohydrates += meal.getItemTotalCarbohydrates() * meal.getNumberOfServings();
                    }
                }
                if (loggedProteins == 0.0 && loggedFats == 0.0 && loggedCarbohydrates == 0.0) {
                    barChart1.setVisibility(View.GONE);
                    barChart.setVisibility(View.GONE);
                } else {
                    macros.add(new BarEntry(1f, (float) loggedFats));
                    macros.add(new BarEntry(2f, (float) loggedCarbohydrates));
                    macros.add(new BarEntry(3f, (float) loggedProteins));
                    showBarChart1(macros);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showBarChart1(ArrayList<BarEntry> macros) {
        barEntries1.add(macros.get(0));
        barEntries1.add(macros.get(1));
        barEntries1.add(macros.get(2));
        XAxis xAxis = barChart1.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getXAxisValues1()));
        BarDataSet barDataSet = new BarDataSet(barEntries1, "Data Set");
        BarData barData = new BarData(barDataSet);
        barChart1.clear();
        barChart1.setData(barData);
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart1.invalidate();
    }

    public ArrayList<String> getXAxisValues1() {
        ArrayList<String> xLabels = new ArrayList<>();
        xLabels.add("0");
        xLabels.add("Fats (g)");
        xLabels.add("Carbohydrates (g)");
        xLabels.add("Proteins (g)");
        return new ArrayList<>(xLabels);
    }

    public void setUpBarChart() {
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("DayOfWeek").child("Saturday");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            double loggedCalories, loggedProteins, loggedFats, loggedCarbohydrates;
            final ArrayList<BarEntry> macros = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Meal meal = userSnapshot.getValue(Meal.class);
                    assert meal != null;
                    if (meal.getUserID().equals(userID)) {
                        loggedCalories += meal.getCalories() * meal.getNumberOfServings();
                        loggedProteins += meal.getItemProtein() * meal.getNumberOfServings();
                        loggedFats += meal.getItemTotalFat() * meal.getNumberOfServings();
                        loggedCarbohydrates += meal.getItemTotalCarbohydrates() * meal.getNumberOfServings();
                    }
                }
                macros.add(new BarEntry(1f, (float) loggedFats * 9));
                macros.add(new BarEntry(2f, (float) loggedCarbohydrates * 4));
                macros.add(new BarEntry(3f, (float) loggedProteins * 4));
                showBarChart(macros);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showBarChart(ArrayList<BarEntry> macros) {
        barEntries.add(macros.get(0));
        barEntries.add(macros.get(1));
        barEntries.add(macros.get(2));
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getXAxisValues()));
        BarDataSet barDataSet = new BarDataSet(barEntries, "Data Set");
        BarData barData = new BarData(barDataSet);
        barChart.clear();
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart.invalidate();
    }

    public ArrayList<String> getXAxisValues() {
        ArrayList<String> xLabels = new ArrayList<>();
        xLabels.add("0");
        xLabels.add("Fat Calories");
        xLabels.add("Carbohydrate Calories");
        xLabels.add("Protein Calories");
        return new ArrayList<>(xLabels);
    }

    public void getMacroCopyValues() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek").child("Saturday");
        databaseReference.addValueEventListener(new ValueEventListener() {
            double loggedCalories, loggedProteins, loggedFats, loggedCarbohydrates;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Meal meal = userSnapshot.getValue(Meal.class);
                    assert meal != null;
                    if (meal.getUserID().equals(userID)) {
                        loggedCalories += meal.getCalories() * meal.getNumberOfServings();
                        loggedProteins += meal.getItemProtein() * meal.getNumberOfServings();
                        loggedFats += meal.getItemTotalFat() * meal.getNumberOfServings();
                        loggedCarbohydrates += meal.getItemTotalCarbohydrates() * meal.getNumberOfServings();
                    }
                }
                updateMacroCopy(loggedCalories, loggedProteins, loggedFats, loggedCarbohydrates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateMacroCopy(final double calories, final double proteins, final double fats, final double carbohydrates) {
        assert userID != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TemporaryMacros").child("Saturday").child(userID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MacroCopy macroCopy = snapshot.getValue(MacroCopy.class);
                if (macroCopy != null) {
                    DatabaseReference updateDBReference = FirebaseDatabase.getInstance().getReference("TemporaryMacros").child("Saturday").child(userID);
                    updateDBReference.child("calorieConsumption").setValue(macroCopy.getCalorieConsumption() - calories);
                    updateDBReference.child("carbohydrateConsumption").setValue(macroCopy.getCarbohydrateConsumption() - carbohydrates);
                    updateDBReference.child("fatConsumption").setValue(macroCopy.getFatConsumption() - fats);
                    updateDBReference.child("proteinConsumption").setValue(macroCopy.getProteinConsumption() - proteins);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}




