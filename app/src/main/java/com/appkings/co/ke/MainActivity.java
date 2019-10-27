package com.appkings.co.ke;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.appkings.co.ke.Products.AddProductsActivity;
import com.appkings.co.ke.Products.ProductsModelClass;
import com.appkings.co.ke.Users.LoginActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference productsReference;
    String currentUserID;

    private FloatingActionButton addProductsFab;
    private RecyclerView productsRecyclerView;

    /**
     * Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content),
     * "You need to update your profile", Snackbar.LENGTH_LONG);
     * snackBar.show();
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if (auth == null) {
            sendUserToLoginActivity();
        }

        mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        productsReference = FirebaseDatabase.getInstance().getReference().child("Products");

        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        displayAllProductsLayout();


        addProductsFab = findViewById(R.id.fab);
        addProductsFab.setOnClickListener(view -> sendUserToAddProducts());


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

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        productsRecyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);

        //Query productsQuery = productsReference;

        final FirebaseRecyclerOptions<ProductsModelClass> options = new
                FirebaseRecyclerOptions.Builder<ProductsModelClass>()
                .setQuery(productsReference, ProductsModelClass.class)
                .build();

        FirebaseRecyclerAdapter<ProductsModelClass, ProductsViewHolder> fireBaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ProductsModelClass, ProductsViewHolder>(options) {
                    @Override
                    protected void
                    onBindViewHolder(@NonNull ProductsViewHolder holder,
                                                    int position,
                                     @NonNull ProductsModelClass model) {

                        String singleProductName = model.getProductName();
                        String singleProductDescription = model.getProductDescription();
                        String singleProductPrice = model.getSellingPrice();

                        holder.productName.setText(singleProductName);
                        holder.productDescription.setText(singleProductDescription);
                        holder.productSellingPrice.setText(singleProductPrice);
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

        productsRecyclerView.setLayoutManager(gridLayoutManager);
        productsRecyclerView.setAdapter(fireBaseRecyclerAdapter);
        fireBaseRecyclerAdapter.notifyDataSetChanged();
        fireBaseRecyclerAdapter.startListening();


    }


    private static class ProductsViewHolder extends RecyclerView.ViewHolder {
        private TextView productName;
        private TextView productDescription;
        private TextView productSellingPrice;

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
}
