package com.example.bodify;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.bodify.Adapters.MyAdapter;
import com.example.bodify.Models.Macro;
import com.example.bodify.Models.Meal;
import com.google.android.material.tabs.TabLayout;
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
    private double macroCalories, macroProteins, macroFats, macroCarbohydrates;
    private ViewPager viewPager;
    private FragmentActivity fragmentActivity;
    @SuppressLint("CutPasteId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_breakdown, container, false);
        anyChartView = view.findViewById(R.id.pieChart);
        calories = view.findViewById(R.id.dailyCaloriesLeftTextView);
        fats = view.findViewById(R.id.dailyFatsTV);
        proteins = view.findViewById(R.id.dailyProteinsTV);
        carbohydrates = view.findViewById(R.id.dailyCarbsTV);
        viewPager = view.findViewById(R.id.viewPager);
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Mon"));
        tabLayout.addTab(tabLayout.newTab().setText("Tue"));
        tabLayout.addTab(tabLayout.newTab().setText("Wed"));
        MyAdapter adapter = new MyAdapter(getContext(), fragmentActivity.getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        fragmentActivity = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public void onStart() {
        super.onStart();
        getMacroObjectValues();
        calculateDailyMacros();
        setFields();
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

    public void getMacroObjectValues() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Macros").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Macro macro = snapshot.getValue(Macro.class);
                assert macro != null;
                macroCalories = macro.getCalorieConsumption();
                macroProteins = macro.getProteins();
                macroFats = macro.getFats();
                macroCarbohydrates = macro.getCarbohydrates();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void updateMacrosInDatabase(final double protein, final double fats, final double carbohydrates, final double calories) {
        assert userID != null;
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Macros").child(userID);
        databaseReference.child("proteins").setValue(macroProteins - protein);
        databaseReference.child("fats").setValue(macroFats - fats);
        databaseReference.child("carbohydrates").setValue(macroCarbohydrates - carbohydrates);
        databaseReference.child("calorieConsumption").setValue(macroCalories - calories);
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
