package com.example.maintena;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maintena.Adapter.PendingAdapter;
import com.example.maintena.Model.JobDetails;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

// This class is responsible for dealing with the logic behind the activity_records page.
// This page will retrieve all jobs associated with the dealer email
public class RecordsActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    ImageButton backButton;

    PendingAdapter pendingAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        recyclerView = findViewById(R.id.recycler_view);
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(view -> finish());

        setUpRecyclerView();
    }

    // This methods purpose is to retrieve all jobs which are associated with the dealers email
    // This method then sends the group of documents it retrieved to the PendingAdapter which generated cards to populate the layout.
    private void setUpRecyclerView() {
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        CollectionReference collectionReference = Utility.getRecordCollectionReference();
        Query query = collectionReference.whereEqualTo("contact", email).whereEqualTo("requested", true);
        FirestoreRecyclerOptions<JobDetails> jobs = new FirestoreRecyclerOptions.Builder<JobDetails>()
                .setQuery(query, JobDetails.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        pendingAdapter = new PendingAdapter(jobs, this);
        recyclerView.setAdapter(pendingAdapter);
    }

    // These three methods are responsible for updating the recycler view upon any changes to the database
    @Override
    protected void onStart() {
        super.onStart();
        pendingAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        pendingAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        pendingAdapter.notifyDataSetChanged();
    }
}