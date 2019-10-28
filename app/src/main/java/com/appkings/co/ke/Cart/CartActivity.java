package com.appkings.co.ke.Cart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.collection.LruCache;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appkings.co.ke.GenerateReceiptActivity;
import com.appkings.co.ke.Products.ProductsModelClass;
import com.appkings.co.ke.Products.ViewProductActivity;
import com.appkings.co.ke.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.Index;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartItemsList;
    private RecyclerView.LayoutManager layoutManager;
    private AppCompatButton buttonConfirmPurchase;
    private TextView textViewTotalPrice;
    private ImageView imageEmptyCart;

    private int overallTotalPrice = 0;

    String currentUserId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        buttonConfirmPurchase = findViewById(R.id.buttonConfirmPurchase);
        buttonConfirmPurchase.setOnClickListener(view -> sendUserToGenerateReceipt());
        textViewTotalPrice = findViewById(R.id.textViewTotalPrice);
        imageEmptyCart = findViewById(R.id.imageEmptyCart);

        cartItemsList = findViewById(R.id.cartItemsList);
        cartItemsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        cartItemsList.setLayoutManager(layoutManager);
    }


    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference cartListRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Shopping Cart List");
        FirebaseRecyclerOptions<CartModelClass> options = new
                FirebaseRecyclerOptions.Builder<CartModelClass>()
                .setQuery(cartListRef.child(currentUserId), CartModelClass.class)
                .build();

        //check if rv is empty
        cartListRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {
                    buttonConfirmPurchase.setVisibility(View.GONE);
                    textViewTotalPrice.setVisibility(View.GONE);
                    imageEmptyCart.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(CartActivity.this, "An Error occurred: "
                        + databaseError, Toast.LENGTH_SHORT).show();

            }
        });

        FirebaseRecyclerAdapter<CartModelClass, CartViewHolder> adapter = new
                FirebaseRecyclerAdapter<CartModelClass, CartViewHolder>(options) {
                    @SuppressLint("SetTextI18n")
                    @Override
                    protected void onBindViewHolder
                            (@NonNull CartViewHolder holder, int position,
                             @NonNull CartModelClass model) {

                        //Calculate totals
                        int singleProductTotalPrice = ((Integer.valueOf(model.getSellingPrice())))
                                * Integer.valueOf(model.getQuantity());
                        overallTotalPrice = overallTotalPrice + singleProductTotalPrice;
                        final  String overallPrice = "Total Amount: " + overallTotalPrice;
                        textViewTotalPrice.setText(overallPrice);

                        String quantity = "Quantity: " + model.getQuantity();
                        String name = "Item: \n " + model.getProductName();
                        String price = "Price = Ksh." + model.getSellingPrice();

                        holder.cartProductQuantity.setText(quantity);
                        holder.cartProductName.setText(name);
                        holder.cartProductPrice
                                .setText(price
                                        + "(*"
                                        + model.getQuantity()
                                        + "="
                                        + singleProductTotalPrice
                                        + ")");

                        holder.itemView.setOnClickListener(view -> {
                            CharSequence[] options = new CharSequence[]{
                                    "Edit",
                                    "Remove"
                            };
                            AlertDialog.Builder builder = new
                                    AlertDialog.Builder(CartActivity.this);
                            builder.setTitle("Edit cart items: ");
                            builder.setItems(options, (dialogInterface, i) -> {
                                if (i == 0) {

                                    Intent viewProductIntent = new
                                            Intent(CartActivity.this,
                                            ViewProductActivity.class);
                                    viewProductIntent
                                            .putExtra("productId", model.getProductId());
                                    startActivity(viewProductIntent);
                                } else if (i == 1) {

                                    cartListRef.child(currentUserId)
                                            .child(model.getProductId())
                                            .removeValue()
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(CartActivity.this,
                                                            "Item removed Successfully",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                    Intent restartActivityIntent = getIntent();
                                    finish();
                                    startActivity(restartActivityIntent);
                                }
                            });
                            builder.show();
                        });
                    }

                    @NonNull
                    @Override
                    public CartViewHolder
                    onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater
                                .from(parent.getContext())
                                .inflate(R.layout.cart_tems_layout, parent, false);
                        return new CartViewHolder(view);
                    }
                };

        cartItemsList.setAdapter(adapter);
        adapter.startListening();
    }
    private void sendUserToGenerateReceipt() {
        Intent generateReceiptIntent = new Intent(this, GenerateReceiptActivity.class);
        generateReceiptIntent.putExtra("uid", currentUserId);
        startActivity(generateReceiptIntent);
    }
}
