package com.example.bodify;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class PedometerFeature extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer_feature);
        getSupportActionBar().setTitle("Pedometer");

    }
}