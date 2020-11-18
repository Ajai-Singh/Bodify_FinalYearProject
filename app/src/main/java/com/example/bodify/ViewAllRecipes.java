package com.example.bodify;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.Adapters.RecipeAdapter;
import com.example.bodify.Models.Recipe;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Collections;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ViewAllRecipes extends AppCompatActivity {
    private RecyclerView.Adapter adapter;
    private ArrayList<Recipe> recipes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_recipes);
        recipes = (ArrayList<Recipe>) getIntent().getSerializableExtra("recipes");
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(ViewAllRecipes.this);
        dlgAlert.setMessage("To save recipe for later swipe left");
        dlgAlert.setTitle("Tip");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        viewAllRecipes();
    }

    public void viewAllRecipes() {
        RecyclerView recyclerView = findViewById(R.id.recipeRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ViewAllRecipes.this));
        adapter = new RecipeAdapter(recipes);
        new ItemTouchHelper(itemTouch).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);
    }

    ItemTouchHelper.SimpleCallback itemTouch = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN|
            ItemTouchHelper.START| ItemTouchHelper.END,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(recipes,fromPosition,toPosition);
            adapter.notifyItemMoved(fromPosition,toPosition);
            return false;
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(ViewAllRecipes.this, R.color.grey))
                    .addActionIcon(R.drawable.plus)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Recipe recipe = new Recipe();
            for(int i = 0; i < recipes.size(); i++) {
                if(position == i) {
                    recipe = recipes.get(i);
                    break;
                }
            }
            addToMyList(recipe);
            recipes.remove(recipe);
            adapter.notifyDataSetChanged();
        }
    };

        public void addToMyList(Recipe recipe) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Recipes").push().setValue(recipe).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ViewAllRecipes.this,"Error Occurred: " + e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
}