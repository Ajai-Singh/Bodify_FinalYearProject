package com.example.bodify;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.bodify.Models.Macro;
import com.example.bodify.Models.Recipe;
import com.example.bodify.Models.Favourite;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Objects;
import com.example.bodify.BarcodeReader.IntentIntegrator;
import com.example.bodify.BarcodeReader.IntentResult;

public class GenerateRecipes extends Fragment implements AdapterView.OnItemSelectedListener {
    private Spinner timeFrame;
    private ArrayList<String> times;
    public static final String API_KEY = "f900229f64f14de9a2698ea63260454b";
    private final ArrayList<Recipe> recipes = new ArrayList<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView formatTxt, contentTxt;
    private Button randomMeals, scanBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_generate_recipes, container, false);
        randomMeals = view.findViewById(R.id.generateMealPlan);
        scanBtn = view.findViewById(R.id.scan_button);
        formatTxt = view.findViewById(R.id.scan_format);
        contentTxt = view.findViewById(R.id.scan_content);
        timeFrame = view.findViewById(R.id.timeFrameSpinner);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateSpinners();
        generateMeals();
        scan();
    }

    public void generateMeals() {
        AndroidNetworking.initialize(getContext());
        randomMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeFrame.getSelectedItemPosition() == 0) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getContext());
                    dlgAlert.setMessage("Not All Fields are Filled!");
                    dlgAlert.setTitle("Error...");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                } else {
                    final String userID = mAuth.getUid();
                    assert userID != null;
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Macros");
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Macro macro = dataSnapshot.getValue(Macro.class);
                                assert macro != null;
                                if (macro.getUserId().equals(userID)) {
                                    createDayMealPlan(macro.getCalorieConsumption(), timeFrame.getSelectedItem().toString());
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

        public void scan () {
            scanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.scan_button) {
                        IntentIntegrator scanIntegrator = new IntentIntegrator((Activity) getContext());
                        scanIntegrator.initiateScan();
                    }
                }
            });
        }


        public void onActivityResult ( int requestCode, int resultCode, Intent intent){
            super.onActivityResult(requestCode, resultCode, intent);
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (scanningResult != null) {
                String scanContent = scanningResult.getContents();
                String scanFormat = scanningResult.getFormatName();
                formatTxt.setText(scanFormat);
                contentTxt.setText(scanContent);
                if (scanContent != null) {
                    returnNutritionalInformation(scanContent);
                }
            } else {
                Toast.makeText(getContext(), "No scan data received!", Toast.LENGTH_SHORT).show();
            }
        }

        public void updateSpinners () {
            times = new ArrayList<>();
            times.add("Select time frame.");
            times.add("Day");
            times.add("Week");
            ArrayAdapter<String> adapterTimes = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, times) {
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
            adapterTimes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            timeFrame.setAdapter(adapterTimes);
            timeFrame.setOnItemSelectedListener(GenerateRecipes.this);
        }

        public void createDayMealPlan ( double calories, String time){
            AndroidNetworking.get("https://api.spoonacular.com/recipes/mealplans/generate?timeFrame=" + time + "&targetCalories=" + calories + "&diet=vegetarian&exclude=shellfish%2C%20olives&apiKey=" + API_KEY)
                    .addPathParameter("pageNumber", "0")
                    .addQueryParameter("limit", "1")
                    .addHeaders("token", "1234")
                    .setTag("test")
                    .setPriority(Priority.LOW)
                    .build().getAsString(new StringRequestListener() {
                @Override
                public void onResponse(String response) {
                    mAuth = FirebaseAuth.getInstance();
                    final String userID = mAuth.getUid();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("meals");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            int id = jsonArray.getJSONObject(i).getInt("id");
                            String title = jsonArray.getJSONObject(i).getString("title");
                            String readyInMinutes = jsonArray.getJSONObject(i).getString("readyInMinutes");
                            String servings = jsonArray.getJSONObject(i).getString("servings");
                            String sourceUrl = jsonArray.getJSONObject(i).getString("sourceUrl");
                            Recipe recipe = new Recipe(id, title, sourceUrl, readyInMinutes, servings, userID);
                            recipes.add(recipe);
                        }
                        Intent intent = new Intent(getContext(), ViewAllRecipes.class);
                        intent.putExtra("recipes", recipes);
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Toast.makeText(getContext(), "Error Occurred: " + anError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void returnNutritionalInformation (String scanContent){
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
                        Toast.makeText(getContext(), "Error Occurred" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(ANError anError) {
                    Toast.makeText(getContext(), "Error Occurred" + anError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void createPost ( final String itemName, final int calories,
        final int caloriesFromFat, final int itemTotalFat, final int itemSodium,
        final int itemTotalCarbohydrates, final int itemSugars, final int itemProtein,
        final int servings){
            TextView itemNameFromScan, itemCalories, itemCaloriesFromFat, itemTotalFatT, itemSodiumT, itemTotalCarbohydratesT, itemSugarsT, itemProteinT, itemServingsT;
            Button add;
            final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View view = getLayoutInflater().inflate(R.layout.popup, null);
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
            itemNameFromScan.setText(itemName);
            itemCalories.setText(String.valueOf(calories));
            itemCaloriesFromFat.setText(String.valueOf(caloriesFromFat));
            itemTotalFatT.setText(String.valueOf(itemTotalFat));
            itemSodiumT.setText(String.valueOf(itemSodium));
            itemTotalCarbohydratesT.setText(String.valueOf(itemTotalCarbohydrates));
            itemSugarsT.setText(String.valueOf(itemSugars));
            itemProteinT.setText(String.valueOf(itemProtein));
            itemServingsT.setText(String.valueOf(servings));
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    assert firebaseUser != null;
                    String userID = firebaseUser.getUid();
                    Favourite favourite = new Favourite(itemName, calories, caloriesFromFat, itemTotalFat, itemSodium, itemTotalCarbohydrates, itemSugars, itemProtein, userID, servings);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("Favourites").push().setValue(favourite).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Item added to favourites", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Error Occurred" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            builder.setView(view);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        @Override
        public void onItemSelected (AdapterView < ? > parent, View view,int position, long id){

        }

        @Override
        public void onNothingSelected (AdapterView < ? > parent){
        }
    }

