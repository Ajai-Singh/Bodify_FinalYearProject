package com.example.bodify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import java.util.ArrayList;
import java.util.List;

public class Breakdown extends Fragment {
    AnyChartView anyChartView;
    String [] months = {"jan","feb","Mar"};
    int[] earnings = {500,800,2000};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main,container,false);
        anyChartView = view.findViewById(R.id.pieChart);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setUpPieChart();
    }

    public void setUpPieChart() {
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntryList = new ArrayList<>();
        for(int i = 0; i < months.length; i ++) {
            dataEntryList.add(new ValueDataEntry(months[i],earnings[i]));
        }
        pie.data(dataEntryList);
        anyChartView.setChart(pie);
    }
}