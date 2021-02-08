package com.example.bodify;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.bodify.Models.Analysis;
import com.example.bodify.Models.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
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
import java.util.ArrayList;
import java.util.Objects;

public class WeightProgression extends AppCompatActivity {
    private ArrayList<Analysis> analyses = new ArrayList<>();
    private final ArrayList<BarEntry> barEntries = new ArrayList<>();
    private final ArrayList<String> xLabels = new ArrayList<>();
    private BarChart barChart;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final String userID = firebaseAuth.getUid();
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weight_progression);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Weight Progression");
        constraintLayout = findViewById(R.id.wwcl);
        barChart = findViewById(R.id.weightProgressionGraph);
        barChart.setTouchEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDoubleTapToZoomEnabled(false);
        analyses = (ArrayList<Analysis>) getIntent().getSerializableExtra("analyses");
        getStartingInformation();
        showSnackBar();
    }

    public void getStartingInformation() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                assert user != null;
                showBarChart(user.getWeight(),user.getDate());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeightProgression.this,"Error occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showBarChart(double weight,String date) {
        barEntries.add(new BarEntry(0f, (float) weight));
        xLabels.add(date);
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

    public void showSnackBar() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Update your weight accordingly", Snackbar.LENGTH_LONG).setAction(
                "Settings", v -> {
                    Intent intent = new Intent(WeightProgression.this, Settings.class);
                    startActivity(intent);
                    finish();
                }
        );
        snackbar.show();
    }
}