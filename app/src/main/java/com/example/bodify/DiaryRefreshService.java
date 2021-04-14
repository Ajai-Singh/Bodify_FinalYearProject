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
import com.example.bodify.Models.Macro;
import com.example.bodify.Models.Meal;
import com.example.bodify.Models.User;
import com.example.bodify.TrackingDaysMeals.FridayMeals;
import com.example.bodify.TrackingDaysMeals.MondayMeals;
import com.example.bodify.TrackingDaysMeals.SaturdayMeals;
import com.example.bodify.TrackingDaysMeals.SundayMeals;
import com.example.bodify.TrackingDaysMeals.ThursdayMeals;
import com.example.bodify.TrackingDaysMeals.TuesdayMeals;
import com.example.bodify.TrackingDaysMeals.WednesdayMeals;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class DiaryRefreshService extends Service {
    private static final String TAG = "BOOMBOOMTESTGPS";
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();
    private int calories, fats, proteins, carbohydrates;
    private ArrayList<String> daysInDB;
    private ArrayList<String> dates;

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

    public DiaryRefreshService() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(userID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    getUserMacroCalories(user.getWeight());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getUserMacroCalories(double weight) {
        assert userID != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Macros").child(userID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Macro macro = snapshot.getValue(Macro.class);
                if (macro != null) {
                    getDaysInDB(macro.getCalorieConsumption(), weight);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("error", ":" + error.getMessage());
            }
        });
    }

    public void getDaysInDB(double calories, double weight) {
        daysInDB = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    daysInDB.add(userSnapshot.getKey());
                }
                test(daysInDB, calories, weight);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("error", ":" + error.getMessage());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void test(ArrayList<String> daysInDB, double dbCalories, double weight) {
        dates = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    for(DataSnapshot userSnapshot2 : userSnapshot.getChildren()) {
                        Meal meal = userSnapshot2.getValue(Meal.class);
                        assert meal != null;
                        if (meal.getUserID().equalsIgnoreCase(mAuth.getUid())) {
                            dates.add(meal.getDate());
                            calories += meal.getCalories() * meal.getNumberOfServings();
                            fats += meal.getItemTotalFat() * meal.getNumberOfServings();
                            carbohydrates += meal.getItemTotalCarbohydrates() * meal.getNumberOfServings();
                            proteins += meal.getItemProtein() * meal.getNumberOfServings();
                        }
                    }
                }
                try {
                    dates(dates, calories / 7, fats / 7, carbohydrates / 7, proteins / 7,daysInDB, dbCalories, weight);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("parse exception", "" + error.getMessage());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void dates(ArrayList<String> dates, int calories, int fats, int carbohydrates, int proteins, ArrayList<String> daysInDB, double dbCalories, double weight) throws ParseException {
        final ArrayList<String> daysOfWeek = new ArrayList<>();
        daysOfWeek.add("Monday");
        daysOfWeek.add("Tuesday");
        daysOfWeek.add("Wednesday");
        daysOfWeek.add("Thursday");
        daysOfWeek.add("Friday");
        daysOfWeek.add("Saturday");
        daysOfWeek.add("Sunday");
        ArrayList<Integer> test = new ArrayList<>();
        for (int i = 0; i < dates.size(); i++) {
            test.add(Integer.parseInt(dates.get(i).substring(0, 2)));
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date smallestDate = null;
        Date largestDate = null;
        Date finalSmallestDate;
        Date finalLargestDate;
        if (!dates.isEmpty() && dates.size() > 1) {
            smallestDate = simpleDateFormat.parse(dates.get(0));
            largestDate = simpleDateFormat.parse(dates.get(0));
            for (int i = 0; i < dates.size(); i++) {
                finalSmallestDate = simpleDateFormat.parse(dates.get(i));
                finalLargestDate = simpleDateFormat.parse(dates.get(i));
                assert finalSmallestDate != null;
                if (finalSmallestDate.before(smallestDate)) {
                    smallestDate = finalSmallestDate;
                }
                assert finalLargestDate != null;
                if (finalLargestDate.after(largestDate)) {
                    largestDate = finalLargestDate;
                }
            }
        }
        int positionOfLastDate = 0;
        int positionOfEarliestDate = 0;
        if (smallestDate != null && largestDate != null) {
            for (int i = 0; i < test.size(); i++) {
                if (test.get(i) == Integer.parseInt(simpleDateFormat.format(smallestDate).substring(0, 2))) {
                    int smallestRecord = test.get(i);
                    positionOfEarliestDate = test.indexOf(smallestRecord);
                } else if (test.get(i) == Integer.parseInt(simpleDateFormat.format(largestDate).substring(0, 2))) {
                    int largestRecord = test.get(i);
                    positionOfLastDate = test.indexOf(largestRecord);
                }
            }
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate ld = LocalDate.parse(dates.get(positionOfEarliestDate), dtf);
            LocalDate ld1 = LocalDate.parse(dates.get(positionOfLastDate), dtf);
            long daysBetween = Duration.between(ld.atStartOfDay(), ld1.atStartOfDay()).toDays();
            String input_date = dates.get(positionOfEarliestDate);
            Log.i("input_date", "input_date" + input_date);
            Date dt1 = simpleDateFormat.parse(input_date);
            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("EEEE");
            assert dt1 != null;
            String finalDay = df.format(dt1);
            String b = "0";
            String weekStartingOf;
            if (!finalDay.equalsIgnoreCase("Monday")) {
                for (int i = 0; i < daysOfWeek.size(); i++) {
                    if (daysOfWeek.get(i).equalsIgnoreCase(finalDay)) {
                        String a = daysOfWeek.get(i);
                        b = String.valueOf(daysOfWeek.indexOf(a));
                        Log.i("difference in days", "" + b);
                        break;
                    }
                }
            }
            weekStartingOf = dates.get(positionOfEarliestDate);
            String indexWeekStartingOf = weekStartingOf.substring(0, 2);
            StringBuilder stringBuffer = new StringBuilder(weekStartingOf);
            stringBuffer.replace(0, 2, String.valueOf(Integer.parseInt(indexWeekStartingOf) - Integer.parseInt(b)));
            DecimalFormat decimalFormat = new DecimalFormat("0.00");
            double calorieNegative = dbCalories - calories;
            calorieNegative *= 7;
            double newWeight = calorieNegative / 7700;
            double finalWeight = Double.parseDouble(decimalFormat.format(weight - newWeight));
            Analysis analysis = new Analysis(calories, fats, carbohydrates, proteins, userID, String.valueOf(stringBuffer), finalWeight);
            if (daysBetween >= 7) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Analysis");
                assert userID != null;
                Log.i("A", "count");
                databaseReference.child(UUID.randomUUID().toString()).setValue(analysis).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.i("A", "Successfully saved");
                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("DayOfWeek");
                        for (int i = 0; i < daysInDB.size(); i++) {
                            int finalI = i;
                            databaseReference1.child(daysInDB.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                        Meal meal = userSnapshot.getValue(Meal.class);
                                        assert meal != null;
                                        meal.setId(userSnapshot.getKey());
                                        if (meal.getUserID().equals(userID)) {
                                            Log.i("meal", "" + meal.getId());
                                            databaseReference1.child(daysInDB.get(finalI)).child(meal.getId()).removeValue();
                                        }
                                    }
                                    Calendar calendar = Calendar.getInstance();
                                    int day = calendar.get(Calendar.DAY_OF_WEEK);
                                    switch (day) {
                                        case Calendar.SUNDAY:
                                            SundayMeals sundayMeals = new SundayMeals();
                                            sundayMeals.getRCVData();
                                            break;
                                        case Calendar.MONDAY:
                                            MondayMeals mondayMeals = new MondayMeals();
                                            mondayMeals.getRCVData();
                                            break;
                                        case Calendar.TUESDAY:
                                            TuesdayMeals tuesdayMeals = new TuesdayMeals();
                                            tuesdayMeals.getRCVData();
                                            break;
                                        case Calendar.WEDNESDAY:
                                            WednesdayMeals wednesdayMeals = new WednesdayMeals();
                                            wednesdayMeals.getRCVData();
                                            break;
                                        case Calendar.THURSDAY:
                                            ThursdayMeals thursdayMeals = new ThursdayMeals();
                                            thursdayMeals.getRCVData();
                                            break;
                                        case Calendar.FRIDAY:
                                            FridayMeals fridayMeals = new FridayMeals();
                                            fridayMeals.getRCVData();
                                            break;
                                        case Calendar.SATURDAY:
                                            SaturdayMeals saturdayMeals = new SaturdayMeals();
                                            saturdayMeals.getRCVData();
                                            break;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.i("error", ":" + error.getMessage());
                                }
                            });
                        }
                    } else {
                        Log.i("error", "7 days have not passed yet");
                    }
                });
            }
        }
    }
}

