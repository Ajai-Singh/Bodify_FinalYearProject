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
import com.example.bodify.Adapters.MondayAdapter;
import com.example.bodify.Models.Meal;
import com.example.bodify.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MondayMeals extends Fragment {
    private RecyclerView breakfastRecyclerView;
    private MondayAdapter mondayAdapter;
    private final ArrayList<Meal> meals = new ArrayList<>();

    public MondayMeals() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_showmeals, container, false);
        breakfastRecyclerView = view.findViewById(R.id.breakfastRecyclerView);
        getAllBreakfastMeals();
        getAllLunchMeals();
        getAllDinnerMeals();
        //I have half a mind to do them all in one method would that be more efficient eg. have 4 arraylists and pass each into the adapter with 4 different if statment checks
        //to see what meal type theyre?
        return view;
    }
    //I believe I can use the same adapter to populate all the recycler views.
    //How I will achieve this? Create 4 recycler views and attach them
    //at the moment I have just hard coded friday into the child of the database. Once I am finished tidying up I will need to pass in the current day of the week.
    //make sure to also do if statment check to make sure it goes into the correct recycler view row.
    //eg . if(day.getMealType.equals("Breakfast") then add it to the arrayList
    public void getAllBreakfastMeals() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek").child("Monday");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Meal meal = userSnapshot.getValue(Meal.class);
                    assert meal != null;
                    if(meal.getMealType().equals("Breakfast")) {
                        meals.add(meal);
                    }
                }
                breakfastRecyclerView.setHasFixedSize(true);
                breakfastRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mondayAdapter = new MondayAdapter(meals);
                breakfastRecyclerView.setAdapter(mondayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(),"Error Occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getAllLunchMeals() {

    }

    public void getAllDinnerMeals() {

    }

    public void getAllOtherMeals() {

    }
}
