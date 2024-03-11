package com.example.elderjobapp.utils;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseUtil {
    private static String currentUserID;
    public static void setCurrentUserID(String phoneNumber){
        currentUserID = phoneNumber;
    }
    public static String getCurrentUserID(){
        return currentUserID;
    }
    public static boolean isLoggedIn(){
        if(currentUserID!=null){
            return true;
        }
        return false;
    }

    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserID);
    }

    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static CollectionReference allJobCollectionReference(){
        return FirebaseFirestore.getInstance().collection("jobs");
    }

    public static void logout(){
        currentUserID = null;
    }

    public static StorageReference getCurrentProfileImageStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_images").child(currentUserID);
    }
    public static StorageReference getDefaultProfileImageStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("default_profile_image.png");
    }

    public static StorageReference  getOtherProfileImageStorageRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_images").child(otherUserId);
    }
}
