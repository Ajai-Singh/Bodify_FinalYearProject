package com.example.bodify;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.bodify.Models.Analysis;
import com.example.bodify.Models.Meal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

public class MyService extends Service {
    private static final String TAG = "BOOMBOOMTESTGPS";
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();
    private int calories, fats, proteins, carbohydrates;
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
        Date date = new Date();
        daysInDB = new ArrayList<>();
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
    }

    public void test(ArrayList<String> daysInDB) {
        final ArrayList<String> dates = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek");
        for (int i = 0; i < daysInDB.size(); i++) {
            databaseReference.child(daysInDB.get(i)).addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
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
                    try {
                        dates(dates, calories / 7, fats / 7, carbohydrates / 7, proteins / 7);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i("B", "Error occurred: " + error.getMessage());
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void dates(ArrayList<String> dates, int calories, int fats, int carbohydrates, int proteins) throws ParseException {
        final ArrayList<String> daysOfWeek = new ArrayList<>();
        daysOfWeek.add("Monday");
        daysOfWeek.add("Tuesday");
        daysOfWeek.add("Wednesday");
        daysOfWeek.add("Thursday");
        daysOfWeek.add("Friday");
        daysOfWeek.add("Saturday");
        daysOfWeek.add("Sunday");
        ArrayList<Integer> intDates = new ArrayList<>();
        for (int i = 0; i < dates.size(); i++) {
            intDates.add(Integer.parseInt(dates.get(i).substring(0, 2)));
        }
        LocalTime refTime = LocalTime.of(23, 59, 59);
        LocalTime localTime = LocalTime.now();
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        dateFormat.format(cal.getTime());
        Date currentWeekDay = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
        Log.i("time", "" + dateFormat.format(cal.getTime()));
        //getWeekStarting now
        ArrayList<Integer> test = new ArrayList<>();
        for (int i = 0; i < dates.size(); i++) {
            test.add(Integer.parseInt(dates.get(i).substring(0, 2)));

        }
        int minIndex = test.indexOf(Collections.min(test));
        Log.i("test", "" + test);
        Log.i("test1", "" + minIndex);
        dates.get(test.indexOf(Collections.min(test)));
        //we now have the earliest record date but now we need to find the day of this date
        Log.i("test2", "" + dates.get(test.indexOf(Collections.min(test))));
        String input_date = dates.get(test.indexOf(Collections.min(test)));
        SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy");
        Date dt1 = format1.parse(input_date);
        DateFormat format2 = new SimpleDateFormat("EEEE");
        String finalDay = format2.format(dt1);
        String b = null;
        String weekStartingOf;
        if (!finalDay.equalsIgnoreCase("Monday")) {
            for(int i = 0; i < daysOfWeek.size(); i++) {
                if(daysOfWeek.get(i).equalsIgnoreCase(finalDay)) {
                    String a = daysOfWeek.get(i);
                    b = String.valueOf(daysOfWeek.indexOf(a));
                    //to get the final date minus b from the range of the date of the smallest value

                }
            }
        }
        Log.i("day", "" + finalDay);
        if (Collections.max(intDates) - Collections.min(intDates) == 7 || simpleDateformat.format(currentWeekDay).equalsIgnoreCase("Sunday") && localTime.isAfter(refTime)) {
            Analysis analysis = new Analysis(calories, fats, carbohydrates, proteins, userID, "");
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Analysis");
            databaseReference.child("").setValue(analysis).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.i("A", "Successfully saved");
                }
            }).addOnFailureListener(e -> Log.i("B", "Error occurred: " + e.getMessage()));
        } else {
            //one week has not passed yet
            Log.i("error", "7 days have not passed yet");
        }
    }
}