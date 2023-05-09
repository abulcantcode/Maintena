package com.example.maintena;

import static com.example.maintena.Utility.getTimestamp;
import static com.example.maintena.Utility.getVehicleCollectionReference;
import static com.example.maintena.Utility.showToast;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.maintena.Api.DVLAService;
import com.example.maintena.Model.Details;
import com.example.maintena.Model.Vehicle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// This class is responsible for allowing users to save vehicles
public class VehicleDetailsActivity extends AppCompatActivity {

    private static final String TAG = "VehicleDetailsActivity";
    EditText textNumPlate, textName, textMake, textColour, textFuel;
    ImageView imgCar;
    Button btnFindCar, btnAddCar, uploadImg;
    ImageButton backButton;
    Vehicle vehicle = new Vehicle();
    private Uri imagePath;
    Boolean imageUploaded = false;
    String docId = null;
    Boolean isEditMode = false;
    Boolean isDealer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);

        // Here, the xml page is being connected to the defined objects
        textNumPlate = findViewById(R.id.textNumPlate);
        textName = findViewById(R.id.textCarName);
        textMake = findViewById(R.id.textMake);
        textColour = findViewById(R.id.textColour);
        textFuel = findViewById(R.id.textFuel);

        imgCar = findViewById(R.id.imgCar);

        btnAddCar = findViewById(R.id.btnAddCar);
        btnFindCar = findViewById(R.id.btnFindCar);
        backButton = findViewById(R.id.backButton);
        uploadImg = findViewById(R.id.uploadImg);

        // Gathering information from the previous page
        docId = getIntent().getStringExtra("docId");
        isDealer = getIntent().getBooleanExtra("isDealer", false);

        // If the docID which was passed from the previous page is not empty, changes to the page will be made
        if(docId!=null && !docId.isEmpty()){
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

                    if (vehicle.getImgUrl() != null && !vehicle.getImgUrl().isEmpty()) {
                        Glide.with(getApplicationContext())
                                .load(vehicle.getImgUrl())
                                .into(imgCar);
                    }
                }
            });

            isEditMode = true;
            btnAddCar.setText("Save Car");
            textNumPlate.setKeyListener(null);
            textNumPlate.setEnabled(false);
        }

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent, 1);
            }
        });

        btnFindCar.setOnClickListener(view -> findCar());
        btnAddCar.setOnClickListener(view -> addCar());
        backButton.setOnClickListener(view -> {
            if(isEditMode){
                Intent intent = new Intent(getApplicationContext(), MaintenanceActivity.class);
                intent.putExtra("docId", docId);
                intent.putExtra("isDealer", isDealer);
                startActivity(intent);
            }
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 & resultCode == RESULT_OK && data!=null){
            imagePath = data.getData();
            getImageInImageView();
        }
    }

    private void getImageInImageView() {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagePath);
            imageUploaded = true;
        } catch (IOException e){
            e.printStackTrace();
        }
        imgCar.setImageBitmap(bitmap);
    }

    private void addCar() {
        String numPlate = textNumPlate.getText().toString().trim().toUpperCase();
        String carName = textName.getText().toString();
        String carMake = textMake.getText().toString();
        String carColour = textColour.getText().toString();
        String carFuel = textFuel.getText().toString();

        // Validating text boxes
        if(numPlate.isEmpty()){
            textNumPlate.setError("Enter a valid Number Plate");
            return;
        }
        if(carName.isEmpty()){
            textName.setError("Enter a Name");
            return;
        }
        if(carMake.isEmpty()){
            textMake.setError("Enter a Make");
            return;
        }
        if(carColour.isEmpty()){
            textColour.setError("Enter a Colour");
            return;
        }
        if(carFuel.isEmpty()){
            textFuel.setError("Enter a Fuel Type");
            return;
        }

        vehicle.setNumberPlate(numPlate);
        vehicle.setName(carName);
        vehicle.setMake(carMake);
        vehicle.setColour(carColour);
        vehicle.setFuelType(carFuel);
        vehicle.setTimestamp(getTimestamp());
        vehicle.setOwner(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        vehicle.setPending(false);

        // checking if image has been uploaded to decide which method to use
        if (imageUploaded) {
            uploadImage(vehicle);
        } else {
            saveVehicleToFirebase(vehicle);
        }

        if(isEditMode){
            Intent intent = new Intent(getApplicationContext(), MaintenanceActivity.class);
            intent.putExtra("numPlate", vehicle.getNumberPlate());
            intent.putExtra("isDealer", isDealer);
            intent.putExtra("docId", docId);
            startActivity(intent);
            finish();
        } else {
            finish();
        }

    }
    // https://firebase.google.com/docs/storage/android/start#java (firebase cloud storage docs)
    // This method allows for the image to be uploaded to the firebase cloud storage where the link is retrieved to be saved into the vehicle instance
    private void uploadImage(Vehicle vehicle){
        // This uuid stores the location and filename of the image which will be store in the firebase cloud storage
        String uuid = "cars/"+ UUID.randomUUID().toString();
        FirebaseStorage.getInstance().getReference(uuid)
                .putFile(imagePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if(task.isSuccessful()){
                                        // Once successfully uploaded, the url is taken from the task object and saved to firestore db
                                        String url = task.getResult().toString();
                                        vehicle.setImgUrl(url);
                                        showToast(getApplicationContext(), "image uploaded");
                                        saveVehicleToFirebase(vehicle); // save job details after URL is retrieved
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
    // This method saves the vehicle object to the firestore database
    private void saveVehicleToFirebase(Vehicle vehicle) {

        DocumentReference documentReference;

        if ( isEditMode ){
            // if the isEditMode variable is true, the firebase connection will reference the existing document using the docID
            documentReference = getVehicleCollectionReference().document(docId);
        }else{
            documentReference = getVehicleCollectionReference().document();
        }

        documentReference.set(vehicle).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    showToast(VehicleDetailsActivity.this, "Vehicle Added Successfully");
                    finish();
                }else{
                    showToast(VehicleDetailsActivity.this, "Failed while adding vehicle");
                }
            }
        });
    }

    // https://www.digitalocean.com/community/tutorials/retrofit-android-example-tutorial (RetroFit Examples)
    // This method is used to communicate with the api endpoint.
    // By using the DVLAService interface, this method is able to retrieve and populate the corresponding fields.
    // By using retroFit, the method is able to build a query, make a post call with the body requestBody
    // convert the response into the class type 'Details' to then populate the fields necessary.
    // Below is a link to the VES API details:
    // https://developer-portal.driver-vehicle-licensing.api.gov.uk/apis/vehicle-enquiry-service/vehicle-enquiry-service-description.html#test-environment-request
    private void findCar() {
        String numPlate = textNumPlate.getText().toString().trim().toUpperCase();

        // The httpClient and retrofit objects are used to build the request
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://driver-vehicle-licensing.api.gov.uk")
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();


        DVLAService service = retrofit.create(DVLAService.class);

        // This is the request body
        String requestBody = "{ \"registrationNumber\":\"" + numPlate + "\" }";
        RequestBody body = RequestBody.create(requestBody, MediaType.parse("application/json"));

        // getVehicleDetails is part of the DVLAService class, which has the correct API Key for the real environment
        Call<Details> call = service.getVehicleDetails(body);
        call.enqueue(new Callback<Details>() {
            @Override
            public void onResponse(Call<Details> call, Response<Details> response) {
                if (response.isSuccessful()) {
                    // handle successful response
                    Details details = response.body();

                    textMake.setText(details.getMake());
                    textColour.setText(details.getColour());
                    textFuel.setText(details.getFuelType());

                } else {
                    // handle other response
                    showToast(getApplicationContext(), "Can not find vehicle");

                }
            }
            @Override
            public void onFailure(Call<Details> call, Throwable t) {
                // handle failed response
                Log.e(TAG, "onFailure: " + t.getMessage());
                showToast(getApplicationContext(), t.getMessage());
            }
        });
    }
}