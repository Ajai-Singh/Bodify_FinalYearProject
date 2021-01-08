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
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class Thursday extends Fragment {
    private AnyChartView anyChartView;
    private TextView caloriesTV, fatsTV, proteinsTV, carbohydratesTV;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();
    private BarChart barChart;
    private final ArrayList<BarEntry> barEntries = new ArrayList<>();

    public Thursday() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_monday, container, false);
        anyChartView = view.findViewById(R.id.pieChart);
        caloriesTV = view.findViewById(R.id.dailyCaloriesLeftTextView);
        fatsTV = view.findViewById(R.id.dailyFatsTV);
        proteinsTV = view.findViewById(R.id.dailyProteinsTV);
        carbohydratesTV = view.findViewById(R.id.dailyCarbsTV);
        barChart = view.findViewById(R.id.breakdownBarChart);
        getValuesForMacroCopy();
        getMacroCopyValues();
        setUIComponents();
        setUpPieChart();
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
                assert macro != null;
                createMacroCopyInDatabase(macro.getCalorieConsumption(), macro.getCarbohydrates(), macro.getFats(), macro.getProteins());
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
        databaseReference.child("Thursday").child(userID).setValue(macroCopy).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("Saved", "Successfully saved");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error Occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setUIComponents() {
        assert userID != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TemporaryMacros").child("Thursday").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MacroCopy macroCopy = snapshot.getValue(MacroCopy.class);

                if (macroCopy != null) {
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

    public void setUpPieChart() {
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("DayOfWeek").child("Thursday");
        databaseReference1.addValueEventListener(new ValueEventListener() {
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
                double[] macroValues = {loggedProteins, loggedFats, loggedCarbohydrates};
                String[] macros = new String[]{"Fat", "Protein", "Carbs"};
                Pie pie = AnyChart.pie();
                List<DataEntry> dataEntries = new ArrayList<>();
                for (int i = 0; i < macros.length; i++) {
                    dataEntries.add(new ValueDataEntry(macros[i], macroValues[i]));
                }
                pie.data(dataEntries);
                anyChartView.setChart(pie);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setUpBarChart() {
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("DayOfWeek").child("Thursday");
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
        xLabels.add("Cals @ Fat");
        xLabels.add("Cals @ Carbohydrate");
        xLabels.add("Cals @ Protein");
        return new ArrayList<>(xLabels);
    }

    public void getMacroCopyValues() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek").child("Thursday");
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TemporaryMacros").child("Thursday").child(userID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MacroCopy macroCopy = snapshot.getValue(MacroCopy.class);
                if (macroCopy != null) {
                    DatabaseReference updateDBReference = FirebaseDatabase.getInstance().getReference("TemporaryMacros").child("Thursday").child(userID);
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




