package com.example.bodify;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bodify.Adapters.FavouriteAdapter;
import com.example.bodify.Models.Favourite;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class Favourites extends Fragment {
    private RecyclerView recyclerView;
    private FavouriteAdapter favouriteAdapter;
    private final ArrayList<Favourite> favourites = new ArrayList<>();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ImageView createFavourite;
    private RelativeLayout relativeLayout;
    private ArrayList<String> itemNames;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_favourites, container, false);
        recyclerView = view.findViewById(R.id.favRCV);
        createFavourite = view.findViewById(R.id.createCustomFavourite);
        relativeLayout = view.findViewById(R.id.favouritesCL);
        getAllFavourites();
        createFavourite.setOnClickListener(v -> {
            DatabaseReference favReference = FirebaseDatabase.getInstance().getReference("Favourites");
            favReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    itemNames = new ArrayList<>();
                    for (DataSnapshot favouriteSnapshot : snapshot.getChildren()) {
                        Favourite favourite = favouriteSnapshot.getValue(Favourite.class);
                        if(favourite != null) {
                            if (favourite.getUserID().equals(mAuth.getUid())) {
                                itemNames.add(favourite.getItemName().toLowerCase());
                            }
                        }
                    }
                    createCustomFavourite(itemNames);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Snackbar snackbar = Snackbar.make(relativeLayout, "Error occurred: " + error.getMessage(), Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            });
        });

        return view;
    }

    public void getAllFavourites() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Favourites");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemNames = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Favourite favourite = userSnapshot.getValue(Favourite.class);
                    assert favourite != null;
                    favourite.setId(userSnapshot.getKey());
                    if (Objects.equals(mAuth.getUid(), favourite.getUserID())) {
                        favourites.add(favourite);
                        itemNames.add(favourite.getItemName().toLowerCase());
                    }
                }
                if (favourites.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(relativeLayout, "Sorry no Favourites! ", Snackbar.LENGTH_LONG)
                            .setAction("Create Favourite?", view -> {
                                createCustomFavourite(itemNames);
                            });
                    snackbar.show();
                } else {
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    favouriteAdapter = new FavouriteAdapter(favourites, getContext(), relativeLayout);
                    recyclerView.setAdapter(favouriteAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Snackbar snackbar = Snackbar.make(relativeLayout, "Error occurred: " + error.getMessage(), Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });
    }

    public void createCustomFavourite(ArrayList<String> itemNames) {
        createFavourite.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            @SuppressLint("InflateParams")
            View view = getLayoutInflater().inflate(R.layout.custom_favourite, null);
            EditText itemName, servings, calories, fat, sodium, carbs, sugar, protein;
            itemName = view.findViewById(R.id.customFavName);
            servings = view.findViewById(R.id.customFavServings);
            calories = view.findViewById(R.id.customFavCalories);
            fat = view.findViewById(R.id.customFavFat);
            sodium = view.findViewById(R.id.customFavSodium);
            carbs = view.findViewById(R.id.customFavCarbohydrate);
            sugar = view.findViewById(R.id.customFavSugar);
            protein = view.findViewById(R.id.customFavProtein);
            builder.setPositiveButton("Create", (dialog, which) -> {
            });
            builder.setNegativeButton("Close", (dialog, which) -> dialog.cancel());
            builder.setView(view);
            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
                if (TextUtils.isEmpty(itemName.getText().toString())) {
                    itemName.setError("Field cannot be empty!");
                    itemName.requestFocus();
                } else if (itemNames.contains(itemName.getText().toString().toLowerCase())) {
                    itemName.setError("Record already exists!");
                    itemName.requestFocus();
                } else if (TextUtils.isEmpty(servings.getText().toString())) {
                    servings.setError("Field cannot be empty!");
                    servings.requestFocus();
                } else if (TextUtils.isEmpty(calories.getText().toString())) {
                    calories.setError("Field cannot be empty!");
                    calories.requestFocus();
                } else if (TextUtils.isEmpty(fat.getText().toString())) {
                    fat.setError("Field cannot be empty!");
                    fat.requestFocus();
                } else if (TextUtils.isEmpty(sodium.getText().toString())) {
                    sodium.setError("Field cannot be empty!");
                    sodium.requestFocus();
                } else if (TextUtils.isEmpty(carbs.getText().toString())) {
                    carbs.setError("Field cannot be empty!");
                    carbs.requestFocus();
                } else if (TextUtils.isEmpty(sugar.getText().toString())) {
                    sugar.setError("Field cannot be empty!");
                    sugar.requestFocus();
                } else if (TextUtils.isEmpty(protein.getText().toString())) {
                    protein.setError("Field cannot be empty!");
                    protein.requestFocus();
                } else {
                    dialog.dismiss();
                    Favourite favourite = new Favourite(itemName.getText().toString(), Integer.parseInt(calories.getText().toString()), Integer.parseInt(fat.getText().toString()),
                            Integer.parseInt(sodium.getText().toString()), Integer.parseInt(carbs.getText().toString()), Integer.parseInt(sugar.getText().toString()),
                            Integer.parseInt(protein.getText().toString()), mAuth.getUid(), Integer.parseInt(servings.getText().toString()), "no url");
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("Favourites").push().setValue(favourite).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Snackbar snackbar = Snackbar.make(relativeLayout, itemName.getText().toString() + " added to Favourites!", Snackbar.LENGTH_SHORT);
                            snackbar.show();
                            DatabaseReference favRef = FirebaseDatabase.getInstance().getReference("Favourites");
                            favRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    favourites.clear();
                                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                        Favourite favourite = userSnapshot.getValue(Favourite.class);
                                        assert favourite != null;
                                        favourite.setId(userSnapshot.getKey());
                                        if (Objects.equals(mAuth.getUid(), favourite.getUserID())) {
                                            favourites.add(favourite);
                                        }
                                    }
                                    recyclerView.setHasFixedSize(true);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                    favouriteAdapter = new FavouriteAdapter(favourites, getContext(), relativeLayout);
                                    recyclerView.setAdapter(favouriteAdapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Snackbar snackbar = Snackbar.make(relativeLayout, "Error occurred: " + error.getMessage(), Snackbar.LENGTH_SHORT);
                                    snackbar.show();
                                }
                            });
                        } else {
                            Snackbar snackbar = Snackbar.make(relativeLayout, "Error occurred: " + Objects.requireNonNull(task.getException()).getMessage(), Snackbar.LENGTH_SHORT);
                            snackbar.show();
                        }
                    });
                }
            });
        });
    }
}