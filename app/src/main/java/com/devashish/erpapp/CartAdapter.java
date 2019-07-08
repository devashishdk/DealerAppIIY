package com.devashish.erpapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    Context mCtx;
    List<Order> OrderList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    HashMap<String,String> hashMap;
    int ProductPrice;

    CartAdapter(Context mCtx, List<Order> OrderList)
    {
        this.mCtx = mCtx;
        this.OrderList = OrderList;
    }
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mCtx).inflate(R.layout.single_cart_layout,
                parent, false);
        CartViewHolder CartViewHolder = new CartViewHolder(view);
        return CartViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) {
        final Order Order = OrderList.get(position);
        holder.price.setText("₹"+Order.getProduct_price());
        holder.orderid.setText(Order.getProduct_id());
        holder.name.setText(Order.getProduct_name());
        holder.quantity.setText("X "+Order.getQuantity());
        holder.quant.setText(Order.getQuantity());
        String status = Order.getStatus().toString();
        holder.status.setText(Order.getStatus());
        Picasso.with(mCtx).load(Order.getProduct_image()).placeholder(R.drawable.orders).into(holder.imageView);

        final String PushId = OrderList.get(position).getProduct_id();
        holder.card_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.card_layout.setClickable(false);
                Intent mIntent = new Intent(mCtx, DescriptionActivity.class);
                mIntent.putExtra("Key", PushId);
                mCtx.startActivity(mIntent);
                holder.card_layout.setClickable(true);
            }
        });

        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cancelButton.setClickable(false);
                holder.cancelButton.setText("CANCELED");
                db.collection("Dealers").document(Order.getDealer_id()).collection("Cart").document(Order.getProduct_id()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            OrderList.remove(position);
                            notifyDataSetChanged();
                        }
                    }
                });
            }
        });
        holder.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.saveButton.setClickable(false);
                holder.saveButton.setText("SAVED");

                hashMap = new HashMap<String, String>();
                hashMap = new HashMap<String, String>();
                hashMap.put("product_name",Order.getProduct_name());
                hashMap.put("dealer_id",Order.getDealer_id());
                hashMap.put("product_id",Order.getProduct_id());
                hashMap.put("status","Pending");
                hashMap.put("product_price",holder.price.getText().toString());
                hashMap.put("product_image",Order.getProduct_image());
                hashMap.put("quantity",holder.quant.getText().toString());
                hashMap.put("year", Order.getYear());
                hashMap.put("month", Order.getYear());
                hashMap.put("search", Order.getProduct_name().toLowerCase());

                db.collection("Dealers").document(Order.getDealer_id()).collection("Saved").document(Order.getProduct_id()).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

                db.collection("Dealers").document(Order.getDealer_id()).collection("Cart").document(Order.getProduct_id()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            OrderList.remove(position);
                            notifyDataSetChanged();
                        }
                    }
                });

            }
        });

        holder.addq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.addq.setClickable(false);
                final int valQuantitiy = Integer.valueOf(holder.quant.getText().toString());
                //int valPriceItem  = Integer.valueOf(holder.price.getText().toString());
                final String valPrice = String.valueOf(Integer.valueOf(String.valueOf(valQuantitiy + 1)) * Integer.valueOf(Order.getOriginal_price()));
                final HashMap<String,String> hash = new HashMap<String, String>();
                hash.put("quantity",String.valueOf(valQuantitiy + 1));
                hash.put("product_price",valPrice);
                db.collection("Dealers").document(Order.getDealer_id()).collection("Cart").document(Order.getProduct_id()).update((Map)hash).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            holder.quant.setText(String.valueOf(valQuantitiy + 1));
                            holder.price.setText(valPrice);
                            holder.quantity.setText(String.valueOf(valQuantitiy + 1));
                            holder.addq.setClickable(true);
                        }
                    }
                });
            }
        });

        holder.delq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.delq.setClickable(false);
                final int valQuantitiy = Integer.valueOf(holder.quant.getText().toString());
                //int valPriceItem  = Integer.valueOf(holder.price.getText().toString());
                final String valPrice = String.valueOf(Integer.valueOf(String.valueOf(valQuantitiy - 1)) * Integer.valueOf(Order.getOriginal_price()));
                final HashMap<String,String> hash = new HashMap<String, String>();
                hash.put("quantity",String.valueOf(valQuantitiy + 1));
                hash.put("product_price",valPrice);
                db.collection("Dealers").document(Order.getDealer_id()).collection("Cart").document(Order.getProduct_id()).update((Map)hash).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            holder.quant.setText(String.valueOf(valQuantitiy - 1));
                            holder.price.setText(valPrice);
                            holder.quantity.setText(String.valueOf(valQuantitiy - 1));
                            holder.addq.setClickable(true);
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return OrderList.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView;
        TextView name,price,quantity,orderid,status;
        CardView card_layout;
        ImageButton addq,delq;
        TextView quant;
        //AppCompatRatingBar ratingBar;
        Button callButton,billButton,cancelButton,saveButton;
        public CartViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.Order_image);

            name = (TextView) itemView.findViewById(R.id.product_name);
            //rating = (TextView) itemView.findViewById(R.id.Order_rating);
            card_layout = (CardView) itemView.findViewById(R.id.card_layout);
            orderid = (TextView) itemView.findViewById(R.id.product_id);
            quantity = (TextView) itemView.findViewById(R.id.product_quantity);
            price = (TextView) itemView.findViewById(R.id.product_price);
            status = (TextView) itemView.findViewById(R.id.status);
            cancelButton = (Button) itemView.findViewById(R.id.cancelButton);
            saveButton = (Button) itemView.findViewById(R.id.saveButton);
            addq = (ImageButton) itemView.findViewById(R.id.add_q);
            delq = (ImageButton) itemView.findViewById(R.id.del_q);
            quant = (TextView) itemView.findViewById(R.id.quantity);
            //ratingBar = (AppCompatRatingBar) itemView.findViewById(R.id.ratingBarMain);
        }
    }
}
