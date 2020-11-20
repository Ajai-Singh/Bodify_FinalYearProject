package com.example.bodify;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.bodify.Models.Macro;
import com.example.bodify.Models.Recipe;
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
import com.example.bodify.BarcodeReader.IntentIntegrator;
import com.example.bodify.BarcodeReader.IntentResult;
import com.google.gson.JsonObject;

public class generateRecipes extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private Spinner timeFrame;
    private ArrayList<String> times;
    public static final String API_KEY = "f900229f64f14de9a2698ea63260454b";
    private final ArrayList<Recipe> recipes = new ArrayList<>();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView formatTxt, contentTxt;
    private AlertDialog.Builder builder;
    private TextView itemNameFromScan,itemCalories,itemCaloriesFromFat,itemTotalFat,itemSodium,itemTotalCarbohydrates,itemSugars,itemProtein;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_recipes);
        Button randomMeals = findViewById(R.id.generateMealPlan);
        Button scanBtn = findViewById(R.id.scan_button);
        formatTxt = findViewById(R.id.scan_format);
        contentTxt = findViewById(R.id.scan_content);
        timeFrame = findViewById(R.id.timeFrameSpinner);
        itemNameFromScan = findViewById(R.id.itemNameTextView);
        itemCalories = findViewById(R.id.itemCaloriesTextView);
        itemCaloriesFromFat = findViewById(R.id.caloriesFromFatTextView);
        itemTotalFat = findViewById(R.id.totalFatTextView);
        itemSodium = findViewById(R.id.totalSodiumTextView);
        itemTotalCarbohydrates = findViewById(R.id.totalCarbohydrateTextView);
        itemSugars = findViewById(R.id.totalSugarTextView);
        itemProtein = findViewById(R.id.totalProteinTextView);

        updateSpinners();
        AndroidNetworking.initialize(getApplicationContext());
        randomMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timeFrame.getSelectedItemPosition() == 0) {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(generateRecipes.this);
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
                                    createDayMealPlan(macro.getCalorieConsumption(),timeFrame.getSelectedItem().toString());
                                    break;
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(generateRecipes.this, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.scan_button){
                    IntentIntegrator scanIntegrator = new IntentIntegrator(generateRecipes.this);
                    scanIntegrator.initiateScan();
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            formatTxt.setText(scanFormat);
            contentTxt.setText(scanContent);
            if(scanContent != null) {
                returnNutritionalInformation(scanContent);
            }
        } else{
            Toast.makeText(generateRecipes.this,"No scan data received!", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateSpinners() {
        times = new ArrayList<>();
        times.add("Select time frame.");
        times.add("Day");
        times.add("Week");
        ArrayAdapter<String> adapterTimes = new ArrayAdapter<String>(generateRecipes.this, android.R.layout.simple_spinner_dropdown_item, times) {
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
        timeFrame.setOnItemSelectedListener(generateRecipes.this);
    }
    public void createDayMealPlan(double calories,String time) {
        AndroidNetworking.get("https://api.spoonacular.com/recipes/mealplans/generate?timeFrame="+time+"&targetCalories="+calories+"&diet=vegetarian&exclude=shellfish%2C%20olives&apiKey="+API_KEY)
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "1")
                .addHeaders("token", "1234")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
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
                        Recipe recipe = new Recipe(id,title,sourceUrl,readyInMinutes,servings,userID);
                        recipes.add(recipe);
                    }
                    Intent intent = new Intent(generateRecipes.this,ViewAllRecipes.class);
                    intent.putExtra("recipes",recipes);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(ANError anError) {
                Toast.makeText(generateRecipes.this, "Error Occurred: " + anError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void returnNutritionalInformation(String scanContent) {
        //This line does show that the code works.
        Toast.makeText(generateRecipes.this,scanContent,Toast.LENGTH_LONG).show();
        AndroidNetworking.get("https://api.nutritionix.com/v1_1/item?upc="+scanContent+"&appId=493d4e98&appKey=95b2bb9b721a2b2898f4a4269228ce93")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "1")
                .addHeaders("token", "1234")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
//                this is where the error is occuring
                //this is coming up as null, I believe the reponse is correct but the parsing is wrong
                //must look into
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("info");
                    for(int i = 0; i < jsonArray.length(); i++){
                        String itemId = jsonArray.getJSONObject(i).getString("item_id");
                        String itemName = jsonArray.getJSONObject(i).getString("item_name");
                        int calories = jsonArray.getJSONObject(i).getInt("nf_calories");
                        int caloriesFromFat = jsonArray.getJSONObject(i).getInt("nf_calories_from_fat");
                        int totalFat = jsonArray.getJSONObject(i).getInt("nf_total_fat");
                        int sodium = jsonArray.getJSONObject(i).getInt("nf_sodium");
                        int totalCarbohydrates = jsonArray.getJSONObject(i).getInt("nf_total_carbohydrate");
                        int sugars = jsonArray.getJSONObject(i).getInt("nf_sugars");
                        int proteins = jsonArray.getJSONObject(i).getInt("nf_protein");

                        itemNameFromScan.setText(itemName);
                        itemCalories.setText(String.valueOf(calories));
                        itemCaloriesFromFat.setText(String.valueOf(caloriesFromFat));
                        itemTotalFat.setText(String.valueOf(totalFat));
                        itemSodium.setText(String.valueOf(sodium));
                        itemTotalCarbohydrates.setText(String.valueOf(totalCarbohydrates));
                        itemSugars.setText(String.valueOf(sugars));
                        itemProtein.setText(String.valueOf(proteins));
                        Toast.makeText(getApplicationContext(),itemName,Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(generateRecipes.this, "Error Occurred" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(ANError anError) {
                Toast.makeText(generateRecipes.this, "Error Occurred" + anError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

//    public void createPopUp() {
//        builder = new AlertDialog.Builder(generateRecipes.this);
//        @SuppressLint("InflateParams") View popup = getLayoutInflater().inflate(R.layout.popup,null);
//        builder.setView(popup);
//        builder.create();
//        builder.show();
//    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}

