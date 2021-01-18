package com.example.bodify;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.bodify.Models.Ingredient;
import com.example.bodify.Models.Meal;
import com.example.bodify.Models.Recipe;
import com.example.bodify.Models.Favourite;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import com.example.bodify.BarcodeReader.IntentIntegrator;
import com.example.bodify.BarcodeReader.IntentResult;

public class FoodFinder extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner mealsSpinner;
    private ArrayList<String> mealTypes;
    private EditText choice, nutrition;
    public static final String API_KEY = "de851175d709445bb3d6149a58107a93";
    private final ArrayList<Recipe> recipes = new ArrayList<>();
    private final ArrayList<Ingredient> ingredients = new ArrayList<>();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
    private Button search, scan, find;
    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter;
    private final ArrayList<Integer> macros = new ArrayList<>();
    private String quantityAdapterChoice;
    private String mealAdapterChoice;
    private ArrayList<String> servingNumbers;
    private final Date today = new Date();
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
    private RequestQueue queue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_finder);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Food Finder");
        queue = Volley.newRequestQueue(FoodFinder.this);
        AndroidNetworking.initialize(FoodFinder.this);
        mealsSpinner = findViewById(R.id.spinner);
        choice = findViewById(R.id.foodName);
        search = findViewById(R.id.searchMeals);
        recyclerView = findViewById(R.id.recipeRCV);
        scan = findViewById(R.id.button4);
        find = findViewById(R.id.nutritionCheckButton);
        nutrition = findViewById(R.id.nutritionCheck);
        updateSpinners();
        nutritionChecker();
        mealSearch();
        scanner();
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
        ArrayAdapter<String> adapterMeals = new ArrayAdapter<String>(FoodFinder.this, android.R.layout.simple_spinner_dropdown_item, mealTypes) {
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
        mealsSpinner.setOnItemSelectedListener(FoodFinder.this);
    }

    public void mealSearch() {
        search.setOnClickListener(v -> {
            if (!recipes.isEmpty()) {
                recipes.clear();
                recipeAdapter.notifyDataSetChanged();
            }
            if (mealsSpinner.getSelectedItemPosition() == 0 || TextUtils.isEmpty(choice.getText().toString())) {
                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(FoodFinder.this);
                dlgAlert.setMessage("Not All Fields are Filled!");
                dlgAlert.setTitle("Error...");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                dlgAlert.setPositiveButton("Ok",
                        (dialog, which) -> {
                        });
            } else {
                AndroidNetworking.get("https://api.spoonacular.com/recipes/complexSearch?query=" + choice.getText().toString() + "&apiKey=" + API_KEY + "&addRecipeNutrition=true&type=" + mealsSpinner.getSelectedItem().toString().toLowerCase())
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
                                int id = jsonArray.getJSONObject(i).getInt("id");
                                String title = jsonArray.getJSONObject(i).getString("title");
                                String readyInMinutes = jsonArray.getJSONObject(i).getString("readyInMinutes");
                                int servings = jsonArray.getJSONObject(i).getInt("servings");
                                String sourceUrl = jsonArray.getJSONObject(i).getString("sourceUrl");
                                String image = jsonArray.getJSONObject(i).getString("image");
                                jsonObject1 = jsonArray.getJSONObject(i).getJSONObject("nutrition");
                                JSONArray jsonArray1 = jsonObject1.getJSONArray("nutrients");
                                JSONArray ingredientsArray = jsonObject1.getJSONArray("ingredients");
                                for(int x = 0; x < ingredientsArray.length(); x++) {
                                    String ingredientName = ingredientsArray.getJSONObject(x).getString("name");
                                    Double ingredientAmount = ingredientsArray.getJSONObject(x).getDouble("amount");
                                    String unit = ingredientsArray.getJSONObject(x).getString("unit");
                                    Ingredient ingredient = new Ingredient(ingredientName,ingredientAmount,unit);
                                    ingredients.add(ingredient);

                                }
                                for (int e = 0; e < jsonArray1.length(); e++) {
                                    macros.add(jsonArray1.getJSONObject(e).getInt("amount"));
                                }
                                Recipe recipe = new Recipe(id, title, sourceUrl, readyInMinutes, servings, userID, macros.get(0), macros.get(1), macros.get(3), macros.get(8), macros.get(5), macros.get(7), image,ingredients);
                                recipes.add(recipe);
                            }
                            recyclerView.setHasFixedSize(true);
                            recyclerView.setLayoutManager(new LinearLayoutManager(FoodFinder.this));
                            recipeAdapter = new RecipeAdapter(recipes, FoodFinder.this);
                            recyclerView.setAdapter(recipeAdapter);

                        } catch (
                                JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(FoodFinder.this, "Error Occurred: " + anError.getMessage(), Toast.LENGTH_SHORT).show();
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
                final String url = "https://calorieninjas.p.rapidapi.com/v1/nutrition?query="+nutrition.getText().toString();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        response -> {
                            try {
                                AlertDialog.Builder builder = new AlertDialog.Builder(FoodFinder.this);
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
                                    Toast.makeText(FoodFinder.this, "No results found! Try again", Toast.LENGTH_SHORT).show();
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
                        error -> Toast.makeText(FoodFinder.this, "Error Occurred" + error.getMessage(), Toast.LENGTH_SHORT).show()
                ) {
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

    public void scanner() {
        scan.setOnClickListener(v -> {
            if (v.getId() == R.id.button4) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(FoodFinder.this);
                scanIntegrator.initiateScan();
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
            Toast.makeText(FoodFinder.this, "No scan data received!", Toast.LENGTH_SHORT).show();
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
                    String itemId = jsonObject.getString("item_id");
                    String itemName = jsonObject.getString("item_name");
                    int calories = jsonObject.getInt("nf_calories");
                    int caloriesFromFat = jsonObject.getInt("nf_calories_from_fat");
                    int totalFat = jsonObject.getInt("nf_total_fat");
                    int sodium = jsonObject.getInt("nf_sodium");
                    int totalCarbohydrates = jsonObject.getInt("nf_total_carbohydrate");
                    int sugars = jsonObject.getInt("nf_sugars");
                    int proteins = jsonObject.getInt("nf_protein");
                    int servings = jsonObject.getInt("nf_servings_per_container");
                    createPost(itemName, calories, caloriesFromFat, totalFat, sodium, totalCarbohydrates, sugars, proteins, servings);
                } catch (JSONException e) {
                    Toast.makeText(FoodFinder.this, "Error Occurred" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(ANError anError) {
                Toast.makeText(FoodFinder.this, "Error Occurred" + anError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createPost(final String itemName, final int calories,
                           final int caloriesFromFat, final int itemTotalFat, final int itemSodium,
                           final int itemTotalCarbohydrates, final int itemSugars, final int itemProtein,
                           final int servings) {
        TextView itemNameFromScan, itemCalories, itemCaloriesFromFat, itemTotalFatT, itemSodiumT, itemTotalCarbohydratesT, itemSugarsT, itemProteinT, itemServingsT;
        Button add, addToDiary;
        AlertDialog.Builder builder = new AlertDialog.Builder(FoodFinder.this);
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
        add = view.findViewById(R.id.save);
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
        add.setOnClickListener(v -> {
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            assert firebaseUser != null;
            String userID = firebaseUser.getUid();
            Favourite favourite = new Favourite(itemName, calories, itemTotalFat, itemSodium, itemTotalCarbohydrates, itemSugars, itemProtein, userID, servings);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Favourites").push().setValue(favourite).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(FoodFinder.this, "Item added to favourites", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FoodFinder.this, "Error Occurred" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        addToDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Spinner meals;
                final Spinner quantity;
                AlertDialog.Builder builder = new AlertDialog.Builder(FoodFinder.this);
                @SuppressLint("InflateParams")
                View view = getLayoutInflater().inflate(R.layout.test, null);
                meals = view.findViewById(R.id.when);
                quantity = view.findViewById(R.id.quan);
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
                ArrayAdapter servingAdapter = new ArrayAdapter(FoodFinder.this, android.R.layout.simple_spinner_dropdown_item, servingNumbers) {
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
                ArrayAdapter mealsAdapter = new ArrayAdapter(FoodFinder.this, android.R.layout.simple_spinner_dropdown_item, mealTypes) {
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
                builder.setPositiveButton("Ok", (dialog, which) -> {
                    if (meals.getSelectedItemPosition() == 0 || quantity.getSelectedItemPosition() == 0) {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(FoodFinder.this);
                        dlgAlert.setMessage("Not All Fields are Filled!");
                        dlgAlert.setTitle("Error...");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.create().show();
                        dlgAlert.setPositiveButton("Ok",
                                (dialog1, which1) -> {
                                });
                    } else {
                        Meal meal = new Meal(itemName, userID, calories, itemTotalFat, itemSodium, itemTotalCarbohydrates, itemSugars, itemProtein, Integer.parseInt(quantityAdapterChoice), mealAdapterChoice, simpleDateformat.format(today));
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("DayOfWeek");
                        databaseReference.child(simpleDateformat.format(today)).push().setValue(meal).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(FoodFinder.this, "Successfully saved", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e -> Toast.makeText(FoodFinder.this, "Error Occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                    }
                }).setNegativeButton("Close", (dialog, which) -> dialog.cancel());
                builder.setView(view);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}



