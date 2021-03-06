package com.devashish.erpapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    EditText userName, userID, userPass, userState, userCity,userGST,userShopName, userAddress, userPhone;
    Button signUpButton;

    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        userID = (EditText) findViewById(R.id.UserId);
        userName = (EditText) findViewById(R.id.UserName);
        userPass = (EditText) findViewById(R.id.UserPass);
        userPhone = (EditText) findViewById(R.id.Phone);
        userAddress = (EditText) findViewById(R.id.address);
        userCity = (EditText) findViewById(R.id.city);
        userState = (EditText) findViewById(R.id.state);
        userGST = (EditText) findViewById(R.id.gst_number);
        userShopName = (EditText) findViewById(R.id.shop_name);

        signUpButton = (Button) findViewById(R.id.SignUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int flag = 1;
                String userId = userID.getText().toString();
                String pass = userPass.getText().toString();

                if(TextUtils.isEmpty(userShopName.getText()))
                {
                    userShopName.setError("This field is compulsory");
                    userShopName.requestFocus();
                    flag = 0;
                }
                if(TextUtils.isEmpty(userName.getText()))
                {
                    userName.setError("This field is compulsory");
                    userName.requestFocus();
                    flag = 0;

                }
                if(TextUtils.isEmpty(userGST.getText()))
                {
                    userGST.setError("This field is compulsory");
                    userGST.requestFocus();
                    flag = 0;

                }
                if(TextUtils.isEmpty(userGST.getText()))
                {
                    userGST.setError("This field is compulsory");
                    userGST.requestFocus();
                    flag = 0;

                }
                if(TextUtils.isEmpty(userPhone.getText()))
                {
                    userPhone.setError("This field is compulsory");
                    userPhone.requestFocus();
                    flag = 0;

                }
                if(TextUtils.isEmpty(userAddress.getText()))
                {
                    userAddress.setError("This field is compulsory");
                    userAddress.requestFocus();
                    flag = 0;

                }
                if(TextUtils.isEmpty(userCity.getText()))
                {
                    userCity.setError("This field is compulsory");
                    userCity.requestFocus();
                    flag = 0;

                }
                if(TextUtils.isEmpty(userState.getText()))
                {
                    userState.setError("This field is compulsory");
                    userState.requestFocus();
                    flag = 0;

                }
                if(TextUtils.isEmpty(userID.getText()))
                {
                    userID.setError("This field is compulsory");
                    userID.requestFocus();
                    flag = 0;

                }
                if(TextUtils.isEmpty(userPass.getText()))
                {
                    userPass.setError("Please enter Your Password");
                    userPass.requestFocus();
                    flag = 0;

                }

                if(userGST.getText().length() != 15)
                {
                    userGST.setError("Enter Correct GST");
                    userGST.requestFocus();
                    flag = 0;

                }
                if(userPhone.getText().length() != 10)
                {
                    userPhone.setError("Enter Correct GST");
                    userPhone.requestFocus();
                    flag = 0;

                }
                if(flag == 1) {
                    pd = new ProgressDialog(SignUpActivity.this);
                    pd.setCanceledOnTouchOutside(false);
                    pd.setCancelable(true);
                    pd.setTitle("Loading....");
                    pd.setMessage("Please Wait");
                    pd.show();

                    //register user
                    registerUser(userId, pass);
                }
            }
        });
    }

    void registerUser(String email, String password) {

        //Register user with this email and pass
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            //Updating data in firebase
                            FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                            //Getting UID of user
                            String uid = current_user.getUid();

                            String device_token = FirebaseInstanceId.getInstance().getToken();

                            //Making a hashmap

                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("name", userName.getText().toString());
                            userMap.put("mob", userPhone.getText().toString());
                            userMap.put("gst", userGST.getText().toString());
                            userMap.put("address", userAddress.getText().toString());
                            userMap.put("city", userCity.getText().toString());
                            userMap.put("state", userState.getText().toString());
                            userMap.put("shopname",userShopName.getText().toString());
                            userMap.put("image", "default");
                            userMap.put("thumb_image", "default");
                            userMap.put("device_token", device_token);

                            db.collection("Dealers").document(current_user.getUid().toString()).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        pd.dismiss();

                                        //On successfull move to main

                                        Intent mainIntent = new Intent(SignUpActivity.this, MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();

                                    }
                                }
                            });
                        } else {
                            pd.dismiss();
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...


                    }


                });

    }

}
