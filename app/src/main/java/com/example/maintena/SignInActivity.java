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

import com.example.maintena.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class SignInActivity extends AppCompatActivity {

    EditText editEmail, editPassword;
    Button buttingLogin;
    TextView txtSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Here, the xml page is being connected to the defined objects
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        buttingLogin = findViewById(R.id.btnLogin);
        txtSignUp = findViewById(R.id.txtSignUp);

        // Setting up navigation
        buttingLogin.setOnClickListener(view -> loginUser());

        txtSignUp.setOnClickListener(view -> goToSignUp());
    }

    private void goToSignUp() {
        startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
    }

    private void loginUser() {
        String getEmail = editEmail.getText().toString();
        String getPsw = editPassword.getText().toString();

        boolean isValidated = validateData(getEmail, getPsw);

        if(!isValidated){
            return;
        }

        loginInFirebase(getEmail, getPsw);
    }

    // This method is called when the Sign In button is clicked.
    // It retrieves the email and password entered by the user and calls the signInWithEmailAndPassword() method of the FirebaseAuth object to authenticate the user.
    // If authentication is successful, it starts the HomeActivity.
    // If authentication fails, it displays an error message to the user.
    private void loginInFirebase(String email, String password) {
        signingInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                signingInProgress(true);
                if(task.isSuccessful()){
                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                        getUserCollection(email).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                User user = documentSnapshot.toObject(User.class);
                                assert user != null;
                                Intent intent;
                                if (user.getDealer()){
                                    intent = new Intent(getApplicationContext(), DealerHomeActivity.class);
                                    intent.putExtra("isDealer", true);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    intent = new Intent(getApplicationContext(), GarageActivity.class);
                                    intent.putExtra("isDealer", false);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    } else {
                        showToast(getApplicationContext(), "Email is not verified");
                    }
                } else {
                    signingInProgress(false);
                    showToast(getApplicationContext(), task.getException().getLocalizedMessage());
                }
            }
        });

    }

    // this disables the sign in button once pressed
    void signingInProgress(boolean inProgress){
        buttingLogin.setEnabled(!inProgress);
    }

    // this is the method which checks the validation
    boolean validateData(String email, String password){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editEmail.setError("Email is invalid");
            return false;
        }
        if(password.length()<6){
            editPassword.setError("Password must be longer than 6 characters");
            return false;
        }
        return true;
    }
}