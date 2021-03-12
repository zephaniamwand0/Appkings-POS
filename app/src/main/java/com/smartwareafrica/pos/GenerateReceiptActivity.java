package com.smartwareafrica.pos;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.smartwareafrica.pos.Cart.CartActivity;
import com.smartwareafrica.pos.Cart.CartModelClass;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class GenerateReceiptActivity extends AppCompatActivity {
    private int STORAGE_CODE = 1000;

    String currentUserId;
    String uid;

    private AppCompatButton buttonGenerateReceipt;
    private AppCompatButton buttonHome;
    private TextView viewResult;

    private FirebaseAuth mAuth;

    private int overallTotalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_receipt);

        buttonGenerateReceipt = findViewById(R.id.buttonGenerateReceipt);
        buttonGenerateReceipt.setOnClickListener(view -> generateReceipt());

        viewResult = findViewById(R.id.viewResult);
        buttonHome = findViewById(R.id.buttonHome);
        buttonHome.setOnClickListener(view -> sendUserToMainActivity());

        uid = getIntent().getStringExtra("uid");

        mAuth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

    }

    private void generateReceipt() {

        final DatabaseReference cartListRef = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Shopping Cart List");

        //check if rv is empty
        cartListRef.child(uid).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<CartModelClass> list = new ArrayList<>();

                for (DataSnapshot dataValues : dataSnapshot.getChildren()) {
                    CartModelClass cartModelClass = dataValues.getValue(CartModelClass.class);
                    list.add(cartModelClass);


                    //Runtime permissions
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                        //OS >= Marshmallow
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                PackageManager.PERMISSION_DENIED) {
                            //Permission not granted: Request for it
                            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permissions, STORAGE_CODE);
                        }
                    }

                    //Create object for document class
                    Document receipt = new Document();
                    //File name
                    String receiptName = "Receipt_"
                            + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                            .format(System.currentTimeMillis());
                    //File path
                    String receiptFilePath = Environment
                            .getExternalStorageDirectory()
                            + "/download/"
                            + receiptName
                            + ".pdf";

                    try {
                        //Instance of pdf writer
                        PdfWriter.getInstance(receipt, new FileOutputStream(receiptFilePath));

                        //Open document to write
                        receipt.open();

                        receipt.add(title());
                        PdfPTable table = new PdfPTable(4);

                        // header row:
                        table.addCell("Item Name");
                        table.addCell("Price");
                        table.addCell("Quantity");
                        table.addCell("Total");

                        table.setHeaderRows(2);
                        table.setFooterRows(1);

                        // Here's how you adding the values
                        for (int i = 0; i < list.size(); i++) {

                            String temp1 = list.get(i).getProductName();
                            String temp2 = list.get(i).getSellingPrice();
                            String temp3 = list.get(i).getQuantity();
                            String price = temp2;
                            int productPrice = Integer.parseInt(price);
                            int productQuantity = Integer.parseInt(temp3);
                            int product = productPrice * productQuantity;
                            String temp4 = Integer.toString(product);

                            overallTotalPrice += product;

                            if (temp1.equalsIgnoreCase("")) {
                                temp1 = "*"; // this fills the cell with * if the String is empty otherwise cell won't be created
                            }

                            if (temp2.equalsIgnoreCase("")) {
                                temp2 = "*"; // this fills the cell with *
                            }

                            if (temp3.equalsIgnoreCase("")) {
                                temp3 = "*"; // this fills the cell with *
                            }
                            if (temp4.equalsIgnoreCase("")) {
                                temp4 = "*"; // this fills the cell with *
                            }

                            table.addCell(temp1); // rows for first column
                            table.addCell(temp2); // rows for seconds column
                            table.addCell(temp3); // rows for third column
                            table.addCell(temp4); // rows for fourth column

                            table.setKeepTogether(true);
                            receipt.add(table);
                        }

                        SharedPreferences spp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                        int myIntValue = spp.getInt("your_int_key", -1);

                        Paragraph total = new Paragraph("Total Amount = " + myIntValue);
                        total.setAlignment(Element.ALIGN_CENTER);
                        receipt.add(total);

                        //Close receipt
                        receipt.close();

                        String path = receiptFilePath.replace(Environment
                                .getExternalStorageDirectory() + "/", "");

                        String st = "Receipt generated and is saved to: \n"
                                + path;

                        viewResult.setText(st);
                        viewResult.setVisibility(View.VISIBLE);
                        buttonGenerateReceipt.setEnabled(false);
                        buttonGenerateReceipt.setBackgroundColor(Color.GRAY);

                    } catch (Exception e) {
                        //For error on generating
                        Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content),
                                "ERROR: " + e, Snackbar.LENGTH_INDEFINITE);
                        snackBar.show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content),
                        "An error occurred: " + databaseError, Snackbar.LENGTH_LONG);
                snackBar.show();
            }
        });

        cartListRef.child(currentUserId)
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(GenerateReceiptActivity.this,
                                "Checkout successful",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        String e = Objects.requireNonNull(task.getException()).toString();
                        Toast.makeText(this, "Failed to checkout: "
                                + e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendUserToMainActivity() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        finish();
        startActivity(mainActivityIntent);
    }

    public static Paragraph title() {
        Font fontBold = FontFactory.getFont("Times-Roman", 14, Font.BOLD);
        Paragraph p = new Paragraph("Purchase details", fontBold);
        p.setSpacingAfter(20);
        p.setAlignment(1); // Center
        return p;
    }

    @Override
    public void onRequestPermissionsResult
            (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permission granted from popup
                //   generatePdf();
            } else {
                //Permission denied from popup show error message
                Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content),
                        "Permission denied", Snackbar.LENGTH_LONG);
                snackBar.show();

            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent cartActivityIntent = new Intent(this, CartActivity.class);
        finish();
        startActivity(cartActivityIntent);
    }

}
