package com.example.bodify;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.bodify.Models.Analysis;
import com.example.bodify.Models.Macro;
import com.example.bodify.Models.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class WeightProgression extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ArrayList<Analysis> analyses = new ArrayList<>();
    private final ArrayList<BarEntry> barEntries = new ArrayList<>();
    private final ArrayList<String> xLabels = new ArrayList<>();
    private BarChart barChart;
    private ConstraintLayout constraintLayout;
    private Spinner spinner;
    private YAxis leftAxis;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weight_progression);
        Objects.requireNonNull(getSupportActionBar()).setTitle("User journey");
        spinner = findViewById(R.id.progressionSpinner);
        Button search = findViewById(R.id.progressionSearch);
        constraintLayout = findViewById(R.id.wwcl);
        barChart = findViewById(R.id.weightProgressionGraph);
        barChart.setTouchEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        analyses = (ArrayList<Analysis>) getIntent().getSerializableExtra("analyses");
        Collections.sort(analyses, (o1, o2) -> o1.getWeekStarting().compareTo(o2.getWeekStarting()));
        leftAxis = barChart.getAxisLeft();
        populateSpinner();
        search.setOnClickListener(v -> {
            if (spinner.getSelectedItemPosition() == 0) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(WeightProgression.this);
                dlgAlert.setMessage("Error no value selected");
                dlgAlert.setTitle("Error...");
                dlgAlert.setCancelable(true);
                dlgAlert.setPositiveButton("Ok",
                        (dialog, which) -> {
                            dialog.cancel();
                        });
                dlgAlert.create().show();
            } else {
                switch (spinner.getSelectedItemPosition()) {
                    case 1:
                        leftAxis.removeAllLimitLines();
                        Intent intent = getIntent();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(Objects.requireNonNull(intent.getStringExtra("userId")));
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User user = snapshot.getValue(User.class);
                                if (user != null) {
                                    getWeightProgression(user.getWeight());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                errorOccurred(error.getMessage());
                            }
                        });
                        break;
                    case 2:
                        leftAxis.removeAllLimitLines();
                        getFatProgression();
                        break;
                    case 3:
                        leftAxis.removeAllLimitLines();
                        getCarbProgression();
                        break;
                    case 4:
                        leftAxis.removeAllLimitLines();
                        getProteinProgression();
                        break;
                }


            }
        });
    }

    public void populateSpinner() {
        ArrayList<String> spinnerValues = new ArrayList<>();
        spinnerValues.add("Select option!");
        spinnerValues.add("Weight progression");
        spinnerValues.add("Fat progression AVG");
        spinnerValues.add("Carb progression AVG");
        spinnerValues.add("Protein progression AVG");
        ArrayAdapter<String> adapterNames = new ArrayAdapter<String>(WeightProgression.this, android.R.layout.simple_spinner_dropdown_item, spinnerValues) {
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
        spinner.setAdapter(adapterNames);
        spinner.setOnItemSelectedListener(WeightProgression.this);
    }

    public void getWeightProgression(double weight) {
        barEntries.clear();
        xLabels.clear();
        barChart.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        if (Objects.equals(mAuth.getUid(), intent.getStringExtra("userId"))) {
            Snackbar snackbar = Snackbar.make(constraintLayout, "Update your weight accordingly", Snackbar.LENGTH_LONG).setAction(
                    "Settings", v -> {
                        Intent i = new Intent(WeightProgression.this, Settings.class);
                        startActivity(i);
                        finish();
                    }
            );
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();
        }
        Collections.sort(analyses, new Comparator<Analysis>() {
            @SuppressLint("SimpleDateFormat")
            final DateFormat f = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            public int compare(Analysis o1, Analysis o2) {
                try {
                    return Objects.requireNonNull(f.parse(o1.getWeekStarting())).compareTo(f.parse(o2.getWeekStarting()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
        barEntries.add(new BarEntry(0f, (float) weight));
        xLabels.add("Starting KGs");
        for (int i = 0; i < analyses.size(); i++) {
            float o = (float) i + 1f;
            barEntries.add(new BarEntry(o, (float) analyses.get(i).getWeight()));
            xLabels.add(analyses.get(i).getWeekStarting());
        }
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
        BarDataSet barDataSet = new BarDataSet(barEntries, "Weight progression");
        BarData barData = new BarData(barDataSet);
        barChart.clear();
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart.invalidate();
    }

    public void getFatProgression() {
        barEntries.clear();
        xLabels.clear();
        barChart.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Macros").child(Objects.requireNonNull(intent.getStringExtra("userId")));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Macro macro = snapshot.getValue(Macro.class);
                assert macro != null;
                barEntries.add(new BarEntry(0f, (float) macro.getFats()));
                xLabels.add("Allowance");
                for (int i = 0; i < analyses.size(); i++) {
                    float o = (float) i + 1f;
                    barEntries.add(new BarEntry(o, (float) analyses.get(i).getFats()));
                    xLabels.add(analyses.get(i).getWeekStarting());
                }
                XAxis xAxis = barChart.getXAxis();
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
                LimitLine limitLine = new LimitLine((float) macro.getFats(), "Max Allowance");
                limitLine.setLineWidth(4f);
                limitLine.enableDashedLine(10f, 10f, 0f);
                limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                limitLine.setTextSize(10f);
                leftAxis.addLimitLine(limitLine);
                BarDataSet barDataSet = new BarDataSet(barEntries, "Fat progression");
                BarData barData = new BarData(barDataSet);
                barChart.clear();
                barChart.setData(barData);
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(16f);
                barChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorOccurred(error.getMessage());
            }
        });
    }

    public void getCarbProgression() {
        barEntries.clear();
        xLabels.clear();
        barChart.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Macros").child(Objects.requireNonNull(intent.getStringExtra("userId")));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Macro macro = snapshot.getValue(Macro.class);
                assert macro != null;
                barEntries.add(new BarEntry(0f, (float) macro.getCarbohydrates()));
                xLabels.add("Allowance");
                for (int i = 0; i < analyses.size(); i++) {
                    float o = (float) i + 1f;
                    barEntries.add(new BarEntry(o, (float) analyses.get(i).getCarbohydrates()));
                    xLabels.add(analyses.get(i).getWeekStarting());
                }
                XAxis xAxis = barChart.getXAxis();
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
                LimitLine limitLine = new LimitLine((float) macro.getCarbohydrates(), "Max Allowance");
                limitLine.setLineWidth(4f);
                limitLine.enableDashedLine(10f, 10f, 0f);
                limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                limitLine.setTextSize(10f);
                leftAxis.addLimitLine(limitLine);
                BarDataSet barDataSet = new BarDataSet(barEntries, "Carbohydrate progression");
                BarData barData = new BarData(barDataSet);
                barChart.clear();
                barChart.setData(barData);
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(16f);
                barChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorOccurred(error.getMessage());
            }
        });
    }

    public void getProteinProgression() {
        barEntries.clear();
        xLabels.clear();
        barChart.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Macros").child(Objects.requireNonNull(intent.getStringExtra("userId")));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Macro macro = snapshot.getValue(Macro.class);
                assert macro != null;
                barEntries.add(new BarEntry(0f, (float) macro.getProteins()));
                xLabels.add("Allowance");
                for (int i = 0; i < analyses.size(); i++) {
                    float o = (float) i + 1f;
                    barEntries.add(new BarEntry(o, (float) analyses.get(i).getProteins()));
                    xLabels.add(analyses.get(i).getWeekStarting());
                }
                XAxis xAxis = barChart.getXAxis();
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
                LimitLine limitLine = new LimitLine((float) macro.getProteins(), "Max Allowance");
                limitLine.setLineWidth(4f);
                limitLine.enableDashedLine(10f, 10f, 0f);
                limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                limitLine.setTextSize(10f);
                leftAxis.addLimitLine(limitLine);
                BarDataSet barDataSet = new BarDataSet(barEntries, "Protein progression");
                BarData barData = new BarData(barDataSet);
                barChart.clear();
                barChart.setData(barData);
                barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(16f);
                barChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                errorOccurred(error.getMessage());
            }
        });
    }

    public void errorOccurred(String errorMessage) {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Error occurred: " + errorMessage, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, BreakdownAnalysis.class));
    }
}