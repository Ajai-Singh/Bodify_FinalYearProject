package com.example.bodify.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bodify.Models.WishList;
import com.example.bodify.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<WishList> wishListArrayList;
    private final Context context;

    public WishListAdapter(ArrayList<WishList> wishListArrayList, Context context) {
        this.wishListArrayList = wishListArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public WishListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deal, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(@NonNull WishListAdapter.ViewHolder holder, final int position) {
        holder.setName(wishListArrayList.get(position).getGrocery().getName());
        holder.setPrice(wishListArrayList.get(position).getGrocery().getName());
        holder.setImage(wishListArrayList.get(position).getGrocery().getImageUrl());
        holder.options.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.options);
            popupMenu.inflate(R.menu.groceryoptions);
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.viewOnline) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(wishListArrayList.get(position).getGrocery().getPageLink()));
                    context.startActivity(browserIntent);
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return wishListArrayList.size();
    }

    @Override
    public void onClick(View v) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, price, options;
        private final ImageView image;

        public ViewHolder(@NonNull View v) {
            super(v);
            name = v.findViewById(R.id.groceryName);
            price = v.findViewById(R.id.groceryPrice);
            image = v.findViewById(R.id.groceryImage);
            options = v.findViewById(R.id.groceryOptions);
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