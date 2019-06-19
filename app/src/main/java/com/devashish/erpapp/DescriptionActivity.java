package com.devashish.erpapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class DescriptionActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView navigationView;

    ViewPager viewPager;
    int images[] = {R.drawable.shopone,R.drawable.shopone};
    MyCustomPagerAdapter myCustomPagerAdapter;
    ProgressDialog pd;
    Toolbar toolbar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser;
    TextView name,price,mrp,brand,quantity,description,savings;
    Button orderButton,cartButton;
    ImageButton addQ,delQ;
    LinearLayout description_view;
    HashMap<String,String> hashMap;
    String product_name,product_mrp,product_price,product_brand,image,product_id,product_description;

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/font_app.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        setUpToolBar();


        pd = new ProgressDialog(DescriptionActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(true);
        pd.setTitle("Loading....");
        pd.setMessage("Please Wait");
        pd.show();


        description_view = (LinearLayout) findViewById(R.id.description_view);
        description_view.setVisibility(View.GONE);
        orderButton = (Button) findViewById(R.id.orderButton);
        cartButton = (Button) findViewById(R.id.cartButton);

        name = (TextView) findViewById(R.id.name);
        price = (TextView) findViewById(R.id.price);
        mrp = (TextView) findViewById(R.id.mrp);
        brand = (TextView) findViewById(R.id.brand);
        savings = (TextView) findViewById(R.id.saving);

        addQ = (ImageButton) findViewById(R.id.add_q);
        delQ = (ImageButton) findViewById(R.id.del_q);
        quantity = (TextView) findViewById(R.id.quantity);
        description = (TextView) findViewById(R.id.description);
        viewPager = (ViewPager)findViewById(R.id.viewPager);
        myCustomPagerAdapter = new MyCustomPagerAdapter(DescriptionActivity.this, images);
        viewPager.setAdapter(myCustomPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(viewPager, true);
        addQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = Integer.parseInt(quantity.getText().toString());
                if(val > 0)
                    quantity.setText(String.valueOf(val + 1));
            }
        });
        delQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int val = Integer.parseInt(quantity.getText().toString());
                if(val > 1)
                    quantity.setText(String.valueOf(val - 1));
            }
        });

        String key = getIntent().getStringExtra("Key");

        db.collection("AllItems").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    pd.dismiss();

                    DocumentSnapshot documentSnapshot = task.getResult();
                    product_name = documentSnapshot.get("item_name").toString();
                    product_mrp = documentSnapshot.get("item_mrp").toString();
                    product_price = documentSnapshot.get("item_price").toString();
                    product_brand = documentSnapshot.get("item_brand").toString();
                    image = documentSnapshot.get("item_image").toString();
                    product_id = documentSnapshot.get("item_id").toString();
                    product_description = documentSnapshot.get("item_desc").toString();

                    name.setText(product_name);
                    brand.setText(product_brand);
                    mrp.setText("₹"+product_mrp);
                    mrp.setPaintFlags(mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    price.setText("₹"+product_price);
                    description.setText(product_description);
                    savings.setText(String.valueOf(Integer.parseInt(product_mrp) - Integer.parseInt(product_price)));
                    description_view.setVisibility(View.VISIBLE);
                }
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(DescriptionActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DescriptionActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
                }


                cartButton.setClickable(false);
                pd = new ProgressDialog(DescriptionActivity.this);
                pd.setCanceledOnTouchOutside(false);
                pd.setCancelable(true);
                pd.setTitle("Loading....");
                pd.setMessage("Please Wait");
                pd.show();

                hashMap = new HashMap<String, String>();
                hashMap.put("product_name",name.getText().toString());
                hashMap.put("dealer_id",firebaseUser.getUid());
                hashMap.put("product_id",product_id);
                hashMap.put("status","Pending");
                hashMap.put("product_name",product_name);
                hashMap.put("product_price",String.valueOf(Integer.parseInt(product_price) * Integer.parseInt(quantity.getText().toString())));
                hashMap.put("product_image",image);
                hashMap.put("quantity",quantity.getText().toString());

                db.collection("Users").document(firebaseUser.getUid().toString()).collection("Cart").document(product_id).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            String message = "You just received an order from "+firebaseUser.getUid().toString()+ " \n\nPRODUCT NAME : "+product_name + "\nQuantity : "+quantity.getText().toString();
                            //SendMail sm = new SendMail(DescriptionActivity.this, "devashisht2914@gmail.com", "ORDER FROM "+firebaseUser.getUid().toString(), message);
                            //Executing sendmail to send email
                            //sm.execute();
                            Toast.makeText(DescriptionActivity.this,"ADDED TO CART",Toast.LENGTH_LONG).show();
                            pd.dismiss();
                            //Intent intent = new Intent(DescriptionActivity.this,OrderConfirmedActivity.class);
                            //startActivity(intent);
                            //finish();
                        }
                        else
                        {
                            pd.dismiss();
                            cartButton.setClickable(true);
                            Toast.makeText(DescriptionActivity.this,"FAIL",Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderButton.setClickable(false);
                pd = new ProgressDialog(DescriptionActivity.this);
                pd.setCanceledOnTouchOutside(false);
                pd.setCancelable(true);
                pd.setTitle("Loading....");
                pd.setMessage("Please Wait");
                pd.show();

                hashMap = new HashMap<String, String>();
                hashMap.put("product_name",name.getText().toString());
                hashMap.put("dealer_id",firebaseUser.getUid());
                hashMap.put("product_id",product_id);
                hashMap.put("status","Pending");
                hashMap.put("product_name",product_name);
                hashMap.put("product_price",product_price);
                hashMap.put("product_image",image);
                hashMap.put("quantity",quantity.getText().toString());

                db.collection("AllOrders").add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful())
                            {
                                db.collection("Users").document(firebaseUser.getUid().toString()).collection("Orders").document(product_id).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        String message = "You just received an order from "+firebaseUser.getUid().toString()+ " \n\nPRODUCT NAME : "+product_name + "\nQuantity : "+quantity.getText().toString();
                                        SendMail sm = new SendMail(DescriptionActivity.this, "devashisht2914@gmail.com", "ORDER FROM "+firebaseUser.getUid().toString(), message);
                                        //Executing sendmail to send email
                                        sm.execute();
                                        Toast.makeText(DescriptionActivity.this,"SUCCESS",Toast.LENGTH_LONG).show();
                                        pd.dismiss();


                                        Intent intent = new Intent(DescriptionActivity.this,checksum.class);
                                        intent.putExtra("orderid", "123");
                                        intent.putExtra("custid", "211");
                                        intent.putExtra("price",product_price);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                            else
                            {
                                pd.dismiss();
                                orderButton.setClickable(true);
                                Toast.makeText(DescriptionActivity.this,"FAIL",Toast.LENGTH_LONG).show();
                            }
                    }
                });
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
                        Intent intent = new Intent(DescriptionActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                        //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;
                    case (R.id.profile):
                        drawerLayout.closeDrawers();
                        Intent intentPro = new Intent(DescriptionActivity.this,ProfileActivity.class);
                        startActivity(intentPro);
                        //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;
                    case (R.id.Orders):
                        drawerLayout.closeDrawers();
                        Intent intentCategory = new Intent(DescriptionActivity.this,OrdersActivity.class);
                        startActivity(intentCategory);
                        //overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                        break;
                    case  (R.id.logout):
                        drawerLayout.closeDrawers();
                        FirebaseAuth.getInstance().signOut();
                        Intent intentL = new Intent(DescriptionActivity.this,LoginActivity.class);
                        startActivity(intentL);
                        finish();
                        break;
                }
                return false;
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
                Intent intent = new Intent(DescriptionActivity.this,SearchActivity.class);
                startActivity(intent);
                break;
            case (R.id.cart):
                Intent intentC = new Intent(DescriptionActivity.this,CartActivity.class);
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


}
