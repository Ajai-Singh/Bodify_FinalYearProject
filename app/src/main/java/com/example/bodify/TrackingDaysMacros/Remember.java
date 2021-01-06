package com.example.bodify.TrackingDaysMacros;

import android.os.Bundle;
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
import com.example.bodify.R;
import com.github.mikephil.charting.charts.PieChart;
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
        setUpTextFields();
        return view;
    }

    public void setUpTextFields() {
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
                macroFats = macro.getFats();
                macroProteins = macro.getProteins();
                macroCarbohydrates = macro.getCarbohydrates();
                setUpPieChart(macroFats,macroProteins,macroCarbohydrates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setUpPieChart(double fats,double proteins,double carbohydrates) {
        String[] macros = new String[]{"Fat", "Protein", "Carbs"};
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();
        dataEntries.clear();
        double[] macrosValues = {fats, proteins, carbohydrates};
        for (int i = 0; i < macros.length; i++) {
            dataEntries.add(new ValueDataEntry(macros[i], macrosValues[i]));
        }
        pie.data(dataEntries);
        anyChartView.setChart(pie);
    }
}

