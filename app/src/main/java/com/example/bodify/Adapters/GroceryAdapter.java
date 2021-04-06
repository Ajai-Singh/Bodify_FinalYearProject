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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.Models.Grocery;
import com.example.bodify.Models.WishList;
import com.example.bodify.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;;
import java.util.ArrayList;
import java.util.Objects;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> implements View.OnClickListener {
    private final ArrayList<Grocery> groceries;
    private final Context context;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public GroceryAdapter(ArrayList<Grocery> groceries, Context context) {
        this.groceries = groceries;
        this.context = context;
    }

    @NonNull
    @Override
    public GroceryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.deal, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(@NonNull GroceryAdapter.ViewHolder holder, final int position) {
        holder.setName(groceries.get(position).getName());
        holder.setPrice(groceries.get(position).getPrice());
        holder.setImage(groceries.get(position).getImageUrl());
        holder.options.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.options);
            popupMenu.inflate(R.menu.groceryoptions);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.addToWishList:
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("WishList");
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                boolean exists = false;
                                for (DataSnapshot wishListSnapshot : snapshot.getChildren()) {
                                    WishList wishList = wishListSnapshot.getValue(WishList.class);
                                    if (wishList != null) {
                                        if (wishList.getGrocery().getName().equalsIgnoreCase(groceries.get(position).getName())
                                                && wishList.getUserID().equalsIgnoreCase(mAuth.getUid())) {
                                            exists = true;
                                            break;
                                        }
                                    }
                                }
                                if (exists) {
                                    Toast.makeText(context, "Error item already exists in Wish List", Toast.LENGTH_SHORT).show();
                                }
                                if (!exists) {
                                    WishList dbWishList = new WishList(groceries.get(position), mAuth.getUid());
                                    databaseReference.push().setValue(dbWishList).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(context, "Item added to Wish List", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Error Occurred" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(context, "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case R.id.viewOnline:
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(groceries.get(position).getPageLink()));
                        context.startActivity(browserIntent);
                        break;
                }
                return false;
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return groceries.size();
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
