package com.example.bodify;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.bodify.Models.Analysis;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BreakdownAnalysis extends AppCompatActivity {
    private AnyChartView anyChartView;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();
    private ImageButton previous,next;
    private TextView week;
    private final ArrayList<String> weeks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakdown_analysis);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Breakdown Analysis");
        anyChartView = findViewById(R.id.anyChartView);
        previous = findViewById(R.id.minus);
        next = findViewById(R.id.plus);
        week = findViewById(R.id.weekStartingOf);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Analysis");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Analysis analysis = userSnapshot.getValue(Analysis.class);
                    assert analysis != null;
                    if (analysis.getUserID().equals(userID)) {
                        weeks.add(analysis.getWeekStarting());
                    }
                }
                populateUI(weeks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BreakdownAnalysis.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void populateUI(ArrayList<String> weeks) {
        Log.i("weeks", "" + weeks);
        week.setText(weeks.get(0));
        //look into possibly ordering dates based on oldest to newest
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
                readDB(week.getText().toString());
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
                readDB(week.getText().toString());
            }
        });
        readDB(week.getText().toString());
    }

    public void readDB(String date) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Analysis");
        databaseReference.addValueEventListener(new ValueEventListener() {
            int calories, carbs, proteins, fats;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Analysis analysis = userSnapshot.getValue(Analysis.class);
                    assert analysis != null;
                    if (analysis.getWeekStarting().equals(date)) {
                        calories = analysis.getCalories();
                        carbs = analysis.getCarbohydrates();
                        proteins = analysis.getProteins();
                        fats = analysis.getFats();
                        break;
                    }
                }
                populateGraph(calories, carbs, proteins, fats);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void populateGraph(int calories,int carbs,int proteins,int fats) {
        Log.i("calories","" + calories);
        Log.i("carbs","" + carbs);
        Log.i("proteins","" + proteins);
        Log.i("fats","" + fats);
        int[] macrosValues = {calories, carbs, proteins,fats};
        String[] macros = {"Calories", "Carbohydrates", "Proteins","Fats"};
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();
        for (int i = 0; i < macros.length; i++) {
            dataEntries.add(new ValueDataEntry(macros[i], macrosValues[i]));
        }
        pie.data(dataEntries);
        anyChartView.setChart(pie);
    }
}

