package com.example.bodify;

import android.annotation.SuppressLint;
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
import com.example.bodify.Models.Meal;
import com.example.bodify.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Breakdown extends Fragment {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();
    private AnyChartView anyChartView;
    private final Date currentWeekDay = new Date();
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
    private TextView calories, fats, proteins, carbohydrates;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_breakdown, container, false);
        anyChartView = view.findViewById(R.id.pieChart);
        calories = view.findViewById(R.id.dailyCaloriesLeftTextView);
        fats = view.findViewById(R.id.dailyFatsTV);
        proteins = view.findViewById(R.id.dailyProteinsTV);
        carbohydrates = view.findViewById(R.id.dailyCarbsTV);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setFields();
        calculateDailyMacros();
    }

    public void setUpPieChart(double fat, double protein, double carbs) {
        double[] macrosValues = {fat, protein, carbs};
        String[] macros = {"Fat", "Protein", "Carbs"};
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();
        for (int i = 0; i < macros.length; i++) {
            dataEntries.add(new ValueDataEntry(macros[i], macrosValues[i]));

        }
        pie.data(dataEntries);
        anyChartView.setChart(pie);
    }

    public void calculateDailyMacros() {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek").child(simpleDateformat.format(currentWeekDay));
        databaseReference.addValueEventListener(new ValueEventListener() {
            double calories, protein, carbohydrates, fats;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Meal meal = userSnapshot.getValue(Meal.class);
                    assert meal != null;
                    if (meal.getUserID().equals(userID)) {
                        protein = protein + meal.getItemProtein() * meal.getNumberOfServings();
                        fats = fats + meal.getItemTotalFat() * meal.getNumberOfServings();
                        carbohydrates = carbohydrates + meal.getItemTotalCarbohydrates() * meal.getNumberOfServings();
                        calories = calories + meal.getCalories() * meal.getNumberOfServings();
                        updateMacrosInDatabase(protein, fats, carbohydrates, calories);
                    }
                }
                setUpPieChart(fats, protein, carbohydrates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateMacrosInDatabase(final double protein, final double fats, final double carbohydrates, final double calories) {
        assert userID != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Macro").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Macro macro = snapshot.getValue(Macro.class);
                if (macro != null) {
                    Log.i("TAG",String.valueOf(macro.getProteins()));
                    Log.i("TAG1",String.valueOf(macro.getFats()));
                    Log.i("TAG2",String.valueOf(macro.getCarbohydrates()));
                    Log.i("TAG3",String.valueOf(macro.getCalorieConsumption()));
//                    macro.setProteins(macro.getProteins() - protein);
//                    macro.setFats(macro.getFats() - fats);
//                    macro.setCarbohydrates(macro.getCarbohydrates() - carbohydrates);
//                    macro.setCalorieConsumption(macro.getCalorieConsumption() - calories);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setFields() {
        assert userID != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Macros").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Macro macro = snapshot.getValue(Macro.class);
                assert macro != null;
                calories.setText(String.valueOf(macro.getCalorieConsumption()));
                fats.setText(String.valueOf(macro.getFats()));
                proteins.setText(String.valueOf(macro.getProteins()));
                carbohydrates.setText(String.valueOf(macro.getCarbohydrates()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
