package com.devashish.erpapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FilterActivity extends AppCompatActivity {
    Button filterButton;
    EditText price_low,price_high;
    HashMap<String,Integer> hm;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    private FirebaseAuth mAuth;
    Toolbar toolbar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog pd;
    ArrayList<String> ProductList;

    RecyclerView mBrandList;
    List<Product> BrandList;
    BrandAdapter BrandAdapter;

    String brandSelection = "default";

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/font_app.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        setUpToolBar();


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-message"));

        pd = new ProgressDialog(FilterActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(true);
        pd.setTitle("Loading....");
        pd.setMessage("Please Wait");
        pd.show();

        mAuth = FirebaseAuth.getInstance();


        mBrandList = (RecyclerView) findViewById(R.id.category_list);
        mBrandList.setHasFixedSize(false);
        mBrandList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        BrandList = new ArrayList<>();

        db.collection("AllItems").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        Product p = doc.toObject(Product.class);
                        BrandList.add(p);
                    }

                    BrandAdapter = new BrandAdapter(FilterActivity.this, BrandList);
                    mBrandList.setAdapter(BrandAdapter);

                    //If ProgressDialog is showing Dismiss it
                    if(pd.isShowing())
                    {
                        pd.dismiss();
                    }

                }
            }
        });

        price_high = (EditText) findViewById(R.id.price_high);
        price_low = (EditText) findViewById(R.id.price_low);
        filterButton = (Button) findViewById(R.id.filter_button);

        hm = new HashMap<String, Integer>();
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> arr = new ArrayList<String>();
                for (Product model : BrandList) {
                    if (model.getSelected()) {
                        arr.add(model.getItem_brand());
                    }
                }
                hm.put("price_low",price_low.getText().toString().equals("") ? 0 : Integer.parseInt(price_low.getText().toString()));
                hm.put("price_high",price_high.getText().toString().equals("") ? Integer.MAX_VALUE :Integer.parseInt(price_high.getText().toString()));
                Intent intent = new Intent(FilterActivity.this,FilteredDisplayActivity.class);
                intent.putExtra("map",hm);
                intent.putExtra("brand",arr);
                startActivity(intent);

            }
        });

        navigationView = (NavigationView) findViewById(R.id.navigationView);

        navigationView.setItemIconTintList(null);
        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case (R.id.home):
                        drawerLayout.closeDrawers();
                        Intent intent = new Intent(FilterActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;
                    case (R.id.profile):
                        drawerLayout.closeDrawers();
                        Intent intentPro = new Intent(FilterActivity.this,ProfileActivity.class);
                        startActivity(intentPro);
                        //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;
                    case (R.id.Orders):
                        drawerLayout.closeDrawers();
                        Intent intentProduct = new Intent(FilterActivity.this,OrdersActivity.class);
                        startActivity(intentProduct);
                        //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;
                    case  (R.id.logout):
                        drawerLayout.closeDrawers();
                        FirebaseAuth.getInstance().signOut();
                        Intent intentL = new Intent(FilterActivity.this,LoginActivity.class);
                        startActivity(intentL);
                        finish();
                        break;
                }
                return false;
            }
        });



       ProductList = new ArrayList<>();

        db.collection("AllItems").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        Product p = doc.toObject(Product.class);
                        ProductList.add(p.getItem_brand());
                    }
                    //If ProgressDialog is showing Dismiss it
                    if(pd.isShowing())
                    {
                        pd.dismiss();
                    }

                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.search):
                Intent intent = new Intent(FilterActivity.this,SearchActivity.class);
                startActivity(intent);
                break;
            case (R.id.cart):
                Intent intentC = new Intent(FilterActivity.this,CartActivity.class);
                startActivity(intentC);
                break;
        }
        return true;
    }

    void setUpToolBar()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        actionBarDrawerToggle =  new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null)
        {
            Intent startIntent = new Intent(FilterActivity.this,LoginActivity.class);

            startActivity(startIntent);
            finish();
        }
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String selected = intent.getStringExtra("selected");
            Toast.makeText(FilterActivity.this,selected,Toast.LENGTH_SHORT).show();
            brandSelection = selected;
        }
    };

}
