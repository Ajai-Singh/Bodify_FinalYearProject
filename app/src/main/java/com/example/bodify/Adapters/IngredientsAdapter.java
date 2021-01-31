package com.example.bodify.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.Models.Ingredient;
import com.example.bodify.R;
import java.util.ArrayList;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Ingredient> ingredients;
    private final ArrayList<String> ingredientNames;

    public IngredientsAdapter(ArrayList<Ingredient> ingredients,ArrayList<String> ingredientNames) {
        this.ingredients = ingredients;
        this.ingredientNames = ingredientNames;
    }

    @NonNull
    @Override
    public IngredientsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_row, parent, false);
        test();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsAdapter.ViewHolder holder, final int position) {
        holder.setName(ingredients.get(position).getName());
        holder.setAmount(ingredients.get(position).getAmount());
        holder.setUnit(ingredients.get(position).getUnit());
        ingredientNames.clear();
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ingredientNames.add(ingredients.get(position).getName());
            } else {
                ingredientNames.remove(ingredients.get(position).getName());
            }
        });
    }

    public ArrayList<String> test() {
        return ingredientNames;
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    @Override
    public void onClick(View v) {
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, amount, unit;
        CheckBox checkBox;

        public ViewHolder(@NonNull View v) {
            super(v);
            name = v.findViewById(R.id.ingredientName);
            amount = v.findViewById(R.id.ingredientAmount);
            unit = v.findViewById(R.id.amountUnit);
            checkBox = v.findViewById(R.id.checkBox);
        }

        public void setName(String n) {
            name.setText(n);
        }

        public void setAmount(double a) {
            amount.setText(String.valueOf(a));
        }

        public void setUnit(String u) {
            unit.setText(u);
        }
    }
}
