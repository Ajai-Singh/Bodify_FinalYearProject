package com.example.bodify;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bodify.Adapters.GroceryAdapter;
import com.example.bodify.Models.Grocery;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;

public class Deals extends AppCompatActivity {
    private RecyclerView recyclerView;
    private final ArrayList<Grocery> groceries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);
        recyclerView = findViewById(R.id.groceryRCV);
        test();
    }

    //start web scarping functionality
    //I need to scrape all the objects from the UI
    //create the objects
    //add to an arraylist
    //pass the arraylist into the adapter class
    //work on wishlist too
    public void test() {
        new Thread(() -> {
            Document document;
            String link = "https://www.tesco.ie/groceries/SpecialOffers/default.aspx";
            try {
                document = Jsoup.connect(link).get();
                Elements name = document.getElementsByTag("strong");
                Elements price = document.getElementsByClass("linePrice");
                Elements image = document.getElementsByClass("image");
                for (Element n : name) {
                    Log.i("name", "" + n.text());
                }
                for (Element p : price) {
                    Log.i("price", "" + p.text());
                }
                //figured out the issue basically I need to go into a href first then image then image alt src
                for (Element i : image) {
                    Log.i("imageUrl","" + i.getElementsByTag("img alt src").text());
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }).start();
        //I need to pass in the arraylist now to this method
        getAllDeals();
    }

    public void getAllDeals() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        GroceryAdapter groceryAdapter = new GroceryAdapter(groceries, getApplicationContext());
        recyclerView.setAdapter(groceryAdapter);
    }
}
