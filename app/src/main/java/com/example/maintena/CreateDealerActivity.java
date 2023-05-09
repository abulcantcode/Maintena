package com.example.maintena;

import static com.example.maintena.Utility.getDealerCollection;
import static com.example.maintena.Utility.getUserCollection;
import static com.example.maintena.Utility.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class CreateDealerActivity extends AppCompatActivity {

    EditText editEmail, editName, editPhone, editAddress, editPostcode, editPassword, editConfirmPassword;
    Button buttingSignUp;
    TextView txtSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_dealer);

        editName = findViewById(R.id.editCompanyNameD);
        editEmail = findViewById(R.id.editEmailD);
        editPhone = findViewById(R.id.editPhoneD);
        editAddress = findViewById(R.id.editAddressD);
        editPostcode = findViewById(R.id.editPostcodeD);
        editPassword = findViewById(R.id.editPasswordD);
        editConfirmPassword = findViewById(R.id.editConfirmPasswordD);
        buttingSignUp = findViewById(R.id.btnCreateAccount);
        txtSignIn = findViewById(R.id.txtSignIn);

        buttingSignUp.setOnClickListener(view -> createAccount());
        txtSignIn.setOnClickListener(view -> goToSignIn());
    }

    // this method allows the user to navigate to the sign in page
    private void goToSignIn() {
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        finish();
    }

    // this is responsible for setting up the objects for user and dealer ( in hashmap format)
    void createAccount(){
        createInProgress(true);

        String getName = editName.getText().toString();
        String getEmail = editEmail.getText().toString().toLowerCase();
        String getPhone = editPhone.getText().toString();
        String getAddress = editAddress.getText().toString();
        String getPostcode = editPostcode.getText().toString();
        String getPsw = editPassword.getText().toString();
        String getConfirmPsw = editConfirmPassword.getText().toString();

        // Validate the data
        boolean isValidated = validateData(getName, getEmail, getPhone, getAddress, getPostcode, getPsw, getConfirmPsw);

        if(!isValidated){
            createInProgress(false);
            return;
        }

        // build user object
        Map<String, Object> user = new HashMap<>();
        user.put("name", getName);
        user.put("username", getName);
        user.put("email", getEmail);
        user.put("dealer", true);

        // build dealer object
        Map<String, Object> dealer = new HashMap<>();
        dealer.put("name", getName);
        dealer.put("email", getEmail);
        dealer.put("phone", getPhone);
        dealer.put("address", getAddress);
        dealer.put("postcode", getPostcode);

        createAccountInFirebase(getEmail, getPsw, user, dealer);
    }

    // This method is responsible for saving the dealer details to firebase authentication and to firebase firestore
    private void createAccountInFirebase(String email, String password, Map<String, Object> user, Map<String, Object> dealer) {
        createInProgress(true);

        // attempting to save to the firestore database
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        // https://firebase.google.com/docs/auth/android/start#java_1 (firebase Auth docs)
        // Using the firebase Auth documentation, I was able to use the method createUserWithEmailAndPassword to create an account of FirebaseAuth which takes username and password
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    getUserCollection(email).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            getDealerCollection(email).set(dealer);
                            showToast(getApplicationContext(), "Successfully created account");
                            // This is a built in feature of firebase where it send an email verification link to the email used to sign up
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }
                    });
                } else {
                    createInProgress(false);
                    showToast(getApplicationContext(), task.getException().getLocalizedMessage());
                }
            }
        });
    }

    // This is responsible for making the login button disabled once clicked to ensure the login query is not executed more than once at a time.
    void createInProgress(boolean inProgress){
        buttingSignUp.setEnabled(!inProgress);
    }

    // This method is responsible for making sure all fields have been added
    // Handles validation
    boolean validateData(String name, String email, String phone, String address, String postcode, String password, String confirmPassword){
        if(name.isEmpty()){
            editName.setError("Name cannot be empty");
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Email is invalid");
            return false;
        }
        if(phone.isEmpty()){
            editPhone.setError("Phone cannot be empty");
            return false;
        }
        if(address.isEmpty()){
            editAddress.setError("Address cannot be empty");
            return false;
        }
        if(postcode.isEmpty()){
            editPostcode.setError("Postcode cannot be empty");
            return false;
        }
        if(password.length()<6){
            editPassword.setError("Password must be longer than 6 characters");
            return false;
        }
        if(!password.matches(confirmPassword)){
            editConfirmPassword.setError("Password does not match");
            return false;
        }
        return true;
    }
}