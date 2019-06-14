package com.devashish.erpapp;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    RecyclerView mProductList;
    List<Product> ProductList;
    ProductAdapter productAdapter;
    ProgressDialog pd;
    ImageButton searchButton;
    EditText searchtext;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchButton = (ImageButton) findViewById(R.id.search_button);
        searchtext = (EditText) findViewById(R.id.search_text);


        mProductList = (RecyclerView) findViewById(R.id.product_list);
        mProductList.setHasFixedSize(true);
        mProductList.setLayoutManager(new GridLayoutManager(this, 2));

        ProductList = new ArrayList<>();

        searchButton.setOnClickListener(new View.OnClickListener() {

            String searchText = searchtext.getText().toString();

            @Override
            public void onClick(View v) {

                pd = new ProgressDialog(SearchActivity.this);
                pd.setCanceledOnTouchOutside(false);
                pd.setCancelable(true);
                pd.setTitle("Loading....");
                pd.setMessage("Please Wait");
                pd.show();
                db.collection("AllItems").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            ProductList.clear();
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                Product p = doc.toObject(Product.class);
                                ProductList.add(p);
                            }

                            productAdapter = new ProductAdapter(SearchActivity.this, ProductList);
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

    }
}
