package com.devashish.erpapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    EditText mName,mMobile,mState,mCity,mAddress,mGST,mShopName;
    Button saveButton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mName = (EditText) findViewById(R.id.mName);
        mShopName = (EditText) findViewById(R.id.mShopName);
        mMobile = (EditText) findViewById(R.id.mMobile);
        mState = (EditText) findViewById(R.id.mState);
        mCity = (EditText) findViewById(R.id.mCity);
        mAddress = (EditText) findViewById(R.id.mAddress);
        mGST = (EditText) findViewById(R.id.mGST);

        saveButton = (Button) findViewById(R.id.mSaveButton);
        String name = getIntent().getStringExtra("name");
        String mob = getIntent().getStringExtra("mob");
        String city = getIntent().getStringExtra("city");
        String state = getIntent().getStringExtra("state");
        String address = getIntent().getStringExtra("address");
        String gst = getIntent().getStringExtra("gst");
        String shopname = getIntent().getStringExtra("shopname");

        mName.setText(name);
        mMobile.setText(mob);
        mCity.setText(city);
        mState.setText(state);
        mAddress.setText(address);
        mGST.setText(gst);
        mShopName.setText(shopname);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> hashMap = new HashMap<String,String>();
                hashMap.put("name",mName.getText().toString());
                hashMap.put("shopname",mShopName.getText().toString());
                hashMap.put("city",mCity.getText().toString());
                hashMap.put("state",mState.getText().toString());
                hashMap.put("gst",mGST.getText().toString());
                hashMap.put("mob",mMobile.getText().toString());
                db.collection("Dealers").document(firebaseUser.getUid()).update((Map)hashMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        Intent intent = new Intent(EditProfileActivity.this,ProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }
}
