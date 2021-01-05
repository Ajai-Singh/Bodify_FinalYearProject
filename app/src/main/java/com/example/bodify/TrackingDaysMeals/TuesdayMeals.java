package com.example.bodify.TrackingDaysMeals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.Adapters.MealAdapter;
import com.example.bodify.Models.Meal;
import com.example.bodify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Objects;

public class TuesdayMeals extends Fragment {
    private MealAdapter mondayBreakfastAdapter;
    private MealAdapter mondayLunchAdapter;
    private MealAdapter mondayDinnerAdapter;
    private MealAdapter mondayOtherAdapter;
    private RecyclerView breakfastRecyclerView;
    private RecyclerView lunchRecyclerView;
    private RecyclerView dinnerRecyclerView;
    private RecyclerView otherRecyclerView;
    private final ArrayList<Meal> breakfastMeals = new ArrayList<>();
    private final ArrayList<Meal> lunchMeals = new ArrayList<>();
    private final ArrayList<Meal> dinnerMeals = new ArrayList<>();
    private final ArrayList<Meal> otherMeals = new ArrayList<>();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();


    public TuesdayMeals() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_showmeals, container, false);
        breakfastRecyclerView = view.findViewById(R.id.breakfastRecyclerView);
        lunchRecyclerView = view.findViewById(R.id.lunchRecyclerView);
        dinnerRecyclerView = view.findViewById(R.id.dinnerRecyclerView);
        otherRecyclerView = view.findViewById(R.id.otherRecyclerView);
        breakfastMeals.clear();
        lunchMeals.clear();
        dinnerMeals.clear();
        otherMeals.clear();
        populateBreakfastRCV();
        populateLunchRCV();
        populateDinnerRCV();
        populateOtherRCV();
        return view;
    }

    //seems to be a problem with it only showing one item in the rcv even though db has multiple records
    public void populateBreakfastRCV() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek").child("Tuesday");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Meal meal = userSnapshot.getValue(Meal.class);
                    assert meal != null;
                    meal.setId(userSnapshot.getKey());
                    if (meal.getMealType().equals("Breakfast") && meal.getUserID().equals(userID)) {
                        breakfastMeals.add(meal);
                        break;
                    }
                }
                breakfastRecyclerView.setHasFixedSize(true);
                breakfastRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mondayBreakfastAdapter = new MealAdapter(breakfastMeals,getContext());
                breakfastRecyclerView.setAdapter(mondayBreakfastAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),"Error Occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void populateLunchRCV() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek").child("Tuesday");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Meal meal = userSnapshot.getValue(Meal.class);
                    assert meal != null;
                    meal.setId(userSnapshot.getKey());
                    if (meal.getMealType().equals("Lunch") && meal.getUserID().equals(userID)) {
                        lunchMeals.add(meal);
                        break;
                    }
                }
                lunchRecyclerView.setHasFixedSize(true);
                lunchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mondayLunchAdapter = new MealAdapter(lunchMeals,getContext());
                lunchRecyclerView.setAdapter(mondayLunchAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),"Error Occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void populateDinnerRCV() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek").child("Tuesday");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Meal meal = userSnapshot.getValue(Meal.class);
                    assert meal != null;
                    meal.setId(userSnapshot.getKey());
                    if (meal.getMealType().equals("Dinner") && meal.getUserID().equals(userID)) {
                        dinnerMeals.add(meal);
                        break;
                    }
                }
                dinnerRecyclerView.setHasFixedSize(true);
                dinnerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mondayDinnerAdapter = new MealAdapter(dinnerMeals,getContext());
                dinnerRecyclerView.setAdapter(mondayDinnerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),"Error Occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void populateOtherRCV() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek").child("Tuesday");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Meal meal = userSnapshot.getValue(Meal.class);
                    assert meal != null;
                    meal.setId(userSnapshot.getKey());
                    if (meal.getMealType().equals("Other") && meal.getUserID().equals(userID)) {
                        otherMeals.add(meal);
                        break;
                    }
                }
                otherRecyclerView.setHasFixedSize(true);
                otherRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mondayOtherAdapter = new MealAdapter(otherMeals,getContext());
                otherRecyclerView.setAdapter(mondayOtherAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),"Error Occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
