package com.example.bodify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.example.bodify.Adapters.CardStackAdapter;
import com.example.bodify.Models.Ingredient;
import com.example.bodify.Models.Recipe;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
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

public class FoodSuggester extends Fragment {
    private CardStackLayoutManager cardStackLayoutManager;
    private CardStackAdapter cardStackAdapter;
    private final ArrayList<Ingredient> ingredients = new ArrayList<>();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = mAuth.getUid();
    private final ArrayList<Recipe> recipes = new ArrayList<>();
    private ConstraintLayout constraintLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_food_suggester, container, false);
        CardStackView cardStackView = view.findViewById(R.id.cardStack);
        constraintLayout = view.findViewById(R.id.foodSuggesterCL);
        AndroidNetworking.initialize(getContext());
        cardStackLayoutManager = new CardStackLayoutManager(getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.i("card swipped", "" + (cardStackLayoutManager.getTopPosition() -1) + " direction: " + direction);
                if (direction == Direction.Right) {
                    Toast.makeText(getContext(), "Card was swipped right", Toast.LENGTH_LONG).show();
                    Log.i("direction right", "yes");
                    Log.i("recipe","" + recipes.get(cardStackLayoutManager.getTopPosition() - 1));
                }
                if (direction == Direction.Left) {
                    Log.i("direction left", "yes");
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
        AndroidNetworking.get("https://api.spoonacular.com/recipes/complexSearch?apiKey=de851175d709445bb3d6149a58107a93&addRecipeNutrition=true&number=2")
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
        return view;
    }

    public void showSnackBar() {
        Snackbar snackbar = Snackbar.make(constraintLayout, "Sorry no results, Modify your settings ->", Snackbar.LENGTH_SHORT).setAction(
                "Settings", v -> {
                    Intent intent = new Intent(getActivity(), Settings.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(0, 0);
                }
        );
        snackbar.show();
    }
}
