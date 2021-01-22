package com.example.bodify;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import java.util.ArrayList;

public class Analysis extends AppCompatActivity {


    LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        lineChart = findViewById(R.id.reportingChart);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        //Im going to want 4 y values eg. calorie, fat, protein, carbohydrate
        ArrayList<Entry> calorieYValues = new ArrayList<>();
        ArrayList<Entry> fatYValues = new ArrayList<>();
        ArrayList<Entry> proteinYValues = new ArrayList<>();
        ArrayList<Entry> carbohydrateYValues = new ArrayList<>();
        //Then I am going to want to add the weekly average for all these yValues to the arraylist also passing in a new Entry
        //They will also have there own line dataset
        LineDataSet calorieLineDataSet = new LineDataSet(calorieYValues,"Weekly average calories");
        calorieLineDataSet.setFillAlpha(110);
        LineDataSet fatLineDataSet = new LineDataSet(fatYValues,"Weekly average fat");
        fatLineDataSet.setFillAlpha(110);
        LineDataSet proteinLineDataSet = new LineDataSet(proteinYValues,"Weekly average protein");
        proteinLineDataSet.setFillAlpha(110);
        LineDataSet carbohydrateLineDataSet = new LineDataSet(carbohydrateYValues,"Weekly average carbohydrate");
        carbohydrateLineDataSet.setFillAlpha(110);

        ArrayList<Entry> yValues = new ArrayList<>();
        yValues.add(new Entry(0,50f));
        yValues.add(new Entry(1,60f));
        yValues.add(new Entry(2,70f));
        yValues.add(new Entry(3,80f));
        yValues.add(new Entry(4,90f));
        yValues.add(new Entry(5,10f));

        LineDataSet lineDataSet = new LineDataSet(yValues,"set 1");
        lineDataSet.setFillAlpha(110);

        dataSets.add(lineDataSet);
        dataSets.add(calorieLineDataSet);
        dataSets.add(fatLineDataSet);
        dataSets.add(proteinLineDataSet);
        dataSets.add(carbohydrateLineDataSet);

        LineData lineData = new LineData(dataSets);
        lineChart.setData(lineData);

    }
}