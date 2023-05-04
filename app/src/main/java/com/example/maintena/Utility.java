package com.example.maintena;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;

// https://firebase.google.com/docs/firestore/quickstart#java
// I used the firebase firestore docs to create methods: getDealers, getUsers, getJobCollectionReference, getVehicleCollectionReference
// This class store frequently used methods within this project
public class Utility {
    // getTimestamp is used to get a timestamp of the current time
    static String getTimestamp(){
        Timestamp timestamp = Timestamp.now();
        Date date = timestamp.toDate();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }

    // showToast is used to display a popup message
    static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    // getVehicleCollectionReference is used to get a reference of the vehicles collection within the firestore db
    static CollectionReference getVehicleCollectionReference(){
        return FirebaseFirestore.getInstance().collection("vehicles");
    }
    // getJobCollectionReference is used to get a reference of the jobs collection within the firestore db
    static CollectionReference getJobCollectionReference(){
        return FirebaseFirestore.getInstance().collection("jobs");
    }
    // getUsers is used to get a reference of the users collection within the firestore db
    static DocumentReference getUserCollection(String email){
        return FirebaseFirestore.getInstance().collection("users").document(email);
    }
    // getDealers is used to get a reference of the dealers collection within the firestore db
    static DocumentReference getDealerCollection(String email){
        return FirebaseFirestore.getInstance().collection("dealers").document(email);
    }
}
