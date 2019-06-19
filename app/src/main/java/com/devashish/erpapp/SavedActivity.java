package com.devashish.erpapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.CompoundButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SavedActivity extends AppCompatActivity {


    RecyclerView mOrderList;
    List<Order> OrderList;
    SaveAdapter OrderAdapter;
    private FirebaseAuth mAuth;
    ProgressDialog pd;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        setUpToolBar();

        pd = new ProgressDialog(SavedActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setTitle("Loading....");
        pd.setMessage("Please Wait");
        pd.show();


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        mOrderList = (RecyclerView) findViewById(R.id.order_list);
        mOrderList.setHasFixedSize(false);
        mOrderList.setLayoutManager(new LinearLayoutManager(this));

        OrderList = new ArrayList<>();

        db.collection("Users").document(firebaseUser.getUid().toString()).collection("Saved").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot doc : task.getResult()){
                        Order p = doc.toObject(Order.class);
                        OrderList.add(p);
                    }

                    OrderAdapter = new SaveAdapter(SavedActivity.this, OrderList);
                    mOrderList.setAdapter(OrderAdapter);

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
