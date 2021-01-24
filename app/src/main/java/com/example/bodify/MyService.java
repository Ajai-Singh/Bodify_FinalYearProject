package com.example.bodify;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.bodify.Models.Analysis;
import com.example.bodify.Models.Meal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MyService extends Service {
    private static final String TAG = "BOOMBOOMTESTGPS";
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();
    private int calories,fats,proteins,carbohydrates;
    private final ArrayList<String> daysInDB;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public MyService() {
//        final ArrayList<String> daysOfWeek = new ArrayList<>();
//        daysOfWeek.add("Monday");
//        daysOfWeek.add("Tuesday");
//        daysOfWeek.add("Wednesday");
//        daysOfWeek.add("Thursday");
//        daysOfWeek.add("Friday");
//        daysOfWeek.add("Saturday");
//        daysOfWeek.add("Sunday");
        Date date = new Date();
        String currentDate = formatter.format(date);
        //minus current date from when it was created and if the difference is 7
        //error with for loop
        daysInDB = new ArrayList<>();
        //lets go through the days of week database and see what days the food has been logged
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    daysInDB.add(userSnapshot.getKey());
                    Log.i("", "" + daysInDB);
                }
                test(daysInDB);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.i("days", "" + daysInDB);
        //I need to read the DB again but loop i

        //now what i can do is I can for loop the daysInDB and then append I to the next reading of the database
        //I need to think of logic to make sure a full week has gone by with the analysis
        //I think I have figured it out
        //lets say we have an arraylist of meal objects lets access the dates of all of them
        //lets then get the smallest value and the largest value
        //if the difference is 7 then a full week has gone by, but I need to think of the OR logic what if a user just isnt the app for the full week how will I know if a full week has gone by??
        //if(largest - smallest == 7 OR .......) {
        // One week has gone by
        //create analysis object
        // }

    }

//    public void createAnalysis(Analysis analysis) {

//    }


    public void test(ArrayList<String> daysInDB) {
        final ArrayList<String> dates = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek");
        for (int i = 0; i < daysInDB.size(); i++) {
            Log.i("i", "" + daysInDB.get(i));
            databaseReference.child(daysInDB.get(i)).addValueEventListener(new ValueEventListener() {


                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        Meal meal = userSnapshot.getValue(Meal.class);
                        assert meal != null;
                        if (meal.getUserID().equalsIgnoreCase(userID)) {
                            dates.add(meal.getDate());
                            calories += meal.getCalories() * meal.getNumberOfServings();
                            fats += meal.getItemTotalFat() * meal.getNumberOfServings();
                            carbohydrates += meal.getItemTotalCarbohydrates() * meal.getNumberOfServings();
                            proteins += meal.getItemProtein() * meal.getNumberOfServings();
                        }
                    }
                    //do division operator here
                    dates(dates,calories / 7,fats / 7,carbohydrates / 7,proteins / 7);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i("B", "Error occurred: " + error.getMessage());
                }
            });
        }
    }

    public void dates(ArrayList<String> dates,int calories,int fats,int carbohydrates,int proteins) {
        //need to find averages
        ArrayList<Integer> intDates = new ArrayList<>();
        for(int i = 0; i < dates.size(); i++) {
            intDates.add(Integer.parseInt(dates.get(i).substring(0,2)));
        }
        Log.i("newDates","" + intDates);
        //if the if statement gets executed that means one week has gone by now I just need to find the OR clause to because the user might not use the app much
        int b = Collections.max(intDates);
        int s = Collections.min(intDates);
        Log.i("D","" + (b - s));
        if(Collections.max(intDates) - Collections.min(intDates) == 6) {
            Analysis analysis = new Analysis(calories,fats,carbohydrates,proteins,userID);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Analysis");
            databaseReference.push().setValue(analysis).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.i("A", "Successfully saved");
                }
            }).addOnFailureListener(e -> Log.i("B", "Error occurred: " + e.getMessage()));
        } else {
            //one week has not passed yet
            Log.i("error","7 days have not passed yet");
        }
    }
}