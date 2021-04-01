package com.example.bodify.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bodify.Groceries;
import com.example.bodify.Models.Grocery;
import com.example.bodify.Models.Ingredient;
import com.example.bodify.R;
import java.util.ArrayList;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Grocery> groceries;

    public GroceryAdapter(ArrayList<Grocery> groceries) {
        this.groceries = groceries;
    }

    @NonNull
    @Override
    public GroceryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_meal_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroceryAdapter.ViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return groceries.size();
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
