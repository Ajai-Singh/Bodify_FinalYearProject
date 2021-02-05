package com.example.bodify;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.bodify.Adapters.CardStackAdapter;
import com.example.bodify.Models.Favourite;
import com.example.bodify.Models.Ingredient;
import com.example.bodify.Models.Recipe;
import com.example.bodify.Models.SuggestionMacros;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Objects;

public class FoodSuggester extends Fragment {
    private CardStackLayoutManager cardStackLayoutManager;
    private CardStackAdapter cardStackAdapter;
    private final ArrayList<Ingredient> ingredients = new ArrayList<>();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();
    private final ArrayList<Recipe> recipes = new ArrayList<>();
    private ConstraintLayout constraintLayout;
    private CardStackView cardStackView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_food_suggester, container, false);
        cardStackView = view.findViewById(R.id.cardStack);
        constraintLayout = view.findViewById(R.id.foodSuggesterCL);
        AndroidNetworking.initialize(getContext());
        ImageButton imageButton = view.findViewById(R.id.settings);
        ImageButton info = view.findViewById(R.id.recipeInfo);
        info.setOnClickListener(v -> infoSnackBar());
        imageButton.setOnClickListener(v -> {
            startActivity(new Intent(getContext(),MacroSettings.class));
            getActivity(). getFragmentManager(). popBackStack();
        });
        cardStackLayoutManager = new CardStackLayoutManager(getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
            }

            @Override
            public void onCardSwiped(Direction direction) {
                if (direction == Direction.Right) {
                    Recipe recipe = recipes.get(cardStackLayoutManager.getTopPosition() - 1);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Favourites");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            boolean exists = false;
                            for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                Favourite favourite = userSnapshot.getValue(Favourite.class);
                                if (favourite != null) {
                                    if (favourite.getItemName().equalsIgnoreCase(recipe.getTitle()) && favourite.getUserID().equalsIgnoreCase(userID)) {
                                        exists = true;
                                        break;
                                    }
                                }
                            }
                            if(exists) {
                                duplicateRecordSnackBar();
                            } if(!exists){
                                Favourite favourite = new Favourite(recipe.getTitle(), recipe.getCalories(), recipe.getFats(),
                                        recipe.getSodium(), recipe.getCarbohydrates(), recipe.getSugar(), recipe.getProteins(), userID, recipe.getServings());
                                databaseReference.push().setValue(favourite).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        addedRecord();
                                    } else {
                                        Toast.makeText(getContext(), "Error Occurred" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                if (direction == Direction.Left) {
                    ignoredRecord();
                }
                if(cardStackLayoutManager.getTopPosition() == cardStackAdapter.getItemCount()) {
                    showSnackBar();
                }
            }

            @Override
            public void onCardRewound() {

            }

            @Override
            public void onCardCanceled() {

            }

            @Override
            public void onCardAppeared(View view, int position) {

            }

            @Override
            public void onCardDisappeared(View view, int position) {

            }
        });
        cardStackLayoutManager.setStackFrom(StackFrom.None);
        cardStackLayoutManager.setVisibleCount(3);
        cardStackLayoutManager.setTranslationInterval(8.0f);
        cardStackLayoutManager.setScaleInterval(0.95f);
        cardStackLayoutManager.setSwipeThreshold(0.3f);
        cardStackLayoutManager.setMaxDegree(20.0f);
        cardStackLayoutManager.setDirections(Direction.FREEDOM);
        cardStackLayoutManager.setCanScrollHorizontal(true);
        cardStackLayoutManager.setSwipeableMethod(SwipeableMethod.Manual);
        cardStackLayoutManager.setOverlayInterpolator(new LinearInterpolator());
        assert userID != null;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("RecipeSuggestionMacro").child(userID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SuggestionMacros suggestionMacros = snapshot.getValue(SuggestionMacros.class);
                if(suggestionMacros != null) {
                    APIParsingWithConstraints(suggestionMacros.getCalories(),suggestionMacros.getFats(),suggestionMacros.getProteins(),suggestionMacros.getCarbohydrates());
                } else {
                    APIParsing("https://api.spoonacular.com/recipes/complexSearch?apiKey=de851175d709445bb3d6149a58107a93&addRecipeNutrition=true");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),"Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    public void APIParsing(String request) {
        Log.i("No constrains","" + request);
        AndroidNetworking.get(request)
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "1")
                .addHeaders("token", "1234")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                try {
                    ArrayList<Integer> macros = new ArrayList<>();
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
                        Recipe recipe = new Recipe(id, title, sourceUrl, readyInMinutes, servings, userID, macros.get(0), macros.get(1), macros.get(3), macros.get(8), macros.get(5), macros.get(7), image, ingredients);
                        recipes.add(recipe);
                    }
                    cardStackAdapter = new CardStackAdapter(recipes,getContext());
                    cardStackView.setLayoutManager(cardStackLayoutManager);
                    cardStackView.setAdapter(cardStackAdapter);
                    cardStackView.setItemAnimator(new DefaultItemAnimator());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {
                Toast.makeText(getContext(),"Error Occurred: " + anError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void APIParsingWithConstraints(int calories,int fats,int proteins,int carbs) {
        Log.i("constraints","" + fats);
        AndroidNetworking.get("https://api.spoonacular.com/recipes/complexSearch?apiKey=de851175d709445bb3d6149a58107a93&addRecipeNutrition=true&maxCarbs="+carbs+"&maxFat="+fats+"&maxProtein="+proteins+"&maxCalories="+calories)
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "1")
                .addHeaders("token", "1234")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                try {
                    ArrayList<Integer> macros = new ArrayList<>();
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
                        Recipe recipe = new Recipe(id, title, sourceUrl, readyInMinutes, servings, userID, macros.get(0), macros.get(1), macros.get(3), macros.get(8), macros.get(5), macros.get(7), image, ingredients);
                        recipes.add(recipe);
                    }
                    cardStackAdapter = new CardStackAdapter(recipes,getContext());
                    cardStackView.setLayoutManager(cardStackLayoutManager);
                    cardStackView.setAdapter(cardStackAdapter);
                    cardStackView.setItemAnimator(new DefaultItemAnimator());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }

    public void showSnackBar() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Sorry no results, Modify your settings ->", Snackbar.LENGTH_SHORT).setAction(
                "Settings", v -> {
                    Intent intent = new Intent(getActivity(), MacroSettings.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(0, 0);
                    getActivity().finish();
                }
        );
        snackbar.show();
    }

    public void infoSnackBar() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Note: If you swipe the recipe right it will add the recipe to your favourites", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void duplicateRecordSnackBar() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Error item already exists in Favourites", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void addedRecord() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Item added to favourites", Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void ignoredRecord() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Ignored", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
