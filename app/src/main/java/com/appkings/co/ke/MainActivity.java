package com.appkings.co.ke;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.appkings.co.ke.Users.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private TextView signOut;

    /**
     * Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content),
     * "You need to update your profile", Snackbar.LENGTH_LONG);
     * snackBar.show();
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if (auth == null) {
            sendUserToLoginActivity();
        }

        signOut = findViewById(R.id.helloWorld);
        signOut.setOnClickListener(view -> {
            mAuth.signOut();
            sendUserToLoginActivity();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser auth = FirebaseAuth.getInstance().getCurrentUser();
        if (auth == null) {
            sendUserToLoginActivity();
        }
    }


    private void sendUserToLoginActivity() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        finish();
        startActivity(loginIntent);
    }
}
