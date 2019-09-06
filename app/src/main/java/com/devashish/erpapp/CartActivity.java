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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

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
    HashMap hashMapSecond;
    String user_image,user_name,user_search;
    int price;
    String p_id;
    LinearLayout savedProducts;

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

        totalprice = (TextView) findViewById(R.id.totalPrice);
        orderButton = (Button) findViewById(R.id.orderButton);

        savedProducts = (LinearLayout) findViewById(R.id.savedProducts);

        savedProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this,SavedActivity.class);
                startActivity(intent);
            }
        });
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        mOrderList = (RecyclerView) findViewById(R.id.order_list);
        mOrderList.setHasFixedSize(false);
        mOrderList.setLayoutManager(new LinearLayoutManager(this));

        OrderList = new ArrayList<>();

        db.collection("Dealers").document(firebaseUser.getUid()).collection("Cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Order p = doc.toObject(Order.class);
                        OrderList.add(p);
                        price = price + (Integer.parseInt(p.getProduct_price().toString()));
                    }

                    OrderAdapter = new CartAdapter(CartActivity.this, OrderList,totalprice);
                    mOrderList.setAdapter(OrderAdapter);
                    totalprice.setText("₹" + String.valueOf(price));
                    //If ProgressDialog is showing Dismiss it
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }

                }
            }
        });

        hashMapSecond = new HashMap<String, String>();

        db.collection("Dealers").document(firebaseUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    user_image = documentSnapshot.get("image").toString();
                    user_name = documentSnapshot.get("name").toString();
                    user_search = user_name.toLowerCase();
                    hashMapSecond.put("image", user_image);
                    hashMapSecond.put("name", user_name);
                    hashMapSecond.put("search", user_search);
                    hashMapSecond.put("uid", firebaseUser.getUid().toString());
                }
            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!OrderList.isEmpty())
                {
                    orderButton.setClickable(false);
                    pd = new ProgressDialog(CartActivity.this);
                    pd.setCanceledOnTouchOutside(false);
                    pd.setCancelable(true);
                    pd.setTitle("Loading....");
                    pd.setMessage("Please Wait");
                    pd.show();

                    Date c = Calendar.getInstance().getTime();
                    System.out.println("Current time => " + c);

                    SimpleDateFormat year = new SimpleDateFormat("yyyy");
                    SimpleDateFormat month = new SimpleDateFormat("MMM");
                    final String year_for = year.format(c);
                    final String month_for = month.format(c);

                    db.collection("Dealers").document(firebaseUser.getUid()).collection("Cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot doc : task.getResult()) {

                                    final String d_id = doc.get("dealer_id").toString();
                                    p_id = doc.get("product_id").toString();
                                    String p_image = doc.get("product_image").toString();
                                    final String p_name = doc.get("product_name").toString();
                                    String p_price = doc.get("product_price").toString();
                                    final String quantity = doc.get("quantity").toString();
                                    String p_status = doc.get("status").toString();

                                    final HashMap<String, Object> hashMap = new HashMap();

                                    final String push = db.collection("AllOrders").document().getId();

                                    hashMap.put("product_name", p_name);
                                    hashMap.put("dealer_id", d_id);
                                    hashMap.put("product_id", p_id);
                                    hashMap.put("status", "ordered");
                                    hashMap.put("product_price", p_price);
                                    hashMap.put("product_image", p_image);
                                    hashMap.put("quantity", quantity);
                                    hashMap.put("status", p_status);
                                    hashMap.put("year", year_for);
                                    hashMap.put("push_id", push);
                                    hashMap.put("month", month_for);
                                    hashMap.put("search", p_name.toLowerCase());

                                    db.collection("AllOrders").document(push).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                            } else {
                                                pd.dismiss();
                                                orderButton.setClickable(true);
                                                Toast.makeText(CartActivity.this, "FAIL", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                    db.collection("OrderById").document(d_id).collection("Orders").document(p_id).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                            } else {
                                                pd.dismiss();
                                                orderButton.setClickable(true);
                                                Toast.makeText(CartActivity.this, "FAIL", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                    db.collection("Dealers").document(firebaseUser.getUid()).collection("Orders").document(p_id).set(hashMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                    }

                                                }
                                            });
                                    db.collection("Dealers").document(firebaseUser.getUid()).collection("Cart").document(p_id).delete().addOnCompleteListener(
                                            new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(CartActivity.this, "deleted" + p_name, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                    );

                                    db.collection("OrderById").document(firebaseUser.getUid()).set(hashMapSecond).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });

                                    db.collection("OrderById").document(firebaseUser.getUid()).collection("Orders").document(push).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });

                                }

                            }
                        }
                    }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        String message = "You just received an order from " + firebaseUser.getUid() + " \n\nPRODUCT NAME : " + "multiple products" + "\nQuantity : " + "multiple quantity";
//                        SendMail sm = new SendMail(CartActivity.this, "devashisht2914@gmail.com", "ORDER FROM " + firebaseUser.getUid(), message);
//                        //Executing sendmail to send email
//                        sm.execute();
                            Toast.makeText(CartActivity.this, "SUCCESS", Toast.LENGTH_LONG).show();
                            pd.dismiss();
                            Intent intent = new Intent(CartActivity.this, OrderConfirmedActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }

            }
        });

    }

    void setUpToolBar() {
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
