package com.example.maintena;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maintena.Adapter.TransferAdapter;
import com.example.maintena.Model.Vehicle;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

public class PendingVehiclesActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    ImageButton backButton;

    TransferAdapter transferAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_vehicles);

        // Here, the xml page is being connected to the defined objects
        recyclerView = findViewById(R.id.recycler_view);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(view -> finish());
        setUpRecyclerView();
    }


    // This methods purpose is to retrieve jobs which are associated with the dealers email and if the "pending" field is set to true
    // This method then sends the group of documents it retrieved to the PendingAdapter which generated cards to populate the layout.
    private void setUpRecyclerView() {
        Query query = Utility.getVehicleCollectionReference()
                .whereEqualTo("owner", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .whereEqualTo("pending", true);
        FirestoreRecyclerOptions<Vehicle> vehicles = new FirestoreRecyclerOptions.Builder<Vehicle>()
                .setQuery(query, Vehicle.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        transferAdapter = new TransferAdapter(vehicles, this);
        recyclerView.setAdapter(transferAdapter);
    }

    // These three methods are responsible for updating the recycler view upon any changes to the database
    @Override
    protected void onStart() {
        super.onStart();
        transferAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        transferAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        transferAdapter.notifyDataSetChanged();
    }
}