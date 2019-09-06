package com.devashish.erpapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseFirestore mUserDatabase;
    private FirebaseUser mCurrentUser;

    //Android Layout

    private CircleImageView mDisplayImage;
    private TextView mName,mMobile,mCity,mState,mShopName,mShopGST,mShopAddress;

    private Button mDetailBtn;
    private Button mImageBtn;

    String download_url;
    String thumb_downloadUrl;

    private static final int GALLERY_PICK = 1;

    // Storage Firebase
    private StorageReference mImageStorage;

    private ProgressDialog mProgressDialog;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mDisplayImage = (CircleImageView) findViewById(R.id.settings_image);
        mName = (TextView) findViewById(R.id.settings_name);
        mMobile = (TextView) findViewById(R.id.settings_phone);
        mShopName = (TextView) findViewById(R.id.shop_name);
        mCity = (TextView) findViewById(R.id.shop_city);
        mState = (TextView) findViewById(R.id.shop_state);
        mShopAddress = (TextView) findViewById(R.id.shop_address);
        mShopGST = (TextView) findViewById(R.id.shop_gst);

        mDetailBtn = (Button) findViewById(R.id.settings_status_btn);
        mImageBtn = (Button) findViewById(R.id.settings_image_btn);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();


        pd = new ProgressDialog(ProfileActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(true);
        pd.setTitle("Loading....");
        pd.setMessage("Please Wait");
        pd.show();

        //mUserDatabase = FirebaseFirestore.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase = FirebaseFirestore.getInstance();
        mUserDatabase.collection("Dealers").document(current_uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                final String image = documentSnapshot.get("image").toString();
                String first_name = documentSnapshot.get("name").toString();
                String last_name = documentSnapshot.get("name").toString();
                String name = first_name;
                String gst = documentSnapshot.get("gst").toString();
                String shopname = documentSnapshot.get("shopname").toString();
                String city = documentSnapshot.get("city").toString();
                String state = documentSnapshot.get("state").toString();
                String mob = documentSnapshot.get("mob").toString();
                //String address = documentSnapshot.get("address").toString();

                mName.setText(name);
                mMobile.setText(mob);
                mShopName.setText(shopname);
                mCity.setText(city);
                mState.setText(state);
                //mShopAddress.setText(address);
                mShopGST.setText(gst);

                if (!image.equals("default")) {
                    //Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.logo).into(mDisplayImage);

                    Picasso.get().load(image).placeholder(R.drawable.placeholder).into(mDisplayImage);

                }

                pd.dismiss();
            }
        });

        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);

                /*
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ProfileActivity.this);
                        */

            }
        });

        mDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mob = mMobile.getText().toString();
                String name = mName.getText().toString();

                Intent intent = new Intent(ProfileActivity.this,EditProfileActivity.class);
                intent.putExtra("mob",mob);
                intent.putExtra("name",name);
                intent.putExtra("state",mState.getText().toString());
                intent.putExtra("city",mCity.getText().toString());
                intent.putExtra("gst",mShopGST.getText().toString());
                intent.putExtra("address",mShopAddress.getText().toString());
                intent.putExtra("shopname",mShopName.getText().toString());
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                Picasso picasso = Picasso.get();
                picasso.setIndicatorsEnabled(false);
                picasso.load(resultUri).placeholder(R.mipmap.ic_launcher).into(mDisplayImage);

                pd.setMessage("updating profile");
                pd.setCanceledOnTouchOutside(false);
                pd.show();

                final File thumb_file_Path = new File(resultUri.getPath());

                final Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(50)
                        .compressToBitmap(thumb_file_Path);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                final byte[] thumb_byte = baos.toByteArray();

                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumb")
                        .child(mCurrentUser.getUid().toString() + ".jpg");

                UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){

                            thumb_filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String thumb_downloadUrl = uri.toString();

                                    Map update_hashMap = new HashMap();
                                    update_hashMap.put("image", thumb_downloadUrl);

                                    mUserDatabase.collection("Dealers").document(mCurrentUser.getUid())
                                            .update(update_hashMap).addOnSuccessListener(new OnSuccessListener() {
                                        @Override
                                        public void onSuccess(Object o) {
                                            pd.dismiss();
                                            Toast.makeText(ProfileActivity.this, "profile updated", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });

                        }
                    }
                });


            }
        }
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(20);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }




}
