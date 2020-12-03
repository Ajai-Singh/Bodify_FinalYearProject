package com.example.bodify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.Adapters.FavouriteAdapter;
import com.example.bodify.Models.ScanProduct;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class Favourites extends Fragment {
        RecyclerView recyclerView;
        RecyclerView.Adapter adapter;
        ArrayList<ScanProduct> scanProducts = new ArrayList<>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                View view =  inflater.inflate(R.layout.activity_favourites,container,false);
                recyclerView = view.findViewById(R.id.favRCV);
                return view;
        }

        @Override
        public void onStart() {
                super.onStart();
                getAllFavourites();
        }

        public void getAllFavourites() {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Favourites");
                databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                        ScanProduct scanProduct = userSnapshot.getValue(ScanProduct.class);
                                        assert scanProduct != null;
                                        if(firebaseUser.getUid().equals(scanProduct.getUserID())) {
                                                scanProducts.add(scanProduct);
                                        }
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                        adapter = new FavouriteAdapter(scanProducts);
                                        recyclerView.setAdapter(adapter);
                                }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                });
        }
}