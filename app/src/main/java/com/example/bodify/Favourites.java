package com.example.bodify;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import java.util.Collections;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class Favourites extends Fragment {
        RecyclerView recyclerView;
        RecyclerView.Adapter adapter;
        ArrayList<ScanProduct> scanProducts = new ArrayList<>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.activity_favourites, container, false);
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
                                        if (firebaseUser.getUid().equals(scanProduct.getUserID())) {
                                                scanProducts.add(scanProduct);
                                        }
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                                        adapter = new FavouriteAdapter(scanProducts);
                                        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
                                        recyclerView.setAdapter(adapter);
                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Error Occurred: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                });
        }

        ItemTouchHelper.SimpleCallback itemTouch = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                }

                @Override
                public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(getContext());
                        dlgAlert.setMessage("Would you like to remove this item from your favourites");
                        dlgAlert.setTitle("Attention");
                        dlgAlert.setPositiveButton("OK", null);
                        dlgAlert.setCancelable(true);
                        dlgAlert.setNegativeButton("NO",null);
                        dlgAlert.create().show();
                        dlgAlert.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                                int position = viewHolder.getAdapterPosition();
                                                scanProducts.remove(position);
                                                adapter.notifyDataSetChanged();
                                        }
                                });
                }

                @Override
                public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                                .addBackgroundColor(ContextCompat.getColor(getContext(), R.color.grey))
                                .addActionIcon(R.drawable.plus)
                                .create()
                                .decorate();
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
        };
}
