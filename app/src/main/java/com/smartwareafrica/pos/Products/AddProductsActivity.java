package com.smartwareafrica.pos.Products;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.smartwareafrica.pos.R;
import com.smartwareafrica.pos.Users.LoginActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class AddProductsActivity extends AppCompatActivity {
    private EditText editTextProductName;
    private EditText editTextBuyingPrice;
    private EditText editTextSellingPrice;
    private EditText editTextDescription;

    private Button buttonAdd;

    String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference productsRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        productsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        if (mAuth.getCurrentUser() == null) {
            sendUserToLoginActivity();
        }

        editTextProductName = findViewById(R.id.editTextProductName);
        editTextBuyingPrice = findViewById(R.id.editTextBuyingPrice);
        editTextSellingPrice = findViewById(R.id.editTextSellingPrice);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(view -> addProduct());
    }

    private void addProduct() {
        final String uniqueKey = productsRef.push().getKey();


        String productName = editTextProductName.getText().toString().trim();
        String buyingPrice = editTextBuyingPrice.getText().toString().trim();
        String sellingPrice = editTextSellingPrice.getText().toString().trim();
        String productDescription = editTextDescription.getText().toString().trim();

        if (productName.isEmpty()) {
            editTextProductName.setError("Please enter product name");
        } else if (buyingPrice.isEmpty()) {
            editTextBuyingPrice.setError("Add a buying price");
        } else if (sellingPrice.isEmpty()) {
            editTextSellingPrice.setError("AddSellingPrice");
        } else if (productDescription.isEmpty()) {
            editTextDescription.setError("Please describe product");
        } else {
            final HashMap<String, Object> productsMap = new HashMap<>();
            productsMap.put("productName", productName);
            productsMap.put("buyingPrice", buyingPrice);
            productsMap.put("sellingPrice", sellingPrice);
            productsMap.put("productDescription", productDescription);
            productsMap.put("userId", currentUserId);
            productsRef.child(uniqueKey).updateChildren(productsMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //Restart Activity
                            finish();
                           Intent starterIntent = getIntent();
                           startActivity(starterIntent);
                        } else {
                            String message = Objects
                                    .requireNonNull(task.getException())
                                    .getMessage();
                            Snackbar snackbar = Snackbar
                                            .make(findViewById(android.R.id.content),
                                                    "Error,please try again. Error: "
                                                            + message, Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    });

        }
    }

    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
    }
}
