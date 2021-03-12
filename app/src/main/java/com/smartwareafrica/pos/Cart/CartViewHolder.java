package com.smartwareafrica.pos.Cart;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartwareafrica.pos.Interface.ItemClickListener;
import com.smartwareafrica.pos.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView cartProductName;
    public TextView cartProductQuantity;
    public TextView cartProductPrice;

    private ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        cartProductName = itemView.findViewById(R.id.cartProductName);
        cartProductPrice = itemView.findViewById(R.id.cartProductPrice);
        cartProductQuantity = itemView.findViewById(R.id.cartProductQuantity);
    }

    @Override
    public void onClick(View view) {
itemClickListener.onClick(view,getAdapterPosition(),false);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
