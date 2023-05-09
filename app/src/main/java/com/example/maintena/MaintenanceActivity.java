package com.example.maintena;

import static com.example.maintena.Utility.getVehicleCollectionReference;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maintena.Adapter.JobAdapter;
import com.example.maintena.Model.JobDetails;
import com.example.maintena.Model.Vehicle;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

// This class is responsible for dealing with the logic behind the activity_maintenance page.
// This page will retrieve all jobs associated with the vehicle which was clicked on to enter this page. (from vehicle adapter)
public class MaintenanceActivity extends AppCompatActivity {

    TextView txtTitle, txtNumPlate;

    RecyclerView recyclerView;

    ImageButton menuButton, backButton;

    Vehicle vehicle = new Vehicle();

    FloatingActionButton btnAddJob;

    String docId;

    Boolean isDealer;

    String baseUrl = "https://www.check-mot.service.gov.uk/results?registration=";

    JobAdapter jobAdapter;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);

        btnAddJob = findViewById(R.id.floatingAddJob);
        recyclerView = findViewById(R.id.recycler_view);
        menuButton = findViewById(R.id.imageButton);
        backButton = findViewById(R.id.backButton);
        txtTitle = findViewById(R.id.txtTitle);
        txtNumPlate = findViewById(R.id.txtNumPlate);

        //receive data
        docId = getIntent().getStringExtra("docId");
        isDealer = getIntent().getBooleanExtra("isDealer", false);


        if(docId!=null && !docId.isEmpty()){
            getVehicleCollectionReference().document(docId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    vehicle = documentSnapshot.toObject(Vehicle.class);
                    assert vehicle != null;

                    txtTitle.setText(vehicle.getName());
                    txtNumPlate.setText(vehicle.getNumberPlate());
                }
            });
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        backButton.setOnClickListener(view -> finish());

        btnAddJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), JobDetailsActivity.class);
                intent.putExtra("numPlate", vehicle.getNumberPlate());
                intent.putExtra("isDealer", isDealer);
                intent.putExtra("docId", docId);
                startActivity(intent);
                finish();
            }
        });

        menuButton.setOnClickListener(view -> showMenu(this));

        setUpRecyclerView();
    }

    // This function sets up the popup menu when clicking the Menu icon
    private void showMenu(Context context) {
        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), menuButton);
        // adding the drop down options
        popupMenu.getMenu().add("View MOT History");
        popupMenu.getMenu().add("Edit Vehicle Details");
        popupMenu.getMenu().add("Transfer Vehicle");
        popupMenu.getMenu().add("Delete Vehicle");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // This is the actions taking place when Edit Vehicle Details is clicked
                if(menuItem.getTitle()=="Edit Vehicle Details"){
                    Intent intent = new Intent(getApplicationContext(), VehicleDetailsActivity.class);
                    intent.putExtra("isDealer", isDealer);
                    intent.putExtra("docId", docId);
                    startActivity(intent);
                    finish();
                } else if (menuItem.getTitle()=="Delete Vehicle") {
                    // This is the actions taking place when Delete Vehicle is clicked
                    // Alert Dialogue Box
                    // https://developer.android.com/reference/android/app/AlertDialog.Builder
                    // setting up a dialogue box
                    new AlertDialog.Builder(context)
                            .setMessage("Are you sure you want to delete")
                            .setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //method for deleting vehicle from firebase DB
                                    deleteVehicleFromFirebase();
                                    finish();
                                }
                            }).setNegativeButton("No", null)
                            .show();

                } else if (menuItem.getTitle()=="Transfer Vehicle"){
                    Intent intent = new Intent(getApplicationContext(), TransferActivity.class);
                    intent.putExtra("docId", docId);
                    intent.putExtra("isDealer", isDealer);
                    startActivity(intent);
                    finish();
                } else if (menuItem.getTitle()=="View MOT History"){
                    String url = baseUrl + vehicle.getNumberPlate();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
                return false;
            }
        });

    }

    // this method uses the document id that was sent in, to delete it from the database
    private void deleteVehicleFromFirebase() {
        DocumentReference documentReference;
        documentReference = Utility.getVehicleCollectionReference().document(docId);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(getApplicationContext(), "Vehicle deleted");
                    finish();
                } else {
                    Utility.showToast(getApplicationContext(), "Failed to delete vehicle");
                }
            }
        });
    }

    // This methods purpose is to retrieve jobs which are associated with the users email and registration number
    // This method then sends the group of documents it retrieved to the JobAdapter which generated cards to populate the layout.
    private void setUpRecyclerView() {
        CollectionReference collectionReference = Utility.getRecordCollectionReference();
        Query query = collectionReference.whereEqualTo("owner", firebaseUser.getEmail()).whereEqualTo("vehicleID", docId);
        FirestoreRecyclerOptions<JobDetails> jobs = new FirestoreRecyclerOptions.Builder<JobDetails>()
                .setQuery(query, JobDetails.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobAdapter = new JobAdapter(jobs, this);
        recyclerView.setAdapter(jobAdapter);
    }


    // These three methods are responsible for updating the recycler view upon any changes to the database
    @Override
    protected void onStart() {
        super.onStart();
        jobAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        jobAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        jobAdapter.notifyDataSetChanged();
    }
}