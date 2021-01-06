package com.example.bodify.TrackingDaysMacros;

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
import com.example.bodify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class Remember extends Fragment {
    private AnyChartView anyChartView;
    private TextView calories, fats, proteins, carbohydrates;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();
    private double macroCalories, macroProteins, macroFats, macroCarbohydrates;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_monday, container, false);
        anyChartView = view.findViewById(R.id.pieChart);
        calories = view.findViewById(R.id.dailyCaloriesLeftTextView);
        fats = view.findViewById(R.id.dailyFatsTV);
        proteins = view.findViewById(R.id.dailyProteinsTV);
        carbohydrates = view.findViewById(R.id.dailyCarbsTV);
        populateUIComponents();
        return view;
    }

    public void populateUIComponents() {
        assert userID != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Macros").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Macro macro = snapshot.getValue(Macro.class);
                assert macro != null;
                macroFats = macro.getFats();
                macroProteins = macro.getProteins();
                macroCarbohydrates = macro.getCarbohydrates();
                macroCalories = macro.getCalorieConsumption();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("DayOfWeek").child("Wednesday");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            double loggedCalories, loggedProteins, loggedFats, loggedCarbohydrates;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Meal meal = userSnapshot.getValue(Meal.class);
                    assert meal != null;
                    if (meal.getUserID().equals(userID)) {
                        loggedCalories = loggedCalories + meal.getCalories() * meal.getNumberOfServings();
                        loggedProteins = loggedProteins + meal.getItemProtein() * meal.getNumberOfServings();
                        loggedFats = loggedFats + meal.getItemTotalFat() * meal.getNumberOfServings();
                        loggedCarbohydrates = loggedCarbohydrates + meal.getItemTotalCarbohydrates() * meal.getNumberOfServings();

                    }
                }
                macroCalories = macroCalories - loggedCalories;
                macroProteins = macroProteins - loggedProteins;
                macroFats = macroFats - loggedFats;
                macroCarbohydrates = macroCarbohydrates - macroCarbohydrates;
                calories.setText(String.valueOf(macroCalories));
                fats.setText(String.valueOf(macroFats));
                proteins.setText(String.valueOf(macroProteins));
                carbohydrates.setText(String.valueOf(macroCarbohydrates));
                double[] macroValues = {loggedProteins, loggedFats, loggedCarbohydrates};
                setUpPieChart(macroValues);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setUpPieChart(double[] macroValues) {
        String[] macros = new String[]{"Fat", "Protein", "Carbs"};
        Log.i("call", "being called");
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();
        for (int i = 0; i < macros.length; i++) {
            dataEntries.add(new ValueDataEntry(macros[i], macroValues[i]));
        }
        pie.data(dataEntries);
        anyChartView.setChart(pie);
    }

    //all thats left is to update the database. I need to think how am I going to have a temporary values then reset them every 24 hours?
    //my plan is to create a pojo that is the same as Macro and every 24 hours reset it to whatever macro is?
    //once that is all complete I just need to duplicate this code throughout the rest of the week and change the day thats passed into the database reference.
}

