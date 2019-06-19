package com.devashish.erpapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CartActivity extends AppCompatActivity {


    RecyclerView mOrderList;
    List<Order> OrderList;
    CartAdapter OrderAdapter;
    private FirebaseAuth mAuth;
    ProgressDialog pd;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser;
    Toolbar toolbar;
    TextView totalprice;
    Button orderButton;
    HashMap<String,String> hashMap;
    LinearLayout savedLinear;
    int price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        setUpToolBar();

        pd = new ProgressDialog(CartActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setTitle("Loading....");
        pd.setMessage("Please Wait");
        pd.show();

        savedLinear = (LinearLayout) findViewById(R.id.savedProducts);

        savedLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this,SavedActivity.class);
                startActivity(intent);
            }
        });
        totalprice = (TextView) findViewById(R.id.totalPrice);
        orderButton = (Button) findViewById(R.id.orderButton);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        mOrderList = (RecyclerView) findViewById(R.id.order_list);
        mOrderList.setHasFixedSize(false);
        mOrderList.setLayoutManager(new LinearLayoutManager(this));

        OrderList = new ArrayList<>();

        db.collection("Users").document(firebaseUser.getUid().toString()).collection("Cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        Order p = doc.toObject(Order.class);
                        OrderList.add(p);
                        price = price + (Integer.parseInt(p.getQuantity()) * Integer.parseInt(p.getProduct_price().toString()));
                    }

                    OrderAdapter = new CartAdapter(CartActivity.this, OrderList);
                    mOrderList.setAdapter(OrderAdapter);

                    totalprice.setText("₹"+String.valueOf(price));
                    //If ProgressDialog is showing Dismiss it
                    if(pd.isShowing())
                    {
                        pd.dismiss();
                    }

                }
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderButton.setClickable(false);
                pd = new ProgressDialog(CartActivity.this);
                pd.setCanceledOnTouchOutside(false);
                pd.setCancelable(true);
                pd.setTitle("Loading....");
                pd.setMessage("Please Wait");
                pd.show();

                for(Order o : OrderList)
                {
                    final String product_name = o.getProduct_name();
                    final String product_quantity = o.getQuantity().toString();
                    final String product_id = o.getProduct_id().toString();
                    hashMap = new HashMap<String, String>();
                    hashMap.put("product_name",o.getProduct_name().toString());
                    hashMap.put("dealer_id",firebaseUser.getUid());
                    hashMap.put("product_id",o.getProduct_id());
                    hashMap.put("status","Pending");
                    hashMap.put("product_name",product_name);
                    hashMap.put("product_price",o.getProduct_price());
                    hashMap.put("product_image",o.getProduct_image());
                    hashMap.put("quantity",product_quantity);

                    db.collection("AllOrders").add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful())
                            {
                                db.collection("Users").document(firebaseUser.getUid().toString()).collection("Orders").document(product_id).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        String message = "You just received an order from "+firebaseUser.getUid().toString()+ " \n\nPRODUCT NAME : "+product_name + "\nQuantity : "+ product_quantity;
                                        SendMail sm = new SendMail(CartActivity.this, "devashisht2914@gmail.com", "ORDER FROM "+firebaseUser.getUid().toString(), message);
                                        //Executing sendmail to send email
                                        sm.execute();
                                        Toast.makeText(CartActivity.this,"SUCCESS",Toast.LENGTH_LONG).show();
                                        pd.dismiss();
                                    }
                                });
                            }
                            else
                            {
                                pd.dismiss();
                                orderButton.setClickable(true);
                                Toast.makeText(CartActivity.this,"FAIL",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                Intent intent = new Intent(CartActivity.this,OrderConfirmedActivity.class);
                startActivity(intent);
                finish();

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
