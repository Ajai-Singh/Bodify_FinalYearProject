package com.example.bodify.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bodify.Models.Favourite;
import com.example.bodify.Models.Habits;
import com.example.bodify.Models.Meal;
import com.example.bodify.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Favourite> favourites;
    private final Context context;
    private ArrayList<String> servingNumbers;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");
    private String whatDayToAddTo, quantityAdapterChoice, mealAdapterChoice;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    private final Date date = new Date();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final RelativeLayout relativeLayout;

    public FavouriteAdapter(ArrayList<Favourite> favourites, Context context, RelativeLayout relativeLayout) {
        this.favourites = favourites;
        this.context = context;
        this.relativeLayout = relativeLayout;
    }

    @NonNull
    @Override
    public FavouriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(@NonNull final FavouriteAdapter.ViewHolder holder, final int position) {
        holder.setItemName(favourites.get(position).getItemName());
        holder.setCaloriesConsumed(favourites.get(position).getCalories());
        holder.setFats(favourites.get(position).getItemTotalFat());
        holder.setProteins(favourites.get(position).getItemProtein());
        holder.setCarbs(favourites.get(position).getItemTotalCarbohydrates());
        holder.buttonViewOption.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.buttonViewOption);
            popup.inflate(R.menu.rcv_menu_options);
            popup.setOnMenuItemClickListener(item -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                switch (item.getItemId()) {
                    case R.id.viewFavOnline:
                        if (favourites.get(position).getSourceUrl().equals("no url")) {
                            Snackbar snackbar = Snackbar.make(relativeLayout, "Sorry no recipe available!", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        } else {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(favourites.get(position).getSourceUrl()));
                            context.startActivity(browserIntent);
                        }
                        break;
                    case R.id.addToDiary:
                        final Spinner meals, quantity, whatDay;
                        @SuppressLint("InflateParams")
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.addtodiarypopup, null);
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
                        String dayPosition = null;
                        for (int i = 0; i < daysOfWeek.size(); i++) {
                            if (simpleDateformat.format(date).equalsIgnoreCase(daysOfWeek.get(i))) {
                                dayPosition = String.valueOf(daysOfWeek.indexOf(daysOfWeek.get(i)));
                                break;
                            }
                        }
                        for (int i = 0; i <= Integer.parseInt(Objects.requireNonNull(dayPosition)); i++) {
                            daysToShow.add(daysOfWeek.get(i));
                        }
                        ArrayAdapter dayAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, daysToShow);
                        whatDay.setAdapter(dayAdapter);
                        whatDay.setSelection(daysToShow.indexOf(simpleDateformat.format(date)));
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
                        for (int i = 1; i <= favourites.get(position).getNumberOfServings(); i++) {
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
                        builder.setPositiveButton("Create", (dialog, which) -> {
                        });
                        builder.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
                        builder.setView(view);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                            if (meals.getSelectedItemPosition() == 0 || quantity.getSelectedItemPosition() == 0) {
                                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context);
                                dlgAlert.setMessage("Not All Fields are Filled!");
                                dlgAlert.setTitle("Error...");
                                dlgAlert.setPositiveButton("Ok", (dialog12, which) -> dialog12.dismiss());
                                dlgAlert.setCancelable(true);
                                dlgAlert.create().show();
                            } else {
                                dialog.dismiss();
                                Favourite favourite = favourites.get(position);
                                assert favourite != null;
                                String positionToAddTo = null;
                                for (int i = 0; i < daysOfWeek.size(); i++) {
                                    if (daysOfWeek.get(i).equalsIgnoreCase(whatDayToAddTo)) {
                                        positionToAddTo = String.valueOf(daysOfWeek.indexOf(daysOfWeek.get(i)));
                                        break;
                                    }
                                }
                                assert positionToAddTo != null;
                                int finalDate = Integer.parseInt(finalDayPosition) - Integer.parseInt(positionToAddTo);
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
                                Meal meal = new Meal(favourite.getItemName(), favourite.getUserID(), favourite.getCalories()
                                        , favourite.getItemTotalFat(), favourite.getItemSodium(),
                                        favourite.getItemTotalCarbohydrates(), favourite.getItemSugars(),
                                        favourite.getItemProtein(), Integer.parseInt(quantityAdapterChoice), mealAdapterChoice, whatDayToAddTo, newDate, favourite.getNumberOfServings(), UUID.randomUUID().toString(), favourite.getSourceUrl());
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("DayOfWeek");
                                databaseReference.child(whatDayToAddTo).push().setValue(meal).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Snackbar snackbar = Snackbar.make(relativeLayout, favourite.getItemName() + " added to " + mealAdapterChoice.toLowerCase() + " on " + whatDayToAddTo, Snackbar.LENGTH_SHORT);
                                        snackbar.show();
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
                                                                        habits.getBreakfastNames().add(favourite.getItemName());
                                                                        break;
                                                                    }
                                                                }
                                                            } else if (habits.getBreakfastNames().stream().noneMatch(favourite.getItemName()::equalsIgnoreCase)) {
                                                                habits.getBreakfastNames().add(favourite.getItemName());
                                                            }
                                                            habitReference.child("breakfastNames").setValue(habits.getBreakfastNames());
                                                            break;
                                                        case "Lunch":
                                                            if (habits.getLunchNames().contains("No Meals")) {
                                                                for (int i = 0; i < Objects.requireNonNull(habits).getLunchNames().size(); i++) {
                                                                    if (habits.getLunchNames().get(i).equals("No Meals")) {
                                                                        habits.getLunchNames().remove(i);
                                                                        habits.getLunchNames().add(favourite.getItemName());
                                                                        break;
                                                                    }
                                                                }
                                                            } else if (habits.getLunchNames().stream().noneMatch(favourite.getItemName()::equalsIgnoreCase)) {
                                                                habits.getLunchNames().add(favourite.getItemName());
                                                            }
                                                            habitReference.child("lunchNames").setValue(habits.getLunchNames());
                                                            break;
                                                        case "Dinner":
                                                            if (habits.getDinnerNames().contains("No Meals")) {
                                                                for (int i = 0; i < Objects.requireNonNull(habits).getDinnerNames().size(); i++) {
                                                                    if (habits.getDinnerNames().get(i).equals("No Meals")) {
                                                                        habits.getDinnerNames().remove(i);
                                                                        habits.getDinnerNames().add(favourite.getItemName());
                                                                        break;
                                                                    }
                                                                }
                                                            } else if (habits.getDinnerNames().stream().noneMatch(favourite.getItemName()::equalsIgnoreCase)) {
                                                                habits.getDinnerNames().add(favourite.getItemName());
                                                            }
                                                            habitReference.child("dinnerNames").setValue(habits.getDinnerNames());
                                                            break;
                                                        case "Other":
                                                            if (habits.getOtherNames().contains("No Meals")) {
                                                                for (int i = 0; i < Objects.requireNonNull(habits).getOtherNames().size(); i++) {
                                                                    if (habits.getOtherNames().get(i).equals("No Meals")) {
                                                                        habits.getOtherNames().remove(i);
                                                                        habits.getOtherNames().add(favourite.getItemName());
                                                                        break;
                                                                    }
                                                                }
                                                            } else if (habits.getOtherNames().stream().noneMatch(favourite.getItemName()::equalsIgnoreCase)) {
                                                                habits.getOtherNames().add(favourite.getItemName());
                                                            }
                                                            habitReference.child("otherNames").setValue(habits.getOtherNames());
                                                            break;
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Snackbar snackbar = Snackbar.make(relativeLayout, "Error occurred: " + error.getMessage(), Snackbar.LENGTH_SHORT);
                                                snackbar.show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(e -> {
                                    Snackbar snackbar = Snackbar.make(relativeLayout, "Error occurred: " + e.getMessage(), Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                });
                            }
                        });
                        break;
                    case R.id.DeleteFromFavourites:
                        builder.setMessage("Are you sure you want to delete this item from your Favourites?").setNegativeButton("No", (dialog1, which) -> dialog1.cancel()).setPositiveButton("Yes", (dialog1, which) -> {
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Favourites");
                            databaseReference.child(favourites.get(position).getId()).removeValue();
                            Snackbar snackbar = Snackbar.make(relativeLayout, favourites.get(position).getItemName() + " removed from Favourites!", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            favourites.clear();
                            notifyDataSetChanged();
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.setTitle("Attention required!");
                        alertDialog.show();
                        break;
                }
                return false;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return favourites.size();
    }


    @Override
    public void onClick(View v) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemName, buttonViewOption, caloriesConsumed, fats, proteins, carbs;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemNameTextField);
            buttonViewOption = itemView.findViewById(R.id.textViewOptions);
            caloriesConsumed = itemView.findViewById(R.id.favouriteCalories);
            fats = itemView.findViewById(R.id.favouritesFats);
            proteins = itemView.findViewById(R.id.favouritesProtein);
            carbs = itemView.findViewById(R.id.favouritesCarbs);
        }

        public void setItemName(String name) {
            itemName.setText(name);
        }

        public void setCaloriesConsumed(int cc) {
            caloriesConsumed.setText(String.valueOf(cc));
        }

        public void setFats(int f) {
            fats.setText(String.valueOf(f).concat("F"));
        }

        public void setProteins(int p) {
            proteins.setText(String.valueOf(p).concat("P"));
        }

        public void setCarbs(int c) {
            carbs.setText(String.valueOf(c).concat("C"));
        }
    }
}
