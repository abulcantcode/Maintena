package com.example.maintena;

import static com.example.maintena.Utility.getRecordCollectionReference;

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

// This method allows for the viewing of saved jobs
public class JobViewActivity extends AppCompatActivity {

    String docId;

    JobDetails jobDetails;

    TextView textComment, textVerify, textEmail, textPostcode, textName, textDate, textPrice, textMillage, textDescription, textTitle, textReject, textNotVerify, textPhone, textAddress;

    LinearLayout layoutImg, scrollContainer, layoutRejection;

    Button btnRequestVerify;

    ImageButton backButton;

    ImageView imgReceipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_view);

        // Here, the xml page is being connected to the defined objects
        textTitle = findViewById(R.id.textTitle);
        textDescription = findViewById(R.id.textDescription);
        textMillage = findViewById(R.id.textMillage);
        textPrice = findViewById(R.id.textPrice);
        textDate = findViewById(R.id.textDate);
        textName = findViewById(R.id.textName);
        textPostcode = findViewById(R.id.textPostcode);
        textAddress = findViewById(R.id.textAddress);
        textEmail = findViewById(R.id.textEmail);
        textPhone = findViewById(R.id.textPhone);
        textVerify = findViewById(R.id.textVerify);
        textReject = findViewById(R.id.textReject);
        textNotVerify = findViewById(R.id.textNotVerify);
        textComment = findViewById(R.id.textComment);
        btnRequestVerify = findViewById(R.id.btnRequestVerify);
        imgReceipt = findViewById(R.id.imgReceipt);
        layoutImg = findViewById(R.id.layoutImg);
        scrollContainer = findViewById(R.id.scrollContainer);
        backButton = findViewById(R.id.backButton);
        layoutRejection = findViewById(R.id.layoutRejection);

        // Setting up the visibility of certain contents
        layoutImg.setVisibility(View.GONE);
        textReject.setVisibility(View.GONE);
        textVerify.setVisibility(View.GONE);
        layoutRejection.setVisibility(View.GONE);
        textComment.setVisibility(View.GONE);
        textNotVerify.setVisibility(View.GONE);

        // Gathering information from the previous page
        docId = getIntent().getStringExtra("docId");

        // This method sets up the components in the corresponding page.
        // it uses the document ID which was passed in from the previous page to retrieve the document and the converts it into JobDetails type
        getRecordCollectionReference().document(docId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                jobDetails = documentSnapshot.toObject(JobDetails.class);
                assert jobDetails != null;
                textTitle.setText(jobDetails.getTitle());
                textDescription.setText(jobDetails.getDescription());
                textMillage.setText(jobDetails.getMillage());
                textPrice.setText(jobDetails.getPrice().toString());
                textDate.setText(jobDetails.getDate());
                textName.setText(jobDetails.getName());
                textPostcode.setText(jobDetails.getPostcode());
                textAddress.setText(jobDetails.getAddress());
                textPhone.setText(jobDetails.getPhone());

                if (jobDetails.getContact() != null){
                    textEmail.setText(jobDetails.getContact());
                } else {
                    hideButtonVerify();
                }

                setVerify(jobDetails.getVerified(), jobDetails.getRequested(), jobDetails.getComment(), jobDetails.getContact());

                if (jobDetails.getImgUrl() != null) {
                    Glide.with(getApplicationContext())
                            .load(jobDetails.getImgUrl())
                            .into(imgReceipt);
                    layoutImg.setVisibility(View.VISIBLE);
                }
            }
        });
        backButton.setOnClickListener(view -> finish());
        btnRequestVerify.setOnClickListener(view -> sendVerifyReq(docId, jobDetails));
    }

    // this method is used to determine which icon is shown based on values within JobDetails
    private void setVerify(Boolean verified, Boolean requested, String comment, String contact){

        if (requested || contact==null || contact.isEmpty()){
            hideButtonVerify();
        }

        if (verified){
            textVerify.setVisibility(View.VISIBLE);
        } else {
            if (comment!=null && !comment.isEmpty()) {
                textReject.setVisibility(View.VISIBLE);
                layoutRejection.setVisibility(View.VISIBLE);
                textComment.setVisibility(View.VISIBLE);
                textComment.setText(comment);
            } else {
                textNotVerify.setVisibility(View.VISIBLE);
            }
        }
    }

    // handles the hiding of btnRequestVerify and resizing of the scroll view
    private void hideButtonVerify() {
        btnRequestVerify.setVisibility(View.GONE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(scrollContainer.getLayoutParams());
        layoutParams.setMargins(0,0,0,0);
        scrollContainer.setLayoutParams(layoutParams);
    }

    // this method is called when the RequestVerify button is clicked and then changes the pending field to true for the corresponding document in firestore db
    private void sendVerifyReq(String docId, JobDetails jobDetails) {

        DocumentReference documentReference;
        documentReference = getRecordCollectionReference().document(docId);

        jobDetails.setRequested(true);
        jobDetails.setPending(true);

        documentReference.set(jobDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(getApplicationContext(), "Verification Requested");
                    finish();
                }else{
                    Utility.showToast(getApplicationContext(), "Request Failed");
                }
            }
        });
    }
}