package com.example.bodify.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.Models.ScanProduct;
import com.example.bodify.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<ScanProduct> scanProducts;
    private final Context context;
    
    public FavouriteAdapter(ArrayList<ScanProduct> scanProducts,Context context) {
        this.scanProducts = scanProducts;
        this.context = context;
    }

    @NonNull
    @Override
    public FavouriteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavouriteAdapter.ViewHolder holder, final int position) {
        
        holder.setItemName(scanProducts.get(position).getItemName());
        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.buttonViewOption);
                popup.inflate(R.menu.rcv_menu_options);
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.addToDiary:
                                //ask user what segment they want to add it
                                AlertDialog.Builder alertDialogMeals = new AlertDialog.Builder(context);
                                alertDialogMeals.setTitle("Select Meal");
                                String[] items = {"Breakfast","Lunch","Dinner","Other"};
                                int checkedItem = 1;
                                alertDialogMeals.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:

                                                break;
                                            case 1:

                                                break;
                                            case 2:

                                                break;
                                            case 3:

                                                break;
                                        }
                                    }
                                });
                                AlertDialog alert = alertDialogMeals.create();
                                alert.setCanceledOnTouchOutside(false);
                                alert.show();
                                break;
                            case R.id.DeleteFromFavourites:
                                builder.setMessage("Are you sure you want to delete this item from your favourites")
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int p = holder.getAdapterPosition();
                                        ScanProduct scanProduct = new ScanProduct();
                                        for(int i = 0; i < scanProducts.size(); i ++) {
                                            if(p == i) {
                                                scanProduct = scanProducts.get(i);
                                                break;
                                            }
                                        }
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Favourites");
                                        databaseReference.child(scanProduct.getId()).removeValue();
                                        scanProducts.clear();
                                        notifyDataSetChanged();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.setTitle("Attention required!");
                                alertDialog.show();
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return scanProducts.size();
    }

    @Override
    public void onClick(View v) {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemName;
        private final TextView buttonViewOption;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemNameTextField);
            buttonViewOption = itemView.findViewById(R.id.textViewOptions);
        }

        public void setItemName(String name) {
            itemName.setText(name);
        }
    }
}
