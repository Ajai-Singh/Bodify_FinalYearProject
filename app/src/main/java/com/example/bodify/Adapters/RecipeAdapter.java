package com.example.bodify.Adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.Models.Recipe;
import com.example.bodify.R;
import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Recipe> recipes;

    public RecipeAdapter(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
    }

    @NonNull
    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.setRecipeName(recipes.get(position).getTitle());
        holder.setUrl(recipes.get(position).getSourceUrl());
        holder.setServingQuantity(recipes.get(position).getServings());
        holder.setCookDuration(recipes.get(position).getReadyInMinutes());
        holder.url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(holder.url.getText().toString()));
                v.getContext().startActivity(intent);
            }
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
        public TextView recipeName,servingQuantity,cookDuration,url;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipeTitle);
            servingQuantity = itemView.findViewById(R.id.Serving);
            cookDuration = itemView.findViewById(R.id.timeToCook);
            url = itemView.findViewById(R.id.source);
        }
        public void setRecipeName(String rn) {
            recipeName.setText(rn);
        }
        public void setServingQuantity(String sq) {
            servingQuantity.setText(sq);
        }
        public void setCookDuration(String cd) {
            cookDuration.setText(cd);
        }
        public void setUrl(String u) {
            url.setText(u);
        }
    }
 }

