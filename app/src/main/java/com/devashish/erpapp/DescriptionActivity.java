package com.devashish.erpapp;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DescriptionActivity extends AppCompatActivity {

    ViewPager viewPager;
    int images[] = {R.drawable.shopone,R.drawable.shopone};
    MyCustomPagerAdapter myCustomPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        viewPager = (ViewPager)findViewById(R.id.viewPager);

        myCustomPagerAdapter = new MyCustomPagerAdapter(DescriptionActivity.this, images);
        viewPager.setAdapter(myCustomPagerAdapter);

    }
}
