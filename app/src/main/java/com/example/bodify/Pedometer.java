package com.example.bodify;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

public class Pedometer extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private TextView numberOfSteps;
    private CircularProgressBar circularProgressBar;
    boolean running = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedometer);
        numberOfSteps = findViewById(R.id.stepTextView);
        circularProgressBar = findViewById(R.id.circularBar);
        Button reset = findViewById(R.id.resetSteps);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberOfSteps.setText("0");
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        running = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null) {
            sensorManager.registerListener(this,countSensor,SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(Pedometer.this, "Sensor not found!",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        running = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(running) {
            int steps = Integer.parseInt(String.valueOf(event.values[0]));
            numberOfSteps.setText(String.valueOf(event.values[0]));
            circularProgressBar.setProgressWithAnimation(steps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void resetSteps() {

    }
}