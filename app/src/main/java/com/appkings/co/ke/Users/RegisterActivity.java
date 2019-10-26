package com.appkings.co.ke.Users;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.appkings.co.ke.MainActivity;
import com.appkings.co.ke.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private TextView textViewSignIn;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);

        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        editTextConfirmPassword = findViewById(R.id.edit_text_confirm_password);

        buttonRegister = findViewById(R.id.button_register);

        textViewSignIn = findViewById(R.id.text_view_sign_in);

        //when clicked both button and textView
        buttonRegister.setOnClickListener(v -> CreateNewAccount());
        textViewSignIn.setOnClickListener(v -> sendUserToLoginActivity());
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        //check for current user
        if (currentUser != null) {
            sendUserToMainActivity();
        }
    }


    //method to register in fireBase
    private void CreateNewAccount() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (!email.matches((emailPattern))) {

            editTextEmail.setError("Please input a valid Email");
        }

        //checking if email and password is empty
        if (email.isEmpty()) {

            editTextEmail.setError("Please enter your email");
            return;
        }
        if (!email.matches((emailPattern))) {

            editTextEmail.setError("Please input a valid Email");
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Please enter your password");
            return;
        }
        if (confirmPassword.isEmpty()) {
            editTextPassword.setError("Please confirm your password");
            return;
        }
        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Make sure Passwords match");
            return;
        }
        if (password.length() < 8) {

            editTextPassword.setError("Minimum password length is 8 characters");
            return;
        }
        if (password.length() > 16) {

            editTextPassword.setError("Maximum password length is 16 characters");

        } else {
            //validations okay then we show a progress bar
            progressDialog.setTitle("Registering User...");
            progressDialog.setMessage("Please wait, Creating account...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(true);

            //create account to fireBase now
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            Snackbar snackbar = Snackbar
                                    .make(findViewById(android.R.id.content),
                                            "Registration is successful, \r\n" +
                                                    " Please check your EMAIL and verify your account...",
                                            Snackbar.LENGTH_LONG);
                            snackbar.show();

                            sendEmailVerificationMessage();
                            progressDialog.dismiss();

                            db.collection("Users")
                                    .add(email)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Toast.makeText(RegisterActivity.this, "Success",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            String message = Objects.requireNonNull(task.getException()).getMessage();
                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                                    "An Error Occurred: " + message, Snackbar.LENGTH_LONG);
                            snackbar.show();
                            progressDialog.dismiss();
                        }
                    });
        }
    }


    private void sendEmailVerificationMessage() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(task -> {

                if (task.isSuccessful()) {

                    sendUserToLoginActivity();
                    mAuth.signOut();

                } else {
                    String message = Objects.requireNonNull(task.getException()).getMessage();
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                            "Error: " + message, Snackbar.LENGTH_LONG);
                    snackbar.show();
                    mAuth.signOut();
                }

            });
        }
    }

    //opens login activity
    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }


    //open main activity
    private void sendUserToMainActivity() {
        Intent registerIntent = new Intent(RegisterActivity.this, MainActivity.class);
        finish();
        startActivity(registerIntent);
    }
}