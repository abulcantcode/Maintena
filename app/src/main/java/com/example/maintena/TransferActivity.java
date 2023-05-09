package com.example.maintena;

import static com.example.maintena.Utility.getUserCollection;
import static com.example.maintena.Utility.getVehicleCollectionReference;
import static com.example.maintena.Utility.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.maintena.Model.User;
import com.example.maintena.Model.Vehicle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

public class TransferActivity extends AppCompatActivity {

    Vehicle vehicle;
    String docId;
    Button btnSearch, btnCancel, btnConfirm;
    EditText textEmail;
    TextView textColour, textFuel, textMake, textName, textNumPlate, textNameUser, textUsername;
    Boolean isFound = false, isDealer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        textNumPlate = findViewById(R.id.textNumPlate);
        textName = findViewById(R.id.textName);
        textMake = findViewById(R.id.textMake);
        textColour = findViewById(R.id.textColour);
        textFuel = findViewById(R.id.textFuel);
        textEmail = findViewById(R.id.textEmail);
        btnSearch = findViewById(R.id.btnSearch);
        textNameUser = findViewById(R.id.textNameUser);
        textUsername = findViewById(R.id.textUsername);
        btnCancel = findViewById(R.id.btnCancel);
        btnConfirm = findViewById(R.id.btnConfirm);

        textUsername.setVisibility(View.GONE);

        docId = getIntent().getStringExtra("docId");
        isDealer = getIntent().getBooleanExtra("isDealer", false);

        getVehicleCollectionReference().document(docId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                vehicle = documentSnapshot.toObject(Vehicle.class);
                assert vehicle != null;
                textNumPlate.setText(vehicle.getNumberPlate());
                textName.setText(vehicle.getName());
                textMake.setText(vehicle.getMake());
                textColour.setText(vehicle.getColour());
                textFuel.setText(vehicle.getFuelType());
            }
        });

        btnSearch.setOnClickListener(view -> search());

        btnCancel.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), MaintenanceActivity.class);
            intent.putExtra("docId", docId);
            intent.putExtra("isDealer", isDealer);
            startActivity(intent);
            finish();
        });

        btnConfirm.setOnClickListener(view -> confirm());
    }

    // This method is called when the Transfer button is clicked.
    // It retrieves the ID of the selected maintenance request from the Intent extras
    // Retrieves the ID of the selected dealer from the Spinner
    // And then changes all documents that correspond with the user (owner) to become the ID of the selected user from spinner
    private void confirm() {
        String email = textEmail.getText().toString();
        if (email.isEmpty()) {
            textEmail.setError("Please Enter Email");
            return;
        }

        search();
        if (!isFound) {
            return;
        }

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Setting the vehicle being transfered to pending and setting up the current user as the previous owner
        vehicle.setOwner(textEmail.getText().toString());
        vehicle.setPreviousOwner(firebaseUser.getEmail());
        vehicle.setPending(true);
        getVehicleCollectionReference().document(docId).set(vehicle).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                showToast(getApplicationContext(), "Vehicle records transferred");
                finish();
            }
        });
    }

    // This method uses the email entered in the search box to find the corresponding user from the firebase DB
    private void search() {
        textUsername.setVisibility(View.GONE);
        String email = textEmail.getText().toString();
        if (email.isEmpty()){
            textEmail.setError("Please Enter Email");
            isFound = false;
            return;
        }
        getUserCollection(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                if(user != null){
                    textNameUser.setText(user.getName());
                    if(!user.getDealer()){
                        textUsername.setText(user.getUsername());
                        textUsername.setVisibility(View.VISIBLE);
                    }
                    isFound = true;
                } else {
                    textEmail.setError("Email not found");
                    isFound = false;
                    textUsername.setVisibility(View.GONE);
                    textNameUser.setText("New Owner Name");
                }
            }
        });
    }
}