package com.example.bodify;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.Adapters.GroceryAdapter;
import com.example.bodify.Models.Habits;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import java.util.ArrayList;
import java.util.Objects;

public class Groceries extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private GroceryAdapter groceryAdapter;
    private RecyclerView recyclerView;
    private Spinner spinner;
    private ArrayList<String> meals;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ConstraintLayout constraintLayout;
    private final ArrayList<String> breakfast = new ArrayList<>();
    private final ArrayList<String> lunch = new ArrayList<>();
    private final ArrayList<String> dinner = new ArrayList<>();
    private final ArrayList<String> other = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groceries);
        recyclerView = findViewById(R.id.groceryRCV);
        spinner = findViewById(R.id.grocerySpinner);
        Button search = findViewById(R.id.grocerySearch);
        constraintLayout = findViewById(R.id.gcl);
        populateSpinner();
        search.setOnClickListener(v -> {
            DatabaseReference habitReference = FirebaseDatabase.getInstance().getReference("Habits").child(Objects.requireNonNull(mAuth.getUid()));
            habitReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Habits habits = snapshot.getValue(Habits.class);
                    if (spinner.getSelectedItemPosition() == 0) {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(Groceries.this);
                        dlgAlert.setMessage("Select drop down value!");
                        dlgAlert.setTitle("Error...");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                    } else if (spinner.getSelectedItem().toString().equals("Breakfast")) {
                        assert habits != null;
                        if (habits.getBreakfastNames().isEmpty() || habits.getBreakfastNames().contains("No Meals")) {
                            noData();
                        } else {
                            populateBreakfastRCV(habits.getBreakfastNames());
                        }
                    } else if (spinner.getSelectedItem().toString().equals("Lunch")) {
                        assert habits != null;
                        if (habits.getLunchNames().isEmpty() || habits.getLunchNames().contains("No Meals")) {
                            noData();
                        } else {
                            populateLunchRCV(habits.getLunchNames());
                        }
                    } else if (spinner.getSelectedItem().toString().equals("Dinner")) {
                        assert habits != null;
                        if (habits.getDinnerNames().isEmpty() || habits.getDinnerNames().contains("No Meals")) {
                            noData();
                        } else {
                            populateDinnerRCV(habits.getDinnerNames());
                        }
                    } else if (spinner.getSelectedItem().toString().equals("Other")) {
                        assert habits != null;
                        if (habits.getOtherNames().isEmpty() || habits.getOtherNames().contains("No Meals")) {
                            noData();
                        } else {
                            populateOtherRCV(habits.getOtherNames());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Groceries.this,"Error occurred: " + error.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        });
    }


    public void populateSpinner() {
        meals = new ArrayList<>();
        meals.add("Select meal of day!");
        meals.add("Breakfast");
        meals.add("Lunch");
        meals.add("Dinner");
        meals.add("Other");
        ArrayAdapter<String> adapterMeals = new ArrayAdapter<String>(Groceries.this, android.R.layout.simple_spinner_dropdown_item, meals) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textview = (TextView) view;
                if (position == 0) {
                    textview.setTextColor(Color.GRAY);
                } else {
                    textview.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        adapterMeals.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterMeals);
        spinner.setOnItemSelectedListener(Groceries.this);
    }

    public void populateBreakfastRCV(ArrayList<String> itemNames) {
        new Thread(() -> {
            Document document;
            for(int i = 0; i < itemNames.size(); i++) {
                String link = "https://www.tesco.ie/groceries/product/search/default.aspx?searchBox="+itemNames.get(i);
                try {

                } finally {

                }

            }
        }).start();
    }

    public void populateLunchRCV(ArrayList<String> itemNames) {
        new Thread(() -> {
            Document document;
            for(int i = 0; i < itemNames.size(); i++) {
                String link = "https://www.tesco.ie/groceries/product/search/default.aspx?searchBox="+itemNames.get(i);
                try {

                } finally {

                }

            }
        }).start();
    }

    public void populateDinnerRCV(ArrayList<String> itemNames) {
        new Thread(() -> {
            Document document;
            for(int i = 0; i < itemNames.size(); i++) {
                String link = "https://www.tesco.ie/groceries/product/search/default.aspx?searchBox="+itemNames.get(i);
                try {

                } finally {

                }

            }
        }).start();
    }

    public void populateOtherRCV(ArrayList<String> itemNames) {
        new Thread(() -> {
            Document document;
            for(int i = 0; i < itemNames.size(); i++) {
                String link = "https://www.tesco.ie/groceries/SpecialOffers/default.aspx";
                try {

                } finally {

                }

            }
        }).start();
    }

    public void noData() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Sorry no user habits!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}