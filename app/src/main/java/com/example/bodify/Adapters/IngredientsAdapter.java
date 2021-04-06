package com.example.bodify.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bodify.Models.Ingredient;
import com.example.bodify.R;

import java.util.ArrayList;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Ingredient> ingredients;

    public IngredientsAdapter(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public IngredientsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsAdapter.ViewHolder holder, final int position) {
        holder.setName(ingredients.get(position).getName());
        holder.setAmount(ingredients.get(position).getAmount());
        holder.setUnit(ingredients.get(position).getUnit());
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    @Override
    public void onClick(View v) {
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, amount, unit;

        public ViewHolder(@NonNull View v) {
            super(v);
            name = v.findViewById(R.id.ingredientName);
            amount = v.findViewById(R.id.ingredientAmount);
            unit = v.findViewById(R.id.amountUnit);
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
