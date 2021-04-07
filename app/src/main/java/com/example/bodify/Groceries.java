package com.example.bodify;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bodify.Adapters.GroceryAdapter;
import com.example.bodify.Models.Grocery;
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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Groceries extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private GroceryAdapter groceryAdapter;
    private RecyclerView recyclerView;
    private Spinner spinner;
    private ArrayList<String> meals;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ConstraintLayout constraintLayout;
    private final ArrayList<Grocery> breakfast = new ArrayList<>();
    private final ArrayList<Grocery> lunch = new ArrayList<>();
    private final ArrayList<Grocery> dinner = new ArrayList<>();
    private final ArrayList<Grocery> other = new ArrayList<>();
    private final ArrayList<String> imageLinks = new ArrayList<>();
    private final ArrayList<String> prices = new ArrayList<>();
    private final ArrayList<String> productNames = new ArrayList<>();
    private final ArrayList<String> urls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groceries);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Tesco.ie");
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
                        selectDropdownValue();
                    } else if (spinner.getSelectedItem().toString().equals("Breakfast")) {
                        assert habits != null;
                        if (habits.getBreakfastNames().isEmpty() || habits.getBreakfastNames().contains("No Meals")) {
                            noData();
                        } else {
                            breakfast.clear();
                            lunch.clear();
                            dinner.clear();
                            other.clear();
                            urls.clear();
                            productNames.clear();
                            prices.clear();
                            imageLinks.clear();
                            populateBreakfastRCV(habits.getBreakfastNames());
                        }
                    } else if (spinner.getSelectedItem().toString().equals("Lunch")) {
                        assert habits != null;
                        if (habits.getLunchNames().isEmpty() || habits.getLunchNames().contains("No Meals")) {
                            noData();
                        } else {
                            breakfast.clear();
                            lunch.clear();
                            dinner.clear();
                            other.clear();
                            urls.clear();
                            productNames.clear();
                            prices.clear();
                            imageLinks.clear();
                            populateLunchRCV(habits.getLunchNames());
                        }
                    } else if (spinner.getSelectedItem().toString().equals("Dinner")) {
                        assert habits != null;
                        if (habits.getDinnerNames().isEmpty() || habits.getDinnerNames().contains("No Meals")) {
                            noData();
                        } else {
                            breakfast.clear();
                            lunch.clear();
                            dinner.clear();
                            other.clear();
                            urls.clear();
                            productNames.clear();
                            prices.clear();
                            imageLinks.clear();
                            populateDinnerRCV(habits.getDinnerNames());
                        }
                    } else if (spinner.getSelectedItem().toString().equals("Other")) {
                        assert habits != null;
                        if (habits.getOtherNames().isEmpty() || habits.getOtherNames().contains("No Meals")) {
                            noData();
                        } else {
                            breakfast.clear();
                            lunch.clear();
                            dinner.clear();
                            other.clear();
                            urls.clear();
                            productNames.clear();
                            prices.clear();
                            imageLinks.clear();
                            populateOtherRCV(habits.getOtherNames());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Groceries.this, "Error occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
            org.jsoup.nodes.Document document;
            org.jsoup.nodes.Document dataDocument;
            for (int i = 0; i < itemNames.size(); i++) {
                String img;
                try {
                    document = Jsoup.connect("https://www.tesco.ie/groceries/product/search/default.aspx?searchBox=" + itemNames.get(i)).get();
                    Elements imageTag = document.getElementsByClass("image");
                    for (Element e : imageTag) {
                        img = e.getElementsByTag("img").toString();
                        imageLinks.add(img.substring(10, img.length() - 9));
                    }
                    Elements absLink = document.select("h3.inBasketInfoContainer > a");
                    for (Element links : absLink) {
                        urls.add(links.attr("href"));
                    }

                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            if (!urls.isEmpty()) {
                loadingData();
                for (int x = 0; x < urls.size(); x++) {
                    try {
                        dataDocument = Jsoup.connect("https://www.tesco.ie" + urls.get(x)).get();
                        Elements itemName = dataDocument.getElementsByTag("h1");
                        Elements priceTag = dataDocument.getElementsByClass("linePrice");
                        productNames.add(itemName.text());
                        prices.add(priceTag.text());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                noResultsFound();
            }
            for (int i = 0; i < productNames.size(); i++) {
                Grocery grocery = new Grocery(productNames.get(i), imageLinks.get(i), prices.get(i), "https://www.tesco.ie" + urls.get(i));
                breakfast.add(grocery);
            }
            runOnUiThread(() -> {
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(Groceries.this));
                groceryAdapter = new GroceryAdapter(breakfast, Groceries.this);
                recyclerView.setAdapter(groceryAdapter);
            });
        }).start();
    }

    public void populateLunchRCV(ArrayList<String> itemNames) {
        new Thread(() -> {
            org.jsoup.nodes.Document document;
            org.jsoup.nodes.Document dataDocument;
            for (int i = 0; i < itemNames.size(); i++) {
                String img;
                try {
                    document = Jsoup.connect("https://www.tesco.ie/groceries/product/search/default.aspx?searchBox=" + itemNames.get(i)).get();
                    Elements imageTag = document.getElementsByClass("image");
                    for (Element e : imageTag) {
                        img = e.getElementsByTag("img").toString();
                        imageLinks.add(img.substring(10, img.length() - 9));
                    }
                    Elements absLink = document.select("h3.inBasketInfoContainer > a");
                    for (Element links : absLink) {
                        urls.add(links.attr("href"));
                    }

                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            if (!urls.isEmpty()) {
                loadingData();
                for (int x = 0; x < urls.size(); x++) {
                    try {
                        dataDocument = Jsoup.connect("https://www.tesco.ie" + urls.get(x)).get();
                        Elements itemName = dataDocument.getElementsByTag("h1");
                        Elements priceTag = dataDocument.getElementsByClass("linePrice");
                        productNames.add(itemName.text());
                        prices.add(priceTag.text());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                noResultsFound();
            }
            for (int i = 0; i < productNames.size(); i++) {
                Grocery grocery = new Grocery(productNames.get(i), imageLinks.get(i), prices.get(i), "https://www.tesco.ie" + urls.get(i));
                lunch.add(grocery);
            }
            runOnUiThread(() -> {
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(Groceries.this));
                groceryAdapter = new GroceryAdapter(lunch, Groceries.this);
                recyclerView.setAdapter(groceryAdapter);
            });
        }).start();
    }

    public void populateDinnerRCV(ArrayList<String> itemNames) {
        new Thread(() -> {
            org.jsoup.nodes.Document document;
            org.jsoup.nodes.Document dataDocument;
            for (int i = 0; i < itemNames.size(); i++) {
                String img;
                try {
                    document = Jsoup.connect("https://www.tesco.ie/groceries/product/search/default.aspx?searchBox=" + itemNames.get(i)).get();
                    Elements imageTag = document.getElementsByClass("image");
                    for (Element e : imageTag) {
                        img = e.getElementsByTag("img").toString();
                        imageLinks.add(img.substring(10, img.length() - 9));
                    }
                    Elements absLink = document.select("h3.inBasketInfoContainer > a");
                    for (Element links : absLink) {
                        urls.add(links.attr("href"));
                    }

                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            if (!urls.isEmpty()) {
                loadingData();
                for (int x = 0; x < urls.size(); x++) {
                    try {
                        dataDocument = Jsoup.connect("https://www.tesco.ie" + urls.get(x)).get();
                        Elements itemName = dataDocument.getElementsByTag("h1");
                        Elements priceTag = dataDocument.getElementsByClass("linePrice");
                        productNames.add(itemName.text());
                        prices.add(priceTag.text());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                noResultsFound();
            }
            for (int i = 0; i < productNames.size(); i++) {
                Grocery grocery = new Grocery(productNames.get(i), imageLinks.get(i), prices.get(i), "https://www.tesco.ie" + urls.get(i));
                dinner.add(grocery);
            }
            runOnUiThread(() -> {
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(Groceries.this));
                groceryAdapter = new GroceryAdapter(dinner, Groceries.this);
                recyclerView.setAdapter(groceryAdapter);
            });
        }).start();
    }

    public void populateOtherRCV(ArrayList<String> itemNames) {
        new Thread(() -> {
            org.jsoup.nodes.Document document;
            org.jsoup.nodes.Document dataDocument;
            for (int i = 0; i < itemNames.size(); i++) {
                String img;
                try {
                    document = Jsoup.connect("https://www.tesco.ie/groceries/product/search/default.aspx?searchBox=" + itemNames.get(i)).get();
                    Elements imageTag = document.getElementsByClass("image");
                    for (Element e : imageTag) {
                        img = e.getElementsByTag("img").toString();
                        imageLinks.add(img.substring(10, img.length() - 9));
                    }
                    Elements absLink = document.select("h3.inBasketInfoContainer > a");
                    for (Element links : absLink) {
                        urls.add(links.attr("href"));
                    }

                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
            if (!urls.isEmpty()) {
                loadingData();
                for (int x = 0; x < urls.size(); x++) {
                    try {
                        dataDocument = Jsoup.connect("https://www.tesco.ie" + urls.get(x)).get();
                        Elements itemName = dataDocument.getElementsByTag("h1");
                        Elements priceTag = dataDocument.getElementsByClass("linePrice");
                        productNames.add(itemName.text());
                        prices.add(priceTag.text());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                noResultsFound();
            }
            for (int i = 0; i < productNames.size(); i++) {
                Grocery grocery = new Grocery(productNames.get(i), imageLinks.get(i), prices.get(i), "https://www.tesco.ie" + urls.get(i));
                other.add(grocery);
            }
            runOnUiThread(() -> {
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(Groceries.this));
                groceryAdapter = new GroceryAdapter(other, Groceries.this);
                recyclerView.setAdapter(groceryAdapter);
            });
        }).start();
    }

    public void noData() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Sorry no user habits!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void loadingData() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Loading suggestions", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void noResultsFound() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Sorry no results found!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void selectDropdownValue() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Error, select drop down value!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}