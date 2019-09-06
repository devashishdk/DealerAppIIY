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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

public class FilteredDisplayActivity extends AppCompatActivity {

    RecyclerView mProductList;
    List<Product> ProductList;
    ProductAdapter productAdapter;
    ProgressDialog pd;
    ImageButton searchButton;
    EditText searchtext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String currentUser;

    ArrayList<Filter> brandList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtered_display);

        final HashMap<String, Integer> hashMapObject = (HashMap<String, Integer>) getIntent().getSerializableExtra("map");
        ArrayList<String> brand = (ArrayList<String>) getIntent().getSerializableExtra("brand");
        Toast.makeText(FilteredDisplayActivity.this,brand.toString(),Toast.LENGTH_LONG).show();
        pd = new ProgressDialog(FilteredDisplayActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(true);
        pd.setTitle("Loading....");
        pd.setMessage("Please Wait");
        pd.show();



        mProductList = (RecyclerView) findViewById(R.id.product_list);
        mProductList.setHasFixedSize(true);
        mProductList.setLayoutManager(new GridLayoutManager(this, 2));

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        ProductList = new ArrayList<>();

        brandList = new ArrayList<>();

        db.collection("Filter").document(currentUser).collection("list").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                brandList.clear();

                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    Filter filter = doc.toObject(Filter.class);
                    brandList.add(filter);
                }

                filterList();
            }
        });

//        if(brand.isEmpty())
//        {
//            db.collection("AllItems").whereGreaterThan("item_price",hashMapObject.get("price_low")).whereLessThan("item_price",hashMapObject.get("price_high")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    if (task.isSuccessful()){
//                        for (QueryDocumentSnapshot doc : task.getResult()){
//                            Product p = doc.toObject(Product.class);
//                            ProductList.add(p);
//                        }
//
//                        productAdapter = new ProductAdapter(FilteredDisplayActivity.this, ProductList);
//                        mProductList.setAdapter(productAdapter);
//
//                        //If ProgressDialog is showing Dismiss it
//                        if(pd.isShowing())
//                        {
//                            pd.dismiss();
//                        }
//
//                    }
//                    else {
//                        pd.dismiss();
//                    }
//                }
//            });
//        }
//        else
//        {
//            db.collection("AllItems").whereEqualTo("item_brand",brand.get(0)).whereGreaterThan("item_price",hashMapObject.get("price_low")).whereLessThan("item_price",hashMapObject.get("price_high")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    if (task.isSuccessful()){
//                        for (QueryDocumentSnapshot doc : task.getResult()){
//                            Product p = doc.toObject(Product.class);
//                            ProductList.add(p);
//                        }
//
//                        productAdapter = new ProductAdapter(FilteredDisplayActivity.this, ProductList);
//                        mProductList.setAdapter(productAdapter);
//
//                        //If ProgressDialog is showing Dismiss it
//                        if(pd.isShowing())
//                        {
//                            pd.dismiss();
//                        }
//
//                    }
//                    else {
//                        pd.dismiss();
//                    }
//                }
//            });
//        }
    }

    private void filterList() {

        ProductList = new ArrayList<>();

        db.collection("AllItems").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                ProductList.clear();

                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    Product p = doc.toObject(Product.class);
                    for (Filter filter : brandList){
                        if (p.getItem_id().equals(filter.getUid())){
                            ProductList.add(p);
                        }
                    }
                }
                productAdapter = new ProductAdapter(FilteredDisplayActivity.this, ProductList);
                mProductList.setAdapter(productAdapter);

                //If ProgressDialog is showing Dismiss it
                if(pd.isShowing())
                {
                    pd.dismiss();
                }else {
                    pd.dismiss();
                }

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        db.collection("Filter").document(currentUser).collection("list").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        String uid = doc.get("uid").toString();
                        db.collection("Filter").document(currentUser).collection("list").document(uid).delete();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.collection("Filter").document(currentUser).collection("list").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        String uid = doc.get("uid").toString();
                        db.collection("Filter").document(currentUser).collection("list").document(uid).delete();
                    }
                }
            }
        });
    }
}
