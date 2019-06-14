package com.devashish.erpapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

public class FilterActivity extends AppCompatActivity {
    Button filterButton;
    EditText price_low,price_high;
    HashMap<String,Integer> hm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        price_high = (EditText) findViewById(R.id.price_high);
        price_low = (EditText) findViewById(R.id.price_low);
        filterButton = (Button) findViewById(R.id.filter_button);

        hm = new HashMap<String, Integer>();
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hm.put("price_low",Integer.parseInt(price_low.getText().toString()));
                hm.put("price_high",Integer.parseInt(price_high.getText().toString()));

                Intent intent = new Intent(FilterActivity.this,FilteredDisplayActivity.class);
                intent.putExtra("map",hm);
                startActivity(intent);

            }
        });
    }
}
