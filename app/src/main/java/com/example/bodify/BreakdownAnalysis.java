package com.example.bodify;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.bodify.Models.Analysis;
import com.example.bodify.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BreakdownAnalysis extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private AnyChartView anyChartView;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();
    private ImageButton previous,next;
    private TextView week;
    private final ArrayList<String> weeks = new ArrayList<>();
    private Pie pie;
    private Spinner userSpinner;
    private Button updateButton,searchButton;
    private ArrayList<User> users = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breakdown_analysis);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Breakdown Analysis");
        userSpinner = findViewById(R.id.showAllUsersSpinner);
        searchButton = findViewById(R.id.searchUser);
        updateButton = findViewById(R.id.updateWeight);
        anyChartView = findViewById(R.id.anyChartView);
        previous = findViewById(R.id.minus);
        next = findViewById(R.id.plus);
        week = findViewById(R.id.weekStartingOf);
        pie = AnyChart.pie();
        anyChartView.setChart(pie);
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User");
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    assert user != null;
                    user.setUserID(userSnapshot.getKey());
                    users.add(user);
                }
                getSpinnerData(users);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
        // Pie pie = AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();
        for (int i = 0; i < macros.length; i++) {
            dataEntries.add(new ValueDataEntry(macros[i], macrosValues[i]));
        }
        pie.data(dataEntries);
        //anyChartView.setChart(pie);
    }

    public void getSpinnerData(ArrayList<User> users) {
        ArrayList<String> userNames = new ArrayList<>();
        for(int i = 0; i < users.size(); i++) {
            userNames.add(users.get(i).getUserName());
        }
        ArrayAdapter<String> adapterNames = new ArrayAdapter<String>(BreakdownAnalysis.this, android.R.layout.simple_spinner_dropdown_item, userNames);
        adapterNames.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(adapterNames);
        userSpinner.setOnItemSelectedListener(BreakdownAnalysis.this);

        //makes certain rows unclickable.

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

