package com.devashish.erpapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devashish.erpapp.Product;
import com.devashish.erpapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.TestViewHolder>{

    Context mCtx;
    List<Product> BrandsList;
    int flagSelected = 0;
    private ProgressDialog dialog;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    BrandAdapter(Context mCtx,List<Product> BrandsList)
    {
        this.mCtx = mCtx;
        this.BrandsList = BrandsList;
    }
    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mCtx).inflate(R.layout.single_category,
                parent, false);
        TestViewHolder testViewHolder = new TestViewHolder(view);
        return testViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final TestViewHolder holder, int position) {
        final Product Brand = BrandsList.get(position);
        holder.name.setText(Brand.getItem_brand());
        dialog = new ProgressDialog(mCtx);
        holder.card_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setMessage("please wait");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                Brand.setSelected(!Brand.getSelected());
                holder.card_layout.setBackgroundColor(Brand.getSelected() ? Color.parseColor("#0288d1") : Color.WHITE);
                holder.name.setTextColor(Brand.getSelected() ? Color.WHITE : Color.BLACK);

                final String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

                String brand = holder.name.getText().toString();
                db.collection("AllItems").whereEqualTo("item_brand", brand).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                String cat_uid = doc.get("uid").toString();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uid", cat_uid);

                                db.collection("Filter").document(currentUser).collection("list").document(cat_uid).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            dialog.dismiss();
                                            Toast.makeText(mCtx, "selected", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }
                    }
                });


            }
        });
    }

    @Override
    public int getItemCount() {
        return BrandsList == null ? 0 :BrandsList.size();
    }

    class TestViewHolder extends RecyclerView.ViewHolder
    {
        TextView name;
        CardView card_layout;
        //AppCompatRatingBar ratingBar;
        Button callButton,billButton,cancelButton,ratingButton;
        public TestViewHolder(View itemView) {
            super(itemView);
            card_layout = (CardView) itemView.findViewById(R.id.sort_card);
            name = (TextView) itemView.findViewById(R.id.category_name);
            //ratingBar = (AppCompatRatingBar) itemView.findViewById(R.id.ratingBarMain);
        }
    }
}
