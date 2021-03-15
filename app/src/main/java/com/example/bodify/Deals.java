package com.example.bodify;

import android.os.Bundle;
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
import java.util.Objects;

public class Deals extends AppCompatActivity {
    private RecyclerView recyclerView;
    private final ArrayList<Grocery> groceries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Tesco deals");
        recyclerView = findViewById(R.id.groceryRCV);
        test();
    }

    public void test() {
        new Thread(() -> {
            Document document;
            String link = "https://www.tesco.ie/groceries/SpecialOffers/default.aspx";
            try {
                String img;
                document = Jsoup.connect(link).get();
                Elements nameTag = document.getElementsByTag("strong");
                Elements priceTag = document.getElementsByClass("linePrice");
                Elements imageTag = document.getElementsByClass("image");
                ArrayList<String> names = new ArrayList<>();
                ArrayList<String> prices = new ArrayList<>();
                ArrayList<String> images = new ArrayList<>();
                for (Element n : nameTag) {
                    names.add(n.text());
                }
                for (Element p : priceTag) {
                    prices.add(p.text());
                }
                for(Element e : imageTag) {
                    img = e.getElementsByTag("img").toString();
                    img = img.substring(17);
                    img = img.substring(0,img.length() - 2);
                    images.add(img);
                }
                for(int i = 0; i < names.size(); i++) {
                    Grocery grocery = new Grocery(names.get(i),images.get(i),prices.get(i));
                    groceries.add(grocery);
                }
                runOnUiThread(() -> {
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    GroceryAdapter groceryAdapter = new GroceryAdapter(groceries);
                    recyclerView.setAdapter(groceryAdapter);
                });
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }).start();
    }
}
