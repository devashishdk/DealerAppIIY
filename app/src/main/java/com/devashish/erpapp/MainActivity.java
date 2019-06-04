package com.devashish.erpapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;
    RecyclerView mProductList;
    List<Product> ProductList;
    ProductAdapter productAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog pd;
    private FirebaseAuth mAuth;

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/font_app.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pd = new ProgressDialog(MainActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(true);
        pd.setTitle("Loading....");
        pd.setMessage("Please Wait");
        pd.show();

        mAuth = FirebaseAuth.getInstance();

        setUpToolBar();

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
                        Intent intent = new Intent(MainActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;
                    case (R.id.profile):
                        drawerLayout.closeDrawers();
                        Intent intentPro = new Intent(MainActivity.this,ProfileActivity.class);
                        startActivity(intentPro);
                        //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;
                    case (R.id.Orders):
                        drawerLayout.closeDrawers();
                        Intent intentOrder = new Intent(MainActivity.this,DescriptionActivity.class);
                        startActivity(intentOrder);
                        //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;

                }
                return false;
            }
        });


        mProductList = (RecyclerView) findViewById(R.id.product_list);
        mProductList.setHasFixedSize(true);
        mProductList.setLayoutManager(new GridLayoutManager(this, 2));

        ProductList = new ArrayList<>();

        db.collection("AllItems").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        Product p = doc.toObject(Product.class);
                        ProductList.add(p);
                    }

                    productAdapter = new ProductAdapter(MainActivity.this, ProductList);
                    mProductList.setAdapter(productAdapter);

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
                drawerLayout.closeDrawers();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
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
            Intent startIntent = new Intent(MainActivity.this,LoginActivity.class);
            if(pd.isShowing())
                pd.dismiss();
            startActivity(startIntent);
            finish();
        }
    }
}
