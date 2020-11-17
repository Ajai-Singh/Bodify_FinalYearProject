package com.example.bodify;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import org.json.JSONArray;

public class APIs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_p_is);
        AndroidNetworking.initialize(getApplicationContext());
//        AndroidNetworking.get("https://api.spoonacular.com/food/ingredients/search?apiKey=c029b15f6c654e36beba722a71295883&qu&query=chicken")
//        .build().getAsJSONArray(new JSONArrayRequestListener() {
//            @Override
//            public void onResponse(JSONArray response) {
//
//            }
//
//            @Override
//            public void onError(ANError anError) {
//
//            }
//        });
        AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "3")
                .addHeaders("token", "1234")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Toast.makeText(APIs.this,"" + response.toString(),Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onError(ANError error) {
                        Toast.makeText(APIs.this,"Error" + error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }
}