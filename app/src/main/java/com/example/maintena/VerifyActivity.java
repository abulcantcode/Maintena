package com.example.maintena;

import static com.example.maintena.Utility.getRecordCollectionReference;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.maintena.Model.JobDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

// This class is responsible for handling the UI and dealer interaction for verifying /accepting a verification request.
public class VerifyActivity extends AppCompatActivity {

    String docId;
    JobDetails jobDetails;
    Button btnVerify, btnReject;
    ImageView imgReceipt;
    ImageButton backButton;
    LinearLayout layoutImg;
    TextView textNumPlate, textTitle, textDescription, textPrice, textDate, textMillage, textName, textPostcode, textEmail, textPhone, textAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        // Here, the xml page is being connected to the defined objects
        textNumPlate = findViewById(R.id.textNumPlate);
        textTitle = findViewById(R.id.textTitle);
        textDescription = findViewById(R.id.textDescription);
        textPrice = findViewById(R.id.textPrice);
        textDate = findViewById(R.id.textDate);
        textMillage = findViewById(R.id.textMillage);
        imgReceipt = findViewById(R.id.imgReceipt);
        btnVerify = findViewById(R.id.btnVerify);
        btnReject = findViewById(R.id.btnReject);
        layoutImg = findViewById(R.id.layoutImg);
        textName = findViewById(R.id.textName);
        textPostcode = findViewById(R.id.textPostcode);
        textEmail = findViewById(R.id.textEmail);
        textPhone = findViewById(R.id.textPhone);
        textAddress = findViewById(R.id.textAddress);
        backButton = findViewById(R.id.backButton);

        layoutImg.setVisibility(View.GONE);

        // Gathering information from the previous page
        docId = getIntent().getStringExtra("docId");
        // Gathering information from firestore database and filling out the corresponding text boxes
        getRecordCollectionReference().document(docId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                jobDetails = documentSnapshot.toObject(JobDetails.class);
                assert jobDetails != null;
                textNumPlate.setText(jobDetails.getNumberPlate());
                textTitle.setText(jobDetails.getTitle());
                textDescription.setText(jobDetails.getDescription());
                textMillage.setText(jobDetails.getMillage());
                textPrice.setText(jobDetails.getPrice().toString());
                textDate.setText(jobDetails.getDate().toString());
                textName.setText(jobDetails.getName());
                textPostcode.setText(jobDetails.getPostcode());
                textAddress.setText(jobDetails.getAddress());
                textPhone.setText(jobDetails.getPhone());
                textEmail.setText(jobDetails.getContact());

                if (jobDetails.getImgUrl() != null) {
                    Glide.with(getApplicationContext())
                            .load(jobDetails.getImgUrl())
                            .into(imgReceipt);
                    layoutImg.setVisibility(View.VISIBLE);
                }
            }
        });

        btnVerify.setOnClickListener(view -> verifyJob(docId, jobDetails));
        btnReject.setOnClickListener(view -> rejectJob(docId));
        backButton.setOnClickListener(view -> finish());

    }

    // If the job is rejected, the rejectJob method is called which will send the user to RejectActivity along with the document id for the job
    private void rejectJob(String docId) {
        Intent intent = new Intent(getApplicationContext(), RejectActivity.class);
        intent.putExtra("docId", docId);
        startActivity(intent);
        finish();
    }

    // Upon clicking the verify button, the verifyJob method will be called.
    // This method sets the pending field in JobDetails to false and sets verified field to true.
    // After updating JobDetails object, it saves it onto the database (updating the previous document)
    private void verifyJob(String docId, JobDetails jobDetails) {
        jobDetails.setPending(false);
        jobDetails.setVerified(true);

        DocumentReference documentReference;
        documentReference = getRecordCollectionReference().document(docId);

        documentReference.set(jobDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(getApplicationContext(), "Job Verified Successfully");
                    finish();
                }else{
                    Utility.showToast(getApplicationContext(), "Failed while verifying Job");
                }
            }
        });
    }
}