package com.example.dealerapp;

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

import com.example.dealerapp.Utils.ALlItems;
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

public class AllItemsListAdapter extends FirestoreRecyclerAdapter<ALlItems, AllItemsListAdapter.ViewHolder> {

    private Context mContext;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    OnItemClick onItemClick;

    public AllItemsListAdapter(Context mContext, FirestoreRecyclerOptions<ALlItems> options) {
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
    protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final ALlItems model) {

        holder.name.setText(model.getItem_name());
        holder.price.setText(String.valueOf(model.getItem_price()) + " Rs");
        holder.brand.setText(model.getItem_brand());
        holder.mrp.setText(String.valueOf(model.getItem_mrp()));
        holder.discount.setText(String.valueOf(model.getItem_discount()));

        Picasso picasso = Picasso.get();
        picasso.setIndicatorsEnabled(false);
        picasso.load(model.getItem_image()).into(holder.itemImage);

        holder.itemCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.getPosition(model.getItem_id());
            }
        });


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_single_item, viewGroup, false);

        return new AllItemsListAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name, brand, price, discount, mrp;
        private ImageView itemImage;
        private CardView itemCard;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_single_item_name);
            brand = itemView.findViewById(R.id.product_single_item_brand);
            price = itemView.findViewById(R.id.product_single_item_price);
            itemImage = itemView.findViewById(R.id.product_single_item_image);
            itemCard = itemView.findViewById(R.id.single_item_card);
            mrp = itemView.findViewById(R.id.single_item_mrp);
            discount = itemView.findViewById(R.id.single_item_discount_percent);

        }

    }
}
