package com.example.bodify.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.Models.Grocery;
import com.example.bodify.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Grocery> groceries;
    private final Context context;

    public GroceryAdapter(ArrayList<Grocery> groceries , Context context) {
        this.groceries = groceries;
        this.context = context;
    }

    @NonNull
    @Override
    public GroceryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return groceries.size();
    }

    @Override
    public void onClick(View v) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name,price;
        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.groceryName);
            price = itemView.findViewById(R.id.groceryPrice);
            image = itemView.findViewById(R.id.groceryImage);
        }

        public void setName(String n) {
            name.setText(n);
        }

        public void setPrice(String p) {
            price.setText(p);
        }

        public void setImage(String i) {
            Picasso.get().load(i).into(image);
        }
    }
}
