package com.example.bodify;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.bodify.Adapters.RecipeAdapter;
import com.example.bodify.Models.Habits;
import com.example.bodify.Models.Ingredient;
import com.example.bodify.Models.Meal;
import com.example.bodify.Models.Recipe;
import com.example.bodify.Models.Favourite;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.example.bodify.BarcodeReader.IntentIntegrator;
import com.example.bodify.BarcodeReader.IntentResult;
import com.google.firebase.database.ValueEventListener;

public class RecipeSearch extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner mealsSpinner;
    private ArrayList<String> mealTypes;
    private EditText choice, nutrition;
    public static final String API_KEY = "de851175d709445bb3d6149a58107a93";
    private final ArrayList<Recipe> recipes = new ArrayList<>();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Button search;
    private Button find;
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private String quantityAdapterChoice;
    private String mealAdapterChoice;
    private String whatDayToAddTo;
    private ArrayList<String> servingNumbers;
    private final Date today = new Date();
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
    private RequestQueue queue;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private final Date date = new Date();
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_finder);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Recipe Search");
        queue = Volley.newRequestQueue(RecipeSearch.this);
        AndroidNetworking.initialize(RecipeSearch.this);
        mealsSpinner = findViewById(R.id.spinner);
        choice = findViewById(R.id.foodName);
        search = findViewById(R.id.searchMeals);
        recyclerView = findViewById(R.id.recipeRCV);
        Button scan = findViewById(R.id.button4);
        find = findViewById(R.id.nutritionCheckButton);
        nutrition = findViewById(R.id.nutritionCheck);
        constraintLayout = findViewById(R.id.clff);
        updateSpinners();
        nutritionChecker();
        mealSearch();
        scan.setOnClickListener(v -> {
            IntentIntegrator scanIntegrator = new IntentIntegrator(RecipeSearch.this);
            scanIntegrator.initiateScan();
        });
    }

    public void updateSpinners() {
        mealTypes = new ArrayList<>();
        mealTypes.add("Select meal type");
        mealTypes.add("Main Course");
        mealTypes.add("Side Dish");
        mealTypes.add("Dessert");
        mealTypes.add("Appetizer");
        mealTypes.add("Salad");
        mealTypes.add("Bread");
        mealTypes.add("Breakfast");
        mealTypes.add("Soup");
        mealTypes.add("Beverage");
        mealTypes.add("Sauce");
        mealTypes.add("Drink");
        ArrayAdapter<String> adapterMeals = new ArrayAdapter<String>(RecipeSearch.this, android.R.layout.simple_spinner_dropdown_item, mealTypes) {
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
        mealsSpinner.setAdapter(adapterMeals);
        mealsSpinner.setOnItemSelectedListener(RecipeSearch.this);
    }

    public void mealSearch() {
        search.setOnClickListener(v -> {
            if (!recipes.isEmpty()) {
                recipes.clear();
                recipeAdapter.notifyDataSetChanged();
            }
            if (mealsSpinner.getSelectedItemPosition() == 0) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RecipeSearch.this);
                dlgAlert.setMessage("Select Meal Type!");
                dlgAlert.setTitle("Error...");
                dlgAlert.setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            } else if (TextUtils.isEmpty(choice.getText().toString())) {
                choice.setError("Field cannot be empty!");
                choice.requestFocus();
            } else {
                AndroidNetworking.get("https://api.spoonacular.com/recipes/complexSearch?query=" + choice.getText().toString() + "&apiKey=" + API_KEY + "&addRecipeNutrition=true&type=" + mealsSpinner.getSelectedItem().toString().toLowerCase() + "&number=" + 1)
                        .addPathParameter("pageNumber", "0")
                        .addQueryParameter("limit", "1")
                        .addHeaders("token", "1234")
                        .setTag("test")
                        .setPriority(Priority.LOW)
                        .build().getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            JSONObject jsonObject1;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                ArrayList<Ingredient> ingredients = new ArrayList<>();
                                ArrayList<Integer> macros = new ArrayList<>();
                                int id = jsonArray.getJSONObject(i).getInt("id");
                                String title = jsonArray.getJSONObject(i).getString("title");
                                String readyInMinutes = jsonArray.getJSONObject(i).getString("readyInMinutes");
                                int servings = jsonArray.getJSONObject(i).getInt("servings");
                                String sourceUrl = jsonArray.getJSONObject(i).getString("sourceUrl");
                                String image = jsonArray.getJSONObject(i).getString("image");
                                jsonObject1 = jsonArray.getJSONObject(i).getJSONObject("nutrition");
                                JSONArray jsonArray1 = jsonObject1.getJSONArray("nutrients");
                                JSONArray ingredientsArray = jsonObject1.getJSONArray("ingredients");
                                for (int x = 0; x < ingredientsArray.length(); x++) {
                                    String ingredientName = ingredientsArray.getJSONObject(x).getString("name");
                                    Double ingredientAmount = ingredientsArray.getJSONObject(x).getDouble("amount");
                                    String unit = ingredientsArray.getJSONObject(x).getString("unit");
                                    Ingredient ingredient = new Ingredient(ingredientName, ingredientAmount, unit);
                                    ingredients.add(ingredient);
                                }
                                for (int e = 0; e < jsonArray1.length(); e++) {
                                    macros.add(jsonArray1.getJSONObject(e).getInt("amount"));
                                }
                                Recipe recipe = new Recipe(id, title, sourceUrl, readyInMinutes, servings, mAuth.getUid(), macros.get(0), macros.get(1), macros.get(3), macros.get(8), macros.get(5), macros.get(7), image, ingredients);
                                recipes.add(recipe);
                            }
                            if (recipes.isEmpty()) {
                                noResultsFound();
                            }
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(RecipeSearch.this));
                            recipeAdapter = new RecipeAdapter(recipes, RecipeSearch.this, constraintLayout);
                            recyclerView.setAdapter(recipeAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        errorOccurred(anError.getMessage());
                    }
                });
            }
        });
    }

    public void nutritionChecker() {
        find.setOnClickListener(v -> {
            if (TextUtils.isEmpty(nutrition.getText().toString())) {
                nutrition.setError("Field cannot be empty!");
                nutrition.requestFocus();
            } else {
                final String url = "https://calorieninjas.p.rapidapi.com/v1/nutrition?query=" + nutrition.getText().toString();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        response -> {
                            try {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RecipeSearch.this);
                                View view = getLayoutInflater().inflate(R.layout.quick_search_popup, null);
                                TextView sugarTV = view.findViewById(R.id.quickSearchSugar);
                                TextView fiberTV = view.findViewById(R.id.quickSearchFiber);
                                TextView servingTV = view.findViewById(R.id.quickSearchServingSize);
                                TextView sodiumTV = view.findViewById(R.id.quickSearchSodium);
                                TextView nameTV = view.findViewById(R.id.quickSearchName);
                                TextView potassiumTV = view.findViewById(R.id.quickSearchPotassium);
                                TextView saturatedFatTV = view.findViewById(R.id.saturatedFatQuickSearch);
                                TextView fatTV = view.findViewById(R.id.quickSearchFat);
                                TextView caloriesTV = view.findViewById(R.id.quickSearchCalories);
                                TextView cholesterolTV = view.findViewById(R.id.cholesterolQuickSearch);
                                TextView proteinTV = view.findViewById(R.id.quickSearchProtein);
                                TextView carbohydrateTV = view.findViewById(R.id.quickSearchCarbohydrate);
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("items");
                                if (jsonArray.length() == 0) {
                                    noResultsFound();
                                } else {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        sugarTV.setText(String.valueOf(jsonArray.getJSONObject(i).getDouble("sugar_g")));
                                        fiberTV.setText(String.valueOf(jsonArray.getJSONObject(i).getDouble("fiber_g")));
                                        servingTV.setText(String.valueOf(jsonArray.getJSONObject(i).getDouble("serving_size_g")));
                                        sodiumTV.setText(String.valueOf(jsonArray.getJSONObject(i).getDouble("sodium_mg")));
                                        nameTV.setText(jsonArray.getJSONObject(i).getString("name"));
                                        potassiumTV.setText(String.valueOf(jsonArray.getJSONObject(i).getInt("potassium_mg")));
                                        saturatedFatTV.setText(String.valueOf(jsonArray.getJSONObject(i).getDouble("fat_saturated_g")));
                                        fatTV.setText(String.valueOf(jsonArray.getJSONObject(i).getDouble("fat_total_g")));
                                        caloriesTV.setText(String.valueOf(jsonArray.getJSONObject(i).getDouble("calories")));
                                        cholesterolTV.setText(String.valueOf(jsonArray.getJSONObject(i).getDouble("cholesterol_mg")));
                                        proteinTV.setText(String.valueOf(jsonArray.getJSONObject(i).getDouble("protein_g")));
                                        carbohydrateTV.setText(String.valueOf(jsonArray.getJSONObject(i).getDouble("carbohydrates_total_g")));
                                    }
                                    builder.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
                                    builder.setView(view);
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> errorOccurred(error.getMessage())) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<>();
                        params.put("x-rapidapi-key", "d272704e83mshe297a13b665b37ap1d6e1ejsn9d0a41048874");
                        return params;
                    }
                };
                queue.add(stringRequest);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            Log.i("scan format", "" + scanFormat);
            Log.i("scan content", "" + scanContent);
            if (scanContent != null) {
                returnNutritionalInformation(scanContent);
            }
        } else {
            noScannedData();
        }
    }

    public void returnNutritionalInformation(String scanContent) {
        AndroidNetworking.get("https://api.nutritionix.com/v1_1/item?upc=" + scanContent + "&appId=493d4e98&appKey=95b2bb9b721a2b2898f4a4269228ce93")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "1")
                .addHeaders("token", "1234")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    createPost(jsonObject.getString("item_name"), jsonObject.getInt("nf_calories"), jsonObject.getInt("nf_calories_from_fat"), jsonObject.getInt("nf_total_fat"),
                            jsonObject.getInt("nf_sodium"), jsonObject.getInt("nf_total_carbohydrate"), jsonObject.getInt("nf_sugars"), jsonObject.getInt("nf_protein"), jsonObject.getInt("nf_servings_per_container"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                    Snackbar snackbar = Snackbar.make(constraintLayout, "Invalid UPC provided", Snackbar.LENGTH_SHORT);
                    snackbar.show();
            }
        });
    }

    public void createPost(final String itemName, final int calories,
                           final int caloriesFromFat, final int itemTotalFat, final int itemSodium,
                           final int itemTotalCarbohydrates, final int itemSugars, final int itemProtein,
                           final int servings) {
        TextView itemNameFromScan, itemCalories, itemCaloriesFromFat, itemTotalFatT, itemSodiumT, itemTotalCarbohydratesT, itemSugarsT, itemProteinT, itemServingsT;
        Button addToFavourites, addToDiary;
        AlertDialog.Builder builder = new AlertDialog.Builder(RecipeSearch.this);
        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.scanner_popup, null);
        itemNameFromScan = view.findViewById(R.id.itemNameTextView);
        itemCalories = view.findViewById(R.id.itemCaloriesTextView);
        itemCaloriesFromFat = view.findViewById(R.id.caloriesFromFatTextView);
        itemTotalFatT = view.findViewById(R.id.totalFatTextView);
        itemSodiumT = view.findViewById(R.id.totalSodiumTextView);
        itemTotalCarbohydratesT = view.findViewById(R.id.totalCarbohydrateTextView);
        itemSugarsT = view.findViewById(R.id.totalSugarTextView);
        itemProteinT = view.findViewById(R.id.totalProteinTextView);
        itemServingsT = view.findViewById(R.id.amount);
        addToFavourites = view.findViewById(R.id.save);
        addToDiary = view.findViewById(R.id.diaryButton);
        itemNameFromScan.setText(itemName);
        itemCalories.setText(String.valueOf(calories));
        itemCaloriesFromFat.setText(String.valueOf(caloriesFromFat));
        itemTotalFatT.setText(String.valueOf(itemTotalFat));
        itemSodiumT.setText(String.valueOf(itemSodium));
        itemTotalCarbohydratesT.setText(String.valueOf(itemTotalCarbohydrates));
        itemSugarsT.setText(String.valueOf(itemSugars));
        itemProteinT.setText(String.valueOf(itemProtein));
        itemServingsT.setText(String.valueOf(servings));
        addToFavourites.setOnClickListener(v -> {
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            assert firebaseUser != null;
            String userID = firebaseUser.getUid();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Favourites");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean exists = false;
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        Favourite favourite = userSnapshot.getValue(Favourite.class);
                        if (favourite != null) {
                            if (favourite.getItemName().equalsIgnoreCase(itemName) && favourite.getUserID().equalsIgnoreCase(userID)) {
                                exists = true;
                                break;
                            }
                        }
                    }
                    if (exists) {
                        duplicateFavourite();
                    }
                    if (!exists) {
                        Favourite favourite = new Favourite(itemName, calories, itemTotalFat, itemSodium, itemTotalCarbohydrates, itemSugars, itemProtein, userID, servings, "no url");
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("Favourites").push().setValue(favourite).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                addedToFavourites();
                            } else {
                                errorOccurred(Objects.requireNonNull(task.getException()).getMessage());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    errorOccurred(error.getMessage());
                }
            });
        });

        addToDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Spinner meals;
                final Spinner quantity;
                final Spinner whatDay;
                AlertDialog.Builder builder = new AlertDialog.Builder(RecipeSearch.this);
                @SuppressLint("InflateParams")
                View view = getLayoutInflater().inflate(R.layout.addtodiarypopup, null);
                meals = view.findViewById(R.id.when);
                quantity = view.findViewById(R.id.quan);
                whatDay = view.findViewById(R.id.dayOfWeek);
                final ArrayList<String> daysOfWeek = new ArrayList<>();
                daysOfWeek.add("Monday");
                daysOfWeek.add("Tuesday");
                daysOfWeek.add("Wednesday");
                daysOfWeek.add("Thursday");
                daysOfWeek.add("Friday");
                daysOfWeek.add("Saturday");
                daysOfWeek.add("Sunday");
                ArrayList<String> daysToShow = new ArrayList<>();
                String position = null;
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
                int defaultP = 0;
                for (int i = 0; i < daysToShow.size(); i++) {
                    if (simpleDateformat.format(today).equalsIgnoreCase(daysToShow.get(i))) {
                        defaultP = i;
                        break;
                    }
                }
                ArrayAdapter dayAdapter = new ArrayAdapter(RecipeSearch.this, android.R.layout.simple_spinner_dropdown_item, daysToShow);
                whatDay.setAdapter(dayAdapter);
                whatDay.setSelection(defaultP);
                dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                whatDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        whatDayToAddTo = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                final ArrayList<String> mealTypes = new ArrayList<>();
                mealTypes.add("Select Meal");
                mealTypes.add("Breakfast");
                mealTypes.add("Lunch");
                mealTypes.add("Dinner");
                mealTypes.add("Other");
                servingNumbers = new ArrayList<>();
                servingNumbers.add("Select Quantity");
                for (int i = 1; i <= servings; i++) {
                    servingNumbers.add(String.valueOf(i));
                }
                ArrayAdapter servingAdapter = new ArrayAdapter(RecipeSearch.this, android.R.layout.simple_spinner_dropdown_item, servingNumbers) {
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
                quantity.setAdapter(servingAdapter);
                servingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                quantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        quantityAdapterChoice = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                ArrayAdapter mealsAdapter = new ArrayAdapter(RecipeSearch.this, android.R.layout.simple_spinner_dropdown_item, mealTypes) {
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
                meals.setAdapter(mealsAdapter);
                mealsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                meals.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mealAdapterChoice = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                String finalPosition = position;
                builder.setView(view);
                builder.setPositiveButton("Create", (dialog, which) -> {
                });
                builder.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                    if (meals.getSelectedItemPosition() == 0 || quantity.getSelectedItemPosition() == 0) {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(RecipeSearch.this);
                        dlgAlert.setMessage("Not All Fields are Filled!");
                        dlgAlert.setTitle("Error...");
                        dlgAlert.setPositiveButton("Ok", (dialog1, which) -> dialog1.dismiss());
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                    } else {
                        dialog.dismiss();
                        String positionToAddTo = null;
                        for (int i = 0; i < daysOfWeek.size(); i++) {
                            if (daysOfWeek.get(i).equalsIgnoreCase(whatDayToAddTo)) {
                                String a = daysOfWeek.get(i);
                                positionToAddTo = String.valueOf(daysOfWeek.indexOf(a));
                                break;
                            }
                        }
                        assert positionToAddTo != null;
                        int finalDate = Integer.parseInt(finalPosition) - Integer.parseInt(positionToAddTo);
                        int subStringCD = Integer.parseInt(formatter.format(date).substring(0, 2));
                        int f = subStringCD - finalDate;
                        StringBuffer stringBuffer = new StringBuffer(formatter.format(date));
                        stringBuffer.replace(0, 2, String.valueOf(f));
                        String newDate;
                        if (stringBuffer.length() == 9) {
                            String date = String.valueOf(stringBuffer);
                            newDate = "0" + date;
                        } else {
                            newDate = String.valueOf(stringBuffer);
                        }
                        Meal meal = new Meal(itemName, mAuth.getUid(), calories, itemTotalFat, itemSodium,
                                itemTotalCarbohydrates, itemSugars, itemProtein,
                                Integer.parseInt(quantityAdapterChoice), mealAdapterChoice, whatDayToAddTo,
                                newDate, servings, UUID.randomUUID().toString(),"no url");
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("DayOfWeek");
                        databaseReference.child(whatDayToAddTo).push().setValue(meal).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                addedToDiary(mealAdapterChoice, whatDayToAddTo);
                                DatabaseReference habitReference = FirebaseDatabase.getInstance().getReference("Habits").child(Objects.requireNonNull(mAuth.getUid()));
                                habitReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Habits habits = snapshot.getValue(Habits.class);
                                        if (habits != null) {
                                            switch (mealAdapterChoice) {
                                                case "Breakfast":
                                                    if (habits.getBreakfastNames().contains("No Meals")) {
                                                        for (int i = 0; i < Objects.requireNonNull(habits).getBreakfastNames().size(); i++) {
                                                            if (habits.getBreakfastNames().get(i).equals("No Meals")) {
                                                                habits.getBreakfastNames().remove(i);
                                                                habits.getBreakfastNames().add(itemName);
                                                                break;
                                                            }
                                                        }
                                                    } else if (habits.getBreakfastNames().stream().noneMatch(itemName::equalsIgnoreCase)) {
                                                        habits.getBreakfastNames().add(itemName);
                                                    }
                                                    habitReference.child("breakfastNames").setValue(habits.getBreakfastNames());
                                                    break;
                                                case "Lunch":
                                                    if (habits.getLunchNames().contains("No Meals")) {
                                                        for (int i = 0; i < Objects.requireNonNull(habits).getLunchNames().size(); i++) {
                                                            if (habits.getLunchNames().get(i).equals("No Meals")) {
                                                                habits.getLunchNames().remove(i);
                                                                habits.getLunchNames().add(itemName);
                                                                break;
                                                            }
                                                        }
                                                    } else if (habits.getLunchNames().stream().noneMatch(itemName::equalsIgnoreCase)) {
                                                        habits.getLunchNames().add(itemName);
                                                    }
                                                    habitReference.child("lunchNames").setValue(habits.getLunchNames());
                                                    break;
                                                case "Dinner":
                                                    if (habits.getDinnerNames().contains("No Meals")) {
                                                        for (int i = 0; i < Objects.requireNonNull(habits).getDinnerNames().size(); i++) {
                                                            if (habits.getDinnerNames().get(i).equals("No Meals")) {
                                                                habits.getDinnerNames().remove(i);
                                                                habits.getDinnerNames().add(itemName);
                                                                break;
                                                            }
                                                        }
                                                    } else if (habits.getDinnerNames().stream().noneMatch(itemName::equalsIgnoreCase)) {
                                                        habits.getDinnerNames().add(itemName);
                                                    }
                                                    habitReference.child("dinnerNames").setValue(habits.getDinnerNames());
                                                    break;
                                                case "Other":
                                                    if (habits.getOtherNames().contains("No Meals")) {
                                                        for (int i = 0; i < Objects.requireNonNull(habits).getOtherNames().size(); i++) {
                                                            if (habits.getOtherNames().get(i).equals("No Meals")) {
                                                                habits.getOtherNames().remove(i);
                                                                habits.getOtherNames().add(itemName);
                                                                break;
                                                            }
                                                        }
                                                    } else if (habits.getOtherNames().stream().noneMatch(itemName::equalsIgnoreCase)) {
                                                        habits.getOtherNames().add(itemName);
                                                    }
                                                    habitReference.child("otherNames").setValue(habits.getOtherNames());
                                                    break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        errorOccurred(error.getMessage());
                                    }
                                });
                            }
                        }).addOnFailureListener(e -> errorOccurred(e.getMessage()));
                    }
                });
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void addedToDiary(String choice, String day) {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Meal added to " + choice + " on " + day, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void addedToFavourites() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Item added to Favourites!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void duplicateFavourite() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Error item already exists in Favourites!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void noScannedData() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "No scan data received!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void errorOccurred(String errorMessage) {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Error occurred: " + errorMessage, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void noResultsFound() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Sorry no matches found for searched criteria!", Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(RecipeSearch.this, Management.class));
    }
}



