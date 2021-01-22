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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyService extends Service {
    private static final String TAG = "BOOMBOOMTESTGPS";
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();

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
        Log.i("hey", "Hello");
        final ArrayList<String> daysOfWeek = new ArrayList<>();
        daysOfWeek.add("Monday");
        daysOfWeek.add("Tuesday");
        daysOfWeek.add("Wednesday");
        daysOfWeek.add("Thursday");
        daysOfWeek.add("Friday");
        daysOfWeek.add("Saturday");
        daysOfWeek.add("Sunday");
        Date date = new Date();
        String currentDate = formatter.format(date);
        //minus current date from when it was created and if the difference is 7
        for (int i = 0; i < daysOfWeek.size(); i++) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek").child(daysOfWeek.get(i));
            databaseReference.addValueEventListener(new ValueEventListener() {
                int calories,fats,proteins,carbohydrates;
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot userSnapshot : snapshot.getChildren()) {
                        Meal meal = userSnapshot.getValue(Meal.class);
                        assert meal != null;
                        if (meal.getUserID().equals(userID)) {
                            String mealCreationDate = meal.getDate();
                            String cd = currentDate.substring(0, 2);
                            String mcd = mealCreationDate.substring(0, 2);
                            int intCd = Integer.parseInt(cd);
                            int intMcd = Integer.parseInt(mcd);
                            if (intCd - intMcd == 7) {
                                //One week has gone by if the result is == 7
                                //Now I need to remove this
                                calories += meal.getCalories() * meal.getNumberOfServings();
                                fats += meal.getItemTotalFat() * meal.getNumberOfServings();
                                proteins += meal.getItemProtein() * meal.getNumberOfServings();
                                carbohydrates += meal.getItemTotalCarbohydrates() * meal.getNumberOfServings();
                            } else {
                                Log.i("D","7 days have not passed since creation");
                            }
                        }
                    }
                    Analysis analysis = new Analysis(calories,fats,carbohydrates,proteins);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Analysis");
                    databaseReference.push().setValue(analysis).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.i("A","Successfully saved");
                        }
                    }).addOnFailureListener(e -> Log.i("B","Error occurred: " + e.getMessage()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i("B","Error occurred: " + error.getMessage());
                }
            });
        }
    }
}