package com.example.bodify.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
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
import com.example.bodify.Models.Ingredient;
import com.example.bodify.Models.Meal;
import com.example.bodify.Models.Recipe;
import com.example.bodify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {
    private List<Recipe> recipes;
    private final Context context;
    private String whatDayToAddTo;
    private String quantityAdapterChoice;
    private String mealAdapterChoice;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private final Date date = new Date(); private ArrayList<String> servingNumbers;
    private final Date today = new Date();
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

    public CardStackAdapter(List<Recipe> recipes,Context context) {
        this.recipes = recipes;
        this.context = context;
    }

    @NonNull
    @Override
    public CardStackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardStackAdapter.ViewHolder holder, int position) {
       holder.setData(recipes.get(position));
        holder.recipeSource.setOnClickListener(v -> {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(holder.recipeSource.getText().toString()));
            v.getContext().startActivity(intent);
        });
       holder.options.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               PopupMenu popupMenu = new PopupMenu(context, holder.options);
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
                           AlertDialog.Builder diaryBuilder = new AlertDialog.Builder(context);
                           final Spinner meals;
                           final Spinner quantity;
                           final Spinner whatDay;
                           @SuppressLint("InflateParams")
                           LayoutInflater diaryInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                           View diaryView = diaryInflater.inflate(R.layout.addtodiarypopup, null);
                           meals = diaryView.findViewById(R.id.when);
                           quantity = diaryView.findViewById(R.id.quan);
                           whatDay = diaryView.findViewById(R.id.dayOfWeek);
                           final ArrayList<String> daysOfWeek = new ArrayList<>();
                           daysOfWeek.add("Monday");
                           daysOfWeek.add("Tuesday");
                           daysOfWeek.add("Wednesday");
                           daysOfWeek.add("Thursday");
                           daysOfWeek.add("Friday");
                           daysOfWeek.add("Saturday");
                           daysOfWeek.add("Sunday");
                           ArrayList<String> daysToShow = new ArrayList<>();
                           String dayPosition = null;
                           for (int i = 0; i < daysOfWeek.size(); i++) {
                               if (simpleDateformat.format(today).equalsIgnoreCase(daysOfWeek.get(i))) {
                                   String a = daysOfWeek.get(i);
                                   dayPosition = String.valueOf(daysOfWeek.indexOf(a));
                                   break;
                               }
                           }
                           for (int i = 0; i <= Integer.parseInt(Objects.requireNonNull(dayPosition)); i++) {
                               daysToShow.add(daysOfWeek.get(i));
                           }
                           int defaultP = 0;
                           for (int i = 0; i < daysToShow.size(); i++) {
                               if (simpleDateformat.format(today).equalsIgnoreCase(daysToShow.get(i))) {
                                   defaultP = i;
                                   break;
                               }
                           }
                           ArrayAdapter dayAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, daysToShow);
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
                           for (int i = 1; i <= recipes.get(position).getServings(); i++) {
                               servingNumbers.add(String.valueOf(i));
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
                           ArrayAdapter mealsAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, mealTypes) {
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
                           String finalDayPosition = dayPosition;
                           diaryBuilder.setPositiveButton("Ok", (diaryDialog, which) ->
                           {
                               if (meals.getSelectedItemPosition() == 0 || quantity.getSelectedItemPosition() == 0) {
                                   AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
                                   dlgAlert.setMessage("Not All Fields are Filled!");
                                   dlgAlert.setTitle("Error...");
                                   dlgAlert.setPositiveButton("OK", null);
                                   dlgAlert.setCancelable(true);
                                   dlgAlert.create().show();
                                   dlgAlert.setPositiveButton("Ok",
                                           (dialog1, which1) -> {
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
                                   String positionToAddTo = null;
                                   for(int i = 0; i < daysOfWeek.size(); i ++) {
                                       if(daysOfWeek.get(i).equalsIgnoreCase(whatDayToAddTo)) {
                                           String a = daysOfWeek.get(i);
                                           positionToAddTo = String.valueOf(daysOfWeek.indexOf(a));
                                           break;
                                       }
                                   }
                                   assert positionToAddTo != null;
                                   int finalDate = Integer.parseInt(finalDayPosition) - Integer.parseInt(positionToAddTo);
                                   int subStringCD = Integer.parseInt(formatter.format(date).substring(0,2));
                                   int f = subStringCD - finalDate;
                                   StringBuilder stringBuffer = new StringBuilder(formatter.format(date));
                                   stringBuffer.replace(0,2,String.valueOf(f));
                                   Meal meal = new Meal(recipe.getTitle(), userID, recipe.getCalories(),
                                           recipe.getFats(), recipe.getSodium(), recipe.getCarbohydrates(),
                                           recipe.getSugar(), recipe.getProteins(),
                                           Integer.parseInt(quantityAdapterChoice), mealAdapterChoice, whatDayToAddTo,String.valueOf(stringBuffer));
                                   DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("DayOfWeek");
                                   databaseReference.child(whatDayToAddTo).push().setValue(meal).addOnCompleteListener(task -> {
                                       if (task.isSuccessful()) {
                                           Toast.makeText(context, "Successfully saved", Toast.LENGTH_SHORT).show();
                                       }
                                   }).addOnFailureListener(e -> Toast.makeText(context, "Error Occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                               }
                           }).setNegativeButton("Close", (diaryDialog, which) -> diaryDialog.cancel());
                           diaryBuilder.setView(diaryView);
                           AlertDialog diaryAlertDialog = diaryBuilder.create();
                           diaryAlertDialog.show();
                           break;
                       case R.id.ingredients:
                           ArrayList<Ingredient> ingredients = recipes.get(position).getIngredients();
                           final AlertDialog.Builder ingredientsBuilder = new AlertDialog.Builder(context);
                           LayoutInflater inflater2 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                           View view2 = inflater2.inflate(R.layout.ingredients, null);
                           RecyclerView recyclerView = view2.findViewById(R.id.ingredientsRCV);
                           recyclerView.setHasFixedSize(true);
                           recyclerView.setLayoutManager(new LinearLayoutManager(context));
                           ArrayList<String> sendIngredients = new ArrayList<>();
                           for(int i = 0; i < ingredients.size(); i++) {
                               sendIngredients.add(ingredients.get(i).getName());
                           }
                           IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(recipes.get(position).getIngredients(), sendIngredients);
                           ingredientsBuilder.setNegativeButton("Close", (dialog15, which) -> dialog15.cancel()).setPositiveButton("Order", (dialog12, which) -> {
                               //Future order of ingredients through Zinc API.... TODO
                               ArrayList<String> test = ingredientsAdapter.test();
                               Log.i("test", "" + test.toString());
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
                               DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Favourites");
                               Recipe finalRecipe = recipe;
                               databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                                       boolean exists = false;
                                       for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                           Favourite favourite = userSnapshot.getValue(Favourite.class);
                                           if (favourite != null) {
                                               if (favourite.getItemName().equalsIgnoreCase(finalRecipe.getTitle()) && favourite.getUserID().equalsIgnoreCase(userID)) {
                                                   exists = true;
                                                   break;
                                               }
                                           }
                                       }
                                       if(exists) {
                                           Toast.makeText(context, "Error item already exists in Favourites", Toast.LENGTH_SHORT).show();
                                       } if(!exists){
                                           assert finalRecipe != null;
                                           Favourite favourite = new Favourite(finalRecipe.getTitle(), finalRecipe.getCalories(), finalRecipe.getFats(),
                                                   finalRecipe.getSodium(), finalRecipe.getCarbohydrates(), finalRecipe.getSugar(), finalRecipe.getProteins(), userID, finalRecipe.getServings());
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
                                   public void onCancelled(@NonNull DatabaseError error) {
                                       Toast.makeText(context, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
           }
       });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView recipeImage;
        private final TextView recipeName;
        private final TextView timeToCook;
        private final TextView servings;
        private final TextView recipeSource;
        private final TextView options;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipeCardImage);
            recipeName = itemView.findViewById(R.id.recipeCardTitle);
            recipeSource = itemView.findViewById(R.id.recipeCardSource);
            timeToCook = itemView.findViewById(R.id.timeToCookRecipeCard);
            servings = itemView.findViewById(R.id.recipeCardServings);
            options = itemView.findViewById(R.id.recipeCardViewOptions);
        }
        @SuppressLint("SetTextI18n")
        public void setData(Recipe data) {
            Picasso.get()
                    .load(data.getImageUrl())
                    .fit()
                    .centerCrop()
                    .into(recipeImage);
            recipeName.setText(data.getTitle());
            timeToCook.setText(data.getReadyInMinutes() + " Minutes");
            servings.setText(data.getServings() + " Servings");
            recipeSource.setText(data.getSourceUrl());
        }
    }
    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public void addRecipes(List<Recipe> recipes) {
        this.recipes.addAll(recipes);
    }
}
