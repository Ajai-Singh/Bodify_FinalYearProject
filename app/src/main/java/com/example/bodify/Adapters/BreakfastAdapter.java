package com.example.bodify.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.Models.Meal;
import com.example.bodify.R;
import java.util.ArrayList;
//investigate if I can use the same adapter for all the days meals
public class BreakfastAdapter extends RecyclerView.Adapter<BreakfastAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Meal> meals;

    public BreakfastAdapter(ArrayList<Meal> meals) {
        this.meals = meals;
    }

    @NonNull
    @Override
    public BreakfastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_meal_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.setItemName(meals.get(position).getItemName());
        holder.setCaloriesConsumed(meals.get(position).getCalories());
        holder.setCarbs(meals.get(position).getItemTotalCarbohydrates());
        holder.setFats(meals.get(position).getItemTotalFat());
        holder.setProteins(meals.get(position).getItemProtein());
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    @Override
    public void onClick(View v) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName,caloriesConsumed,fats,proteins,carbs;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.mealName);
            caloriesConsumed = itemView.findViewById(R.id.textView16);
            fats = itemView.findViewById(R.id.fatsTV);
            proteins = itemView.findViewById(R.id.proteinsTV);
            carbs = itemView.findViewById(R.id.carbsTV);
        }
        public void setItemName(String name) {
            itemName.setText(name);
        }
        public void setCaloriesConsumed(int calories) {
            caloriesConsumed.setText(String.valueOf(calories));
        }
        public void setFats(int f) {
            fats.setText(String.valueOf(f));
        }
        public void setProteins(int p) {
            proteins.setText(String.valueOf(p));
        }
        public void setCarbs(int c) {
            carbs.setText(String.valueOf(c));
        }

    }
}

