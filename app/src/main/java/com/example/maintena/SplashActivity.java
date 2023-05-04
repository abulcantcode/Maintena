package com.example.maintena;

import static com.example.maintena.Utility.getUserCollection;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

// This class is responsible for handling the UI and user interaction for the splash screen that is displayed when the app is launched.
// This code was inspired by: https://codereview.stackexchange.com/questions/128420/android-splash-screen
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                // This decided where the current user will be redirected to depending on wheather they are logged in as a private user, dealer or guest
                if(currentUser == null){
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                } else {
                    getUserCollection(currentUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//                            // Convert document response to an Object of type User
//                            User user = documentSnapshot.toObject(User.class);
//                            assert user != null;
//                            if (user.getDealer()){
//                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//                                finish();
//                            } else {
//                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                                finish();
//                            }
                        }
                    });
                }
            }
        }, 2000);

    }
}