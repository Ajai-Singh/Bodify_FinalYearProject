package com.example.bodify;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bodify.Models.Analysis;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class BreakdownAnalysis extends AppCompatActivity {
    private LineChart lineChart;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakdown_analysis);
        lineChart = findViewById(R.id.reportingChart);
        final ArrayList<Analysis> analyses = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Analysis");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Analysis analysis = userSnapshot.getValue(Analysis.class);
                    assert analysis != null;
                    analyses.add(analysis);
                    populateGraph(analyses);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

    });
    }

    public void populateGraph(ArrayList<Analysis> analyses) {
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        ArrayList<Entry> calorieYValues = new ArrayList<>();
        for (int i = 0; i < analyses.size(); i++) {
            calorieYValues.add(new Entry(i, analyses.get(i).getCalories()));
        }
        ArrayList<Entry> fatYValues = new ArrayList<>();
        for(int i = 0; i < analyses.size(); i++) {
            calorieYValues.add(new Entry(i,analyses.get(i).getFats()));
        }
        ArrayList<Entry> proteinYValues = new ArrayList<>();
        for(int i = 0; i < analyses.size(); i++) {
            calorieYValues.add(new Entry(i,analyses.get(i).getProteins()));
        }
        ArrayList<Entry> carbohydrateYValues = new ArrayList<>();
        for(int i = 0; i < analyses.size(); i++) {
            calorieYValues.add(new Entry(i,analyses.get(i).getCarbohydrates()));
        }

        LineDataSet calorieLineDataSet = new LineDataSet(calorieYValues, "Weekly average calories");
        calorieLineDataSet.setFillAlpha(110);
        LineDataSet fatLineDataSet = new LineDataSet(fatYValues,"Weekly average fat");
        fatLineDataSet.setFillAlpha(110);
        LineDataSet proteinLineDataSet = new LineDataSet(proteinYValues,"Weekly average protein");
        proteinLineDataSet.setFillAlpha(110);
        LineDataSet carbohydrateLineDataSet = new LineDataSet(carbohydrateYValues,"Weekly average carbohydrate");
        carbohydrateLineDataSet.setFillAlpha(110);

        lineDataSets.add(calorieLineDataSet);
        lineDataSets.add(fatLineDataSet);
        lineDataSets.add(proteinLineDataSet);
        lineDataSets.add(carbohydrateLineDataSet);
        LineData lineData = new LineData(lineDataSets);
        lineChart.setData(lineData);
    }
}
