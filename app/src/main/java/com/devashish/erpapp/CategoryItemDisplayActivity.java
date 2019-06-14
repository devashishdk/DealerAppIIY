package com.devashish.erpapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryItemDisplayActivity extends AppCompatActivity {

    RecyclerView mProductList;
    List<Product> ProductList;
    ProductAdapter productAdapter;
    ProgressDialog pd;
    Toolbar toolbar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_item_display);

        setUpToolBar();

        pd = new ProgressDialog(CategoryItemDisplayActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(true);
        pd.setTitle("Loading....");
        pd.setMessage("Please Wait");
        pd.show();

        String key = getIntent().getStringExtra("Key");
        mProductList = (RecyclerView) findViewById(R.id.product_list);
        mProductList.setHasFixedSize(true);
        mProductList.setLayoutManager(new GridLayoutManager(this, 2));

        ProductList = new ArrayList<>();

        db.collection("AllItems").whereEqualTo("item_category",key).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        Product p = doc.toObject(Product.class);
                        ProductList.add(p);
                    }

                    productAdapter = new ProductAdapter(CategoryItemDisplayActivity.this, ProductList);
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

    void setUpToolBar()
    {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
