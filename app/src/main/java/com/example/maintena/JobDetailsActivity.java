package com.example.maintena;

import static com.example.maintena.Utility.getDealerCollection;
import static com.example.maintena.Utility.getRecordCollectionReference;
import static com.example.maintena.Utility.showToast;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.maintena.Model.Dealer;
import com.example.maintena.Model.JobDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

// This class is responsible for allowing users to save jobs / maintenance records
public class JobDetailsActivity extends AppCompatActivity {

    Button uploadReceipt, btnAddJob, btnAutoAdd, btnManualEnter, btnSearch, btnDatePicker;

    DatePickerDialog datePickerDialog;

    ImageView imgReceipt;

    ImageButton backButton;

    EditText textDescription, textMillage, textPrice, textName, textAddress, textPostcode, textEmail, autoEmail, textPhone;

    TextView autoAddress, autoPostcode, autoName, autoPhone;

    LinearLayout manualEnter, autoFind;

    AutoCompleteTextView dropDownJobList;

    private Uri imagePath;

    String numPlate;

    Boolean imageUploaded = false, isAutoFind = true, isDealer = false;

    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        uploadReceipt = findViewById(R.id.uploadReceipt);
        imgReceipt = findViewById(R.id.imgReceipt);
        textDescription = findViewById(R.id.textDescription);
        textMillage = findViewById(R.id.textMillage);
        textPrice = findViewById(R.id.textPrice);
        btnDatePicker = findViewById(R.id.datePicker);
        textName = findViewById(R.id.editName);
        textAddress = findViewById(R.id.editAddress);
        textPostcode = findViewById(R.id.editPostcode);
        textEmail = findViewById(R.id.editEmail);
        textPhone = findViewById(R.id.editPhone);
        btnAddJob = findViewById(R.id.btnAddJob);
        btnAutoAdd = findViewById(R.id.btnAutoFind);
        btnManualEnter = findViewById(R.id.btnManualEnter);
        autoFind = findViewById(R.id.autoFind);
        manualEnter = findViewById(R.id.manualEnter);
        autoPostcode = findViewById(R.id.textPostcode);
        autoAddress = findViewById(R.id.textAddress);
        autoName = findViewById(R.id.textName);
        autoPhone = findViewById(R.id.textPhone);
        btnSearch = findViewById(R.id.btnSearch);
        autoEmail = findViewById(R.id.textEmail);
        dropDownJobList = findViewById(R.id.dropDownJobList);
        backButton = findViewById(R.id.backButton);

        isDealer = getIntent().getBooleanExtra("isDealer", false);
        autoFind(true);

        initDatePicker();
        btnDatePicker.setText(getTodayDate());
        btnDatePicker.setOnClickListener(view -> datePickerDialog.show());

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.drop_down, getResources().getStringArray(R.array.Records));
        dropDownJobList.setAdapter(arrayAdapter);


        uploadReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent, 1);
            }
        });

        btnAddJob.setOnClickListener(view -> addJob());

        btnAutoAdd.setOnClickListener(view -> autoFind(true));
        btnManualEnter.setOnClickListener(view -> autoFind(false));
        btnSearch.setOnClickListener(view -> search());
        backButton.setOnClickListener(view -> finish());
    }

    // initDatePicker was inspired by code from https://www.youtube.com/watch?v=qCoidM98zNk&ab_channel=CodeWithCal
    // This method initialises the date picker window
    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day, month, year);
                btnDatePicker.setText(date);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    // makeDateString was inspired by code from https://www.youtube.com/watch?v=qCoidM98zNk&ab_channel=CodeWithCal
    // this method converts the date into a string
    private String makeDateString(int day, int month, int year) {
        return day + " " + getMonthFormat(month) + " " + year;
    }

    // getMonthFormat was inspired by code from https://www.youtube.com/watch?=qCoidM98zNk&ab_channel=CodeWithCal
    // this method converts the month from an int to a string in the format of 'JAN'
    private String getMonthFormat(int month) {
        if (month == 1){
            return "JAN";
        }if (month == 2){
            return "FEB";
        }if (month == 3){
            return "MAR";
        }if (month == 4){
            return "APR";
        }if (month == 5){
            return "MAY";
        }if (month == 6){
            return "JUN";
        }if (month == 7){
            return "JUL";
        }if (month == 8){
            return "AUG";
        }if (month == 9){
            return "SEP";
        }if (month == 10){
            return "OCT";
        }if (month == 11){
            return "NOV";
        }if (month == 12){
            return "DEC";
        }
        return "JAN";
    }

    // getTodayDate was inspired by code from https://www.youtube.com/watch?v=qCoidM98zNk&ab_channel=CodeWithCal
    // This method retrieves the current date from the Calendar instance
    private String getTodayDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month = month + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    // this method is called upon clicking the search button
    // it makes a query to retrieve the document stored in the dealer collection via the email id.
    private void search() {
        String email = autoEmail.getText().toString();
        if (email.isEmpty()){
            autoEmail.setError("Please Enter Email");
            return;
        }
        getDealerCollection(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Dealer dealer = documentSnapshot.toObject(Dealer.class);
                if(dealer != null){
                    autoPostcode.setText(dealer.getPostcode());
                    autoAddress.setText(dealer.getAddress());
                    autoName.setText(dealer.getName());
                    autoPhone.setText(dealer.getPhone());
                } else {
                    autoEmail.setError("Email not found");
                }
            }
        });
    }

    // this method is called to change the card from auto find to manual find and vice versa.
    private void autoFind(Boolean showAutoFind) {
        if(showAutoFind){
            isAutoFind = true;
            autoFind.setVisibility(View.VISIBLE);
            manualEnter.setVisibility(View.GONE);
            if(isDealer){
                autoEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                search();
            }
        }else{
            isAutoFind = false;
            autoFind.setVisibility(View.GONE);
            manualEnter.setVisibility(View.VISIBLE);
        }

    }

    // This method is called to create a new document in the 'jobs' collection in Firebase Firestore
    // Before saving the JobDetails to the database, validations are performed to make sure that there is no empty fields where necessary
    private void addJob() {
        numPlate = getIntent().getStringExtra("numPlate");
        String docId = getIntent().getStringExtra("docId");
        String title = dropDownJobList.getText().toString();
        String description = textDescription.getText().toString();
        String millage = textMillage.getText().toString();
        String price = textPrice.getText().toString();
        String date = btnDatePicker.getText().toString();

        if(title.isEmpty()){
            showToast(getApplicationContext(), "Please enter a valid Job Title");
            return;
        }
        if(description.isEmpty()){
            textDescription.setError("Enter a Description");
            return;
        }
        if(millage.isEmpty()){
            textMillage.setError("Enter a Millage");
            return;
        }
        if(textPrice.getText().toString().isEmpty()){
            textPrice.setError("Enter a Price");
            return;
        }

        JobDetails jobDetails = new JobDetails();
        jobDetails.setVehicleID(docId);
        jobDetails.setNumberPlate(numPlate);
        jobDetails.setTitle(title);
        jobDetails.setDescription(description);
        jobDetails.setMillage(millage);
        jobDetails.setPrice(Double.parseDouble(price));
        jobDetails.setDate(date);
        if(isAutoFind){
            jobDetails.setName(autoName.getText().toString());
            jobDetails.setPostcode(autoPostcode.getText().toString());
            jobDetails.setContact(autoEmail.getText().toString());
            jobDetails.setAddress(autoAddress.getText().toString());
            jobDetails.setPhone(autoPhone.getText().toString());
        } else {
            jobDetails.setName(textName.getText().toString());
            jobDetails.setPostcode(textPostcode.getText().toString());
            jobDetails.setContact(textEmail.getText().toString());
            jobDetails.setPhone(textPhone.getText().toString());
            jobDetails.setAddress(textAddress.getText().toString());
        }
        jobDetails.setVerified(false);
        jobDetails.setRequested(false);
        jobDetails.setPending(false);
        jobDetails.setOwner(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        if(imageUploaded){
            uploadImage(jobDetails);
        } else {
            saveJobToFirebase(jobDetails);
        }

        Intent intent = new Intent(getApplicationContext(), MaintenanceActivity.class);
        intent.putExtra("numPlate", numPlate);
        intent.putExtra("isDealer", isDealer);
        intent.putExtra("docId", docId);
        startActivity(intent);
        finish();
    }

    // This method is the second part of adding a job
    // It creates a query to the respective firebase database to save the jobDetails object (which stores the details of the job.
    private void saveJobToFirebase(JobDetails jobDetails) {

        DocumentReference documentReference;

        documentReference = getRecordCollectionReference().document();

        documentReference.set(jobDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    showToast(getApplicationContext(), "Job Added Successfully");
                    finish();
                }else{
                    showToast(getApplicationContext(), "Failed while adding Job");
                }
            }
        });
    }

    // this method bring up the camera roll /gallery to allow the user to select an image ( receipt ) to upload.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 & resultCode == RESULT_OK && data!=null){
            imagePath = data.getData();
            getImageInImageView();
        }
    }

    // This method loads the selected image as a bitmap allowing for display within the app
    private void getImageInImageView() {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
            imageUploaded = true;
        } catch (IOException e){
            e.printStackTrace();
        }
        imgReceipt.setImageBitmap(bitmap);
    }

    // This method allows for the image to be uploaded to the firebase cloud storage where the link is retrieved to be saved into the jobDetails instance
    private void uploadImage(JobDetails jobDetails){
        String uuid = "receipts/"+ UUID.randomUUID().toString();
        FirebaseStorage.getInstance().getReference(uuid)
                .putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()){
                                        url = task.getResult().toString();
                                        jobDetails.setImgUrl(url);
                                        showToast(getApplicationContext(), "image uploaded");
                                        saveJobToFirebase(jobDetails); // save job details after URL is retrieved
                                    } else {
                                        showToast(getApplicationContext(), task.getException().getLocalizedMessage());
                                    }
                                }
                            });
                        } else {
                            showToast(getApplicationContext(), task.getException().getLocalizedMessage());
                        }
                    }
                });
    }
}