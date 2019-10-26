package com.appkings.co.ke.Users;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.appkings.co.ke.MainActivity;
import com.appkings.co.ke.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewRegister;
    private TextView textViewResetPassword;

    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    private boolean emailAddressChecker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.login_email);
        editTextPassword = findViewById(R.id.login_password);
        buttonSignIn = findViewById(R.id.button_sign_in);
        textViewRegister = findViewById(R.id.text_view_register);
        textViewResetPassword = findViewById(R.id.text_view_forgot_password);

        progressDialog = new ProgressDialog(this);

        buttonSignIn.setOnClickListener(v -> allowUserToLogin());
        textViewRegister.setOnClickListener(v -> sendUserToRegisterActivity());
        textViewResetPassword.setOnClickListener(v -> sendUserToResetPasswordActivity());

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

    private void allowUserToLogin() {

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

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
        if (password.length() < 8) {

            editTextPassword.setError("Minimum password length is 8 characters");
            return;
        }
        if (password.length() > 16) {

            editTextPassword.setError("Maximum password length is 16 characters");

        } else {
            //validations okay we show a progress bar
            progressDialog.setTitle("Logging in User...");
            progressDialog.setMessage("Logging in, Please wait...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {

                            VerifyEmailAddress();
                            progressDialog.dismiss();
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


    //email verification checker
    private void VerifyEmailAddress() {

        FirebaseUser user = mAuth.getCurrentUser();
        emailAddressChecker = Objects.requireNonNull(user).isEmailVerified();

        if (emailAddressChecker) {

            sendUserToMainActivity();

        } else {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                    "Please verify your email address first", Snackbar.LENGTH_LONG);
            snackbar.show();
            mAuth.signOut();
        }

    }

    //open login activity
    private void sendUserToRegisterActivity() {
        Intent registerIntent = new
                Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);

    }

    //open main activity
    private void sendUserToMainActivity() {
        Intent mainActivityIntent = new
                Intent(LoginActivity.this, MainActivity.class);
        finish();
        startActivity(mainActivityIntent);
    }

    private void sendUserToResetPasswordActivity() {
        Intent resetPasswordIntent = new
                Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(resetPasswordIntent);
    }
}
