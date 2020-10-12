package com.example.bodify;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class Tailoring extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tailoring);
        getSupportActionBar().setTitle("Personalize your profile");
        Intent intent = getIntent();
        String receiveUserName = intent.getStringExtra(SignUp.sendUserName);
        String receiveUserEmailAddress = intent.getStringExtra(SignUp.sendUserEmailAddress);
        String receiveUserEmailAdd = intent.getStringExtra(SignUp.sendUserPassword);



    }
}