package com.smartwareafrica.pos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smartwareafrica.pos.Cart.CartActivity;
import com.smartwareafrica.pos.Products.AddProductsActivity;
import com.smartwareafrica.pos.Products.ProductsModelClass;
import com.smartwareafrica.pos.Products.ViewProductActivity;
import com.smartwareafrica.pos.Users.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private CollectionReference productsReference;
    String currentUserID;

    private FloatingActionButton addProductsFab;
    private RecyclerView productsRecyclerView;

    private ImageButton shoppingCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if (auth == null) {
            sendUserToLoginActivity();
        } else {

            mAuth = FirebaseAuth.getInstance();
            currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
            productsReference = FirebaseFirestore.getInstance().collection("Products");

            shoppingCartButton = findViewById(R.id.shoppingCartButton);
            shoppingCartButton.setOnClickListener(view -> sendUserToShoppingCart());

            productsRecyclerView = findViewById(R.id.productsRecyclerView);
            displayAllProductsLayout();

            addProductsFab = findViewById(R.id.fab);
            addProductsFab.setOnClickListener(view -> sendUserToAddProducts());

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if (auth == null) {
            sendUserToLoginActivity();
        }
    }

    private void displayAllProductsLayout() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        productsRecyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        //Query productsQuery = productsReference;

        final FirestoreRecyclerOptions<ProductsModelClass> options = new
                FirestoreRecyclerOptions.Builder<ProductsModelClass>()
                .setQuery(productsReference, ProductsModelClass.class)
                .build();

        FirestoreRecyclerAdapter<ProductsModelClass, ProductsViewHolder> fireBaseRecyclerAdapter =
                new FirestoreRecyclerAdapter<ProductsModelClass, ProductsViewHolder>(options) {
                    @Override
                    protected void
                    onBindViewHolder(@NonNull ProductsViewHolder holder,
                                     int position,
                                     @NonNull ProductsModelClass model) {

                        String singleProductName = model.getProductName();
                        String singleProductDescription = model.getProductDescription();
                        String singleProductPrice = "Ksh: " + model.getSellingPrice();

                        holder.productName.setText(singleProductName);
                        holder.productDescription.setText(singleProductDescription);
                        holder.productSellingPrice.setText(singleProductPrice);

                        holder.itemView.setOnClickListener(view -> {
                            final String productId = getSnapshots().getSnapshot(position).getId();
                            Intent productIntent = new Intent(MainActivity.this,
                                    ViewProductActivity.class);
                            productIntent.putExtra("productId", productId);
                            startActivity(productIntent);
                        });
                    }

                    @NonNull
                    @Override
                    public ProductsViewHolder
                    onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.single_product_layout, parent, false);
                        return new ProductsViewHolder(view);
                    }
                };

        productsRecyclerView.setLayoutManager(linearLayoutManager);
        productsRecyclerView.setAdapter(fireBaseRecyclerAdapter);
        fireBaseRecyclerAdapter.notifyDataSetChanged();
        fireBaseRecyclerAdapter.startListening();


    }


    private static class ProductsViewHolder extends RecyclerView.ViewHolder {
        private final TextView productName;
        private final TextView productDescription;
        private final TextView productSellingPrice;

        private ProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.productName);
            productDescription = itemView.findViewById(R.id.productDescription);
            productSellingPrice = itemView.findViewById(R.id.productSellingPrice);


        }
    }

    private void sendUserToAddProducts() {
        Intent addProductIntent = new Intent(this, AddProductsActivity.class);
        startActivity(addProductIntent);
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        finish();
        startActivity(loginIntent);
    }

    private void sendUserToShoppingCart() {
        Intent cartActivityIntent = new Intent(this, CartActivity.class);
        startActivity(cartActivityIntent);
    }
}
