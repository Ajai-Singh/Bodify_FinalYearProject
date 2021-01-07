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
import com.example.bodify.Models.MacroCopy;
import com.example.bodify.Models.Meal;
import com.example.bodify.R;
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

public class Remember extends Fragment {
    private AnyChartView anyChartView;
    private TextView caloriesTV, fatsTV, proteinsTV, carbohydratesTV;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_monday, container, false);
        anyChartView = view.findViewById(R.id.pieChart);
        caloriesTV = view.findViewById(R.id.dailyCaloriesLeftTextView);
        fatsTV = view.findViewById(R.id.dailyFatsTV);
        proteinsTV = view.findViewById(R.id.dailyProteinsTV);
        carbohydratesTV = view.findViewById(R.id.dailyCarbsTV);
        getValuesForMacroCopy();
        getMacroCopyValues();
        setUIComponents();
        setUpPieChart();
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
                double globalCalories = macro.getCalorieConsumption();
                double globalCarbohydrates = macro.getCarbohydrates();
                double globalProteins = macro.getProteins();
                double globalFats = macro.getFats();
                createMacroCopyInDatabase(globalCalories, globalCarbohydrates, globalFats, globalProteins);
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
                    Toast.makeText(getContext(), "Successfully saved", Toast.LENGTH_SHORT).show();
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
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TemporaryMacros").child("Thursday");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    MacroCopy macroCopy = userSnapshot.getValue(MacroCopy.class);
                    assert macroCopy != null;
                    if (macroCopy.getUserID().equals(userID)) {
                        caloriesTV.setText(String.valueOf(macroCopy.getCalorieConsumption()));
                        fatsTV.setText(String.valueOf(macroCopy.getFatConsumption()));
                        proteinsTV.setText(String.valueOf(macroCopy.getProteinConsumption()));
                        carbohydratesTV.setText(String.valueOf(macroCopy.getCarbohydrateConsumption()));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred!" + error.getMessage(), Toast.LENGTH_SHORT).show();
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
                        loggedCalories = loggedCalories + meal.getCalories() * meal.getNumberOfServings();
                        loggedProteins = loggedProteins + meal.getItemProtein() * meal.getNumberOfServings();
                        loggedFats = loggedFats + meal.getItemTotalFat() * meal.getNumberOfServings();
                        loggedCarbohydrates = loggedCarbohydrates + meal.getItemTotalCarbohydrates() * meal.getNumberOfServings();
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
//                Log.i("A", "A" + loggedCalories);
//                Log.i("B", "B" + loggedProteins);
//                Log.i("C", "C" + loggedFats);
//                Log.i("D", "D" + loggedCarbohydrates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //problem with this method
    //okay so no error now but update is not happening, very close now
    public void updateMacroCopy(final double calories, final double proteins, final double fats, final double carbohydrates) {
        assert userID != null;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TemporaryMacros").child("Thursday").child(userID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            double usersCalories, usersCarbohydrates, usersProteins, usersFats;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MacroCopy macroCopy = snapshot.getValue(MacroCopy.class);
                assert macroCopy != null;

                usersCalories = macroCopy.getCalorieConsumption();
                usersCarbohydrates = macroCopy.getCarbohydrateConsumption();
                usersProteins = macroCopy.getProteinConsumption();
                usersFats = macroCopy.getFatConsumption();
                DatabaseReference updateDBReference = FirebaseDatabase.getInstance().getReference("TemporaryMacros").child("Thursday").child(userID);
                updateDBReference.child("calorieConsumption").setValue(usersCalories - calories);
                updateDBReference.child("carbohydrateConsumption").setValue(usersCarbohydrates - carbohydrates);
                updateDBReference.child("fatConsumption").setValue(usersFats - fats);
                updateDBReference.child("proteinConsumption").setValue(usersProteins - proteins);


//                Log.i("A", "A" + usersCalories);
//                Log.i("B", "B" + usersCarbohydrates);
//                Log.i("C", "C" + usersFats);
//                Log.i("D", "D" + usersProteins);
//                updateDBReference.child("calorieConsumption").setValue(1 - calories);
//                updateDBReference.child("carbohydrateConsumption").setValue(1 - carbohydrates);
//                updateDBReference.child("fatConsumption").setValue(1 - fats);
//                updateDBReference.child("proteinConsumption").setValue(1 - proteins);
//                Log.i("A", "A2" + usersCalories);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}




