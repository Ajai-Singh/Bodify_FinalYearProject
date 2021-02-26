package com.example.bodify;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.bodify.Models.Analysis;
import com.example.bodify.Models.Macro;
import com.example.bodify.Models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BreakdownAnalysis extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();
    private ImageButton previous, next;
    private Pie pie;
    private TextView fatsTV, carbsTV, proteinsTV, caloriesTV, weightTV, week, header, carbs, fats, proteins, calories, weight;
    private ConstraintLayout constraintLayout;
    private Spinner userSP;
    private AnyChartView anyChartView;
    private final ArrayList<String> dates = new ArrayList<>();
    private ImageButton weights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakdown_analysis);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Breakdown Analysis");
        weights = findViewById(R.id.weightProgression);
        userSP = findViewById(R.id.userSpinner);
        carbs = findViewById(R.id.avgCarbsHeader);
        fats = findViewById(R.id.avgFatsHeader);
        proteins = findViewById(R.id.avgProteinHeader);
        calories = findViewById(R.id.avgCaloriesHeader);
        weight = findViewById(R.id.currentWeightHeader);
        header = findViewById(R.id.wsp);
        Button search = findViewById(R.id.searchUser);
        fatsTV = findViewById(R.id.avgFats);
        carbsTV = findViewById(R.id.avgCarbs);
        proteinsTV = findViewById(R.id.avgProteins);
        caloriesTV = findViewById(R.id.avgCalories);
        weightTV = findViewById(R.id.newWeight);
        ImageButton imageButton = findViewById(R.id.analysisInfo);
        constraintLayout = findViewById(R.id.analysisCL);
        anyChartView = findViewById(R.id.anyChartView);
        previous = findViewById(R.id.minus);
        next = findViewById(R.id.plus);
        week = findViewById(R.id.weekStartingOf);
        pie = AnyChart.pie();
        anyChartView.setChart(pie);
        anyChartView.setBackgroundColor(Color.RED);
        imageButton.setOnClickListener(v -> showInfoSnackBar());
        ArrayList<String> userIDs = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Analysis");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Analysis analysis = userSnapshot.getValue(Analysis.class);
                    assert analysis != null;
                    userIDs.add(analysis.getUserID());
                }
                getSpinnerData(userIDs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BreakdownAnalysis.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        next.setVisibility(View.INVISIBLE);
        previous.setVisibility(View.INVISIBLE);
        fatsTV.setVisibility(View.INVISIBLE);
        carbsTV.setVisibility(View.INVISIBLE);
        proteinsTV.setVisibility(View.INVISIBLE);
        caloriesTV.setVisibility(View.INVISIBLE);
        weightTV.setVisibility(View.INVISIBLE);
        header.setVisibility(View.INVISIBLE);
        anyChartView.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
        carbs.setVisibility(View.INVISIBLE);
        fats.setVisibility(View.INVISIBLE);
        proteins.setVisibility(View.INVISIBLE);
        weight.setVisibility(View.INVISIBLE);
        calories.setVisibility(View.INVISIBLE);
        week.setVisibility(View.INVISIBLE);
        weights.setVisibility(View.INVISIBLE);
        informationSnackBar();
        search.setOnClickListener(v -> {
            if(userSP.getSelectedItemPosition() == 0) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(BreakdownAnalysis.this);
                dlgAlert.setMessage("Error no user selected");
                dlgAlert.setTitle("Error...");
                dlgAlert.setCancelable(true);
                dlgAlert.setPositiveButton("Ok",
                        (dialog, which) -> {
                            dialog.cancel();
                        });
                dlgAlert.create().show();
            } else {
                getNewSpinnerValue(userSP.getSelectedItem().toString());
            }
        });
    }

    public void getSpinnerData(ArrayList<String> userIDs) {
        Set<String> set = new HashSet<>(userIDs);
        userIDs.clear();
        userIDs.addAll(set);
        ArrayList<String> userNames = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    assert user != null;
                    user.setUserID(userSnapshot.getKey());
                    if (userIDs.contains(user.getUserID())) {
                        userNames.add(user.getUserName());
                    }
                }
                populateSpinner(userNames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BreakdownAnalysis.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void populateSpinner(ArrayList<String> userNames) {
        userNames.add(0,"Select user");
        ArrayAdapter<String> adapterNames = new ArrayAdapter<String>(BreakdownAnalysis.this, android.R.layout.simple_spinner_dropdown_item, userNames) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textview = (TextView) view;
                if (position == 0) {
                    textview.setTextColor(Color.GRAY);
                } else {
                    textview.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapterNames.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSP.setAdapter(adapterNames);
        userSP.setOnItemSelectedListener(BreakdownAnalysis.this);
    }

    public void getNewSpinnerValue(String name) {
        next.setVisibility(View.VISIBLE);
        previous.setVisibility(View.VISIBLE);
        fatsTV.setVisibility(View.VISIBLE);
        carbsTV.setVisibility(View.VISIBLE);
        proteinsTV.setVisibility(View.VISIBLE);
        caloriesTV.setVisibility(View.VISIBLE);
        weightTV.setVisibility(View.VISIBLE);
        header.setVisibility(View.VISIBLE);
        anyChartView.setVisibility(View.VISIBLE);
        next.setVisibility(View.VISIBLE);
        carbs.setVisibility(View.VISIBLE);
        fats.setVisibility(View.VISIBLE);
        proteins.setVisibility(View.VISIBLE);
        weight.setVisibility(View.VISIBLE);
        calories.setVisibility(View.VISIBLE);
        week.setVisibility(View.VISIBLE);
        weights.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            String userTag;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    assert user != null;
                    user.setUserID(userSnapshot.getKey());
                    if (user.getUserName().equals(name)) {
                        userTag = user.getUserID();
                        break;
                    }
                }
                getAnalysisWeeks(userTag);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BreakdownAnalysis.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getAnalysisWeeks(String userTag) {
        dates.clear();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Analysis");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Analysis analysis = userSnapshot.getValue(Analysis.class);
                    assert analysis != null;
                    if (analysis.getUserID().equals(userTag)) {
                        dates.add(analysis.getWeekStarting());
                    }
                }
                populateUI(dates,userTag);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BreakdownAnalysis.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void populateUI(ArrayList<String> weeks,String userTag) {
        week.setText(weeks.get(0));
        previous.setOnClickListener(new View.OnClickListener() {
            int currentIndex = weeks.indexOf(week.getText());

            @Override
            public void onClick(View v) {
                if (currentIndex == 0) {
                    currentIndex = weeks.size() - 1;
                } else {
                    currentIndex = currentIndex - 1;
                }
                week.setText(weeks.get(currentIndex));
                readDB(week.getText().toString(),userTag);
                getBarChartInformation(userTag);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            int currentIndex = weeks.indexOf(week.getText());

            @Override
            public void onClick(View v) {
                if (currentIndex == weeks.size() - 1) {
                    currentIndex = 0;
                } else {
                    currentIndex = currentIndex + 1;
                }
                week.setText(weeks.get(currentIndex));
                readDB(week.getText().toString(),userTag);
                getBarChartInformation(userTag);
            }
        });
        readDB(week.getText().toString(),userTag);
        getBarChartInformation(userTag);
    }

    public void getBarChartInformation(String userTag) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Analysis");
        databaseReference.addValueEventListener(new ValueEventListener() {
            final ArrayList<Analysis> analyses = new ArrayList<>();
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Analysis analysis = userSnapshot.getValue(Analysis.class);
                    assert analysis != null;
                    if(analysis.getUserID().equals(userTag)) {
                        analyses.add(analysis);
                    }
                }
                showChart(analyses);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showChart(ArrayList<Analysis> analyses) {
        weights.setOnClickListener(v -> {
            Intent intent = new Intent(BreakdownAnalysis.this, WeightProgression.class);
            intent.putExtra("analyses", analyses);
            startActivity(intent);
        });
    }

    public void readDB(String date,String userTag) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Analysis");
        databaseReference.addValueEventListener(new ValueEventListener() {
            int calories, carbs, proteins, fats;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Analysis analysis = userSnapshot.getValue(Analysis.class);
                    assert analysis != null;
                    if (analysis.getWeekStarting().equals(date) && analysis.getUserID().equals(userTag)) {
                        calories = analysis.getCalories();
                        carbs = analysis.getCarbohydrates();
                        proteins = analysis.getProteins();
                        fats = analysis.getFats();
                        break;
                    }
                }
                populateGraph(calories, carbs, proteins, fats,userTag);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BreakdownAnalysis.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void populateGraph(int calories, int carbs, int proteins, int fats,String userTag) {
        int[] macrosValues = {calories, carbs, proteins, fats};
        String[] macros = {"Calories", "Carbohydrates", "Proteins", "Fats"};
        List<DataEntry> dataEntries = new ArrayList<>();
        for (int i = 0; i < macros.length; i++) {
            dataEntries.add(new ValueDataEntry(macros[i], macrosValues[i]));
        }
        pie.data(dataEntries);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Macros").child(userTag);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Macro macro = snapshot.getValue(Macro.class);
                assert macro != null;
                if (macro.getCarbohydrates() < carbs) {
                    carbsTV.setTextColor(Color.parseColor("#FF0000"));
                } else {
                    carbsTV.setTextColor(Color.BLACK);
                }
                if (macro.getFats() < fats) {
                    fatsTV.setTextColor(Color.parseColor("#FF0000"));
                } else {
                    fatsTV.setTextColor(Color.BLACK);
                }
                if (macro.getProteins() < proteins) {
                    proteinsTV.setTextColor(Color.parseColor("#FF0000"));
                } else {
                    proteinsTV.setTextColor(Color.BLACK);
                }
                if (macro.getCalorieConsumption() < calories) {
                    caloriesTV.setTextColor(Color.parseColor("#FF0000"));
                } else {
                    caloriesTV.setTextColor(Color.BLACK);
                }
                DecimalFormat df = new DecimalFormat("0.00");
                double calorieNegative = macro.getCalorieConsumption() - calories;
                calorieNegative *= 7;
                double newWeight = calorieNegative / 7700;
                DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User").child(userID);
                userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        assert user != null;
                        weightTV.setText(df.format(user.getWeight() - newWeight));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(BreakdownAnalysis.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                caloriesTV.setText(String.valueOf(calories));
                fatsTV.setText(String.valueOf(fats));
                carbsTV.setText(String.valueOf(carbs));
                proteinsTV.setText(String.valueOf(proteins));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BreakdownAnalysis.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showInfoSnackBar() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "If any values are shown in red, You have gone over your recommended amount!", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void informationSnackBar() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Search for user to see data", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

