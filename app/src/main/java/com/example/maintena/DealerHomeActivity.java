package com.example.maintena;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

// This class is responsible for handling dealers main UI and user interaction.
// This page allows dealers to navigate to the dealer specific pages such as recordsActivity and Pending Activity
public class DealerHomeActivity extends AppCompatActivity {

    Button btnPending, btnAllRecs, btnMyGarage;

    ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dealer_home);

        btnPending = findViewById(R.id.btnPending);
        btnAllRecs = findViewById(R.id.btnAllRecs);
        btnMyGarage = findViewById(R.id.btnMyGarage);
        imageButton = findViewById(R.id.imageButton);

        imageButton.setOnClickListener(view -> showMenu());

        btnMyGarage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GarageActivity.class);
                intent.putExtra("isDealer", true);
                startActivity(intent);
            }
        });

        // navigation for recordsActivity
        btnAllRecs.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), RecordsActivity.class)));
        // navigation for pendingActivity
        btnPending.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), PendingActivity.class)));

    }

    // This method displays a pop up menu which allows the user to log out.
    private void showMenu() {
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), imageButton);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getTitle()=="Logout"){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });

    }
}