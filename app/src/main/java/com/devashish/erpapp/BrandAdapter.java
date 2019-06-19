package com.devashish.erpapp;

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

import com.devashish.erpapp.Product;
import com.devashish.erpapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.TestViewHolder>{

    Context mCtx;
    List<Product> BrandsList;
    int flagSelected = 0;

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


        holder.card_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("custom-message");
                //            intent.putExtra("quantity",Integer.parseInt(quantity.getText().toString()));
                intent.putExtra("selected",Brand.getItem_brand());
                LocalBroadcastManager.getInstance(mCtx).sendBroadcast(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return BrandsList.size();
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
