package com.example.maintena;

import static com.example.maintena.Utility.getRecordCollectionReference;
import static com.example.maintena.Utility.getUserCollection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.maintena.Model.JobDetails;
import com.example.maintena.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

// This class is responsible for handling the UI and dealer interaction for rejecting a verification request.
public class RejectActivity extends AppCompatActivity {
    String docId, comment;
    JobDetails jobDetails;
    Button btnConfirm, btnCancel;
    TextView textNumPlate, textTitle, textDescription, textPrice, textDate, textDName, textMillage, textPostcode, textAddress, textPhone, textEmail, textName;
    EditText editComment;

    LinearLayout layoutImg;
    ImageView imgReceipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reject);

        // Here, the xml page is being connected to the defined objects
        textNumPlate = findViewById(R.id.textNumPlate);
        textTitle = findViewById(R.id.textJob);
        textDescription = findViewById(R.id.textDescription);
        textMillage = findViewById(R.id.textMillage);
        textPrice = findViewById(R.id.textPrice);
        textDate = findViewById(R.id.textDate);
        textDName = findViewById(R.id.textDName);
        textPostcode = findViewById(R.id.textPostcode);
        textAddress = findViewById(R.id.textAddress);
        textPhone = findViewById(R.id.textPhone);
        textEmail = findViewById(R.id.textEmail);
        textName = findViewById(R.id.textName);
        editComment = findViewById(R.id.editComment);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnCancel = findViewById(R.id.btnCancel);
        layoutImg = findViewById(R.id.layoutImg);
        imgReceipt = findViewById(R.id.imgReceipt);

        layoutImg.setVisibility(View.GONE);


        getUserCollection(FirebaseAuth.getInstance().getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                assert user != null;
                textDName.setText(user.getName());
            }
        });


        docId = getIntent().getStringExtra("docId");

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


        btnConfirm.setOnClickListener(view -> validateAndConfirm(docId, jobDetails));
        btnCancel.setOnClickListener(view -> cancel(docId));

    }

    private void cancel(String docId) {
        Intent intent = new Intent(getApplicationContext(), VerifyActivity.class);
        intent.putExtra("docId", docId);
        startActivity(intent);
        finish();
    }

    private void validateAndConfirm(String docId, JobDetails jobDetails){
        comment = String.valueOf(editComment.getText());

        if(!validate(comment)){
            return;
        }

        confirm(docId, jobDetails, comment);
    }

    private Boolean validate(String comment){
        if(comment.isEmpty()){
            editComment.setError("Comment cannot be empty");
            return false;
        }
        return true;
    }

    // Upon clicking the confirm button, the confirm method will be called.
    // This method sets the pending field in JobDetails to false and sets the comment.
    // After updating JobDetails object, it saves it onto the database (updating the previous document)
    private void confirm(String docId, JobDetails jobDetails, String comment) {
        jobDetails.setPending(false);
        jobDetails.setComment(comment);

        DocumentReference documentReference;
        documentReference = getRecordCollectionReference().document(docId);

        documentReference.set(jobDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(getApplicationContext(), "Job Rejected Successfully");
                    finish();
                }else{
                    Utility.showToast(getApplicationContext(), "Failed while rejecting Job");
                }
            }
        });
    }
}