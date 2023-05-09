package com.example.maintena;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    Button btnPrivate, btnDealer;
    TextView txtSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Here, the xml page is being connected to the defined objects
        btnPrivate = findViewById(R.id.btnPrivate);
        btnDealer = findViewById(R.id.btnDealer);
        txtSignIn = findViewById(R.id.txtSignIn);


        // Setting up navigation
        btnPrivate.setOnClickListener(view -> goToPrivate());

        btnDealer.setOnClickListener(view -> goToDealer());

        txtSignIn.setOnClickListener(view -> goToSignIn());
    }

    // this method allows the user to navigate to the sign in page
    private void goToSignIn() {
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        finish();
    }

    // this method allows the user to navigate to the Dealer sign up page
    private void goToDealer() {
        startActivity(new Intent(getApplicationContext(), CreateDealerActivity.class));
        finish();
    }

    // this method allows the user to navigate to the Private user sign in page
    private void goToPrivate() {
        startActivity(new Intent(getApplicationContext(), CreatePrivateActivity.class));
        finish();
    }
}