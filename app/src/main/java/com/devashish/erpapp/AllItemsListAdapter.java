package com.devashish.erpapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllItemsListAdapter extends FirestoreRecyclerAdapter<Product, AllItemsListAdapter.ViewHolder> {

    private Context mContext;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    OnItemClick onItemClick;

    public AllItemsListAdapter(Context mContext, FirestoreRecyclerOptions<Product> options) {
        super(options);
        this.mContext = mContext;
        this.notifyDataSetChanged();
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick {

        void getPosition(String userId);

    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final Product model) {

        holder.product_name.setText(model.getItem_name());
        holder.price.setText(String.valueOf(model.getItem_price()) + " Rs");
        holder.brand_name.setText(model.getItem_brand());
        holder.mrp.setText(String.valueOf(model.getItem_mrp()));
        holder.discount.setText(String.valueOf(model.getItem_discount()));

        //Picasso picasso = Picasso.get();
        //picasso.setIndicatorsEnabled(false);
        //picasso.load(model.getItem_image()).into(holder.itemImage);

        holder.card_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.getPosition(model.getItem_id());
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_product, viewGroup, false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView price,mrp,discount,brand_name,product_name;
        CardView card_layout;
        ImageView product_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mrp = (TextView) itemView.findViewById(R.id.mrp);
            price = (TextView) itemView.findViewById(R.id.price);
            discount = (TextView) itemView.findViewById(R.id.discount);
            brand_name = (TextView) itemView.findViewById(R.id.brand_name);
            product_name = (TextView) itemView.findViewById(R.id.product_name);
            product_image = (ImageView) itemView.findViewById(R.id.product_image);
            card_layout = (CardView) itemView.findViewById(R.id.card_layout);
        }




    }
}
