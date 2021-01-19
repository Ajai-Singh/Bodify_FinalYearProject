package com.example.bodify.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.Models.Meal;
import com.example.bodify.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Meal> meals;
    private final Context context;

    public MealAdapter(ArrayList<Meal> meals, Context context) {
        this.meals = meals;
        this.context = context;
    }

    @NonNull
    @Override
    public MealAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_meal_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.setItemName(meals.get(position).getItemName());
        holder.setCaloriesConsumed(meals.get(position).getCalories() * meals.get(position).getNumberOfServings());
        holder.setFats(meals.get(position).getItemTotalFat() * meals.get(position).getNumberOfServings());
        holder.setProteins(meals.get(position).getItemProtein() * meals.get(position).getNumberOfServings());
        holder.setCarbs(meals.get(position).getItemTotalCarbohydrates() * meals.get(position).getNumberOfServings());
        holder.menuOptions.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.menuOptions);
            popupMenu.inflate(R.menu.meal_menu_options);
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.deleteMeal) {
                    builder.setMessage("Are you sure you want to delete this meal")
                            .setNegativeButton("No", (dialog, which) -> dialog.cancel()).setPositiveButton("Yes", (dialog, which) -> {
                                Meal meal = new Meal();
                                for (int i = 0; i < meals.size(); i++) {
                                    if (holder.getAdapterPosition() == i) {
                                        meal = meals.get(i);
                                        break;
                                    }
                                }
                                //the problem with the delete is that
                                //.child(meal.getDayOfWeek) is null that means when the meal is being saved maybe the day of week isnt being saved
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DayOfWeek").child(meal.getDayOfWeek()).child(meal.getId());
                                databaseReference.removeValue();
                                meals.clear();
                                notifyDataSetChanged();
                            });
                    AlertDialog alertDialog = builder.create();
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
        return meals.size();
    }

    @Override
    public void onClick(View v) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemName, caloriesConsumed, fats, proteins, carbs, menuOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.mealName);
            caloriesConsumed = itemView.findViewById(R.id.textView16);
            fats = itemView.findViewById(R.id.fatsTV);
            proteins = itemView.findViewById(R.id.proteinsTV);
            carbs = itemView.findViewById(R.id.carbsTV);
            menuOptions = itemView.findViewById(R.id.mealMenuOptions);
        }

        public void setItemName(String name) {
            itemName.setText(name);
        }

        public void setCaloriesConsumed(int calories) {
            caloriesConsumed.setText(String.valueOf(calories));
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

