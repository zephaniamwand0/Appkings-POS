package com.appkings.co.ke.Cart;

import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appkings.co.ke.Interface.ItemClickListener;
import com.appkings.co.ke.R;

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
