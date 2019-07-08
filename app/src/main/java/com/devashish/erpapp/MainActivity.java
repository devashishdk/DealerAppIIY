package com.devashish.erpapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
    AllItemsListAdapter adapter;
    RecyclerView mCategoryList;
    List<Category> CategoryList;
    CategoryAdapter CategoryAdapter;
    CardView sortCard,filterCard,categoryCard;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog pd;
    private FirebaseAuth mAuth;
    String sort_choice = "";

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

        sortCard = (CardView) findViewById(R.id.sort_card);
        filterCard = (CardView) findViewById(R.id.filter_card);
        categoryCard = (CardView) findViewById(R.id.categoryCard);

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
                        Intent intentCategory = new Intent(MainActivity.this,OrdersActivity.class);
                        startActivity(intentCategory);
                        //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;
                    case  (R.id.logout):
                        drawerLayout.closeDrawers();
                        FirebaseAuth.getInstance().signOut();
                        Intent intentL = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intentL);
                        finish();
                        break;
                }
                return false;
            }
        });


        mCategoryList = (RecyclerView) findViewById(R.id.category_list);
        mCategoryList.setHasFixedSize(false);
        mCategoryList.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        CategoryList = new ArrayList<>();

        db.collection("Categories").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        Category p = doc.toObject(Category.class);
                        CategoryList.add(p);
                    }

                    CategoryAdapter = new CategoryAdapter(MainActivity.this, CategoryList);
                    mCategoryList.setAdapter(CategoryAdapter);

                    //If ProgressDialog is showing Dismiss it
                    if(pd.isShowing())
                    {
                        pd.dismiss();
                    }

                }
            }
        });

        mProductList = (RecyclerView) findViewById(R.id.product_list);
        mProductList.setHasFixedSize(true);
        mProductList.setLayoutManager(new GridLayoutManager(this, 2));

        ProductList = new ArrayList<>();

        //createing new viewmodel for firebase recycler ui
        Query query = db.collection("AllItems");
        final FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query,Product.class)
                .build();

        adapter = new AllItemsListAdapter(getApplicationContext(), options);
        mProductList.setAdapter(adapter);

//
//        db.collection("AllItems").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()){
//                    for (QueryDocumentSnapshot doc : task.getResult()){
//                        Product p = doc.toObject(Product.class);
//                        ProductList.add(p);
//                    }
//
//                    productAdapter = new ProductAdapter(MainActivity.this, ProductList);
//                    mProductList.setAdapter(productAdapter);
//
//                    //If ProgressDialog is showing Dismiss it
//                    if(pd.isShowing())
//                    {
//                        pd.dismiss();
//                    }
//
//                }
//            }
//        });

        filterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,FilterActivity.class);
                startActivity(intent);
            }
        });
        sortCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] listitem = new String[]{"High to Low","Low to High"};
                AlertDialog.Builder sort_alert= new AlertDialog.Builder(MainActivity.this);
                sort_alert.setTitle("Sort by price");
                sort_alert.setIcon(R.drawable.sort);
                sort_alert.setSingleChoiceItems(listitem, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ProductList.clear();
                        pd = new ProgressDialog(MainActivity.this);
                        pd.setCanceledOnTouchOutside(false);
                        pd.setCancelable(true);
                        pd.setTitle("Loading....");
                        pd.setMessage("Please Wait");
                        pd.show();
                        db.collection("AllItems").orderBy("item_price", listitem[which]=="Low to High" ? Query.Direction.ASCENDING : Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                });
                sort_alert.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog ad = sort_alert.create();
                ad.show();
            }
        });

        categoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCategoryList.isShown()){
                    mCategoryList.setVisibility(View.GONE);
                }
                else{
                    mCategoryList.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionmenu,menu);

        MenuItem item = menu.findItem(R.id.search);


        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //searchData(s.toLowerCase());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchLiveData(s.toLowerCase());
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);

        //return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.cart):
                Intent intentC = new Intent(MainActivity.this,CartActivity.class);
                startActivity(intentC);
                break;
        }
        return true;
    }

    private void searchLiveData(String s) {

        Query query = db.collection("AllItems").orderBy("search").startAt(s).endAt(s+"\uf8ff");
        final FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query,Product.class)
                .build();

        adapter = new AllItemsListAdapter(getApplicationContext(), options);
        mProductList.setAdapter(adapter);
        adapter.setOnItemClick(new AllItemsListAdapter.OnItemClick() {
            @Override
            public void getPosition(String item_id) {
                Intent intent = new Intent(MainActivity.this, DescriptionActivity.class);
                intent.putExtra("push_id", item_id);
                startActivity(intent);
            }
        });

        adapter.startListening();
    }

    private void searchData(String s) {
        /*
        dialog.setMessage("Searching");
        dialog.show();
        Query query = db.collection("AllItems").orderBy("search").startAt(s).endAt(s+"\uf8ff");
        final FirestoreRecyclerOptions<ALlItems> options = new FirestoreRecyclerOptions.Builder<ALlItems>()
                .setQuery(query, ALlItems.class)
                .build();

        adapter = new AllItemsListAdapter(getApplicationContext(), options);
        allItemsList.setAdapter(adapter);

        adapter.setOnItemClick(new AllItemsListAdapter.OnItemClick() {
            @Override
            public void getPosition(String item_id) {
                Intent intent = new Intent(AllItemsActivity.this, EditProductActivity.class);
                intent.putExtra("push_id", item_id);
                startActivity(intent);
            }
        });
        adapter.startListening();
        */
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
        adapter.startListening();
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

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
