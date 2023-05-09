package com.example.maintena;

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

public class CreatePrivateActivity extends AppCompatActivity {

    EditText editEmail, editName, editUsername, editPassword, editConfirmPassword;
    Button buttingSignUp;
    TextView txtSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_private);

        editName = findViewById(R.id.editName);
        editUsername = findViewById(R.id.editUsername);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
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

    // this is responsible for setting up the objects for private user ( in hashmap format)
    void createAccount(){
        createInProgress(true);
        String getName = editName.getText().toString();
        String getUsername = editUsername.getText().toString();
        String getEmail = editEmail.getText().toString().toLowerCase();
        String getPsw = editPassword.getText().toString();
        String getConfirmPsw = editConfirmPassword.getText().toString();

        boolean isValidated = validateData(getName, getUsername, getEmail, getPsw, getConfirmPsw);

        if(!isValidated){
            createInProgress(false);
            return;
        }

        // Building hashmap to store user in firestore database
        Map<String, Object> user = new HashMap<>();
        user.put("name", getName);
        user.put("username", getUsername);
        user.put("email", getEmail);
        user.put("dealer", false);

        createAccountInFirebase(getEmail, getPsw, user);
    }


    // This method is responsible for saving the dealer details to firebase authentication and to firebase firestore
    private void createAccountInFirebase(String email, String password, Map<String, Object> user) {
        createInProgress(true);

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
    boolean validateData(String name, String username, String email, String password, String confirmPassword){
        if(name.isEmpty()){
            editName.setError("Name cannot be empty");
            return false;
        }
        if(username.isEmpty()){
            editUsername.setError("Username cannot be empty");
            return false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Email is invalid");
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