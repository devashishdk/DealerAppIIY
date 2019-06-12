package com.devashish.erpapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devashish.erpapp.Product;
import com.devashish.erpapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.TestViewHolder>{

    Context mCtx;
    List<Product> ProductsList;

    ProductAdapter(Context mCtx,List<Product> ProductsList)
    {
        this.mCtx = mCtx;
        this.ProductsList = ProductsList;
    }
    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mCtx).inflate(R.layout.single_product,
                parent, false);
        TestViewHolder testViewHolder = new TestViewHolder(view);
        return testViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final TestViewHolder holder, int position) {
        Product Product = ProductsList.get(position);

        holder.product_name.setText(Product.getItem_name());
        holder.price.setText("₹"+Product.getItem_price());
        holder.mrp.setText(Product.getItem_mrp());
        holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.discount.setText(Product.getItem_discount()+"%");
        holder.brand_name.setText(Product.getItem_brand());
        String image = Product.getItem_image();
        Picasso.with(mCtx).load(image).placeholder(R.drawable.shopone).into(holder.product_image);


        final String PushId = ProductsList.get(position).getItem_id();
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

    }

    @Override
    public int getItemCount() {
        return ProductsList.size();
    }

    class TestViewHolder extends RecyclerView.ViewHolder
    {
        TextView price,mrp,discount,brand_name,product_name;
        CardView card_layout;
        ImageView product_image;

        public TestViewHolder(View itemView) {
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
