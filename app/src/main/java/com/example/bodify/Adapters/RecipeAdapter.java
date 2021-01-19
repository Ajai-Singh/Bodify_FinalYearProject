package com.example.bodify.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.bodify.Models.Favourite;
import com.example.bodify.Models.Meal;
import com.example.bodify.Models.Recipe;
import com.example.bodify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Recipe> recipes;
    private final Context context;
    private String mealType;
    private String adapterChoice;
    private ArrayList<String> servingNumbers;
    private final Date today = new Date();
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

    public RecipeAdapter(ArrayList<Recipe> recipes, Context context) {
        this.recipes = recipes;
        this.context = context;
    }

    @NonNull
    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.setRecipeName(recipes.get(position).getTitle());
        holder.setUrl(recipes.get(position).getSourceUrl());
        holder.setServingQuantity(String.valueOf(recipes.get(position).getServings()));
        holder.setCookDuration(recipes.get(position).getReadyInMinutes());
        holder.url.setOnClickListener(v -> {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(holder.url.getText().toString()));
            v.getContext().startActivity(intent);
        });
        holder.menuOptions.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.menuOptions);
            popupMenu.inflate(R.menu.recipe_menu_options);
            popupMenu.setOnMenuItemClickListener(item -> {
                TextView itemNameFromScan, itemCalories, itemTotalFatT, itemSodiumT, itemTotalCarbohydratesT, itemSugarsT, itemProteinT, itemServingsT;
                ImageView imageView;
                switch (item.getItemId()) {
                    case R.id.information:
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.meal_popup, null);
                        itemNameFromScan = view.findViewById(R.id.mealItemNameTextView);
                        itemCalories = view.findViewById(R.id.mealItemCaloriesTextView);
                        itemTotalFatT = view.findViewById(R.id.mealTotalFatTextView);
                        itemSodiumT = view.findViewById(R.id.mealTotalSodiumTextView);
                        itemTotalCarbohydratesT = view.findViewById(R.id.mealTotalCarbohydrateTextView);
                        itemSugarsT = view.findViewById(R.id.mealTotalSugarTextView);
                        itemProteinT = view.findViewById(R.id.mealTotalProteinTextView);
                        itemServingsT = view.findViewById(R.id.mealAmount);
                        imageView = view.findViewById(R.id.recipeImage);
                        itemNameFromScan.setText(recipes.get(position).getTitle());
                        itemCalories.setText(String.valueOf(recipes.get(position).getCalories()));
                        itemTotalFatT.setText(String.valueOf(recipes.get(position).getFats()));
                        itemSodiumT.setText(String.valueOf(recipes.get(position).getSodium()));
                        itemTotalCarbohydratesT.setText(String.valueOf(recipes.get(position).getCarbohydrates()));
                        itemSugarsT.setText(String.valueOf(recipes.get(position).getSugar()));
                        itemProteinT.setText(String.valueOf(recipes.get(position).getProteins()));
                        itemServingsT.setText(String.valueOf(recipes.get(position).getServings()));
                        Glide.with(context).load(recipes.get(position).getImageUrl()).into(imageView);
                        builder.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
                        builder.setView(view);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        break;
                    case R.id.addToTodaysDiary:
                        final AlertDialog.Builder alertDialogMeals = new AlertDialog.Builder(context);
                        alertDialogMeals.setTitle("Select Meal");
                        final String[] items = {"Breakfast", "Lunch", "Dinner", "Other"};
                        Spinner servings = new Spinner(context);
                        servingNumbers = new ArrayList<>();
                        servingNumbers.add("Select Quantity");
                        for (int i = 0; i < recipes.size(); i++) {
                            if (i == holder.getAdapterPosition()) {
                                Recipe recipe = recipes.get(i);
                                for (int e = 1; e <= recipe.getServings(); e++) {
                                    servingNumbers.add(String.valueOf(e));
                                }
                            }
                        }
                        ArrayAdapter servingAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, servingNumbers) {
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
                        servings.setAdapter(servingAdapter);
                        servingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        servings.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position1, long id) {
                                adapterChoice = parent.getItemAtPosition(position1).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        alertDialogMeals.setView(servings);
                        alertDialogMeals.setSingleChoiceItems(items, -1, (dialog1, which) -> {
                            switch (which) {
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                    mealType = items[which];
                                    break;
                            }
                        }).setPositiveButton("Ok", (dialog12, which) -> {
                            if (!Arrays.asList(items).contains(mealType)) {
                                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
                                dlgAlert.setMessage("Not All Fields are Filled!");
                                dlgAlert.setTitle("Error...");
                                dlgAlert.setPositiveButton("OK", null);
                                dlgAlert.setCancelable(true);
                                dlgAlert.create().show();
                                dlgAlert.setPositiveButton("Ok", (dialog121, which1) -> {
                                });
                            } else {
                                Recipe recipe = null;
                                for (int i = 0; i < recipes.size(); i++) {
                                    if (i == holder.getAdapterPosition()) {
                                        recipe = recipes.get(i);
                                        break;
                                    }
                                }
                                assert recipe != null;
                                Meal meal = new Meal(recipe.getTitle(), userID, recipe.getCalories(),
                                        recipe.getFats(), recipe.getSodium(), recipe.getCarbohydrates(),
                                        recipe.getSugar(), recipe.getProteins(),
                                        Integer.parseInt(adapterChoice), mealType, simpleDateformat.format(today));
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("DayOfWeek");
                                databaseReference.child(simpleDateformat.format(today)).push().setValue(meal).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Successfully saved", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(e -> Toast.makeText(context, "Error Occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        });
                        AlertDialog alert = alertDialogMeals.create();
                        alert.setCanceledOnTouchOutside(false);
                        alert.show();
                        break;
                    case R.id.ingredients:
                        Log.i("ingredients", "" + recipes.get(position).getIngredients());
                        final AlertDialog.Builder ingredientsBuilder = new AlertDialog.Builder(context);
                        LayoutInflater inflater2 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view2 = inflater2.inflate(R.layout.ingredients, null);
                        RecyclerView recyclerView = view2.findViewById(R.id.ingredientsRCV);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        ArrayList<String> a = new ArrayList<>();
                        for (int i = 0; i < recipes.size(); i++) {
                            a.add(recipes.get(i).getIngredients().get(i).getName());
                        }
                        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(recipes.get(position).getIngredients(), context, a);
                        ingredientsBuilder.setNegativeButton("Close", (dialog15, which) -> dialog15.cancel()).setPositiveButton("Order", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Future order of ingredients through Zinc API.... TODO
                                ArrayList<String> test = ingredientsAdapter.test();
                                Log.i("test", "" + test.toString());
                            }
                        });
                        recyclerView.setAdapter(ingredientsAdapter);
                        ingredientsBuilder.setView(view2);
                        AlertDialog ingredientsDialog = ingredientsBuilder.create();
                        ingredientsDialog.show();
                        break;
                    case R.id.addToFavs:
                        final AlertDialog.Builder favouritesBuilder = new AlertDialog.Builder(context);
                        favouritesBuilder.setMessage("Would you like to add this Recipe to your Favourites?")
                                .setNegativeButton("No", (dialog13, which) -> dialog13.cancel()).setPositiveButton("Yes", (dialog14, which) -> {
                            Recipe recipe = null;
                            for (int i = 0; i < recipes.size(); i++) {
                                if (holder.getAdapterPosition() == i) {
                                    recipe = recipes.get(i);
                                    break;
                                }
                            }
                            assert recipe != null;
                            Favourite favourite = new Favourite(recipe.getTitle(), recipe.getCalories(), recipe.getFats(),
                                    recipe.getSodium(), recipe.getCarbohydrates(), recipe.getSugar(), recipe.getProteins(), userID, recipe.getServings());
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                            Query query = reference
                                    .child("Favourites")
                                    .orderByChild("itemName")
                                    .equalTo(recipe.getTitle());
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getChildrenCount() > 0) {
                                        Toast.makeText(context, "Error, Item already exists in favourites!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Favourites");
                                        databaseReference.push().setValue(favourite).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(context, "Item added to favourites", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(context, "Error Occurred" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NotNull DatabaseError databaseError) {
                                    Toast.makeText(context, "Error Occurred: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                        AlertDialog alertDialog = favouritesBuilder.create();
                        alertDialog.setTitle("Attention required!");
                        alertDialog.show();
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    @Override
    public void onClick(View v) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView recipeName, servingQuantity, cookDuration, url, menuOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipeTitle);
            servingQuantity = itemView.findViewById(R.id.Serving);
            cookDuration = itemView.findViewById(R.id.timeToCook);
            url = itemView.findViewById(R.id.source);
            menuOptions = itemView.findViewById(R.id.recipeOptions);
        }

        public void setRecipeName(String rn) {
            recipeName.setText(rn);
        }

        public void setServingQuantity(String sq) {
            servingQuantity.setText(sq.concat(" Servings"));
        }

        public void setCookDuration(String cd) {
            cookDuration.setText(cd.concat(" Mins"));
        }

        public void setUrl(String u) {
            url.setText(u);
        }
    }
}

