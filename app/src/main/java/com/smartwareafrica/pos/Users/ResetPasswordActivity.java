package com.smartwareafrica.pos.Users;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.smartwareafrica.pos.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ResetPasswordActivity extends AppCompatActivity {

    private Button resetPasswordSendEmailButton;
    private EditText resetEmailInput;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        resetPasswordSendEmailButton = findViewById(R.id.button_send_email);
        resetEmailInput = findViewById(R.id.reset_pwd_email);

        resetPasswordSendEmailButton.setOnClickListener(v -> {
            String userEmail = resetEmailInput.getText().toString();

            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

            if (userEmail.isEmpty()) {

                resetEmailInput.setError("Email is required");

            }
            if (!userEmail.matches((emailPattern))) {

                resetEmailInput.setError("Please input a valid Email");
            } else {
                mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Snackbar snackbar = Snackbar
                                .make(findViewById(android.R.id.content),
                                        "Email sent. Please check your mailbox",
                                        Snackbar.LENGTH_LONG);
                        snackbar.show();
                        sendUserToLoginActivity();

                    } else {
                        String message = Objects
                                .requireNonNull(task.getException())
                                .getMessage();
                        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                                "An error occurred: " + message, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                });
            }
        });

    }

    //open login activity
    private void sendUserToLoginActivity() {
        Intent mainActivityIntent = new
                Intent(ResetPasswordActivity.this, LoginActivity.class);
        finish();
        startActivity(mainActivityIntent);
    }
}
