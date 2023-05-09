package com.example.maintena;

import static com.example.maintena.Utility.getUserCollection;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maintena.Adapter.VehicleAdapter;
import com.example.maintena.Model.User;
import com.example.maintena.Model.Vehicle;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

// This class is responsible for dealing with the logic behind the activity_main (my garage) page.
// This page will retrieve all vehicles associated with the users email id.
public class GarageActivity extends AppCompatActivity {

    FloatingActionButton btnAddCar;
    RecyclerView recyclerView;
    ImageButton menuButton, backButton;
    VehicleAdapter vehicleAdapter;
    Boolean isDealer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garage);

        btnAddCar = findViewById(R.id.floatingAddCar);
        recyclerView = findViewById(R.id.recycler_view);
        menuButton = findViewById(R.id.imageButton);
        backButton = findViewById(R.id.backButton);

        backButton.setVisibility(View.GONE);

        isDealer = getIntent().getBooleanExtra("isDealer", false);

        if (isDealer){
            backButton.setVisibility(View.VISIBLE);
        } else {
            getUserCollection(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);
                    assert user != null;
                    if(user.getDealer()){
                        backButton.setVisibility(View.VISIBLE);
                        isDealer = true;
                    }
                }
            });
        }

        backButton.setOnClickListener(view -> finish());

        btnAddCar.setOnClickListener(view -> {
                Intent intent = new Intent(getApplicationContext(), VehicleDetailsActivity.class);
                intent.putExtra("isDealer", isDealer);
                startActivity(intent);
            });

        menuButton.setOnClickListener(view -> showMenu());

        setUpRecyclerView();
    }

    // This method is responsible for setting up the popUp Menu where it allows the user to log out.
    private void showMenu() {
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), menuButton);
        popupMenu.getMenu().add("Logout");
        popupMenu.getMenu().add("Pending Transfers");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getTitle()=="Logout"){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                    return true;
                }else if(menuItem.getTitle()=="Pending Transfers"){
                    startActivity(new Intent(getApplicationContext(), PendingVehiclesActivity.class));
                    return true;
                }
                return false;
            }
        });

    }

    // This methods purpose is to retrieve vehicles which are associated with the users email ( stored in vehicles as 'owner' )
    // This method then sends the group of documents it retrieved to the vehicle adapter which generated cards to populate the layout.
    private void setUpRecyclerView() {
        Query query = Utility.getVehicleCollectionReference()
                .whereEqualTo("owner", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .whereEqualTo("pending", false);
        FirestoreRecyclerOptions<Vehicle> options = new FirestoreRecyclerOptions.Builder<Vehicle>()
                .setQuery(query, Vehicle.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        vehicleAdapter = new VehicleAdapter(options, this, isDealer);
        recyclerView.setAdapter(vehicleAdapter);
    }

    // These three methods are responsible for updating the recycler view upon any changes to the database
    @Override
    protected void onStart() {
        super.onStart();
        vehicleAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        vehicleAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        vehicleAdapter.notifyDataSetChanged();
    }
}