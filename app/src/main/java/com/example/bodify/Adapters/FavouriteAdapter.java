package com.example.bodify.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.Models.Favourite;
import com.example.bodify.Models.Meal;
import com.example.bodify.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Favourite> favourites;
    private final Context context;
    private String mealType;
    private String adapterChoice;
    private ArrayList<String> servingNumbers;
    private final Date today = new Date();
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE");

    public FavouriteAdapter(ArrayList<Favourite> favourites, Context context) {
        this.favourites = favourites;
        this.context = context;
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
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.addToDiary:
                        final AlertDialog.Builder alertDialogMeals = new AlertDialog.Builder(context);
                        alertDialogMeals.setTitle("Select Meal");
                        final String[] items = {"Breakfast", "Lunch", "Dinner", "Other"};
                        Spinner servings = new Spinner(context);
                        servingNumbers = new ArrayList<>();
                        servingNumbers.add("Select Quantity");
                        for (int i = 0; i < favourites.size(); i++) {
                            if (i == holder.getAdapterPosition()) {
                                Favourite favourite = favourites.get(i);
                                for (int e = 1; e <= favourite.getNumberOfServings(); e++) {
                                    servingNumbers.add(String.valueOf(e));
                                }
                            }
                        }
                        ArrayAdapter<String> servingAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, servingNumbers){
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
                        alertDialogMeals.setSingleChoiceItems(items, -1, (dialog, which) -> {
                            switch (which) {
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                    mealType = items[which];
                                    break;
                            }
                        }).setPositiveButton("Ok", (dialog, which) -> {
                            if (!Arrays.asList(items).contains(mealType) || servings.getSelectedItemPosition() == 0) {
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
                                Favourite favourite = null;
                                for (int i = 0; i < favourites.size(); i++) {
                                    if (i == holder.getAdapterPosition()) {
                                        favourite = favourites.get(i);
                                        break;
                                    }
                                }
                                assert favourite != null;
                                Meal meal = new Meal(favourite.getItemName(), favourite.getUserID(), favourite.getCalories()
                                        , favourite.getItemTotalFat(), favourite.getItemSodium(),
                                        favourite.getItemTotalCarbohydrates(), favourite.getItemSugars(),
                                        favourite.getItemProtein(), Integer.parseInt(adapterChoice), mealType,simpleDateformat.format(today));
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("DayOfWeek");
                                databaseReference.child(simpleDateformat.format(today)).push().setValue(meal).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(context, "Successfully saved", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).addOnFailureListener(e -> Toast.makeText(context, "Error Occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        }).setNegativeButton("Close", (dialog, which) -> dialog.cancel());
                        AlertDialog alert = alertDialogMeals.create();
                        alert.setCanceledOnTouchOutside(false);
                        alert.show();
                        break;
                    case R.id.DeleteFromFavourites:
                        builder.setMessage("Are you sure you want to delete this item from your favourites")
                                .setNegativeButton("No", (dialog, which) -> dialog.cancel()).setPositiveButton("Yes", (dialog, which) -> {
                                    Favourite favourite = new Favourite();
                                    for (int i = 0; i < favourites.size(); i++) {
                                        if (holder.getAdapterPosition() == i) {
                                            favourite = favourites.get(i);
                                            break;
                                        }
                                    }
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Favourites");
                                    databaseReference.child(favourite.getId()).removeValue();
                                    favourites.clear();
                                    notifyDataSetChanged();
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.setTitle("Attention required!");
                        alertDialog.show();
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
        private final TextView itemName, buttonViewOption,caloriesConsumed, fats, proteins, carbs;

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
