package com.appkings.co.ke.Products;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.appkings.co.ke.Cart.CartActivity;
import com.appkings.co.ke.MainActivity;
import com.appkings.co.ke.R;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

public class ViewProductActivity extends AppCompatActivity {

    private TextView viewProductName;
    private TextView viewProductDescription;
    private TextView viewProductSellingPrice;
    private ElegantNumberButton adjustQuantityButton;
    private FloatingActionButton addToCartButton;

    private String productId = "";
    private String productQuantity = "";

    //For FireBase
    String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference productsRef;

    private ImageButton shoppingCartButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        productId = getIntent().getStringExtra("productId");
        productQuantity = getIntent().getStringExtra("productQuantity");



        mAuth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        productsRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Products")
                .child(productId);

        viewProductName = findViewById(R.id.viewProductName);
        viewProductDescription = findViewById(R.id.viewProductDescription);
        viewProductSellingPrice = findViewById(R.id.viewProductSellingPrice);
        adjustQuantityButton = findViewById(R.id.adjustQuantityButton);
        addToCartButton = findViewById(R.id.addToCartButton);

        addToCartButton.setOnClickListener(view -> addingToCartList());

        shoppingCartButton = findViewById(R.id.shoppingCartButton);
        shoppingCartButton.setOnClickListener(view -> sendUserToShoppingCart());

        //Setting value if item exists in cart
        if (productQuantity != null){
            adjustQuantityButton.setNumber(productQuantity);
        }

        fetchProductDetail();
    }

    private void fetchProductDetail() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String productName = Objects
                            .requireNonNull(dataSnapshot.child("productName")
                                    .getValue()).toString();
                    String productDescription = Objects
                            .requireNonNull(dataSnapshot.child("productDescription")
                                    .getValue()).toString();
                    String sellingPrice = Objects
                            .requireNonNull(dataSnapshot.child("sellingPrice")
                                    .getValue()).toString();

                    viewProductName.setText(productName);
                    viewProductDescription.setText(productDescription);
                    viewProductSellingPrice.setText(sellingPrice);

                } else {
                    Snackbar snackBar = Snackbar
                            .make(findViewById(android.R.id.content),
                                    "Error",
                                    Snackbar.LENGTH_INDEFINITE);
                    snackBar.show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addingToCartList() {
        String saveCurrentDate;
        String saveCurrentTime;

        Calendar callForDate = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat currentDate = new SimpleDateFormat("dd MMM, yyyy");
        saveCurrentDate = currentDate.format(callForDate.getTime());

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(callForDate.getTime());

        String quantity = adjustQuantityButton.getNumber();

        DatabaseReference cartListRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Shopping Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("productId", productId);
        cartMap.put("uid", currentUserId);
        cartMap.put("productName", viewProductName.getText().toString());
        cartMap.put("sellingPrice", viewProductSellingPrice.getText().toString());
        cartMap.put("quantity", quantity);
        cartMap.put("date", saveCurrentDate);
        cartMap.put("time", saveCurrentTime);
        cartMap.put("discount", "");

        cartListRef
                .child(currentUserId)
                .child(productId)
                .updateChildren(cartMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        Toast.makeText(this, "Successfully Added to Cart",
                                Toast.LENGTH_SHORT).show();

                        sendUserToMainActivity();
                    } else {
                        Snackbar snackBar = Snackbar
                                .make(findViewById(android.R.id.content),
                                        "Error: " + task.getException(),
                                        Snackbar.LENGTH_LONG);
                        snackBar.show();
                    }
                });
    }

    private void sendUserToMainActivity() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
    }

    private void sendUserToShoppingCart() {
        Intent cartActivityIntent = new Intent(this, CartActivity.class);
        startActivity(cartActivityIntent);
    }
}
