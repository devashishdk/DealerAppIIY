package com.devashish.erpapp;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.annotation.Nullable;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder>{

    Context mCtx;
    List<Order> OrderList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    OrderAdapter(Context mCtx, List<Order> OrderList)
    {
        this.mCtx = mCtx;
        this.OrderList = OrderList;
    }
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mCtx).inflate(R.layout.single_layout_order,
                parent, false);
        OrderViewHolder OrderViewHolder = new OrderViewHolder(view);
        return OrderViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position) {
        final Order Order = OrderList.get(position);
        holder.price.setText("₹" + Order.getProduct_price());
        holder.orderid.setText(Order.getProduct_id());
        holder.name.setText(Order.getProduct_name());
        holder.quantity.setText("X " + Order.getQuantity());
        holder.status.setText(Order.getStatus());
        Picasso.with(mCtx).load(Order.getProduct_image()).placeholder(R.drawable.orders).into(holder.imageView);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
                db.collection("Dealers").document(Order.getDealer_id()).collection("Orders").document(Order.getProduct_id()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            db.collection("AllOrders").document(Order.getPush_id()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
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
                    }
                });
            }
        });

        if (holder.status.getText().toString().equals("Confirmed")){
            holder.cancelButton.setVisibility(View.GONE);
            holder.deleteOrd.setVisibility(View.VISIBLE);

        }else {
            holder.deleteOrd.setVisibility(View.GONE);
            holder.cancelButton.setVisibility(View.VISIBLE);
        }

    }
    @Override
    public int getItemCount() {
        return OrderList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageView, deleteOrd;
        TextView name,price,quantity,orderid,status;
        CardView card_layout;
        //AppCompatRatingBar ratingBar;
        Button callButton,billButton,cancelButton,ratingButton;
        public OrderViewHolder(View itemView) {
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
            deleteOrd = (ImageView) itemView.findViewById(R.id.delete_order);
            //ratingBar = (AppCompatRatingBar) itemView.findViewById(R.id.ratingBarMain);
        }
    }
}
