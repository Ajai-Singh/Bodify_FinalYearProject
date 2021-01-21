package com.example.bodify.TrackingDaysMacros;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import com.example.bodify.FoodFinder;
import com.example.bodify.Models.Macro;
import com.example.bodify.Models.MacroCopy;
import com.example.bodify.Models.Meal;
import com.example.bodify.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Wednesday extends Fragment {
    private TextView caloriesTV, fatsTV, proteinsTV, carbohydratesTV;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();
    private BarChart barChart;
    private BarChart barChart1;
    private final ArrayList<BarEntry> barEntries = new ArrayList<>();
    private final ArrayList<BarEntry> barEntries1 = new ArrayList<>();
    private ConstraintLayout constraintLayout;

    public Wednesday() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_monday, container, false);
        caloriesTV = view.findViewById(R.id.dailyCaloriesLeftTextView);
        fatsTV = view.findViewById(R.id.dailyFatsTV);
        proteinsTV = view.findViewById(R.id.dailyProteinsTV);
        carbohydratesTV = view.findViewById(R.id.dailyCarbsTV);
        barChart = view.findViewById(R.id.breakdownBarChart);
        barChart1 = view.findViewById(R.id.pieChart);
        constraintLayout = view.findViewById(R.id.cl);
        barEntries.clear();
        barEntries1.clear();
        getValuesForMacroCopy();
        getMacroCopyValues();
        setUIComponents();
        setUpBarChart1();
        setUpBarChart();
        return view;
    }

    public void getValuesForMacroCopy() {
        assert userID != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Macros").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Macro macro = snapshot.getValue(Macro.class);
                assert macro != null;
                createMacroCopyInDatabase(macro.getCalorieConsumption(), macro.getCarbohydrates(), macro.getFats(), macro.getProteins());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createMacroCopyInDatabase(double a, double b, double c, double d) {
        assert userID != null;
        MacroCopy macroCopy = new MacroCopy(a, b, c, d, userID);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("TemporaryMacros");
        databaseReference.child("Wednesday").child(userID).setValue(macroCopy).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i("Saved", "Successfully saved");
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Error Occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void setUIComponents() {
        assert userID != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TemporaryMacros").child("Wednesday").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MacroCopy macroCopy = snapshot.getValue(MacroCopy.class);
                if (macroCopy != null) {
                    if (getActivity() != null) {
                        if (macroCopy.getCarbohydrateConsumption() < 0) {
                            carbohydratesTV.setTextColor(Color.parseColor("#FF0000"));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel notificationChannel = new NotificationChannel("My Notification", "test", NotificationManager.IMPORTANCE_DEFAULT);
                                NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
                                notificationManager.createNotificationChannel(notificationChannel);
                            }
                            String message = "You have gone over your daily Carbohydrates";
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "My Notification").setSmallIcon(
                                    R.drawable.info).setContentTitle("Attention").setContentText(message).setAutoCancel(true);
                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
                            notificationManagerCompat.notify(0, builder.build());


                        } else if (macroCopy.getFatConsumption() < 0) {
                            fatsTV.setTextColor(Color.parseColor("#FF0000"));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel notificationChannel = new NotificationChannel("My Notification", "test", NotificationManager.IMPORTANCE_DEFAULT);
                                NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
                                notificationManager.createNotificationChannel(notificationChannel);
                            }
                            String message = "You have gone over your daily Fat";
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "My Notification").setSmallIcon(
                                    R.drawable.info).setContentTitle("Attention").setContentText(message).setAutoCancel(true);
                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
                            notificationManagerCompat.notify(0, builder.build());
                        } else if (macroCopy.getProteinConsumption() < 0) {
                            proteinsTV.setTextColor(Color.parseColor("#FF0000"));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel notificationChannel = new NotificationChannel("My Notification", "test", NotificationManager.IMPORTANCE_DEFAULT);
                                NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
                                notificationManager.createNotificationChannel(notificationChannel);
                            }
                            String message = "You have gone over your daily Protein";
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "My Notification").setSmallIcon(
                                    R.drawable.info).setContentTitle("Attention").setContentText(message).setAutoCancel(true);
                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
                            notificationManagerCompat.notify(0, builder.build());
                        } else if (macroCopy.getCalorieConsumption() < 0) {
                            caloriesTV.setTextColor(Color.parseColor("#FF0000"));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                NotificationChannel notificationChannel = new NotificationChannel("My Notification", "test", NotificationManager.IMPORTANCE_DEFAULT);
                                NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
                                notificationManager.createNotificationChannel(notificationChannel);
                            }
                            String message = "You have gone over your daily Calories";
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "My Notification").setSmallIcon(
                                    R.drawable.info).setContentTitle("Attention").setContentText(message).setAutoCancel(true);
                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
                            notificationManagerCompat.notify(0, builder.build());
                        }
                        caloriesTV.setText(String.valueOf(macroCopy.getCalorieConsumption()));
                        fatsTV.setText(String.valueOf(macroCopy.getFatConsumption()));
                        proteinsTV.setText(String.valueOf(macroCopy.getProteinConsumption()));
                        carbohydratesTV.setText(String.valueOf(macroCopy.getCarbohydrateConsumption()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setUpBarChart1() {
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("DayOfWeek").child("Wednesday");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            double loggedCalories, loggedProteins, loggedFats, loggedCarbohydrates;
            final ArrayList<BarEntry> macros = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Meal meal = userSnapshot.getValue(Meal.class);
                    assert meal != null;
                    if (meal.getUserID().equals(userID)) {
                        loggedCalories += meal.getCalories() * meal.getNumberOfServings();
                        loggedProteins += meal.getItemProtein() * meal.getNumberOfServings();
                        loggedFats += meal.getItemTotalFat() * meal.getNumberOfServings();
                        loggedCarbohydrates += meal.getItemTotalCarbohydrates() * meal.getNumberOfServings();
                    }
                }
                if (loggedProteins == 0.0 && loggedFats == 0.0 && loggedCarbohydrates == 0.0) {
                    barChart1.setVisibility(View.GONE);
                    barChart.setVisibility(View.GONE);
                    final ArrayList<String> daysOfWeek = new ArrayList<>();
                    final ArrayList<String> daysToShow = new ArrayList<>();
                    daysOfWeek.add("Monday");
                    daysOfWeek.add("Tuesday");
                    daysOfWeek.add("Wednesday");
                    daysOfWeek.add("Thursday");
                    daysOfWeek.add("Friday");
                    daysOfWeek.add("Saturday");
                    daysOfWeek.add("Sunday");
                    String position = null;
                    final SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
                    final Date today = new Date();
                    for (int i = 0; i < daysOfWeek.size(); i++) {
                        if (simpleDateformat.format(today).equalsIgnoreCase(daysOfWeek.get(i))) {
                            String a = daysOfWeek.get(i);
                            position = String.valueOf(daysOfWeek.indexOf(a));
                            break;
                        }
                    }
                    for (int i = 0; i <= Integer.parseInt(Objects.requireNonNull(position)); i++) {
                        daysToShow.add(daysOfWeek.get(i));
                    }
                    if (daysToShow.contains("Wednesday")) {
                        showPositiveSnackBar();
                    } else {
                        showNegativeSnackBar();
                    }
                } else {
                    macros.add(new BarEntry(1f, (float) loggedFats));
                    macros.add(new BarEntry(2f, (float) loggedCarbohydrates));
                    macros.add(new BarEntry(3f, (float) loggedProteins));
                    showBarChart1(macros);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showPositiveSnackBar() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Sorry no data! Please navigate Food finder", Snackbar.LENGTH_SHORT).setAction(
                "Food finder", v -> {
                    Intent intent = new Intent(getActivity(), FoodFinder.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(0, 0);
                }
        );
        snackbar.show();
    }

    public void showNegativeSnackBar() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Sorry cannot add meals to days in the future!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void showBarChart1(ArrayList<BarEntry> macros) {
        barEntries1.add(macros.get(0));
        barEntries1.add(macros.get(1));
        barEntries1.add(macros.get(2));
        XAxis xAxis = barChart1.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getXAxisValues1()));
        BarDataSet barDataSet = new BarDataSet(barEntries1, "Data Set");
        BarData barData = new BarData(barDataSet);
        barChart1.clear();
        barChart1.setData(barData);
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart1.invalidate();
    }

    public ArrayList<String> getXAxisValues1() {
        ArrayList<String> xLabels = new ArrayList<>();
        xLabels.add("0");
        xLabels.add("Fats (g)");
        xLabels.add("Carbohydrates (g)");
        xLabels.add("Proteins (g)");
        return new ArrayList<>(xLabels);
    }

    public void setUpBarChart() {
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("DayOfWeek").child("Wednesday");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            double loggedCalories, loggedProteins, loggedFats, loggedCarbohydrates;
            final ArrayList<BarEntry> macros = new ArrayList<>();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Meal meal = userSnapshot.getValue(Meal.class);
                    assert meal != null;
                    if (meal.getUserID().equals(userID)) {
                        loggedCalories += meal.getCalories() * meal.getNumberOfServings();
                        loggedProteins += meal.getItemProtein() * meal.getNumberOfServings();
                        loggedFats += meal.getItemTotalFat() * meal.getNumberOfServings();
                        loggedCarbohydrates += meal.getItemTotalCarbohydrates() * meal.getNumberOfServings();
                    }
                }
                macros.add(new BarEntry(1f, (float) loggedFats * 9));
                macros.add(new BarEntry(2f, (float) loggedCarbohydrates * 4));
                macros.add(new BarEntry(3f, (float) loggedProteins * 4));
                showBarChart(macros);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showBarChart(ArrayList<BarEntry> macros) {
        barEntries.add(macros.get(0));
        barEntries.add(macros.get(1));
        barEntries.add(macros.get(2));
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getXAxisValues()));
        BarDataSet barDataSet = new BarDataSet(barEntries, "Data Set");
        BarData barData = new BarData(barDataSet);
        barChart.clear();
        barChart.setData(barData);
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(16f);
        barChart.invalidate();
    }

    public ArrayList<String> getXAxisValues() {
        ArrayList<String> xLabels = new ArrayList<>();
        xLabels.add("0");
        xLabels.add("Fat Calories");
        xLabels.add("Carbohydrate Calories");
        xLabels.add("Protein Calories");
        return new ArrayList<>(xLabels);
    }

    public void getMacroCopyValues() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek").child("Wednesday");
        databaseReference.addValueEventListener(new ValueEventListener() {
            double loggedCalories, loggedProteins, loggedFats, loggedCarbohydrates;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Meal meal = userSnapshot.getValue(Meal.class);
                    assert meal != null;
                    if (meal.getUserID().equals(userID)) {
                        loggedCalories += meal.getCalories() * meal.getNumberOfServings();
                        loggedProteins += meal.getItemProtein() * meal.getNumberOfServings();
                        loggedFats += meal.getItemTotalFat() * meal.getNumberOfServings();
                        loggedCarbohydrates += meal.getItemTotalCarbohydrates() * meal.getNumberOfServings();
                    }
                }
                updateMacroCopy(loggedCalories, loggedProteins, loggedFats, loggedCarbohydrates);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateMacroCopy(final double calories, final double proteins, final double fats, final double carbohydrates) {
        assert userID != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TemporaryMacros").child("Wednesday").child(userID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                MacroCopy macroCopy = snapshot.getValue(MacroCopy.class);
                if (macroCopy != null) {
                    DatabaseReference updateDBReference = FirebaseDatabase.getInstance().getReference("TemporaryMacros").child("Wednesday").child(userID);
                    updateDBReference.child("calorieConsumption").setValue(macroCopy.getCalorieConsumption() - calories);
                    updateDBReference.child("carbohydrateConsumption").setValue(macroCopy.getCarbohydrateConsumption() - carbohydrates);
                    updateDBReference.child("fatConsumption").setValue(macroCopy.getFatConsumption() - fats);
                    updateDBReference.child("proteinConsumption").setValue(macroCopy.getProteinConsumption() - proteins);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error Occurred!" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}




